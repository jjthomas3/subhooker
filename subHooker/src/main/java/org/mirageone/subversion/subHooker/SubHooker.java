package org.mirageone.subversion.subHooker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class SubHooker {
	
	private static Properties props = new Properties();
	private static File propsFile = new File(System.getProperty("basedir") + File.separator + "etc"+ File.separator + "subHooker.properties");
	private static String svnLookPath;
	private static File emailList;
	private static String mailServer;
	private static String mailPort;
	@SuppressWarnings("unused")
	private static String mailProtocol;
	private static String mailFromAddress;
	private static String mailAuthUser;
	private static String mailAuthPassword;
	private static String mailSubjectTag;
	private static String mailFormat;
	private static Boolean mailUseAuthentication;
	private static Boolean nagNoLog;
	private static Boolean postShowDiff;
	private static Boolean postShowChangeset;
	private static Boolean preCommitLogRequired;
	private static int preCommitMinLength;
	private static Boolean preCommitBliNumberRequired;
	private static String preCommitRegexSearch;
	private static SimpleI18n simpleI18n = new SimpleI18n();
	private static String REV_PROP;
	private static String REPO_PATH;
	private static String HOOK_TYPE;
	private static Logger log = Logger.getLogger(SubHooker.class);
	private static String[] emailRecipients;
	
	/*
	 * Program should take 3 arguments - 
	 *   1. Hook type, one of: pre or post
	 *   2. Repository path
	 *   3. txID or Revision number of commit
	 * @throws RuntimeException 
	 */
	
	public static void main(String[] args) throws RuntimeException {

		if (args.length!=3){
			String message=simpleI18n.getKey("COMMAND_INVALID_NUMBER_ARGS");
			log.error(message);
			throw new RuntimeException(message);
		}
		
		HOOK_TYPE = args[0];
		REPO_PATH = args[1];
		REV_PROP = args[2];
		
		initHooker();
		log.info("subHooker initialized, Subversion Hooker: | HOOK_TYPE: "+HOOK_TYPE+" | REPO_PATH: "+REPO_PATH+" | REV_PROP: "+REV_PROP);
		
		if(HOOK_TYPE.equalsIgnoreCase("post")){
			postCommit();
		}else if(HOOK_TYPE.equalsIgnoreCase("pre")){
			preCommit();
		} else {
			String message = simpleI18n.getKey("COMMAND_INVALID_HOOK_TYPE");
			log.error(message);
			System.err.println(message);
			throw new RuntimeException(message);
		}
	}
	
	private static void postCommit() throws RuntimeException {
		
		String[] getAuthor = {svnLookPath, "author", REPO_PATH, "-r", REV_PROP};
		String[] getLog = {svnLookPath, "log", REPO_PATH, "-r", REV_PROP};
		String[] getChanges = {svnLookPath, "changed", REPO_PATH, "-r", REV_PROP};
		String[] getDiffs = {svnLookPath, "diff", REPO_PATH, "-r", REV_PROP};
		
		Program command = new Program();
		FileArrayProvider recipients = new FileArrayProvider();

		try {
			emailRecipients = recipients.readLines(emailList.toString());
		} catch (IOException e) {
			log.error(simpleI18n.getKey("POST_COMMIT_NO_EMAIL_LIST"));
			System.err.println(simpleI18n.getKey("POST_COMMIT_NO_EMAIL_LIST"));
			throw new RuntimeException(); 
		}
		
		String auth=(command.run(getAuthor)).replace(System.getProperty("line.separator"), "");
		String svnLog=command.run(getLog);
		String change;
		String diff;
		
		//No need wasting resources getting DIFF and Changeset info if they are disabled.
		if(postShowChangeset){
			change=command.run(getChanges);
		}else{
			change="";
		}
		
		if(postShowDiff){
			diff=command.run(getDiffs);
		}else{
			diff="";
		}
		
		if(svnLog.length()<=3 && nagNoLog){
			//THIS IS TOTALLY A NAG LINE, IT TAKES TWO MINIUTES TO PUT A USEFUL NOTE IN THE COMMIT LOG
			svnLog+=simpleI18n.getKey("POST_COMMIT_NO_LOG_NAG");
		}
		EmailAssembler commitMail = new EmailAssembler(auth, change, diff, svnLog, REV_PROP, mailFormat, postShowDiff,postShowChangeset);
		String commitMessage=commitMail.fetch();
		
		SMTPClient client = new SMTPClient(mailServer,mailPort,mailAuthUser,mailAuthPassword,mailUseAuthentication);
		client.sendMail(emailRecipients, mailFromAddress,  mailSubjectTag  + " Author: " + auth + " Revision: " + REV_PROP , commitMessage, mailFormat);
		
	}
	
	private static void preCommit() throws RuntimeException{
		String[] getPreAuthor = {svnLookPath, "author", REPO_PATH, "-t", REV_PROP};
		String[] getPreLog = {svnLookPath, "log", REPO_PATH, "-t", REV_PROP};
	
		Program command = new Program();
		String auth=command.run(getPreAuthor).replace(System.getProperty("line.separator"), "");
		String svnLog=command.run(getPreLog);
		
		log.debug("Pre-commit routine called with the following paraameters:");
		log.debug("Commit Log Required                   : "+preCommitLogRequired.toString());
		log.debug("Backlog or Defect Identifier Required : "+preCommitBliNumberRequired.toString());
		log.debug("Commit Log length                     : "+svnLog.length());
		log.debug("Log Message                           : " + svnLog);
		
		if(preCommitLogRequired){
			if(svnLog.length() <= preCommitMinLength){
				log.error("REJECTING commit based on no commit log, author :" +auth);
				throw new RuntimeException(simpleI18n.getKey("PRECOMMIT_REJECTED_COMMIT_COMMENTS_REQUIRED"));
			}
			
			if(preCommitBliNumberRequired){
				Pattern myPattern = Pattern.compile(preCommitRegexSearch);
				Matcher matcher = myPattern.matcher(svnLog);
				if(!matcher.find()){
					log.error("REJECTING commit based on missing Backlog or Defect Number, author :" +auth);
					throw new RuntimeException(simpleI18n.getKey("PRECOMMIT_REJECTED_FAILED_REGEX"));		
				}
			}
		}
	}
	
	private static boolean loadProps(Properties properties, File propsFile) {
		
		Boolean success=true;
		FileInputStream fs =  null;
		
		try {
			fs = new FileInputStream(propsFile);
			properties.load(fs);
			fs.close();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
			success = false;
		}
		return success;
	}
	
	private static boolean initHooker() throws RuntimeException{
		if (!loadProps(props, propsFile)) {
			String message = simpleI18n.getKey("CONFIGURATION_INIT_ERR_LOAD_PROPS");
			log.error(message);
			throw new RuntimeException(message);
		} else {			
			mailServer = props.getProperty("mail.server.name");
			mailPort = props.getProperty("mail.server.port");
			mailProtocol = props.getProperty("mail.server.protocol");
			mailFromAddress = props.getProperty("mail.user.fromAddress");
			mailUseAuthentication=Boolean.parseBoolean(props.getProperty("mail.server.useAuth"));
			mailAuthUser=props.getProperty("mail.user.auth.name");
			mailAuthPassword=props.getProperty("mail.user.auth.password");
			mailSubjectTag=props.getProperty("mail.message.subjectTag", "Subversion");
			/*
			 * if Mail format is missing from propertied file it will default to a nice HTML Formatted message.
			 */
			mailFormat=props.getProperty("mail.message.format", "HTML");
			svnLookPath = props.getProperty("svn.lookPath");
			emailList = new File(props.getProperty("svn.recipiantList"));
			nagNoLog = Boolean.parseBoolean(props.getProperty("svn.nagNoLogCommits"));
			
			preCommitLogRequired = Boolean.parseBoolean(props.getProperty("svn.precommit.CommitLogManditory"));
			preCommitBliNumberRequired = Boolean.parseBoolean(props.getProperty("svn.precommit.BliOrDefectNumberRequired"));
			preCommitRegexSearch = props.getProperty("svn.precommit.BliRegex");
			
			postShowDiff = Boolean.parseBoolean(props.getProperty("email.contents.show.diff"));
			postShowChangeset = Boolean.parseBoolean(props.getProperty("email.contents.show.changeset"));
			
			/*			
			 * Below, We add 2 to the minimal length because an empty commit log is 2 characters long.
			 * This way if you decide to disable the feature by setting min length to 0 it will
			 * work as anticipated
			 */
			
			preCommitMinLength = (Integer.parseInt(props.getProperty("svn.precommit.CommitLogMinLength"))+2);
		}
		return true;
	}
	
}

