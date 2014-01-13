package controllers;

import org.neo4j.shell.util.json.JSONObject;

import emailers.ContactUsEmailer;
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
}
