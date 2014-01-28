package org.coode.utils.owl;

import org.coode.distance.Distance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.distance.owl.StructuralAxiomRelevanceAxiomBasedDistance;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author Eleni Mikroyannidi */
public class DistanceCreator {
    /** @param manager
     *            manager
     * @return distance */
    public static Distance<OWLEntity> createAxiomRelevanceAxiomBasedDistance(
            OWLOntologyManager manager) {
        OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
                manager.getOWLDataFactory(), new ReplacementByKindStrategy(
                        manager.getOWLDataFactory()));
        Distance<OWLEntity> distance = new AxiomRelevanceAxiomBasedDistance(
                manager.getOntologies(), owlEntityReplacer, manager);
        return distance;
    }

    /** @param manager
     *            manager
     * @return distance */
    public static Distance<OWLEntity> createOWLEntityRelevanceAxiomBasedDistance(
            OWLOntologyManager manager) {
        Distance<OWLEntity> distance = new OWLEntityRelevanceAxiomBasedDistance(
                manager.getOntologies(), manager);
        return distance;
    }

    /** @param manager
     *            manager
     * @return distance */
    public static Distance<OWLEntity> createStructuralAxiomRelevanceAxiomBasedDistance(
            OWLOntologyManager manager) {
        Distance<OWLEntity> distance = new StructuralAxiomRelevanceAxiomBasedDistance(
                manager.getOntologies(), manager.getOWLDataFactory(), manager);
        return distance;
    }
}
