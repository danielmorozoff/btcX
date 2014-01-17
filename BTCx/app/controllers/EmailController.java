package controllers;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.shell.util.json.JSONObject;

import databases.BTCxDatabase;

import emailers.ContactUsEmailer;
import play.cache.Cache;
import play.mvc.Controller;

public class EmailController extends Controller {
	/**
	 * Methods to send contact us email to our contact us email account see Emailer for address
	 * @param request
	 * @param email
	 * @param subject
	 * @param message
	 * @throws Exception
	 */
	public static void sendContactUsEmail(String usrStr) throws Exception{
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
	}
	public static void emailVerification(){
		String sCode = request.querystring;
		System.out.println("Verifying Email: "+sCode.substring(5));
		GraphDatabaseService bDB = BTCxDatabase.bDB;
		try(Transaction tx = bDB.beginTx()){
		//If server shuts down cache is cleared.
		Node uNode = BTCxDatabase.USER_INDEX.get("codeToValidateEmail", sCode).getSingle();
		if(uNode!=null){
				
			System.out.println("Usernode: "+uNode);
			if(uNode!=null){
				uNode.setProperty("emailValidated", true);
				uNode.removeProperty("emailValidation");
				MainSystemController.renderIndexPage();
			}
		}
			tx.success();
		}
	}
}
