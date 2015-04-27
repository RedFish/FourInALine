package com.main.fourinaline;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * This class shows a dialog allowing the user to fill in the name of the session he want to create.
 * Dialog : http://developer.android.com/guide/topics/ui/dialogs.html
 * @author Richard GUERCI & Julien SIERRA
 *
 */
@SuppressLint("ValidFragment")
public class CreateSessionDialog extends DialogFragment {

	public interface NoticeDialogListener {
		public void onDialogPositiveClick(String name);
	}

	public NoticeDialogListener  mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (NoticeDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
		}
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		final View layout = inflater.inflate(R.layout.create_session_layout, null);
		builder.setView(layout);
		
		Random rand = new Random();
		int  n = rand.nextInt(899) + 100;
		EditText editText = (EditText)layout.findViewById(R.id.editTextSessionName);
		editText.setText("Session"+n);

		// Add action buttons
		builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				new MusicPlayer(getActivity(), R.raw.click_sound,StartMenuActivity.volume,false);
				EditText et = (EditText) layout.findViewById(R.id.editTextSessionName);
				mListener.onDialogPositiveClick(et.getText().toString());
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				new MusicPlayer(getActivity(), R.raw.click_sound,StartMenuActivity.volume,false);
				CreateSessionDialog.this.getDialog().cancel();
			}
		});      
		return builder.create();
	}
}
