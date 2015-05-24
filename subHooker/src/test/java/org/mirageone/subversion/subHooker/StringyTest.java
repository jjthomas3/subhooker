package org.mirageone.subversion.subHooker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class StringyTest extends TestCase {

	private String testFile = "tests/mock.diff_test.txt";
	public void testStringy()
	{
		/// load up stringy with a String
		try
		{
			Stringy myStringy1 = new Stringy(readFileAsString(testFile));
			// load another one up with the top 10 lines from the first one
			Stringy myStringy2 = new Stringy(myStringy1.returnTopLines(20));
			// This should be true if the code works.
			System.out.println("===================== 20 LINES BELOW =====================");
			System.out.println(myStringy1.returnTopLines(20));
			System.out.println("===================== 20 LINES ABOVE =====================");
			assertEquals(20,myStringy2.countLines());
		} 
		catch (Exception e)
		{
			fail(e.getMessage());
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
