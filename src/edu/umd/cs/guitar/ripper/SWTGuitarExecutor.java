package edu.umd.cs.guitar.ripper;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.SWTApplication;
import edu.umd.cs.guitar.model.SWTConstants;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentListType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.Configuration;
import edu.umd.cs.guitar.model.data.FullComponentType;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.util.DefaultFactory;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * <p>
 * This is the parent class of <code>SWTRipper</code> and
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
public abstract class SWTGuitarExecutor {

	private final SWTGuitarConfiguration config;
	private final SWTApplication application;
	
	private final Configuration xmlConfig; 
	
	private long startTime;

	/**
	 * Constructs a new <code>SWTGuitarExecutor</code>. This constructor is
	 * equivalent to
	 * 
	 * <pre>
	 * SWTGuitarExecutor(config, Thread.currentThread())
	 * </pre>
	 * 
	 * Consequently, this constructor must be called on the same thread that the
	 * application under test is running on (usually the <code>main</code>
	 * thread).
	 * 
	 * @param config
	 *            configuration
	 * 
	 * @see SWTApplicationRunner
	 */
	protected SWTGuitarExecutor(SWTGuitarConfiguration config) {
		this(config, Thread.currentThread());
	}

	/**
	 * Constructs a new <code>SWTGuitarExecutor</code>. The thread passed in is
	 * the thread on which the SWT application under test runs. This is almost
	 * always the <code>main</code> thread (and actually must be the
	 * <code>main</code> thread on Cocoa).
	 * 
	 * @param config
	 *            configuration
	 * @param guiThread
	 *            thread the GUI runs on
	 * 
	 * @see SWTApplicationRunner
	 */
	protected SWTGuitarExecutor(SWTGuitarConfiguration config, Thread guiThread) {
		if (config == null) {
			config = new SWTRipperConfiguration();
		}
		
		this.config = config;
		this.application = initSWTApplication(config, guiThread);
		this.xmlConfig = loadXmlConfig();
		
		System.setProperty(GUITARLog.LOGFILE_NAME_SYSTEM_PROPERTY, config.getLogFile());
	}
	
	// initialize the SWTApplication
	private SWTApplication initSWTApplication(SWTGuitarConfiguration config, Thread guiThread) {
		SWTApplication app = new SWTApplication(config.getMainClass(), guiThread);
		
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
	 * Return the monitor used by this {@code SWTGuitarExecutor}. As their is no
	 * common superclass for all monitors, this method returns an {@code Object}
	 * . Subclasses are encouraged to modify the signature of their
	 * implementations of this method to return a more appropriate type, e.g.
	 * {@code SWTRipperMonitor} or {@code SWTReplayerMonitor}.
	 * 
	 * @return the monitor used by this executor
	 */
	public abstract Object getMonitor();

	/**
	 * Get the {@code SWTApplication} used by this {@code SWTGuitarExecutor}.
	 * 
	 * @return the {@code SWTApplication} that models the GUI
	 */
	public SWTApplication getApplication() {
		return application;
	}
	
	/**
	 * Get the time that the {@code SWTGuitarExecutor} starting executing. This
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
	 * Get the {@code Configuration} used by this {@code SWTGuitarExecutor}. The
	 * {@code Configuration} stores user-specified ignored and terminal
	 * components.
	 * 
	 * @return the {@code Configuration} used by this {@code SWTGuitarExecutor}
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
		initTerminalComponents();
		initIgnoredComponents();
		
		beginTiming();
	}

	/**
	 * Run this {@code SWTGuitarExecutor}. This method simply calls
	 * {@link #onBeforeExecute()}, {@link #onExecute()}, and then
	 * {@link #onAfterExecute()}.
	 */
	public final void execute() {
		onBeforeExecute();
		onExecute();
		onAfterExecute();
	}

	/**
	 * Execute the {@code SWTGuitarExecutor}. Called after
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
	
	private void initTerminalComponents() {
		List<FullComponentType> cTerminalList = getXmlConfig().getTerminalComponents().getFullComponent();

		for (FullComponentType cTermWidget : cTerminalList) {
			ComponentType component = cTermWidget.getComponent();
			AttributesType attributes = component.getAttributes();
			if (attributes != null) {
				SWTConstants.sTerminalWidgetSignature.add(new AttributesTypeWrapper(component.getAttributes()));
			}
		}
	}
	
	private void initIgnoredComponents() {
		List<FullComponentType> lIgnoredComps = new ArrayList<FullComponentType>();
		ComponentListType ignoredAll = getXmlConfig().getIgnoredComponents();

		if (ignoredAll != null)
			for (FullComponentType fullComp : ignoredAll.getFullComponent()) {
				ComponentType comp = fullComp.getComponent();

				if (comp == null) {
					ComponentType win = fullComp.getWindow();
					ComponentTypeWrapper winAdapter = new ComponentTypeWrapper(win);
					String ID = winAdapter.getFirstValueByName(GUITARConstants.ID_TAG_NAME);
					if (ID != null) {
						SWTConstants.sIgnoredWins.add(ID);
					}

				} else {
					lIgnoredComps.add(fullComp);
				}
			}
	}
	
}
