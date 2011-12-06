package org.mirageone.subversion.subHooker;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SimpleI18nTest extends TestCase {


	public void testGetKey() {
		SimpleI18n i18n = new SimpleI18n("en","EN","TestBundle");
		Assert.assertEquals("one", i18n.getKey("ONE"));
	}
	
	public void testGetKeys(){
		SimpleI18n i18n = new SimpleI18n("en","EN", "TestBundle");
		String[] myI18nKeys = i18n.getKeys();
		System.out.println("Enumerating Test MessageBundle Keys");
		for (String key : myI18nKeys){
			System.out.println("\t"+key);
		}
		Assert.assertEquals(10, myI18nKeys.length);
	}

}
