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
package edu.uiuc.cs.guitar.ripper;

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
import edu.uiuc.cs.guitar.model.SWTConstants;
import edu.uiuc.cs.guitar.model.SWTDefaultIDGenerator;
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
import edu.umd.cs.guitar.ripper.GRipperMonitor;
import edu.umd.cs.guitar.ripper.Ripper;
import edu.umd.cs.guitar.util.DefaultFactory;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Executing class for SWTRipper
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 */
public class SWTRipper {

	SWTRipperConfiguration CONFIG;

	/**
	 * @param CONFIG
	 */
	public SWTRipper(SWTRipperConfiguration CONFIG) {
		super();
		this.CONFIG = CONFIG;
	}

	// Logger logger;

	/**
	 * Execute the jfc ripper
	 * 
	 * @throws CmdLineException
	 */
	Ripper ripper;

	public void execute() throws CmdLineException {
		if (CONFIG.help) {
			throw new CmdLineException("");
		}
		System.setProperty(GUITARLog.LOGFILE_NAME_SYSTEM_PROPERTY,
				SWTRipperConfiguration.LOG_FILE);
		// PropertyConfigurator.configure(SWTConstants.LOG4J_PROPERTIES_FILE);
		// URL logFile = this.getClass().getClassLoader().getResource(
		// SWTConstants.LOG4J_PROPERTIES_FILE);
		// PropertyConfigurator.configure(logFile);

		// org.apache.log4j.helpers.Loader.getResource(resource, Logger.class)

		// GUITARLog.log =
		// Logger.getLogger(JFCRipperMain.class.getSimpleName());
		long nStartTime = System.currentTimeMillis();
		ripper = new Ripper(GUITARLog.log);

		// -------------------------
		// Setup configuration
		// -------------------------

		try {
			setupEnv();
			ripper.execute();
		} catch (Exception e) {
			GUITARLog.log.error("JFCRipper: ", e);
			System.exit(1);
		}

		GUIStructure dGUIStructure = ripper.getResult();
		IO.writeObjToFile(dGUIStructure, SWTRipperConfiguration.GUI_FILE);

		GUITARLog.log.info("-----------------------------");
		GUITARLog.log.info("OUTPUT SUMARY: ");
		GUITARLog.log.info("Number of Windows: "
				+ dGUIStructure.getGUI().size());
		GUITARLog.log.info("GUI file:" + SWTRipperConfiguration.GUI_FILE);
		GUITARLog.log.info("Open Component file:"
				+ SWTRipperConfiguration.LOG_WIDGET_FILE);
		ComponentListType lOpenWins = ripper.getlOpenWindowComps();
		ComponentListType lCloseWins = ripper.getlCloseWindowComp();
		ObjectFactory factory = new ObjectFactory();

		LogWidget logWidget = factory.createLogWidget();
		logWidget.setOpenWindow(lOpenWins);
		logWidget.setCloseWindow(lCloseWins);

		IO.writeObjToFile(logWidget, SWTRipperConfiguration.LOG_WIDGET_FILE);

		// ------------------
		// Elapsed time:
		long nEndTime = System.currentTimeMillis();
		long nDuration = nEndTime - nStartTime;
		DateFormat df = new SimpleDateFormat("HH : mm : ss: SS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		GUITARLog.log.info("Ripping Elapsed: " + df.format(nDuration));
		GUITARLog.log.info("Log file: " + SWTRipperConfiguration.LOG_FILE);
	}

	/**
     * 
     */
	/**
	 * 
	 */
	private void setupEnv() {
		// --------------------------
		// Terminal list

		// Try to find absolute path first then relative path

		Configuration conf = null;

		try {
			conf = (Configuration) IO.readObjFromFile(
					SWTRipperConfiguration.CONFIG_FILE, Configuration.class);

			if (conf == null) {
				InputStream in = getClass()
						.getClassLoader()
						.getResourceAsStream(SWTRipperConfiguration.CONFIG_FILE);
				conf = (Configuration) IO.readObjFromFile(in,
						Configuration.class);
			}

		} catch (Exception e) {
			GUITARLog.log.error("No configuration file. Using an empty one...");
			// return;
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

		if (ignoredAll != null)
			for (FullComponentType fullComp : ignoredAll.getFullComponent()) {
				ComponentType comp = fullComp.getComponent();

				// TODO: Shortcut here
				if (comp == null) {
					ComponentType win = fullComp.getWindow();
					ComponentTypeWrapper winAdapter = new ComponentTypeWrapper(
							win);
					String sWindowTitle = winAdapter
							.getFirstValueByName(GUITARConstants.TITLE_TAG_NAME);
					if (sWindowTitle != null)
						SWTConstants.sIgnoredWins.add(sWindowTitle);

				} else
					lIgnoredComps.add(fullComp);
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
	
	public GRipperMonitor getMonitor() {
		return new SWTRipperMonitor(CONFIG);
	}

}
