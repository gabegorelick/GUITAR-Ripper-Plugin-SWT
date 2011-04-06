/*  
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *  the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in all copies or substantial 
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *  LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *  EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.kohsuke.args4j.CmdLineException;

import edu.umd.cs.guitar.model.GIDGenerator;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.SWTConstants;
import edu.umd.cs.guitar.model.SWTDefaultIDGenerator;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentListType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.Configuration;
import edu.umd.cs.guitar.model.data.FullComponentType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.LogWidget;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.util.DefaultFactory;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Executing class for SWTRipper
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 */
public class SWTRipper {

	private final SWTRipperConfiguration config;
	private final SWTRipperMonitor monitor;

	/**
	 * Constructs a new <code>SWTRipper</code>. This constructor is equivalent 
	 * to <code>SWTRipper(config, Thread.currentThread())</code>. Consequently,
	 * this constructor must be called on the same thread that the application
	 * under test is running on (usually the <code>main</code> thread). 
	 * 
	 * @param config
	 * @param appThread thread the application under test runs on
	 */
	public SWTRipper(SWTRipperConfiguration config) {
		this(config, Thread.currentThread());
	}
	
	/**
	 * Constructs a new <code>SWTRipper</code>. The thread passed in is the
	 * thread on which the SWT application under test runs. This is almost 
	 * always the main thread and actually must be the main thread on Cocoa.
	 * 
	 * @param config
	 * @param appThread thread the application under test runs on
	 */
	public SWTRipper(SWTRipperConfiguration config, Thread appThread) {
		super();
		
		if (config == null) {
			config = new SWTRipperConfiguration();
		}
		
		this.config = config;
		monitor = new SWTRipperMonitor(config, appThread);
	}
	
	/**
	 * Execute the SWT ripper.
	 * 
	 * @throws CmdLineException
	 */
	public void execute() throws CmdLineException {
		if (config.help) {
			throw new CmdLineException("");
		}
		
		System.setProperty(GUITARLog.LOGFILE_NAME_SYSTEM_PROPERTY,
				config.getLogFile());
		
		long nStartTime = System.currentTimeMillis();
		Ripper ripper = new Ripper(GUITARLog.log);

		// -------------------------
		// Setup configuration
		// -------------------------

		try {
			setupEnv(ripper);
			ripper.execute();
			
			GUIStructure dGUIStructure = ripper.getResult();
			IO.writeObjToFile(dGUIStructure, config.getGuiFile());

			GUITARLog.log.info("-----------------------------");
			GUITARLog.log.info("OUTPUT SUMARY: ");
			GUITARLog.log.info("Number of Windows: "
					+ dGUIStructure.getGUI().size());
			GUITARLog.log.info("GUI file:" + config.getGuiFile());
			GUITARLog.log.info("Open Component file:"
					+ config.getLogWidgetFile());
			ComponentListType lOpenWins = ripper.getlOpenWindowComps();
			ComponentListType lCloseWins = ripper.getlCloseWindowComp();
			ObjectFactory factory = new ObjectFactory();

			LogWidget logWidget = factory.createLogWidget();
			logWidget.setOpenWindow(lOpenWins);
			logWidget.setCloseWindow(lCloseWins);

			IO.writeObjToFile(logWidget, config.getLogWidgetFile());

			// ------------------
			// Elapsed time:
			long nEndTime = System.currentTimeMillis();
			long nDuration = nEndTime - nStartTime;
			DateFormat df = new SimpleDateFormat("HH : mm : ss: SS");
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			GUITARLog.log.info("Ripping Elapsed: " + df.format(nDuration));
			GUITARLog.log.info("Log file: " + config.getLogFile());
		} catch (Exception e) {
			GUITARLog.log.error("SWTRipper: ", e);
		}
	}

	
	private void setupEnv(Ripper ripper) {
		// --------------------------
		// Terminal list

		// Try to find absolute path first then relative path

		Configuration conf = null;

		try {
			conf = (Configuration) IO.readObjFromFile(
					config.getConfigFile(), Configuration.class);

			if (conf == null) {
				InputStream in = getClass()
						.getClassLoader()
						.getResourceAsStream(config.getConfigFile());
				conf = (Configuration) IO.readObjFromFile(in,
						Configuration.class);
			}

		} catch (Exception e) {
			GUITARLog.log.info("No configuration file. Using an empty one...");
		}

		if (conf == null) {
			DefaultFactory df = new DefaultFactory();
			conf = df.createDefaultConfiguration();
		}

		List<FullComponentType> cTerminalList = conf.getTerminalComponents()
				.getFullComponent();

		for (FullComponentType cTermWidget : cTerminalList) {
			ComponentType component = cTermWidget.getComponent();
			AttributesType attributes = component.getAttributes();
			if (attributes != null)
				SWTConstants.sTerminalWidgetSignature
						.add(new AttributesTypeWrapper(component
								.getAttributes()));
		}

		GRipperMonitor gMonitor = getMonitor();

		List<FullComponentType> lIgnoredComps = new ArrayList<FullComponentType>();
		// List<String> ignoredWindow = new ArrayList<String>();

		ComponentListType ignoredAll = conf.getIgnoredComponents();

		if (ignoredAll != null) {
			for (FullComponentType fullComp : ignoredAll.getFullComponent()) {
				ComponentType comp = fullComp.getComponent();

				// TODO: Shortcut here
				if (comp == null) {
					ComponentType win = fullComp.getWindow();
					ComponentTypeWrapper winAdapter = new ComponentTypeWrapper(
							win);
					String sWindowTitle = winAdapter
							.getFirstValueByName(GUITARConstants.TITLE_TAG_NAME);
					if (sWindowTitle != null) {
						SWTConstants.sIgnoredWins.add(sWindowTitle);
					}

				} else {
					lIgnoredComps.add(fullComp);
				}
			}
		}

		// --------------------------
		// Add ripper.addComponentFilter() calls to ignore components here

		// Set up Monitor
		ripper.setMonitor(gMonitor);
		
		// Set up HashcodeGenerator
		
//		GHashcodeGenerator jHashcodeGenerator = JFCDefaultHashcodeGenerator.getInstance();
//		ripper.setHashcodeGenerator(jHashcodeGenerator);

		
		// Set up IDGenerator
		
		GIDGenerator jIDGenerator = SWTDefaultIDGenerator.getInstance();
		ripper.setIDGenerator(jIDGenerator);
	}
	
	public SWTRipperMonitor getMonitor() {
		return monitor;
	}

}
