package eu.fbk.ict4g.samo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
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

	DefaultHttpClient httpClient;
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
		httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		
		// Session management
		cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	public synchronized JSONObject sendHTTPRequest(String url, String method, List<NameValuePair> nameValuePairs, boolean isArray) throws SamoServiceException {

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
				//httpGet.setHeader("Content-Type", "application/json");
				//httpGet.setHeader("Accepts", "application/json");
				Log.d("HTTPRequest", httpGet.getURI().toString());
				BasicCookieStore cs = (BasicCookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);
	            for (Cookie cookie : cs.getCookies()) {
	            	System.out.println("Cookie in cookieStore: " + cookie.getValue());
				}
				// execute request and get response
//				responseGet = httpClient.execute(httpGet, responseHandler, httpContext);
				HttpResponse response = httpClient.execute(httpGet, httpContext);
				HttpEntity entity = response.getEntity();
	            for (int i = 0; i < response.getAllHeaders().length; i++) {
					org.apache.http.Header header = response.getAllHeaders()[i];
					System.out.println(header.getName() + " " + header.getValue());
				}
	            System.out.println("----------------------------------------");
	            System.out.println(response.getStatusLine());
	            if (entity != null) {
	                System.out.println("Response content length: " + entity.getContentLength());
	                BufferedReader r = new BufferedReader(new InputStreamReader(entity.getContent()));
	                StringBuilder total = new StringBuilder();
	                String line;
	                while ((line = r.readLine()) != null) {
	                    total.append(line);
	                }
	                System.out.println(total);
	                responseGet = total.toString();
	            }
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
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
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
			throw new SamoServiceException(e);
		} catch (Exception e) {
			throw new SamoServiceException(e);
		}

		return result;

	}
	
	public synchronized void auth(String url) {
		try {
			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(
                    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials("pbmoini@fbk.eu", "12345"));
			httpClient.setCredentialsProvider(credentialsProvider);
            HttpGet httpget = new HttpGet(url);

            System.out.println("executing request" + httpget.getRequestLine());
//            HttpResponse response = httpClient.execute(httpget);
            HttpResponse response = httpClient.execute(httpget, httpContext);
            HttpEntity entity = response.getEntity();
            for (int i = 0; i < response.getAllHeaders().length; i++) {
				org.apache.http.Header header = response.getAllHeaders()[i];
				System.out.println(header.getName() + " " + header.getValue());
			}
            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            BasicCookieStore cs = (BasicCookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);
            for (Cookie cookie : cs.getCookies()) {
            	System.out.println(cookie.getValue());
			}
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
                BufferedReader r = new BufferedReader(new InputStreamReader(entity.getContent()));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                System.out.println(total);
            }
            entity.consumeContent();
        } catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
//        	httpClient.getConnectionManager().shutdown();
        }
    }

	
	public synchronized void sendHTTPRequestPOST(String url, JSONObject jsonObject) throws SamoServiceException {
		Log.d(this.getClass().getName(), url);
		HttpPost httpPost = new HttpPost(url);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responsePost = "";
		try {
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Accepts", "application/json");
			httpPost.setEntity(new StringEntity(jsonObject.toString()));
			Log.d(this.getClass().getName(), httpPost.toString());
			responsePost = httpClient.execute(httpPost, responseHandler, httpContext);
			Log.w("POST response", responsePost);
		} catch (UnsupportedEncodingException e) {
			throw new SamoServiceException(e);
		} catch (ClientProtocolException e) {
			throw new SamoServiceException(e);
		} catch (IOException e) {
			throw new SamoServiceException(e);
		}
		
	}
	
	public void clearCache() {
		((BasicCookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE)).clear();
	}
}
