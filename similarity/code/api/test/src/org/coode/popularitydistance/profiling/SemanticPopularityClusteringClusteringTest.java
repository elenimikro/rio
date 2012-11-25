package org.coode.popularitydistance.profiling;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.basetest.ClusterCreator;
import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillersFactplusplusImpl;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
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
import experiments.SimpleMetric;

public class SemanticPopularityClusteringClusteringTest {

	public SemanticPopularityClusteringClusteringTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws ParserConfigurationException
	 * @throws OPPLException
	 * @throws FileNotFoundException
	 * @throws OWLOntologyCreationException
	 */
	// public static void main(String[] args) throws
	// OWLOntologyCreationException,
	// FileNotFoundException, OPPLException, ParserConfigurationException {
	// testSemanticPopularityClusteringStats();
	//
	// }

	@Test
	public void testSemanticPopularityClusteringStats() throws OPPLException,
			ParserConfigurationException, OWLOntologyCreationException,
			FileNotFoundException {
		String ontology_iri = "bioportal/minimal-anatomical-terminology/minimal-anatomical-terminology_main.owl";
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o = m.loadOntologyFromOntologyDocument(new File(
				ontology_iri));
		PrintStream out = new PrintStream(new File(
				"profiling_ontology_cluster_stats.csv"));
		JFactReasoner reasoner = new JFactReasoner(o,
				new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
		reasoner.precomputeInferences();
		KnowledgeExplorer ke = new KnowledgeExplorerMaxFillersFactplusplusImpl(
				reasoner);

		Distance<OWLEntity> distance = DistanceCreator
				.createStructuralKnowledgeExplorerAxiomRelevanceBasedDistance(
						o, ke);
		final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		Set<OWLEntity> entities = new TreeSet<OWLEntity>(
				new Comparator<OWLEntity>() {
					@Override
					public int compare(final OWLEntity o1, final OWLEntity o2) {
						return shortFormProvider.getShortForm(o1).compareTo(
								shortFormProvider.getShortForm(o2));
					}
				});

		entities.addAll(ke.getEntities());
		ClusterCreator clusterer = new ClusterCreator();
		Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(o,
				distance, entities);
		ClusterDecompositionModel<OWLEntity> model = clusterer
				.buildKnowledgeExplorerClusterDecompositionModel(o,
						ke.getAxioms(), m, clusters);

		Collection<? extends SimpleMetric<?>> stats = ClusteringWithADEvaluationExperimentBase
				.getClusteringStats(out, model.getClusterList());

		for (SimpleMetric<?> sm : stats) {
			System.out.println(sm.getName() + " : " + sm.getValue());
			assertFalse((Double) sm.getValue() < 0);
		}
	}
}
