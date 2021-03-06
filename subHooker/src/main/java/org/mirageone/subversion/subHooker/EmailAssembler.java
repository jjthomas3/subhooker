package org.mirageone.subversion.subHooker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.*;

public class EmailAssembler {
	
	private SimpleI18n i18n = new SimpleI18n("EmailBundle");
	private Map<String, String>valuesMap=null;
	
	private String strAuthor;
	private String strChanges;
	private String strDiff;
	private String strLog;
	private String strRevision;
	private Boolean showChangeset;
	private Boolean showDiff;
	private String mainFile;
	private String emailFormat;
	private String changeFile;
	private String diffFile;
	
	public EmailAssembler(String author, String changes, String diff, String log, String revision, String format, Boolean showDiff, Boolean showChangeset){

		this.strAuthor = author;
		this.strChanges = changes;
		this.strLog = log;
		this.strRevision = revision; 
		this.showChangeset = showChangeset;
		this.showDiff = showDiff;
		this.emailFormat = format;

		// This configures items specific to what format email is being set.
		if(emailFormat.equalsIgnoreCase("text")||emailFormat.equalsIgnoreCase("txt")){
			this.strDiff = diff;
			mainFile = (new File(System.getProperty("basedir") + File.separator + "emailTemplates"+ File.separator + "textTemplate-mainbody.txt")).toString();
			changeFile  = (new File(System.getProperty("basedir") + File.separator + "emailTemplates"+ File.separator + "textTemplate-changeset.txt")).toString();
			diffFile = (new File(System.getProperty("basedir") + File.separator + "emailTemplates"+ File.separator + "textTemplate-diff.txt")).toString();
		}else if(emailFormat.equalsIgnoreCase("html")||emailFormat.equalsIgnoreCase("htm")){
			this.strDiff = prepDiff(diff);
			mainFile = (new File(System.getProperty("basedir") + File.separator + "emailTemplates"+ File.separator + "htmlTemplate-mainbody.html")).toString();
			changeFile  = (new File(System.getProperty("basedir") + File.separator + "emailTemplates"+ File.separator + "htmlTemplate-changeset.html")).toString();
			diffFile = (new File(System.getProperty("basedir") + File.separator + "emailTemplates"+ File.separator + "htmlTemplate-diff.html")).toString();
		}
		loadValuesMap();
	}
	/**
	 * 
	 * @return
	 */
	public String applyTemplate(){
		return doIt();
	}

	private String doIt(){
		 /*
		  * Here we simply use apache.commons.lang3.text in conjunction with
		  * our value map, and we now have a simple email template system. 
		  */
		String templateString=readFileAsString(this.mainFile);
		StrSubstitutor sub = new StrSubstitutor(this.valuesMap);
		return sub.replace(templateString);
	}
	
	/*
	 * This code segment cleans up the DIFF statement if HTML formatted e-mail is selected
	 * this is necessary to prevent any HTML that could be in the DIFF from being interpreted
	 * rather than displayed.
	 */
	private String prepDiff(String diff){
			return StringUtils.replaceEach(diff, new String[]{"&", "\"", "<", ">"}, new String[]{"&amp;", "&quot;", "&lt;", "&gt;"});
	}
	
	private void loadValuesMap(){
		/*
		 * This builds a map of key/value pairs to be replaced in the email template,
		 * and includes the i18n strings as well...
		 */
	 	this.valuesMap = new HashMap<String, String>();
	 	String[] keys = i18n.getKeys();
		for (String key : keys){
			valuesMap.put(key,i18n.getKey(key));
		}
		
		valuesMap.put("SUB_AUTHOR", this.strAuthor);
		valuesMap.put("SUB_REVISION", this.strRevision);
		valuesMap.put("SUB_COMMITLOG", this.strLog);
		valuesMap.put("SUB_DIFF", this.strDiff);
		valuesMap.put("SUB_CHANGESET", this.strChanges);
		
		if(showChangeset){
			String changeset = readFileAsString(this.changeFile);
			valuesMap.put("SUB_TEMPLATE_CHANGESET", changeset);
		}else{
			valuesMap.put("SUB_TEMPLATE_CHANGESET", "");
		}
		
		if(showDiff){
			String diff = readFileAsString(this.diffFile);
			valuesMap.put("SUB_TEMPLATE_DIFF", diff);
		}else{
			valuesMap.put("SUB_TEMPLATE_DIFF", "");
		}
		 
	}
	
    private String readFileAsString(String filePath){
        try{
            byte[] buffer = new byte[(int) new File(filePath).length()];
            BufferedInputStream f=null;
            try {
                f = new BufferedInputStream(new FileInputStream(filePath));
                f.read(buffer);
            } finally {
                if (f != null) try { f.close(); } catch (IOException ignored) { System.out.println(ignored.getMessage()); }
            }
            return new String(buffer);

        }catch(java.io.IOException e){
        	return e.getMessage();
        }
    }
}
