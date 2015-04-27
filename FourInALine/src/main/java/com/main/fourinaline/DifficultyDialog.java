package com.main.fourinaline;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * This class shows a dialog allowing the user to choose one of the 3 difficulties
 * Dialog : http://developer.android.com/guide/topics/ui/dialogs.html
 * @author Richard GUERCI & Julien SIERRA
 *
 */
@SuppressLint("ValidFragment")
public class DifficultyDialog extends DialogFragment {
	StartMenuActivity activity;
	
	public DifficultyDialog(StartMenuActivity startMenuActivity) {
		activity = startMenuActivity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		StartMenuActivity.stop_music=false;//do not stop the music when onPause() is called
		super.onCreateDialog(savedInstanceState);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.difficulty_question);
		builder.setItems(R.array.difficulties, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				new MusicPlayer(getActivity(), R.raw.click_sound,StartMenuActivity.volume,false);
				// The 'which' argument contains the index position
				// of the selected item
				
		    	Intent intent = new Intent(activity, GameActivity.class);
		    	
		    	// Save the choice of the user
		    	String game = "ONE_PLAYER";
		        intent.putExtra(StartMenuActivity.GAME, game);
		        String level = (which+1)+"";
		        intent.putExtra(StartMenuActivity.LEVEL, level);
		        
		        // Start the game
		        startActivity(intent);
			}
		});
		return builder.create();
	}
}
