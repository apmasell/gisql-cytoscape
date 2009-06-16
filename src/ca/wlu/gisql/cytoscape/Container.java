package ca.wlu.gisql.cytoscape;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.gui.BusyDialog;
import ca.wlu.gisql.gui.CommandBox;
import ca.wlu.gisql.gui.InteractomeTask;
import ca.wlu.gisql.gui.TaskParent;
import ca.wlu.gisql.gui.output.EnvironmentTreeView;
import ca.wlu.gisql.gui.output.InteractomeTreeCellRender;
import ca.wlu.gisql.interactome.CachedInteractome;
import cytoscape.Cytoscape;

public class Container extends JPanel implements ActionListener, TaskParent {

	private static final long serialVersionUID = 9200554954420289191L;

	private final CommandBox command;

	private final EnvironmentTreeView environmentTree;

	/* TODO: Replace with Cytoscape API in 3.0. */
	public final BusyDialog progress = new BusyDialog(Cytoscape.getDesktop(),
			this);

	private InteractomeTask<Container> task = null;
	private final JTree variablelist;

	private final JScrollPane variablelistPane;

	public Container(UserEnvironment environment) {
		super();
		environmentTree = new EnvironmentTreeView(environment);
		variablelist = new JTree(environmentTree);
		variablelist.setCellRenderer(new InteractomeTreeCellRender());
		variablelist.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		variablelistPane = new JScrollPane(variablelist);

		command = new CommandBox(environment);
		command.setActionListener(this);

		setLayout(new BorderLayout());
		add(command, BorderLayout.SOUTH);
		add(variablelistPane, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == command) {
			CachedInteractome interactome = CachedInteractome.wrap(command
					.getInteractome(), null);
			task = new InteractomeTask<Container>(this, interactome);
			task.execute();
			progress.start(task.getMessage());
		} else if (e.getSource() == progress && task != null) {
			task.cancel(true);
			task = null;
		}
	}

	public void processedInteractome(CachedInteractome interactome) {
		progress.stop();
	}
}
