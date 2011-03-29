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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.event.SWTActionEDT;
import edu.umd.cs.guitar.event.SWTEventHandler;
import edu.umd.cs.guitar.exception.ApplicationConnectException;
import edu.umd.cs.guitar.internal.SWTGlobals;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.SWTApplication;
import edu.umd.cs.guitar.model.SWTConstants;
import edu.umd.cs.guitar.model.SWTWidget;
import edu.umd.cs.guitar.model.SWTWindow;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * 
 * Monitor for the ripper to handle SWT specific features
 * 
 * @see GRipperMonitor
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 */
public class SWTRipperMonitor extends GRipperMonitor {

	SWTApplication application;
	
	// thread application is running on
	private Thread appThread;
	
	private final Display display;
	
	// Logger logger;
	SWTRipperConfiguration configuration;

	List<String> sRootWindows = new ArrayList<String>();

	List<String> sIgnoreWindowList = new ArrayList<String>();

	/**
	 * Temporary list of windows opened during the expand event is being
	 * performed. Those windows are in a native form to prevent data loss.
	 * 
	 */
	volatile LinkedList<Shell> tempOpenedWinStack = new LinkedList<Shell>();

	volatile LinkedList<Shell> tempClosedWinStack = new LinkedList<Shell>();
	
	/**
	 * Constructor
	 * 
	 * @param configuration
	 *            ripper configuration
	 */
	public SWTRipperMonitor(SWTRipperConfiguration configuration, Thread appThread) {
		super();
		// this.logger = logger;
		this.configuration = configuration;
		
		this.appThread = appThread;
		
		String[] URLs;
		if (configuration.getUrlList() != null)
			URLs = configuration.getUrlList()
					.split(GUITARConstants.CMD_ARGUMENT_SEPARATOR);
		else {
			URLs = new String[0];
		}
		
		try {
			application = new SWTApplication(configuration.getMainClass(),
					URLs, appThread);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) { // TODO handle these farther up the chain
			e.printStackTrace();
		}
		
		display = application.getDisplay();
	}

	@Override
	public List<GWindow> getRootWindows() {
		final List<GWindow> retWindowList = new ArrayList<GWindow>();
				
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (display.isDisposed()) {
					throw new AssertionError("display is disposed");
				}
				
				for (Shell shell : display.getShells()) {
					if (!isValidRootWindow(shell)) {
						continue;
					}

					if (sRootWindows.size() == 0
							|| (sRootWindows.contains(shell.getText()))) {
						
						GWindow gWindow = new SWTWindow(shell);
						retWindowList.add(gWindow);
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
			Thread.sleep(50);
		} catch (InterruptedException e) {
			GUITARLog.log.error(e);
		}
		return retWindowList;
	}

	@Override
	public void setUp() {

		// Registering default supported events

		EventManager em = EventManager.getInstance();

		for (Class<? extends SWTEventHandler> event : SWTConstants.DEFAULT_SUPPORTED_EVENTS) {
			em.registerEvent(event);
		}

		// Registering customized supported event
//		Class<? extends GEvent> gCustomizedEvents;

		String[] sCustomizedEventList;
		if (configuration.getCustomizedEventList() != null)
			sCustomizedEventList = configuration.getCustomizedEventList()
					.split(GUITARConstants.CMD_ARGUMENT_SEPARATOR);
		else
			sCustomizedEventList = new String[0];

		for (String sEvent : sCustomizedEventList) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends GEvent> cEvent = (Class<? extends GEvent>) Class.forName(sEvent);
				em.registerEvent(cEvent);
			} catch (ClassNotFoundException e) {
				GUITARLog.log.error(e);
			}

		}

		// Set up parameters
		sIgnoreWindowList = SWTConstants.sIgnoredWins;

