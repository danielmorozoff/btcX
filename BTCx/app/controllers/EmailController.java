package controllers;

import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import login.UserLoginAndSignup;

import org.mindrot.jbcrypt.BCrypt;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.shell.util.json.JSONObject;

import databases.BTCxDatabase;

import emailers.ContactUsEmailer;
import emailers.PasswordResetEmailer;
import emailers.SignupEmailer;
import frontend.Response;
import play.cache.Cache;
import play.mvc.Controller;
import serverLoggers.ServerLoggers;

public class EmailController extends Controller {
	/**
	 * Methods to send contact us email to our contact us email account see Emailer for address
	 * @param request
	 * @param email
	 * @param subject
	 * @param message
	 * @throws Exception
	 */
	public static String sendContactUsEmail(String usrStr) throws Exception{
		Response response = new Response();
		response.method = "Contact";
		try{
		
			JSONObject usrObj = new JSONObject(usrStr);
			String request = usrObj.getString("request");
			System.out.println("Request: "+request);
			String email = usrObj.getString("email");
			System.out.println("email: "+email);
			String subject = usrObj.getString("subject");
			System.out.println("subject: "+subject);
			String message = usrObj.getString("message");
			System.out.println("message: "+message);
		
			if(request.equals("contact")) new ContactUsEmailer().sendSignupEmail(email, subject, message);
			response.message = "Thank your for contacting us!";
			response.success = true;
		
		}
		catch(Exception e )
		{
			response.standard();
		}
		return response.toString();
	}
	/**
	 * Send email verification 
	 * @param usrStr
	 * @return
	 */
	public static String sendVerificationEmail(String usrStr) 
	{
		Response response = new Response();
		response.method = "Verification";
		try
		{
			JSONObject userObj = new JSONObject(usrStr);
			String userName = (String) userObj.get("userName");
			if(userName != null && userName != "")
			{
				GraphDatabaseService bDB = BTCxDatabase.bDB;
				Transaction tx = bDB.beginTx();
				Node uNode = BTCxDatabase.USER_INDEX.get("userName", userName).getSingle();
				if(uNode != null)
				{
					String email = (String)uNode.getProperty("email");
					String firstName = (String)uNode.getProperty("firstName");
					String emailVerificationStr = (String)uNode.getProperty("emailVerificationStr");
					
					new SignupEmailer().sendSignupEmail(email, userName,firstName,emailVerificationStr);
	
					response.success = true;
					response.message = "Verification email has been sent. Please check your inbox.";
				}
				else
				{
					response.message = "Username not found."; 	
				}
				tx.success();
			}
			else
			{
				response.message = "Invalid username."; 	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			response.standard();
		}
		return response.toString();
	}
	/**
	 * Send password reset email
	 * @param userName
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public static String sendPasswordResetEmail(String usrStr) throws Exception{
		JSONObject usrObj = new JSONObject(usrStr);
		String userName = (String) usrObj.get("userName");
		Response response = new Response();
		response.method = "Password Reset Email";

		try(Transaction tx= BTCxDatabase.bDB.beginTx())
		{
			Node userNode = BTCxDatabase.USER_INDEX.get("userName", userName).getSingle();
			tx.success();
			
			if(userNode!=null)
			{
				if((Boolean)userNode.getProperty("emailValidated")){
					String hashedKey = BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt());
					//Add key to Cache under the userName -- set to 10 mins
					Cache.add(hashedKey, userName,"10mn");
					//Send reset email
					new PasswordResetEmailer().sendPasswordResetEmail((String)userNode.getProperty("email"),userName, hashedKey );
					response.message = "Please check your inbox for the email.";
					response.success = true;
				}
				else{
					response.message = "Please verify your email";
					response.success = true;
				}
			}
			else
			{
				response.message = "Invalid username.";
			}
		}
		return response.toString();
	}
	/**
	 * The actual function that does property resetting
	 * @param userName
	 * @param password
	 * @param repPassword
	 * @param key
	 * @throws Exception
	 */
	public static String passwordReset(String usrStr) throws Exception{
		JSONObject usrObj = new JSONObject(usrStr);
		String userName = (String) usrObj.get("userName"),
		 password = (String) usrObj.get("password"), 
		 repPassword = (String) usrObj.get("reppassword"),
		 key = (String) usrObj.get("key");
		 Response response = new Response();
		 response.method = "Password Reset";
		if(password.equals(repPassword))
		{
			String retrievedUserName = (String) Cache.get(key);
			if(retrievedUserName.equals(userName))
			{
				try(Transaction tx= BTCxDatabase.bDB.beginTx()){
					
					Node uNode = BTCxDatabase.USER_INDEX.get("userName", userName).getSingle();
					String salt= BCrypt.hashpw(UUID.randomUUID().toString(),BCrypt.gensalt());
					String hPass = new UserLoginAndSignup().encryptPassword(password, "userCreation", null, salt,"2wayEncryption");
					uNode.setProperty("password", hPass);
					uNode.setProperty("salt", salt);
						
					response.message = "Your password was reset.";
					response.success = true;	
					
					//Remove from cache
					Cache.delete(key);
					
					tx.success();
				}
				catch(Exception e)
				{
					response.message = "Please try again later.";
				}
			}
			else
			{
				response.message = "Invalid username.";
			}
		}
		else
		{
			response.message = "Passwords do not match.";
		}
		return response.toString();
	}
	/**
	 * Method that routes a user's click on email link to verify email
	 */
	public static void emailResponseVerification(){
		String sCode = request.querystring;
		Node uNode=null;
		System.out.println("Verifying Email: "+sCode.substring(5));
		GraphDatabaseService bDB = BTCxDatabase.bDB;
		try(Transaction tx = bDB.beginTx()){
		//If server shuts down cache is cleared.
		 uNode = BTCxDatabase.USER_INDEX.get("codeToValidateEmail", sCode.substring(5)).getSingle();
			if(uNode!=null){
				if(uNode.getProperty("codeToValidateEmail").equals(sCode.substring(5))){
					
					String userName = (String) uNode.getProperty("userName");
					uNode.setProperty("emailValidated", true);
					uNode.removeProperty("emailVerificationStr");
					BTCxDatabase.USER_INDEX.remove(uNode, "codeToValidateEmail");
					tx.success();
					
					System.out.println("Validated: "+uNode.getProperty("emailValidated"));
					Cache.set(session.getAuthenticityToken(), userName);
					MainSystemController.renderIndexPage();
				}			
			}
			
		}catch(Exception e){
			ServerLoggers.errorLog.error("***Error in verifying email. EmailController.emailVerificationResponse  ***");
		}
	}
}
