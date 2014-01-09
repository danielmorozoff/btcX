package controllers;

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
	public static void sendContactUsEmail(String request, String email, String subject, String message) throws Exception{
		if(request.equals("contact")) new ContactUsEmailer().sendSignupEmail(email, subject, message);
	}
}
