package eu.fbk.ict4g.samo.activities;

import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.models.Target;
import eu.fbk.ict4g.samo.utils.SAMoLog;

public class TargetDetails extends Activity {

	Target mTarget;

	TextView latTextView, updateLatTextView;
	TextView lngTextView, updateLngTextView;
	TextView updateAccuracyTextView, updateNumSatTextView, updateAltitudeTextView, updateNumFixesTextView;
	String provider;
	Chronometer chronometer;
	ProgressBar progressBar;
	Button startGPSButton, stopGPSButton, saveCoordsButton;

	GPSTask gpsTask;
	Location lastLocation;

	private final static String GPS_RUNNING = "GPS_RUNNING";
	private final static String CHRONOMETER_BASE = "CHRONOMETER_BASE";
	private static final String NUM_FIXES = "NUM_FIXES";
	private static final String TAG = TargetDetails.class.getSimpleName();
	private static final int ONE_MINUTE = 60*1000; 

	boolean gpsRunning;
	int numFixes, numSats;
	long mChronometerBase;

	private static LocationManager locationManager;
	LocationListener locationListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.target_details);
		TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
		TextView streetTextView = (TextView) findViewById(R.id.streetTextView);
		TextView zipCityTextView = (TextView) findViewById(R.id.zipCityTextView);
		TextView stateTextView = (TextView) findViewById(R.id.stateTextView);
		TextView countryTextView = (TextView) findViewById(R.id.countryTextView);
		latTextView = (TextView) findViewById(R.id.latTextView);
		lngTextView = (TextView) findViewById(R.id.lngTextView);
		updateLatTextView = (TextView) findViewById(R.id.updateLatTextView);
		updateLngTextView = (TextView) findViewById(R.id.updateLngTextView);
		updateAccuracyTextView = (TextView) findViewById(R.id.updateAccuracyTextView);
		updateNumSatTextView = (TextView) findViewById(R.id.updateNumSatTextView);
		updateAltitudeTextView = (TextView) findViewById(R.id.updateAltitudeTextView);
		updateNumFixesTextView = (TextView) findViewById(R.id.updateNumFixesTextView);

		startGPSButton = (Button) findViewById(R.id.startGPSButton);
		stopGPSButton = (Button) findViewById(R.id.stopGPSButton);
		saveCoordsButton = (Button) findViewById(R.id.saveCoordsButton);

		chronometer = (Chronometer) findViewById(R.id.chronometer);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		if (savedInstanceState != null) {
			gpsRunning = savedInstanceState.getBoolean(GPS_RUNNING);
			SAMoLog.d(TAG, "numFixes = " + numFixes);
			saveCoordsButton.setEnabled(numFixes > 0 && !gpsRunning); // enabled when there has been at least one fix and gps is not running
			mChronometerBase = savedInstanceState.getLong(CHRONOMETER_BASE);
			numFixes = savedInstanceState.getInt(NUM_FIXES);
		} else {
			gpsRunning = false;
			mChronometerBase = SystemClock.elapsedRealtime();
			numFixes = -1;
		}

		mTarget = getIntent().getParcelableExtra(getString(R.string.target));
		if (mTarget != null) {
			nameTextView.setText(mTarget.getName());
			streetTextView.setText(mTarget.getStreet());
			zipCityTextView.setText(mTarget.getName());
			stateTextView.setText(mTarget.getState());
			countryTextView.setText(mTarget.getCountry());
			latTextView.setText(mTarget.getLat() + "");
			lngTextView.setText(mTarget.getLng() + "");
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Location stuff 
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


		Criteria criteria = new Criteria(); 
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false); 
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false); 
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		//		provider = locationManager.getBestProvider(criteria, true);
		provider = LocationManager.GPS_PROVIDER;
		if (provider != null) {
			Location lastLocation = locationManager.getLastKnownLocation(provider);
			long now = Calendar.getInstance().getTimeInMillis();
			if (lastLocation != null && now - lastLocation.getTime() < ONE_MINUTE && numFixes != -1) {
				numFixes--; // this is not a real GPS fix!
				updateWithNewLocation(lastLocation); // call this only for updating the fields
			}

			locationListener = new LocationListener() { 

				public void onLocationChanged(Location location) {
					updateWithNewLocation(location); 
				}

				public void onProviderDisabled(String provider) {
					updateWithNewLocation(null);
				}

				public void onProviderEnabled(String provider) { 
					Toast.makeText(TargetDetails.this, provider + getString(R.string._enabled), Toast.LENGTH_SHORT).show();
				}

				public void onStatusChanged(String provider, int status, Bundle extras) { }
			};

		}

	}

	protected void updateWithNewLocation(Location location) {
		if (location != null) {
			SAMoLog.w(this.getClass().getSimpleName(), "got new location!");
			numFixes++;
			updateLatTextView.setText("" + location.getLatitude());
			updateLngTextView.setText("" + location.getLongitude());
			if (location.hasAccuracy()) updateAccuracyTextView.setText("" + location.getAccuracy());
			if (location.hasAltitude()) updateAltitudeTextView.setText("" + location.getAltitude());
			updateNumFixesTextView.setText("" + numFixes);
			lastLocation = location;
		}
		else {
			SAMoLog.w(this.getClass().getSimpleName(), "location is null!");
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (gpsRunning) startGPS(mChronometerBase);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(GPS_RUNNING, gpsRunning);
		outState.putLong(CHRONOMETER_BASE, chronometer.getBase());
		if (numFixes != -1) outState.putInt(NUM_FIXES, numFixes);

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (gpsRunning) stopGPS();
		locationListener = null;
	}

	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.startGPSButton:
			numFixes = 0;
			numSats = 0;
			startGPS(SystemClock.elapsedRealtime());
			saveCoordsButton.setEnabled(false);
			break;

		case R.id.stopGPSButton:
			stopGPS();
			saveCoordsButton.setEnabled(numFixes > 0); // enabled if there has been at least one fix
			break;

		case R.id.saveCoordsButton:
			Toast.makeText(this, "saving coords", Toast.LENGTH_SHORT).show();
			mTarget.setLat(lastLocation.getLatitude());
			mTarget.setLng(lastLocation.getLongitude());
			new UpdateCoordsTask(this).execute();
			break;

		default:
			break;
		}
	}

	private void startGPS(long chronometerBase) {
		chronometer.setBase(chronometerBase);
		chronometer.start();
		progressBar.setVisibility(View.VISIBLE);
		startGPSButton.setEnabled(false);
		stopGPSButton.setEnabled(true);
		locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
		//gpsTask = (GPSTask) new GPSTask(this).execute();

		gpsRunning = true;

	}

	private void stopGPS() {
		chronometer.stop();
		progressBar.setVisibility(View.INVISIBLE);
		startGPSButton.setEnabled(true);
		stopGPSButton.setEnabled(false);
		locationManager.removeUpdates(locationListener);
		//gpsTask.cancel(true);
		gpsRunning = false;

	}

	protected class GPSTask extends AsyncTask<Long, Void, Boolean> implements GpsStatus.Listener {

		Context mContext;

		public GPSTask(Context mContext) {
			this.mContext = mContext;
		}

		@Override
		protected void onPreExecute() {
			SAMoLog.w(TAG, "GPSTask started");
			locationManager.addGpsStatusListener(this);
		}

		@Override
		protected Boolean doInBackground(Long ... params) {
			SAMoLog.d(TAG, "sleeping ...");
			while (!isCancelled())
				SystemClock.sleep(500);
			return null;
		}

		@Override
		protected void onCancelled(Boolean result) {
			SAMoLog.w(TAG, "GPSTask stopped");
			locationManager.removeGpsStatusListener(this);
		}

		@Override
		protected void onCancelled() {
			SAMoLog.w(TAG, "GPSTask stopped");
			locationManager.removeGpsStatusListener(this);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			SAMoLog.w(TAG, "GPSTask stopped");
			locationManager.removeGpsStatusListener(this);
		}

		@Override
		public void onGpsStatusChanged(int event) {
			switch (event) {

			case GpsStatus.GPS_EVENT_FIRST_FIX:
				SAMoLog.w(TAG, "First Fix!");
				break;

			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				GpsStatus gpsStatus = locationManager.getGpsStatus(null);
				updateWithNewGpsStatus(gpsStatus);						
				break;

			default:
				break;
			}
		}

		protected void updateWithNewGpsStatus(GpsStatus gpsStatus) {
			if (gpsStatus != null) {
				SAMoLog.w(TAG, "got new gpsStatus: " + gpsStatus.getTimeToFirstFix());
				while (gpsStatus.getSatellites().iterator().hasNext()) {
					GpsSatellite sat = gpsStatus.getSatellites().iterator().next();
					SAMoLog.d(TAG, sat.getPrn() + "");
					if (sat.usedInFix())
						numSats++;
				}
				updateNumSatTextView.setText("" + numSats);
			} else {
				SAMoLog.w(TAG, "gpsStatus is null!");
			}
		}

	}

	protected class UpdateCoordsTask extends AsyncTask<Void, Void, Boolean> {

		Context mContext;
		ProgressDialog dialog;

		public UpdateCoordsTask(Context mContext) {
			this.mContext = mContext;
			dialog = new ProgressDialog(mContext);
			dialog.setTitle(getString(R.string.updating_coords));
		}

		@Override
		protected void onPreExecute() {
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SamoDbDataSource dataSource = new SamoDbDataSource(mContext);
				dataSource.open();
				dataSource.updateTargetLocation(mTarget);
				dataSource.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (dialog.isShowing()) dialog.dismiss();
			if (result) {
				Toast.makeText(mContext, R.string.toast_coords_updated, Toast.LENGTH_SHORT).show();
				latTextView.setText(mTarget.getLat() + "");
				lngTextView.setText(mTarget.getLng() + "");
			} else 
				Toast.makeText(mContext, R.string.toast_error_db_populated, Toast.LENGTH_SHORT).show();
		}

	}
}
