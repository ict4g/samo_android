package eu.fbk.ict4g.samo.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.db.SamoDbHelper;
import eu.fbk.ict4g.samo.models.Campaign;

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

		case R.id.campaignDetailsButton:
			intent = new Intent(this, CampaignDetails.class);
			startActivity(intent);
			break;
			
		}
	}
}