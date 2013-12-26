package databases;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.RelationshipType;

import org.neo4j.graphdb.config.Setting;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;

import serverLoggers.ServerLoggers;

public class BTCxDatabase {
	static public GraphDatabaseService bDB;
	static public IndexManager bDBIndex;
	/**
	 * These are the database supported indexes.
	 * EXCHANGE_INDEX -- Exchanges
		USER_INDEX -- Users
		TRANSACTION_INDEX -- Transactions
	 * @author danielmorozoff
	 *
	 */
	static public Index EXCHANGE_INDEX,
				 USER_INDEX,
				 TRANSACTION_INDEX;
	/**
	 * BTCx Database -- Neo4j 2.0 supports a new config mechanism- will need to update this.
	 */
	public BTCxDatabase(){
		File folder = new File("PTVN_Databases");
		if(!folder.exists())folder.mkdirs();
		System.out.println("***STARTING UP PTVN_USER_DB****");
			serverLoggers.ServerLoggers.infoLog.info("***BTCx Server Started***");
		
		Map<String, String> config = new HashMap<String, String>();
		config.put( "neostore.nodestore.db.mapped_memory", "10M" );
		config.put( "string_block_size", "60" );
		config.put( "array_block_size", "300" );
		
		 bDB = new GraphDatabaseFactory()
		    .newEmbeddedDatabaseBuilder( "Databses/BTCx-Databse" )
//		    .setConfig(config)
		    .newGraphDatabase();
		 //Start the indexes
		 bDBIndex = bDB.index();
		 
		 EXCHANGE_INDEX = bDBIndex.forNodes("exchanges");
		 USER_INDEX = bDBIndex.forNodes("users");
		 TRANSACTION_INDEX = bDBIndex.forNodes("transactions");
		 
		registerShutdownHook(bDB);
	}
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running example before it's completed)
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	        	System.out.println("*****SHUTTING DOWN BTCx_USER_DB*****");
	        		serverLoggers.ServerLoggers.infoLog.info("***BTCx Server Shutdown***");
	        	bDB.shutdown();
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
		/*
		 *  RELATIONSHIP USER -> TRANSACTION
		 */
			CONDUCTED,
			BUY,
			SELL,
		/*
		 * RELATIONSHIP USER -> EXCHANGE
		 */
			MEMBER_OF,
		/*
		 * RELATIONSHIP TRANSACTION -> EXCHANGE
		 */
			TRADED_ON,
	}
	
}
