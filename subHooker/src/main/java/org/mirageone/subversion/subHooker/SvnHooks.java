package org.mirageone.subversion.subHooker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;


/**
 * @author John J Thomas
 * 
 *
 */
public class SvnHooks {
	
	private Properties props = new Properties();
	private File propsFile = new File(System.getProperty("basedir") + File.separator + "etc"+ File.separator + "subHooker.properties");
	private String svnLookPath;
	private File emailList;
	private String mailServer;
	private String mailPort;
	@SuppressWarnings("unused")
	private String mailProtocol;
	private String mailFromAddress;
	private String mailAuthUser;
	private String mailAuthPassword;
	private String mailSubjectTag;
	private String mailFormat;
	private Boolean mailUseAuthentication;
	private Boolean nagNoLog;
	private Boolean postShowDiff;
	private Boolean postShowChangeset;
	private Boolean preCommitLogRequired;
	private int preCommitMinLength;
	private Boolean preCommitBliNumberRequired;
	private String preCommitRegexSearch;
	private SimpleI18n simpleI18n = new SimpleI18n();
	private String REV_PROP;
	private String REPO_PATH;
	private String HOOK_TYPE;
	private Logger log = Logger.getLogger(SvnHooks.class);
	private String[] emailRecipients;
	
	/**
	 * SvnHooks Constructor, inputs hook type, repository path, and revision or transaction id. 
	 * @param HOOK_TYPE : One of two value; "pre" for pre commit hook, or "post" for post commit hook
	 * @param REPO_PATH : The local path, on the server, to the repository, typically supplied by the server.
	 * @param REV_PROP  : The revision property or transaction id of the commit, also usually supplied by the server.
	 */
	public SvnHooks(String HOOK_TYPE, String REPO_PATH, String REV_PROP){
		this.HOOK_TYPE = HOOK_TYPE;
		this.REPO_PATH = REPO_PATH;
		this.REV_PROP = REV_PROP;
		initSvnHooks();
		log.info("SvnHooks class initialized, ready to run.");
		log.debug("Hook type is		:"+HOOK_TYPE);
		log.debug("Repository Path	:"+REPO_PATH);
		log.debug("Revision Prop	:"+REV_PROP);
	}
	
	public void run(){
		if (HOOK_TYPE.equalsIgnoreCase("pre")){
			log.info("Running Subversion Pre-commit hook.");
			preCommit();
		}else if(HOOK_TYPE.equalsIgnoreCase("post")){
			log.info("Running Subversion Post-commit hook.");
			postCommit();
		}
	}
	
	private void postCommit() throws RuntimeException {
		
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
		String commitMessage=commitMail.applyTemplate();
		
		SMTPClient client = new SMTPClient(mailServer,mailPort,mailAuthUser,mailAuthPassword,mailUseAuthentication);
		client.sendMail(emailRecipients, mailFromAddress, mailSubjectTag + " Author: " + auth + " Revision: " + REV_PROP , commitMessage, mailFormat);
		
	}
	
	private void preCommit() throws RuntimeException{
		String[] getPreAuthor = {svnLookPath, "author", REPO_PATH, "-t", REV_PROP};
		String[] getPreLog = {svnLookPath, "log", REPO_PATH, "-t", REV_PROP};
	
		Program command = new Program();
		String auth=command.run(getPreAuthor).replace(System.getProperty("line.separator"), "");
		String svnLog=command.run(getPreLog);
		
		log.debug("Pre-commit routine called with the following paraameters:");
		log.debug("Commit Log Required          : "+preCommitLogRequired.toString());
		log.debug("Backlog or Defect Identifier Required : "+preCommitBliNumberRequired.toString());
		log.debug("Commit Log length           : "+svnLog.length());
		log.debug("Log Message              : " + svnLog);
		
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
	
	private boolean loadProps(Properties properties, File propsFile) {
		Boolean success=true;
		FileInputStream fs = null;
		
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
	
	private boolean initSvnHooks() throws RuntimeException{
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
			 * if Mail format is missing from properties file it will default to a nice HTML Formatted message.
			 */
			mailFormat=props.getProperty("mail.message.format", "HTML");
			svnLookPath = props.getProperty("svn.lookPath", "svnlook"); // might get lucky, it could be in the path
			emailList = new File(props.getProperty("svn.recipiantList"));
			nagNoLog = Boolean.parseBoolean(props.getProperty("svn.nagNoLogCommits"));
			
			preCommitLogRequired = Boolean.parseBoolean(props.getProperty("svn.precommit.CommitLogManditory"));
			preCommitBliNumberRequired = Boolean.parseBoolean(props.getProperty("svn.precommit.BliOrDefectNumberRequired"));
			preCommitRegexSearch = props.getProperty("svn.precommit.BliRegex");
			
			postShowDiff = Boolean.parseBoolean(props.getProperty("email.contents.show.diff", "true"));
			postShowChangeset = Boolean.parseBoolean(props.getProperty("email.contents.show.changeset", "true"));
			
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
