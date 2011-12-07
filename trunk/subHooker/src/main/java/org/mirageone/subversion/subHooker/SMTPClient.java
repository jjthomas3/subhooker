package org.mirageone.subversion.subHooker;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class SMTPClient {

	private boolean useAuthentication = false;
	private String smtpServerHost = "localhost";
	private String smtpServerPort = "25";
	private String smptUserName;
	private String smtpPassword;
	private String smtpProtocol;
	private static Logger log = Logger.getLogger(SMTPClient.class);
	
	
	public SMTPClient (String host, String port, String user, String pass, boolean auth){
		this.smtpServerHost = host;
		this.smtpServerPort = port;
		this.smptUserName = user;
		this.smtpPassword = pass;
		this.useAuthentication = auth;
		this.smtpProtocol = "smtp";
	}
	
	public void SendMail (String to[],String from, String subject, String content, String format) {
		SendMessage(to,from,subject,content,format);
	}
	
	private void SendMessage(String to[],String from, String subject, String content, String format)  {
		try{
			int count = to.length;
			Message message = new MimeMessage(getSession());
	        InternetAddress[] recipiants = new InternetAddress[count];
	        // assemble the recipients array.
	        for (int i=0; i<count; i++){
	        	recipiants[i] = new InternetAddress(to[i]); 
	        }
	        message.addRecipients(Message.RecipientType.TO, recipiants);
			message.addFrom(new InternetAddress[] { new InternetAddress(from)});
			message.setSubject(subject);
			
			if(format.equalsIgnoreCase("html")||format.equalsIgnoreCase("htm")){
				format="text/html; charset=ISO-8859-1";
			}else if(format.equalsIgnoreCase("text")||format.equalsIgnoreCase("txt")){
				format="text/plain";
			}
			message.setContent(content, format);
			Transport.send(message);
		}
		catch(MessagingException e)
		{
			log.error(e.getLocalizedMessage());
		}
	}

	private Session getSession() {
		Authenticator authenticator = new Authenticator(this.smptUserName,this.smtpPassword);
		Properties properties = new Properties();
		if(this.useAuthentication){
			properties.setProperty("mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName());
			properties.setProperty("mail.smtp.auth", String.valueOf(this.useAuthentication));
		}else{
			properties.setProperty("mail.smtp.auth", String.valueOf(this.useAuthentication));
		}
		properties.setProperty("mail.smtp.host", this.smtpServerHost);
		properties.setProperty("mail.smtp.port", this.smtpServerPort);
		properties.setProperty("mail.transport.protocol", this.smtpProtocol);
		return Session.getInstance(properties, authenticator);
	}

	private class Authenticator extends javax.mail.Authenticator {
		private PasswordAuthentication authentication;
		public Authenticator(String username, String password) {
			authentication = new PasswordAuthentication(username, password);
		}
		protected PasswordAuthentication getPasswordAuthentication() {
			return authentication;
		}
	}
}
