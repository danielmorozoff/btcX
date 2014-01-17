package controllers;




import api.MapAPI;
import play.Play;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;


public class DataAPIController extends Controller {
	//Code required to authenticate that the frontend server is making the request
	//private static final String uniqueDataAccessCode="";https://newsrover
	
	@Before
	public static void authentify(){
		if(Cache.get(session.getAuthenticityToken())==null){
			renderTemplate("");
		}
	}
	/**
	 * Retrieve all store markers for mapbox map display
	 * @return
	 * @throws JSONException
	 */
	public static String getAllCurrentMapMarkers(){
		try
		{
		return new MapAPI().getCoordsForAllStores();
		}
		catch(Exception e)
		{
			return "{'markers':[]}";
		}
	}
	
	


}
