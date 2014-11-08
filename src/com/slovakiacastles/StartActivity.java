package com.slovakiacastles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class StartActivity extends BaseActivity {

	Button buttonMap;
	Button buttonPlaces;
	String prefix;
	ImageView image;
	private static final int MAIN_MENU_ID = 1001;
	private static final int ABOUT_MENU_ID = 1005;
	private static final int APP_MENU_ID = 1010;
	SQLiteDatabase db;
	DataBaseHelper myDbHelper;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefix = prefs.getString("prefix", "");
		buttonMap = (Button) findViewById(R.id.localmap);
		buttonPlaces = (Button) findViewById(R.id.place);
		image = (ImageView) findViewById(R.id.imageView1);
		buttonMap.setText(getResources().getIdentifier(
				"start_button_map" + prefix, "string", getPackageName()));
		buttonPlaces.setText(getResources().getIdentifier(
				"start_button_places" + prefix, "string", getPackageName()));
		enableGpsModal(prefix);
		
		ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f,
                0.0f, 1.0f);
        animation.setDuration(2000); 
        image.setAnimation(animation);
	}

	public void startPlace(View v) {
		Intent i = new Intent(StartActivity.this, PlacesActivity.class);
		i.putExtra("prefix", prefix);
		startActivity(i);
	}

	public void startLocalMap(View v) {
		Intent i = new Intent(StartActivity.this, MapActivity.class);
		i.putExtra("prefix", prefix);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		MenuInflater inflater = getMenuInflater();
//		if(prefs.getString("isNew", "2").equals("3"))
//		{
//			inflater.inflate(R.menu.menu_app, menu);	
//		}
//		else{
//			inflater.inflate(R.menu.menu_app_notif, menu);
//		}
		inflater.inflate(R.menu.menu_app, menu);	
		menu.add(
				0,
				MAIN_MENU_ID,
				0,
				getString(getResources().getIdentifier(
						"change_language" + prefix, "string", getPackageName())));
		menu.add(
				0,
				ABOUT_MENU_ID,
				1,
				getString(getResources().getIdentifier("about_menu" + prefix,
						"string", getPackageName())));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		if (item.getItemId() == MAIN_MENU_ID) {
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
			Editor editor = prefs.edit();
			editor.putString("prefix", "");
			editor.commit();
			i = new Intent(StartActivity.this, MainActivity.class);
		} 
		else if (item.getItemId() == ABOUT_MENU_ID){
			i = new Intent(StartActivity.this, AboutActivity.class);
		}else {
			i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Denys+Zaiats"));
			Editor editor = prefs.edit();
			editor.putString("isNew", "3");
			editor.commit();
		} 
		startActivity(i);
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Monitor launch times and interval from installation
		RateThisApp.onStart(this);
		// Show a dialog if criteria is satisfied
		RateThisApp.showRateDialogIfNeeded(this);
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
			View rootView = inflater.inflate(R.layout.fragment_start,
					container, false);
			return rootView;
		}
	}
}
