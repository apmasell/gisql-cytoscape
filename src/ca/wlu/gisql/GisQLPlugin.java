package ca.wlu.gisql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import ca.wlu.gisql.cytoscape.LoginAction;
import ca.wlu.gisql.cytoscape.ToCyNetwork;
import ca.wlu.gisql.environment.parser.Parser;
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
		Parser.addParseable(ToCyNetwork.descriptor);

		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction(
				new LoginAction(properties));
	}

	public void onCytoscapeExit() {
		if (properties != null) {
			try {
				properties.store(new FileOutputStream(propertiesfile),
						"gisql database configuration");
			} catch (Exception e) {
				log.error("Failed to save gisQL configuration.", e);
			}
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
				} catch (IOException e) {
					log.fatal("Error while reading properties.", e);
				}
			}
		}
	}

}
