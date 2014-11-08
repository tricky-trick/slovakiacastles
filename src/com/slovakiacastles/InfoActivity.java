package com.slovakiacastles;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

@SuppressWarnings("deprecation")
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
	SocialAuthAdapter adapter;
	boolean status;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		prefix = prefs.getString("prefix", "");
		RelativeLayout relBar = (RelativeLayout) findViewById(R.id.relbar);
		RelativeLayout zoomBar = (RelativeLayout) findViewById(R.id.zoombar);
		LinearLayout linearBar = (LinearLayout) findViewById(R.id.linearbar);
		linearBar.setBackgroundResource(R.drawable.bar_gradient);
		
		if (Build.VERSION.SDK_INT >= 11) {
			linearBar.setAlpha((float) 0.8);
			relBar.setAlpha((float) 0.8);
			zoomBar.setAlpha((float) 0.6);
		}

		// Add Bar to library
		adapter = new SocialAuthAdapter(new ResponseListener());

		// Add providers
		adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
		adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
		adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);
		adapter.addProvider(Provider.EMAIL, R.drawable.gmail);

		// For twitter use add callback method. Put your own callback url here.
		adapter.addCallBack(Provider.TWITTER, "https://twitter.com/");
		textDescription = (TextView) findViewById(R.id.textView2);

		// Add keys and Secrets
		try {
			adapter.addConfig(Provider.FACEBOOK, "297841130364674",
					"dc9c59d0c72d4f2533580e80ba4c2a59", null);
			adapter.addConfig(Provider.TWITTER, "1B8FBZxvW9u9Ps8r2bWVuNwj9",
					"SeMAWeGCC4SrBf6f66PlgUBc2yliOWwdrcOLKHOAIPX3rdxXWA", null);
			adapter.addConfig(Provider.LINKEDIN, "778g2um8c3ohar",
					"vzHUufjGeVI5TE8e", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		adapter.enable(linearBar);

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
			LinkedList<Integer> images = new LinkedList<Integer>();
			images.add(this.getResources().getIdentifier("drawable/" + image,
					null, this.getPackageName()));
			for (int i = 1; i < 10; i++) {
				try {
					if (this.getResources().getIdentifier(
							"drawable/" + image + i, null,
							this.getPackageName()) != 0) {
						images.add(this.getResources().getIdentifier(
								"drawable/" + image + i, null,
								this.getPackageName()));
					}
				} catch (Exception e) {
				}
			}
			Gallery gallery = (Gallery) findViewById(R.id.gallery1);

			if (images.size() > 1) {
				TranslateAnimation animation = new TranslateAnimation(0.0f,
						-50.0f, 0.0f, 0.0f); // new
												// TranslateAnimation(xFrom,xTo,
												// yFrom,yTo)
				animation.setDuration(1000); // animation duration
				animation.setRepeatCount(1); // animation repeat count
				animation.setRepeatMode(2);
				gallery.startAnimation(animation);
				gallery.setSelection(1);
				gallery.setSpacing(2);
			}

			gallery.setAdapter(new GalleryImageAdapter(this, images));
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
			textUri = (TextView) findViewById(R.id.textView3);
			// buttonPlay = (ImageButton) findViewById(R.id.button1);
			// buttonStop = (ImageButton) findViewById(R.id.button2);
			// buttonPlay.setBackgroundResource(R.drawable.play);
			// buttonStop.setBackgroundResource(R.drawable.pause);

			textTitle.setText(title);
			textDescription.setText(description + "\n\n");
			textUri.setText(spanString);

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

	private final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {

			// Variable to receive message status
			Log.d("Share-Bar", "Authentication Successful");

			// Get name of provider after authentication
			final String providerName = values
					.getString(SocialAuthAdapter.PROVIDER);
			Log.d("Share-Bar", "Provider Name = " + providerName);
			// Toast.makeText(InfoActivity.this, providerName + " connected",
			// Toast.LENGTH_SHORT).show();
			// call to update on all connected providers at once
			adapter.updateStatus(
					getString(getResources().getIdentifier("welcome" + prefix,
							"string", getPackageName()))
							+ textTitle.getText().toString()
							+ "\nDownload application from https://play.google.com/store/apps/details?id=com.ukrcastles",
					new MessageListener(), false);

			// Share via Email Intent
			if (providerName.equalsIgnoreCase("share_mail")) {
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
						Uri.fromParts("mailto", "", null));
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"I like this application 'Castles of Ukraine'");
				emailIntent
						.putExtra(
								android.content.Intent.EXTRA_TEXT,
								"Visit our page on https://play.google.com/store/apps/details?id=com.ukrcastles");

				Uri uri = Uri.parse("android.resource://com.ukrcastles/"
						+ R.drawable.ic_launcher);
				emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
				startActivity(Intent.createChooser(emailIntent, "Email"));
			}

			// Share via mms intent
			if (providerName.equalsIgnoreCase("share_mms")) {
				File file = new File(
						Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
						"image5964402.png");
				Uri uri = Uri.fromFile(file);

				Intent mmsIntent = new Intent(Intent.ACTION_SEND, uri);
				mmsIntent.putExtra("sms_body", "Test");
				mmsIntent.putExtra(Intent.EXTRA_STREAM, uri);
				mmsIntent.setType("image/png");
				startActivity(mmsIntent);
			}
		}

		@Override
		public void onError(SocialAuthError error) {
			error.printStackTrace();
			Log.d("Share-Bar", error.getMessage());
		}

		@Override
		public void onCancel() {
			Log.d("Share-Bar", "Authentication Cancelled");
		}

		@Override
		public void onBack() {
			Log.d("Share-Bar", "Dialog Closed by pressing Back Key");

		}
	}

	// To get status of message after authentication
	private final class MessageListener implements SocialAuthListener<Integer> {
		@Override
		public void onExecute(String provider, Integer t) {
			Integer status = t;
			if (status.intValue() == 200 || status.intValue() == 201
					|| status.intValue() == 204)
				Toast.makeText(InfoActivity.this,
						"Message posted on " + provider, Toast.LENGTH_LONG)
						.show();
			else {
				Toast.makeText(InfoActivity.this,
						"Message not posted on" + provider, Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		public void onError(SocialAuthError e) {
			Toast.makeText(InfoActivity.this, "Error " + e, Toast.LENGTH_LONG)
					.show();
		}
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

	public void zoomIn(View v) {
		float size = textDescription.getTextSize();
		textDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, size + 2);
	}

	public void zoomOut(View v) {
		float size = textDescription.getTextSize();
		textDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, size - 2);
	}

	// public void goToMap(View v) {
	// if (Build.VERSION.SDK_INT <= 15) {
	// Intent i = new Intent(InfoActivity.this, RoutActivity.class);
	// i.putExtra("title", textTitle.getText());
	// startActivity(i);
	// }
	// }

}
