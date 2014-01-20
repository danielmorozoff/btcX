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



import play.mvc.Http.Request;
import server.loggers.ServerLoggers;

public class ContactUsEmailer extends Emailer {

		
public void sendSignupEmail(String emailAddress,  String subject, String mssg) throws Exception{
	   
	
		Properties props = new Properties();
	    props.put("mail.smtps.auth", "true");
	    
	    Session session = Session.getDefaultInstance(props);	
	    
	    MimeMessage msg = new MimeMessage(session); 
	   	msg.setFrom(new InternetAddress(emailAddress));
	   	
	   	InternetAddress[] addresses = {new InternetAddress(ContactUsEmailAddress),};
        msg.setRecipients(Message.RecipientType.TO, addresses);
        
        msg.setSubject("[CryptRex User Contact] "+subject);
        msg.setSentDate(new Date());

        // Set message content
        msg.setText(mssg);      			
        
	    Transport t = session.getTransport("smtps");
	    try {
		    ServerLoggers.infoLog.info("***Email Question  from "+emailAddress+" ***");	
			t.connect(host, username, password);
			t.sendMessage(msg, msg.getAllRecipients());
	    } finally {
	    	t.close();
	    }
}

	

}

