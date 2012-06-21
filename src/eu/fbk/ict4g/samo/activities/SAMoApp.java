package eu.fbk.ict4g.samo.activities;

import java.io.File;

import android.app.Application;
import android.content.SharedPreferences;
import eu.fbk.ict4g.samo.models.Campaign;
import eu.fbk.ict4g.samo.service.SamoServiceIF;
import eu.fbk.ict4g.samo.service.SamoServiceRESTful;

public class SAMoApp extends Application {

	public static String PACKAGE_NAME;
	
	private static final String SAMO_PREFS = "SAMO_PREFS";
	private static final String USER_NAME_KEY = "USER_NAME_KEY";
	private static final String USER_EMAIL_KEY = "USER_EMAIL_KEY";
	
	private static SamoServiceIF service;
	private static Campaign currentCampaign;
	private static File databasePath;
	
	private static SharedPreferences prefs;
	
	private static boolean userLogged;
	private static long userId;
	private static String userName;
	private static String userEmail;
	
	@Override
	public void onCreate() {
		super.onCreate();
		service = new SamoServiceRESTful(getApplicationContext(), getString(R.string.server_url));
		userLogged = false;
		userId = 0;
//		userName = "";
		PACKAGE_NAME = getApplicationContext().getPackageName();
		
		prefs = getSharedPreferences(SAMO_PREFS, MODE_PRIVATE);
		
		userName = prefs.getString(USER_NAME_KEY, "");
		userEmail = prefs.getString(USER_EMAIL_KEY, "");
		
	}
	
	public static boolean isUserLogged() {
		return userLogged;
	}

	public static void setUserLogged(boolean userLogged) {
		SAMoApp.userLogged = userLogged;
	}

	public static long getUserId() {
		return userId;
	}

	public static void setUserId(long userId) {
		SAMoApp.userId = userId;
	}

	public static String getUserName() {
		return userName;
	}

	public static void setUserName(String userName) {
		SAMoApp.userName = userName;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(USER_NAME_KEY, userName);
		editor.commit();
	}

	public static String getUserEmail() {
		return userEmail;
	}

	public static void setUserEmail(String userEmail) {
		SAMoApp.userEmail = userEmail;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(USER_EMAIL_KEY, userEmail);
		editor.commit();
	}

	public static SamoServiceIF getService() {
		return service;
	}

	public static Campaign getCurrentCampaign() {
		return currentCampaign;
	}

	public static void setCurrentCampaign(Campaign currentCampaign) {
		SAMoApp.currentCampaign = currentCampaign;
	}

	public static File getDatabasePath() {
		return databasePath;
	}

	public static void setDatabasePath(File databasePath) {
		SAMoApp.databasePath = databasePath;
	}

}
