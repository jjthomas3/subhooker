package org.mirageone.subversion.subHooker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class EmailAssemblerTest extends TestCase {
	

	public void testFetch() {
		
		String auth="angry-coder";
		String chang="Changeset Information would normally be here\n\n\n\n\n\n\nIt could take several lines or more.";
		String diff = (readFileAsString("tests/mock.diff_test.txt"));
		String revision = "27982";
		String log = "I'm committing code change x because human resources can't find good programmers so on with the rework.";
		EmailAssembler emailAssembler = new EmailAssembler(auth, chang, diff, log, revision, "html", true, true);
		System.out.println("SAMPLE EMAIL OUTPUT BELOW, FAKE DIFFF BELOW");
		System.out.println(emailAssembler.applyTemplate());
		System.out.println("SAMPLE EMAIL OUTPUT ABOVE, FAKE DIFFF ABOVE");
		assertTrue((emailAssembler.applyTemplate()).toString() != null );
		emailAssembler = new EmailAssembler(auth, chang, diff, log, revision, "text", true, true);
		System.out.println("SAMPLE EMAIL OUTPUT BELOW, FAKE DIFFF BELOW");
		System.out.println(emailAssembler.applyTemplate());
		System.out.println("SAMPLE EMAIL OUTPUT ABOVE, FAKE DIFFF ABOVE");
		assertTrue((emailAssembler.applyTemplate()).toString() != null );
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
