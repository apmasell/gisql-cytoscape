package ca.wlu.gisql.cytoscape;

import java.util.Set;

import ca.wlu.gisql.annotation.GisqlConstructorFunction;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.interactome.ProcessableInteractome;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;

@GisqlConstructorFunction(name="cytoscape", description="Converts an interactome to a Cytoscape network.")
public class ToCyNetwork extends ProcessableInteractome {
	
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

	public Set<Interactome> collectAll(Set<Interactome> set) {
		return source.collectAll(set);
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	public Construction getConstruction() {
		return source.getConstruction();
	}

	public double membershipOfUnknown() {
		return source.membershipOfUnknown();
	}

	@Override
	public boolean postpare() {
		return source.postpare();
	}

	public boolean prepare() {
		network = Cytoscape.createNetwork(source.toString());
		return source.prepare();
	}

	public void show(ShowablePrintWriter<Set<Interactome>> print) {
		print.print(source, this.getPrecedence());
		print.print(" :cytoscape");
	}

}
