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

import databases.BTCxDatabase;
import databases.objects.User;
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

	@Before(unless={"resetPassword","linkToResetPassword","forgotEmail","signupUser"})
	/**
	 * Test if user has previously logged in
	 * If AuthenticityToken is present user is logged in and index page is rendered
	 */
	public static void authentify(){
		//
		if(Cache.get(session.getAuthenticityToken())!=null)
		{
			System.out.println("Logging in: "+session.getAuthenticityToken());
			Cache.set(session.getAuthenticityToken(), Cache.get(session.getAuthenticityToken()));
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
	public static void logIntoSite(String userStr){
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
							boolean emailVerified = (Boolean) curNode.getProperty("emailVerified");
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
					}
					catch(Exception e )
					{
						//Transaction Exception
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
			//Currently only enforcing unique usernames.
			if(userExistsTestResult.equals("false")){
				ServerLoggers.infoLog.info("***CREATING NEW USER: "+userObj.get("userName")+" ***");
					
				User newUser = new User();
				String userName=(String)userObj.get("userName"),
						password=(String)userObj.get("password"),
						email=(String)userObj.get("email"),
						fName=(String)userObj.get("firstName"),
						lName=(String)userObj.get("lastName");
					
					//Loads User class for creation. Generates unique ids, encrypts password and creates salt
					newUser.loadUserClassForSignup(userName, password, email,fName,lName);
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
//	/**
//	 * Method for verifying provided email
//	 * @param emailValidationCode
//	 */
//	public static void validateEmail(String emailValidationCode, String pubId){
//		System.out.println("Verifying Email for: "+pubId);
//		Node userNode = uDB.index().forNodes("users").get("publicId", pubId).getSingle();
//		if(userNode!=null){
//			if(userNode.getProperty("uniqueEmailVerificationString").equals(emailValidationCode)){
//				//pass test update db
//				System.out.println("Email verified for: "+pubId);
//				try{
//					Transaction tx = uDB.beginTx();
//						userNode.setProperty("emailVerified", true);
//						userNode.setProperty("uniqueEmailVerificationString", "");
//					tx.success();
//					tx.finish();
//				
//					new GenericUserMethods().convertNodeToObject(userNode, new User(), null);
//					Cache.set(session.getAuthenticityToken(), userNode.getProperty("userName"));
//					MainSystemController.renderTopicLayoutPage();
//				}catch(RuntimeException e){
//					e.printStackTrace();
//				}
//			}
//		}
//		else{
//			//renderTemplate("app/views/Application/EmailValidation.html" );
//		}
//	}
//	/**
//	 * Email password to user. Email code is cached and stored for 10 mins 
//	 * you cannot resend an email before it expires or you have to close your browser/clear cookies.
//	 * @param email
//	 * @throws Exception
//	 */
//	public static String emailResetPassword(String email) throws Exception{
//		if(email!=null){
//			if(!email.equals("")){
//				Node userNode =uDB.index().forNodes("users").get("email", email).getSingle();
//				if(userNode!=null){
//					ServerLoggers.infoLog.info("Emailing "+userNode.getProperty("userName")+"reset link to" +
//							request.url);
//					Boolean resetMade = (Boolean)Cache.get("resetPassword-"+session.getAuthenticityToken());
//					if(resetMade == null )resetMade=false;
//					if(!resetMade){
//						ServerLoggers.infoLog.info("All tests passed, generating unique link to reset password...");
//						String uniqueCode = URLEncoder.encode(BCrypt.hashpw(UUID.randomUUID().toString(),BCrypt.gensalt()),"UTF-8");
//							new ForgotPassEmailer().sendForgotPassEmail(email,currentServerURL+"/passwordResetPage/"+uniqueCode,true);
//						
//						Cache.add("resetLinkCode-"+uniqueCode, email,"10mn");
//						Cache.add("resetPassword-"+session.getAuthenticityToken(), true,"10mn");
//						
//						return "Email sent";
//					}
//					return "Please wait to resend an email.";
//				}
//				return "Email does not exist";
//			}
//		}
//		return null;
//	}
//	/**
//	 * Load the reset page based on the url
//	 */
//	public static void loadPasswordResetPage(){
//		ServerLoggers.infoLog.info("Password reset request through email: "+request.url);
//		String uniqueCode = request.url.substring(request.url.indexOf("/passwordResetPage/")+19);
//		System.out.println("Unique code: " + uniqueCode);
//			if(Cache.get("resetLinkCode-"+uniqueCode)!=null){
//				System.out.println("Rendering...");
////				MainSystemController.renderPasswordResetPage(uniqueCode);
//			}
//	}
//	
//	/**
//	 * The actual method that updates the password code
//	 * @param newPass
//	 * @param repeatedPass
//	 * @return
//	 * @throws Exception
//	 */
//	public static String resetPassword(String newPass, String repeatedPass) throws Exception{
//		String uniqueCode = request.url.substring(request.url.indexOf("/passwordReset/")+15);
//			if(Cache.get("resetLinkCode-"+uniqueCode)!=null){
//				if(newPass.equals(repeatedPass)){
//					GraphDatabaseService  uDB = BTCxDatabase.bDB;
//					Node userNode = uDB.index().forNodes("users").get("email", Cache.get("resetLinkCode-"+uniqueCode)).getSingle();
//					try{
//						ServerLoggers.infoLog.info("Reseting user password for : "+userNode.getProperty("userName"));
//						Transaction tx = BTCxDatabase.bDB.beginTx();
//							String salt= BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt());
//							userNode.setProperty("salt", salt);
//							userNode.setProperty("password", new UserEntrance().encryptPassword(newPass, "userCreation", null, salt,"2wayEncryption"));
//						tx.success();
//						ServerLoggers.infoLog.info("***Password Changed***");
//						//Remove Cache obj 
//						Cache.delete("resetLinkCode-"+uniqueCode);
//						return "Password changed successfully"; 
//					}catch(RuntimeException e){
//						e.printStackTrace();
//						ServerLoggers.errorLog.error("!!! Failed to reset password. MainLoginController.resetPassword !!!");
//					}
//				}
//				return null;
//			}
//			return null;
//	}
//	
//	/**
//	 * Log user out of site and perform all logout clearing -- currently only Cache.
//	 */
//	public static void logoutUser(){
//		ServerLoggers.infoLog.info("Logging out "+Cache.get(session.getAuthenticityToken())+" with session id: "+session.getAuthenticityToken());
//		Cache.delete(session.getAuthenticityToken());
//		MainSystemController.renderLoginPage();
//	}
//
//
}
