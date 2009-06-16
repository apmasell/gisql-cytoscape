package ca.wlu.gisql.cytoscape;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import ca.wlu.gisql.db.DatabaseEnvironment;
import ca.wlu.gisql.db.DatabaseManager;
import ca.wlu.gisql.environment.UserEnvironment;
import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

public class LoginDialog extends JDialog implements ActionListener {

	private final static String defaulturl = "jdbc:postgresql://localhost/interactome";

	private static final Logger log = Logger.getLogger(LoginDialog.class);

	private static final long serialVersionUID = -3790429193777222647L;

	public static void connect(Properties properties) {
		LoginDialog dialog = new LoginDialog(properties);
		dialog.setVisible(true);
	}

	private JButton cancel = new JButton("Cancel");

	private JButton connect = new JButton("Connect");

	private JPasswordField password = new JPasswordField();

	private final Properties properties;

	private JTextField url = new JTextField();

	private JTextField username = new JTextField();

	private LoginDialog(Properties properties) {
		super(Cytoscape.getDesktop(), "Log in", true);
		this.properties = properties;
		setResizable(false);
		setSize(407, 303);
		getContentPane().setLayout(new GridLayout(4, 2));

		setModal(true);
		connect.addActionListener(this);
		cancel.addActionListener(this);

		getContentPane().add(new JLabel("User:"));
		getContentPane().add(username);
		getContentPane().add(new JLabel("Password:"));
		getContentPane().add(password);
		getContentPane().add(new JLabel("JDBC URL:"));
		getContentPane().add(url);
		getContentPane().add(connect);
		getContentPane().add(cancel);

		username.setText(properties.getProperty("user", ""));
		password.setText(properties.getProperty("password", ""));
		url.setText(properties.getProperty("url", defaulturl));
		if (url.getText().length() == 0)
			url.setText(defaulturl);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == connect) {
			properties.setProperty("user", username.getText());
			properties.setProperty("password", new String(password
					.getPassword()));
			properties.setProperty("url", url.getText());

			DatabaseManager dm;
			UserEnvironment environment;
			try {
				dm = new DatabaseManager(properties);
			} catch (Exception ex) {
				log.error("Failed to connect to database.", ex);
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						"Failed to connect to database.", "gisQL",
						JOptionPane.ERROR_MESSAGE);
				environment = null;
				return;
			}
			environment = new UserEnvironment(new DatabaseEnvironment(dm));

			Container container = new Container(environment);
			CytoPanel panel = Cytoscape.getDesktop().getCytoPanel(
					SwingConstants.WEST);
			panel.add("gisQL Console", null, container, "gisQL Console");
			panel.setState(CytoPanelState.DOCK);

		}
		dispose();
	}
}
