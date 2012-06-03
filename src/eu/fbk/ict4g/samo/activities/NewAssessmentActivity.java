package eu.fbk.ict4g.samo.activities;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.models.Assessment;
import eu.fbk.ict4g.samo.models.Indicator;
import eu.fbk.ict4g.samo.models.Target;

public class NewAssessmentActivity extends Activity {

	SamoDbDataSource dataSource;
	
	ArrayAdapter<Target> targetAdapter;
	ArrayAdapter<Indicator> indicatorAdapter;
	Target selectedTarget;
	List<Indicator> indicators;
	List<Target> targets;
	
	EditText nameEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_assessment);

        ListView listView = (ListView) findViewById(R.id.indicatorsListView);
        Spinner spinner = (Spinner) findViewById(R.id.targetSpinner);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        
        dataSource = new SamoDbDataSource(this);
        dataSource.open();     
        
        targets = dataSource.getAllTargets();
        if (!targets.isEmpty()) selectedTarget = targets.get(0);
        
        targetAdapter =  new ArrayAdapter<Target>(this, android.R.layout.simple_spinner_dropdown_item, targets);
        spinner.setAdapter(targetAdapter);    
        
        indicators = dataSource.getAllIndicators();
        indicatorAdapter =  new ArrayAdapter<Indicator>(this, android.R.layout.simple_list_item_1, indicators);
        listView.setAdapter(indicatorAdapter); 
//        
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				// Create the alert for the pickupCode
				AlertDialog.Builder alert = new AlertDialog.Builder(NewAssessmentActivity.this);
			
				alert.setTitle("Value");
				//alert.setMessage("Message");
			
				// Set an EditText view to get user input 
				final EditText input = new EditText(NewAssessmentActivity.this);
				alert.setView(input);
				
				final int fPosition = position;
			
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Editable value = input.getText();
						if (value.length() > 0) {
							Log.d("value", "" + value);
							indicators.get(fPosition).setValue(value.toString());
							indicatorAdapter.notifyDataSetChanged();
						} else
							Toast.makeText(getApplicationContext(), "Value not valid", Toast.LENGTH_SHORT).show();
					}
				});
			
				alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
			
				alert.show();

				
			}
		});
        
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedTarget =  targets.get(position);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	protected void onResume() {
//		targetsDataSource.open();
//		indicatorsDataSource.open();
//        assessmentsDataSource.open();
		dataSource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
//		targetsDataSource.close();
//		indicatorsDataSource.close();
//        assessmentsDataSource.close();
		dataSource.close();
		super.onPause();
	}
	
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.saveButton:
			Assessment assessment = new Assessment();
			assessment.setName(nameEditText.getText().toString());
			assessment.setTargetId(selectedTarget.getId());
			assessment.setTargetName(selectedTarget.getName());
			assessment.setAssessorId(1); // TODO get it from somewhere
			
			assessment.setIndicators(indicators);
			dataSource.createAssessment(assessment);
			Toast.makeText(this, "Assessment created!", Toast.LENGTH_SHORT).show();
			Log.d("assessment created", assessment.getName() + assessment.getIndicators().toString());
			finish();
			break;
		
		}
	}

}
