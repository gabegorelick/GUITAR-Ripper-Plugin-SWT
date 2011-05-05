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

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.SitarApplication;
import edu.umd.cs.guitar.model.SitarGUIInteraction;
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

	// windows encountered during GUI expansion
	private final LinkedList<Shell> openedWinStack;
	private final LinkedList<Shell> closedWinStack;

	/**
	 * Sole constructor.
	 * 
	 * @param config
	 *            ripper configuration
	 * @param app
	 *            application to rip
	 */
	public SitarRipperMonitor(SitarRipperConfiguration config, SitarApplication app) {
		super();
		
		if (config == null) {
			config = new SitarRipperConfiguration();
		}
		
		this.configuration = config;
		this.application = app;
		this.monitor = new SitarMonitor(configuration, app);
		
		openedWinStack = new LinkedList<Shell>();
		closedWinStack = new LinkedList<Shell>();
		
		// don't store application.getDisplay because it's still null at this point 
	}

	/**
	 * Get a list of the GUI's root windows. Since SWT does not have a notion of
	 * "root" windows, the root windows are whatever windows are open when this method is called.
	 * 
	 * @return list of windows
	 * 
	 * @see org.eclipse.swt.widgets.Display#getShells()
	 */
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

		// Debugs:
		GUITARLog.log.debug("Root window size: " + retWindowList.size());
		for (GWindow window : retWindowList) {
			GUITARLog.log.debug("Window title: " + window.getTitle());
		}

		return retWindowList;
	}

	/**
	 * Wait for the application to start.
	 * 
	 * @see SitarApplication#connect()
	 */
	@Override
	public void setUp() {		
		// sleep until application is ready
		application.connect();
	}
	
	/**
	 * Clean up after ripping has completed.
	 * 
	 * @see SitarMonitor#cleanUp()
	 */
	@Override
	public void cleanUp() {
		monitor.cleanUp();
	}

	/**
	 * Get whether the previous interaction with the GUI opened a new window.
	 * 
	 * @return {@code true} if new window was opened
	 * @see #getOpenedWindowCache()
	 * @see #isWindowClosed()
	 */
	@Override
	public boolean isNewWindowOpened() {
		return !openedWinStack.isEmpty();
	}

	/**
	 * Get whether the previous interaction with the GUI closed a window.
	 * 
	 * @return {@code true} if a window was closed
	 * @see #getClosedWindowCache()
	 * @see #isNewWindowOpened()
	 */
	@Override
	public boolean isWindowClosed() {
		return !closedWinStack.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LinkedList<GWindow> getOpenedWindowCache() {
		LinkedList<GWindow> retWindows = new LinkedList<GWindow>();

		for (Shell shell : openedWinStack) {
			GWindow gWindow = new SitarWindow(shell);
			if (gWindow.isValid())
				retWindows.addLast(gWindow);
		}
		return retWindows;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LinkedList<GWindow> getClosedWindowCache() {
		LinkedList<GWindow> retWindows = new LinkedList<GWindow>();

		for (Shell shell : closedWinStack) {
			GWindow gWindow = new SitarWindow(shell);
			if (gWindow.isValid())
				retWindows.addLast(gWindow);
		}
		return retWindows;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resetWindowCache() {
		openedWinStack.clear();
		closedWinStack.clear();
	}

	/**
	 * Close a given window.
	 * 
	 * @param gWindow
	 *            the window to close
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isIgnoredWindow(GWindow window) {
		String sWindow = window.getTitle();
		// TODO: Ignore template
		return windowsToIgnore.contains(sWindow);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void expandGUI(GComponent component) {
		SitarWidget widget = (SitarWidget) component;
		SitarGUIInteraction expansionData = widget.interact();
		
		openedWinStack.addAll(expansionData.getOpenedShells());
		closedWinStack.addAll(expansionData.getClosedShells());
	}

	/**
	 * Check if a root window is worth ripping
	 * 
	 * @param window
	 *            the window to consider
	 * @return {@code true} if worth ripping, false otherwise
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
	 * Determine whether a given component is expandable.
	 * @deprecated Redundant since {@link #expandGUI(GComponent)} checks for itself. 
	 * @param gComponent component to test
	 * @param window the window the component lives in
	 * @return {@code true} if expandable
	 */
	@Override
	@Deprecated
	final boolean isExpandable(GComponent gComponent, GWindow window) {
		// attempt to expand all widgets
		return true;
	}

	/**
	 * Get the application used by this monitor.
	 * 
	 * @return the application used
	 */
	public SitarApplication getApplication() {
		return application;
	}

}
