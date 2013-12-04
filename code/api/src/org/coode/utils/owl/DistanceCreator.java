package org.coode.utils.owl;

import org.coode.distance.Distance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.distance.owl.StructuralAxiomRelevanceAxiomBasedDistance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerAxiomRelevanceAxiomBasedDistance;
import org.coode.knowledgeexplorer.KnowledgeExplorerOWLEntityRelevanceBasedDistance;
import org.coode.knowledgeexplorer.StructuralKnowledgeExplorerAxiomRelevanceBasedDistance;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author Eleni Mikroyannidi */
public class DistanceCreator {
    public static Distance<OWLEntity> createAxiomRelevanceAxiomBasedDistance(
            OWLOntologyManager manager) {
        final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
                manager.getOWLDataFactory(), new ReplacementByKindStrategy(
                        manager.getOWLDataFactory()));
        final Distance<OWLEntity> distance = new AxiomRelevanceAxiomBasedDistance(
                manager.getOntologies(), owlEntityReplacer, manager);
        return distance;
    }

    public static Distance<OWLEntity> createOWLEntityRelevanceAxiomBasedDistance(
            final OWLOntologyManager manager) {
        final Distance<OWLEntity> distance = new OWLEntityRelevanceAxiomBasedDistance(
                manager.getOntologies(), manager);
        return distance;
    }

    public static Distance<OWLEntity> createStructuralAxiomRelevanceAxiomBasedDistance(
            final OWLOntologyManager manager) {
        final Distance<OWLEntity> distance = new StructuralAxiomRelevanceAxiomBasedDistance(
                manager.getOntologies(), manager.getOWLDataFactory(), manager);
        return distance;
    }

    public static Distance<OWLEntity>
            createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(OWLOntology o,
                    KnowledgeExplorer ke) {
        final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(o
                .getOWLOntologyManager().getOWLDataFactory(),
                new ReplacementByKindStrategy(o.getOWLOntologyManager()
                        .getOWLDataFactory()));
        final Distance<OWLEntity> distance = new KnowledgeExplorerAxiomRelevanceAxiomBasedDistance(
                o, owlEntityReplacer, ke);
        return distance;
    }

    public static Distance<OWLEntity>
            createKnowledgeExplorerOWLEntityRelevanceBasedDistance(OWLOntology o,
                    KnowledgeExplorer ke) {
        final Distance<OWLEntity> distance = new KnowledgeExplorerOWLEntityRelevanceBasedDistance(
                o, ke);
        return distance;
    }

    public static Distance<OWLEntity>
            createStructuralKnowledgeExplorerAxiomRelevanceBasedDistance(
                    final OWLOntology ontology, KnowledgeExplorer ke) {
        final Distance<OWLEntity> distance = new StructuralKnowledgeExplorerAxiomRelevanceBasedDistance(
                ontology, ke);
        return distance;
    }
}
