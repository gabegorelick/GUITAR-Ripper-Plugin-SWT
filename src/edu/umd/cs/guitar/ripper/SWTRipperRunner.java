package edu.umd.cs.guitar.ripper;

import edu.umd.cs.guitar.model.SWTApplicationStartException;

/**
 * Convenience class to run {@link SWTRipper}.
 * 
 * Handles starting the application under test and the execution of the ripper.
 * 
 * @author Gabe Gorelick
 * 
 */
public class SWTRipperRunner {

	private final SWTRipper ripper;
	private final Thread ripperThread;

	public SWTRipperRunner(final SWTRipper ripper) {
		this.ripper = ripper;
		this.ripperThread = new Thread(new Runnable() {
			@Override
			public void run() {
				ripper.execute();
			}
		});

		// set ripper thread name to make debugging easier
		ripperThread.setName("ripper-thread");
	}

	/**
	 * Start the ripper and the application under test. This method must be
	 * called on the main thread so that the application under test is started
	 * on the main thread. The ripper is executed on its own thread.
	 */
	public void start() { // TODO change method name to rip
		// ripper shouldn't be preempted by GUI once it's started
		ripperThread.setPriority(Thread.MAX_PRIORITY);
		
		ripperThread.start();
		SWTRipperMonitor monitor = ripper.getMonitor();

		try {
			// start GUI on main thread, this blocks until GUI terminates
			monitor.getApplication().startGUI();

			// prevent the main thread closing (and thus the JVM) before
			// the ripper is done
			ripperThread.join();
		} catch (SWTApplicationStartException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}

}
