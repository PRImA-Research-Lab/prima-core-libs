/*
 * Copyright 2019 PRImA Research Lab, University of Salford, United Kingdom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primaresearch.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Helper class for running an external command line tool.
 * 
 * @author Christian Clausner
 * @version 2 (changed from command line string to string array)
 *
 */
public class ExternalProcess {
	
	private String[] commandLine;
	private String executionDirectory;
	private String output = null;
	private Integer exitCode = null;

	/**
	 * Constructor
	 * 
	 * @param commandLine - Command line call including executable and arguments 
	 * @param executionDirectory - Working directory for the external process (use null for current working directory)
	 */
	public ExternalProcess(String[] commandLine, String executionDirectory) {
		this.commandLine = commandLine;
		this.executionDirectory = executionDirectory;
	}
	
	/**
	 * Starts the external process.
	 * @return true if the process returned with exit code 0, false otherwise
	 */
	public boolean run() throws IOException, InterruptedException {
		return run(0);
	}

	/**
	 * Starts the external process.
	 * @param timeout Timeout in seconds (0 for infinite time)
	 * @return true if the process returned with exit code 0, false otherwise
	 */
	public boolean run(int timeout) throws IOException, InterruptedException {
		
		//if (debugMode)
		//	System.out.println("Executing in: "+execIn.getAbsolutePath());
		
        final StringBuilder output = new StringBuilder();

        ArrayList<String> lst = new ArrayList<String>();
        for (int i=0; i<commandLine.length; i++)
        	lst.add(commandLine[i]);
		//Log.debug("Starting external process: "+lst);
		//if (executionDirectory != null)
		//	Log.debug(" Executing in directory: "+executionDirectory);
		

		ProcessCallable callable = new ProcessCallable(executionDirectory, commandLine, output);
		
	    FutureTask<Integer> task = new FutureTask<Integer>(callable);
	    ExecutorService service = Executors.newSingleThreadExecutor();
	    service.execute(task);
	    Integer ret = -1;
	    
	    try {
			ret = timeout > 0 ? task.get(timeout, TimeUnit.SECONDS) : task.get();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			//Log.warning("External process timed out after "+timeout+" seconds.");
			callable.killProcess();
		}

		
		/*File execIn = executionDirectory != null ? new File(executionDirectory) : null;
		
		Process process = Runtime.getRuntime().exec(commandLine, 
													null, 
													execIn);
		
        InputStream stdin = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdin);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        StringBuilder output = new StringBuilder();
		while ( (line = br.readLine()) != null)
			output.append(line);
		*/
		
        //InputStream stderr = process.getErrorStream();
        //InputStreamReader isr = new InputStreamReader(stderr);
        //BufferedReader br = new BufferedReader(isr);
        //String line = null;
		//if (debugMode) {
			//System.out.println("<ERR_OUTPUT>");
			//while ( (line = br.readLine()) != null)
			//	System.out.println(line);
			//System.out.println("</ERR_OUTPUT>");
		//}

		//process.waitFor();
		
		this.output = output.toString();
		this.exitCode = ret;
		
		return exitCode == 0;
	}

	/**
	 * Returns the output from the external process to stdout.
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * Returns the exit code the external process returned.
	 */
	public Integer getExitCode() {
		return exitCode;
	}
	
	private static class ProcessCallable implements Callable<Integer> {
		private Process process = null;
		private String executionDirectory;
		private String[] commandLine;
		private StringBuilder output;
		
		public ProcessCallable(String executionDirectory, String[] commandLine, StringBuilder output) {
			this.executionDirectory = executionDirectory;
			this.commandLine = commandLine;
			this.output = output;
		}
		
        public Integer call() throws Exception {
    		File execIn = executionDirectory != null ? new File(executionDirectory) : null;
    		
    		process = Runtime.getRuntime().exec(commandLine, 
    													null, 
    													execIn);
    		
            InputStream stdin = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            
    		while ( (line = br.readLine()) != null) {
    			if (output.length() > 0)
    				output.append("\n");
    			output.append(line);
    		}
    		
    		return process.waitFor();
        }
        
        public void killProcess() {
        	if (process != null)
        		process.destroy();
        }
	};
}
