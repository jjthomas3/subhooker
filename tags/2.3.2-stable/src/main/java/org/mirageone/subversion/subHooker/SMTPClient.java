package org.mirageone.subversion.subHooker;

import java.util.Properties;

import javax.mail.AuthenticationFailedException;
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
	
	/**
	 * SMTPClient used for sending SMTP Messages
	 * @param host The SMTP Server's name or IP Address
	 * @param port The port the SMTP Server is listening on, typically 25
	 * @param user If necessary, the user to use for Authentication
	 * @param pass If necessary, the password of the authenticating user
	 * @param auth Set to true if security is required.
	 */
	public SMTPClient (String host, String port, String user, String pass, boolean auth){
		this.smtpServerHost = host;
		this.smtpServerPort = port;
		this.smptUserName = user;
		this.smtpPassword = pass;
		this.useAuthentication = auth;
		this.smtpProtocol = "smtp";
		
		if(log.isDebugEnabled()){
			debugDump();
		}
	}
	
	public void sendMail (String to[],String from, String subject, String content, String format) {
		sendMessage(to,from,subject,content,format);
	}
	
	private void sendMessage(String to[],String from, String subject, String content, String format)  {
		try{
			
			Message message = new MimeMessage(getSession());
			// assemble the recipients array, and add it to our message.
			int count = to.length;
	        InternetAddress[] recipiants = new InternetAddress[count];
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
		catch (AuthenticationFailedException e)
		{
			log.error("Authentication Failed, please check your configuration and try again.");
		}
		catch(MessagingException e)
		{
			log.error(e.getLocalizedMessage());
		}
	}
	
	private void debugDump(){
		log.debug("SMTP Server Host                       : "+this.smtpServerHost);
		log.debug("SMTP Server Port                       : "+this.smtpServerPort);
		log.debug("SMTP Server Use Authentication         : "+String.valueOf(this.useAuthentication));
		log.debug("SMTP Server User                       : "+this.smptUserName);
		log.debug("SMTP Server Password                   : Please see properties file.");
		log.debug("SMTP Transport Protocol                : "+this.smtpProtocol);
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

	private static class Authenticator extends javax.mail.Authenticator {
		private PasswordAuthentication authentication;
		public Authenticator(String username, String password) {
			authentication = new PasswordAuthentication(username, password);
		}
		protected PasswordAuthentication getPasswordAuthentication() {
			return authentication;
		}
	}
}
