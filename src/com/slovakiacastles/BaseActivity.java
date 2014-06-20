package com.slovakiacastles;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.LocationManager;

public class BaseActivity extends Activity {
	protected void enableGpsModal(final String sPrefix) {
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean statusOfGPS = manager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (statusOfGPS != true) {
			AlertDialog dialog = new AlertDialog.Builder(this)
					.setMessage(
							getString(getResources().getIdentifier(
									"turn_gps" + sPrefix, "string",
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
										enableGpsModal(sPrefix);
									}
								}
							}).create();
			dialog.show();
		}
	}
}
