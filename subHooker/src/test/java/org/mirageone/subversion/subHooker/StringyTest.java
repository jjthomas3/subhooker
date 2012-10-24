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
		Stringy myTooledString = new Stringy(readFileAsString(testFile));
		System.out.println(myTooledString.returnTopLines(10));
		assertEquals( myTooledString.countLines(), 74);
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
