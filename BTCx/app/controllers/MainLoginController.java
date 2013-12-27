package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import javax.crypto.Cipher;

import login.UserEntrance;

import org.mindrot.jbcrypt.BCrypt;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.shell.util.json.JSONObject;
import databases.BTCxDatabase;

import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Cookie;
import serverLoggers.ServerLoggers;

public class MainLoginController extends Controller {
	
final static String currentServerURL = "";	
final static  String accessCode="";	
public static GraphDatabaseService uDB = BTCxDatabase.bDB;
	

	@Before(unless={"resetPassword","linkToResetPassword","forgotEmail","loginToSite","logoutUser"})
	
	public static void authentify(){
		//User not logged in
		System.out.println("Session: "+Cache.get(session.getAuthenticityToken()));
		if(Cache.get(session.getAuthenticityToken())!=null){
			System.out.println("Logging in: "+session.getAuthenticityToken());
			//Login in to site
			Cache.set(session.getAuthenticityToken(), Cache.get(session.getAuthenticityToken()));
			//Add page to render
			
		}
	}
	
	
	/**
	 * Beginning of Login flow
	 * @param username
	 * @param password
	 */
	 
	public static void loginToSite(String userName, String password,boolean remember){
		//Currently remember set to true -- removed the box
		remember = true;
		
		UserEntrance entranceClass = new UserEntrance();
		System.out.println("AUTH token: "+session.getAuthenticityToken());
			if(Cache.get(session.getAuthenticityToken())==null){
				if(userName!=null){
					System.out.println("Session null");
				Node curNode = BTCxDatabase.bDB.index().forNodes("users").get("userName", userName).getSingle();	
					if(curNode!=null){
						System.out.println("User Found");
						System.out.println("Checking email verification");
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
							System.out.println("Logging in: "+userName);
							//Login in to site
							Cache.set(session.getAuthenticityToken(), userName);
							if(remember){
								Node userNode = uDB.index().forNodes("users").get("userName", userName).getSingle();
								String hashedStringForCookie ="";
								try {
									try {
										hashedStringForCookie = entranceClass.encryptOrDecryptUserCookie((String) userNode.getProperty("publicId"), Cipher.ENCRYPT_MODE);
									} catch (Throwable e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									System.out.println("Encrypted Cookie: "+hashedStringForCookie);
								} catch (Exception e) {
									e.printStackTrace();
								}
								response.setCookie("PTVN_rememberCookie", (String) hashedStringForCookie,"3h");
								Cache.add(hashedStringForCookie, (String) userNode.getProperty("publicId"),"3h");
								//Set tagger cookie 
								System.out.println("Setting tagger cookie");
								int secretKey = UUID.randomUUID().toString().hashCode();
								String keyStr = "tagger_"+userName+"_"+secretKey;
									//when we switch to https then set secure to true
								Cookie cook = new Cookie();
									cook.name = "taggerCookie";
									cook.value = "tagger_"+userName+"_"+secretKey;
									
								response.cookies.put("taggerCookie", cook);
								System.out.println("TaggerSystem cookie set: "+response.cookies.get("taggerCookie").value);
								Cache.add(keyStr, userName,"4h");
								
							}
							//Add page to render
							System.out.println("rendering homepage");
							MainSystemController.renderTopicLayoutPage();
						}
						else{
							System.out.println("auth tok null");
							flash.error(entranceClass.errorString);
							MainSystemController.renderLoginPage();
						}
					}
					else{
						System.out.println("Username does not exist...");
						flash.error("Username or Password Incorrect");
						MainSystemController.renderLoginPage();
					}
				}
				else{
					//No username sent render te login page
					MainSystemController.renderLoginPage();
				}
			}
			else{
				System.out.println("Auth token not null- loggin "+Cache.get(session.getAuthenticityToken())+" in...");
					//Login in to site
					Cache.set(session.getAuthenticityToken(), Cache.get(session.getAuthenticityToken()));
					//Add page to Render
					MainSystemController.renderTopicLayoutPage();
			}
	}
	/**
	 * Create new user in DB
	 * @param userClass
	 * @return
	 * @throws Exception 
	 */
	public static String signUpUser(String userObj) throws Exception{
		JSONObject jObj = new JSONObject(userObj);
		System.out.println("CREATING NEW USER: "+jObj.get("userName"));
		System.out.println(jObj.toString());
		if(jObj.get("accessCode").equals(accessCode)){
			System.out.println("Access Code verified...");
			User newUser = new User();
			String userName=(String)jObj.get("userName"),
					password=(String)jObj.get("password"),
					email=(String)jObj.get("email"),
					fName=(String)jObj.get("fName"),
					lName=(String)jObj.get("lName");
				
				//In the future we will have a different flow to signup stations
				//new ClientMethods().addClient(userName, password, email,fName,lName, "", "");
				newUser.loadUserClassForSignup(userName, password, email,fName,lName, "", "");
				
				UserEntrance uEnter = new UserEntrance();
				String userExistsTestResult = uEnter.checkIfUserExistsInDb(userName, email); 
				if(userExistsTestResult.equals("false")){
					
					if(uEnter.makeNewUser(newUser)){
						Node userNode = uDB.index().forNodes("users").get("userName", userName).getSingle();
						System.out.println("USER ACCESS CODE: "+userNode.getProperty("uniqueEmailVerificationString"));
						return "validateEmail";
						//new SignupEmailer().sendSignupEmail(email, userName,(String)userNode.getProperty("fName"),(String)userNode.getProperty("uniqueEmailVerificationString"));
						//return "userCreated_"+newUser.userName;
						
					}
					else return "UserNOTCREATED";
				}
				else{
					return userExistsTestResult;
				}
		}
		return "WrongAccessCode";
	}
	/**
	 * Method for verifying provided email
	 * @param emailValidationCode
	 */
	public static void validateEmail(String emailValidationCode, String pubId){
		System.out.println("Verifying Email for: "+pubId);
		Node userNode = uDB.index().forNodes("users").get("publicId", pubId).getSingle();
		if(userNode!=null){
			if(userNode.getProperty("uniqueEmailVerificationString").equals(emailValidationCode)){
				//pass test update db
				System.out.println("Email verified for: "+pubId);
				try{
					Transaction tx = uDB.beginTx();
						userNode.setProperty("emailVerified", true);
						userNode.setProperty("uniqueEmailVerificationString", "");
					tx.success();
					tx.finish();
				
					new GenericUserMethods().convertNodeToObject(userNode, new User(), null);
					Cache.set(session.getAuthenticityToken(), userNode.getProperty("userName"));
					MainSystemController.renderTopicLayoutPage();
				}catch(RuntimeException e){
					e.printStackTrace();
				}
			}
		}
		else{
			//renderTemplate("app/views/Application/EmailValidation.html" );
		}
	}
	/**
	 * Email password to user. Email code is cached and stored for 10 mins 
	 * you cannot resend an email before it expires or you have to close your browser/clear cookies.
	 * @param email
	 * @throws Exception
	 */
	public static String emailResetPassword(String email) throws Exception{
		if(email!=null){
			if(!email.equals("")){
				Node userNode =uDB.index().forNodes("users").get("email", email).getSingle();
				if(userNode!=null){
					ServerLoggers.infoLog.info("Emailing "+userNode.getProperty("userName")+"reset link to" +
							request.url);
					Boolean resetMade = (Boolean)Cache.get("resetPassword-"+session.getAuthenticityToken());
					if(resetMade == null )resetMade=false;
					if(!resetMade){
						ServerLoggers.infoLog.info("All tests passed, generating unique link to reset password...");
						String uniqueCode = URLEncoder.encode(BCrypt.hashpw(UUID.randomUUID().toString(),BCrypt.gensalt()),"UTF-8");
							new ForgotPassEmailer().sendForgotPassEmail(email,currentServerURL+"/passwordResetPage/"+uniqueCode,true);
						
						Cache.add("resetLinkCode-"+uniqueCode, email,"10mn");
						Cache.add("resetPassword-"+session.getAuthenticityToken(), true,"10mn");
						
						return "Email sent";
					}
					return "Please wait to resend an email.";
				}
				return "Email does not exist";
			}
		}
		return null;
	}
	/**
	 * Load the reset page based on the url
	 */
	public static void loadPasswordResetPage(){
		ServerLoggers.infoLog.info("Password reset request through email: "+request.url);
		String uniqueCode = request.url.substring(request.url.indexOf("/passwordResetPage/")+19);
		System.out.println("Unique code: " + uniqueCode);
			if(Cache.get("resetLinkCode-"+uniqueCode)!=null){
				System.out.println("Rendering...");
//				MainSystemController.renderPasswordResetPage(uniqueCode);
			}
	}
	
