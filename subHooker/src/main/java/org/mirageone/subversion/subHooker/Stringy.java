package org.mirageone.subversion.subHooker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Stringy 
{
	
	private String myString;
	
	public Stringy(String string)
	{
		this.myString=string;
	}
	
	public int countLines()
	{
		return cntLines(); 
	}
	
	public String getCurrentString()
	{
		return this.myString;
	}
	
	public String returnTopLines(int numLines)
	{
		return retTopLines(numLines);
	}
	
	private String retTopLines(int numLines)
	{
		String[] temp = this.myString.split("(\r\n|\r|\n|\n\r)");
	    String myReturn="";
	    for(int x=0;x<numLines;x++)
	    {
	    	if (x!=(numLines-1))
	    	{
	    		myReturn += (temp[x]+"\r\n");
	    	}else{
	    		myReturn += temp[x];
	    	}
	    }
		return myReturn;
	}
	
	private int cntLines()
	{
        Pattern patt = Pattern.compile("(\r\n|\r|\n|\n\r)");
        Matcher  match = patt.matcher(this.myString);
        //we start with a count of one because a single string with no line breaks is still one line.
        int count = 1;
        while (match.find())
        {
            count++;
        }
        return count; 
    }
	
}
