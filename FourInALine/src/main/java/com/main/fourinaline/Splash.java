package com.main.fourinaline;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.Menu;
import android.widget.VideoView;

/**
 * This class is an activity, it shows the splash video and open the main menu activity.
 * @author Richard GUERCI & Julien SIERRA
 *
 */
public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.activity_splash);
		
		// Starting background sound
		StartMenuActivity.musicP = new MusicPlayer(this, R.raw.background_sound,100, true);
		
		// Starting the splash video
		// from: http://stackoverflow.com/questions/19235812/android-eclipe-video-view-file-name-path
		VideoView videoView = (VideoView)findViewById(R.id.videoView);
		String elmo = "android.resource://" + getPackageName() + "/" + R.raw.splash_vid;
		videoView.setVideoURI(Uri.parse(elmo));
	    videoView.start();
	    
		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(3000);// waiting 3 seconds (time of the video) before going to main menu
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finally{
					// Start Main menu activity
					Intent openMain = new Intent("com.main.fourinaline.STARTMENUACTIVITY");
					startActivity(openMain);
				}

			}
		};
		timer.start();
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}
}
