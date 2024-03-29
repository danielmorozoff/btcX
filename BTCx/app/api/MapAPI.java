package api;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.shell.util.json.JSONArray;
import org.neo4j.shell.util.json.JSONException;
import org.neo4j.shell.util.json.JSONObject;
import org.neo4j.graphdb.Transaction;
import databases.BTCxDatabase;

public class MapAPI {

	public MapAPI(){
		
	}
	/**
	 * Retrieves all stores and provides the following response
	 * {'markers:[{'title':'Store 1','description':'Description 1','coordinates':[lat,lng]}]}
	 * @return
	 * @throws JSONException 
	 */
	public String getCoordsForAllStores() throws JSONException{
		JSONObject retObj = new JSONObject();
		JSONArray markers = new JSONArray();

		Transaction tx = BTCxDatabase.bDB.beginTx();	
			IndexHits hits = BTCxDatabase.USER_INDEX.query("geoCoords","*");
			if(hits!=null){	
					while(hits.hasNext()){
						JSONObject marker = new JSONObject();
						Node storeNode = (Node) hits.next();
						marker.put("title", storeNode.getProperty("storeName"));
						marker.put("description", storeNode.getProperty("storeDescription"));
						marker.put("phoneNumber", storeNode.getProperty("phoneNumber"));
						marker.put("acceptsBTC", storeNode.getProperty("acceptsBTC"));
						
						JSONObject googleCoords = new JSONObject((String)storeNode.getProperty("geoCoords"));
		
							JSONArray coords = new JSONArray();
								coords.put(googleCoords.get("lat"));
								coords.put(googleCoords.get("lng"));
						marker.put("coordinates", coords);
						markers.put(marker);
					}
				retObj.put("markers",markers);		
			}
		tx.success();  	
		return retObj.toString();
	}
	/**
	 * For a specific store retrieve the location json object provided by google maps api
	 * @param id
	 * @param idType
	 * @return
	 */
	public JSONObject retrieveCoordsForSpecificStore(String id, String idType){
		Node storeNode = BTCxDatabase.USER_INDEX.get(idType,id).getSingle();
		if(storeNode!=null){
			if(storeNode.hasLabel(DynamicLabel.label("Merchant"))){
				return new JSONObject(storeNode.getProperty("geoCoords"));
			}
		}
		return null;
	}
}
