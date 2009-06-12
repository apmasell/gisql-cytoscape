package ca.wlu.gisql.cytoscape;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.parser.Literal;
import ca.wlu.gisql.environment.parser.NextTask;
import ca.wlu.gisql.environment.parser.Parseable;
import ca.wlu.gisql.environment.parser.Parser;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.InteractomeUtil;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;

public class ToCyNetwork implements Interactome {
	public final static Parseable descriptor = new Parseable() {

		public Interactome construct(Environment environment,
				List<Object> params, Stack<String> error) {
			Interactome interactome = (Interactome) params.get(0);
			return new ToCyNetwork(interactome);
		}

		public int getNestingLevel() {
			return 0;
		}

		public boolean isMatchingOperator(char c) {
			return c == '@';
		}

		public boolean isPrefixed() {
			return false;
		}

		public PrintStream show(PrintStream print) {
			print.print("Create Cytoscape Network: A @ *");
			return null;
		}

		public StringBuilder show(StringBuilder sb) {
			sb.append("Create Cytoscape Network: A @ *");
			return sb;
		}

		public NextTask[] tasks(Parser parser) {
			return new NextTask[] { new Literal(parser, '*') };
		}

	};
	private CyNetwork network;
	private Interactome source;

	public ToCyNetwork(Interactome interactome) {
		this.source = interactome;
	}

	public double calculateMembership(Gene gene) {
		return source.calculateMembership(gene);
	}

	public double calculateMembership(Interaction interaction) {
		double membership = source.calculateMembership(interaction);
		CyNode gene1 = Cytoscape.getCyNode(interaction.getGene1().toString(),
				true);
		CyNode gene2 = Cytoscape.getCyNode(interaction.getGene2().toString(),
				true);
		CyEdge edge = Cytoscape.getCyEdge(gene1, gene2, Semantics.INTERACTION,
				"gg", true, false);
		network.addNode(gene1);
		network.addNode(gene2);
		network.addEdge(edge);
		return membership;
	}

	public Interactome fork(Interactome substitute) {
		return new ToCyNetwork(source.fork(substitute));
	}

	public int getPrecedence() {
		return descriptor.getNestingLevel();
	}

	public Type getType() {
		return source.getType();
	}

	public double membershipOfUnknown() {
		return source.membershipOfUnknown();
	}

	public boolean needsFork() {
		return source.needsFork();
	}

	public int numGenomes() {
		return source.numGenomes();
	}

	public boolean postpare() {
		return source.postpare();
	}

	public boolean prepare() {
		network = Cytoscape.createNetwork(source.toString());
		return source.prepare();
	}

	public PrintStream show(PrintStream print) {
		InteractomeUtil.precedenceShow(print, source, this.getPrecedence());
		print.print(" @ *");
		return print;
	}

	public StringBuilder show(StringBuilder sb) {
		InteractomeUtil.precedenceShow(sb, source, this.getPrecedence());
		sb.append(" @ *");
		return sb;
	}

	public String toString() {
		return show(new StringBuilder()).toString();
	}

}
