package br.com.dabage.investments.mail;
 
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class SendMailSSL {

	private static Logger log = Logger.getLogger(SendMailSSL.class);

	public static void send(String subject, String textContent, File file) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
 
		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("dogaum","T1@o0783");
				}
			});
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("dogaum@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("dogaum@gmail.com"));

			message.setSubject(subject);

			if (file != null) {
				// creates message part
		        MimeBodyPart messageBodyPart = new MimeBodyPart();
		        messageBodyPart.setContent(textContent, "text/html");
		 
		        // creates multi-part
		        Multipart multipart = new MimeMultipart();
		        multipart.addBodyPart(messageBodyPart);
		 
                MimeBodyPart attachPart = new MimeBodyPart();
 
                try {
                    attachPart.attachFile(file);
                } catch (IOException ex) {
                	log.error(ex);
                }
 
                multipart.addBodyPart(attachPart);
	 
		        // sets the multi-part as e-mail's content
                message.setContent(multipart);
			} else {
				message.setText(textContent);
			}

			Transport.send(message);
 
		} catch (MessagingException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}
}