package org.mirageone.subversion.subHooker;

import org.apache.log4j.Logger;

public class HookMain {

	private static SimpleI18n simpleI18n = new SimpleI18n();
	private static Logger log = Logger.getLogger(HookMain.class);
	
	/*
	 * Program should take 3 arguments - 
	 *   1. Hook type, one of: pre or post
	 *   2. Repository path
	 *   3. txID or Revision number of commit
	 * @throws RuntimeException 
	 */
	
	public static void main(String[] args) throws RuntimeException {

		if (args.length!=3){
			String message=simpleI18n.getKey("COMMAND_INVALID_NUMBER_ARGS");
			log.error(message);
			throw new RuntimeException(message);
		}
		SvnHooks svnHook = new SvnHooks(args[0],args[1],args[2]);
		svnHook.run();
	}
	
}

