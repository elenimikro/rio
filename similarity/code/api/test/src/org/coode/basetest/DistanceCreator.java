package org.coode.basetest;

import org.coode.distance.Distance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.KnowledgeExplorerAxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.KnowledgeExplorerOWLEntityRelevanceBasedDistance;
import org.coode.distance.owl.OWLEntityRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.distance.owl.StructuralAxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.StructuralKnowledgeExplorerAxiomRelevanceBasedDistance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;


public class DistanceCreator {
	
	public static Distance<OWLEntity> createAxiomRelevanceAxiomBasedDistance(OWLOntologyManager manager){
		final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
                manager.getOWLDataFactory(), new ReplacementByKindStrategy(
                        manager.getOWLDataFactory()));
        final Distance<OWLEntity> distance = new AxiomRelevanceAxiomBasedDistance(
                manager.getOntologies(), owlEntityReplacer, manager);
        return distance;
	}
	
	public static Distance<OWLEntity> createOWLEntityRelevanceAxiomBasedDistance(final OWLOntologyManager manager) {
        final Distance<OWLEntity> distance = new OWLEntityRelevanceAxiomBasedDistance(
                manager.getOntologies(), manager);
        return distance;
}
	
	public static Distance<OWLEntity> createStructuralAxiomRelevanceAxiomBasedDistance(final OWLOntologyManager manager) {
	        final Distance<OWLEntity> distance = new StructuralAxiomRelevanceAxiomBasedDistance(
	                manager.getOntologies(), manager.getOWLDataFactory(), manager);
	        return distance;
	}
	
	public static Distance<OWLEntity> createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(
			final OWLOntologyManager manager, KnowledgeExplorer ke) {
		final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
				manager.getOWLDataFactory(), new ReplacementByKindStrategy(
						manager.getOWLDataFactory()));
		final Distance<OWLEntity> distance = new KnowledgeExplorerAxiomRelevanceAxiomBasedDistance(
				manager.getOntologies(), owlEntityReplacer, manager, ke);
		return distance;
	}
	
	public static Distance<OWLEntity> createKnowledgeExplorerOWLEntityRelevanceBasedDistance(
			final OWLOntologyManager manager, KnowledgeExplorer ke) {
		final Distance<OWLEntity> distance = new KnowledgeExplorerOWLEntityRelevanceBasedDistance(
				manager.getOntologies(), manager, ke);
		return distance;
	}
	
	public static Distance<OWLEntity> createStructuralKnowledgeExplorerAxiomRelevanceBasedDistance(
			final OWLOntology ontology, KnowledgeExplorer ke) {
		final Distance<OWLEntity> distance = new StructuralKnowledgeExplorerAxiomRelevanceBasedDistance(
				ontology, ke);
		return distance;
	}
}
