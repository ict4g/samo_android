package eu.fbk.ict4g.samo.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import eu.fbk.ict4g.samo.service.SamoServiceException;

public class SAMoActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onClick(View view) {
		Intent intent;
		switch (view.getId()) {
		case R.id.indicatorsButton:
			intent = new Intent(this, IndicatorsActivity.class);
			startActivity(intent);
			break;

		case R.id.targetsButton:
			intent = new Intent(this, TargetsActivity.class);
			startActivity(intent);
			break;

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
				SAMoApp.getService().login("manager@gmail.com", "12345");
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