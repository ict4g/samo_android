package eu.fbk.ict4g.samo.service;

import java.util.List;

import eu.fbk.ict4g.samo.models.Assessment;
import eu.fbk.ict4g.samo.models.Campaign;

public interface SamoServiceIF {

	public List<Campaign> getAllCampaigns() throws SamoServiceException;
	
	public void getCampaign(long campaignId) throws SamoServiceException;
	
	public void getIndicators(long campaignId) throws SamoServiceException;
	
	public void getTargets(long campaignId) throws SamoServiceException;
	
	public void publishAllAssessments() throws SamoServiceException;
	
	public void publishAssessment(Assessment assessment) throws SamoServiceException;

}
