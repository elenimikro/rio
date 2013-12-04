package org.coode.popularitydistance.profiling;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.distance.Distance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillersFactplusplusImpl;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.utils.SimpleMetric;
import org.coode.utils.owl.ClusterCreator;
import org.coode.utils.owl.DistanceCreator;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.jfact.JFactReasoner;
import experiments.ClusteringWithADEvaluationExperimentBase;

public class SemanticPopularityClusteringClusteringTest {
    public SemanticPopularityClusteringClusteringTest() {
        // TODO Auto-generated constructor stub
    }

    /** @param args
     * @throws ParserConfigurationException
     * @throws OPPLException
     * @throws FileNotFoundException
     * @throws OWLOntologyCreationException */
    // public static void main(String[] args) throws
    // OWLOntologyCreationException,
    // FileNotFoundException, OPPLException, ParserConfigurationException {
    // testSemanticPopularityClusteringStats();
    //
    // }
    @Test
    public void testSemanticPopularityClusteringStats() throws OPPLException,
            OWLOntologyCreationException, FileNotFoundException {
        String ontology_iri = "bioportal/minimal-anatomical-terminology/minimal-anatomical-terminology_main.owl";
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.loadOntologyFromOntologyDocument(new File(ontology_iri));
        PrintStream out = new PrintStream(
                new File("profiling_ontology_cluster_stats.csv"));
        JFactReasoner reasoner = new JFactReasoner(o, new SimpleConfiguration(),
                BufferingMode.NON_BUFFERING);
        reasoner.precomputeInferences();
        KnowledgeExplorer ke = new KnowledgeExplorerMaxFillersFactplusplusImpl(reasoner);
        Distance<OWLEntity> distance = DistanceCreator
                .createStructuralKnowledgeExplorerAxiomRelevanceBasedDistance(o, ke);
        final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
            @Override
            public int compare(final OWLEntity o1, final OWLEntity o2) {
                return shortFormProvider.getShortForm(o1).compareTo(
                        shortFormProvider.getShortForm(o2));
            }
        });
        entities.addAll(ke.getEntities());
        ClusterCreator clusterer = new ClusterCreator();
        Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(distance, entities);
        ClusterDecompositionModel<OWLEntity> model = clusterer
                .buildKnowledgeExplorerClusterDecompositionModel(o, ke.getAxioms(), m,
                        clusters);
        // List<SimpleMetric<Double>> list = new
        // ArrayList<SimpleMetric<Double>>();
        Collection<? extends SimpleMetric<?>> stats = ClusteringWithADEvaluationExperimentBase
                .getClusteringStats(out, model.getClusterList());
        for (SimpleMetric<?> sm : stats) {
            if (sm.getName().equals("MeanInternalDistance")) {
                assertEquals(0.319, (Double) sm.getValue(), 0.001);
            }
            if (sm.getName().equals("MeanExternalDistance")) {
                assertEquals(0.333, (Double) sm.getValue(), 0.001);
            }
            if (sm.getName().equals("MaxInternalDistance")) {
                assertEquals(0.0, (Double) sm.getValue(), 0.001);
            }
            if (sm.getName().equals("MaxInternalDistance")) {
                assertEquals(0.0, (Double) sm.getValue(), 0.001);
            }
            if (sm.getName().equals("MinInternalDistance")) {
                assertEquals(0.0, (Double) sm.getValue(), 0.001);
            }
            if (sm.getName().equals("MaxExternalDistance")) {
                assertEquals(1.0, (Double) sm.getValue(), 0.001);
            }
            if (sm.getName().equals("minExternalDistance")) {
                assertEquals(0.333, (Double) sm.getValue(), 0.001);
            }
            System.out.println(sm.getName() + " : " + sm.getValue());
        }
    }
}
