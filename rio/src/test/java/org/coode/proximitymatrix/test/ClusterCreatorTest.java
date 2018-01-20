package org.coode.proximitymatrix.test;

import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.coode.basetest.TestHelper;
import org.coode.distance.Distance;
import org.coode.distance.wrapping.DistanceTableObject;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterStatistics;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.owl.ClusterCreator;
import org.coode.utils.owl.DistanceCreator;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/** @author eleni */
@SuppressWarnings("javadoc")
public class ClusterCreatorTest {
    private OWLOntology ontology;
    private ClusterCreator clusterer;
    private Distance<OWLEntity> distance;
    private Set<Cluster<OWLEntity>> clusters;

    @Before
    public void setUp() {
        ontology = TestHelper.getPizza();
        clusterer = new ClusterCreator();
        distance = DistanceCreator
            .createAxiomRelevanceAxiomBasedDistance(ontology.getOWLOntologyManager());
        List<OWLEntity> entities = Utils.getSortedSignature(ontology);
        clusters = clusterer.agglomerateAll(distance, entities);
    }

    @Test
    public void testClusteringMatrix() {
        ClusteringProximityMatrix<DistanceTableObject<OWLEntity>> clusteringMatrix =
            clusterer.getClusteringMatrix();
        assertFalse(clusteringMatrix.getObjects().equals(0));
        Collection<Collection<? extends DistanceTableObject<OWLEntity>>> objects =
            clusteringMatrix.getObjects();
        for (Collection<? extends DistanceTableObject<OWLEntity>> anObject : objects) {
            for (Collection<? extends DistanceTableObject<OWLEntity>> anotherObject : objects) {
                double d = clusteringMatrix.getDistance(anObject, anotherObject);
                assertFalse(d < 0);
            }
        }
    }

    @Test
    public void testClusteringStats() {
        double totalAverageInternalDistance = 0;
        double totalAverageHomogeneity = 0;
        int clNo = clusters.size();
        for (Cluster<OWLEntity> cluster : clusters) {
            ClusterStatistics<OWLEntity> stats = ClusterStatistics.buildStatistics(cluster);
            assertFalse(stats.getAverageExternalDistance() < 0);
            assertFalse(stats.getAverageInternalDistance() < 0);
            totalAverageInternalDistance += stats.getAverageInternalDistance();
            double homogeneity = 1 - stats.getAverageInternalDistance();
            assertFalse(totalAverageInternalDistance < 0);
            System.out.println("Cluster homogeneity " + homogeneity);
            assertFalse(homogeneity < 0);
            assertFalse(stats.getMaxExternalDistance() < 0);
            assertFalse(stats.getMaxInternalDistance() < 0);
            assertFalse(stats.getMinExternalDistance() < 0);
            assertFalse(stats.getMinInternalDistance() < 0);
        }
        double avgInternalDistanceFinal = totalAverageInternalDistance / clNo;
        System.out.println("ClusterCreatorTest.testClusteringStats() totalAverageInternalDistance "
            + avgInternalDistanceFinal);
        assertFalse(avgInternalDistanceFinal < 0);
        totalAverageHomogeneity = 1 - avgInternalDistanceFinal;
        assertFalse(totalAverageHomogeneity < 0);
        System.out.println(
            "ClusterCreatorTest.testClusteringStats() Homogeneity " + totalAverageHomogeneity);
    }
}
