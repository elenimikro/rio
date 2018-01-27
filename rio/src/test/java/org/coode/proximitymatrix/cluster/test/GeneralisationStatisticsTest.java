package org.coode.proximitymatrix.cluster.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.util.List;
import java.util.Set;

import org.coode.basetest.ClusteringHelper;
import org.coode.basetest.OntologyTestHelper;
import org.coode.distance.Distance;
import org.coode.oppl.Variable;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisationStatistics;
import org.coode.utils.owl.ClusterCreator;
import org.coode.utils.owl.DistanceCreator;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
@SuppressWarnings("javadoc")
public class GeneralisationStatisticsTest {
    @Test
    public void testAminoClusterCoverage() throws OWLOntologyCreationException, OPPLException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        // OWLOntology o = TestHelper.loadFileMappers(new File(amino_iri), m);
        OWLOntology o = m.loadOntologyFromOntologyDocument(
            getClass().getResourceAsStream("/amino-acid-original.owl"));
        ClusterCreator clusterer = new ClusterCreator();
        Distance<OWLEntity> distance = DistanceCreator.createAxiomRelevanceAxiomBasedDistance(m);
        Set<Cluster<OWLEntity>> agglomerateAll =
            clusterer.agglomerateAll(distance, asList(o.signature()));
        ClusterDecompositionModel<OWLEntity> model =
            clusterer.buildClusterDecompositionModel(o, agglomerateAll);
        GeneralisationStatistics<Cluster<OWLEntity>, OWLEntity> stats =
            GeneralisationStatistics.buildStatistics(model);
        double meanClusterCoveragePerGeneralisation =
            stats.getMeanClusterCoveragePerGeneralisation();
        assertTrue(meanClusterCoveragePerGeneralisation < 1);
        System.out.println("GeneralisationStatisticsTest.testClusterCoverage() "
            + meanClusterCoveragePerGeneralisation);
    }

    @Test
    public void testSmallOntologyClusterCoverage() {
        OWLOntology o = OntologyTestHelper.getSmallTestOntology();
        o.axioms().forEach(a -> System.out.println(
            "GeneralisationStatisticsTest.testSmallOntologyClusterCoverage() axiom: " + a));

        ClusterDecompositionModel<OWLEntity> model =
            ClusteringHelper.getSyntacticPopularityClusterModel(o);
        List<Cluster<OWLEntity>> clusterList = model.getClusterList();
        for (Cluster<OWLEntity> c : clusterList) {
            Variable<?> var = model.getVariableRepresentative(c);
            if (var != null) {
                System.out.println(
                    "GeneralisationStatisticsTest.testSmallOntologyClusterCoverage() Cluster "
                        + var.getName() + " \t size " + c.size());
                System.out.println(
                    "GeneralisationStatisticsTest.testSmallOntologyClusterCoverage() " + c);
            }
        }
        GeneralisationStatistics<Cluster<OWLEntity>, OWLEntity> stats =
            GeneralisationStatistics.buildStatistics(model);
        double meanClusterCoveragePerGeneralisation =
            stats.getMeanClusterCoveragePerGeneralisation();
        assertTrue("Expected " + meanClusterCoveragePerGeneralisation + " < 1",
            meanClusterCoveragePerGeneralisation < 1);
        assertEquals(0.875, meanClusterCoveragePerGeneralisation, 0.001);
        System.out.println("GeneralisationStatisticsTest.testClusterCoverage() "
            + meanClusterCoveragePerGeneralisation);
    }
}
