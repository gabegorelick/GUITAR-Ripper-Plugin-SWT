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

import java.io.InputStream;
import java.net.URL;
import java.security.Permission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.SitarApplication;
import edu.umd.cs.guitar.model.SitarApplication.ExitException;
import edu.umd.cs.guitar.model.data.Configuration;
import edu.umd.cs.guitar.util.DefaultFactory;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * <p>
 * This is the parent class of <code>SitarRipper</code> and
 * <code>SWTReplayer</code>. Responsible for abstracting away the differences
 * between the two and implementing common functionality.
 * </p>
 * <p>
 * Subclasses are responsible for implementing the {@link #onExecute()
 * onExecute} method, which does most of the work of running the ripper or
 * replayer.
 * </p>
 * 
 * @author Gabe Gorelick
 * 
 */
public abstract class SitarExecutor {

	private final SitarConfiguration config;
	private final SitarApplication application;
	
	private final Configuration xmlConfig; 
	
	private long startTime;
	
	private final SecurityManager oldManager;

	/**
	 * Constructs a new <code>SitarExecutor</code>. This constructor is
	 * equivalent to
	 * 
	 * <pre>
	 * SitarExecutor(config, Thread.currentThread())
	 * </pre>
	 * 
	 * Consequently, this constructor must be called on the same thread that the
	 * application under test is running on (usually the <code>main</code>
	 * thread).
	 * 
	 * @param config
	 *            configuration
	 * 
	 * @see SitarRunner
	 */
	protected SitarExecutor(SitarConfiguration config) {
		this(config, Thread.currentThread());
	}

	/**
	 * Constructs a new <code>SitarExecutor</code>. The thread passed in is
	 * the thread on which the SWT application under test runs. This is almost
	 * always the <code>main</code> thread (and actually must be the
	 * <code>main</code> thread on Cocoa).
	 * 
	 * @param config
	 *            configuration
	 * @param guiThread
	 *            thread the GUI runs on
	 * 
	 * @see SitarRunner
	 */
	protected SitarExecutor(SitarConfiguration config, Thread guiThread) {
		if (config == null) {
			config = new SitarRipperConfiguration();
		}
		
		this.config = config;
		this.application = initSWTApplication(config, guiThread);
		this.xmlConfig = loadXmlConfig();
		oldManager = System.getSecurityManager();
		
		System.setProperty(GUITARLog.LOGFILE_NAME_SYSTEM_PROPERTY, config.getLogFile());
	}
	
	// initialize the SitarApplication
	private SitarApplication initSWTApplication(SitarConfiguration config, Thread guiThread) {
		SitarApplication app = new SitarApplication(config.getMainClass(), guiThread);
		
		app.setTimeout(config.getGuiStartTimeout());		
		app.setInitialWait(config.getInitialWaitTime());		
		app.setArgsToApp(config.getArguments());
		for (URL u : config.getUrls()) {
			app.addURL(u);
		}
		
		return app;
	}
	
	// load the config file (the XML one that lists ignored components)
	private Configuration loadXmlConfig() {
		Configuration conf = null;

		try {
			conf = (Configuration) IO.readObjFromFile(config.getConfigFile(),
					Configuration.class);

			if (conf == null) {
				InputStream in = getClass().getClassLoader()
						.getResourceAsStream(config.getConfigFile());
				if (in != null) {
					conf = (Configuration) IO.readObjFromFile(in,
							Configuration.class);
				}
			}

		} catch (Exception e) {
			GUITARLog.log.warn(e);
			// if there's any problem loading the config, we use an empty one
		}
		
		// failed to load config file
		if (conf == null) {
			GUITARLog.log.info("No configuration file. Using an empty one...");
			DefaultFactory df = new DefaultFactory();
			conf = df.createDefaultConfiguration();
		}
		
		return conf;
	}

	/**
	 * Return the monitor used by this {@code SitarExecutor}. As their is no
	 * common superclass for all monitors, this method returns an {@code Object}
	 * . Subclasses are encouraged to modify the signature of their
	 * implementations of this method to return a more appropriate type, e.g.
	 * {@code SitarRipperMonitor} or {@code SWTReplayerMonitor}.
	 * 
	 * @return the monitor used by this executor
	 */
	public abstract Object getMonitor();

	/**
	 * Get the {@code SitarApplication} used by this {@code SitarExecutor}.
	 * 
	 * @return the {@code SitarApplication} that models the GUI
	 */
	public SitarApplication getApplication() {
		return application;
	}
	
	/**
	 * Get the time that the {@code SitarExecutor} starting executing. This
	 * is set by {@link #beginTiming()}. Useful if subclasses want to log how
	 * long execution took. 
	 * 
	 * @return start time in milliseconds
	 * 
	 * @see System#currentTimeMillis()
	 */
	protected long getStartTime() {
		return startTime;
	}

	/**
	 * Get the {@code Configuration} used by this {@code SitarExecutor}. The
	 * {@code Configuration} stores user-specified ignored and terminal
	 * components.
	 * 
	 * @return the {@code Configuration} used by this {@code SitarExecutor}
	 */
	protected Configuration getXmlConfig() {
		return xmlConfig;
	}

	/**
	 * Called directly before the {@link #execute() execute} method. Subclasses
	 * are encouraged to call their parent's implementation of this method so
	 * that it can perform necessary setup.
	 */
	protected void onBeforeExecute() {		
		beginTiming();
		disableExit();
	}

	/**
	 * Run this {@code SitarExecutor}. This method simply calls
	 * {@link #onBeforeExecute()}, {@link #onExecute()}, and then
	 * {@link #onAfterExecute()}.
	 */
	public final void execute() {
		onBeforeExecute();
		onExecute();
		onAfterExecute();
	}

	/**
	 * Execute the {@code SitarExecutor}. Called after
	 * {@link #onBeforeExecute()} and before {@link #onAfterExecute()}.
	 * Subclasses should execute their respective tools in this method.
	 */
	protected abstract void onExecute();
	
	/**
	 * Called after {@link #onExecute()} has completed. This implementation simply
	 * ends timing the execution.
	 * 
	 * @see #getStartTime()
	 */
	protected void onAfterExecute() {
		endTiming();
		enableExit();
	}
	
	/**
	 * Begin timing execution. 
	 */
	private void beginTiming() {
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * Stop timing execution.
	 */
	private void endTiming() {
		long nEndTime = System.currentTimeMillis();
		long nDuration = nEndTime - getStartTime();
		DateFormat df = new SimpleDateFormat("HH : mm : ss: SS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		GUITARLog.log.info("Time Elapsed: " + df.format(nDuration));
	}
	
	/**
	 * Disable attempts by the application under test to exit the JVM. Clients
	 * that use this method should call {@link #enableExit()} to re-enable exiting
	 * the JVM, otherwise the JVM may never terminate.
	 * 
	 * @see ExitException
	 * @see #enableExit()
	 */
	protected void disableExit() {
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
	}
	
	/**
	 * Re-enable closing the JVM. 
	 * 
	 * @see #disableExit()
	 */
	protected void enableExit() {
		System.setSecurityManager(oldManager);
	}
		
}
