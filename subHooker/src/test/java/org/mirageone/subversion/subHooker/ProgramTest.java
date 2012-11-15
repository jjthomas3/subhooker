package org.mirageone.subversion.subHooker;

import junit.framework.TestCase;

public class ProgramTest extends TestCase {

	public void testRun() {
		String[] cmdOutput = null;
		Program test = new Program();
		
		if(isWindows()){
			System.out.println("Testing ability to launch program with arguments (Windows): ");
			System.out.println("   Launching cmd.exe /c time /t");
			cmdOutput = new String[]{"cmd.exe", "/c", "time", "/t"};
		}else if(isMac()){
			System.out.println("Testing ability to launch program with arguments (Apple OS): ");
			System.out.println("   Launching ping -c 2 localhost");
			cmdOutput = new String[]{"ping", "-c", "2", "localhost"};
		}else if(isUnix()){
			System.out.println("Testing ability to launch program with arguments (Linux/Unix): ");
			System.out.println("   Launching ping -c 2 localhost");
			cmdOutput = new String[]{"ping", "-c", "2", "localhost"};
		}else{
			System.out.println("Your OS is not support!!");
			cmdOutput = new String[]{"whoami", "/f"};
		}

		String result = (test.run(cmdOutput));
		System.out.println(result);
		assertEquals(test.getExitCode(),0);
		assertNotNull(result);
	}
	
	 
		public static boolean isWindows(){
			String os = System.getProperty("os.name").toLowerCase();
		    return (os.indexOf( "win" ) >= 0); 
		}
	 
		public static boolean isMac(){
			String os = System.getProperty("os.name").toLowerCase();
		    return (os.indexOf( "mac" ) >= 0); 
		}
	 
		public static boolean isUnix(){
			String os = System.getProperty("os.name").toLowerCase();
		    return (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0);
		}
}
