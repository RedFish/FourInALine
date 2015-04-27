package com.main.fourinaline;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.engine.BadColumnException;
import com.engine.Game;
import com.online.Online;

/**
 * The class which manage all the game graphics elements 
 * 
 * @author Richard GUERCI & Julien SIERRA
 *
 */

@SuppressLint("ViewConstructor")
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

	private Game game = new Game();
	private MainThread thread;
	private ArrayList<Pion> pion = new ArrayList<Pion>();
	private int last_move_id = -1; // id of the last move (online)

	// image of the grid and background
	private Bitmap grid;
	private Bitmap background;

	// coordinates of the grid
	int xGrid;
	int yGrid;

	// coordinates of the first case of the grid
	int line;
	int column;

	// place between two cases
	int btwLine;
	int btwColumn;

	// to check the move down
	int downX;
	int downY;
	boolean moveDown;

	// to scale
	int gridWidth;
	int gridHeight;

	// background
	int imageWidth;
	int xBackground;

	// screen size
	int xFrame;
	int yFrame;

	// one player game
	private boolean onePlayer = false;

	private boolean playerTwoTurn = false;
	private boolean blockScreen = false;

	private boolean online = false;
	private int level = 0;

	/**
	 * This constructor when 1 player
	 * 
	 * @param context
	 * @param level
	 */
	public GamePanel(Context context, String level) {
		super(context);
		getHolder().addCallback(this);

		this.onePlayer = true;
		if (level.trim().equals("1")) {
			this.level = 1;
		} else if (level.trim().equals("2")) {
			this.level = 2;
		} else
			this.level = 3;
		System.err.println("level: " + this.level);
		init(context);
	}

	/**
	 * This constructor when online or 2 players (online == false if 2 players)
	 * 
	 * @param context
	 * @param online
	 */
	public GamePanel(Context context, boolean online) {
		super(context);
		getHolder().addCallback(this);
		this.online = online;
		init(context);
	}

	public void init(Context context) {
		// size of the screen
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point Size = new Point();
		display.getSize(Size);
		xFrame = Size.x;
		yFrame = Size.y;

		// background
		background = BitmapFactory.decodeResource(getResources(),
				R.drawable.sky);

		imageWidth = (int) (yFrame * 3.671);
		xBackground = -imageWidth;
		background = Bitmap.createScaledBitmap(background, imageWidth, yFrame,
				true);

		// create grid and load bitmap
		grid = BitmapFactory.decodeResource(getResources(), R.drawable.grid);

		gridWidth = (int) (90 * xFrame / 100);
		gridHeight = (int) (gridWidth * 0.86);

		xGrid = (int) (5 * xFrame / 100);
		yGrid = (int) (23 * yFrame / 100);

		grid = Bitmap.createScaledBitmap(grid, gridWidth, gridHeight, true);

		column = (int) (0.0770 * gridWidth);
		line = (int) (0.0865 * gridHeight);
		btwLine = (int) (0.1630 * gridHeight);
		btwColumn = (int) (0.1412 * gridWidth);

		if (online) {
			System.err.println("online");
			if (!Online.session.isHost()) {
				System.err.println("no hosts");
				setPlayerTwoTurn(true);
			}
		}

		// create the first pion
		newPion();

		// create the game loop thread
		thread = new MainThread(getHolder(), this);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		onPause();
	}

	public void onPause() {
		thread.setRunning(false);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// nothing
	}

	@Override
	/**
	 * Manage the user behavior when he touch the screen
	 */
	public boolean onTouchEvent(MotionEvent event) {

		if (!playerTwoTurn && !blockScreen) {
			// finger on the screen
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				getCurrentPion().handleAction((int) event.getX(),
						(int) event.getY());
				downX = (int) event.getX();
				downY = (int) event.getY();

			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {

				// if the current pion is touched, he can move left and right
				if (getCurrentPion().isTouched()) {
					// check the limit of his movements
					if ((int) event.getX() > (xGrid + column / 2)
							&& (int) event.getX() < xGrid + gridWidth - column
									/ 2)
						getCurrentPion().setX((int) event.getX());
				} else {
					// if the movement is down, the current pion is moving down
					// in the grid
					int currentX = (int) event.getX();
					int currentY = (int) event.getY();
					if (Math.abs(downX - currentX) < Math.abs(downY - currentY)) {
						if (downY < currentY) {
							moveDown = true;
						} else
							moveDown = false;
					}

				}
			}
			// if the finger is released
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (getCurrentPion().isTouched()) {
					getCurrentPion().setTouched(false);
				} else {
					if (moveDown) {
						getCurrentPion().setMove(true, this.getContext());
						setPlayerTwoTurn(false);
					}
				}
			}
		}
		return true;
	}

	/**
	 * Create a new pion on the screen
	 */
	public void newPion() {
		Bitmap bitmap;
		// load bitmap
		if (game.getTurn() == 1) {
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.yellow);
			// player1 = false;
		} else {
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.red);
			// player1 = true;
		}
		// create new pion
		pion.add(new Pion(this, Bitmap.createScaledBitmap(bitmap,
				(int) (gridWidth * 0.13), (int) (gridWidth * 0.13), true), 160,
				150));

		if (onePlayer && game.getTurn() != 1) {
			setPlayerTwoTurn(true);
			Point pnt = new Point();
			switch (level) {
			case 1:
				pnt = game.computerLevel1();
				break;
			case 2:
				pnt = game.computerLevel2();
				break;
			case 3:
				pnt = game.computerLevel3();
				break;
			}
			getCurrentPion().autoMove(pnt.x, pnt.y);
		}

		if (online && game.getTurn() == 2 && Online.session.isHost()) {
			setPlayerTwoTurn(true);
			GetMoveTask task = new GetMoveTask(this);
			task.execute(new String[] { Online.lastMove(Online.session.getId()) });
		} else if (online && game.getTurn() == 1 && !Online.session.isHost()) {

			setPlayerTwoTurn(true);
			GetMoveTask task = new GetMoveTask(this);
			task.execute(new String[] { Online.lastMove(Online.session.getId()) });

		}
		setBlockScreen(false);
	}

	/**
	 * 
	 * To manage next turn when online game
	 * 
	 * @param column
	 *            The column chosen by the last player
	 * 
	 **/
	public void onlinePlay(int column) {
		int line;
		if (playerTwoTurn) {
			try {
				line = getGame().play(column);
				getCurrentPion().autoMove(column, line);
				Thread.sleep(1000);
				// newPion();
				setPlayerTwoTurn(false);
			} catch (BadColumnException e) {
				CharSequence text = "Bad column!";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(this.getContext(), text, duration);
				toast.show();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			PlayTask pt = new PlayTask();
			pt.execute(new String[] { Online.play(Online.session.getId(),
					Online.session.getPlayer1(), "" + column) });
			setPlayerTwoTurn(true);
		}
	}

	/**
	 * Update the graphics of pion
	 */
	public void update() {
		for (int i = 0; i < pion.size(); i++) {
			pion.get(i).update();
		}
	}

	/**
	 * Manage the end of a game
	 * 
	 */
	public void gameOver() {
		Intent intent = new Intent("kill");
		String text = "";
		if (online) {
			if (game.whoWin() == 1) {
				text += "Congratulation " + Online.session.getPlayer1()
						+ ", you win!";
			} else
				text += "Sorry, " + Online.session.getPlayer2() + " won ...";
		} else if (onePlayer) {
			if (game.whoWin() == 1) {
				text += "Congratulation, you win!";
			} else
				text += "Sorry, you lose ...";
		} else {
			text += "Congratulation player" + game.getTurn() + ", you win!";
		}
		intent.putExtra("text", text);
		LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
	}

	/**
	 * DRAWING FUNCTIONS
	 */

	/**
	 * Drawing all the elements of the scene
	 * 
	 * @param canvas
	 */
	public void render(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		// background
		drawBackground(canvas);
		// pions
		for (int i = 0; i < pion.size(); i++) {
			pion.get(i).draw(canvas);
		}
		// grid
		drawGrid(canvas);

		// the text
		Paint pnt = new Paint();
		pnt.setStyle(Style.FILL);
		pnt.setTextSize(40);
		int a = 8;
		int b = 9;
		if (game.getTurn() == 2) {
			pnt.setColor(Color.rgb(97, 0, 16));
			if (online) {
				if (Online.session.getPlayer2().equals("")) {
					canvas.drawText("Waiting for the other player to play.",
							xGrid, (a * yFrame) / b, pnt);
				} else
					canvas.drawText(
							"Waiting for " + Online.session.getPlayer2()
									+ " to play.", xGrid, (a * yFrame) / b, pnt);
			} else if (onePlayer) {
				canvas.drawText("Waiting for player 2 to play.", xGrid,
						(a * yFrame) / b, pnt);
			} else
				canvas.drawText("Your turn player 2.", xGrid, (a * yFrame) / b,
						pnt);
		} else {
			pnt.setColor(Color.rgb(254, 210, 64));
			if (online) {
				canvas.drawText("Your turn " + Online.session.getPlayer1()
						+ ".", xGrid, (a * yFrame) / b, pnt);
			} else
				canvas.drawText("Your turn player 1.", xGrid, (a * yFrame) / b,
						pnt);
		}
	}

	/**
	 * Draw the grid on screen
	 * 
	 * @param canvas
	 */
	public void drawGrid(Canvas canvas) {
		canvas.drawBitmap(grid, xGrid, yGrid, null);
	}

	/**
	 * Draw the background on screen
	 * 
	 * @param canvas
	 */
	private void drawBackground(Canvas canvas) {
		canvas.drawBitmap(background, xBackground + imageWidth, 0, null);
		canvas.drawBitmap(background, xBackground, 0, null);
		xBackground += 2;
		if (xBackground >= -2) {
			xBackground = -imageWidth;
		}

	}

	/**
	 * GETTERS AND SETTERS
	 * 
	 */

	public Pion getCurrentPion() {
		return pion.get(pion.size() - 1);
	}

	public int getxGrid() {
		return xGrid;
	}

	public void setxGrid(int xGrid) {
		this.xGrid = xGrid;
	}

	public int getyGrid() {
		return yGrid;
	}

	public void setyGrid(int yGrid) {
		this.yGrid = yGrid;
	}

	public int getBtwLine() {
		return btwLine;
	}

	public void setBtwLine(int btwLine) {
		this.btwLine = btwLine;
	}

	public int getBtwColumn() {
		return btwColumn;
	}

	public void setBtwColumn(int btwColumn) {
		this.btwColumn = btwColumn;
	}

	public int getGridHeight() {
		return gridHeight;
	}

	public int getGridWidth() {
		return gridWidth;
	}

	public int getColumn() {
		return column;
	}

	public int getLine() {
		return line;
	}

	public Game getGame() {
		return game;
	}

	public boolean isWin() {
		return game.isWin();
	}

	public boolean isPlayerTwoTurn() {
		return playerTwoTurn;
	}

	public void setPlayerTwoTurn(boolean playerTwoTurn) {
		this.playerTwoTurn = playerTwoTurn;
	}

	public boolean isOnline() {
		return online;
	}

	public void setBlockScreen(boolean blockScreen) {
		this.blockScreen = blockScreen;
	}

	public boolean isOnePlayer() {
		return onePlayer;
	}

	/**
	 * This class allow the player to play (HTTP request)
	 */
	private class PlayTask extends AsyncTask<String, Void, String> {
		@Override
		// Request
		protected String doInBackground(String... urls) {
			String result = "";

			try {
				System.err.println("PlayTask : " + urls[0]);
				InputStream content = null;
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response = httpclient
						.execute(new HttpGet(urls[0]));
				content = response.getEntity().getContent();

				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(content));
				String s = "";
				while ((s = buffer.readLine()) != null) {
					result += s + "\n";
				}
			} catch (Exception e) {
				System.err.println("PlayTask : ERROR " + e);
			}

			return result;
		}

		// When the request is over
		@Override
		protected void onPostExecute(String result) {
			System.err.println("PlayTask result : " + result);
		}
	}

	/**
	 * This class allow the player to get the move of the other player (HTTP
	 * request)
	 */
	private class GetMoveTask extends AsyncTask<String, Void, String> {

		GamePanel gp;

		public GetMoveTask(GamePanel gp) {
			this.gp = gp;
		}

		@Override
		// Request
		protected String doInBackground(String... urls) {
			String result = "";

			do {
				try {
					result = "";
					InputStream content = null;
					HttpClient httpclient = new DefaultHttpClient();
					HttpResponse response = httpclient.execute(new HttpGet(
							urls[0]));
					content = response.getEntity().getContent();

					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						result += s + "\n";
					}
				} catch (Exception e) {
					System.err.println(e);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					System.err.println("GetMoveTask res = " + result);
					if (result.split(";").length == 3) { // valid result

						if (Integer.parseInt(result.split(";")[0].trim()) > last_move_id)
							last_move_id = Integer
									.parseInt(result.split(";")[0].trim()); // Updating
																			// last
																			// move
																			// id
						System.err.println("GetMoveTask : last_move_id = "
								+ last_move_id + " \nnew_id = "
								+ result.split(";")[0] + " \nplayer = "
								+ result.split(";")[1]);
					}
				}
			} while (result.equals("")
					|| (Integer.parseInt(result.split(";")[0].trim()) <= last_move_id && result
							.split(";")[1].equals(Online.session.getPlayer1())));

			return result;
		}

		// When the request is over
		@Override
		protected void onPostExecute(String result) {
			Online.session.setPlayer2(result.split(";")[1].trim());
			int column = Integer.parseInt(result.split(";")[2].trim());
			gp.onlinePlay(column);

			System.err.println("result : " + result);
		}
	}

}
