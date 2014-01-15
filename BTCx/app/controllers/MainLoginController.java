package controllers;

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
	 
	public static void logIntoSite(String userStr) throws JSONException{
		UserLoginAndSignup entranceClass = new UserLoginAndSignup();
		//Double check the Cache.
			if(Cache.get(session.getAuthenticityToken())==null && userStr!=null ){
				JSONObject userObj = new JSONObject(userStr);
				String userName = (String) userObj.get("userName");
				if(userName!=null){
					Node curNode = BTCxDatabase.USER_INDEX.get("userName", userName).getSingle();	
					if(curNode!=null){
						String password = (String) userObj.get("password");
						//Login with email.
						/*
							if(!(Boolean) curNode.getProperty("emailVerified")){
								//Render Validation page
								System.out.println("Need to Validate Email");
								String userFName = (String) curNode.getProperty("fName");
								String userEmail = (String) curNode.getProperty("email");
								String userPubId = (String) curNode.getProperty("publicId");
								
								renderTemplate("app/views/Application/EmailValidation.html",userFName, userEmail,userPubId );
							}
						*/
						if(entranceClass.testLoginInfo(userName, password, "userName")){
							ServerLoggers.infoLog.info("***Logging in: "+userName+" ***");
							//Set Cache for logged in User
							Cache.set(session.getAuthenticityToken(), userName);
							}
							//Add page to render
							MainSystemController.renderIndexPage();
						}
						else{
							flash.error("Username or Password Incorrect");//entranceClass.errorString
							MainSystemController.renderLoginPage();
						}
					}
				else{
					//Username null
					flash.error("Username or Password Incorrect");
					MainSystemController.renderLoginPage();
				}
		}
		else{
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
	public static String signupUser(String userStr) throws Exception{
		JSONObject userObj = new JSONObject(userStr);
		if(userObj.get("password1").equals(userObj.get("password2"))){
			UserLoginAndSignup uEnter = new UserLoginAndSignup();
			String userExistsTestResult = uEnter.checkIfUserExistsInDb((String)userObj.get("userName"), (String)userObj.get("email"),"userName");
			//Currently only enforcing unique usernames.
			if(userExistsTestResult.equals("false")){
				ServerLoggers.infoLog.info("***CREATING NEW USER: "+userObj.get("userName")+" ***");
					
				User newUser = new User();
				String userName=(String)userObj.get("userName"),
						password=(String)userObj.get("password"),
						email=(String)userObj.get("email"),
						fName=(String)userObj.get("fName"),
						lName=(String)userObj.get("lName");
					
					//Loads User class for creation. Generates unique ids, encrypts password and creates salt
					newUser.loadUserClassForSignup(userName, password, email,fName,lName);
						//Stores and verifies user in DB
						if(uEnter.makeNewUser(newUser)){
							Node userNode =  BTCxDatabase.USER_INDEX.get("userName", userName).getSingle();
							ServerLoggers.infoLog.info("***Sending "+newUser.userName+" email verifcation code ***");
							new SignupEmailer().sendSignupEmail(email, userName,(String)userNode.getProperty("fName"),(String)userNode.getProperty("uniqueEmailVerificationString"));
							return "userCreated_"+newUser.public_uniqueId;
						}
						else return "User was not stored - error occured";
			}
			else{
				return "Username exists";
			}
		}
		return "Passwords do not match";
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
