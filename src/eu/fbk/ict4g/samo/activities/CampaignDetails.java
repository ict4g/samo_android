package eu.fbk.ict4g.samo.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.models.Indicator;
import eu.fbk.ict4g.samo.models.Target;

public class CampaignDetails extends ExpandableListActivity {

	SamoDbDataSource dataSource;
	List<Target> targets;
	List<Indicator> indicators;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campaign_details);
		ExpandableListView listView = getExpandableListView();

		dataSource = new SamoDbDataSource(this); 
		dataSource.open();       

		targets = dataSource.getAllTargets();
		indicators = dataSource.getAllIndicators();
		ExpandableListAdapter adapter = new SimpleExpandableListAdapter(this, 
				createGroupList(), 
				R.layout.group_row, 
				new String[] {"group item"}, 
				new int[] {R.id.row_name}, 
				createChildList(), 
				R.layout.child_row,
				new String[] {"sub item"},
				new int[] {R.id.grp_child}
				);
		listView.setAdapter(adapter);
	}

	private List<HashMap<String, String>> createGroupList() {  
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> targetsHashMap = new HashMap<String, String>();
		targetsHashMap.put("group item", getString(R.string.targets));
		HashMap<String, String> indicatorsHashMap = new HashMap<String, String>();
		indicatorsHashMap.put("group item", getString(R.string.indicators));
		result.add(targetsHashMap);
		result.add(indicatorsHashMap);
		return result;
	}

	private List<List<HashMap<String, String>>> createChildList() {  
		ArrayList<List<HashMap<String, String>>> result = new ArrayList<List<HashMap<String, String>>>(); 

		ArrayList<HashMap<String, String>> targetsList = new ArrayList<HashMap<String, String>>();
		for (Target target : targets) {
			HashMap<String, String> child = new HashMap<String, String>();
			child.put("sub item", target.getName());
			targetsList.add( child );
		}

		ArrayList<HashMap<String, String>> indicatorsList = new ArrayList<HashMap<String, String>>();
		for (Indicator indicator : indicators) {
			HashMap<String, String> child = new HashMap<String, String>();
			child.put("sub item", indicator.getName());
			indicatorsList.add(child);
		}

		result.add(targetsList);
		result.add(indicatorsList);
		return result;
	}

	@Override
	protected void onPause() {
		dataSource.close();
		super.onPause();
	}

	@Override
	protected void onResume() {
		dataSource.open();
		super.onResume();
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(state);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

}
