package eu.fbk.ict4g.samo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import eu.fbk.ict4g.samo.utils.SAMoLog;

public class SamoDbHelper extends SQLiteOpenHelper {

	public static final String TABLE_ASSESSMENTS = "assessment";
	public static final String TABLE_CAMPAIGNS = "campaign";
	public static final String TABLE_INDICATORS = "indicator";
	public static final String TABLE_TARGETS = "target";
	public static final String COLUMN_ASSESSOR_ID = "user";
	public static final String COLUMN_ASSESSOR_NAME = "user_name";
	public static final String COLUMN_CAMPAIGN_ID = "campaign_id";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_DATE_FROM = "campaign_date_from";
	public static final String COLUMN_DATE_TO = "campaign_date_to";
	public static final String COLUMN_DESCRIPTION = "campaign_description";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_REMOTE_ID = "remId";
	public static final String COLUMN_TARGET_ID = "target";
	public static final String COLUMN_TARGET_NAME = "target_name";
	public static final String COLUMN_TITLE = "campaign_title";
	public static final String COLUMN_TYPE = "indicator_type";
	public static final String COLUMN_UPLOADED = "uploaded";
	public static final String COLUMN_VALUE = "value";

	public static final String DATABASE_NAME = "samo.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	public static final String TABLE_ASSESSMENTS_CREATE = "create table "
			+ TABLE_ASSESSMENTS + "( " 
			+ COLUMN_ID + " integer primary key autoincrement, " // 0
			+ COLUMN_NAME + " text not null, " // 1
			+ COLUMN_ASSESSOR_ID + " integer, " // 2
			+ COLUMN_ASSESSOR_NAME + " text, " // 4
			+ COLUMN_CAMPAIGN_ID + " integer, " // 2
			+ COLUMN_TARGET_ID + " integer, " // 3
			+ COLUMN_TARGET_NAME + " text, " // 4
			+ COLUMN_UPLOADED + " integer, " // 5
			+ COLUMN_DATE + " text " // 6
			+ ");";
	public static final String TABLE_CAMPAIGNS_CREATE = "create table "
			+ TABLE_CAMPAIGNS + "( " 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_TITLE + " text not null, "
			+ COLUMN_DESCRIPTION + " text not null, "
			+ COLUMN_DATE_FROM + " text not null, "
			+ COLUMN_DATE_TO + " text not null, "
			+ COLUMN_REMOTE_ID + " integer);";
	
	public static final String TABLE_INDICATORS_CREATE = "create table "
			+ TABLE_INDICATORS + "( " 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_VALUE + " text not null, "
			+ COLUMN_TYPE + " text not null, "
			+ COLUMN_REMOTE_ID + " integer);";
	
	public static final String TABLE_TARGETS_CREATE = "create table "
			+ TABLE_TARGETS + "( " + COLUMN_ID
			+ " integer primary key autoincrement, " 
			+ COLUMN_LATITUDE + " real, "
			+ COLUMN_LONGITUDE + " real, "
			+ COLUMN_NAME + " text not null"
			+ ");";

	public SamoDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_ASSESSMENTS_CREATE);
		db.execSQL(TABLE_CAMPAIGNS_CREATE);
		db.execSQL(TABLE_INDICATORS_CREATE);
		db.execSQL(TABLE_TARGETS_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		SAMoLog.w(SamoDbHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAMPAIGNS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INDICATORS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TARGETS);
		onCreate(db);
	}

}
