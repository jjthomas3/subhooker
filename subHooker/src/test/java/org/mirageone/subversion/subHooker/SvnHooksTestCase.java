package org.mirageone.subversion.subHooker;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SvnHooksTestCase extends TestCase {
	
	public void testInitializeProps() {
		SvnHooks hookInit = new SvnHooks("Post", "C:\\mock\\file\\path" , "22345");
		Assert.assertNotNull(hookInit);
	}
	
}
