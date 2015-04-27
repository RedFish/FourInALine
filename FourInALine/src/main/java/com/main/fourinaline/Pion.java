package com.main.fourinaline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.widget.Toast;

import com.engine.BadColumnException;

/**
 * 
 * Graphic instance of a disc
 * 
 * @author Richard GUERCI & Julien SIERRA
 */

public class Pion {

	GamePanel panel;
	private Bitmap bitmap;

	// coordinate of the pion
	private int x;
	private int y;

	// if the pion is touched, can go left and right
	private boolean touched;

	// if the pion is falling down
	private boolean move = false;

	// auto move to a column
	private boolean moveColumn = false;

	// when the pion is falling
	private int acceleration = 0;

	// the coordinate's target
	private int yTarget;
	private int xTarget;

	public Pion(GamePanel panel, Bitmap bitmap, int x, int y) {
		this.panel = panel;
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
	}

	/**
	 * When the user asks the pion to fall, change the variable move to true,
	 * the target coordinates, and update the game engine
	 * 
	 * @param move
	 * @param line
	 */
	public void setMove(boolean move, Context context) {
		int i;
		try {
			i = 5 - panel.getGame().play(getColumn(x));
			panel.setBlockScreen(true);
			x = panel.getxGrid() + panel.getColumn() + panel.getBtwColumn()
					* getColumn(x);
			yTarget = panel.getyGrid() + panel.getLine() + panel.getBtwLine()
					* i;
			this.move = move;
			if (panel.isOnline()) {
				panel.onlinePlay(getColumn(x));
			}
		} catch (BadColumnException e) {
			CharSequence text = "Bad column!";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	}

	/**
	 * To make the pion move automatically to a special case in the grid (for
	 * 'one player' game and online)
	 * 
	 * @param column
	 * @param line
	 * 
	 */
	public void autoMove(int column, int line) {
		xTarget = panel.getxGrid() + panel.getColumn() + panel.getBtwColumn()
				* column;
		yTarget = panel.getyGrid() + panel.getLine() + panel.getBtwLine()
				* (5 - line);
		this.moveColumn = true;
		if (x < xTarget) {
			acceleration = 1;
		} else
			acceleration = -1;

	}

	/**
	 * Get the column in which the pion have to fall according to the position x
	 * of the screen
	 * 
	 * @param x
	 * @return
	 */
	private int getColumn(int x) {
		if ((x * 7) % panel.getGridWidth() > 0.5) {
			System.err.println(((x * 7) / panel.getGridWidth()));
			if (((x * 7) / panel.getGridWidth()) == 7) {
				return 6;
			} else
				return ((x * 7) / panel.getGridWidth());
		} else {
			System.err.println(((x * 7) / panel.getGridWidth()));
			return (x * 7) / panel.getGridWidth() - 1;
		}
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2),
				y - (bitmap.getHeight() / 2), null);
	}

	/**
	 * Updates the pion's state on the screen if auto moving
	 */
	public void update() {
		if (move) {
			if (y + acceleration < yTarget) {
				y += acceleration;
				acceleration += 2;
			} else {
				y = yTarget;
				move = false;
				if (!panel.isOnline()) {
					panel.setPlayerTwoTurn(false);
				}
				if (!panel.isWin()) {
					panel.setBlockScreen(true);
					panel.newPion();
				} else
					panel.gameOver();
			}
		} else if (moveColumn) {
			if (acceleration > 0) {
				if (x + acceleration < xTarget) {
					x += acceleration;
					acceleration += 2;
				} else {
					x = xTarget;
					moveColumn = false;
					move = true;
				}
			} else if (acceleration < 0) {
				if (x + acceleration > xTarget) {
					x += acceleration;
					acceleration -= 2;
				} else {
					x = xTarget;
					moveColumn = false;
					move = true;
				}
			}
		}

	}

	/**
	 * Handle the touch on the screen
	 * 
	 * @param eventX
	 *            x coordinate of the touched point
	 * @param eventY
	 *            y coordinate of the touched point
	 */
	public void handleAction(int eventX, int eventY) {
		if (eventX >= (x - bitmap.getWidth() / 2)
				&& (eventX <= (x + bitmap.getWidth() / 2))) {
			if (eventY >= (y - bitmap.getHeight() / 2)
					&& (eventY <= (y + bitmap.getHeight() / 2))) {
				setTouched(true);
			} else {
				setTouched(false);
			}
		} else {
			setTouched(false);
		}

	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isTouched() {
		return touched;
	}

	public void setTouched(boolean touched) {
		this.touched = touched;
	}

	public boolean isMove() {
		return move;
	}
}
