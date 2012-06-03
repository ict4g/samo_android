package eu.fbk.ict4g.samo.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * @author pietro
 *
 */
public class HTTPUtils {
	
	Context mContext;

	HttpClient httpClient;
	CookieStore cookieStore;
	HttpContext httpContext;
	
	final int CONNECTION_TIMEOUT = 10000;
	final int SO_TIMEOUT = 10000;
	
	public static final String METHOD_GET =  "GET";
	public static final String METHOD_DELETE =  "DELETE";
	public static final String METHOD_PUT =  "PUT";
	public static final String METHOD_POST =  "POST";
	public static final String ARRAY_TOKEN = "array";

	/**
	 * 
	 */
	public HTTPUtils(Context context) {
		this.mContext = context;
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		httpClient = new DefaultHttpClient(httpParams);
		
		// Session management
		cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	public synchronized JSONObject sendHTTPRequest(String url, String method, List<NameValuePair> nameValuePairs, boolean isArray) {

		JSONObject result = null;
		Log.d(this.getClass().getName(), url);
		try {
			Log.d("Http" + method + " params", nameValuePairs.toString());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseGet = "";
			if (method.equalsIgnoreCase(METHOD_GET)) { 
				// HTTP GET
				HttpGet httpGet =  new HttpGet(url + 
						(nameValuePairs.isEmpty() ? "" : "/" + nameValuePairs.get(0).getValue()));
				Log.d("HTTPRequest", httpGet.getURI().toString());	
				// execute request and get response
				responseGet = httpClient.execute(httpGet, responseHandler, httpContext);
			} else if (method.equalsIgnoreCase(METHOD_DELETE)) { 
				// HTTP DELETE
				HttpDelete httpDelete =  new HttpDelete(url + 
						(nameValuePairs.isEmpty() ? "" : "/" + nameValuePairs.get(0).getValue()));
				Log.d("HTTPRequest", httpDelete.getURI().toString());		
				// execute request and get response					
				responseGet = httpClient.execute(httpDelete, responseHandler, httpContext);
			} else if (method.equalsIgnoreCase(METHOD_PUT)) { 
				// HTTP PUT
				HttpPut httpPut = new HttpPut(url);
				httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				Log.d("HTTPRequest", httpPut.getURI().toString());			
				// execute request and get response		
				responseGet = httpClient.execute(httpPut, responseHandler, httpContext);
			} else { 
				// HTTP POST - default for php client
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				Log.d("HTTPRequest", httpPost.getURI().toString());		
				// execute request and get response
				responseGet = httpClient.execute(httpPost, responseHandler, httpContext);
			}
			// This replacement deletes the non-unicode characters that may break the JSON encapsulation
			//Log.d("JSON response before replacing", responseGet);
			responseGet = responseGet.replaceAll("\\p{C}", "");
			Log.d("JSON response after replacing", responseGet);
			if (isArray) {
				JSONArray jsonArray = new JSONArray(responseGet);
				result = new JSONObject();
				result.put(ARRAY_TOKEN, jsonArray);
			} else 		
				result = new JSONObject(responseGet);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}
	
	public synchronized void sendHTTPRequestPOST(String url, JSONObject jsonObject) {
		Log.d(this.getClass().getName(), url);
		HttpPost httpPost = new HttpPost(url);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responsePost = "";
		try {
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Accepts", "application/json");
			httpPost.setEntity(new StringEntity(jsonObject.toString()));
			responsePost = httpClient.execute(httpPost, responseHandler, httpContext);
			Log.w("POST response", responsePost);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void clearCache() {
		cookieStore.clear();
	}
}
