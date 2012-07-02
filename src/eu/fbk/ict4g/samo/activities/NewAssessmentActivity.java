package eu.fbk.ict4g.samo.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.models.Assessment;
import eu.fbk.ict4g.samo.models.Indicator;
import eu.fbk.ict4g.samo.models.Target;
import eu.fbk.ict4g.samo.utils.SAMoLog;

public class NewAssessmentActivity extends Activity {

	SamoDbDataSource dataSource;

	ArrayAdapter<Target> targetAdapter;
	//	ArrayAdapter<Indicator> indicatorAdapter;
	IndicatorAdapter indicatorAdapter;
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

		targetAdapter =  new ArrayAdapter<Target>(this, android.R.layout.simple_spinner_dropdown_item, targets);
		spinner.setAdapter(targetAdapter);    

		if (savedInstanceState != null) {
			SAMoLog.d(getClass().getSimpleName(), "savedInstanceState is NOT null");
			indicators = savedInstanceState.getParcelableArrayList(getString(R.string.indicators));	
			if (indicators != null)
				for (Indicator i : indicators) {
					SAMoLog.d(getClass().getSimpleName(), "saved value for " + i.getName() + " is " + i.getValue());
				}
		} else {
			SAMoLog.d(getClass().getSimpleName(), "savedInstanceState is null");
			indicators = (ArrayList<Indicator>) dataSource.getAllIndicators();
		}


		//        indicatorAdapter =  new ArrayAdapter<Indicator>(this, android.R.layout.simple_list_item_1, indicators);
		//		indicatorAdapter = new IndicatorAdapter(this, R.layout.indicator_row_yesno, indicators);
		indicatorAdapter = new IndicatorAdapter(this);
		listView.setAdapter(indicatorAdapter); 
		listView.setItemsCanFocus(true);
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
		newAssessment.setAssessorId(SAMoApp.getUserId()); 
		newAssessment.setAssessorName(SAMoApp.getUserName()); 
		newAssessment.setCampaignId(SAMoApp.getCurrentCampaign().getRemId());

		// date stuff
		Calendar now = Calendar.getInstance();
		String month = (now.get(Calendar.MONTH) + 1) < 10 ? "0" + (now.get(Calendar.MONTH) + 1) : (now.get(Calendar.MONTH) + 1) + "";
		String day = now.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + now.get(Calendar.DAY_OF_MONTH) : now.get(Calendar.DAY_OF_MONTH) + "";
		newAssessment.setDate(now.get(Calendar.YEAR) + "-" + month + "-" + day);

