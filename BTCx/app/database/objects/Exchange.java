package database.objects;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.UUID;

import org.neo4j.graphdb.Node;

import databases.BTCxDatabase;


import serverLoggers.ServerLoggers;

/**
 * This is the Exchange class. It is an object of the various exchanges that BTCx supports.
 * Current properties
 * Exchange
	+ name: String
	+ website: String
	+ exchangeRate: Double
	+ rating: Integer
	+ summary: String
	+ exchange Prices
 * @author danielmorozoff
 *
 */
public class Exchange  {
	
	public  String name;
	public  String uniqueId;
	public  String website;
	public  double exchangeRate; //This seems redundant to exchange rates (since it will contain it) will be marked for review.
	public  int rating;
	public  String summary;
	public  HashMap <String,Double> exchangeRates;
	
	
	//Can optionally automatically retrieve exchange object by passing a nodeId;
	public Exchange(String nodeId){
		if(nodeId!=null){
			Node exNode = (Node)BTCxDatabase.EXCHANGE_INDEX.get("id", nodeId).getSingle();
				Exchange retObj = (Exchange) BTCxObject.convertNodeToObject(exNode, null);
				this.name = retObj.name;
				this.uniqueId = retObj.uniqueId;
				this.website = retObj.website;
				this.exchangeRate = retObj.exchangeRate;
				this.rating = retObj.rating;
				this.summary = retObj.summary;
				this.exchangeRates = (HashMap <String,Double>)BTCxObject.deserializeToHashMap((byte[])retObj.exchangeRates);
				
		}
	}
	
	
/************************************************************************************************/
	//Creation Methods
	/**
	 * A method for creating a new Exchange object. We only allow unique names and ids for these objects
	 * @param assignName
	 * @param assignWebsite
	 * @param assignExchangeRates
	 * @param assignRating
	 * @param assignSummary
	 * @return
	 */
	public Exchange addExchangeInDB(String assignName, String assignWebsite, HashMap <String,Double> assignExchangeRates, int assignRating, String assignSummary){
		if(exchangeNameExists(assignName)){
			try{
			//Create node and assign properties.
				Node exchangeNode = BTCxDatabase.bDB.createNode();
					exchangeNode.setProperty("name", assignName);
					String uniqueId = UUID.randomUUID().toString();
					exchangeNode.setProperty("uniqueId", uniqueId);
					exchangeNode.setProperty("exchangeRates",(byte[])BTCxObject.serializeHashMap(assignExchangeRates));
					exchangeNode.setProperty("website", assignWebsite);
					exchangeNode.setProperty("rating", assignRating);
					exchangeNode.setProperty("summary", assignSummary);
					//Add to Exchange index
					BTCxDatabase.EXCHANGE_INDEX.add(exchangeNode, "id", uniqueId );		
			
			}catch(RuntimeException e){
					ServerLoggers.errorLog.error("!!! Failed to create Exchange object. Method Exchange.addExchangeInDB !!!");
					e.printStackTrace();
			}
		}
		else System.out.println("Please choose different name for Exchange object.");
		return null;
	}
/************************************************************************************************/
	//Retrieval methods
	public Object getExchangeById(String uniqueId,  String returnType){
		Object exchangeNode = BTCxDatabase.EXCHANGE_INDEX.get("id", uniqueId).getSingle();
		if(exchangeNode!=null){
			if(returnType.equals("node")){
				return (Node) exchangeNode;
			}
			else return BTCxObject.convertNodeToObject((Node)exchangeNode, null);
		}
		return null;
	}
	public Object getExchangeByName(String uniqueId, String returnType){
		Object exchangeNode = BTCxDatabase.EXCHANGE_INDEX.get("id", uniqueId).getSingle();
		if(exchangeNode!=null){
			if(returnType.equals("node")){
				return (Node) exchangeNode;
			}
			else return BTCxObject.convertNodeToObject((Node)exchangeNode, null);
		}
		return null;
	}
/************************************************************************************************/
	//Search and Testing methods
	public boolean exchangeNameExists(String name){
		if(BTCxDatabase.EXCHANGE_INDEX.get("name", name).getSingle()!=null) return true;
		else return false;
	}
	public boolean exchangeIdExists(String id){
		if(BTCxDatabase.EXCHANGE_INDEX.get("id", id).getSingle()!=null) return true;
		else return false;
	}
	
}
