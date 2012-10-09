package org.coode.basetest;

import org.coode.distance.Distance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.semanticweb.owlapi.model.OWLEntity;
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
	
	
	
}
