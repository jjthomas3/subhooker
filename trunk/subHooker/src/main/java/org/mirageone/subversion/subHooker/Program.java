package org.mirageone.subversion.subHooker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.log4j.Logger;


public class Program {
	
	private int exitCode=-1;
	static Logger log = Logger.getLogger(Program.class);
	
	public Program(){
		
	}
	
	private void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}
	
	public int getExitCode(){
		return this.exitCode;
	}
	
	public String run(String args[]){
		return runProgram(args);
	}
	
	private String runProgram(String args[]){
		String line="";
		String out="";
		String path=System.getProperty("java.io.tmpdir");
		String osLineSep=System.getProperty("line.separator");
		Process process=null;
		try {
			process = new ProcessBuilder((args))
			.directory(new File(path))
			.start();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line+osLineSep);
			}
			br.close();
			process.waitFor();
			out=sb.toString();
			int eCode=process.exitValue();
			if(eCode==0){
				log.debug("Successfully launched command \"" + Arrays.toString(args) + "\"");
			}else{
				log.error( "running "+ Arrays.toString(args)+" raised errorcode "+eCode);
				System.err.println("running "+ Arrays.toString(args)+" raised errorcode "+eCode);
			}
			this.setExitCode(eCode);
		} catch (IOException e) {
			String error=e.getLocalizedMessage();
			log.error("Attempting to run \"" + Arrays.toString(args) + "\" has failed:");
			log.error(error);
			System.err.println(error);
			System.err.println(Arrays.toString(args));
			this.setExitCode(420);
			return error;
		} catch (InterruptedException e) {
			String error=e.getLocalizedMessage();
			log.error("Attempting to run \"" + Arrays.toString(args) + "\" has failed:");
			log.error(error);
			System.err.println(error);
			System.err.println(Arrays.toString(args));
		}catch(Exception e){
			String error=e.getLocalizedMessage();
			log.error("Attempting to run \"" + Arrays.toString(args) + "\" has failed:");
			log.error(error);
			System.err.println(error);
			System.err.println(Arrays.toString(args));
		}
		return out;
	}
}
