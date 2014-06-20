package com.slovakiacastles;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

@SuppressLint("NewApi")
public class RoutActivity extends FragmentActivity {

	DataBaseHelper myDbHelper;
	LatLng myCoord = null;
	LatLng myNearCoord = null;
	SQLiteDatabase db;
	GPSTracker gpsTracker;
	GoogleMap map;
	private Handler mHandler = new Handler();
	private Handler spHandler = new Handler();
	String title;
	Marker marker;
	String prefix;
	private static final int NEW_MENU_ID = Menu.FIRST + 2;
	private String type = "driving";
	private String distance;
	private String time;
	String country;
	String city;
	String addressLine;
	String stringLatitude;
	String stringLongitude;
	PolylineOptions rectLineDist;

	private class AsyncMaps extends AsyncTask<String, Void, Integer> {
		@Override
		protected void onPreExecute() {
			spHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					setProgressBarIndeterminateVisibility(true);
				}
			});
		}

		@Override
		protected Integer doInBackground(String... params) {
			Intent intent = getIntent();
			title = intent.getStringExtra("title");
			myDbHelper = new DataBaseHelper(RoutActivity.this);
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
			if (params.length != 0)
				type = params[0];
			mHandler.post(new Runnable() {
				@SuppressLint("NewApi")
				public void run() {
					// check if GPS enabled
					// Get a handle to the Map Fragment
					String coordinates = "";
					if (map == null) {
						map = ((SupportMapFragment) getSupportFragmentManager()
								.findFragmentById(R.id.mapView)).getMap();
						map.setMyLocationEnabled(true);
					}
					gpsTracker = new GPSTracker(RoutActivity.this);

					if (gpsTracker.canGetLocation()) {
						stringLatitude = String.valueOf(gpsTracker.latitude);
						stringLongitude = String.valueOf(gpsTracker.longitude);
						if (stringLatitude.equals("0.0")) {
							stringLatitude = "49.853192";
							stringLongitude = "24.024499";
						}
						country = gpsTracker.getCountryName(RoutActivity.this);
						city = gpsTracker.getLocality(RoutActivity.this);
						addressLine = gpsTracker
								.getAddressLine(RoutActivity.this);
						map.clear();
						myCoord = new LatLng(
								Double.parseDouble(stringLatitude), Double
										.parseDouble(stringLongitude));

					}
					db = myDbHelper.getWritableDatabase();
					Cursor c = db.query("info_data", new String[] { "*" },
							"name" + prefix + " LIKE \"" + title + "%\"", null,
							null, null, null);
					for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

						coordinates = c.getString(c
								.getColumnIndex("coordinates"));
					}

					myNearCoord = new LatLng(Double.parseDouble(coordinates
							.split(",")[0]), Double.parseDouble(coordinates
							.split(",")[1]));

					// Walking
					if (isNetworkAvailable()) {
						try {
							GMapV2Direction mdDist = new GMapV2Direction();
							Document doc_dist = mdDist.getDocument(myCoord,
									myNearCoord, type);
							ArrayList<LatLng> directionPointDist = mdDist
									.getDirection(doc_dist);
							distance = mdDist.getDistanceText(doc_dist);
							time = mdDist.getDurationText(doc_dist);
							rectLineDist = new PolylineOptions().width(6)
									.color(Color.GREEN);

							for (int i = 0; i < directionPointDist.size(); i++) {
								rectLineDist.add(directionPointDist.get(i));
							}
						} catch (Exception ex) {
						}
					} else {
						Toast toast = Toast.makeText(
								getApplicationContext(),
								getString(getResources().getIdentifier(
										"no_inet_string" + prefix, "string",
										getPackageName())), Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}

				}
			});

			return null;
		}

		@Override
		protected void onPostExecute(Integer i) {
			map.addMarker(new MarkerOptions()
					.title(title)
					.snippet(
							distance
									+ " - "
									+ time
									+ "\n\n("
									+ getString(getResources().getIdentifier(
											"touch_here" + prefix, "string",
											getPackageName())) + ")")
					.anchor(0.0f, 1.0f)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.finish))
					.position(myNearCoord));
			db.close();
			setProgressBarIndeterminateVisibility(false);
			map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {
					String title = marker.getTitle();
					if (title.equals(getString(getResources().getIdentifier(
							"add_place_string" + prefix, "string",
							getPackageName())))) {
						Intent intent = new Intent(RoutActivity.this,
								AddActivity.class);
						intent.putExtra(
								"position",
								marker.getPosition().latitude + ","
										+ marker.getPosition().longitude);
						startActivity(intent);
					} else {
						if (title.equals(getString(getResources()
								.getIdentifier("you_here_string" + prefix,
										"string", getPackageName())))) {
						} else {
							Intent intent = new Intent(RoutActivity.this,
									InfoActivity.class);
							intent.putExtra("title", title.trim());
							startActivity(intent);
						}
					}
				}
			});

			// map.setOnMarkerClickListener(new OnMarkerClickListener() {
			//
			// @Override
			// public boolean onMarkerClick(Marker marker) {
			// String title = marker.getTitle();
			// if (title.equals(getResources().getIdentifier(
			// "add_place_string" + prefix, "string",
			// getPackageName()))) {
			// Intent intent = new Intent(RoutActivity.this,
			// AddActivity.class);
			// intent.putExtra(
			// "position",
			// marker.getPosition().latitude + ","
			// + marker.getPosition().longitude);
			// startActivity(intent);
			// } else {
			// if (title.equals(getResources().getIdentifier(
			// "you_here_string" + prefix, "string",
			// getPackageName()))) {
			// } else {
			// Intent intent = new Intent(RoutActivity.this,
			// InfoActivity.class);
			// intent.putExtra("title", title);
			// startActivity(intent);
			// }
			// }
			// return true;
			// }
			// });

			/*
			 * map.setOnMapClickListener(new OnMapClickListener() {
			 * 
			 * @Override public void onMapClick(LatLng point) { if (marker !=
			 * null) marker.remove(); marker = map.addMarker(new MarkerOptions()
			 * .title(getResources().getString(
			 * R.string.add_place_string)).position(point) .draggable(true)); }
			 * });
			 */
			map.addMarker(new MarkerOptions()
					.title(getString(getResources().getIdentifier(
							"you_here_string" + prefix, "string",
							getPackageName())))
					.snippet(
							country + ", " + city + "\n" + addressLine + "\n"
									+ stringLatitude + ", " + stringLongitude)
					.position(myCoord));

			map.moveCamera(CameraUpdateFactory.newLatLngZoom(myCoord, 8));

			if (isNetworkAvailable()) {
				try {
					map.addPolyline(rectLineDist);
				} catch (Exception ex) {
				}
			} else {
				Toast toast = Toast.makeText(
						getApplicationContext(),
						getString(getResources().getIdentifier(
								"no_inet_string" + prefix, "string",
								getPackageName())), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}

		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		prefix = prefs.getString("prefix", "");

		if (isNetworkAvailable()) {
			if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
				setContentView(R.layout.activity_rout);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				AsyncMaps maps = new AsyncMaps();
				maps.execute();
				if (Build.VERSION.SDK_INT >= 15) {
					ActionBar bar = getActionBar();
					bar.setDisplayHomeAsUpEnabled(true);
				}
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
		} else {
			Toast toast = Toast.makeText(
					getApplicationContext(),
					getString(getResources().getIdentifier(
							"no_inet_string" + prefix, "string",
							getPackageName())), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		enableGpsModal();
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_type_drive, menu);
		menu.add(
				0,
				NEW_MENU_ID,
				0,
				getString(getResources().getIdentifier("change_view" + prefix,
						"string", getPackageName())));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AsyncMaps maps = new AsyncMaps();
		switch (item.getItemId()) {
		case R.id.action_drive:
			if (isNetworkAvailable()) {
				maps.execute("driving");
			} else {
				Toast toast = Toast.makeText(
						getApplicationContext(),
						getString(getResources().getIdentifier(
								"no_inet_string" + prefix, "string",
								getPackageName())), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			return true;
		case R.id.action_walk:
			if (isNetworkAvailable()) {
				maps.execute("walking");
			} else {
				Toast toast = Toast.makeText(
						getApplicationContext(),
						getString(getResources().getIdentifier(
								"no_inet_string" + prefix, "string",
								getPackageName())), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			return true;
		case android.R.id.home:
			Intent intent = new Intent(this, StartActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case NEW_MENU_ID: {
			if (isNetworkAvailable()) {
				if (map.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
					map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				else if (map.getMapType() == GoogleMap.MAP_TYPE_HYBRID)
					map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			} else {
				Toast toast = Toast.makeText(
						getApplicationContext(),
						getString(getResources().getIdentifier(
								"no_inet_string" + prefix, "string",
								getPackageName())), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			return true;
		}
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
			View rootView = inflater.inflate(R.layout.fragment_rout, container,
					false);
			return rootView;
		}
	}

	private void enableGpsModal() {
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean statusOfGPS = manager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (statusOfGPS != true) {
			AlertDialog dialog = new AlertDialog.Builder(this)
					.setMessage(
							getString(getResources().getIdentifier(
									"turn_gps" + prefix, "string",
									getPackageName())))
					.setCancelable(false)
					.setPositiveButton(android.R.string.ok,
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
									boolean statusOfGPS = manager
											.isProviderEnabled(LocationManager.GPS_PROVIDER);
									if (statusOfGPS == true) {
										dialog.dismiss();
									} else {
										enableGpsModal();
									}
								}
							}).create();
			dialog.show();
		}
	}
}
