package com.main.fourinaline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class GameActivity extends Activity {
	GamePanel game_panel;
	Activity thisAct;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		thisAct = this;
		LocalBroadcastManager.getInstance(this).registerReceiver(
				new MessageHandler(), new IntentFilter("kill"));

		// Get game and level from the intent
		Intent intent = getIntent();
		String game = intent.getStringExtra(StartMenuActivity.GAME);
		String level = intent.getStringExtra(StartMenuActivity.LEVEL);

		// requesting to turn the title OFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// set our MainGamePanel as the View
		if(game.equals("ONE_PLAYER")) game_panel = new GamePanel(this,level);
		else if(game.equals("TWO_PLAYER")) game_panel = new GamePanel(this,false);
		else if(game.equals("ONLINE")) game_panel = new GamePanel(this,true);
		
		setContentView(game_panel);
	}

	public void onBackPressed() {
		game_panel.onPause();
		StartMenuActivity.stop_music=false;
		Intent intent = new Intent(thisAct, StartMenuActivity.class);
		startActivity(intent);
	}

	public class MessageHandler extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			gameOver(intent.getStringExtra("text"));

		}
	}

	@SuppressLint("NewApi")
	public void gameOver(String text) {
		// Inflate the about message contents
		View messageView = getLayoutInflater().inflate(R.layout.activity_end,
				null, false);
		TextView textView = (TextView) messageView
				.findViewById(R.id.GameOverText);
		
		textView.setText(text);
		int defaultColor = textView.getTextColors().getDefaultColor();
		textView.setTextColor(defaultColor);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(R.string.app_name);
		builder.setView(messageView);
		builder.create();
		builder.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				Intent intent = new Intent(thisAct, StartMenuActivity.class);
				startActivity(intent);
			}

		});
		builder.show();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(StartMenuActivity.stop_music) StartMenuActivity.musicP.player.pause();
		//System.err.println("onpause");
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		//System.err.println("onresume");
		StartMenuActivity.stop_music=true;
		StartMenuActivity.musicP.player.start();
	}

}
