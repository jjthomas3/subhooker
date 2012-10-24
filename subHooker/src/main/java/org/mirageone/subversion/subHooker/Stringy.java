package org.mirageone.subversion.subHooker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Stringy 
{
	
	private String myString;
	
	public Stringy(String string){
		this.myString=string;
	}
	
	public String returnTopLines(int numLines)
	{
		String[] temp = this.myString.split("(\r\n|\r|\n|\n\r)");
	    String myReturn="";
	    for(int x=0;x<numLines;x++){
	    	if (x!=(numLines-1)){
	    		myReturn += (temp[x]+"\r\n");
	    	}else{
	    		myReturn += temp[x];
	    	}
	    }
		return myReturn;
	}
	
	public int countLines()
	{
        Pattern pattern = Pattern.compile("(\r\n|\r|\n|\n\r)");
        Matcher  matcher = pattern.matcher(this.myString);
        int count = 0;
        while (matcher.find())
            count++;
        return count; 
    }
	
}
