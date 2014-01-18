package controllers;
import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import databases.BTCxDatabase;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.Before;
import play.mvc.Controller;
import serverLoggers.ServerLoggers;

public class MainSystemController extends Controller {
/************************************************************************************************/
//Static values for server
	public static String system_name = "CryptRex";
	public static String system_url = "http://localhost:9000"; //http://cryptrex.com
	public static String system_email = "info@cryptrex.com";
	
/************************************************************************************************/	
/**
 * Startup code fo the server. Log instantiation, user file directories and DB activation.
 * @author danielmorozoff
 *
 */
	@OnApplicationStart
	public class startupCode extends Job {
	    public void doJob() {
	    	//Start Log4j Logs
	    	//Logger.log4j.getLogger("INFOLOG").info("SERVER HAS STARTED");
	    	ServerLoggers.infoLog.info("SERVER HAS STARTED");
	    	//load User DB.
	    	new BTCxDatabase();
	    	//Create User file Stores
	    	File userImageStore = new File("UserFiles/");
	    	if(!userImageStore.exists()){
	    		System.out.println("Making User file Stores...");
	    		userImageStore.mkdirs();
	    	}
	    	
	    	//Add Shutdown hool
	    	Runtime.getRuntime().addShutdownHook( new Thread()
		    {
		        @Override
		        public void run()
		        {
		        	ServerLoggers.infoLog.info("SERVER SHUTTING DOWN");
		        }
		    } );
	    }    
	}
	
/************************************************************************************************/
	/**
	 * Before methods are run code. Will be used to maintain a user's login state.
	 */
	@Before
	static public void authenticate(){
		
	}
	/**
	 * Default rendering for pages
	 */
	public static void renderLoginPage(){
		renderTemplate("app/views/webpages/login.html");
	}
	public static void renderIndexPage(){
		renderTemplate("app/views/webpages/index.html");
	}
	/**
	 * Method that routes a user's click on email link to reset password
	 */
	public static void renderPasswordResetPage(){
		Node uNode=null;
		String[] paramAr = request.querystring.split("&");
		String code = paramAr[0].substring(5);
		String uName = paramAr[1].substring(6);
		System.out.println("Verifying Password reset hash: "+code);
		System.out.println("Verifying Password reset uName: "+uName);
		GraphDatabaseService bDB = BTCxDatabase.bDB;
		try(Transaction tx = bDB.beginTx()){
		//If server shuts down cache is cleared.
		 uNode = BTCxDatabase.USER_INDEX.get("resetPasswordRequest", code).getSingle();
			if(uNode!=null){
				if(uNode.getProperty("userName").equals(uName)){
					//Render Email reset modal/page pass parameters
					
				}			
			}
		}catch(Exception e){
			ServerLoggers.errorLog.error("***Error in verifying email. EmailController.emailVerificationResponse  ***");
		}
	}
	
}
