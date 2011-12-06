package org.mirageone.subversion.subHooker;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// This class needs replacing, this is student level code.

public class SMTPMailer {
	
	private Properties props = new Properties();
	private boolean ready = false;
	private String mailFormat;
	
    public SMTPMailer(String protocol, String host ,String port, String format){
        this.props.setProperty("mail.transport.protocol", protocol);
        this.props.setProperty("mail.host", host);
        this.props.setProperty("mail.port", port);
        this.mailFormat = format;
        this.ready=true;
    }
    
    public SMTPMailer(String protocol, String host, String port, String format, String user, String pswd){
    	Authenticator authenticator = new Authenticator(user,pswd);
    	this.props.setProperty("mail.transport.protocol", protocol);
    	this.props.setProperty("mail.host", host);
    	this.props.setProperty("mail.port",port);
    	this.props.setProperty("mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName());
    	this.props.setProperty("mail.smtp.auth", "true");
    	this.mailFormat = format;
        this.ready=true;
    }
    
    public void sendMessage(String to[],String from, String subject, String content){
    	if (ready){
    		this.props.setProperty("mail.from", from);
    		SendHTMLmessage(to, subject, content);
    	} else {
    		throw new RuntimeException("SMTPMailer.class -> The SMTP Mailer class was called before it was initialized. The commit was successful, however the commit hook failed");
    	}
    }

	private void SendHTMLmessage(String to[], String subject, String content){
		 
	    try {

	    	Session mailSession = Session.getDefaultInstance(props, null);
	        Transport transport = mailSession.getTransport();
	        MimeMessage message = new MimeMessage(mailSession);
	        message.setSubject(subject);
	        
	        if(this.mailFormat.equalsIgnoreCase("html")||this.mailFormat.equalsIgnoreCase("htm")){
	        	message.setContent(content, "text/html; charset=ISO-8859-1");
	        } else {
	        	message.setContent(content, "text/plain");
	        }
	        
	        int count = to.length;
	        InternetAddress[] recipiants = new InternetAddress[count];
	  
	        for (int i=0; i<count; i++){
	        	recipiants[i] = new InternetAddress(to[i]); 
	        }
	        
	        message.addRecipients(Message.RecipientType.TO, recipiants);
	        
	        transport.connect();
	        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
	        transport.close();

		} catch (NoSuchProviderException e) {
			// Host unreachable or invalid
    		throw new RuntimeException("SMTPMailer.class -> The mail server specified could not be reached. The commit was successful, however the commit hook failed to send an e-mail.");
		} catch (MessagingException e) {
			// Not sure what would get caught in this.
			e.printStackTrace();
		}
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

