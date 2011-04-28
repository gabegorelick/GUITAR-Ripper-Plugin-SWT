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

import edu.umd.cs.guitar.model.SWTApplicationStartException;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * This class handles running the GUI and {@link SWTGuitarExecutor}. As
 * SWTGuitar uses a fairly complicated threading model, clients should make use
 * of this class instead of trying to run {@link SWTRipper} or
 * {@link SWTReplayer} directly.
 * 
 * @author Gabe Gorelick
 * 
 */
public class SWTGuitarRunner {

	private final SWTGuitarExecutor executor;
	private final Thread executorThread;

	/**
	 * The default name of the {@link Thread} that the
	 * <code>SWTGuitarExecutor</code> will run on. This can be overridden with
	 * {@link #setThreadName(String)}.
	 */
	public static final String THREAD_NAME = "SWTGuitarExecutor";
	
	public SWTGuitarRunner(SWTGuitarExecutor exec) {
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
	 * Start the GUI and the {@link SWTGuitarExecutor}.
	 * </p>
	 * <p>
	 * Because of inherent limitations in SWT, the GUI must be started on the
	 * <code>main</code> thread. But once the GUI starts, it will block until it
	 * is closed by the user. To avoid this, this method starts the
	 * <code>SWTGuitarExecutor</code> first on another thread and then starts
	 * the GUI on the <code>main</code> thread. It is the responsibility of the
	 * <code>SWTGuitarExecutor</code> to wait until the GUI is ready.
	 * </p>
	 */
	public void run() {
		executorThread.start();
		try {
			executor.getApplication().startGUI();

			// prevent the main thread closing (and thus the JVM) before
			// the executor is done
			executorThread.join();
		} catch (SWTApplicationStartException e) {
			GUITARLog.log.error(e);
		} catch (InterruptedException e) {
			GUITARLog.log.error(e);
		}
	}

	/**
	 * Set the name of the {@link Thread} that the
	 * <code>SWTGuitarExecutor</code> will run on. By default, this is {@link SWTGuitarRunner#THREAD_NAME}
	 * 
	 * @param name
	 *            the name of the thread
	 */
	public void setThreadName(String name) {
		executorThread.setName(name);
	}
}
