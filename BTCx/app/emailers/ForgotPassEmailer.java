package emailers;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import controllers.MainSystemController;

import play.cache.Cache;
import server.loggers.ServerLoggers;

import databases.BTCxDatabase;
import databases.objects.*;
import databases.objects.users.User;


public class ForgotPassEmailer extends Emailer{
	
	public void sendForgotPassEmail(String email, String resetLink, boolean allowToReset) throws Exception{
		if(allowToReset){   
			Node userNode = BTCxDatabase.USER_INDEX.get("email", email).getSingle();
			if(userNode!=null){
				User userObj = (User) new BTCxObject().convertNodeToObject(userNode, null, new User());
				
				Properties props = new Properties();
				props.put("mail.smtp.host", Emailer.host);
				props.put("mail.smtp.socketFactory.port", "465");
				props.put("mail.smtp.socketFactory.class",
						"javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.port", "465");
		 
				Session session = Session.getDefaultInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(Emailer.username,Emailer.password);
						}
					});
		 
				try {
		 
					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress(Emailer.username));
					message.setRecipients(Message.RecipientType.TO,
							InternetAddress.parse(email));
					message.setSubject(""+MainSystemController.system_name+" Password Reset");
					message.setText("Hi "+userObj.firstName+"," +
							"\n\nWe heard you forgot your password! No big deal- happens to us all the time. \n" +
							"Just click on the link below and reset your password. Please note that this link expires in 10mn and will" +
							" no longer work. \n\n" +
							 resetLink+
							 
							"\n\n Thanks,\n" +
							""+MainSystemController.system_name+" Team");
					ServerLoggers.infoLog.info("***Emailing user password reset link: "+email+" ***");
					Transport.send(message);
		 
					System.out.println("Done");
		 
				} catch (MessagingException e) {
					throw new RuntimeException(e);
				}
			}
			
			
			
		}
	}


}
