package eu.fbk.ict4g.samo.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import eu.fbk.ict4g.samo.models.Assessment;
import eu.fbk.ict4g.samo.models.Campaign;
import eu.fbk.ict4g.samo.models.Indicator;
import eu.fbk.ict4g.samo.models.Target;
import eu.fbk.ict4g.samo.utils.SAMoConsts;

public class SamoServiceRESTful implements SamoServiceIF {
	
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat timeformat;

	private String serverUrl;
	private HTTPUtils serviceInvoker;
	
	private static final String ASSESSMENTS = "/assessments";
	private static final String ASSESSMENTS_JSON = "/assessments.json";
	private static final String CAMPAIGNS = "/campaigns";
	private static final String CAMPAIGNS_JSON = "/campaigns.json";
	private static final String NEW = "/new.json";
	private static final String TARGETS = "/targets";
	private static final String LOGIN = "/user_sessions";
	private static final String LOGIN_JSON = "/user_sessions.json";
	
	/**
	 * 
	 */
	public SamoServiceRESTful(Context context, String serverUrl) {
		this.serverUrl = serverUrl;
		serviceInvoker = new HTTPUtils(context);
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		timeformat = new SimpleDateFormat("hh:mm:ss");
		
	}

	@Override
	public List<Campaign> getAllCampaigns() throws SamoServiceException {

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		JSONObject jsonResp = serviceInvoker.sendHTTPRequest(serverUrl + CAMPAIGNS_JSON, HTTPUtils.METHOD_GET, nameValuePairs, true);
		if (jsonResp == null) {
			Log.e(SamoServiceRESTful.class.getSimpleName(), "jsonResp is null");
			throw new SamoServiceException("jsonResp is null");
		}
		ArrayList<Campaign> campaigns = new ArrayList<Campaign>();
		try {
			JSONArray jsonArray = jsonResp.getJSONArray(HTTPUtils.ARRAY_TOKEN);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonCampaign = jsonArray.getJSONObject(i);
				Campaign campaign = new Campaign();
				campaign.setId(jsonCampaign.optLong(SAMoConsts.id));
				campaign.setTitle(jsonCampaign.optString(SAMoConsts.title));
				campaign.setDescription(jsonCampaign.optString(SAMoConsts.description));
				campaign.setDateFrom(jsonCampaign.optString(SAMoConsts.dateFrom));
				campaign.setDateTo(jsonCampaign.optString(SAMoConsts.dateTo));
				
				// extract targets from json and build a temporary list of targets
				JSONArray jsonTargets = jsonCampaign.getJSONArray(SAMoConsts.targets);
				ArrayList<Target> tmpTargets =  new ArrayList<Target>();
				for (int j = 0; j < jsonTargets.length(); j++) {
					tmpTargets.add(jsonObjToTarget(jsonTargets.getJSONObject(j)));
				}				
				// put targets in campaign
				campaign.setTargets(tmpTargets);
				
				// extract indicators from json and build a temporary list of indicators
				JSONArray jsonIndicators = jsonCampaign.getJSONArray(SAMoConsts.indicators);
				ArrayList<Indicator> tmpIndicators = new ArrayList<Indicator>();
				for (int j = 0; j < jsonIndicators.length(); j++) {
					tmpIndicators.add(jsonObjToIndicator(jsonIndicators.getJSONObject(j)));
				}
				//put indicators in campaigns
				campaign.setIndicators(tmpIndicators);
				
				campaigns.add(campaign);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new SamoServiceException(e);
		}
		return campaigns;

	}

	@Override
	public void getCampaign(long campaignId) throws SamoServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void getIndicators(long campaignId) throws SamoServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void getTargets(long campaignId) throws SamoServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void login(String username, String password) throws SamoServiceException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("user_sessions[email]", username));
		nameValuePairs.add(new BasicNameValuePair("user_sessions[password]", password));
