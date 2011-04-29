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

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.guitar.model.GIDGenerator;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.SWTConstants;
import edu.umd.cs.guitar.model.SWTDefaultIDGenerator;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentListType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.FullComponentType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.LogWidget;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.ripper.filter.GComponentFilter;
import edu.umd.cs.guitar.ripper.filter.SWTIgnoreWidgetFilter;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Adapts a {@link Ripper} for use with SWT GUIs.
 * 
 * @author Gabe Gorelick
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 * 
 */
public class SWTRipper extends SWTGuitarExecutor {

	private final SWTRipperConfiguration config;
	private final SWTRipperMonitor monitor;
	private final Ripper ripper;

	/**
	 * Constructs a new <code>SWTRipper</code>. This constructor is equivalent
	 * to
	 * 
	 * <pre>
	 * SWTRipper(config, Thread.currentThread())
	 * </pre>
	 * 
	 * Consequently, this constructor must be called on the same thread that the
	 * application under test is running on (usually the <code>main</code>
	 * thread).
	 * 
	 * @param config
	 *            configuration
	 * 
	 * @see SWTGuitarRunner
	 */
	public SWTRipper(SWTRipperConfiguration config) {
		this(config, Thread.currentThread());
	}

	/**
	 * Constructs a new <code>SWTRipper</code>. The thread passed in is the
	 * thread on which the SWT application under test runs. This is almost
	 * always the <code>main</code> thread (and actually must be the
	 * <code>main</code> thread on Cocoa).
	 * 
	 * @param config
	 *            configuration
	 * @param guiThread
	 *            thread the GUI runs on
	 * 
	 * @see SWTGuitarRunner
	 */
	public SWTRipper(SWTRipperConfiguration config, Thread guiThread) {
		super(config, guiThread);
		
		if (config == null) {
			this.config = new SWTRipperConfiguration();
		} else {
			this.config = config;
		}
		
		monitor = new SWTRipperMonitor(this.config, getApplication());
		ripper = initRipper();
		initIgnoredComponents(ripper);
		initTerminalComponents();
	}
	
	/**
	 * initialize the ripper
	 * 
	 * @return the ripper to use 
	 */
	private Ripper initRipper() {
		Ripper ripper = new Ripper(GUITARLog.log);
				
		ripper.setMonitor(monitor);
		
		GIDGenerator idGenerator = SWTDefaultIDGenerator.getInstance();
		ripper.setIDGenerator(idGenerator);
		
		return ripper;
	}
	/**initialize the terminal components
	 */
	private void initTerminalComponents() {
		List<FullComponentType> cTerminalList = getXmlConfig().getTerminalComponents().getFullComponent();

		for (FullComponentType cTermWidget : cTerminalList) {
			ComponentType component = cTermWidget.getComponent();
			AttributesType attributes = component.getAttributes();
			if (attributes != null) {
				// TODO don't use global variable
				SWTConstants.sTerminalWidgetSignature.add(new AttributesTypeWrapper(component.getAttributes()));
			}
		}
	}
	/**initialize components the ripper will ignore
	 * 
	 * 
	 * @param ripper
	 * 		ripper that will need to find components to ignore
	 */
	private void initIgnoredComponents(Ripper ripper) {
		List<FullComponentType> lIgnoredComps = new ArrayList<FullComponentType>();
		ComponentListType ignoredAll = getXmlConfig().getIgnoredComponents();

		if (ignoredAll != null) {
			for (FullComponentType fullComp : ignoredAll.getFullComponent()) {
				ComponentType comp = fullComp.getComponent();

				if (comp == null) {
					ComponentType win = fullComp.getWindow();
					ComponentTypeWrapper winAdapter = new ComponentTypeWrapper(win);
					String ID = winAdapter.getFirstValueByName(GUITARConstants.ID_TAG_NAME);
					if (ID != null) {
						SWTConstants.sIgnoredWins.add(ID);
					}

				} else {
					lIgnoredComps.add(fullComp);
				}
			}
		}
		
		GComponentFilter filter = new SWTIgnoreWidgetFilter(lIgnoredComps);
		ripper.addComponentFilter(filter);
	}

	
	/**
	 * Execute the ripper.
	 * 
	 * @see Ripper#execute()
	 */
	@Override
	protected void onExecute() {
		try {
			ripper.execute();			
		} catch (Exception e) {
			GUITARLog.log.error("SWTRipper: ", e);
		}
	}
	
	/**
	 * Log the results of ripping.
	 */
	@Override
	protected void onAfterExecute() {
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

		// print time elapsed
		super.onAfterExecute();
	}

	/**
	 * Get the {@code SWTRipperMonitor} used by this {@code SWTRipper}.
	 * 
	 * @return the monitor used by this ripper
	 */
	@Override
	public SWTRipperMonitor getMonitor() {
		return monitor;
	}

}
