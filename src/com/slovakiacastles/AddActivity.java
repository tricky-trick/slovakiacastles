package com.slovakiacastles;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

@SuppressLint("NewApi")
public class AddActivity extends Activity {

	TextView coordView;
	TextView addName;
	TextView addDescr;
	Button buttDone;
	EditText name;
	EditText description;
	Handler handler = new Handler();
	String coord;
	String namePlace;
	String descPlace;
	String result;
	String prefix;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		prefix = getIntent().getStringExtra("prefix");
		coordView = (TextView) findViewById(R.id.coordView);
		addName = (TextView) findViewById(R.id.addNameView);
		addDescr = (TextView) findViewById(R.id.addDescView);
		buttDone = (Button) findViewById(R.id.done);
		name = (EditText) findViewById(R.id.addNameField);
		description = (EditText) findViewById(R.id.addDescField);
		coordView.setText(getResources().getIdentifier("add_coord" + prefix, "string", this.getPackageName()) + getIntent().getStringExtra("position"));
		addName.setText(getResources().getIdentifier("add_name" + prefix, "string", this.getPackageName()));
		addDescr.setText(getResources().getIdentifier("add_descr" + prefix, "string", this.getPackageName()));
		buttDone.setText(getResources().getIdentifier("add_done" + prefix, "string", this.getPackageName()));
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();

		StrictMode.setThreadPolicy(policy);
		
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
	}

	private class SendResults extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			prefix = getIntent().getStringExtra("prefix");
			coord = coordView.getText().toString().split(":")[1].trim();
			namePlace = name.getText().toString();
			descPlace = description.getText().toString();
			result = "";

			handler.post(new Runnable() {

				@Override
				public void run() {
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("coord", coord));
					nameValuePairs
							.add(new BasicNameValuePair("name", namePlace));
					nameValuePairs.add(new BasicNameValuePair("description",
							descPlace));
					try {
						HttpClient httpclient = new DefaultHttpClient();
						HttpPost httppost = new HttpPost(
								"http://lvivguide.zayco.com.ua/addplace.php");
						httppost.setEntity(new UrlEncodedFormEntity(
								nameValuePairs, HTTP.UTF_8));
						httpclient.execute(httppost);
						Toast toast = Toast.makeText(getApplicationContext(),
								getResources().getIdentifier("add_thanks" + prefix, "string", getPackageName()), Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						name.setText("");
						description.setText("");

						onBackPressed();

					} catch (Exception e) {
						Log.e("log_tag",
								"Error in http connection " + e.toString());
					}

				}
			});
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			prefix = getIntent().getStringExtra("prefix");
		}
	}

	public void clickDone(View v) {
		SendResults send = new SendResults();
		send.execute();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{    
	   switch (item.getItemId()) 
	   {        
	      case android.R.id.home:            
	         Intent intent = new Intent(this, StartActivity.class);            
	         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
	         intent.putExtra("prefix", prefix);
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
			View rootView = inflater.inflate(R.layout.fragment_add, container,
					false);
			return rootView;
		}
	}
}
