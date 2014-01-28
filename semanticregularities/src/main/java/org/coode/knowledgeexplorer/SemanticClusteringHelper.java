package org.coode.knowledgeexplorer;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerAxiomRelevanceAxiomBasedDistance;
import org.coode.knowledgeexplorer.KnowledgeExplorerOWLEntityRelevanceBasedDistance;
import org.coode.knowledgeexplorer.StructuralKnowledgeExplorerAxiomRelevanceBasedDistance;

import org.coode.basetest.ClusteringHelper;
import org.coode.distance.Distance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.utils.owl.ClusterCreator;
import org.coode.utils.owl.DistanceCreator;
import org.coode.utils.owl.KnowledgeExplorer;
import org.coode.utils.owl.KnowledgeExplorerAxiomRelevanceAxiomBasedDistance;
import org.coode.utils.owl.KnowledgeExplorerOWLEntityRelevanceBasedDistance;
import org.coode.utils.owl.StructuralKnowledgeExplorerAxiomRelevanceBasedDistance;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasoner;
import uk.ac.manchester.cs.factplusplus.owlapiv3.OWLKnowledgeExplorationReasonerWrapper;

/** @author eleni */
public class SemanticClusteringHelper {
    /** @param o
     *            o
     * @return cluster decomposition model
     * @throws Exception
     *             Exception */
    public static ClusterDecompositionModel<OWLEntity> getSemanticPopularityClusterModel(
            OWLOntology o) throws Exception {
        OWLOntologyManager m = o.getOWLOntologyManager();
        KnowledgeExplorer ke = runFactplusplusKnowledgeExplorerReasoner(o);
        Distance<OWLEntity> distance = createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(o, ke);
        ClusterCreator clusterer = new ClusterCreator();
        final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
            @Override
            public int compare(OWLEntity o1, OWLEntity o2) {
                return shortFormProvider.getShortForm(o1).compareTo(
                        shortFormProvider.getShortForm(o2));
            }
        });
        entities.addAll(ke.getEntities());
        Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(distance, entities);
        ClusterDecompositionModel<OWLEntity> model = clusterer
                .buildKnowledgeExplorerClusterDecompositionModel(o, ke.getAxioms(), m,
                        clusters);
        ClusteringHelper.extractGeneralisationMap(model);
        return model;
    }
    /** @param o
     *            o
     * @param ke
     *            ke
     * @return distance */
    public static Distance<OWLEntity>
            createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(OWLOntology o,
                    KnowledgeExplorer ke) {
        OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(o
                .getOWLOntologyManager().getOWLDataFactory(),
                new ReplacementByKindStrategy(o.getOWLOntologyManager()
                        .getOWLDataFactory()));
        Distance<OWLEntity> distance = new KnowledgeExplorerAxiomRelevanceAxiomBasedDistance(
                o, owlEntityReplacer, ke);
        return distance;
    }

    /** @param o
     *            o
     * @param ke
     *            ke
     * @return distance */
    public static Distance<OWLEntity>
            createKnowledgeExplorerOWLEntityRelevanceBasedDistance(OWLOntology o,
                    KnowledgeExplorer ke) {
        Distance<OWLEntity> distance = new KnowledgeExplorerOWLEntityRelevanceBasedDistance(
                o, ke);
        return distance;
    }

    /** @param ontology
     *            ontology
     * @param ke
     *            ke
     * @return distance */
    public static Distance<OWLEntity>
            createStructuralKnowledgeExplorerAxiomRelevanceBasedDistance(
                    OWLOntology ontology, KnowledgeExplorer ke) {
        Distance<OWLEntity> distance = new StructuralKnowledgeExplorerAxiomRelevanceBasedDistance(
                ontology, ke);
        return distance;
    }


    private static KnowledgeExplorer runFactplusplusKnowledgeExplorerReasoner(
            OWLOntology o) {
        OWLReasoner reasoner = new FaCTPlusPlusReasoner(o, new SimpleConfiguration(),
                BufferingMode.NON_BUFFERING);
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        KnowledgeExplorer ke = new KnowledgeExplorerMaxFillersImpl(reasoner,
                new OWLKnowledgeExplorationReasonerWrapper(new FaCTPlusPlusReasoner(o,
                        new SimpleConfiguration(), BufferingMode.NON_BUFFERING)));
        return ke;
    }
}
