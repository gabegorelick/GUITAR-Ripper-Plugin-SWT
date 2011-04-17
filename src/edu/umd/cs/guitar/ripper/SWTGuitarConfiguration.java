package edu.umd.cs.guitar.ripper;

import org.kohsuke.args4j.Option;

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

	@Option(name = "-i", usage = "initial waiting time for the application to get stablized before being ripped", aliases = "--initial-wait")
	private int initialWaitTime = 500;

	// Application Under Test parameters

	@Option(name = "-c", usage = "<REQUIRED> main class name for the Application Under Test ", aliases = "--main-class", required = true)
	private String mainClass = null;
	
	@Option(name = "-cf", usage = "Configure file for the ripper defining terminal, ignored components and ignored windows", aliases = "--configure-file")
    private String configFile = "configuration.xml";

	@Option(name = "-a", usage = "arguments for the Application Under Test, separated by a colon (:) ", aliases = "--arguments")
    private String argumentList;

    @Option(name = "-u", usage = "URLs for the Application Under Test, separated by a colon (:) ", aliases = "--urls")
    private String urlList;
	
	
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
	
	public void setArgumentList(String argumentList) {
		this.argumentList = argumentList;
	}

	public String getArgumentList() {
		return argumentList;
	}

	// TODO use Args4j option handlers to make this array of URLs instead
	public void setUrlList(String urlList) {
		this.urlList = urlList;
	}

	public String getUrlList() {
		return urlList;
	}

}
