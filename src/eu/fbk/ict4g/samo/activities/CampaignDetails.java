package eu.fbk.ict4g.samo.activities;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.fragments.IndicatorsList;
import eu.fbk.ict4g.samo.fragments.TargetsList;
import eu.fbk.ict4g.samo.models.Campaign;
import eu.fbk.ict4g.samo.models.Indicator;
import eu.fbk.ict4g.samo.models.Target;

public class CampaignDetails extends Activity {

	SamoDbDataSource dataSource;
	List<Target> targets;
	List<Indicator> indicators;
	Campaign campaign;

	private static final int TARGETS_STATE = 0x1;
	private static final int INDICATORS_STATE = 0x2;

	private int mTabState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campaign_details_frags);

		dataSource = new SamoDbDataSource(this);       

		campaign = SAMoApp.getCurrentCampaign();
		TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
		TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		TextView dateFromTextView = (TextView) findViewById(R.id.dateFromTextView);
		TextView dateToTextView = (TextView) findViewById(R.id.dateToTextView);
		nameTextView.setText(campaign.getTitle());
		descriptionTextView.setText(campaign.getDescription());
		dateFromTextView.setText(campaign.getDateFrom());
		dateToTextView.setText(campaign.getDateTo());
		showTargets(null);
	}

	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.dumpDbButton:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.about_to_dump_db));
			builder.setMessage(R.string.are_you_sure)
			.setCancelable(false)
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dataSource.open();  
					String path = dataSource.dumpDatabase();
					if (path != null)
						Toast.makeText(CampaignDetails.this, getString(R.string.toast_db_saved_in) + path, Toast.LENGTH_LONG).show();
					else
						Toast.makeText(CampaignDetails.this, getString(R.string.toast_error_db_saved) + path, Toast.LENGTH_LONG).show();
					dataSource.close();
				}
			})
			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();

			break;
		}
	}

	public void showTargets(View view) {
		if (mTabState != TARGETS_STATE) {

			mTabState = TARGETS_STATE;
			FragmentManager fm = getFragmentManager();

			TargetsList targetsFragment = new TargetsList();
			if (fm != null) {
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.fragment_content, targetsFragment);
				ft.commit();
			}
		}
	}

	public void showIndicators(View view) {
		if (mTabState != INDICATORS_STATE) {

			mTabState = INDICATORS_STATE;
			FragmentManager fm = getFragmentManager();

			IndicatorsList targetsFragment = new IndicatorsList();
			if (fm != null) {
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.fragment_content, targetsFragment);
				ft.commit();
			}
		}
	}
}
