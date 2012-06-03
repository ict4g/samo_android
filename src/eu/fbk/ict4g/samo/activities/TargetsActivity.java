package eu.fbk.ict4g.samo.activities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.models.Target;

public class TargetsActivity extends Activity {
    
	SamoDbDataSource dataSource;
	ArrayAdapter<Target> adapter;
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indicators);
        ListView listView = (ListView) findViewById(R.id.listView);
        
        dataSource = new SamoDbDataSource(this); 
		dataSource.open();       
        
        List<Target> targets = dataSource.getAllTargets();
        adapter =  new ArrayAdapter<Target>(this, android.R.layout.simple_list_item_1, targets);
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
		Target target = null;
		switch (view.getId()) {
		case R.id.populateButton:
			for (int i = 0; i < 3; i++) {
				target = dataSource.createTarget("Target " + i);
				adapter.add(target);				
			}
			break;
			
		case R.id.deleteAllButton:
			dataSource.deleteAllTargets();
			adapter.clear();
			break;
		
		}
		adapter.notifyDataSetChanged();
	}

}
