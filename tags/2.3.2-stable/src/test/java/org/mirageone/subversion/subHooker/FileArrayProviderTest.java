package org.mirageone.subversion.subHooker;

import java.io.IOException;

import junit.framework.TestCase;

public class FileArrayProviderTest extends TestCase {

	public void testReadLines() {
		FileArrayProvider recipients = new FileArrayProvider();
		try {
			//This tests the array provider, which turns a flat file list of e-mail recipients into an array
			//It needs to be able to handle blank lines, and comment ## statements
			String[] emailList = recipients.readLines("tests/fileArrayProvider_test.txt");
			//Test Array Length
			System.out.println("");
			System.out.println("   Testing email array length, should be 6            : " + emailList.length);
			assertEquals(6, emailList.length);
			//Test first Array Element
			System.out.println("   First array element, should be user-one@test.case  : " + emailList[0]);
			assertEquals("user-one@test.case",emailList[0]);
			//Test Last Array Element
			System.out.println("   Last array element, should be user-six@test.case   : " + emailList[5]);
			assertEquals("user-six@test.case",emailList[5]);
			System.out.println("");
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}
}
