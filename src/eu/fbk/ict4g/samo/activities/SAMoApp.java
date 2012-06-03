package eu.fbk.ict4g.samo.activities;

import android.app.Application;
import eu.fbk.ict4g.samo.models.Campaign;
import eu.fbk.ict4g.samo.service.SamoServiceIF;
import eu.fbk.ict4g.samo.service.SamoServiceRESTful;

public class SAMoApp extends Application {

	private static SamoServiceIF service;
	private Campaign currentCampaign;
	
	@Override
	public void onCreate() {
		super.onCreate();
		service = new SamoServiceRESTful(getApplicationContext(), getString(R.string.server_url));
	}

	public static SamoServiceIF getService() {
		return service;
	}

	public Campaign getCurrentCampaign() {
		return currentCampaign;
	}

	public void setCurrentCampaign(Campaign currentCampaign) {
		this.currentCampaign = currentCampaign;
	}

}
