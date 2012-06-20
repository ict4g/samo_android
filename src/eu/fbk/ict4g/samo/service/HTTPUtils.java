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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import eu.fbk.ict4g.samo.utils.SAMoConsts;
import eu.fbk.ict4g.samo.utils.SAMoLog;

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
		SAMoLog.d(this.getClass().getSimpleName(), url);
		try {
			SAMoLog.d(this.getClass().getSimpleName(), "Http" + method + " params: " + nameValuePairs.toString());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String respStr = "";
			if (method.equalsIgnoreCase(METHOD_GET)) { 
				// HTTP GET
				HttpGet httpGet =  new HttpGet(url + 
						(nameValuePairs.isEmpty() ? "" : "/" + nameValuePairs.get(0).getValue()));
				//httpGet.setHeader("Content-Type", "application/json");
				//httpGet.setHeader("Accepts", "application/json");
				SAMoLog.d(this.getClass().getSimpleName(), httpGet.getURI().toString());
				// execute request and get response
//				responseGet = httpClient.execute(httpGet, responseHandler, httpContext);
				HttpResponse response = httpClient.execute(httpGet, httpContext);
				HttpEntity entity = response.getEntity();
	            for (int i = 0; i < response.getAllHeaders().length; i++) {
					org.apache.http.Header header = response.getAllHeaders()[i];
					SAMoLog.d(this.getClass().getSimpleName(), header.getName() + " " + header.getValue());
				}
	            SAMoLog.d(this.getClass().getSimpleName(), "----------------------------------------");
	            SAMoLog.d(this.getClass().getSimpleName(), response.getStatusLine().toString());
	            if (entity != null) {
	            	SAMoLog.d(this.getClass().getSimpleName(), "Response content length: " + entity.getContentLength());
	                BufferedReader r = new BufferedReader(new InputStreamReader(entity.getContent()));
	                StringBuilder total = new StringBuilder();
	                String line;
	                while ((line = r.readLine()) != null) {
	                    total.append(line);
	                }
	                SAMoLog.d(this.getClass().getSimpleName(), total.toString());
	                respStr = total.toString();
	            }
			} else if (method.equalsIgnoreCase(METHOD_DELETE)) { 
				// HTTP DELETE
				HttpDelete httpDelete =  new HttpDelete(url + 
						(nameValuePairs.isEmpty() ? "" : "/" + nameValuePairs.get(0).getValue()));
				Log.d("HTTPRequest", httpDelete.getURI().toString());		
				// execute request and get response					
				respStr = httpClient.execute(httpDelete, responseHandler, httpContext);
			} else if (method.equalsIgnoreCase(METHOD_PUT)) { 
				// HTTP PUT
				HttpPut httpPut = new HttpPut(url);
				httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				Log.d("HTTPRequest", httpPut.getURI().toString());			
				// execute request and get response		
				respStr = httpClient.execute(httpPut, responseHandler, httpContext);
			} else { 
				// HTTP POST - default for php client
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
				Log.d("HTTPRequest", httpPost.getURI().toString());		
				// execute request and get response
//				respStr = httpClient.execute(httpPost, responseHandler, httpContext);
				
				HttpResponse response = httpClient.execute(httpPost, httpContext);
				HttpEntity entity = response.getEntity();
	            for (int i = 0; i < response.getAllHeaders().length; i++) {
					org.apache.http.Header header = response.getAllHeaders()[i];
					SAMoLog.d(this.getClass().getSimpleName(), header.getName() + " " + header.getValue());
				}
	            SAMoLog.d(this.getClass().getSimpleName(), "----------------------------------------");
	            SAMoLog.d(this.getClass().getSimpleName(), response.getStatusLine().toString());
	            if (entity != null) {
	            	SAMoLog.d(this.getClass().getSimpleName(), "Response content length: " + entity.getContentLength());
	                BufferedReader r = new BufferedReader(new InputStreamReader(entity.getContent()));
	                StringBuilder total = new StringBuilder();
	                String line;
	                while ((line = r.readLine()) != null) {
	                    total.append(line);
	                }
	                SAMoLog.d(this.getClass().getSimpleName(), total.toString());
	                respStr = total.toString();
	            }
			}
			
			// This replacement deletes the non-unicode characters that may break the JSON encapsulation
			//Log.d("JSON response before replacing", responseGet);
			respStr = respStr.replaceAll("\\p{C}", "");
			Log.d("JSON response after replacing", respStr);
			if (isArray) {
				JSONArray jsonArray = new JSONArray(respStr);
				result = new JSONObject();
				result.put(ARRAY_TOKEN, jsonArray);
			} else 		
				result = new JSONObject(respStr);
			
		} catch (IOException e) {
			throw new SamoServiceException(e);
		} catch (Exception e) {
			throw new SamoServiceException(e);
		}

		return result;

	}
	
	public synchronized void auth(String url) {
//		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(
                    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials("pbmolini@fbk.eu", "12345"));
			
			
			httpClient.setCredentialsProvider(credentialsProvider);
            HttpPost httpget = new HttpPost(url);

            SAMoLog.d(this.getClass().getSimpleName(), "executing request" + httpget.getRequestLine());
