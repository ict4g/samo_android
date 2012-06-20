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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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

//		campaignsAdapter = new ArrayAdapter<Campaign>(this, android.R.layout.simple_list_item_1, campaigns);
		campaignsAdapter =  new CampaignAdapter(this, R.layout.campaign_list_item, campaigns);
		listView.setAdapter(campaignsAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				selectedCampaign = campaigns.get(position);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(CampaignsActivity.this);
				builder.setTitle(getString(R.string.about_to_use) + selectedCampaign.getTitle());
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

		// Populate tables
		// campaign
		// add an entry to the campaign table
		Log.d("campaign", campaign.getTitle());
		dataSource.createCampaign(campaign);
		SAMoApp.setCurrentCampaign(campaign);
		
		// assessment and indicator
		for (Indicator indicator : campaign.getIndicators()) {
			// add a column to the assessment table
			Log.d(this.getClass().getSimpleName(), "adding indicator column");
			dataSource.addIndicatorColumn(indicator);
			
			// add an entry to the indicator table
			Log.d(this.getClass().getSimpleName(), "adding indicator '" + indicator.getName() + "' with id: " + indicator.getId());
			dataSource.createIndicator(indicator);			
		}
		Log.d(this.getClass().getSimpleName(), dataSource.getAllIndicators().toString());

		// target
		Log.d(this.getClass().getSimpleName(), "adding targets");
		for (Target target : campaign.getTargets()) {
			// add an entry to the target table
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

		case R.id.deleteAllButton:
			dataSource.deleteAllTargets();
			dataSource.deleteAllIndicators();
			dataSource.deleteAllAssessments();
			campaignsAdapter.clear();
			break;

		}
	}
	
	private class CampaignAdapter extends ArrayAdapter<Campaign> {

		public CampaignAdapter(Context context, int textViewResourceId,
				List<Campaign> objects) {
			super(context, textViewResourceId, objects);
			
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.campaign_list_item, null);
			Campaign campaign = campaigns.get(position);
			TextView titleTextView = (TextView) v.findViewById(R.id.titleTextView);
			TextView descriptionTextView = (TextView) v.findViewById(R.id.descriptionTextView);
			TextView dateFromTextView = (TextView) v.findViewById(R.id.dateFromTextView);
			TextView dateToTextView = (TextView) v.findViewById(R.id.dateToTextView);
			
			titleTextView.setText(campaign.getTitle());
			descriptionTextView.setText(campaign.getDescription());
			dateFromTextView.setText(campaign.getDateFrom());
			dateToTextView.setText(campaign.getDateTo());
			
			return v;
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
			} else
				Toast.makeText(mContext, R.string.toast_error_cannot_connect, Toast.LENGTH_SHORT).show();
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
