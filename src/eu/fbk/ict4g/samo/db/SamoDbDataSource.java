package eu.fbk.ict4g.samo.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import eu.fbk.ict4g.samo.models.Assessment;
import eu.fbk.ict4g.samo.models.Indicator;
import eu.fbk.ict4g.samo.models.Target;

public class SamoDbDataSource {

	// Database fields
	private SQLiteDatabase database;
	private SamoDbHelper dbHelper;
	private String[] allColumns = { SamoDbHelper.COLUMN_ID,
			SamoDbHelper.COLUMN_NAME };

	private String assessmentMinColumns = 
			SamoDbHelper.COLUMN_ID + ","
					+ SamoDbHelper.COLUMN_NAME;

	public SamoDbDataSource(Context context) {
		
		if (dbHelper == null) dbHelper =  new SamoDbHelper(context);
	}

	public void open() throws SQLException {
		if (database == null) {
			database = dbHelper.getWritableDatabase();
			Log.d(this.getClass().getSimpleName(), "open()");
		}
	}

	public void close() {
		Log.d(this.getClass().getSimpleName(), "close()");
		dbHelper.close();
	}

	public synchronized void addIndicatorColumn(Indicator indicator) {
		//String colName = indicator.getName().replace(" ", "_").replace("?", "").toLowerCase() + "_" + indicator.getId();
		String colName = createColumnName(indicator.getName(), indicator.getType(), indicator.getId());
		String sql = "ALTER TABLE " + SamoDbHelper.TABLE_ASSESSMENTS 
				+ " ADD COLUMN '" + colName + "' text;";
		database.execSQL(sql);
		Log.d("sql:", sql);
		printColumnsOfAssessmentsTable();
	}

	public void addIndicator(Indicator indicator) {
		Log.d(this.getClass().getSimpleName(), "Indicator added deleted with name: " + indicator.getName());
		database.execSQL("ALTER TABLE " + SamoDbHelper.TABLE_INDICATORS + " ADD " + indicator.getName() + " text not null");
	}

	public void addTarget(Target target) {
		Log.d(this.getClass().getSimpleName(), "Target added deleted with name: " + target.getName());
		database.execSQL("ALTER TABLE " + SamoDbHelper.TABLE_TARGETS + " ADD " + target.getName() + " text not null");
	}

	public void createAssessment(Assessment assessment) {

		Cursor c = database.query(SamoDbHelper.TABLE_ASSESSMENTS,
				null, null, null, null, null, null);
		for (int i = 0; i < c.getColumnNames().length; i++) {
			Log.d("column " + i, c.getColumnNames()[i]);
		}

		ContentValues values = new ContentValues();
		values.put(SamoDbHelper.COLUMN_NAME, assessment.getName());
		values.put(SamoDbHelper.COLUMN_ASSESSOR_ID, assessment.getAssessorId());
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
		Log.d("cursor size after having created", cursor.getCount() + "");
		if (cursor.getCount() > 0) {
			Assessment newAssessment = cursorToAssessment(cursor);
			Log.d("newAssessment", newAssessment.toString());
		}
		cursor.close();
		//return newAssessment;
	}

