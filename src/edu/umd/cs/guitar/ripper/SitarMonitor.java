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

import java.security.Permission;

import edu.umd.cs.guitar.model.SitarApplication;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Contains actions common to {@link SitarRipperMonitor} and
 * {@link SWTReplayerMonitor}.
 * 
 * @author Gabe Gorelick
 */
public class SitarMonitor {

	private final SitarApplication application;
	private final SecurityManager oldManager;
	
	public SitarMonitor(SitarConfiguration config, SitarApplication app) {
		this.application = app;
		oldManager = System.getSecurityManager();
	}
	
	public void cleanUp() {
		application.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				application.getDisplay().dispose();
			}
		});
		GUITARLog.log.info("Display disposed");
		
		// re-enable exiting the JVM
		System.setSecurityManager(oldManager);
	}

	/**
	 * Disable attempts by the application under test to exit the JVM. Clients
	 * that use this method should call {@link #cleanUp()} to re-enable exiting
	 * the JVM, otherwise the JVM may never terminate.
	 * 
	 * @see ExitException
	 */
	public void disableExit() {
		SecurityManager manager = new SecurityManager() {
			@Override
			public void checkPermission(Permission perm) {
				// allow anything
			}
			
			@Override
	        public void checkPermission(Permission perm, Object context) {
				// allow anything
	        }
			
			@Override
			public void checkExit(int status) {
				super.checkExit(status);
				throw new ExitException();
			}
		};
		System.setSecurityManager(manager);
		
		// block uncaught exceptions so we can finish up
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				if (e instanceof ExitException) {
					GUITARLog.log.warn("Application tried to call System.exit");
				} else {
					GUITARLog.log.error("Uncaught exception", e);
				}
				
				// dispose GUI and restore old SecurityManager so we can exit
				cleanUp();
			}
		});
	}

	/**
	 * Thrown to indicate the application under test has attempted to exit the
	 * JVM, e.g. with a call to {@link System#exit(int)}.
	 */
	protected static class ExitException extends SecurityException {
		private static final long serialVersionUID = 1L;
	}
	
}
