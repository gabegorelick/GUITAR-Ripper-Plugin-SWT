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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.event.SitarDefaultAction;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.SitarApplication;
import edu.umd.cs.guitar.model.SitarWindow;
import edu.umd.cs.guitar.model.swtwidgets.SitarWidget;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Monitor for {@link SitarRipper} to handle SWT specific features. Adapted from
 * <code>JFCRipperMonitor</code>.
 * 
 * @author Gabe Gorelick
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 */
public class SitarRipperMonitor extends GRipperMonitor {

	private final SitarApplication application;
	private final SitarRipperConfiguration configuration;
	
	// monitor to delegate actions shared with replayer to
	private final SitarMonitor monitor;

	private List<String> windowsToIgnore = new ArrayList<String>();

	/**
	 * Temporary list of windows opened during the expand event is being
	 * performed. Those windows are in a native form to prevent data loss.
	 * 
	 */
	private volatile LinkedList<Shell> tempOpenedWinStack = new LinkedList<Shell>();
	// TODO I don't think these have to be volatile
	private volatile LinkedList<Shell> tempClosedWinStack = new LinkedList<Shell>();

	/**
	 * Constructor
	 * 
	 * @param config
	 *            ripper configuration
	 * @param app
	 */
	public SitarRipperMonitor(SitarRipperConfiguration config, SitarApplication app) {
		super();
		
		if (config == null) {
			config = new SitarRipperConfiguration();
		}
		
		this.configuration = config;
		this.application = app;
		this.monitor = new SitarMonitor(configuration, app);
						
		// don't store application.getDisplay because it's still null at this point 
	}

	@Override
	public List<GWindow> getRootWindows() {
		final List<GWindow> retWindowList = new ArrayList<GWindow>();
				
		application.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				synchronized (retWindowList) {
					for (Shell shell : application.getDisplay().getShells()) {
						if (isValidRootWindow(shell)) {
							GWindow gWindow = new SitarWindow(shell);
							retWindowList.add(gWindow);
						}
					}
				}
			}
		});

		// / Debugs:
		GUITARLog.log.debug("Root window size: " + retWindowList.size());
		for (GWindow window : retWindowList) {
			GUITARLog.log.debug("Window title: " + window.getTitle());
		}

		try {
			// TODO this doesn't seem to do anything
			Thread.sleep(50);
		} catch (InterruptedException e) {
			GUITARLog.log.error(e);
		}
		return retWindowList;
	}

	@Override
	public void setUp() {
		monitor.disableExit();
		
		// sleep until application is ready
		application.connect();
	}
	
	@Override
	public void cleanUp() {
		monitor.cleanUp();
	}

	@Override
	public boolean isNewWindowOpened() {
		return (tempOpenedWinStack.size() > 0);
	}

	@Override
	public boolean isWindowClosed() {
		return (tempClosedWinStack.size() > 0);
	}

	@Override
	public LinkedList<GWindow> getOpenedWindowCache() {
		LinkedList<GWindow> retWindows = new LinkedList<GWindow>();

		for (Shell shell : tempOpenedWinStack) {
			GWindow gWindow = new SitarWindow(shell);
			if (gWindow.isValid())
				retWindows.addLast(gWindow);
		}
		return retWindows;
	}

	@Override
	public void resetWindowCache() {
		this.tempOpenedWinStack.clear();
		this.tempClosedWinStack.clear();
	}

	@Override
	public void closeWindow(GWindow gWindow) {
		SitarWindow sWindow = (SitarWindow) gWindow;
		final Shell shell = sWindow.getShell();

		shell.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				// call close instead of dispose so close event is sent
				shell.close();
			}
		});
	}

	@Override
	public boolean isIgnoredWindow(GWindow window) {
		String sWindow = window.getTitle();
		// TODO: Ignore template
		return windowsToIgnore.contains(sWindow);
	}

	
	@Override
	public void expandGUI(GComponent component) {
		new SitarDefaultAction().perform(component);
		
		// no need to wait for action, perform blocks until complete
	}

	/**
	 * Check if a root window is worth ripping
	 * 
	 * @param window
	 *            the window to consider
	 * @return true if worth ripping, false otherwise
	 */
	private boolean isValidRootWindow(final Shell window) {
		final AtomicBoolean visible = new AtomicBoolean();
		
		window.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				visible.set(window.isVisible());
			}
		});
		
		return visible.get();
	}

	/**
	 * Check if a component is click-able.
	 * 
	 * @param component
	 * @return true/false
	 */
	private boolean isClickable(final Widget widget) {
		final AtomicBoolean disposed = new AtomicBoolean();
		
		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				disposed.set(widget.isDisposed());
			}
		});
		
		return !disposed.get();
	}

	@Override
	protected boolean isExpandable(GComponent gComponent, GWindow window) {

		Widget widget = ((SitarWidget) gComponent).getWidget();

		String title = gComponent.getTitle();
		if (title == null) {
			return false;
		}
		
		if (title.isEmpty()) {
			return false;
		}
		
		if (!gComponent.isEnable()) {
			GUITARLog.log.debug("Component is disabled");
			return false;
		}

		if (!isClickable(widget)) {
			return false;
		}

		if (gComponent.getTypeVal().equals(GUITARConstants.TERMINAL)) {
			return false;
		}
		
		return true;
	}

	@Override
	protected LinkedList<GWindow> getClosedWindowCache() {
		LinkedList<GWindow> retWindows = new LinkedList<GWindow>();

		for (Shell shell : tempClosedWinStack) {
			GWindow gWindow = new SitarWindow(shell);
			if (gWindow.isValid())
				retWindows.addLast(gWindow);
		}
		return retWindows;
	}
	
	public SitarApplication getApplication() {
		return application;
	}

}