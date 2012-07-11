package eu.fbk.ict4g.samo.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import eu.fbk.ict4g.samo.activities.SAMoApp;
import eu.fbk.ict4g.samo.models.Assessment;
import eu.fbk.ict4g.samo.models.Campaign;
import eu.fbk.ict4g.samo.models.Indicator;
import eu.fbk.ict4g.samo.models.Target;
import eu.fbk.ict4g.samo.utils.SAMoLog;

public class SamoDbDataSource {

	private static final String TAG = SamoDbDataSource.class.getSimpleName();

	private SimpleDateFormat dateFormat;

	// Database fields
	private SQLiteDatabase database;
	private SamoDbHelper dbHelper;

	private String assessmentMinColumns = 
			SamoDbHelper.COLUMN_ID + ","
					+ SamoDbHelper.COLUMN_NAME;

	public SamoDbDataSource(Context context) {

		if (dbHelper == null) dbHelper =  new SamoDbHelper(context);
		dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");
	}

	public void open() throws SQLException {
		if (database == null) {
			database = dbHelper.getWritableDatabase();
			SAMoLog.d(TAG, "open()");
		}
	}

	public void close() {
		SAMoLog.d(TAG, "close()");
		try {
			if (dbHelper != null)
				dbHelper.close();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public synchronized void addIndicatorColumn(Indicator indicator) {
		//String colName = indicator.getName().replace(" ", "_").replace("?", "").toLowerCase() + "_" + indicator.getId();
		String colName = createColumnName(indicator.getName(), indicator.getType(), indicator.getId());
		String sql = "ALTER TABLE " + SamoDbHelper.TABLE_ASSESSMENTS 
				+ " ADD COLUMN '" + colName + "' text;";
		database.execSQL(sql);
		SAMoLog.d(TAG, "sql:" + sql);
		printColumnsOfAssessmentsTable();
	}

	public void addIndicator(Indicator indicator) {
		SAMoLog.d(TAG, "Indicator added deleted with name: " + indicator.getName());
		database.execSQL("ALTER TABLE " + SamoDbHelper.TABLE_INDICATORS + " ADD " + indicator.getName() + " text not null");
	}

	public void addTarget(Target target) {
		SAMoLog.d(TAG, "Target added deleted with name: " + target.getName());
		database.execSQL("ALTER TABLE " + SamoDbHelper.TABLE_TARGETS + " ADD " + target.getName() + " text not null");
	}

	public synchronized void createAssessment(Assessment assessment) {

		Cursor c = database.query(SamoDbHelper.TABLE_ASSESSMENTS,
				null, null, null, null, null, null);
		for (int i = 0; i < c.getColumnNames().length; i++) {
			SAMoLog.d(TAG, "column " + i + ": " + c.getColumnNames()[i]);
		}

		ContentValues values = new ContentValues();
		values.put(SamoDbHelper.COLUMN_NAME, assessment.getName());
		values.put(SamoDbHelper.COLUMN_ASSESSOR_ID, assessment.getAssessorId());
		// TODO 
		values.put(SamoDbHelper.COLUMN_ASSESSOR_NAME, assessment.getAssessorName());
		values.put(SamoDbHelper.COLUMN_CAMPAIGN_ID, assessment.getCampaignId());
		values.put(SamoDbHelper.COLUMN_TARGET_ID, assessment.getTargetId());
		values.put(SamoDbHelper.COLUMN_TARGET_NAME, assessment.getTargetName());
		values.put(SamoDbHelper.COLUMN_UPLOADED, assessment.isUploaded() ? 1 : 0);
		values.put(SamoDbHelper.COLUMN_DATE, assessment.getDate());
		String colName;

		for (Indicator indicator : assessment.getIndicators()) {
			//colName = indicator.getName().replace(" ", "_").replace("?", "").toLowerCase() + "_" + indicator.getId();
			colName = createColumnName(indicator.getName(), indicator.getType(), indicator.getId());
			values.put(colName, indicator.getValue());
		}
		long insertId = database.insert(SamoDbHelper.TABLE_ASSESSMENTS, null,
				values);
		Cursor cursor = database.query(SamoDbHelper.TABLE_ASSESSMENTS,
				null, SamoDbHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		SAMoLog.d(TAG, "cursor size after having created: " + cursor.getCount() + "");
		if (cursor.getCount() > 0) {
			Assessment newAssessment = cursorToAssessment(cursor);
			SAMoLog.d(TAG, "newAssessment: " + newAssessment.toString());
		}
		cursor.close();
		//return newAssessment;
	}

	public synchronized Campaign createCampaign(Campaign campaign) {
		Campaign newCampaign;
		Cursor cursor = null;
		try {
			ContentValues values = new ContentValues();
			values.put(SamoDbHelper.COLUMN_TITLE, campaign.getTitle());
			values.put(SamoDbHelper.COLUMN_DESCRIPTION, campaign.getDescription());
			values.put(SamoDbHelper.COLUMN_DATE_FROM, campaign.getDateFrom());
			values.put(SamoDbHelper.COLUMN_DATE_TO, campaign.getDateTo());
			values.put(SamoDbHelper.COLUMN_REMOTE_ID, campaign.getRemId());
			long insertId = database.insert(SamoDbHelper.TABLE_CAMPAIGNS, null,
					values);
			cursor = database.query(SamoDbHelper.TABLE_CAMPAIGNS,
					null, SamoDbHelper.COLUMN_ID + " = " + insertId, null,
					null, null, null);
			cursor.moveToFirst();
			newCampaign = cursorToCampaign(cursor);
		} finally {
			if (cursor != null) cursor.close();
		}
		return newCampaign;

	}

	public synchronized Indicator createIndicator(Indicator indicator) {
		Indicator newIndicator;
		Cursor cursor = null;
		try {
			ContentValues values = new ContentValues();
			values.put(SamoDbHelper.COLUMN_NAME, indicator.getName());
			values.put(SamoDbHelper.COLUMN_VALUE, "");
			values.put(SamoDbHelper.COLUMN_TYPE, indicator.getType());
			values.put(SamoDbHelper.COLUMN_REMOTE_ID, indicator.getId());
			long insertId = database.insert(SamoDbHelper.TABLE_INDICATORS, null,
					values);
			cursor = database.query(SamoDbHelper.TABLE_INDICATORS,
					null, SamoDbHelper.COLUMN_ID + " = " + insertId, null,
					null, null, null);
			cursor.moveToFirst();
			newIndicator = cursorToIndicator(cursor);
		} finally {
			if (cursor != null) cursor.close();
		}
		return newIndicator;
	}

	public synchronized Target createTarget(Target target) {
		Target newTarget;
		Cursor cursor = null;
		try {
			ContentValues values = new ContentValues();
			values.put(SamoDbHelper.COLUMN_NAME, target.getName());
			values.put(SamoDbHelper.COLUMN_LATITUDE, target.getLat());
			values.put(SamoDbHelper.COLUMN_LONGITUDE, target.getLng());
			long insertId = database.insert(SamoDbHelper.TABLE_TARGETS, null,
					values);
			cursor = database.query(SamoDbHelper.TABLE_TARGETS,
					null, SamoDbHelper.COLUMN_ID + " = " + insertId, null,
					null, null, null);
			cursor.moveToFirst();
			newTarget = cursorToTarget(cursor);
		} finally {
			if (cursor != null) cursor.close();
		}
		return newTarget;
	}

	public synchronized void deleteAssessment(Assessment assessment) {
		long id = assessment.getId();
		SAMoLog.d(TAG, "Assessment deleted with id: " + id);
		database.delete(SamoDbHelper.TABLE_ASSESSMENTS, SamoDbHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public synchronized void deleteIndicator(Indicator indicator) {
		long id = indicator.getId();
		SAMoLog.d(TAG, "Indicator deleted with id: " + id);
		database.delete(SamoDbHelper.TABLE_INDICATORS, SamoDbHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public synchronized void deleteTarget(Target target) {
		long id = target.getId();
		SAMoLog.d(TAG, "Target deleted with id: " + id);
		database.delete(SamoDbHelper.TABLE_TARGETS, SamoDbHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public synchronized void deleteAllAssessments() {
		database.delete(SamoDbHelper.TABLE_ASSESSMENTS, null, null);
		SAMoLog.w(TAG, "All Assessments deleted");
	}

	public synchronized void deleteAllIndicatorColumns() {
		String sql = /*"BEGIN TRANSACTION; " +*/
				"CREATE TEMPORARY TABLE " + SamoDbHelper.TABLE_ASSESSMENTS + "_backup(" + assessmentMinColumns + ");" +
				"INSERT INTO " + SamoDbHelper.TABLE_ASSESSMENTS + "_backup SELECT " + assessmentMinColumns + " FROM " + SamoDbHelper.TABLE_ASSESSMENTS + ";" +
				"DROP TABLE " + SamoDbHelper.TABLE_ASSESSMENTS + ";" +
				"CREATE TABLE " + SamoDbHelper.TABLE_ASSESSMENTS + "(" + assessmentMinColumns + ");" +
				"INSERT INTO " + SamoDbHelper.TABLE_ASSESSMENTS + " SELECT " + assessmentMinColumns + " FROM " + SamoDbHelper.TABLE_ASSESSMENTS + "_backup; " +
				"DROP TABLE " + SamoDbHelper.TABLE_ASSESSMENTS + "_backup;" +
				"COMMIT;";
		database.beginTransaction();
		database.execSQL(sql);
		database.endTransaction();
		SAMoLog.d("sql:", sql);
	}

	public synchronized void deleteAllIndicators() {
		database.delete(SamoDbHelper.TABLE_INDICATORS, null, null);
		SAMoLog.w(TAG, "All Indicators deleted");
	}

	public synchronized void deleteAllTargets() {
		database.delete(SamoDbHelper.TABLE_TARGETS, null, null);
		SAMoLog.w(TAG, "All Targets deleted");
	}

	public String dumpDatabase() {
		String result = null;
		try {	
			SAMoLog.w(TAG, "trying to dump the db...");
			File sd = Environment.getExternalStorageDirectory();

			if (sd.canWrite()) {
				String timeStamp = dateFormat.format(Calendar.getInstance().getTime());
				String backupDBPath = SamoDbHelper.DATABASE_NAME + "_" + timeStamp;
				File currentDB = SAMoApp.getDatabasePath();
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB).getChannel();
					FileChannel dst = new FileOutputStream(backupDB).getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
					result = backupDB.getAbsolutePath();
					SAMoLog.w(TAG, "db saved in " + backupDB.getAbsolutePath());
				} else
					SAMoLog.w(TAG, "currentDB.exists() false");
			} else
				SAMoLog.w(TAG, "sd.canWrite() false");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public synchronized List<Assessment> getAllAssessments() {
		List<Assessment> assessments = new ArrayList<Assessment>();

		Cursor cursor = database.query(SamoDbHelper.TABLE_ASSESSMENTS,
				null, null, null, null, null, null);

		cursor.moveToFirst();

		SAMoLog.d(TAG + ".getAllAssessments()", "there are " + cursor.getCount() + " assessments rows");
		while (!cursor.isAfterLast()) {
			SAMoLog.d("cursor", cursor.toString());
			Assessment assessment = cursorToAssessment(cursor);
			assessments.add(assessment);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return assessments;
	}

	public synchronized List<Campaign> getAllCampaigns() {
		List<Campaign> campaigns = new ArrayList<Campaign>();
		Cursor cursor = database.query(SamoDbHelper.TABLE_CAMPAIGNS,
				null, null, null, null, null, null);

		cursor.moveToFirst();

		SAMoLog.d(TAG + ".getAllCampaigns()", "there are " + cursor.getCount() + " campaigns rows");
		while (!cursor.isAfterLast()) {
			SAMoLog.d("cursor", cursor.toString());
			Campaign campaign = cursorToCampaign(cursor);
			campaigns.add(campaign);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();

		return campaigns;
	}

	public synchronized List<Indicator> getAllIndicators() {
		List<Indicator> indicators = new ArrayList<Indicator>();

		Cursor cursor = database.query(SamoDbHelper.TABLE_INDICATORS,
				null, null, null, null, null, null);
		try {
			cursor.moveToFirst();
			SAMoLog.d(TAG + ".getAllIndicators()", "there are " + cursor.getCount() + " indicators rows");
			while (!cursor.isAfterLast()) {
				Indicator indicator = cursorToIndicator(cursor);
				indicators.add(indicator);
				cursor.moveToNext();
			}
		} finally {
			// Make sure to close the cursor
			if (cursor != null) cursor.close();
		}
		return indicators;
	}

	public synchronized List<Target> getAllTargets() {
		List<Target> targets = new ArrayList<Target>();

		Cursor cursor = database.query(SamoDbHelper.TABLE_TARGETS,
				null, null, null, null, null, null);
		try {
			cursor.moveToFirst();
			SAMoLog.d(TAG + ".getAllTargets()", "there are " + cursor.getCount() + " targets rows");
			while (!cursor.isAfterLast()) {
				Target target = cursorToTarget(cursor);
				targets.add(target);
				cursor.moveToNext();
			}
		} finally {
			// Make sure to close the cursor
			if (cursor != null) cursor.close();
		}
		return targets;
	}

	public void markAssessmentAsUploaded(long assessmentId) {

		// This can be commented
		String[] upColumn = { SamoDbHelper.COLUMN_UPLOADED };
		Cursor cursor = database.query(true, SamoDbHelper.TABLE_ASSESSMENTS, upColumn, SamoDbHelper.COLUMN_ID + "=" + assessmentId, null, null, null, null, null);
		cursor.moveToFirst();
		SAMoLog.d(TAG, "uploaded is " + cursor.getInt(cursor.getPosition()));

		ContentValues values = new ContentValues();
		values.put(SamoDbHelper.COLUMN_UPLOADED, 1);
		database.update(SamoDbHelper.TABLE_ASSESSMENTS, values, SamoDbHelper.COLUMN_ID + "=" + assessmentId, null);

		// This can be commented
		cursor = database.query(true, SamoDbHelper.TABLE_ASSESSMENTS, upColumn, SamoDbHelper.COLUMN_ID + "=" + assessmentId, null, null, null, null, null);
		cursor.moveToFirst();
		SAMoLog.d(TAG, "uploaded is " + cursor.getInt(cursor.getPosition()));
	}

	public void printColumnsOfAssessmentsTable() {

		Cursor c = database.query(SamoDbHelper.TABLE_ASSESSMENTS,
				null, null, null, null, null, null);
		for (int i = 0; i < c.getColumnNames().length; i++) {
			SAMoLog.d(TAG,"column " + i + ": " + c.getColumnNames()[i]);
		}
	}

	public synchronized void resetAll() {
		database.execSQL("DROP TABLE IF EXISTS " + SamoDbHelper.TABLE_INDICATORS);
		database.execSQL(SamoDbHelper.TABLE_INDICATORS_CREATE);
		database.execSQL("DROP TABLE IF EXISTS " + SamoDbHelper.TABLE_TARGETS);
		database.execSQL(SamoDbHelper.TABLE_TARGETS_CREATE);
		database.execSQL("DROP TABLE IF EXISTS " + SamoDbHelper.TABLE_CAMPAIGNS);
		database.execSQL(SamoDbHelper.TABLE_CAMPAIGNS_CREATE);
		resetAssessmentsTable();

	}

	public synchronized void resetAssessmentsTable() {
		database.execSQL("DROP TABLE IF EXISTS " + SamoDbHelper.TABLE_ASSESSMENTS);
		database.execSQL(SamoDbHelper.TABLE_ASSESSMENTS_CREATE);
	}

	public synchronized void updateTargetLocation(Target target) {
		ContentValues values = new ContentValues();
		values.put(SamoDbHelper.COLUMN_LATITUDE, target.getLat());
		values.put(SamoDbHelper.COLUMN_LONGITUDE, target.getLng());
		database.update(SamoDbHelper.TABLE_TARGETS, values, SamoDbHelper.COLUMN_ID + "=" + target.getId(), null);
	}

	private Assessment cursorToAssessment(Cursor cursor) {
		Assessment assessment = new Assessment();
		assessment.setId(cursor.getLong(cursor.getColumnIndex(SamoDbHelper.COLUMN_ID)));
		assessment.setName(cursor.getString(cursor.getColumnIndex(SamoDbHelper.COLUMN_NAME)));
		assessment.setAssessorId(cursor.getLong(cursor.getColumnIndex(SamoDbHelper.COLUMN_ASSESSOR_ID)));
		// TODO 
		assessment.setAssessorName(cursor.getString(cursor.getColumnIndex(SamoDbHelper.COLUMN_ASSESSOR_NAME)));
		assessment.setCampaignId(cursor.getLong(cursor.getColumnIndex(SamoDbHelper.COLUMN_CAMPAIGN_ID)));
		assessment.setTargetId(cursor.getLong(cursor.getColumnIndex(SamoDbHelper.COLUMN_TARGET_ID)));
		assessment.setTargetName(cursor.getString(cursor.getColumnIndex(SamoDbHelper.COLUMN_TARGET_NAME)));
		assessment.setUploaded(cursor.getInt(cursor.getColumnIndex(SamoDbHelper.COLUMN_UPLOADED)) == 1 ? true : false);
		assessment.setDate(cursor.getString(cursor.getColumnIndex(SamoDbHelper.COLUMN_DATE)));
		for (int i = cursor.getColumnIndex(SamoDbHelper.COLUMN_DATE) + 1; i < cursor.getColumnCount(); i++) {
			Indicator indicator = new Indicator();
			String columnName = cursor.getColumnName(i);
			indicator.setName(columnName);
			// I take the type from the column name
			indicator.setType(columnName.split("_")[1]);
			// I take the remId from the column name
			indicator.setId(Long.valueOf(columnName.split("_")[2]));
			indicator.setValue(cursor.getString(i));
			assessment.addIndicator(indicator);
		}
		return assessment;
	}

	private Campaign cursorToCampaign(Cursor cursor) {
		Campaign campaign = new Campaign();
		campaign.setId(cursor.getLong(cursor.getColumnIndex(SamoDbHelper.COLUMN_ID)));
		campaign.setRemId(cursor.getLong(cursor.getColumnIndex(SamoDbHelper.COLUMN_REMOTE_ID)));
		campaign.setDescription(cursor.getString(cursor.getColumnIndex(SamoDbHelper.COLUMN_DESCRIPTION)));
		campaign.setDateFrom(cursor.getString(cursor.getColumnIndex(SamoDbHelper.COLUMN_DATE_FROM)));
		campaign.setDateTo(cursor.getString(cursor.getColumnIndex(SamoDbHelper.COLUMN_DATE_TO)));
		campaign.setTitle(cursor.getString(cursor.getColumnIndex(SamoDbHelper.COLUMN_TITLE)));
		return campaign;
	}

	private Indicator cursorToIndicator(Cursor cursor) {
		Indicator indicator = new Indicator();
		indicator.setId(cursor.getLong(cursor.getColumnIndex(SamoDbHelper.COLUMN_ID)));
		indicator.setName(cursor.getString(cursor.getColumnIndex(SamoDbHelper.COLUMN_NAME)));
		indicator.setType(cursor.getString(cursor.getColumnIndex(SamoDbHelper.COLUMN_TYPE)));
		return indicator;

	}

	private Target cursorToTarget(Cursor cursor) {
		Target target = new Target();
		target.setId(cursor.getLong(cursor.getColumnIndex(SamoDbHelper.COLUMN_ID)));
		target.setName(cursor.getString(cursor.getColumnIndex(SamoDbHelper.COLUMN_NAME)));
		target.setLat(cursor.getDouble(cursor.getColumnIndex(SamoDbHelper.COLUMN_LATITUDE)));
		target.setLng(cursor.getDouble(cursor.getColumnIndex(SamoDbHelper.COLUMN_LONGITUDE)));
		return target;

	}

	private String createColumnName(String name, String type, long id) {
		return name.replaceAll("[^A-Za-z0-9]", "").toLowerCase() + "_" + type + "_" + id;
	}

}
