package eu.fbk.ict4g.samo.fragments;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import eu.fbk.ict4g.samo.activities.R;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.models.Indicator;

public class IndicatorsList extends ListFragment {

	ArrayList<Indicator> indicators;
	SamoDbDataSource dataSource;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		dataSource = new SamoDbDataSource(getActivity()); 
		dataSource.open();  
		indicators = (ArrayList<Indicator>) dataSource.getAllIndicators();
		ArrayAdapter<Indicator> adapter = new IndicatorAdapter(getActivity(), R.layout.indicator_list_item, indicators);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onDestroy() {
		dataSource.close();
		super.onDestroy();
	}

	private class IndicatorAdapter extends ArrayAdapter<Indicator> {

		private LayoutInflater mInflater;
		private ArrayList<Indicator> items;
		private int layoutId;

		public IndicatorAdapter(Context context, int textViewResourceId,
				ArrayList<Indicator> objects) {
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
		public Indicator getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			TextView nameTextView;
			TextView categoryTextView;
			TextView typeTextView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			final Indicator cIndicator = items.get(position);
			if (convertView == null) {
				convertView = mInflater.inflate(layoutId, null);

				// Creates a ViewHolder and store references to the two children views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
				holder.categoryTextView = (TextView) convertView.findViewById(R.id.categoryTextView);
				holder.typeTextView = (TextView) convertView.findViewById(R.id.typeTextView);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.nameTextView.setText(cIndicator.getName());
			holder.categoryTextView.setText(""); // TODO Category of Indicator
			holder.typeTextView.setText(cIndicator.getType());

			return convertView;
		}

	}

}
