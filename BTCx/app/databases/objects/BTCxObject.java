package databases.objects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import serverLoggers.ServerLoggers;

import databases.BTCxDatabase;
import databases.BTCxDatabase.BTCRelationship;
import databases.objects.*;
/**
 * Conversion of BTCx Objects to and from Node objects in Neo4j
 * @author danielmorozoff
 *
 */
public class BTCxObject {
	/**
	 * Object to Node conversion does not exist because you can always retrieve it from DB.
	 * @param obj
	 * @return
	 */
	
	/**
	 * This deserializes a byte[] into a HashMap;
	 * @param byteAr
	 * @return
	 */
	 public HashMap <String, Double> deserializeToHashMap(byte[] byteAr){
		ByteArrayInputStream bis = new ByteArrayInputStream(byteAr);
		 ObjectInputStream ois =null;
		 HashMap<String,Double> map=null;
		try {
			ois = new ObjectInputStream(bis);
		} catch (IOException e) {
			e.printStackTrace();
			ServerLoggers.errorLog.error("!!! Failed to create Exchange object. Method BTCxObject.deserializeToHashMap in serializing Exchange Rates !!!");
		}
		 try {
			map = (HashMap<String,Double>) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			ServerLoggers.errorLog.error("!!! Failed to create Exchange object. Method BTCxObject.deserializeToHashMap in serializing Exchange Rates !!!");
		} catch (IOException e) {
			e.printStackTrace();
			ServerLoggers.errorLog.error("!!! Failed to create Exchange object. Method BTCxObject.deserializeToHashMap in serializing Exchange Rates !!!");
		}
		return map;
	}
	/**
	 * A method to serialize a HashMap to byte[]
	 * @param map
	 * @return
	 */
	 public byte[] serializeHashMap(HashMap <String, Double> map){
		// Since neo4j does not support storing of non primitives wrote serializer of hashmap for exchange rates. and byte storage
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream serializeExchangeRates =null;
		try {
			serializeExchangeRates = new ObjectOutputStream(bos);
		} catch (IOException e) {
			e.printStackTrace();
			ServerLoggers.errorLog.error("!!! Failed to create Exchange object. Method BTCxObject.serializeHashMap in serializing Exchange Rates !!!");
		}
		try {
			serializeExchangeRates.writeObject(map);
		} catch (IOException e) {
			e.printStackTrace();
			ServerLoggers.errorLog.error("!!! Failed to create Exchange object. Method BTCxObject.serializeHashMap in serializing Exchange Rates !!!");
		}
		byte[] exchangeRateBytes =  bos.toByteArray();
		try {
			serializeExchangeRates.close();
		} catch (IOException e) {
			e.printStackTrace();
			ServerLoggers.errorLog.error("!!! Failed to create Exchange object. Method BTCxObject.serializeHashMap in serializing Exchange Rates !!!");
		}
		return exchangeRateBytes;
	}
	
