package eu.fbk.ict4g.samo.activities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.models.Indicator;

public class IndicatorsActivity extends Activity {
    
	SamoDbDataSource dataSource;
	ArrayAdapter<Indicator> adapter;
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indicators);
        ListView listView = (ListView) findViewById(R.id.listView);
        
        dataSource = new SamoDbDataSource(this); 
		dataSource.open();       
        
        List<Indicator> indicators = dataSource.getAllIndicators();
		Log.d(this.getClass().getSimpleName(), indicators.toString());
        
        adapter =  new ArrayAdapter<Indicator>(this, android.R.layout.simple_list_item_1, indicators);
        listView.setAdapter(adapter);
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
		Indicator indicator = null;
		switch (view.getId()) {
		case R.id.populateButton:
			for (int i = 0; i < 5; i++) {
				indicator = new Indicator();
				indicator.setName("Indicator_n" + i);
				dataSource.createIndicator(indicator);
				adapter.add(indicator);				
			}
			break;
			
		case R.id.deleteAllButton:
			dataSource.deleteAllIndicators();
			adapter.clear();
			break;
		
		}
		adapter.notifyDataSetChanged();
	}

}
