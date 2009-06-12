package ca.wlu.gisql.cytoscape;

import java.awt.event.ActionEvent;
import java.util.Properties;

import cytoscape.util.CytoscapeAction;

public class LoginAction extends CytoscapeAction {

	private static final long serialVersionUID = 7148132663367787289L;

	private final Properties properties;

	public LoginAction(Properties properties) {
		super("gisQL Database Logon");
		this.properties = properties;
		setPreferredMenu("Plugins");
	}

	public void actionPerformed(ActionEvent event) {
		LoginDialog.connect(properties);
	}

}
