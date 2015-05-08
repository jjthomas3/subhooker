package org.mirageone.subversion.subHooker;

	import java.util.ArrayList;
	import java.util.Enumeration;
	import java.util.List;
	import java.util.Locale;
	import java.util.ResourceBundle;
	import java.io.*;
	import java.net.URL;
	import java.net.URLClassLoader;

	public class SimpleI18n {

	    private ResourceBundle messages;
	    private ClassLoader loader;
	    
		public SimpleI18n(){
			initializeBundles();
			Locale locale = Locale.getDefault(); 
			this.messages =  ResourceBundle.getBundle("MessageBundle", locale,loader);
		}
		
		public SimpleI18n(String messageBundle){
			initializeBundles();
			Locale locale = Locale.getDefault(); 
			this.messages =  ResourceBundle.getBundle(messageBundle, locale,loader);
		}
		
	    public SimpleI18n(Locale locale){
	    	initializeBundles();
			this.messages =  ResourceBundle.getBundle("MessageBundle", locale, loader);
	    }
	    
	    public SimpleI18n(Locale locale,String messageBundle){
	    	initializeBundles();
			this.messages =  ResourceBundle.getBundle(messageBundle, locale, loader);
	    }
	    
	    public SimpleI18n( String lang, String country){
	    	initializeBundles();
			Locale currentLocale = new Locale(lang, country);
			this.messages = ResourceBundle.getBundle("MessageBundle", currentLocale, loader);
		}
		
	    public SimpleI18n( String lang, String country, String messageBundle){
	    	initializeBundles();
			Locale currentLocale = new Locale(lang, country);
			this.messages = ResourceBundle.getBundle(messageBundle, currentLocale, loader);
		}
		
		public String getKey(String key){
			return getTranslatedString(key);
		}
		
		public String[] getKeys(){
			return enumKeys();
		}

		private String getTranslatedString(String key){
			return this.messages.getString(key);
		}
		
		private String[] enumKeys(){
			List<String> keys = new ArrayList<String>();
			for (Enumeration<String> e = this.messages.getKeys() ; e.hasMoreElements() ;) {
		        keys.add(e.nextElement());
		     }
			 return keys.toArray(new String[keys.size()]); 
		}
		
		private void initializeBundles()
		{
			try{
				File file = new File(System.getProperty("basedir") + File.separator + "i18nSupport");
				URL[] urls = {file.toURI().toURL()};
				loader = new URLClassLoader(urls);
			}catch(Exception e){
				
			}
		}
}
