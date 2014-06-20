package com.slovakiacastles;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.TextView;

@SuppressLint("NewApi")
public class AboutActivity extends BaseActivity {

	String prefix;
	TextView message;
	TextView email;
	TextView messageSecond;
	TextView uri;
	TextView version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		message = (TextView) findViewById(R.id.textViewAbout);
		email = (TextView) findViewById(R.id.textViewAboutLink);
		messageSecond = (TextView) findViewById(R.id.textViewAboutSecond);
		uri = (TextView) findViewById(R.id.textViewAboutSecondLink);
		version = (TextView) findViewById(R.id.textViewVersion);
		
		try {
			version.setText("v " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
		}
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		prefix = prefs.getString("prefix", "");
		if (Build.VERSION.SDK_INT >= 15) {
			android.app.ActionBar bar = getActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
		}

		message.setText(getResources().getIdentifier("about_message" + prefix,
				"string", getPackageName()));

		messageSecond.setText(getResources().getIdentifier(
				"about_second" + prefix, "string", getPackageName()));
		
		enableGpsModal(prefix);

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, StartActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
