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
package edu.umd.cs.guitar.ripper;

import edu.umd.cs.guitar.model.SitarApplication;
import edu.umd.cs.guitar.model.SitarApplication.ExitException;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Contains actions common to {@link SitarRipperMonitor} and
 * {@code SWTReplayerMonitor}.
 * 
 * @author Gabe Gorelick
 */
public class SitarMonitor {

	private final SitarApplication application;

	/**
	 * Sole constructor.
	 * 
	 * @param config
	 *            configuration
	 * @param app
	 *            the application to test
	 */
	public SitarMonitor(SitarConfiguration config, SitarApplication app) {
		this.application = app;
		
		// block uncaught exceptions so we can finish up
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				if (e instanceof ExitException) {
					GUITARLog.log.warn("Application tried to call System.exit");
					// don't quit if GUI tried to close
				} else {
					GUITARLog.log.error("Uncaught exception", e);
					// dispose GUI if GUI threw exception
					cleanUp();
				}
			}
		});
	}

	/**
	 * Dispose of the GUI after execution is complete.
	 * 
	 * @see SitarRipperMonitor#cleanUp()
	 */
	public void cleanUp() {		
		application.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				application.getDisplay().dispose();
			}
		});
		GUITARLog.log.info("Display disposed");
	}
	
}
