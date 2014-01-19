package controllers;

import java.io.IOError;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import javax.crypto.Cipher;

import login.UserLoginAndSignup;

import org.mindrot.jbcrypt.BCrypt;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.shell.util.json.JSONException;
import org.neo4j.shell.util.json.JSONObject;

import ch.qos.logback.core.subst.Token.Type;
import databases.BTCxDatabase;
import databases.objects.users.User;
import emailers.SignupEmailer;
import frontend.Response;

import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import serverLoggers.ServerLoggers;

public class MainLoginController extends Controller {

final static String currentServerURL = "";	
final static  String accessCode="";	
public static GraphDatabaseService bDB = BTCxDatabase.bDB;

	@Before(unless={"resetPassword","linkToResetPassword","forgotEmail","signupUser","logoutUser"})
	
	public static void authentify(){
		//Test if user has previously logged in
		if(Cache.get(session.getAuthenticityToken())!=null){
			System.out.println("Logging in: "+session.getAuthenticityToken());
			//User has already logged in
			Cache.set(session.getAuthenticityToken(), Cache.get(session.getAuthenticityToken()));
			//Render index page
			MainSystemController.renderIndexPage();
		}
	}
	
	
	/**
	 * Beginning of Login flow
	 * @param username
	 * @param password
	 * @throws JSONException 
	 */
	 
	 //ADD email verification ERROR
	public static void logIntoSite(String userStr) throws JSONException{
		UserLoginAndSignup entranceClass = new UserLoginAndSignup();
		flash.clear();
			//Double check the Cache.
			if(Cache.get(session.getAuthenticityToken())== null && userStr !=null && userStr != "")
			{
				try
				{
					JSONObject userObj = new JSONObject(userStr);
					String userName = (String) userObj.get("userName");
					if(userName != null && userName != "")
					{
					try(Transaction tx = bDB.beginTx())
					{
						Node curNode = BTCxDatabase.USER_INDEX.get("userName", userName).getSingle();	
						if(curNode != null)
						{
							String password = (String) userObj.get("password");
							boolean emailVerified = (Boolean) curNode.getProperty("emailValidated");
							System.out.println("EMAIL VERIFIED: "+emailVerified);
							if(!emailVerified)
							{
								//Did not verify Email
								flash.error("Please verify your email.");
								MainSystemController.renderLoginPage();
							}
							else if(entranceClass.testLoginInfo(userName, password, "userName"))
							{
								//Set Cache for logged in User
								ServerLoggers.infoLog.info("***Logging in: "+userName+" ***");
								Cache.set(session.getAuthenticityToken(), userName);
								MainSystemController.renderIndexPage();
							}
							else
							{
								MainSystemController.renderLoginPage();
							}
						}
						else
						{
							flash.error("Username or Password Incorrect");//entranceClass.errorString
							MainSystemController.renderLoginPage();
						}
					tx.success();	
					}
					catch(Exception e )
					{
						//Transaction Exception
						e.printStackTrace();
						System.out.println("Transaction exception");
						flash.error("Please try again later");
						MainSystemController.renderLoginPage();
					}
					}
				else
				{
					//Username null
					flash.error("Username or Password Incorrect");
					MainSystemController.renderLoginPage();
				}	
			}
			catch(Exception e)
			{
				//JSON Exception
				System.out.println("JSON exception");
				flash.error("Please try again later");
				MainSystemController.renderLoginPage();
			}
		}
		else
		{
			//No auth token
			MainSystemController.renderLoginPage();
		}
	}

	/**
	* Log out the user
	*/
	public static void logoutUser()
	{
		Cache.delete(session.getAuthenticityToken());
		MainSystemController.renderLoginPage();
	}
	/**
	 * Create new user in DB
	 * @param userClass
	 * @return
	 * @throws Exception 
	 */
	public static String signupUser(String usrStr) throws Exception{
		Response response = new Response();
		Response.method = "Signup";

		JSONObject userObj = new JSONObject(usrStr);
		if(userObj.get("password").equals(userObj.get("reppassword")) && ((Boolean) userObj.get("agreement"))){
			UserLoginAndSignup uEnter = new UserLoginAndSignup();
			String userExistsTestResult = uEnter.checkIfUserExistsInDb((String)userObj.get("userName"), (String)userObj.get("email"),"userName");
			boolean acceptsTerms = (Boolean) userObj.get("acceptsTerms") ;
			//Currently only enforcing unique usernames.
			if(userExistsTestResult.equals("false") && acceptsTerms){
				ServerLoggers.infoLog.info("***CREATING NEW USER: "+userObj.get("userName")+" ***");
					
				User newUser = new User();
				String userName=(String)userObj.get("userName"),
						password=(String)userObj.get("password"),
						email=(String)userObj.get("email"),
						fName=(String)userObj.get("firstName"),
						lName=(String)userObj.get("lastName"),
						type = (String) userObj.get("type");
				
				String address = null;
				String city = null;
				String state = null;
				String phoneNumber  = null;
				
				if(type.equals("merchant")){
					address= (String) userObj.get("address");
					city=  (String) userObj.get("city");
					state=  (String) userObj.get("state");
					phoneNumber=  (String) userObj.get("phone");
				}
					
					//Loads User class for creation. Generates unique ids, encrypts password and creates salt
					newUser.loadUserClassForSignup(userName, password, email,fName,lName,type,acceptsTerms,address,city,state,phoneNumber);
					try{	
					//Stores and verifies user in DB
						if(uEnter.makeNewUser(newUser)){
							
							try(Transaction tx =bDB.beginTx()){
								Node userNode =  BTCxDatabase.USER_INDEX.get("userName", userName).getSingle();
								
								userNode.setProperty("codeToValidateEmail", newUser.emailVerificationStr);
								BTCxDatabase.USER_INDEX.add(userNode,"codeToValidateEmail",newUser.emailVerificationStr);
								
								ServerLoggers.infoLog.info("***Sending "+newUser.userName+" email verifcation code ***");
								new SignupEmailer().sendSignupEmail(email, userName,(String)userNode.getProperty("firstName"),(String)userNode.getProperty("emailVerificationStr"));
								Response.message = "Thank you for signing up! Check your inbox please.";
								Response.success = true;
								
								tx.success();
							}
						}
					}catch(IOError e){
						e.printStackTrace();
						response.standard();
					}
			}
			else{
				Response.message = "Username exists already";
			}
		}
		else
		{
			Response.message = "Passwords do not match";
		}
		return response.toString();
	}
}
