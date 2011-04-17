package edu.umd.cs.guitar.ripper;

import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.SWTApplication;
import edu.umd.cs.guitar.model.SWTConstants;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Contains actions common to {@link SWTRipperMonitor} and
 * {@link SWTReplayerMonitor}.
 * 
 * @author Gabe Gorelick
 */
public class SWTMonitor {

	private final SWTApplication application;
	
	public SWTMonitor(SWTGuitarConfiguration config, SWTApplication app) {
		this.application = app;
	}
	
	public void cleanUp() {
		application.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				application.getDisplay().dispose();
			}
		});
		GUITARLog.log.info("Display disposed");
	}
	
	/**
	 * Register the default supported events. 
	 * 
	 * @see EventManager
	 * @see SWTConstants#DEFAULT_SUPPORTED_EVENTS
	 */
	public void registerEvents() {
		EventManager em = EventManager.getInstance();

		for (Class<? extends GEvent> event : SWTConstants.DEFAULT_SUPPORTED_EVENTS) {
			em.registerEvent(event);
		}
	}
}
