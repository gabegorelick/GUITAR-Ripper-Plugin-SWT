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

import edu.umd.cs.guitar.model.SitarApplicationStartException;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * This class handles running the GUI and {@link SitarExecutor}. As
 * SWTGuitar uses a fairly complicated threading model, clients should make use
 * of this class instead of trying to run {@link SitarRipper} or
 * {@code SitarReplayer} directly.
 * 
 * @author Gabe Gorelick
 * 
 */
public class SitarRunner {

	private final SitarExecutor executor;
	private final Thread executorThread;

	/**
	 * The default name of the {@link Thread} that the
	 * <code>SitarExecutor</code> will run on. This can be overridden with
	 * {@link #setThreadName(String)}.
	 */
	public static final String THREAD_NAME = "SitarExecutor";
	
	public SitarRunner(SitarExecutor exec) {
		this.executor = exec;
		executorThread = new Thread(new Runnable() {
			@Override
			public void run() {
				executor.execute();
			}
		});

		// set thread name to ease debugging
		executorThread.setName(THREAD_NAME);
	}

	/**
	 * <p>
	 * Start the GUI and the {@link SitarExecutor}.
	 * </p>
	 * <p>
	 * Because of inherent limitations in SWT, the GUI must be started on the
	 * <code>main</code> thread. But once the GUI starts, it will block until it
	 * is closed by the user. To avoid this, this method starts the
	 * <code>SitarExecutor</code> first on another thread and then starts
	 * the GUI on the <code>main</code> thread. It is the responsibility of the
	 * <code>SitarExecutor</code> to wait until the GUI is ready.
	 * </p>
	 */
	public void run() {
		executorThread.start();
		try {
			executor.getApplication().startGUI();
		} catch (SitarApplicationStartException e) {
			GUITARLog.log.error(e);
		} finally {
			// done in finally block because want to make sure executorThread
			// terminates even if error occurs, otherwise it may stick around
			// and cause bad bugs if this is run in testing context
			try {
				// prevent the main thread closing (and thus the JVM) before
				// the executor is done; this causes executor to
				// eventually time out while connecting to GUI.
				// TODO notify executor that it can quit so it doesn't keep waiting for no reason
				executorThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
	}

	/**
	 * Set the name of the {@link Thread} that the
	 * <code>SitarExecutor</code> will run on. By default, this is {@link SitarRunner#THREAD_NAME}
	 * 
	 * @param name
	 *            the name of the thread
	 */
	public void setThreadName(String name) {
		executorThread.setName(name);
	}
}
