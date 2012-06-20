package eu.fbk.ict4g.samo.activities;

import java.io.File;

import android.app.Application;
import eu.fbk.ict4g.samo.models.Campaign;
import eu.fbk.ict4g.samo.service.SamoServiceIF;
import eu.fbk.ict4g.samo.service.SamoServiceRESTful;

public class SAMoApp extends Application {

	public static String PACKAGE_NAME;
	private static SamoServiceIF service;
	private static Campaign currentCampaign;
	private static File databasePath;
	private static boolean userLogged;
	private static long userId;
	private static String userName;
	
	@Override
	public void onCreate() {
		super.onCreate();
		service = new SamoServiceRESTful(getApplicationContext(), getString(R.string.server_url));
		userLogged = false;
		userId = 0;
		userName = "";
		PACKAGE_NAME = getApplicationContext().getPackageName();
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