//		serviceInvoker.sendHTTPRequest(serverUrl + LOGIN + "/", HTTPUtils.METHOD_POST, nameValuePairs, false);
		JSONObject jsonObject =  new JSONObject();
		try {
			jsonObject.put("user_sessions[email]", username);
			jsonObject.put("user_sessions[password]", password);
			serviceInvoker.sendHTTPRequestPOST(serverUrl + LOGIN_JSON, jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new SamoServiceException(e);
		}
	}

	@Override
	public void publishAllAssessments() throws SamoServiceException {
		JSONObject jsonObject =  new JSONObject();
		JSONArray jsonArray =  new JSONArray();
		for (int i = 0; i < 5; i++) {
			jsonArray.put(createJsonAssessment());
		}
		try {
			jsonObject.put("assessments", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new SamoServiceException(e);
		}
		serviceInvoker.sendHTTPRequestPOST(serverUrl + CAMPAIGNS + "/1" + ASSESSMENTS + "/batch_create.json", jsonObject);
		
	}

	@Override
	public void publishAssessment(Assessment assessment) throws SamoServiceException {
		try {
			
			JSONObject jsonObject =  new JSONObject();
			if (assessment == null) 
				jsonObject.put("assessment", createJsonAssessment());
			else
				jsonObject.put("assessment", assessmentToJsonObject(assessment));
			Log.d("jsonAssessment", jsonObject.toString());
			serviceInvoker.sendHTTPRequestPOST(serverUrl + CAMPAIGNS + "/1" + ASSESSMENTS_JSON, jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new SamoServiceException(e);
		}
		

	}
	
	private JSONObject assessmentToJsonObject (Assessment assessment) {
		JSONObject jsonAssessment = new JSONObject();
		try {
			jsonAssessment.put("target_id", assessment.getTargetId());
			jsonAssessment.put("user_id", assessment.getAssessorId());
			jsonAssessment.put("date", assessment.getDate());
			jsonAssessment.put("finalized", true);
			
			JSONObject jsonIndicator; // reusable JSONObject
			JSONArray jsonArray = new JSONArray();
			
			for (Indicator indicator : assessment.getIndicators()) {
				jsonIndicator =  new JSONObject();
				jsonIndicator.put("indicator_id", indicator.getId());
				jsonIndicator.put("value", indicator.getValue());
				jsonArray.put(jsonIndicator);
			}
			
//			JSONObject jsonObjectArray = new JSONObject();
//			for (int i = 1; i <= 3; i++) {
//				jsonIndicator =  new JSONObject();
//				jsonIndicator.put("indicator_id", i);
//				jsonIndicator.put("value", i+100);
//				jsonObjectArray.put("" + (i-1), jsonIndicator);				
//			}
			jsonAssessment.put("answers", jsonArray);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonAssessment;
		
	}
	
	private JSONObject createJsonAssessment() {
		JSONObject jsonAssessment = new JSONObject();
		try {
			jsonAssessment.put("target", 1);
			jsonAssessment.put("user", 1);
			jsonAssessment.put("date", "2012-06-01");
			jsonAssessment.put("finalized", true);
			JSONObject jsonIndicator;
			JSONArray jsonArray = new JSONArray();
			for (int i = 1; i <= 3; i++) {
				jsonIndicator =  new JSONObject();
				jsonIndicator.put("indicator_id", i);
				jsonIndicator.put("value", i+100);
				jsonArray.put(jsonIndicator);				
			}
			
//			JSONObject jsonObjectArray = new JSONObject();
//			for (int i = 1; i <= 3; i++) {
//				jsonIndicator =  new JSONObject();
//				jsonIndicator.put("indicator_id", i);
//				jsonIndicator.put("value", i+100);
//				jsonObjectArray.put("" + (i-1), jsonIndicator);				
//			}
			jsonAssessment.put("answers", jsonArray);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonAssessment;
	}
	private Indicator jsonObjToIndicator(JSONObject jsonObject) {
		Indicator indicator = new Indicator();
		indicator.setId(jsonObject.optLong(SAMoConsts.id));
		indicator.setName(jsonObject.optString(SAMoConsts.name));
		indicator.setType(jsonObject.optString(SAMoConsts.indicator_type));
		indicator.setValue(jsonObject.optString(SAMoConsts.value));
		return indicator;
	}
	
	private Target jsonObjToTarget (JSONObject jsonObject) {
		Target target = new Target();
		target.setId(jsonObject.optLong(SAMoConsts.id));
		target.setName(jsonObject.optString(SAMoConsts.name));
		target.setStreet(jsonObject.optString(SAMoConsts.street));
		target.setCity(jsonObject.optString(SAMoConsts.city));
		target.setZip(jsonObject.optString(SAMoConsts.zip));
		target.setState(jsonObject.optString(SAMoConsts.state));
		target.setCountry(jsonObject.optString(SAMoConsts.country));
		target.setLatE6((int) (jsonObject.optDouble(SAMoConsts.latitude) * 1E6));
		target.setLngE6((int) (jsonObject.optDouble(SAMoConsts.longitude) * 1E6));
		return target;
	}

}