	/**
	 * The actual method that updates the password code
	 * @param newPass
	 * @param repeatedPass
	 * @return
	 * @throws Exception
	 */
	public static String resetPassword(String newPass, String repeatedPass) throws Exception{
		String uniqueCode = request.url.substring(request.url.indexOf("/passwordReset/")+15);
			if(Cache.get("resetLinkCode-"+uniqueCode)!=null){
				if(newPass.equals(repeatedPass)){
					GraphDatabaseService  uDB = BTCxDatabase.bDB;
					Node userNode = uDB.index().forNodes("users").get("email", Cache.get("resetLinkCode-"+uniqueCode)).getSingle();
					try{
						ServerLoggers.infoLog.info("Reseting user password for : "+userNode.getProperty("userName"));
						Transaction tx = BTCxDatabase.bDB.beginTx();
							String salt= BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt());
							userNode.setProperty("salt", salt);
							userNode.setProperty("password", new UserEntrance().encryptPassword(newPass, "userCreation", null, salt,"2wayEncryption"));
						tx.success();
						ServerLoggers.infoLog.info("***Password Changed***");
						//Remove Cache obj 
						Cache.delete("resetLinkCode-"+uniqueCode);
						return "Password changed successfully"; 
					}catch(RuntimeException e){
						e.printStackTrace();
						ServerLoggers.errorLog.error("!!! Failed to reset password. MainLoginController.resetPassword !!!");
					}
				}
				return null;
			}
			return null;
	}
	
	/**
	 * Log user out of site and perform all logout clearing -- currently only Cache.
	 */
	public static void logoutUser(){
		ServerLoggers.infoLog.info("Logging out "+Cache.get(session.getAuthenticityToken())+" with session id: "+session.getAuthenticityToken());
		Cache.delete(session.getAuthenticityToken());
		MainSystemController.renderLoginPage();
	}


}
