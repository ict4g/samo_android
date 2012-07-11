package eu.fbk.ict4g.samo.fragments;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import eu.fbk.ict4g.samo.activities.R;
import eu.fbk.ict4g.samo.activities.TargetDetails;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.models.Target;

public class TargetsList extends ListFragment {

	ArrayList<Target> targets;
	ArrayAdapter<Target> adapter;
	SamoDbDataSource dataSource;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		dataSource = new SamoDbDataSource(getActivity()); 
		dataSource.open();  
		targets = (ArrayList<Target>) dataSource.getAllTargets();
		adapter = new TargetAdapter(getActivity(), R.layout.target_list_item, targets);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), TargetDetails.class);
		intent.putExtra(getString(R.string.target), targets.get(position));
		startActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		targets = (ArrayList<Target>) dataSource.getAllTargets();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		dataSource.close();
		super.onDestroy();
	}

	private class TargetAdapter extends ArrayAdapter<Target> {
	
		private LayoutInflater mInflater;
		private ArrayList<Target> items;
		private int layoutId;
	
		public TargetAdapter(Context context, int textViewResourceId,
				ArrayList<Target> objects) {
			super(context, textViewResourceId);
			this.mInflater = LayoutInflater.from(context);
			this.items = objects;
			this.layoutId = textViewResourceId;
		}
	
		@Override
		public int getCount() {
			return items.size();
		}
	
	
		@Override
		public Target getItem(int position) {
			return items.get(position);
		}
	
		@Override
		public long getItemId(int position) {
			return position;
		}
	
		private class ViewHolder {
			TextView nameTextView;
			TextView categoryTextView;
			TextView streetTextView;
			TextView addressTextView;
		}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			final Target cTarget = items.get(position);
			if (convertView == null) {
				convertView = mInflater.inflate(layoutId, null);
	
				// Creates a ViewHolder and store references to the two children views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
				holder.categoryTextView = (TextView) convertView.findViewById(R.id.categoryTextView);
				holder.streetTextView = (TextView) convertView.findViewById(R.id.streetTextView);
				holder.addressTextView = (TextView) convertView.findViewById(R.id.addressTextView);
	
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
	
			holder.nameTextView.setText(cTarget.getName());
			holder.categoryTextView.setText(""); // TODO Category of Target
			holder.streetTextView.setText(cTarget.getStreet());
			holder.addressTextView.setText(getAddressFromTarget(cTarget));
	
			return convertView;
		}
	
		private String getAddressFromTarget(Target target) {
			
			StringBuilder sb =  new StringBuilder();
			sb.append(target.getZip() != null ? target.getZip() + " " : "");
			sb.append(target.getCity() != null ? target.getCity() + " " : "");
			sb.append(target.getState() != null ? target.getState() + " " : "");
			sb.append(target.getCountry() != null ? target.getCountry() : "");
			
			return sb.toString();
		}
	}

}
