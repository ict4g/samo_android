package eu.fbk.ict4g.samo.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.models.Campaign;
import eu.fbk.ict4g.samo.models.Indicator;
import eu.fbk.ict4g.samo.models.Target;

public class CampaignDetailsOld extends ExpandableListActivity {

	SamoDbDataSource dataSource;
	List<Target> targets;
	List<Indicator> indicators;
	Campaign campaign;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.campaign_details);
		ExpandableListView listView = getExpandableListView();

		dataSource = new SamoDbDataSource(this); 
		dataSource.open();       

		campaign = SAMoApp.getCurrentCampaign();
		targets = dataSource.getAllTargets();
		indicators = dataSource.getAllIndicators();
		TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
		TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		TextView dateFromTextView = (TextView) findViewById(R.id.dateFromTextView);
		TextView dateToTextView = (TextView) findViewById(R.id.dateToTextView);
		nameTextView.setText(campaign.getTitle());
		descriptionTextView.setText(campaign.getDescription());
		dateFromTextView.setText(campaign.getDateFrom());
		dateToTextView.setText(campaign.getDateTo());
		ExpandableListAdapter adapter = new SimpleExpandableListAdapter(this, 

				createGroupList(), 				// Group List
				R.layout.group_row, 			// Group item Layout XML
				new String[] {"group item"}, 	// the key of group item.
				new int[] {R.id.row_name}, 		// ID of each group item.-Data under the key goes into this TextView.

				createChildList(), 				// childData describes second-level entries.
				R.layout.child_row,				// Layout for sub-level entries(second level).
				new String[] {"sub item"},		// Keys in childData maps to display.	
				new int[] {R.id.grp_child}		// Data under the keys above go into these TextViews.

				);
		listView.setAdapter(adapter);
	}

	private List<HashMap<String, String>> createGroupList() {  
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
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

	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.dumpDbButton:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.about_to_dump_db));
			builder.setMessage(R.string.are_you_sure)
			.setCancelable(false)
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String path = dataSource.dumpDatabase();
					if (path != null)
						Toast.makeText(CampaignDetailsOld.this, getString(R.string.toast_db_saved_in) + path, Toast.LENGTH_LONG).show();
					else
						Toast.makeText(CampaignDetailsOld.this, getString(R.string.toast_error_db_saved) + path, Toast.LENGTH_LONG).show();
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

	public class ExpandableListViewAdapter extends BaseExpandableListAdapter
	{

		private final class ViewHolder
		{
			TextView textLabel;

		}

		private final Map.Entry<Object, List<Object>> entries[];

		private final LayoutInflater inflater;

		@SuppressWarnings("unchecked")
		public ExpandableListViewAdapter(final Context theContext, final Map<Object, List<Object>> theMap)
		{
			inflater = LayoutInflater.from(theContext);

			entries = theMap.entrySet().toArray(new Map.Entry[0]);

		}

		@Override
		public Object getChild(final int groupPosition, final int childPosition)
		{
			final List<Object> childList = entries[groupPosition].getValue();

			return childList.get(childPosition);
		}

		@Override
		public long getChildId(final int groupPosition, final int childPosition)
		{
			return childPosition;
		}

		@Override
		public int getChildrenCount(final int groupPosition)
		{

			final List<Object> childList = entries[groupPosition].getValue();

			final int childCount = childList.size();

			return childCount;
		}

		@Override
		public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild,
				final View theConvertView, final ViewGroup parent)
		{
			View resultView = theConvertView;

			ViewHolder holder = null;

			if (resultView == null)
			{
				resultView = inflater.inflate(R.layout.target_list_item, null);

				holder = new ViewHolder();

				holder.textLabel = (TextView) resultView.findViewById(R.id.titleTextView);

				resultView.setTag(holder);
			} else
			{
				holder = (ViewHolder) resultView.getTag();
			}

			final Object item = getChild(groupPosition, childPosition);

			holder.textLabel.setText(item.toString());

			return resultView;
		}

		@Override
		public Object getGroup(final int groupPosition)
		{
			return entries[groupPosition].getKey();
		}

		@Override
		public int getGroupCount()
		{
			final int groupCount = entries.length;

			return groupCount;
		}

		@Override
		public long getGroupId(final int groupPosition)
		{
			return groupPosition;
		}

		@Override
		public View getGroupView(final int groupPosition, final boolean isExpanded, final View theConvertView,
				final ViewGroup parent)
		{
			View resultView = theConvertView;

			ViewHolder holder = null;

			if (resultView == null)
			{
				resultView = inflater.inflate(R.layout.group_row, null);

				holder = new ViewHolder();
				holder.textLabel = (TextView) resultView.findViewById(R.id.row_name);

				resultView.setTag(holder);
			} else
			{
				holder = (ViewHolder) resultView.getTag();
			}

			final Object item = getGroup(groupPosition);

			holder.textLabel.setText(item.toString());

			return resultView;
		}

		@Override
		public boolean hasStableIds()
		{
			return true;
		}

		@Override
		public boolean isChildSelectable(final int groupPosition, final int childPosition)
		{
			return true;
		}

	}
}
