package databases;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import org.neo4j.graphdb.config.Setting;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;

import controllers.SignupController;

import serverLoggers.ServerLoggers;



public class BTCxDatabase {
	static public GraphDatabaseService signupDB;
	static public IndexManager signupDBIndex;
	/**
	 * These are the database supported indexes.
	 * EXCHANGE_INDEX -- Exchanges
		USER_INDEX -- Users
		TRANSACTION_INDEX -- Transactions
	 * @author danielmorozoff
	 *
	 */
	static public Index <Node> USER_INDEX;
	/**
	 * BTCx Database -- Neo4j 2.0 supports a new config mechanism- will need to update this.
	 */
	public BTCxDatabase(){
		File folder = new File("Databases");
		if(!folder.exists())folder.mkdirs();
			
		
//		Map<String, String> config = new HashMap<String, String>();
//		config.put( "neostore.nodestore.db.mapped_memory", "10M" );
//		config.put( "string_block_size", "60" );
//		config.put( "array_block_size", "300" );
		

   		 System.out.println(" DB LOADED...");
		 signupDB = new GraphDatabaseFactory().newEmbeddedDatabase(SignupController.DB_LOCATION);

		 
		 try(Transaction tx = signupDB.beginTx()){
				System.out.println("number of emails stored: "+signupDB.index().forNodes("users").query("email", "*").size());
				tx.success();
			}
	
//		    .setConfig(config)
			
//			 registerShutdownHook(signupDB);
		 

		 
	}
	public static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running example before it's completed)
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {	
	        	System.out.println("DB SHUTTING DOWN..."); 
	        	signupDB.shutdown();
	        	System.out.println("DB SHUTDOWN");
	        }
	    } );
	}
/************************************************************************************************/	
	
	/**
	 * Current relationships supported by the database
	 * @author danielmorozoff
	 *
	 */
	public enum BTCRelationship implements RelationshipType{
		
	}
	
}
