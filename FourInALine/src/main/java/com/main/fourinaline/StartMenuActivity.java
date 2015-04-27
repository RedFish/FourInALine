package com.main.fourinaline;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * This activity shows the main menu of the app :
 * - 1 player (3 difficulties)
 * - 2 players
 * - Play online
 * - About
 * 
 * @author Richard GUERCI & Julien SIERRA
 *
 */
public class StartMenuActivity extends FragmentActivity {
	public static MusicPlayer musicP;
	public static int volume = 50;
	public final static String GAME = "com.main.fourinaline.GAME";
	public final static String LEVEL = "com.main.fourinaline.LEVEL";
	public static boolean stop_music = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_menu, menu);
		return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(stop_music) musicP.player.pause();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		stop_music=true;
		musicP.player.start();
	}

	/**
	 * 1 player button clicked; Show a dialog allowing the user to choose between the 3 difficulties.
	 * @param view
	 */
	public void playOnePlayer(View view) {
		stop_music=false;//do not stop the music when onPause() is called
		new MusicPlayer(this, R.raw.click_sound, volume, false);
		/*CharSequence text = "Not yet implemented ...";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(getBaseContext(), text, duration);
		toast.show();*/

		FragmentManager fm = getSupportFragmentManager(); 
		DialogFragment newFragment = new DifficultyDialog(this); newFragment.show(fm, "difficulty");

	}
	
	/**
	 * 2 player button clicked; Start the game
	 * @param view
	 */
	public void playTwoPlayer(View view) {
		stop_music=false;//do not stop the music when onPause() is called
		new MusicPlayer(this, R.raw.click_sound,volume,false);
		Intent intent = new Intent(this, GameActivity.class);

		String game = "TWO_PLAYER";
		/*CharSequence text = "TWO PLAYER ...";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(getBaseContext(), text, duration);
		toast.show();*/
		intent.putExtra(GAME, game);
		intent.putExtra(LEVEL, "");

		startActivity(intent);
	}

	/**
	 * Online button clicked; Start SessionActivity()
	 * @param view
	 */
	public void playOnline(View view) {
		stop_music=false;//do not stop the music when onPause() is called
		
		Intent intent = new Intent(this, SessionActivity.class);

		String game = "ONLINE";
		intent.putExtra(GAME, game);
		intent.putExtra(LEVEL, "");

		startActivity(intent);
	}

	/**
	 * About button clicked; Shows an about dialog
	 * from : http://android.okhelp.cz/create-about-dialog-android-example/
	 * @param view
	 */
	public void about(View view) {
		new MusicPlayer(this, R.raw.click_sound,volume,false);
		// Inflate the about message contents
		View messageView = getLayoutInflater().inflate(R.layout.about, null,false);

		// When linking text, force to always use default color. This works
		// around a pressed color state bug.
		TextView textView = (TextView) messageView
				.findViewById(R.id.about_credits);
		int defaultColor = textView.getTextColors().getDefaultColor();
		textView.setTextColor(defaultColor);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(R.string.app_name);
		builder.setView(messageView);
		builder.create();
		builder.show();
	}
	
	/**
	 * Switch sound on/off clicked
	 * @param view
	 */
	public void switchSoundOnOff(View view) {
		MusicPlayer.on_off=(MusicPlayer.on_off+1)%2;
		ImageButton imgButton = (ImageButton) findViewById(R.id.imageButtonSound);
		if(MusicPlayer.on_off == 1){
			imgButton.setBackgroundResource(R.drawable.volume_on);
		}
		else{
			imgButton.setBackgroundResource(R.drawable.volume_off);
		}
		musicP.player.pause();
		musicP.mBackgroundSound.cancel(true);
		musicP = new MusicPlayer(this, R.raw.background_sound,100, true);
	}
}
