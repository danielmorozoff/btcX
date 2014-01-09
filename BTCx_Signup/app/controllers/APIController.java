package controllers;

import org.neo4j.shell.util.json.JSONException;
import org.neo4j.shell.util.json.JSONObject;

import api.MapAPI;

import play.mvc.Controller;

public class APIController extends Controller{
	
	/**
	 * Retrieve all store markers for mapbox map display
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject getAllCurrentMapMarkers() throws JSONException{
		return new MapAPI().getCoordsForAllStores();
	}
}
