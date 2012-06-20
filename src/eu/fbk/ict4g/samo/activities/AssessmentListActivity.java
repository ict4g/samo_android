package eu.fbk.ict4g.samo.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import eu.fbk.ict4g.samo.db.SamoDbDataSource;
import eu.fbk.ict4g.samo.models.Assessment;
import eu.fbk.ict4g.samo.service.SamoServiceException;

public class AssessmentListActivity extends ListActivity {

	SamoDbDataSource dataSource;
	//	ArrayAdapter<Assessment> assessmentAdapter;
	AssessmentAdapter assessmentAdapter;
	List<Assessment> assessments;
	Assessment selectedAssessment;
	ImageButton authButton;
	TextView authTextView;

	private static final int DETAILS_DIALOG = 42;
	private static final int LOGIN_DIALOG = 43;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.assessment_list);
		ListView assessmentListView = getListView();
		authButton =  (ImageButton) findViewById(R.id.authButton);
		authTextView = (TextView) findViewById(R.id.authTextView);
		
		authButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (SAMoApp.isUserLogged())
					// logout
					new LogoutTask(AssessmentListActivity.this).execute();
				else
					// login
					showDialog(LOGIN_DIALOG);
				
			}
		});

		dataSource = new SamoDbDataSource(this);
		dataSource.open();

		assessments = dataSource.getAllAssessments();
		Log.d("assessments.size()", "" + assessments.size());
		//        assessmentAdapter = new ArrayAdapter<Assessment>(this, android.R.layout.simple_list_item_1, assessments);
		assessmentAdapter = new AssessmentAdapter(this);
		assessmentListView.setAdapter(assessmentAdapter);

		if (savedInstanceState != null)
			selectedAssessment = savedInstanceState.getParcelable(getString(R.string.assessments));
		
		assessmentListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				selectedAssessment = assessments.get(position);
				Log.d("selectedAssessment", selectedAssessment.getName() 
						+ selectedAssessment.getIndicators().toString() 
						+ " target id is " + selectedAssessment.getTargetId() 
						+ " target name is " + selectedAssessment.getTargetName());
