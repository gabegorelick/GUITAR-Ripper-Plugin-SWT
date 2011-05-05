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

import edu.umd.cs.guitar.model.SitarApplication;
import edu.umd.cs.guitar.util.Util;

/**
 * Superclass of all SWT GUITAR configuration objects. This class collects
 * configuration options common to SitarRipper and SWTReplayer.
 * 
 * @author Gabe Gorelick
 * 
 */
public abstract class SitarConfiguration {

	@Option(name = "-?", usage = "print this help message", aliases = "--help")
	private boolean help;

	// GUITAR runtime parameters

	@Option(name = "-l", usage = "log file name ", aliases = "--log-file")
	private String logFile = Util.getTimeStamp() + ".log";

	@Option(name = "-i", usage = "initial waiting time for the GUI to get stablized (in milliseconds)", aliases = "--initial-wait")
	private int initialWaitTime = 0;

	@Option(name = "-mt", usage = "maximum time to wait for the GUI to start (in milliseconds)", aliases = "--gui-start-timeout")
	private int guiStartTimeout = SitarApplication.DEFAULT_TIMEOUT;

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

	/**
	 * Set whether to print the help message and quit.
	 * 
	 * @param help
	 *            {@code true} to print help message
	 * @see #getHelp()
	 */
	public void setHelp(boolean help) {
		this.help = help;
	}

	/**
	 * Get whether to print the help message and quit.
	 * 
	 * @return {@code true} to print help message
	 * @see #setHelp(boolean)
	 */
	public boolean getHelp() {
		return help;
	}

	/**
	 * Set the path to the log file.
	 * 
	 * @param logFile
	 *            path to log file
	 * @see #getLogFile()
	 */
	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	/**
	 * Get the path to the log file.
	 * 
	 * @return path to log file
	 * @see #setLogFile(String)
	 */
	public String getLogFile() {
		return logFile;
	}

	/**
	 * Set the maximum amount of time to wait for the GUI to start.
	 * 
	 * @param guiStartTimeout
	 *            time to wait (in milliseconds)
	 * @see #getGuiStartTimeout()
	 */
	public void setGuiStartTimeout(int guiStartTimeout) {
		this.guiStartTimeout = guiStartTimeout;
	}

	/**
	 * Get the maximum amount of time to wait for the GUI to start.
	 * 
	 * @return time to wait (in milliseconds)
	 * @see #setGuiStartTimeout(int)
	 */
	public int getGuiStartTimeout() {
		return guiStartTimeout;
	}

	/**
	 * Set the amount of time to wait before attempting to find the GUI.
	 * 
	 * @param initialWaitTime
	 *            time to wait (in milliseconds)
	 * @see #getInitialWaitTime()
	 */
	public void setInitialWaitTime(int initialWaitTime) {
		this.initialWaitTime = initialWaitTime;
	}

	/**
	 * Get the amount of time to wait befroe attempting to find the GUI.
	 * 
	 * @return time to wait (in milliseconds)
	 * @see #setInitialWaitTime(int)
	 */
	public int getInitialWaitTime() {
		return initialWaitTime;
	}

	/**
	 * Set the main class of the GUI.
	 * 
	 * @param mainClass
	 *            fully qualified name of main class
	 * @see Class#getName()
	 * @see #setMainClass(String)
	 */
	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	/**
	 * Get the name of the main class of the GUI.
	 * 
	 * @return the name of the main class
	 * @see #setMainClass(String)
	 */
	public String getMainClass() {
		return mainClass;
	}

	/**
	 * Set the configuration.xml file to use.
	 * 
	 * @param configFile
	 *            path to config file
	 * @see #getConfigFile()
	 */
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	/**
	 * Get the path to the configuration.xml file.
	 * 
	 * @return path to config file
	 * @see #setConfigFile(String)
	 */
	public String getConfigFile() {
		return configFile;
	}

	/**
	 * Set the arguments to the main method of the GUI.
	 * 
	 * @param arguments
	 *            the arguments to the GUI
	 * @see #getArguments()
	 */
	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}

	/**
	 * Get the arguments to the main method of the GUI.
	 * 
	 * @return arguments to pass to main method
	 * @see #setArguments(String[])
	 */
	public String[] getArguments() {
		return arguments;
	}

	/**
	 * Set the URLs to add to the classpath when loading the GUI.
	 * 
	 * @param urls
	 *            URLs to use
	 * @see #getUrls()
	 */
	public void setUrls(URL[] urls) {
		this.urls = urls;
	}

	/**
	 * Get the URLS to add to the classpath when loading the GUI.
	 * @return a possibly empty array of URLs to use
	 */
	public URL[] getUrls() {
		return urls;
	}

}
