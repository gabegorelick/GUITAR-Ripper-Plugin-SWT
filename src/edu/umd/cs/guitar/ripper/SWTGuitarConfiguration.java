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

	@Option(name = "-at", usage = "maximum time to wait for the GUI to start (in milliseconds)", aliases = "--gui-start-timeout")
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