//				new PublishTask(AssessmentListActivity.this).execute();
				showDialog(DETAILS_DIALOG);

			}
		});

	}

	@Override
	protected void onResume() {
		dataSource.open();
		// here I change the appearence of the authButton accordingly to the current status
		toggleAuthButton();
		super.onResume();
	}

	@Override
	protected void onPause() {
		dataSource.close();
		super.onPause();
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		selectedAssessment = state.getParcelable(getString(R.string.assessments));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(getString(R.string.assessments), selectedAssessment);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle bundle) {
		LayoutInflater li = LayoutInflater.from(this);
		switch (id) {
		case DETAILS_DIALOG:
			View detailsView = li.inflate(R.layout.assessment_details, null);

			AlertDialog.Builder detailsDialog = new AlertDialog.Builder(this);
//			detailsDialog.setTitle(R.string.details);
			detailsDialog.setView(detailsView);
			detailsDialog.setPositiveButton(R.string.close, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			return detailsDialog.create();

//			Dialog dialog = new Dialog(this);
//			dialog.setContentView(R.layout.assessment_details);
//			dialog.setTitle(R.string.details);
//			dialog.setCancelable(true);
//			return dialog;
			
		case LOGIN_DIALOG:
			View loginView = li.inflate(R.layout.login_dialog, null);

			AlertDialog.Builder loginDialog = new AlertDialog.Builder(this);
//			detailsDialog.setTitle(R.string.details);
			loginDialog.setView(loginView);
//			loginDialog.setPositiveButton(R.string.ok, new OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					new LoginTask(AssessmentListActivity.this);
//					dialog.cancel();
//				}
//			});
//			loginDialog.setNegativeButton(R.string.close, new OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.cancel();
//				}
//			});
			
			return loginDialog.create();
			
		default:
			break;
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
		final AlertDialog d = (AlertDialog) dialog;
		switch (id) {
		case DETAILS_DIALOG:
//			final Dialog d = dialog;
			TextView nameTextView = (TextView) d.findViewById(R.id.nameTextView);
			TextView dateTextView = (TextView) d.findViewById(R.id.dateTextView);
			TextView targetTextView = (TextView) d.findViewById(R.id.targetTextView);
			TextView assessorTextView = (TextView) d.findViewById(R.id.assessorTextView);
			ImageButton uploadButton = (ImageButton) d.findViewById(R.id.uploadButton);
			ImageButton deleteButton = (ImageButton) d.findViewById(R.id.deleteButton);
			
			nameTextView.setText(selectedAssessment.getName());
			dateTextView.setText(selectedAssessment.getDate());
			targetTextView.setText(selectedAssessment.getTargetName());
			assessorTextView.setText(selectedAssessment.getAssessorName());	
			
			uploadButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					new PublishTask(AssessmentListActivity.this).execute();
					d.cancel();
					
				}
			});

			deleteButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					new DeleteAssessmentTask(AssessmentListActivity.this).execute();
					d.cancel();
					
				}
			});
			
			break;

		case LOGIN_DIALOG:
			d.getWindow().setLayout(600, 400);
			final EditText emailEditText =  (EditText) d.findViewById(R.id.emailEditText);
			final EditText passwdEditText =  (EditText) d.findViewById(R.id.passwordEditText);
			Button loginButton = (Button) d.findViewById(R.id.loginButton);
			Button cancelButton = (Button) d.findViewById(R.id.cancelButton);
			
			loginButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String email = emailEditText.getText().toString();
					String passwd = passwdEditText.getText().toString();
					new LoginTask(AssessmentListActivity.this).execute(email, passwd);
					dismissDialog(LOGIN_DIALOG);
				}
			});
			
			cancelButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismissDialog(LOGIN_DIALOG);
					
				}
			});
			
			break;
		default:
			break;
		}
	}

	public void onClick(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.are_you_sure);
		builder.setCancelable(false);
		
		switch (view.getId()) {
		case R.id.deleteAllButton:
			builder.setTitle(getString(R.string.about_to_delete_all));
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					new DeleteAllAssessmentsTask(AssessmentListActivity.this).execute();
				}
			});
			break;

		case R.id.uploadAllButton:
			builder.setTitle(getString(R.string.about_to_delete_all));
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					new PublishAllTask(AssessmentListActivity.this).execute();
				}
			});
			break;
		}
		
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void toggleAuthButton() {
		if (SAMoApp.isUserLogged()) {
			authButton.setImageResource(R.drawable.logout);
			authTextView.setText(R.string.logout);
		} else {
			authButton.setImageResource(R.drawable.login);
			authTextView.setText(R.string.login);
		}
		
	}

	private class PublishTask extends AsyncTask<Void, Void, Boolean> {

		ProgressDialog dialog;
		Context mContext;
		String errMsg;

		/**
		 * 
		 */
		public PublishTask(Context context) {
			this.mContext = context;
			dialog = new ProgressDialog(mContext);
			dialog.setTitle(R.string.loading);
			errMsg = "";
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
				errMsg = e.getMessage();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (dialog.isShowing()) dialog.dismiss();
			if (result) {
				dataSource.markAssessmentAsUploaded(selectedAssessment.getId());
				selectedAssessment.setUploaded(true);
				assessmentAdapter.notifyDataSetChanged();
				Toast.makeText(mContext, R.string.toast_assmnt_uploaded, Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(mContext, errMsg, Toast.LENGTH_SHORT).show();
		}

	}

	private class PublishAllTask extends AsyncTask<Void, Void, Boolean> {
	
		ProgressDialog dialog;
		Context mContext;
		String errMsg;
		List<Assessment> assmtsToUpload;
	
		/**
		 * 
		 */
		public PublishAllTask(Context context) {
			this.mContext = context;
			dialog = new ProgressDialog(mContext);
			dialog.setTitle(R.string.loading);
			errMsg = "";
			assmtsToUpload = new ArrayList<Assessment>();
		}
	
		@Override
		protected void onPreExecute() {
			dialog.show();
		}
	
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				// Publish only unpublished assessments
				for (Assessment assessment : assessments) {
					if (!assessment.isUploaded())
						assmtsToUpload.add(assessment);
					else
						Log.d(mContext.getClass().getSimpleName(), "Assessment " + assessment.getName() + " should be uploaded");
				}
				if (assmtsToUpload.isEmpty()) {
					errMsg = getString(R.string.toast_error_nothing_to_upload);
					return false;
				} else
					SAMoApp.getService().publishAllAssessments(assmtsToUpload);
				return true;
			} catch (SamoServiceException e) {
				e.printStackTrace();
				errMsg = e.getMessage();
				return false;
			}
		}
	
		@Override
		protected void onPostExecute(Boolean result) {
			if (dialog.isShowing()) dialog.dismiss();
			if (result) {
				for (Assessment assessment : assmtsToUpload) {
					dataSource.markAssessmentAsUploaded(assessment.getId());
					assessment.setUploaded(true);
				}
				assessmentAdapter.notifyDataSetChanged();
				Toast.makeText(mContext, R.string.toast_assmnt_uploaded, Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(mContext, errMsg, Toast.LENGTH_SHORT).show();
		}
	
	}

	private class DeleteAssessmentTask extends AsyncTask<Void, Void, Boolean> {
	
		ProgressDialog dialog;
		Context mContext;
	
		/**
		 * 
		 */
		public DeleteAssessmentTask(Context context) {
			this.mContext = context;
			dialog = new ProgressDialog(mContext);
			dialog.setTitle(getString(R.string.deleting_assessment));
		}
	
		@Override
		protected void onPreExecute() {
			dialog.show();
		}
	
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				dataSource.deleteAssessment(selectedAssessment);
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
				Toast.makeText(mContext, R.string.toast_assmnt_deleted, Toast.LENGTH_SHORT).show();
				assessments.remove(selectedAssessment);
				assessmentAdapter.notifyDataSetChanged();
			}
		}
	
	}

	private class DeleteAllAssessmentsTask extends AsyncTask<Void, Void, Boolean> {
	
		ProgressDialog dialog;
		Context mContext;
	
		/**
		 * 
		 */
		public DeleteAllAssessmentsTask(Context context) {
			this.mContext = context;
			dialog = new ProgressDialog(mContext);
			dialog.setTitle(getString(R.string.deleting_assessment));
		}
	
		@Override
		protected void onPreExecute() {
			dialog.show();
		}
	
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				dataSource.deleteAllAssessments();
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
				Toast.makeText(mContext, R.string.toast_all_assmnts_deleted, Toast.LENGTH_SHORT).show();
				assessments.clear();
				assessmentAdapter.notifyDataSetChanged();
			}
		}
	
	}

	private class AssessmentAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		//        public AssessmentAdapter(Context context, int textViewResourceId,
		//				List<Assessment> objects) {
		//			super(context, textViewResourceId, objects);
		//			mInflater = LayoutInflater.from(context);
		//		}
		public AssessmentAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return assessments.size();
		}


		@Override
		public Assessment getItem(int position) {
			return assessments.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			TextView nameTextView;
			TextView dateTextView;
			TextView targetTextView;
			TextView assessorTextView;
			ImageView badgeImageView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			final Assessment currentAssessment = assessments.get(position);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.assessment_list_item, null);

				// Creates a ViewHolder and store references to the two children views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
				holder.dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
				holder.targetTextView = (TextView) convertView.findViewById(R.id.targetTextView);
				holder.assessorTextView = (TextView) convertView.findViewById(R.id.assessorTextView);
				holder.badgeImageView = (ImageView) convertView.findViewById(R.id.badgeImageView);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.nameTextView.setText(currentAssessment.getName());
			holder.dateTextView.setText(currentAssessment.getDate());
			holder.targetTextView.setText(currentAssessment.getTargetName());
			holder.assessorTextView.setText(currentAssessment.getAssessorName());
			holder.badgeImageView.setVisibility(currentAssessment.isUploaded() ? View.VISIBLE : View.INVISIBLE);

			return convertView;
		}

	}

	private class LoginTask extends AsyncTask<String, Void, Boolean> {
	
		ProgressDialog dialog;
		Context mContext;
	
		/**
		 * 
		 */
		public LoginTask(Context context) {
			this.mContext = context;
			dialog = new ProgressDialog(mContext);
			dialog.setTitle("Loading");
		}
	
		@Override
		protected void onPreExecute() {
			dialog.show();
		}
	
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				//SAMoApp.getService().login("pbmolini@fbk.eu", "Asdf1");
				SAMoApp.getService().login(params[0], params[1]);
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
				SAMoApp.setUserLogged(true);
				toggleAuthButton();
			}
		}
	
	}

	private class LogoutTask extends AsyncTask<Void, Void, Boolean> {
	
		ProgressDialog dialog;
		Context mContext;
	
		/**
		 * 
		 */
		public LogoutTask(Context context) {
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
				SAMoApp.getService().logout();
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
				SAMoApp.setUserLogged(false);
				toggleAuthButton();	
			}
		}
	
	}
}
