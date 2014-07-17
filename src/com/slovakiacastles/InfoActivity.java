package com.slovakiacastles;

import java.io.IOException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

@SuppressLint("NewApi")
public class InfoActivity extends BaseActivity implements MediaPlayerControl {

	TextView textTitle;
	TextView textDescription;
	TextView textUri;
	ImageView imageView;
	SQLiteDatabase db;
	private MediaController mMediaController;
	private MediaPlayer mMediaPlayer;
	ImageButton buttonPlay;
	ImageButton buttonStop;
	String prefix;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		prefix = prefs.getString("prefix", "");
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
			if (Build.VERSION.SDK_INT >= 15) {
				ActionBar bar = getActionBar();
				bar.setDisplayHomeAsUpEnabled(true);
			}
			Intent intent = getIntent();
			String title = intent.getStringExtra("title");
			DataBaseHelper myDbHelper = new DataBaseHelper(InfoActivity.this);
			try {
				myDbHelper.createDataBase();
			} catch (IOException ioe) {
				throw new Error("Unable to create database");
			}
			try {
				myDbHelper.openDataBase();
			} catch (SQLException sqle) {
				throw sqle;
			}
			db = myDbHelper.getWritableDatabase();
			String image = "";
			String description = "";
			String audioFile = "";
			Cursor c = db.query("info_data", new String[] { "*" }, "name"
					+ prefix + " LIKE \"" + title + "%\"", null, null, null,
					null);
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				image = c.getString(c.getColumnIndex("image"));
				description = c.getString(c.getColumnIndex("description"
						+ prefix));
				audioFile = c.getString(c.getColumnIndex("audio")) + ".mp3";
			}
			if (audioFile.contains("null"))
				audioFile = "audio.mp3";
			
			if(description.equals(""))
			{
				description = getString(getResources()
						.getIdentifier(
								"empty_info" + prefix,
								"string",
								getPackageName()));
			}
			
			imageView = (ImageView) findViewById(R.id.imageView1);
			textTitle = (TextView) findViewById(R.id.textView1);
			SpannableString spanString = new SpannableString(
					getString(getResources().getIdentifier(
							"description_info" + prefix, "string",
							getPackageName())));
			spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
			spanString.setSpan(new StyleSpan(Typeface.BOLD), 0,
					spanString.length(), 0);
			spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0,
					spanString.length(), 0);
			//textUri = (TextView) findViewById(R.id.textView3);
			// buttonPlay = (ImageButton) findViewById(R.id.button1);
			// buttonStop = (ImageButton) findViewById(R.id.button2);
			// buttonPlay.setBackgroundResource(R.drawable.play);
			// buttonStop.setBackgroundResource(R.drawable.pause);
			textDescription = (TextView) findViewById(R.id.textView2);
			imageView.setImageResource(this.getResources().getIdentifier(
					"drawable/" + image, null, this.getPackageName()));
			textTitle.setText(title);
			textDescription.setText(description + "\n\n");
			//textUri.setText(spanString);

			mMediaPlayer = new MediaPlayer();
			mMediaController = new MediaController(this);
			mMediaController.setMediaPlayer(InfoActivity.this);
			/*
			 * mMediaController.setAnchorView(findViewById(R.id.mediaController1)
			 * ); AssetFileDescriptor afd = null; try { afd =
			 * getAssets().openFd(audioFile); } catch (IOException e1) { // TODO
			 * Auto-generated catch block e1.printStackTrace(); } try {
			 * mMediaPlayer
			 * .setDataSource(afd.getFileDescriptor(),afd.getStartOffset
			 * (),afd.getLength()); mMediaPlayer.prepare();
			 * mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			 * 
			 * @Override public void onPrepared(MediaPlayer mp) {
			 * mHandler.post(new Runnable() { public void run() {
			 * //mMediaController.show(0); mMediaPlayer.start(); } }); } }); }
			 * catch (IOException e) { Log.e("PlayAudioDemo",
			 * "Could not open file " + audioFile + " for playback.", e); }
			 */
		} else {
			Toast toast = Toast.makeText(
					getApplicationContext(),
					getString(getResources().getIdentifier(
							"no_google_play_services" + prefix, "string",
							getPackageName())), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			onBackPressed();
		}
		
		enableGpsModal(prefix);
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return false;
	}

	@Override
	public boolean canSeekForward() {
		return false;
	}

	@Override
	public int getBufferPercentage() {
		int percentage = (mMediaPlayer.getCurrentPosition() * 100)
				/ mMediaPlayer.getDuration();

		return percentage;
	}

	@Override
	public int getCurrentPosition() {
		return mMediaPlayer.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	@Override
	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}

	@Override
	public void pause() {
		if (mMediaPlayer.isPlaying())
			mMediaPlayer.pause();
	}

	@Override
	public void seekTo(int pos) {
		mMediaPlayer.seekTo(pos);
	}

	@Override
	public void start() {
		mMediaPlayer.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mMediaController.show();

		return false;
	}

	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void playClick(View v) {
		start();
	}

	public void pauseClick(View v) {
		if (isPlaying())
			pause();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_rout:
			Intent i = new Intent(InfoActivity.this, RoutActivity.class);
			i.putExtra("title", textTitle.getText());
			startActivity(i);
			return true;
		case R.id.action_list:
			Intent in = new Intent(InfoActivity.this, PlacesActivity.class);
			startActivity(in);
			return true;
		case android.R.id.home:
			Intent intent = new Intent(this, StartActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_info, container,
					false);
			return rootView;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_rout, menu);
		return super.onCreateOptionsMenu(menu);
	}

//	public void goToMap(View v) {
//		if (Build.VERSION.SDK_INT <= 15) {
//			Intent i = new Intent(InfoActivity.this, RoutActivity.class);
//			i.putExtra("title", textTitle.getText());
//			startActivity(i);
//		}
//	}

}
