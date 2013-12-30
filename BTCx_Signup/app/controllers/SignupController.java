package controllers;

import play.*;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.*;
import serverLoggers.ServerLoggers;


import java.io.File;
import java.util.*;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.shell.util.json.JSONException;
import org.neo4j.shell.util.json.JSONObject;

import databases.BTCxDatabase;



public class SignupController extends Controller {

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
	
    public static void index() {
        renderTemplate("app/views/webpages/signup.html");
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
    public static String storeUserData(String usrStr) throws JSONException{
    	ServerLoggers.infoLog.info("***Userdata: "+usrStr+"***");
    	JSONObject usrObj = new JSONObject(usrStr);
    	if(BTCxDatabase.USER_INDEX.get("email", usrObj.get("email")).getSingle()==null){
	    	try{
	    		Transaction tx = sDB.beginTx();
	    		userStoredCount++;
	    		ServerLoggers.infoLog.info("***Storing new user -- count: "+userStoredCount+"***");
	    		Node uNode = sDB.createNode();
	    		
	    			uNode.setProperty("firstName", usrObj.get("firstName"));
	    			uNode.setProperty("lastName", usrObj.get("lastName"));
	    			uNode.setProperty("email", usrObj.get("email"));
	    			uNode.setProperty("phoneNumber", usrObj.get("phoneNumber"));
	    			uNode.setProperty("address", usrObj.get("address"));
	    			uNode.setProperty("city", usrObj.get("city"));
	    			uNode.setProperty("state", usrObj.get("state"));
	    			uNode.setProperty("percentChargedByCC", usrObj.get("percentChargedByCC"));
	    		
	    			//Add email to index
	    			BTCxDatabase.USER_INDEX.add(uNode, "email", usrObj.get("email"));	
	    		
	    		tx.success();
	    		return "Thanks for signing up!";
	    	}catch(RuntimeException e){
	    		e.printStackTrace();
	    		ServerLoggers.errorLog.error("!Failed storing user in DB. SignupController.storeUserData!");
	    	}
    	}
    	else{
    		return "We have you in our system. You should be receiving your invite soon!";
    	}
    	return "Error occured- you have not been stored";
    }

}