package com.slovakiacastles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	SQLiteDatabase db;
	DataBaseHelper myDbHelper;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (!prefs.getString("prefix", "").equals("")) {
			if (Build.VERSION.SDK_INT >= 15) {
				Intent i = new Intent(MainActivity.this, StartActivity.class);
				startActivity(i, savedInstanceState);
			}
		} 
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void langUa(View v){
		updateValue("_ua");
		Intent i = new Intent(MainActivity.this,
				StartActivity.class);
		startActivity(i);
	}
	
	public void langPl(View v){
		updateValue("_pl");
		Intent i = new Intent(MainActivity.this,
				StartActivity.class);
		startActivity(i);
	}
	
	public void langEn(View v){
		updateValue("_en");
		Intent i = new Intent(MainActivity.this,
				StartActivity.class);
		startActivity(i);
	}
	
	public void langHu(View v){
		updateValue("_hu");
		Intent i = new Intent(MainActivity.this,
				StartActivity.class);
		startActivity(i);
	}
	
	public void langSk(View v){
		updateValue("_sk");
		Intent i = new Intent(MainActivity.this,
				StartActivity.class);
		startActivity(i);
	}

	private void updateValue(String val) {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		editor.putString("prefix", val);
		editor.commit();
	}
	
	

	@SuppressWarnings("unused")
	private void addShortcut() {
		// Adding shortcut for MainActivity
		// on Home screen
		Intent shortcutIntent = new Intent(getApplicationContext(),
				MainActivity.class);

		shortcutIntent.setAction(Intent.ACTION_MAIN);

		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, R.string.app_name);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(
						getApplicationContext(), R.drawable.ic_launcher));

		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		getApplicationContext().sendBroadcast(addIntent);
	}
}
