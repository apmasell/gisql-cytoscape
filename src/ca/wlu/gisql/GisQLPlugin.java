package ca.wlu.gisql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import ca.wlu.gisql.cytoscape.LoggingAdapter;
import ca.wlu.gisql.cytoscape.LoginAction;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.plugin.CytoscapePlugin;

public class GisQLPlugin extends CytoscapePlugin {

	private static final Logger log = Logger.getLogger(GisQLPlugin.class);
	private final Properties properties = new Properties();

	private File propertiesfile = CytoscapeInit
			.getConfigFile("gisql.properties");

	public GisQLPlugin() {
		super();
		Logger.getRootLogger().addAppender(new LoggingAdapter());

		restoreInitState();

		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction(
				new LoginAction(properties));
	}

	@Override
	public void onCytoscapeExit() {
		try {
			/* Do not save the raw password to the file. */
			properties.remove("password");
			properties.store(new FileOutputStream(propertiesfile),
					"gisql database configuration");
		} catch (Exception e) {
			log.error("Failed to save gisQL configuration.", e);
		}
	}

	public synchronized void restoreInitState() {
		if (propertiesfile != null && propertiesfile.exists()) {
			InputStream is;
			try {
				is = new FileInputStream(propertiesfile);
			} catch (FileNotFoundException e) {
				log.fatal("Cannot find properties file.", e);
				return;
			}
			if (is == null) {
				log.fatal("Cannot find gisql.properties.");
			} else {
				try {
					properties.load(is);
					log.info("Loaded properties from file.");
				} catch (IOException e) {
					log.fatal("Error while reading properties.", e);
				}
			}
		} else {
			log.info("No properties file to read.");
		}
	}

}
