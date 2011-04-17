package edu.umd.cs.guitar.ripper;

import java.io.InputStream;
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

public abstract class SWTGuitarExecutor {

	private final SWTGuitarConfiguration config;
	private final SWTApplication application;
	
	private final Configuration xmlConfig; 
	
	private long startTime;
	
	protected SWTGuitarExecutor(SWTGuitarConfiguration config) {
		this(config, Thread.currentThread());
	}
	
	protected SWTGuitarExecutor(SWTGuitarConfiguration config, Thread guiThread) {
		if (config == null) {
			config = new SWTRipperConfiguration();
		}
		
		this.config = config;
		this.application = new SWTApplication(config.getMainClass(), guiThread);
		
		this.xmlConfig = loadXmlConfig();
		
		System.setProperty(GUITARLog.LOGFILE_NAME_SYSTEM_PROPERTY, config.getLogFile());
	}
	
	private Configuration loadXmlConfig() {
		Configuration conf = null;

		try {
			conf = (Configuration) IO.readObjFromFile(config.getConfigFile(),
					Configuration.class);

			if (conf == null) {
				InputStream in = getClass().getClassLoader()
						.getResourceAsStream(config.getConfigFile());
				conf = (Configuration) IO.readObjFromFile(in,
						Configuration.class);
			}

		} catch (Exception e) {
			// this space left intentionally blank
		}
		
		if (conf == null) {
			GUITARLog.log.info("No configuration file. Using an empty one...");
			DefaultFactory df = new DefaultFactory();
			conf = df.createDefaultConfiguration();
		}
		
		return conf;
	}
	
	protected SWTGuitarConfiguration getConfig() {
		return config;
	}
	
	public SWTApplication getApplication() {
		return application;
	}
	
	protected long getStartTime() {
		return startTime;
	}
	
	protected Configuration getXmlConfig() {
		return xmlConfig;
	}

	/**
	 * Called directly before the {@link #execute() execute} method.
	 */
	protected void onBeforeExecute() {		
		initTerminalComponents();
		initIgnoredComponents();
		
		beginTiming();
	}
	
	public void execute() {
		onBeforeExecute();
		onExecute();
		onAfterExecute();
	}
	
	protected abstract void onExecute();
	
	protected void onAfterExecute() {
		endTiming();
	}
	
	protected void beginTiming() {
		startTime = System.currentTimeMillis();
	}
	
	protected void endTiming() {
		long nEndTime = System.currentTimeMillis();
		long nDuration = nEndTime - getStartTime();
		DateFormat df = new SimpleDateFormat("HH : mm : ss: SS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		GUITARLog.log.info("Time Elapsed: " + df.format(nDuration));
	}
	
	protected void initTerminalComponents() {
		List<FullComponentType> cTerminalList = getXmlConfig().getTerminalComponents().getFullComponent();

		for (FullComponentType cTermWidget : cTerminalList) {
			ComponentType component = cTermWidget.getComponent();
			AttributesType attributes = component.getAttributes();
			if (attributes != null) {
				SWTConstants.sTerminalWidgetSignature.add(new AttributesTypeWrapper(component.getAttributes()));
			}
		}
	}
	
	protected void initIgnoredComponents() {
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
