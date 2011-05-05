/*	
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
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
/* Copyright (c) 2010
 * Matt Kirn (mattkse@gmail.com) and Alex Loeb (atloeb@gmail.com)
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.ripper;

import org.kohsuke.args4j.Option;

/**
 * Configuration specific to the ripper. The configuration options
 * held by this class can be set through its setter methods or by passing
 * and instance of this class to an Args4j {@code CmdLineParser}.
 * 
 * @author Gabe Gorelick
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 */
public class SitarRipperConfiguration extends SitarConfiguration {

	@Option(name = "-g", usage = "destination GUI file path", aliases = "--gui-file")
	private String guiFile = "GUITAR-Default.GUI";
	
	@Option(name = "-ow", usage = "log file name ", aliases = "--open-win-file")
	private String logWidgetFile = "log_widget.xml";

	/**
	 * Set the resulting GUI structure file path.
	 * 
	 * @param guiFile
	 *            path to output GUI structure
	 * @see #getGuiFile()
	 */
	public void setGuiFile(String guiFile) {
		this.guiFile = guiFile;
	}

	/**
	 * Get the resulting GUI structure file path.
	 * 
	 * @return path to output GUI structure
	 * @see #setGuiFile(String)
	 */
	public String getGuiFile() {
		return guiFile;
	}

	/**
	 * Set the log file for widgets.
	 * 
	 * @param logWidgetFile
	 *            path to log file
	 * @see #getLogWidgetFile()
	 */
	public void setLogWidgetFile(String logWidgetFile) {
		this.logWidgetFile = logWidgetFile;
	}

	/**
	 * Get the log file for widgets.
	 * 
	 * @return path to log file
	 */
	public String getLogWidgetFile() {
		return logWidgetFile;
	}
	
}