	/**
	 * Node to Object conversion. Supports an exception list if certain properties you do not want or are private
	 */
	 public Object convertNodeToObject(Node  node, ArrayList <String> listOfExceptions , Object objToReturn  ){
		Iterable <String> keys = node.getPropertyKeys();
		for (String key:keys){
			try{
				if(listOfExceptions!=null){
					if(!listOfExceptions.contains(key)){
						Field field = objToReturn.getClass().getField(key);
						try {
							field.set(objToReturn, node.getProperty(key));
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
							ServerLoggers.errorLog.error("!!! Failed to convert NEO4j node into Object. Method BTCxObject.convertNodeToObject !!!");
						} catch (IllegalAccessException e) {
							e.printStackTrace();
							ServerLoggers.errorLog.error("!!! Failed to convert NEO4j node into Object. Method BTCxObject.convertNodeToObject !!!");
						}
					}
				}
				else{
					Field field = objToReturn.getClass().getField(key);
					try {
					if(field.isAccessible())field.set(objToReturn, node.getProperty(key));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						ServerLoggers.errorLog.error("!!! Failed to convert NEO4j node into Object. Method BTCxObject.convertNodeToObject !!!");
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						ServerLoggers.errorLog.error("!!! Failed to convert NEO4j node into Object. Method BTCxObject.convertNodeToObject !!!");
					}
				}
			}
			catch(NoSuchFieldException e){
				//Field not found 
				ServerLoggers.errorLog.error("!!! Failed to convert NEO4j node into Object. Method BTCxObject.convertNodeToObject !!!");
			}
		}
	return objToReturn;
	}
	/**
	 * This method creates a relationship between to objects (not nodes). It enforces directionality.
	 * @param obj1
	 * @param obj2
	 * @param relType
	 * @return
	 */
	public  int createRelationshipBetweenObjects(Object obj1,Object obj2, String relType){
		if(obj1!=null && obj2!=null){
			Exchange ex2= null; //ex1 = null not supported since ex1 is never a start node
			Transaction tx1= null,tx2 = null;
			User us1= null;// us2 = null not supported because we currently do not support us-> us relationships
			
//			if(obj1 instanceof Exchange){
//				 ex1 = (Exchange) obj1;
//			}
			if (obj1 instanceof Transaction){
				tx1 = (Transaction) obj1;
			}
			else if(obj1 instanceof User){
				us1 = (User) obj1;
			}
			
			if(obj2 instanceof Exchange){
				ex2 = (Exchange) obj2;
			}
			else if (obj2 instanceof Transaction){
				tx2 = (Transaction) obj2;
			}
//			else if(obj2 instanceof User){
//				us2 = (User) obj2;
//			}
			try{
			// User to exchange or transaction-- Relationships: BUY, SELL, CONDUCTED, MEMBER_OF
			org.neo4j.graphdb.Transaction tx = BTCxDatabase.bDB.beginTx();	
			if(us1!=null && tx2!=null || ex2!=null){
				Node userNode = (Node) BTCxDatabase.USER_INDEX.get("id", us1.private_uniqueId).getSingle();
				if(tx2!=null){
					//us1 -> tx2
					Node txNode = (Node) BTCxDatabase.TRANSACTION_INDEX.get("id", tx2.uniqueId).getSingle();
					if(userNode!=null && txNode!=null){
						if(relType.equals("BUY")){
								try{
									Relationship rel = userNode.createRelationshipTo(txNode, BTCRelationship.BUY);
									rel.setProperty("date", System.currentTimeMillis());
								}catch(RuntimeException e){
									e.printStackTrace();
									ServerLoggers.errorLog.error("!!! Failed to create BTCxObject object. Method BTCxObject.createRelationshipBetweenObjects. BUY Relationship !!!");
									return 0;
								}
						}
						else if(relType.equals("SELL")){
							try{
								Relationship rel = userNode.createRelationshipTo(txNode, BTCRelationship.SELL);
								rel.setProperty("date", System.currentTimeMillis());
							}catch(RuntimeException e){
								e.printStackTrace();
								ServerLoggers.errorLog.error("!!! Failed to create BTCxObject object. Method BTCxObject.createRelationshipBetweenObjects. SELL Relationship !!!");
								return 0;
							}
						}
						
						else if(relType.equals("CONDUCTED")){
							try{
								Relationship rel = userNode.createRelationshipTo(txNode, BTCRelationship.CONDUCTED);
								rel.setProperty("date", System.currentTimeMillis());
							}catch(RuntimeException e){
								e.printStackTrace();
								ServerLoggers.errorLog.error("!!! Failed to create BTCxObject object. Method BTCxObject.createRelationshipBetweenObjects. CONDUCTED Relationship  !!!");
								return 0;
							}
						}
					}
				}
				else if(ex2!=null){
					//us1 -> ex2
					Node exNode = (Node) BTCxDatabase.EXCHANGE_INDEX.get("id", ex2.uniqueId).getSingle();
					if(relType.equals("MEMBER_OF")){
						try{
							Relationship rel = userNode.createRelationshipTo(exNode, BTCRelationship.MEMBER_OF);
							rel.setProperty("date", System.currentTimeMillis());
						}catch(RuntimeException e){
							e.printStackTrace();
							ServerLoggers.errorLog.error("!!! Failed to create BTCxObject object. Method BTCxObject.createRelationshipBetweenObjects. MEMBER_OF Relationship !!!");
							return 0;
						}					
					}
				}
			}
			// Transaction to Exchange  -- Only TRADED_ON Relationship --requires explicit declaration
			if(tx1!=null && ex2!=null){
				Node txNode = (Node) BTCxDatabase.TRANSACTION_INDEX.get("id", tx1.uniqueId).getSingle();
				if(ex2!=null){
					Node exNode = (Node) BTCxDatabase.EXCHANGE_INDEX.get("id", ex2.uniqueId).getSingle();
					//tx1 -> ex2
					if(relType.equals("TRADED_ON")){
						try{
							Relationship rel = txNode.createRelationshipTo(exNode, BTCRelationship.TRADED_ON);
							rel.setProperty("date", System.currentTimeMillis());
						}catch(RuntimeException e){
							e.printStackTrace();
							ServerLoggers.errorLog.error("!!! Failed to create BTCxObject object. Method BTCxObject.createRelationshipBetweenObjects. TRADED_ON Relationship !!!");
							return 0;
						}		
					}
				}
			}
			//Exchange cannot make a relationship, since it is never a start node (for now)
			tx.success();
			return 1;
			}catch(Exception e){
				e.printStackTrace();
				ServerLoggers.errorLog.error("!!! Failed to create BTCxObject object. Method BTCxObject.createRelationshipBetweenObjects !!!");
				return 0;
			}
		}
		return -1;
	}
}
