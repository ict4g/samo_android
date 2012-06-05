package eu.fbk.ict4g.samo.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.models.Campaign;
import eu.fbk.ict4g.samo.models.Indicator;
import eu.fbk.ict4g.samo.models.Target;
import eu.fbk.ict4g.samo.service.SamoServiceException;

public class CampaignsActivity extends Activity {

	SamoDbDataSource dataSource;

	List<Campaign> campaigns;
	
	ArrayAdapter<Campaign> campaignsAdapter;
	
	Campaign selectedCampaign;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campaigns);
		ListView listView = (ListView) findViewById(R.id.listView);
		
		dataSource = new SamoDbDataSource(this);
		dataSource.open();
		
		Log.d(this.getClass().getSimpleName(), dataSource.getAllIndicators().toString());
		dataSource.printColumnsOfAssessmentsTable();

		campaigns = new ArrayList<Campaign>();

		campaignsAdapter = new ArrayAdapter<Campaign>(this, android.R.layout.simple_list_item_1, campaigns);
		listView.setAdapter(campaignsAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				selectedCampaign = campaigns.get(position);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(CampaignsActivity.this);
				builder.setTitle(getString(R.string.about_to_use) + selectedCampaign.getDescription());
				builder.setMessage(R.string.are_you_sure)
				.setCancelable(false)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						new PopulateDBTask(CampaignsActivity.this).execute();
					}
				})
				.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
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

	private void populateDB(Campaign campaign) {

		// Ripulisco la tabella
//		dataSource.deleteAllIndicatorColumns();
		dataSource.resetAll();
		dataSource.printColumnsOfAssessmentsTable();

		for (Indicator indicator : campaign.getIndicators()) {
			Log.d(this.getClass().getSimpleName(), "adding indicator column");
			dataSource.addIndicatorColumn(indicator);
			Log.d(this.getClass().getSimpleName(), "adding indicator '" + indicator.getName() + "' with id: " + indicator.getId());
			dataSource.createIndicator(indicator);			
		}
		Log.d(this.getClass().getSimpleName(), dataSource.getAllIndicators().toString());

		Log.d(this.getClass().getSimpleName(), "adding targets");
		for (Target target : campaign.getTargets()) {
			Log.d("target", target.getName());
			dataSource.createTarget(target.getName());
		}
		Log.d(this.getClass().getSimpleName(), dataSource.getAllTargets().toString());

		finish();
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.downloadButton:
			new CampaignsTask(this).execute();
			break;
		case R.id.logDbButton:
			Log.d(this.getClass().getSimpleName(), dataSource.getAllIndicators().toString());
			Log.d(this.getClass().getSimpleName(), dataSource.getAllTargets().toString());
			break;

		case R.id.deleteAllButton:
			dataSource.deleteAllTargets();
			dataSource.deleteAllIndicators();
			dataSource.deleteAllAssessments();
			campaignsAdapter.clear();
			break;

		}
	}

	private class CampaignsTask extends AsyncTask<Void, Void, Boolean> {

		ProgressDialog dialog;
		Context mContext;
		ArrayList<Campaign> tmpCampaigns;

		/**
		 * 
		 */
		public CampaignsTask(Context context) {
			this.mContext = context;
			dialog = new ProgressDialog(mContext);
			dialog.setTitle(getString(R.string.loading));
		}

		@Override
		protected void onPreExecute() {
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				tmpCampaigns = (ArrayList<Campaign>) SAMoApp.getService().getAllCampaigns();
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
				campaigns.clear();
				for (Campaign campaign : tmpCampaigns) {
					campaigns.add(campaign);
				}
				campaignsAdapter.notifyDataSetChanged();
			}
		}

	}

	private class PopulateDBTask extends AsyncTask<Void, Void, Boolean> {
	
		ProgressDialog dialog;
		Context mContext;
	
		/**
		 * 
		 */
		public PopulateDBTask(Context context) {
			this.mContext = context;
			dialog = new ProgressDialog(mContext);
			dialog.setTitle(getString(R.string.populating_db));
		}
	
		@Override
		protected void onPreExecute() {
			dialog.show();
		}
	
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				populateDB(selectedCampaign);
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
				Toast.makeText(mContext, "DB Populated with selected campaign", Toast.LENGTH_SHORT).show();
			}
		}
	
	}

}
