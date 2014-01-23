package emailers;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import serverLoggers.ServerLoggers;

public class WelcomeEmailer extends Emailer {
	
	private static String  emailMssg = "Welcome to CryptREX, \n\n" +
    		"Thank you for signing up with us! We are working hard during this beta stage to ensure that you will maximize your buck once we are up and running. \n " +
    		"In the meantime check out our growing map. As we reach out to the rest of the world with our new merchant and trading tools, you will see more and more businesses pop up that you never knew accepted Bitcoin.\n\n" +
    		"If you have any questions, concerns, or suggestions, please email us at support@cryptrex.com. \n\n"+ 
    		"Thank you for your patience, \n" +
    		"Team CryptREX";
	
	/**
	 * @param args
	 * @throws MessagingException 
	 * @throws AddressException 
	 */
	public static void sendWelcomeEmail(String emailAddress) throws AddressException, MessagingException {
		Properties props = new Properties();
	    props.put("mail.smtps.auth", "true");
	    
	    Session session = Session.getDefaultInstance(props);	
	    
	    MimeMessage msg = new MimeMessage(session); 
	   	msg.setFrom(new InternetAddress(username));
	   	
	   	InternetAddress[] addresses = {new InternetAddress(emailAddress)};
        msg.setRecipients(Message.RecipientType.TO, addresses);
        
        msg.setSubject("Welcome To CryptRex! ");
        msg.setSentDate(new Date());

        // Set message content
        msg.setText(emailMssg);
	    Transport t = session.getTransport("smtps");
	    try {
		    ServerLoggers.infoLog.info("***Emailing welcome to "+emailAddress+" ***");	
			t.connect(host, username, password);
			t.sendMessage(msg, msg.getAllRecipients());
	    } finally {
	    	t.close();
	    }
	}

}
