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
 * Class contains the runtime configurations of SWT GUITAR Ripper
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 */
public class SWTRipperConfiguration extends SWTGuitarConfiguration {

	@Option(name = "-g", usage = "destination GUI file path", aliases = "--gui-file")
	private String guiFile = "GUITAR-Default.GUI";
	
	@Option(name = "-ow", usage = "log file name ", aliases = "--open-win-file")
	private String logWidgetFile = "log_widget.xml";
	
    // @Option(name = "-iw", usage =
    // "file  containing a list of windows should be ignored during ripping ",
    // aliases = "--ignore-window-file")
    // static public String IGNORE_WIN_FILE;
    //
    // @Option(name = "-ic", usage =
    // "file  containing a list of components should be ignored during ripping ",
    // aliases = "--ignore-component-file")
    // static public String IGNORE_COMPONENT_FILE;
    
	
	public void setGuiFile(String guiFile) {
		this.guiFile = guiFile;
	}

	public String getGuiFile() {
		return guiFile;
	}
	
	public void setLogWidgetFile(String logWidgetFile) {
		this.logWidgetFile = logWidgetFile;
	}

	public String getLogWidgetFile() {
		return logWidgetFile;
	}
	
}
