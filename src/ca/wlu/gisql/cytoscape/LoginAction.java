package ca.wlu.gisql.cytoscape;

import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.SwingConstants;

import ca.wlu.gisql.ast.util.BuiltInResolver;
import ca.wlu.gisql.db.DatabaseEnvironment;
import ca.wlu.gisql.db.DatabaseManager;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.gui.login.LoginDialog;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

public class LoginAction extends CytoscapeAction {

	private static final long serialVersionUID = 7148132663367787289L;

	private final Properties properties;

	public LoginAction(Properties properties) {
		super("gisQL Database Logon");
		this.properties = properties;
		setPreferredMenu("Plugins");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		DatabaseManager dm = LoginDialog.connect(Cytoscape.getDesktop(),
				properties);
		UserEnvironment environment = new UserEnvironment(
				new DatabaseEnvironment(dm));
		BuiltInResolver.addDefault(ToCyNetwork.class);

		Container container = new Container(environment);
		CytoPanel panel = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.WEST);
		panel.add("gisQL Console", null, container, "gisQL Console");
		panel.setState(CytoPanelState.DOCK);
	}

}
