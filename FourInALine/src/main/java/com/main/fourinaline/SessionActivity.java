package com.main.fourinaline;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.online.Online;
import com.online.Session;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This class is the activity using "activity_session.xml".
 * It allows the user to select an existing online session or creates his own.
 * 
 * After choosing or creating a session, the informations of this session are saved in Online.session
 * 
 * Some web site we use in this class:
 * http://www.vogella.com/articles/AndroidListView/article.html#listview_listviewexample
 * http://stackoverflow.com/questions/13281197/android-how-to-create-clickable-listview
 * http://samadmalik.com/simple-android-webview-spinner/
 * 
 * @author Richard GUERCI & Julien SIERRA
 *
 */
public class SessionActivity extends FragmentActivity implements OnItemClickListener, CreateSessionDialog.NoticeDialogListener{
	private ArrayList<Session> sessions; // List of all available online session
	private SessionArrayAdapter adapter; // ArrayAdapter used by the ListView of this activity

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Creating new session (an empty one for the moment)
		Online.session = new Session("","","","",true);

		// Initializing content with activity_session.xml
		setContentView(R.layout.activity_session);
		
		// Initializing texteditName
		Random rand = new Random();
		int  n = rand.nextInt(899) + 100;
		EditText editText = (EditText)findViewById(R.id.editTextName);
		editText.setText("Guest"+n);

		// Initializing the ListView
		ListView listview = (ListView) findViewById(R.id.listViewSessions);
		listview.setOnItemClickListener(this);
		// Use layout row_layout.xml
		sessions = new ArrayList<Session>();
		adapter = new SessionArrayAdapter(this,sessions);
		listview.setAdapter(adapter);
		
		// Hide waiting elements
		findViewById(R.id.progressBar).setVisibility(View.GONE);
		findViewById(R.id.textViewCreating).setVisibility(View.GONE);
		findViewById(R.id.textViewLoading).setVisibility(View.GONE);
		findViewById(R.id.textViewJoining).setVisibility(View.GONE);

