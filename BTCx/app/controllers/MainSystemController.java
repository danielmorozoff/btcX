package controllers;
import java.io.File;
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
	public static String system_url = "http://localhost:9000"; //NEED TO CHANGE http://cryptrex.com
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
	    	if(!userImageStore.exists())
	    	{
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
	
	/**
	 * Renders Login page for login and signup 
	 */
	public static void renderLoginPage(){
		renderTemplate("app/views/webpages/login.html");
	}
	/**
	 * Renders Index page of the application
	 */
	public static void renderIndexPage(){
		renderTemplate("app/views/webpages/index.html");
	}
	
}