//            HttpResponse response = httpClient.execute(httpget);
            HttpResponse response = httpClient.execute(httpget, httpContext);
            HttpEntity entity = response.getEntity();
            for (int i = 0; i < response.getAllHeaders().length; i++) {
				org.apache.http.Header header = response.getAllHeaders()[i];
				SAMoLog.d(this.getClass().getSimpleName(), header.getName() + " " + header.getValue());
			}
            SAMoLog.d(this.getClass().getSimpleName(), "----------------------------------------");
            SAMoLog.d(this.getClass().getSimpleName(), response.getStatusLine().toString());
            BasicCookieStore cs = (BasicCookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);
            for (Cookie cookie : cs.getCookies()) {
            	SAMoLog.d(this.getClass().getSimpleName(), cookie.getValue());
			}
            if (entity != null) {
            	SAMoLog.d(this.getClass().getSimpleName(), "Response content length: " + entity.getContentLength());
                BufferedReader r = new BufferedReader(new InputStreamReader(entity.getContent()));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                SAMoLog.d(this.getClass().getSimpleName(), total.toString());
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
	
	public synchronized JSONObject sendHTTPRequestPOST(String url, JSONObject jsonObject) throws SamoServiceException {
		JSONObject result = null;
		SAMoLog.d(this.getClass().getSimpleName(), url);
		HttpPost httpPost = new HttpPost(url);
//		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try {
			String respStr = "";
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Accepts", "application/json");
			httpPost.setEntity(new StringEntity(jsonObject.toString()));
			SAMoLog.d(this.getClass().getSimpleName(), httpPost.getRequestLine().toString() + jsonObject.toString(1));
//			responsePost = httpClient.execute(httpPost, responseHandler, httpContext);
//			HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {
//			    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
//			        AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
//			        CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
//			                ClientContext.CREDS_PROVIDER);
//			        HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
//			        
//			        if (authState.getAuthScheme() == null) {
//			            AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
//			            Credentials creds = credsProvider.getCredentials(authScope);
//			            if (creds != null) {
//			                authState.setAuthScheme(new BasicScheme());
//			                authState.setCredentials(creds);
//			            }
//			        }
//			    }    
//			};
//			httpClient.addRequestInterceptor(preemptiveAuth, 0);
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			HttpEntity entity = response.getEntity();
            for (int i = 0; i < response.getAllHeaders().length; i++) {
				org.apache.http.Header header = response.getAllHeaders()[i];
				SAMoLog.d(this.getClass().getSimpleName(), header.getName() + " " + header.getValue());
			}
            SAMoLog.d(this.getClass().getSimpleName(), "----------------------------------------");
            SAMoLog.d(this.getClass().getSimpleName(), response.getStatusLine().toString());
            if (entity != null) {
            	SAMoLog.d(this.getClass().getSimpleName(), "Response content length: " + entity.getContentLength());
                BufferedReader r = new BufferedReader(new InputStreamReader(entity.getContent()));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                SAMoLog.d(this.getClass().getSimpleName(), total.toString());
                respStr = total.toString();
                
            }
			respStr = respStr.replaceAll("\\p{C}", "");
			Log.d("JSON response after replacing", respStr);
			result = new JSONObject(respStr);
			if (!result.optBoolean(SAMoConsts.success))
				throw new SamoServiceException(result.optString(SAMoConsts.message));
			
		} catch (UnsupportedEncodingException e) {
			throw new SamoServiceException(e);
		} catch (ClientProtocolException e) {
			throw new SamoServiceException(e);
		} catch (IOException e) {
			throw new SamoServiceException(e);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void clearCache() {
		cookieStore.clear();
	}
}