	public synchronized Indicator createIndicator(Indicator indicator) {
		ContentValues values = new ContentValues();
		values.put(SamoDbHelper.COLUMN_NAME, indicator.getName());
		values.put(SamoDbHelper.COLUMN_VALUE, "");
		values.put(SamoDbHelper.COLUMN_TYPE, indicator.getType());
		values.put(SamoDbHelper.COLUMN_REMOTE_ID, indicator.getId());
		long insertId = database.insert(SamoDbHelper.TABLE_INDICATORS, null,
				values);
		Cursor cursor = database.query(SamoDbHelper.TABLE_INDICATORS,
				null, SamoDbHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Indicator newIndicator = cursorToIndicator(cursor);
		cursor.close();
		return newIndicator;
	}

	public synchronized Target createTarget(String name) {
		ContentValues values = new ContentValues();
		values.put(SamoDbHelper.COLUMN_NAME, name);
		long insertId = database.insert(SamoDbHelper.TABLE_TARGETS, null,
				values);
		Cursor cursor = database.query(SamoDbHelper.TABLE_TARGETS,
				allColumns, SamoDbHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Target newTarget = cursorToTarget(cursor);
		cursor.close();
		return newTarget;
	}

	public synchronized void deleteAssessment(Assessment assessment) {
		long id = assessment.getId();
		Log.d(this.getClass().getSimpleName(), "Assessment deleted with id: " + id);
		database.delete(SamoDbHelper.TABLE_ASSESSMENTS, SamoDbHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public synchronized void deleteIndicator(Indicator indicator) {
		long id = indicator.getId();
		Log.d(this.getClass().getSimpleName(), "Indicator deleted with id: " + id);
		database.delete(SamoDbHelper.TABLE_INDICATORS, SamoDbHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public synchronized void deleteTarget(Target target) {
		long id = target.getId();
		Log.d(this.getClass().getSimpleName(), "Target deleted with id: " + id);
		database.delete(SamoDbHelper.TABLE_TARGETS, SamoDbHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public synchronized void deleteAllAssessments() {
		database.delete(SamoDbHelper.TABLE_ASSESSMENTS, null, null);
		Log.w(this.getClass().getSimpleName(), "All Assessments deleted");
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
		Log.d("sql:", sql);
	}

	public synchronized void deleteAllIndicators() {
		database.delete(SamoDbHelper.TABLE_INDICATORS, null, null);
		Log.w(this.getClass().getSimpleName(), "All Indicators deleted");
	}

	public synchronized void deleteAllTargets() {
		database.delete(SamoDbHelper.TABLE_TARGETS, null, null);
		Log.w(this.getClass().getSimpleName(), "All Targets deleted");
	}

	public List<Assessment> getAllAssessments() {
		List<Assessment> assessments = new ArrayList<Assessment>();

		Cursor cursor = database.query(SamoDbHelper.TABLE_ASSESSMENTS,
				null, null, null, null, null, null);

		cursor.moveToFirst();

		Log.d(this.getClass().getSimpleName() + ".getAllAssessments()", "there are " + cursor.getCount() + " assessments rows");
		while (!cursor.isAfterLast()) {
			Log.d("cursor", cursor.toString());
			Assessment assessment = cursorToAssessment(cursor);
			assessments.add(assessment);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return assessments;
	}

	public List<Indicator> getAllIndicators() {
		List<Indicator> indicators = new ArrayList<Indicator>();

		Cursor cursor = database.query(SamoDbHelper.TABLE_INDICATORS,
				null, null, null, null, null, null);

		cursor.moveToFirst();
		Log.d(this.getClass().getSimpleName() + ".getAllIndicators()", "there are " + cursor.getCount() + " indicators rows");
		while (!cursor.isAfterLast()) {
			Indicator indicator = cursorToIndicator(cursor);
			indicators.add(indicator);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return indicators;
	}

	public List<Target> getAllTargets() {
		List<Target> targets = new ArrayList<Target>();

		Cursor cursor = database.query(SamoDbHelper.TABLE_TARGETS,
				null, null, null, null, null, null);

		cursor.moveToFirst();
		Log.d(this.getClass().getSimpleName() + ".getAllTargets()", "there are " + cursor.getCount() + " targets rows");
		while (!cursor.isAfterLast()) {
			Target target = cursorToTarget(cursor);
			targets.add(target);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return targets;
	}
	
	public void markAssessmentAsUploaded(long assessmentId) {
		
		// This can be commented
		String[] upColumn = { SamoDbHelper.COLUMN_UPLOADED };
		Cursor cursor = database.query(true, SamoDbHelper.TABLE_ASSESSMENTS, upColumn, SamoDbHelper.COLUMN_ID + "=" + assessmentId, null, null, null, null, null);
		cursor.moveToFirst();
		Log.d(this.getClass().getSimpleName(), "uploaded is " + cursor.getInt(cursor.getPosition()));
		
		ContentValues values = new ContentValues();
		values.put(SamoDbHelper.COLUMN_UPLOADED, 1);
		database.update(SamoDbHelper.TABLE_ASSESSMENTS, values, SamoDbHelper.COLUMN_ID + "=" + assessmentId, null);

		// This can be commented
		cursor = database.query(true, SamoDbHelper.TABLE_ASSESSMENTS, upColumn, SamoDbHelper.COLUMN_ID + "=" + assessmentId, null, null, null, null, null);
		cursor.moveToFirst();
		Log.d(this.getClass().getSimpleName(), "uploaded is " + cursor.getInt(cursor.getPosition()));
	}

	public void printColumnsOfAssessmentsTable() {

		Cursor c = database.query(SamoDbHelper.TABLE_ASSESSMENTS,
				null, null, null, null, null, null);
		for (int i = 0; i < c.getColumnNames().length; i++) {
			Log.d("column " + i, c.getColumnNames()[i]);
		}
	}
	
	public synchronized void resetAll() {
		database.execSQL("DROP TABLE IF EXISTS " + SamoDbHelper.TABLE_INDICATORS);
		database.execSQL(SamoDbHelper.TABLE_INDICATORS_CREATE);
		database.execSQL("DROP TABLE IF EXISTS " + SamoDbHelper.TABLE_TARGETS);
		database.execSQL(SamoDbHelper.TABLE_TARGETS_CREATE);
		// TODO database.execSQL("DROP TABLE IF EXISTS " + SamoDbHelper.TABLE_CAMPAIGNS);
		// TODO database.execSQL(SamoDbHelper.TABLE_CAMPAIGNS_CREATE);
		resetAssessmentsTable();
		
	}
	
	public synchronized void resetAssessmentsTable() {
		database.execSQL("DROP TABLE IF EXISTS " + SamoDbHelper.TABLE_ASSESSMENTS);
		database.execSQL(SamoDbHelper.TABLE_ASSESSMENTS_CREATE);
	}
	
	private Assessment cursorToAssessment(Cursor cursor) {
		Assessment assessment = new Assessment();
		assessment.setId(cursor.getLong(cursor.getColumnIndex(SamoDbHelper.COLUMN_ID)));
		assessment.setName(cursor.getString(cursor.getColumnIndex(SamoDbHelper.COLUMN_NAME)));
		assessment.setAssessorId(cursor.getLong(cursor.getColumnIndex(SamoDbHelper.COLUMN_ASSESSOR_ID)));
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
		return target;

	}
	
	private String createColumnName(String name, String type, long id) {
		return name.replaceAll("[^A-Za-z0-9]", "").toLowerCase() + "_" + type + "_" + id;
	}

}