		// Get online sessions from Internet
		refreshSessions(null);
	}

	/**
	 * This method starts a thread that downloads all available sessions
	 */
	private void createListView(){
		// Show loading element
		findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
		findViewById(R.id.textViewLoading).setVisibility(View.VISIBLE);
		// Starting the download sessions thread
		DownloadSessionListTask task = new DownloadSessionListTask();
		task.execute(new String[] { Online.sessions() });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.session, menu);
		return true;
	}

	/**
	 * Called by create session button
	 * This method allows to create a new session, calling CreateSessionDialog() for initializing the name session
	 * The user needs to fill in his name before creating a session
	 * @param view
	 */
	public void addSession(View view){
		new MusicPlayer(this, R.raw.click_sound,StartMenuActivity.volume,false);
		if(((EditText) findViewById(R.id.editTextName)).getText().toString().equals("")){ // Checking of the user name is filled in
			Toast.makeText(getApplicationContext(),"Enter your name first", Toast.LENGTH_LONG).show();
		}
		else if(((EditText) findViewById(R.id.editTextName)).getText().toString().contains(";")){
			Toast.makeText(getApplicationContext(),"Your name cannot contain semicolon character", Toast.LENGTH_LONG).show();
		}
		else{
			// starting a new dialog to create the new session
			FragmentManager fm = getSupportFragmentManager();
			DialogFragment newFragment = new CreateSessionDialog();
			newFragment.show(fm, "CreateSessionDialog");
		}
	}

	/**
	 * Called by refresh button and onCreate method
	 * It calls createListView() method for update the list of session
	 * It also checks if there is an Internet connectivity.
	 * 
	 * @param view
	 */
	public void refreshSessions(View view){
		new MusicPlayer(this, R.raw.click_sound,StartMenuActivity.volume, false);
		ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) { // Connection OK
			createListView();
		}
		else { // No connection... Go back in main menu
			Toast.makeText(getApplicationContext(),"No network connection...", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, StartMenuActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * Called when the back button is pressed down. It will load StartMenuActivity()
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent intent = new Intent(this, StartMenuActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Update the ListView
	 * @param result result form the web site (see DownloadSessionListTask)
	 */
	public void loadListView(String result){
		// Clean old list
		adapter.clear();
		sessions.clear();
		// Update session ArrayList if some sessions exist
		if(result.equals("")){
			Toast.makeText(getApplicationContext(),"No session available\n    Create your own", Toast.LENGTH_LONG).show();
		}
		else{
			String sessions_tmp[] = result.split("\n");
			for(int i=0; i<sessions_tmp.length; i++){
				String session_tmp[] = sessions_tmp[i].split(";");
				if(session_tmp.length==3){
					try { //check if it's an id (integer)
				        Integer.parseInt(session_tmp[0]);
						sessions.add(new Session(session_tmp[0],session_tmp[1],"",session_tmp[2],false));
				    } catch(NumberFormatException e) {}
				}
			}
		}
		// Updating the adapter
		adapter.notifyDataSetChanged();
	}


	/**
	 * This method is called when the user click on the ListView
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		new MusicPlayer(this, R.raw.click_sound,StartMenuActivity.volume, false);
		if(((EditText) findViewById(R.id.editTextName)).getText().toString().equals("")){ // The user need to fill up his name before joining a session
			Toast.makeText(getApplicationContext(),"Enter your name first", Toast.LENGTH_LONG).show();
		}
		else if(((EditText) findViewById(R.id.editTextName)).getText().toString().contains(";")){
			Toast.makeText(getApplicationContext(),"Your name cannot contain semicolon character", Toast.LENGTH_LONG).show();
		}
		else{
			//Updating session status
			Online.session = sessions.get(position);
			Online.session.setPlayer1(((EditText) findViewById(R.id.editTextName)).getText().toString());
			// Showing waiting element
			findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
			findViewById(R.id.textViewJoining).setVisibility(View.VISIBLE);
			JoinSessionTask task = new JoinSessionTask();
			task.execute(new String[] { Online.join(sessions.get(position).getId(), ((EditText) findViewById(R.id.editTextName)).getText().toString()) });
		}
	}



	/**
	 * Create online session
	 * Called by CreateSessionDialog
	 * @param name
	 */
	@Override
	public void onDialogPositiveClick(String name) {
		if(name.equals("")){
			Toast.makeText(getApplicationContext(),"Session name cannot be empty", Toast.LENGTH_LONG).show();
		}
		else if(name.contains(";")){
			Toast.makeText(getApplicationContext(),"Session name cannot contain semicolon character", Toast.LENGTH_LONG).show();
		}
		else{
			//Updating session status
			Online.session.setName(name);
			Online.session.setPlayer1(((EditText) findViewById(R.id.editTextName)).getText().toString());
			// Showing waiting elements
			findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
			findViewById(R.id.textViewCreating).setVisibility(View.VISIBLE);
			// Starting the create sessions thread
			CreateSessionTask task = new CreateSessionTask();
			task.execute(new String[] { Online.create(name, ((EditText) findViewById(R.id.editTextName)).getText().toString()) });
		}
	}

	
	/**
	 * This method allow to call the game activity
	 */
	public void startGame(){
		StartMenuActivity.stop_music=false;
		Intent intent = new Intent(this, GameActivity.class);

		String game = "ONLINE";
		intent.putExtra(StartMenuActivity.GAME, game);
		intent.putExtra(StartMenuActivity.LEVEL, "");
		
		startActivity(intent);
	}
	
	
	/**
	 * This class download the session list from Internet (HTTP request)
	 */
	private class DownloadSessionListTask extends AsyncTask<String, Void, String>{
		// Request
		@Override
		protected String doInBackground(String... urls) {
			String result = "";

			try {
				InputStream content = null;
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response = httpclient.execute(new HttpGet(urls[0]));
				content = response.getEntity().getContent();

				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String s = "";
				while ((s = buffer.readLine()) != null) {
					result += s+"\n";
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),"Server unreachable...", Toast.LENGTH_LONG).show();
			}

			return result;
		}

		// When the request is over
		@Override
		protected void onPostExecute(String result) {
			// Hide waiting elements
			findViewById(R.id.progressBar).setVisibility(View.GONE);
			findViewById(R.id.textViewLoading).setVisibility(View.GONE);
			// Update ListView
			loadListView(result);
		}
	}

	/**
	 * This class create an online session (HTTP request)
	 */
	private class CreateSessionTask extends AsyncTask<String, Void, String>{
		// Request
		@Override
		protected String doInBackground(String... urls) {
			String result = "";

			try {
				InputStream content = null;
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response = httpclient.execute(new HttpGet(urls[0]));
				content = response.getEntity().getContent();

				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String s = "";
				while ((s = buffer.readLine()) != null) {
					result += s+"\n";
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),"Server unreachable...", Toast.LENGTH_LONG).show();
			}

			return result;
		}

		// When the request is over
		@Override
		protected void onPostExecute(String result) {
			// Hide waiting elements
			findViewById(R.id.progressBar).setVisibility(View.GONE);
			findViewById(R.id.textViewCreating).setVisibility(View.GONE);

			// Start game activity (if no error)
			if(result.equals("Error")) Toast.makeText(getApplicationContext(),"Error HTTP Request", Toast.LENGTH_LONG).show();
			else{
				Online.session.setId(result); // Updating id of the session
				startGame();
			}
		}
	}
	
	/**
	 * This class allow the player to join a session (HTTP request)
	 */
	private class JoinSessionTask extends AsyncTask<String, Void, String>{
		@Override
		// Request
		protected String doInBackground(String... urls) {
			String result = "";

			try {
				InputStream content = null;
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response = httpclient.execute(new HttpGet(urls[0]));
				content = response.getEntity().getContent();

				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String s = "";
				while ((s = buffer.readLine()) != null) {
					result += s+"\n";
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),"Server unreachable...", Toast.LENGTH_LONG).show();
			}

			return result;
		}

		// When the request is over
		@Override
		protected void onPostExecute(String result) {
			// Hide waiting elements
			findViewById(R.id.progressBar).setVisibility(View.GONE);
			findViewById(R.id.textViewJoining).setVisibility(View.GONE);
			
			// Start game activity (if no error)
			if(result.equals("Error")) Toast.makeText(getApplicationContext(),"Error HTTP Request", Toast.LENGTH_LONG).show();
			else startGame();
		}
	}
	

	
	@Override
	protected void onPause() {
		super.onPause();
		if(StartMenuActivity.stop_music) StartMenuActivity.musicP.player.pause();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		StartMenuActivity.stop_music=true;
		StartMenuActivity.musicP.player.start();
	}
}
