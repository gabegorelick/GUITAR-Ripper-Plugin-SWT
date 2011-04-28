/*	
 *  Copyright (c) 2011-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.ripper;

import java.net.URL;

import org.kohsuke.args4j.Option;

import edu.umd.cs.guitar.model.SWTApplication;
import edu.umd.cs.guitar.util.Util;

/**
 * Superclass of all SWT GUITAR configuration objects. This class collects
 * configuration options common to SWTRipper and SWTReplayer.
 * 
 * @author Gabe Gorelick
 * 
 */
public abstract class SWTGuitarConfiguration {

	@Option(name = "-?", usage = "print this help message", aliases = "--help")
	private boolean help;

	// GUITAR runtime parameters

	@Option(name = "-l", usage = "log file name ", aliases = "--log-file")
	private String logFile = Util.getTimeStamp() + ".log";

	@Option(name = "-i", usage = "initial waiting time for the GUI to get stablized (in milliseconds)", aliases = "--initial-wait")
	private int initialWaitTime = 0;

	@Option(name = "-mt", usage = "maximum time to wait for the GUI to start (in milliseconds)", aliases = "--gui-start-timeout")
	private int guiStartTimeout = SWTApplication.DEFAULT_TIMEOUT;

	// Application Under Test parameters

	@Option(name = "-c", usage = "<REQUIRED> main class name for the Application Under Test ", aliases = "--main-class", required = true)
	private String mainClass = null;
	
	@Option(name = "-cf", usage = "Configure file for the ripper defining terminal, ignored components and ignored windows", aliases = "--configure-file")
    private String configFile = "configuration.xml";

	@Option(name = "-a", usage = "arguments for the Application Under Test", aliases = "--arguments")
    private String[] arguments = new String[0];

    @Option(name = "-u", usage = "URLs for the Application Under Test", aliases = "--urls")
    private URL[] urls = new URL[0];
	
	
    // getters and setters
	
	public void setHelp(boolean help) {
		this.help = help;
	}

	public boolean getHelp() {
		return help;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String getLogFile() {
		return logFile;
	}
	
	public void setGuiStartTimeout(int guiStartTimeout) {
		this.guiStartTimeout = guiStartTimeout;
	}
	
	public int getGuiStartTimeout() {
		return guiStartTimeout;
	}

	public void setInitialWaitTime(int initialWaitTime) {
		this.initialWaitTime = initialWaitTime;
	}

	public int getInitialWaitTime() {
		return initialWaitTime;
	}
	
	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public String getMainClass() {
		return mainClass;
	}
	
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public String getConfigFile() {
		return configFile;
	}
	
	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}

	public String[] getArguments() {
		return arguments;
	}

	public void setUrls(URL[] urls) {
		this.urls = urls;
	}

	public URL[] getUrls() {
		return urls;
	}

}
