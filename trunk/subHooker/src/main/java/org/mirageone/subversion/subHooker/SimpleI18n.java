package org.mirageone.subversion.subHooker;

	import java.util.ArrayList;
	import java.util.Enumeration;
	import java.util.List;
	import java.util.Locale;
	import java.util.ResourceBundle;

	public class SimpleI18n {

	    private ResourceBundle messages;
	    
		public SimpleI18n(){
			Locale locale = Locale.getDefault(); 
			this.messages =  ResourceBundle.getBundle("MessageBundle", locale);
		}
		
		public SimpleI18n(String messageBundle){
			Locale locale = Locale.getDefault(); 
			this.messages =  ResourceBundle.getBundle(messageBundle, locale);
		}
		
	    public SimpleI18n(Locale locale){
			this.messages =  ResourceBundle.getBundle("MessageBundle", locale);
	    }
	    
	    public SimpleI18n(Locale locale,String messageBundle){
			this.messages =  ResourceBundle.getBundle(messageBundle, locale);
	    }
	    
	    public SimpleI18n( String lang, String country){
			Locale currentLocale = new Locale(lang, country);
			this.messages = ResourceBundle.getBundle("MessageBundle", currentLocale);
		}
		
	    public SimpleI18n( String lang, String country, String messageBundle){
			Locale currentLocale = new Locale(lang, country);
			this.messages = ResourceBundle.getBundle(messageBundle, currentLocale);
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
		
		
		
}
