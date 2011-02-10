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
package edu.uiuc.cs.guitar.ripper;

import org.kohsuke.args4j.Option;
import edu.umd.cs.guitar.util.Util;

/**
 * Class contains the runtime configurations of SWT GUITAR Ripper
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 */
public class SWTRipperConfiguration extends GRipperConfiguration {

    // GUITAR runtime parameters
    @Option(name = "-g", usage = "destination GUI file path", aliases = "--gui-file")
    static public String GUI_FILE = "GUITAR-Default.GUI";

    @Option(name = "-l", usage = "log file name ", aliases = "--log-file")
    static public String LOG_FILE = Util.getTimeStamp() + ".log";

    @Option(name = "-ow", usage = "log file name ", aliases = "--open-win-file")
    static public String LOG_WIDGET_FILE = "log_widget.xml";

    @Option(name = "-i", usage = "initial waiting time for the application to get stablized before being ripped", aliases = "--initial-wait")
    static public Integer INITIAL_WAITING_TIME = 500;

    // @Option(name = "-iw", usage =
    // "file  containing a list of windows should be ignored during ripping ",
    // aliases = "--ignore-window-file")
    // static public String IGNORE_WIN_FILE;
    //
    // @Option(name = "-ic", usage =
    // "file  containing a list of components should be ignored during ripping ",
    // aliases = "--ignore-component-file")
    // static public String IGNORE_COMPONENT_FILE;

    // Application Under Test
    @Option(name = "-c", usage = "<REQUIRED> main class name for the Application Under Test ", aliases = "--main-class", required = true)
    static public String MAIN_CLASS = null;

    @Option(name = "-a", usage = "arguments for the Application Under Test, separated by a colon (:) ", aliases = "--arguments")
    static public String ARGUMENT_LIST;

    @Option(name = "-u", usage = "URLs for the Application Under Test, separated by a colon (:) ", aliases = "--urls")
    static public String URL_LIST;

    @Option(name = "-j", usage = "Java Virtual Machine options for the Application Under Test", aliases = "--jvm-options")
    static public String JVM_OPTIONS;

    @Option(name = "-cf", usage = "Configure file for the ripper defining terminal, ignored components and ignored windows", aliases = "--configure-file")
    public static String CONFIG_FILE = //"resources" + File.separator + "config"
           // + File.separator + 
            "configuration.xml";
    
    @Option(name = "-ce", usage = "Customized event list (usually aut-specific events)", aliases = "--event-list")
    public static String CUSTOMIZED_EVENT_LIST = null;
}
