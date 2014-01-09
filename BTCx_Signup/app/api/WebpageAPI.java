package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



import org.apache.commons.validator.routines.UrlValidator;
import org.neo4j.shell.util.json.JSONArray;
import org.neo4j.shell.util.json.JSONException;
import org.neo4j.shell.util.json.JSONObject;

public class WebpageAPI {
	
	private String accessCode;
	
	public WebpageAPI(String accessCode){
		this.accessCode = accessCode;
	}
	/**
	 * Send Request to Backend Server for data, handles on GET and POST requests.
	 * Parameters may be null;
	 * @param requestURL
	 * @param type
	 * @param parametersToSend
	 * @return
	 */
	public Object sendRequestToDataHub(String requestURL, String reqType, String responseType, String parametersToSend){
		if((reqType.equals("GET")||reqType.equals("POST"))&&(requestURL!=null || new UrlValidator().isValid(requestURL)) ){
			HttpURLConnection connection = null;
		    try {
		    	URL url = new URL(requestURL);
		    	connection = (HttpURLConnection)url.openConnection();
		    	connection.setRequestMethod(reqType);
		    	if(accessCode!=null)connection.addRequestProperty("authenticationCode", accessCode);
		    	if(parametersToSend!=null)connection.addRequestProperty("parameters", parametersToSend);
		    	connection.connect();
	
		    	//int code = connection.getResponseCode();
		    	
		    	if(responseType.equals("string")){
		    		return readStringFromInputStream(connection.getInputStream());
		    	}

		    		
		    } catch (MalformedURLException e1) {
		        e1.printStackTrace();
		    } catch (IOException e1) {
		        e1.printStackTrace();
		    } finally {
		        if(null != connection) { connection.disconnect(); }
		    }
		}
		return null;
	}
	
	/**
	 * Parses an inputstream into a string, used to retrieve a string object from the server.
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private String readStringFromInputStream(InputStream is) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String conLine;
		StringBuffer sb = new StringBuffer();
		while ((conLine = br.readLine())!=null){
			sb.append(conLine);
		}
		br.close();
		return sb.toString();
	}
}
