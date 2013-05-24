package org.coode.proximitymatrix.cluster.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.basetest.ClusterCreator;
import org.coode.basetest.ClusteringHelper;
import org.coode.basetest.DistanceCreator;
import org.coode.basetest.OntologyTestHelper;
import org.coode.basetest.TestHelper;
import org.coode.distance.Distance;
import org.coode.oppl.Variable;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisationStatistics;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class GeneralisationStatisticsTest {

	public GeneralisationStatisticsTest() {
		// TODO Auto-generated constructor stub
	}

	public static String amino_iri = "similarity/experiment-ontologies/amino-acid-original.owl";

	@Test
	public void testAminoClusterCoverage() throws OWLOntologyCreationException,
			OPPLException, ParserConfigurationException {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o = TestHelper.loadFileMappers(new File(amino_iri), m);
		ClusterCreator clusterer = new ClusterCreator();
		Distance<OWLEntity> distance = DistanceCreator
				.createAxiomRelevanceAxiomBasedDistance(m);
		Set<Cluster<OWLEntity>> agglomerateAll = clusterer.agglomerateAll(o,
				distance, o.getSignature());
		ClusterDecompositionModel<OWLEntity> model = clusterer
				.buildClusterDecompositionModel(o, m, agglomerateAll);
		GeneralisationStatistics<Cluster<OWLEntity>, OWLEntity> stats = GeneralisationStatistics
				.buildStatistics(model);
		double meanClusterCoveragePerGeneralisation = stats
				.getMeanClusterCoveragePerGeneralisation();
		assertTrue(meanClusterCoveragePerGeneralisation < 1);
		System.out
				.println("GeneralisationStatisticsTest.testClusterCoverage() "
						+ meanClusterCoveragePerGeneralisation);
	}

	@Test
	public void testSmallOntologyClusterCoverage() throws Exception {
		OWLOntology o = OntologyTestHelper.getSmallTestOntology();
		for (OWLAxiom a : o.getAxioms()) {
			System.out
					.println("GeneralisationStatisticsTest.testSmallOntologyClusterCoverage() axiom: "
							+ a);
		}
		ClusterDecompositionModel<OWLEntity> model = ClusteringHelper
				.getSyntacticPopularityClusterModel(o);
		List<Cluster<OWLEntity>> clusterList = model.getClusterList();
		for (Cluster<OWLEntity> c : clusterList) {
			Variable<?> var = model.getVariableRepresentative(c);
			if (var != null) {
				System.out
						.println("GeneralisationStatisticsTest.testSmallOntologyClusterCoverage() Cluster "
								+ var.getName() + " \t size " + c.size());
				System.out
						.println("GeneralisationStatisticsTest.testSmallOntologyClusterCoverage() "
								+ c);
			}
		}
		GeneralisationStatistics<Cluster<OWLEntity>, OWLEntity> stats = GeneralisationStatistics
				.buildStatistics(model);
		double meanClusterCoveragePerGeneralisation = stats
				.getMeanClusterCoveragePerGeneralisation();
		assertTrue(meanClusterCoveragePerGeneralisation < 1);
		assertEquals(0.875, meanClusterCoveragePerGeneralisation, 0.001);
		System.out
				.println("GeneralisationStatisticsTest.testClusterCoverage() "
						+ meanClusterCoveragePerGeneralisation);
	}
}
