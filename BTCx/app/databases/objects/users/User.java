package databases.objects.users;

import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;
import org.neo4j.shell.util.json.JSONArray;
import org.neo4j.shell.util.json.JSONException;
import org.neo4j.shell.util.json.JSONObject;

import api.WebpageAPI;

import serverLoggers.ServerLoggers;

import login.UserLoginAndSignup;

/**
 * This is the User class. It is an object of users BTCx supports.
 * Current properties:
 * User
+ Email: String
+ Username: String
+ Unique ID: String
+ Firstname: String
+ Lastname: String
+ Country: String
+ Password: String (delete when leaves)
+ LoginDate: Long
+ SignupDate: Long
+ ActiveAccount: boolean
+ EmailVerificationStr: String -- temporary then deleted
+ emailValidated: boolean
 
 * @author danielmorozoff
 *
 */
public class User extends Object {

	public String email;
	public String private_uniqueId;
	public String public_uniqueId;
	public String accountType;
	public String userName;
	public String firstName;
	public String lastName;
	public String country;
	public String password;
	public String salt;
	public long lastLoginDate;
	public long signupDate;
	public boolean accountActive;
	public String emailVerificationStr;
	public boolean emailValidated;
	
	/**
	 * Current supported account types. These are reflected in both Labels
	 * @author danielmorozoff
	 *
	 */
	public enum  ACCOUNT_TYPE{
		MERCHANT,
		TRADER
	}
	/**
	 * Loads user parameters upon signup on login page. Generates unique ids.
	 * @param userName
	 * @param password
	 * @param email
	 * @param fName
	 * @param lName
	 * @return
	 * @throws JSONException 
	 */
	public User loadUserClassForSignup(String type, String userName, String password, String email,String fName, String lName,boolean acceptsTerms, String address,String city, String state, String phoneNumber) throws JSONException
	{ 
		User user = null;
		System.out.println(type+" "+address+" "+city+" "+state+" "+phoneNumber);
		if(type.equals("merchant") && address!=null && city!=null && state!=null && phoneNumber!=null)
		{	
			user = new Merchant();
			user.accountType = ACCOUNT_TYPE.MERCHANT.toString();
			Merchant merchObj = ((Merchant)user);
					 merchObj.address = address;
					 merchObj.city = city;
					 merchObj.state = state;
					 merchObj.phoneNumber = phoneNumber;
//					 Convert to geo coordinates using google maps api
		    			String fullAddress =  address +" "+ city+" "+state;
		    			fullAddress = fullAddress.replaceAll(" ", "%20");
		    			JSONObject coordsObj = new JSONObject((String) new WebpageAPI(null).sendRequestToDataHub("http://maps.googleapis.com/maps/api/geocode/json?address="+fullAddress+"&sensor=true", "GET", "string", null));

		    			JSONArray results = (JSONArray) coordsObj.get("results");
		    			JSONObject result = (JSONObject) results.get(0);
		    			String geoCoordsStr = ((JSONObject)result.get("geometry")).get("location").toString();
	    			 merchObj.geoCords = geoCoordsStr;	
			
		}
		else if(type.equals("trader")){
			user = new Trader();
			user.accountType = ACCOUNT_TYPE.TRADER.toString();
			//Currently we do not have any specific trader properties
		}
		
		UserLoginAndSignup uEnter = new UserLoginAndSignup();
		user.private_uniqueId = UUID.randomUUID().toString();
		//Added security since it is public.
		user.public_uniqueId = BCrypt.hashpw(UUID.randomUUID().toString(),BCrypt.gensalt());

		user.userName = userName;
		user.salt= BCrypt.hashpw(UUID.randomUUID().toString(),BCrypt.gensalt());
		try {
			user.password = uEnter.encryptPassword(password, "userCreation", null, salt,"2wayEncryption");
		} catch (Exception e) {
			ServerLoggers.errorLog.error("!!! Failed to create User object. Method User.loadUserClassForSignup !!!");
			e.printStackTrace();
		}
		user.email=email;
		user.emailVerificationStr = UUID.randomUUID().toString();
		user.firstName = fName;
		user.lastName=lName;
		user.country="";
		
		user.emailValidated  =false;
		
		long curTime = System.currentTimeMillis();
		user.signupDate  = curTime;
		user.lastLoginDate = curTime;
		
		return user;
	}
}
