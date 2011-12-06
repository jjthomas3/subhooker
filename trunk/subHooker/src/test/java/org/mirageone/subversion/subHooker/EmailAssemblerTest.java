package org.mirageone.subversion.subHooker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class EmailAssemblerTest extends TestCase {
	
	private String fakeDiff = (new File(System.getProperty("basedir") + File.separator + "tests"+ File.separator + "mock.diff_test.txt")).toString();

	public void testFetch() {
		String auth="angry-coder";
		String chang="Changeset Information would normally be here\n\n\n\n\n\n\nIt could take several lines or more.";
		String diff = readFileAsString(fakeDiff);
		String revision = "27982";
		String log = "I'm committing code change x because human resources can't find good programmers so on with the rework.";
		EmailAssembler emailAssembler = new EmailAssembler(auth, chang, diff, log, revision, "html", true, true);
		System.out.println("SAMPLE EMAIL OUTPUT BELOW, FAKE DIFFF BELOW");
		System.out.println(emailAssembler.fetch());
		System.out.println("SAMPLE EMAIL OUTPUT ABOVE, FAKE DIFFF ABOVE");
		Assert.assertTrue((emailAssembler.fetch()).toString() != null );
		emailAssembler = new EmailAssembler(auth, chang, diff, log, revision, "text", true, true);
		System.out.println("SAMPLE EMAIL OUTPUT BELOW, FAKE DIFFF BELOW");
		System.out.println(emailAssembler.fetch());
		System.out.println("SAMPLE EMAIL OUTPUT ABOVE, FAKE DIFFF ABOVE");
		Assert.assertTrue((emailAssembler.fetch()).toString() != null );
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
