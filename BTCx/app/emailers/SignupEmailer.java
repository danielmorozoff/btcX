package emailers;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import controllers.MainSystemController;

import play.mvc.Http.Request;
import serverLoggers.ServerLoggers;

public class SignupEmailer extends Emailer {

	
public void sendSignupEmail(String emailAddress, String inputedUsername, String fName, String secretEmailKey) throws Exception{
	   
	
		Properties props = new Properties();
	    props.put("mail.smtps.auth", "true");
	    
	    Session session = Session.getDefaultInstance(props);	
	    
	    MimeMessage msg = new MimeMessage(session); 
	   	msg.setFrom(new InternetAddress("***SOME INTERNET ADDRESS"));
	   	
	   	InternetAddress[] addresses = {new InternetAddress(emailAddress),};
        msg.setRecipients(Message.RecipientType.TO, addresses);
        
        msg.setSubject("News Rover Signup Information");
        msg.setSentDate(new Date());

        // Set message content
        msg.setText(fName+" welcome to "+MainSystemController.system_name+"!\n \n" +
        		"Thank you for registering for a pre-Alpha account. Just link back to "+MainSystemController.system_url+" and sign-in. Once you confirm your email, by providing us with this key: " +secretEmailKey+"\n"+
        		"You will have full access to the "+MainSystemController.system_name+" system. "+
        			"If you run into any issues or problems, please contact us at: "+
        			""+MainSystemController.system_email+" \n" +
        			"Or if you prefer, you may leave us annonymous feedback through our website. \n \n"+
        			"We are working hard to improve our system and your thoughts are very important to us! Be sure you share them, especially if they are critical. \n \n"+
        			"Sincerely Yours, \n \n"+
        			"The "+MainSystemController.system_name+" Team\n\n"+
        			"---Also for your records your "+MainSystemController.system_name+" username is: "+inputedUsername+"---"
        		);      			
        
	    Transport t = session.getTransport("smtps");
	    try {
	    ServerLoggers.infoLog.info("***Emailing user: "+inputedUsername+" email verification @ "+emailAddress+" ***");	
		t.connect(host, username, password);
		t.sendMessage(msg, msg.getAllRecipients());
	    } finally {
	    	//System.out.println("Emailing Transaction --  COMPLETE");
		t.close();
	    }
}

	

}