		newAssessment.setIndicators(indicators);
		dataSource.createAssessment(newAssessment);
		finish();
	}

	private class IndicatorAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public IndicatorAdapter(Context context) {
			//super(context, textViewResourceId, objects);
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
		}

		class ViewHolderText {
			TextView nameTextView;
			EditText editText;
		}

		class ViewHolderYesNo {
			TextView nameTextView;
			RadioGroup radioGroup;
		}

		class ViewHolderNumber {
			TextView nameTextView;
			EditText editText;
			ImageButton plusButton, minusButton;			
		}

		class ViewHolderPercent {
			TextView nameTextView, percentTextView;
			SeekBar seekBar;
		}

		class ViewHolderStar {
			TextView nameTextView;
			RatingBar ratingBar;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final Indicator currentIndicator = indicators.get(position);

			if (currentIndicator.getType().equals(Indicator.TYPE_STAR)) {
				// STAR Indicator management
				ViewHolderStar holder;
				// When convertView is not null, we can reuse it directly, there is no need
				// to reinflate it. We only inflate a new View when the convertView supplied
				// by ListView is null.
				if (convertView == null || !ViewHolderStar.class.isInstance(convertView.getTag())) {
					convertView = mInflater.inflate(R.layout.indicator_row_star, null);

					// Creates a ViewHolder and store references to the two children views
					// we want to bind data to.
					holder = new ViewHolderStar();
					holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
					holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);

					convertView.setTag(holder);
				} else {
					// Get the ViewHolder back to get fast access to the TextView
					// and the ImageView.
					holder = (ViewHolderStar) convertView.getTag();
				}

				// name
				holder.nameTextView.setText(currentIndicator.getName());

				// value
				// set the listeners
				holder.ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar, float rating,
							boolean fromUser) {
						if (fromUser) {
							currentIndicator.setValue("" + rating);
						}

					}
				});

				// restore previous value, if any
				if (currentIndicator.getValue() != null) 
					holder.ratingBar.setRating(Float.parseFloat(currentIndicator.getValue().toString()));

			} else if (currentIndicator.getType().equals(Indicator.TYPE_NUMBER)) {
				// NUMBER Indicator management
				ViewHolderNumber holder;
				// When convertView is not null, we can reuse it directly, there is no need
				// to reinflate it. We only inflate a new View when the convertView supplied
				// by ListView is null.
				if (convertView == null || !ViewHolderNumber.class.isInstance(convertView.getTag())) {
					convertView = mInflater.inflate(R.layout.indicator_row_number, null);

					// Creates a ViewHolder and store references to the two children views
					// we want to bind data to.
					holder = new ViewHolderNumber();
					holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
					holder.editText = (EditText) convertView.findViewById(R.id.editText);
					holder.plusButton = (ImageButton) convertView.findViewById(R.id.plusButton);
					holder.minusButton = (ImageButton) convertView.findViewById(R.id.minusButton);

					convertView.setTag(holder);
				} else {
					// Get the ViewHolder back to get fast access to the TextView
					// and the ImageView.
					holder = (ViewHolderNumber) convertView.getTag();
				}

				// name
				holder.nameTextView.setText(currentIndicator.getName());

				// value
				// set the listeners
				holder.editText.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (!hasFocus)
							currentIndicator.setValue(((EditText) v).getText().toString());

					}
				});

				holder.plusButton.setOnClickListener(new PlusMinusOnClickListener(holder.editText));
				holder.minusButton.setOnClickListener(new PlusMinusOnClickListener(holder.editText));

				// restore previous value, if any
				if (currentIndicator.getValue() != null) 
					holder.editText.setText(currentIndicator.getValue());

			} else if (currentIndicator.getType().equals(Indicator.TYPE_YESNO)) {
				// YESNO Indicator management
				ViewHolderYesNo holder;

				// When convertView is not null, we can reuse it directly, there is no need
				// to reinflate it. We only inflate a new View when the convertView supplied
				// by ListView is null.
				if (convertView == null || !ViewHolderYesNo.class.isInstance(convertView.getTag())) {
					convertView = mInflater.inflate(R.layout.indicator_row_yesno, null);

					// Creates a ViewHolder and store references to the two children views
					// we want to bind data to.
					holder = new ViewHolderYesNo();
					holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
					holder.radioGroup = (RadioGroup) convertView.findViewById(R.id.radioGroup);

					convertView.setTag(holder);
				} else {
					// Get the ViewHolder back to get fast access to the TextView
					// and the ImageView.
					holder = (ViewHolderYesNo) convertView.getTag();
				}

				// name
				holder.nameTextView.setText(currentIndicator.getName());

				// value
				// set the listener
				holder.radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.yesRadioButton:
							currentIndicator.setValue("1"); // true
							break;

						case R.id.noRadioButton:
							currentIndicator.setValue("0"); // false					
							break;
						}
						//indicatorAdapter.notifyDataSetChanged();
					}
				});

				// restore previous value, if any
				if (currentIndicator.getValue() != null) 
					if (currentIndicator.getValue().equalsIgnoreCase("1"))
						holder.radioGroup.check(R.id.yesRadioButton);
					else if (currentIndicator.getValue().equalsIgnoreCase("0"))
						holder.radioGroup.check(R.id.noRadioButton);
					else
						holder.radioGroup.clearCheck();


			} else if (currentIndicator.getType().equals(Indicator.TYPE_PERCENT)) {
				// PERCENT Indicator management
				final ViewHolderPercent holder;
				// When convertView is not null, we can reuse it directly, there is no need
				// to reinflate it. We only inflate a new View when the convertView supplied
				// by ListView is null.
				if (convertView == null || !ViewHolderPercent.class.isInstance(convertView.getTag())) {
					convertView = mInflater.inflate(R.layout.indicator_row_percent, null);

					// Creates a ViewHolder and store references to the two children views
					// we want to bind data to.
					holder = new ViewHolderPercent();
					holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
					holder.seekBar = (SeekBar) convertView.findViewById(R.id.seekBar);
					holder.percentTextView = (TextView) convertView.findViewById(R.id.percentTextView);

					convertView.setTag(holder);
				} else {
					// Get the ViewHolder back to get fast access to the TextView
					// and the ImageView.
					holder = (ViewHolderPercent) convertView.getTag();
				}

				// name
				holder.nameTextView.setText(currentIndicator.getName());

				// value
				// set the listener
				holder.seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						currentIndicator.setValue(""+ seekBar.getProgress());

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						holder.percentTextView.setText(seekBar.getProgress() + "%");

					}

					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						holder.percentTextView.setText(progress + "%");

					}
				});
			} else {
				// TEXT Indicator management
				ViewHolderText holder;

				// When convertView is not null, we can reuse it directly, there is no need
				// to reinflate it. We only inflate a new View when the convertView supplied
				// by ListView is null.
				if (convertView == null || !ViewHolderText.class.isInstance(convertView.getTag())) {
					convertView = mInflater.inflate(R.layout.indicator_row_text, null);

					// Creates a ViewHolder and store references to the two children views
					// we want to bind data to.
					holder = new ViewHolderText();
					holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
					holder.editText = (EditText) convertView.findViewById(R.id.editText);

					convertView.setTag(holder);
				} else {
					// Get the ViewHolder back to get fast access to the TextView
					// and the ImageView.
					holder = (ViewHolderText) convertView.getTag();
				}

				// name
				holder.nameTextView.setText(currentIndicator.getName());

				// value
				// set the listener
				holder.editText.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (!hasFocus)
							currentIndicator.setValue(((EditText) v).getText().toString());

					}


				});
				// restore previous value, if any
				if (currentIndicator.getValue() != null) 
					holder.editText.setText(currentIndicator.getValue());
			}
			return convertView;

		}

		@Override
		public int getCount() {
			return indicators.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
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
				SAMoLog.d(mContext.getClass().getSimpleName(), "assessment created: " + newAssessment.getName() + newAssessment.getIndicators().toString());
			}
		}

	}

	private class PlusMinusOnClickListener implements OnClickListener {
		EditText mEditText;

		/**
		 * @param editText
		 */
		public PlusMinusOnClickListener(EditText editText) {
			this.mEditText = editText;
		}

		@Override
		public void onClick(View v) {
			mEditText.requestFocus();
			switch (v.getId()) {
			case R.id.plusButton:
				mEditText.setText(
						Integer.parseInt(mEditText.getText().toString().equalsIgnoreCase("") ? "0" : mEditText.getText().toString()) + 1 + "",
						TextView.BufferType.EDITABLE);
				break;

			case R.id.minusButton:
				int value = Integer.parseInt(mEditText.getText().toString().equalsIgnoreCase("") ? "0" : mEditText.getText().toString());
				mEditText.setText(value - 1 >= 0 ? value - 1 + "" : "", 
						TextView.BufferType.EDITABLE);
				break;

			default:
				break;
			}
		}

	}

}
