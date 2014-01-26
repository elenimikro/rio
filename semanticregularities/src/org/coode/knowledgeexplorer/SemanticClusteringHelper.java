package org.coode.knowledgeexplorer;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.coode.basetest.ClusteringHelper;
import org.coode.distance.Distance;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.utils.owl.ClusterCreator;
import org.coode.utils.owl.DistanceCreator;
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
        Distance<OWLEntity> distance = DistanceCreator
                .createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(o, ke);
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
