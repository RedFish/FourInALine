package com.main.fourinaline;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

/**
 * MusicPlayer class allow to play music from raw directory
 * @author Richard GUERCI & Julien SIERRA
 *
 */
public class MusicPlayer {
	private Context context;
	private int id_music;
	private boolean loop;
	private int volume;
	public BackgroundSound mBackgroundSound;
	public MediaPlayer player;
	public static int on_off = 1;

	public MusicPlayer(final Context context, final int id_music, int volume, boolean loop) {
		this.context=context;
		this.id_music=id_music;
		this.loop=loop;
		this.volume=volume;

		mBackgroundSound = new BackgroundSound();
		mBackgroundSound.execute();
	}

	public class BackgroundSound extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			player = MediaPlayer.create(context, id_music); 
			player.setLooping(loop); // Set looping 
			player.setVolume(on_off*volume,on_off*volume); 
			player.start();
			return null;
		}
	}
}


