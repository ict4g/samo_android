package eu.fbk.ict4g.samo.activities;

import java.io.File;

import android.app.Application;
import eu.fbk.ict4g.samo.models.Campaign;
import eu.fbk.ict4g.samo.service.SamoServiceIF;
import eu.fbk.ict4g.samo.service.SamoServiceRESTful;

public class SAMoApp extends Application {

	private static SamoServiceIF service;
	private static Campaign currentCampaign;
	private static File databasePath;
	public static String PACKAGE_NAME;
	
	@Override
	public void onCreate() {
		super.onCreate();
		service = new SamoServiceRESTful(getApplicationContext(), getString(R.string.server_url));
		PACKAGE_NAME = getApplicationContext().getPackageName();
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
