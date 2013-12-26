package controllers;
import java.io.File;
import databases.BTCxDatabase;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.Before;
import play.mvc.Controller;
import serverLoggers.ServerLoggers;

public class MainSystemController extends Controller {
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
		renderTemplate("app/views/pages/loginPage.html");
	}
	public static void renderIndexPage(){
		renderTemplate("app/views/pages/indexPage.html");
	}
	
}