		// Start the application
		try {
//			String[] URLs;
//			if (configuration.getUrlList() != null)
//				URLs = configuration.getUrlList()
//						.split(GUITARConstants.CMD_ARGUMENT_SEPARATOR);
//			else
//				URLs = new String[0];

//			application = new SWTApplication(configuration.getMainClass(),
//					URLs);

			// Parsing arguments
			String[] args;
			if (configuration.getArgumentList() != null)
				args = configuration.getArgumentList()
						.split(GUITARConstants.CMD_ARGUMENT_SEPARATOR);
			else
				args = new String[0];

			application.connect(args);

			// Delay ... we don't know why this was necessary
			// Internally in the preceding connect() call, we do another delay
			// try {
			// GUITARLog.log
			// .info("Initial waiting: "
			// + SWTRipperConfiguration.INITIAL_WAITING_TIME
			// + "ms...");
			// Thread.sleep(SWTRipperConfiguration.INITIAL_WAITING_TIME);
			// } catch (InterruptedException e) {
			// GUITARLog.log.error(e);
			// }

		} catch (ApplicationConnectException e) {
			GUITARLog.log.error(e);
		}
	}

	@Override
	public void cleanUp() {
		// remove this if you get deadlock, works sometimes though
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				display.dispose();
			}
		});
		GUITARLog.log.info("Display disposed");
		SWTGlobals.rootSeen = false; // TODO remove this when we figure out a better way to do this
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
			GWindow gWindow = new SWTWindow(shell);
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
		SWTWindow sWindow = (SWTWindow) gWindow;
		final Shell shell = sWindow.getShell();

		// TODO: A bug might happen here, will fix later
		// window.setVisible(false);
		shell.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				shell.dispose();
			}
		});
	}

	@Override
	public boolean isIgnoredWindow(GWindow window) {
		String sWindow = window.getTitle();
		// TODO: Ignore template
		return (this.sIgnoreWindowList.contains(sWindow));
	}

	/**
	 * TODO: perform actions on the GUI to see if we can expand
	 * the set of GUI elements
	 */
	@Override
	public void expandGUI(GComponent component) {
		GEvent action = new SWTActionEDT();
		action.perform(component, null);
		// JFCXComponent jComponent = (JFCXComponent) component;
		// Accessible aComponent = jComponent.getAComponent();
		// GComponent gComponent = new JFCXComponent(aComponent);

		// if (component == null)
		// return;

		// GUITARLog.log.info("Expanding *" + component.getTitle() + "*...");

		// GThreadEvent action = new JFCActionHandler();
		// GEvent action = new JFCActionEDT();

		// action.perform(component, null);
		// GUITARLog.log.info("Waiting  " + configuration.DELAY
		// + "ms for a new window to open");

		// new QueueTool().waitEmpty(configuration.DELAY);
		// new EventTool().waitNoEvent(configuration.DELAY);

		// try {
		// Thread.sleep(configuration.DELAY);
		// } catch (InterruptedException e) {
		// }
	}

	/**
	 * Check if a root window is worth ripping
	 * 
	 * @param window
	 *            the window to consider
	 * @return true if worth ripping, false otherwise
	 */
	private boolean isValidRootWindow(final Shell window) {
		final boolean[] visible = new boolean[1];
		
		window.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				visible[0] = window.isVisible();
			}
		});
		
		return visible[0];
	}

	/**
	 * Check if a component is click-able.
	 * 
	 * @param component
	 * @return true/false
	 */
	private boolean isClickable(final Widget widget) {
		final boolean[] disposed = new boolean[1];
		
		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				disposed[0] = widget.isDisposed();
			}
		});
		
		return !disposed[0];
	}

	@Override
	protected boolean isExpandable(GComponent gComponent, GWindow window) {

		Widget widget = ((SWTWidget) gComponent).getWidget();

		String ID = gComponent.getTitle();
		if (ID == null)
			return false;
		/*
		if ("".equals(ID)) // TODO fix title getting
			return false;
		*/
		if (!gComponent.isEnable()) {
			GUITARLog.log.debug("Component is disabled");
			return false;
		}

		if (!isClickable(widget)) {
			return false;
		}

		if (gComponent.getTypeVal().equals(GUITARConstants.TERMINAL))
			return false;
		
		return true;
	}

	@Override
	protected LinkedList<GWindow> getClosedWindowCache() {
		LinkedList<GWindow> retWindows = new LinkedList<GWindow>();

		for (Shell shell : tempClosedWinStack) {
			GWindow gWindow = new SWTWindow(shell);
			if (gWindow.isValid())
				retWindows.addLast(gWindow);
		}
		return retWindows;
	}

	public void setAppThread(Thread appThread) {
		this.appThread = appThread;
	}

	public Thread getAppThread() {
		return appThread;
	}
	
	public SWTApplication getApplication() {
		return application;
	}

}
