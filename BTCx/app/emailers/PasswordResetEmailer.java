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

import server.loggers.ServerLoggers;
import controllers.MainSystemController;

public class PasswordResetEmailer extends Emailer {

	public void sendPasswordResetEmail(String emailAddress,String inputedUserName, String secretResetKey) throws AddressException, MessagingException{
		Properties props = new Properties();
	    props.put("mail.smtps.auth", "true");
	    
	    Session session = Session.getDefaultInstance(props);	
	    
	    MimeMessage msg = new MimeMessage(session); 
	   	msg.setFrom(new InternetAddress(Emailer.username));
	   	
	   	InternetAddress[] addresses = {new InternetAddress(emailAddress),};
        msg.setRecipients(Message.RecipientType.TO, addresses);
        
        msg.setSubject("CryptRex Signup Information");
        msg.setSentDate(new Date());

        // Set message content
        msg.setText(
        		"Hi There!\n" +
        		"We heard you were trying to reset your password. We have generated a unique link for you to do so. It's right below \n\n" +
        		MainSystemController.system_url+"/reset?code="+secretResetKey+"&uName="+inputedUserName+" \n\n"+
        		"If you did not request this email, please ignore it.\n" +
        		"The CryptRex Team"
        		);      			
        
	    Transport t = session.getTransport("smtps");
	    try {
	    ServerLoggers.infoLog.info("***Emailing Password reset to : "+inputedUserName+" email verification @ "+emailAddress+" ***");	
		t.connect(host, username, password);
		t.sendMessage(msg, msg.getAllRecipients());
	    } finally {
	    	//System.out.println("Emailing Transaction --  COMPLETE");
		t.close();
	    }	
	}

}
