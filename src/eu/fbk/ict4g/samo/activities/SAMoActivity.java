package eu.fbk.ict4g.samo.activities;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.db.SamoDbHelper;
import eu.fbk.ict4g.samo.models.Campaign;
import eu.fbk.ict4g.samo.service.SamoServiceException;

public class SAMoActivity extends Activity {
	
	SamoDbDataSource dataSource;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		dataSource = new SamoDbDataSource(this);
		dataSource.open(); 
		// retrieve a campaign from the DB and set it in the SAMoApp
		List<Campaign> campaigns = dataSource.getAllCampaigns();
		if (!campaigns.isEmpty()) 
			SAMoApp.setCurrentCampaign(campaigns.get(0));
		SAMoApp.setDatabasePath(getDatabasePath(SamoDbHelper.DATABASE_NAME));
	}

	@Override
	protected void onResume() {
		dataSource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		dataSource.close();
		super.onPause();
	}

	public void onClick(View view) {
		Intent intent;
		switch (view.getId()) {

		case R.id.newAssessmentButton:
			intent = new Intent(this, NewAssessmentActivity.class);
			startActivity(intent);
			break;

		case R.id.viewAssButton:
			intent = new Intent(this, AssessmentListActivity.class);
			startActivity(intent);
			break;

		case R.id.campaignsButton:
			intent = new Intent(this, CampaignsActivity.class);
			startActivity(intent);
			break;

		case R.id.publishButton:
			new PublishTask(this).execute();
			break;

		case R.id.loginButton:
			new LoginTask(this).execute();
			break;

		case R.id.logoutButton:
			new LogoutTask(this).execute();
			break;

		case R.id.campaignDetailsButton:
			intent = new Intent(this, CampaignDetails.class);
			startActivity(intent);
			break;
			
		}
	}

	private class PublishTask extends AsyncTask<Void, Void, Boolean> {

		ProgressDialog dialog;
		Context mContext;

		/**
		 * 
		 */
		 public PublishTask(Context context) {
			 this.mContext = context;
			 dialog = new ProgressDialog(mContext);
			 dialog.setTitle("Loading");
		 }

		 @Override
		 protected void onPreExecute() {
			 dialog.show();
		 }

		 @Override
		 protected Boolean doInBackground(Void... params) {
			 try {
				 SAMoApp.getService().publishAssessment(null);
				 //				SAMoApp.getService().publishAllAssessments();
				 return true;
			 } catch (SamoServiceException e) {
				 e.printStackTrace();
				 return false;
			 }
		 }

		 @Override
		 protected void onPostExecute(Boolean result) {
			 if (dialog.isShowing()) dialog.dismiss();
			 if (result) {

			 }
		 }

	}

	private class LoginTask extends AsyncTask<Void, Void, Boolean> {

		ProgressDialog dialog;
		Context mContext;

		/**
		 * 
		 */
		public LoginTask(Context context) {
			this.mContext = context;
			dialog = new ProgressDialog(mContext);
			dialog.setTitle("Loading");
		}

		@Override
		protected void onPreExecute() {
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SAMoApp.getService().login("pbmolini@fbk.eu", "12345");
				return true;
			} catch (SamoServiceException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (dialog.isShowing()) dialog.dismiss();
			if (result) {

			}
		}

	}

	private class LogoutTask extends AsyncTask<Void, Void, Boolean> {
	
		ProgressDialog dialog;
		Context mContext;
	
		/**
		 * 
		 */
		public LogoutTask(Context context) {
			this.mContext = context;
			dialog = new ProgressDialog(mContext);
			dialog.setTitle("Loading");
		}
	
		@Override
		protected void onPreExecute() {
			dialog.show();
		}
	
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				SAMoApp.getService().logout();
				return true;
			} catch (SamoServiceException e) {
				e.printStackTrace();
				return false;
			}
		}
	
		@Override
		protected void onPostExecute(Boolean result) {
			if (dialog.isShowing()) dialog.dismiss();
			if (result) {
	
			}
		}
	
	}
}