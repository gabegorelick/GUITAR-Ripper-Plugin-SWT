package edu.umd.cs.guitar.ripper;

import edu.umd.cs.guitar.model.SWTApplication;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Contains actions common to {@link SWTRipperMonitor} and
 * {@link SWTReplayerMonitor}.
 * 
 * @author Gabe Gorelick
 */
public class SWTMonitor {

	private final SWTApplication application;
	//Initialize the application to the one passed in
	public SWTMonitor(SWTGuitarConfiguration config, SWTApplication app) {
		this.application = app;
	}
	
	/**After running applications, we need to dispose of the displays correctly
	 * 
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
