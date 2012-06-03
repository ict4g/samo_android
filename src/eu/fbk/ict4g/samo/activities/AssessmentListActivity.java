package eu.fbk.ict4g.samo.activities;

import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.models.Assessment;
import eu.fbk.ict4g.samo.service.SamoServiceException;

public class AssessmentListActivity extends ListActivity {

	SamoDbDataSource dataSource;
	ArrayAdapter<Assessment> assessmentAdapter;
	List<Assessment> assessments;
	Assessment selectedAssessment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.assessment_list);
		ListView assessmentListView = getListView();

        dataSource = new SamoDbDataSource(this);
        dataSource.open();
        
        assessments = dataSource.getAllAssessments();
        Log.d("assessments.size()", "" + assessments.size());
        assessmentAdapter = new ArrayAdapter<Assessment>(this, android.R.layout.simple_list_item_1, assessments);
        assessmentListView.setAdapter(assessmentAdapter);
        
        assessmentListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				selectedAssessment = assessments.get(position);
				Log.d("selectedAssessment", selectedAssessment.getName() 
						+ selectedAssessment.getIndicators().toString() 
						+ " target id is " + selectedAssessment.getTargetId() 
						+ " target name is " + selectedAssessment.getTargetName());
				new PublishTask(AssessmentListActivity.this).execute();
				
			}
		});
		
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
		switch (view.getId()) {
		case R.id.clearButton:
			dataSource.deleteAllAssessments();
			assessmentAdapter.clear();
			break;
		
		case R.id.publishAllButton:
			
			break;
		}
	}

	private class PublishTask extends AsyncTask<Void, Void, Boolean> {
		
			ProgressDialog dialog;
			Context mContext;
		
			/**
			 * 
			 */
			public PublishTask(Context context) {
				this.mContext = context;
				dialog = new ProgressDialog(mContext);
				dialog.setTitle("Loading");
			}
		
			@Override
			protected void onPreExecute() {
				dialog.show();
			}
		
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					SAMoApp.getService().publishAssessment(selectedAssessment);
					return true;
				} catch (SamoServiceException e) {
					e.printStackTrace();
					return false;
				}
			}
		
			@Override
			protected void onPostExecute(Boolean result) {
				if (dialog.isShowing()) dialog.dismiss();
				if (result) {
					
				}
			}
		
		}
	
}
