package databases.objects.users;

import org.neo4j.shell.util.json.JSONArray;
import org.neo4j.shell.util.json.JSONObject;

import api.WebpageAPI;

/**
 * This is the merchant class that will handle properties of stores that use our system
 * @author danielmorozoff
 *
 */
public class Merchant extends User {
	
	public String phoneNumber;
	public String address;
	public String city;
	public String state;
	public String geoCords;
	
	public String storeName;
	public String storeDescription;
	public boolean acceptsBTC;
	
}
