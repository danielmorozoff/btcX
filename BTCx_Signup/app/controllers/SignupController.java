package controllers;

import play.*;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.*;
import serverLoggers.ServerLoggers;
import signup.Verification;


import java.io.File;
import java.util.*;

import jsonFormaters.SignupFormatter;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.shell.util.json.JSONException;
import org.neo4j.shell.util.json.JSONObject;
import org.neo4j.shell.util.json.JSONArray;

import api.WebpageAPI;

import databases.BTCxDatabase;



public class SignupController extends Controller {
/************************************************************************************************/
//Static values for server
	public static String system_name = "";
	public static String system_url = "";
	public static String system_email = "";
	
	public static final String DB_LOCATION ="Databases/BTCx-Signup-Database";
	
	
	
	
	
/************************************************************************************************/	
	/**
	 * Startup code fo the server. Log instantiation, user file directories and DB activation.
	 * @author danielmorozoff
	 *
	 */
		@OnApplicationStart
		public class startupCode extends Job {
		    public void doJob() {
		    	ServerLoggers.infoLog.info("SIGNUP SERVER HAS STARTED");
		    	new BTCxDatabase();
		    	ServerLoggers.infoLog.info("SIGNUP DB STARTED");
		    	//Add Shutdown hool
		    	Runtime.getRuntime().addShutdownHook( new Thread()
			    {
			        @Override
			        public void run()
			        {
			        	BTCxDatabase.signupDB.shutdown();
			        }
			    });
		    }    
		}
		
	public static GraphDatabaseService sDB = BTCxDatabase.signupDB;
	public static long userStoredCount = 0;
	
	/**
	 * Method routes the request to the proper page
	 * btc-map.com --> map page
	 * cryptrex.com --> default cryptrex page
	 */
	
//	@Before
//	public static void route(){
//		switch(request.domain){
//			case "cryptrex.com":
//				index();
//			break;
//			case "btc-map.com":
//				map();
//			break;
//			default:
//				index();
//			break;
//		}
//	}
	
    public static void index() {
        renderTemplate("app/views/webpages/signup.html");
    }
    public static void map() {
        renderTemplate("app/views/webpages/map.html");
    }
    /**
     * Signup User for future usage of the system
     * Properties stored:
     * 
     * firstName - String
     * lastName - String
     * email - String
     * phoneNumber - String
     * address - String
     * city - String
     * state - String
     * percentChargedByCC -  double
     * 
     * @param usrStr
     * @return
     * @throws JSONException
     */
    public static JSONObject storeUserData(String usrStr) throws JSONException{
    	ServerLoggers.infoLog.info("***Userdata: "+usrStr+"***");
    	
    	JSONObject usrObj = new JSONObject(usrStr);
    	SignupFormatter sFormatter = new SignupFormatter();
    	if(new Verification(usrObj).verifySignupObject())
    	{
	    	if(BTCxDatabase.USER_INDEX.get("email", usrObj.get("email")).getSingle()==null)
	    	{
		    	try
		    	{
		    		Transaction tx = sDB.beginTx();
		    		userStoredCount++;
		    		ServerLoggers.infoLog.info("***Storing new user -- count: "+userStoredCount+"***");
		    		Node uNode = sDB.createNode();
		    		
		    		String privId = UUID.randomUUID().toString();
		    		String pubId = UUID.randomUUID().toString();
//		    		Create ids for node
		    		uNode.setProperty("uniquePrivateId",privId );
		    		uNode.setProperty("uniquePublicId", pubId);
//		    		Add ids to index
		    		BTCxDatabase.USER_INDEX.add(uNode, "privateId", privId);
		    		BTCxDatabase.USER_INDEX.add(uNode, "publicId", pubId);
		    		
		    		String type= (String) usrObj.get("type"); 
		    		uNode.setProperty("type",type );
	    			uNode.setProperty("firstName", usrObj.get("firstName"));
	    			uNode.setProperty("email", usrObj.get("email"));

	    			if(type.equals("merchant"))
	    			{
		    			uNode.setProperty("lastName", usrObj.get("lastName"));
		    			uNode.setProperty("phoneNumber", usrObj.get("phoneNumber"));
		    			uNode.setProperty("address", usrObj.get("address"));
		    			uNode.setProperty("city", usrObj.get("city"));
		    			uNode.setProperty("state", usrObj.get("state"));

//			    		Convert to geo coordinates using google maps api
		    			String fullAddress =  usrObj.get("address") +" "+ usrObj.get("city")+" "+usrObj.get("state");
		    			fullAddress = fullAddress.replaceAll(" ", "%20");
		    			JSONObject coordsObj = new JSONObject((String) new WebpageAPI(null).sendRequestToDataHub("http://maps.googleapis.com/maps/api/geocode/json?address="+fullAddress+"&sensor=true", "GET", "string", null));

		    			JSONArray results = (JSONArray) coordsObj.get("results");
		    			JSONObject result = (JSONObject) results.get(0);
		    			String geoCoordsStr = ((JSONObject)result.get("geometry")).get("location").toString();
		    			uNode.setProperty("geoCoords", geoCoordsStr);

		    			uNode.setProperty("storeName", usrObj.get("storeName"));
		    			uNode.setProperty("storeDescription", usrObj.get("storeDescription"));
		    			uNode.setProperty("acceptsBTC", (Boolean) usrObj.get("acceptance"));
		    			uNode.setProperty("acceptsTerms", (Boolean) usrObj.get("agreement"));
			    			
			    			
//			    		Add Geo coords if store
			    		BTCxDatabase.USER_INDEX.add(uNode, "geoCoords", geoCoordsStr);
//			    		Add label Merchant 
		    			Label merchantLabel = DynamicLabel.label("Merchant");
		    			uNode.addLabel(merchantLabel);
			    			
	    			}
	    			else
	    			{
//		    			Add trader label -- traders are not merchants
		    			Label traderLabel = DynamicLabel.label("Trader");
		    			uNode.addLabel(traderLabel);
	    			}
		    			
//		    		Add email to index for all users.
	    			BTCxDatabase.USER_INDEX.add(uNode, "email", usrObj.get("email"));
		    			
		    		
		    		tx.success();
		    		sFormatter.login = true;
		    		sFormatter.message = "Thank you for signing up!";
		    	}
		    	catch(RuntimeException e)
		    	{
		    		sFormatter.login = false;
		    		sFormatter.message = "We are sorry try again later.";
		    		e.printStackTrace();
		    		ServerLoggers.errorLog.error("!Failed storing user in DB. SignupController.storeUserData!");
		    	}
	    	}
	    	else{
	    		    sFormatter.login = true;
	    		    sFormatter.message = "You have already registered";
	    	}
    	}
    	else
    	{
    		sFormatter.login = false;
    		sFormatter.message = "Invalid input.";
    	}	
    	return sFormatter.loadSignupFormatter();
    }

}