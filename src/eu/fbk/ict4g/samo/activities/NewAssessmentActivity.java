package eu.fbk.ict4g.samo.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
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
	ArrayList<Indicator> indicators;
	List<Target> targets;
	
	Assessment newAssessment;
	
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
        
        targetAdapter =  new ArrayAdapter<Target>(this, android.R.layout.simple_spinner_item, targets);
        spinner.setAdapter(targetAdapter);    
        
        if (savedInstanceState != null)
        	indicators = savedInstanceState.getParcelableArrayList(getString(R.string.indicators));
        else
        	indicators = (ArrayList<Indicator>) dataSource.getAllIndicators();
        
        
//        indicatorAdapter =  new ArrayAdapter<Indicator>(this, android.R.layout.simple_list_item_1, indicators);
        indicatorAdapter = new IndicatorAdapter(this, R.layout.indicator_row_yesno, indicators);
        listView.setAdapter(indicatorAdapter); 
//        
//        listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position,
//					long id) {
//
//				// Create the alert for the pickupCode
//				AlertDialog.Builder alert = new AlertDialog.Builder(NewAssessmentActivity.this);
//			
//				alert.setTitle("Value");
//				//alert.setMessage("Message");
//			
//				// Set an EditText view to get user input 
//				final EditText input = new EditText(NewAssessmentActivity.this);
//				alert.setView(input);
//				
//				final int fPosition = position;
//			
//				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						Editable value = input.getText();
//						if (value.length() > 0) {
//							Log.d("value", "" + value);
//							indicators.get(fPosition).setValue(value.toString());
//							indicatorAdapter.notifyDataSetChanged();
//						} else
//							Toast.makeText(getApplicationContext(), "Value not valid", Toast.LENGTH_SHORT).show();
//					}
//				});
//			
//				alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						// Canceled.
//					}
//				});
//			
//				alert.show();
//
//				
//			}
//		});
        
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
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		indicators = savedInstanceState.getParcelableArrayList(getString(R.string.indicators));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(getString(R.string.indicators), indicators);
		super.onSaveInstanceState(outState);
	}

	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.saveButton:
			new SaveAssessmentTask(NewAssessmentActivity.this).execute();
			break;
		
		}
	}
	
	private void saveAssessment() {
		newAssessment = new Assessment();
		newAssessment.setName(nameEditText.getText().toString());
		newAssessment.setTargetId(selectedTarget.getId());
		newAssessment.setTargetName(selectedTarget.getName());
		newAssessment.setAssessorId(1); // TODO get it from somewhere
		
		newAssessment.setIndicators(indicators);
		dataSource.createAssessment(newAssessment);
		finish();
	}

	private class IndicatorAdapter extends ArrayAdapter<Indicator> {

		public IndicatorAdapter(Context context, int textViewResourceId,
				ArrayList<Indicator> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			final Indicator selectedIndicator = indicators.get(position);
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (selectedIndicator.getType().equals(Indicator.TYPE_STAR)) {
				// STAR Indicator management
				v = inflater.inflate(R.layout.indicator_row_star, null);
			} else if (selectedIndicator.getType().equals(Indicator.TYPE_NUMBER)) {
				// NUMBER Indicator management
				v = inflater.inflate(R.layout.indicator_row_number, null);
				final EditText editText = (EditText) v.findViewById(R.id.editText);
				if (selectedIndicator.getValue() != null) 
					editText.setText(selectedIndicator.getValue());
				editText.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (!hasFocus)
							selectedIndicator.setValue(editText.getText().toString());
						
					}
				});
			} else if (selectedIndicator.getType().equals(Indicator.TYPE_YESNO)) {
				// YESNO Indicator management
				v = inflater.inflate(R.layout.indicator_row_yesno, null);
				RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
				if (selectedIndicator.getValue() != null) 
					if (selectedIndicator.getValue().equalsIgnoreCase("0"))
						radioGroup.check(R.id.noRadioButton);
					else
						radioGroup.check(R.id.yesRadioButton);
				
				radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (group.getCheckedRadioButtonId()) {
						case R.id.yesRadioButton:
							selectedIndicator.setValue("1"); // true
							break;

						case R.id.noRadioButton:
							selectedIndicator.setValue("0"); // false					
							break;
						}
						indicatorAdapter.notifyDataSetChanged();
					}
				});
			} else if (selectedIndicator.getType().equals(Indicator.TYPE_PERCENT)) {
				// PERCENT Indicator management
				v = inflater.inflate(R.layout.indicator_row_percent, null);
				SeekBar seekBar = (SeekBar) v.findViewById(R.id.seekBar);
				final TextView percentTextView = (TextView) v.findViewById(R.id.percentTextView);
				seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						selectedIndicator.setValue(""+ seekBar.getProgress());
						indicatorAdapter.notifyDataSetChanged();
						
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						percentTextView.setText(progress + "%");
						
					}
				});
			} else {
				// TEXT Indicator management
				v = inflater.inflate(R.layout.indicator_row_text, null);
				final EditText editText = (EditText) v.findViewById(R.id.editText);
				if (selectedIndicator.getValue() != null) 
					editText.setText(selectedIndicator.getValue());
				editText.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (!hasFocus)
							selectedIndicator.setValue(editText.getText().toString());
						
					}
				});
			}
			
			((TextView) v.findViewById(R.id.nameTextView)).setText(selectedIndicator.getName());
			return v;
		}
		
	}

	private class SaveAssessmentTask extends AsyncTask<Void, Void, Boolean> {
	
		ProgressDialog dialog;
		Context mContext;
	
		/**
		 * 
		 */
		public SaveAssessmentTask(Context context) {
			this.mContext = context;
			dialog = new ProgressDialog(mContext);
			dialog.setTitle(getString(R.string.saving_assessment));
		}
	
		@Override
		protected void onPreExecute() {
			dialog.show();
		}
	
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				saveAssessment();
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
				Toast.makeText(mContext, "Assessment created!", Toast.LENGTH_SHORT).show();
				Log.d(mContext.getClass().getSimpleName(), "assessment created: " + newAssessment.getName() + newAssessment.getIndicators().toString());
			}
		}
	
	}
	
}
