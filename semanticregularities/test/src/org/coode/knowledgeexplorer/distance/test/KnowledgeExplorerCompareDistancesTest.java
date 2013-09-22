package org.coode.knowledgeexplorer.distance.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Set;

import org.coode.distance.owl.AbstractAxiomBasedDistance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillersFactplusplusImpl;
import org.coode.utils.owl.DistanceCreator;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.jfact.JFactReasoner;

public class KnowledgeExplorerCompareDistancesTest {

	public KnowledgeExplorerCompareDistancesTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Test
	public void testPopularityStructuralDistance()
			throws OWLOntologyCreationException {
		String ontology_iri = "bioportal/minimal-anatomical-terminology/minimal-anatomical-terminology_main.owl";
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o = m.loadOntologyFromOntologyDocument(new File(
				ontology_iri));

		JFactReasoner reasoner = new JFactReasoner(o,
				new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
		reasoner.precomputeInferences();
		KnowledgeExplorer ke = new KnowledgeExplorerMaxFillersFactplusplusImpl(
				reasoner);
		Set<OWLAxiom> axioms = ke.getAxioms();

		AbstractAxiomBasedDistance popularity_distance = (AbstractAxiomBasedDistance) DistanceCreator
				.createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(
o, ke);

		AbstractAxiomBasedDistance structural_distance = (AbstractAxiomBasedDistance) DistanceCreator
				.createStructuralKnowledgeExplorerAxiomRelevanceBasedDistance(
						o, ke);

		MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<OWLEntity, OWLAxiom>();
		for (OWLAxiom ax : axioms) {
			if (!ax.isOfType(AxiomType.DECLARATION)) {
				for (OWLEntity e : ax.getSignature()) {
					candidates.put(e, ax);
				}
			}
		}

		OWLClass c = o
				.getOWLOntologyManager()
				.getOWLDataFactory()
				.getOWLClass(
						IRI.create("http://purl.obolibrary.org/obo/MAT_0000196"));
		System.out
				.println("KnowledgeExplorerCompareDistancesTest.testPopularityStructuralDistance() Entailments of "
						+ c);
		for (OWLAxiom ax : candidates.get(c)) {
			System.out.println(ax);
		}
		assertEquals(3, candidates.get(c).size());
		System.out.println("structural ");
		for (OWLAxiom ax : structural_distance.getAxioms(c)) {
			System.out.println("\t" + ax);
		}
		System.out.println("popularity ");
		for (OWLAxiom ax : popularity_distance.getAxioms(c)) {
			System.out.println("\t" + ax);
		}

		assertTrue(popularity_distance.getAxioms(c).equals(
				structural_distance.getAxioms(c)));

		Set<OWLEntity> entities = ke.getEntities();
		for (OWLEntity e : entities) {
			System.out.println("Entity: " + e.toString());
			printDistance("popularity", popularity_distance, e);
			printDistance("structural", structural_distance, e);
		}

	}

	private void printDistance(String type,
			AbstractAxiomBasedDistance distance, OWLEntity e) {
		System.out.println("\t" + type + " distance axioms: ");
		for (OWLAxiom ax : distance.getAxioms(e)) {
			System.out.println("\t\t" + ax.toString());
		}
	}

}
