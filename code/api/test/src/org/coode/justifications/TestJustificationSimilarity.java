package org.coode.justifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.coode.basetest.ClusteringHelper;
import org.coode.basetest.OntologyTestHelper;
import org.coode.basetest.TestHelper;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.junit.Test;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory;

public class TestJustificationSimilarity {

	@Test
	public void SmallOntologyJustificationTest() throws Exception {
		OWLOntology o = OntologyTestHelper.getSmallMeaningfullTestOntology();
		ClusterDecompositionModel<OWLEntity> model = ClusteringHelper
				.getSemanticPopularityClusterModel(o);
		List<Cluster<OWLEntity>> clusterList = model.getClusterList();
		for (int i = 0; i < clusterList.size(); i++) {
			MultiMap<OWLAxiom, OWLAxiomInstantiation> multiMap = model
					.get(clusterList.get(i));
			Set<OWLAxiom> entailments = Utils.extractAxioms(multiMap
					.getAllValues());
			JustificationSimilarity just = new JustificationSimilarity(
					entailments, new FaCTPlusPlusReasonerFactory(), o);
			Set<Set<OWLAxiom>> isomorphicJustifications = just
					.getIsomorphicJustifications();
			assertTrue(isomorphicJustifications.size() > 0);
			System.out.println("Isomporphic justifications percentage: "
					+ just.getJustificationSimilarity());
			for (Set<OWLAxiom> j : isomorphicJustifications) {
				for (OWLAxiom ax : j) {
					System.out.println(ax);
				}
				System.out.println();
			}
		}
	}

	@Test
	public void PizzaSliceJustificationTest() throws Exception {
		ToStringRenderer.getInstance().setRenderer(
				new ManchesterSyntaxRenderer());
		OWLOntology o = TestHelper.getPizza();
		ClusterDecompositionModel<OWLEntity> model = ClusteringHelper
				.getSemanticPopularityClusterModel(o);
		List<Cluster<OWLEntity>> clusterList = model.getClusterList();
		System.out.println("Number of clusters: " + clusterList.size());
		MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = Utils
				.extractGeneralisationMap(model);
		System.out.println("Number of generalisations: "
				+ generalisationMap.keySet().size());
		OWLAxiom generalisation = generalisationMap.keySet().iterator().next();

		System.out.println("Generalisation " + generalisation);
		Set<OWLAxiom> entailments = Utils.extractAxioms(generalisationMap
				.get(generalisation));
		System.out.println("Entailments: " + entailments.size() + "\t"
				+ entailments);
		JustificationSimilarity just = new JustificationSimilarity(entailments,
				new FaCTPlusPlusReasonerFactory(), o);
		Set<Set<OWLAxiom>> isomorphicJustifications = just
				.getIsomorphicJustifications();
		assertTrue(isomorphicJustifications.size() > 0);
		System.out.println("Isomporphic justifications percentage: "
				+ just.getJustificationSimilarity());
		MultiMap<OWLAxiom, Explanation<OWLAxiom>> justificationMap = just
				.getJustificationMap();
		for (OWLAxiom entail : justificationMap.keySet()) {
			System.out.println("Entailment: " + entail.toString());
			for (Explanation<OWLAxiom> expl : justificationMap.get(entail)) {
				System.out.println("\t" + expl.toString());
			}
		}
		for (Set<OWLAxiom> j : isomorphicJustifications) {
			for (OWLAxiom ax : j) {
				System.out.println(ax);
			}
			System.out.println();
		}
		assertEquals(0.5, just.getJustificationSimilarity(), 0.01);
	}

	@Test
	public void PizzaJustificationTest() throws Exception {
		OWLOntology o = TestHelper.getPizza();
		ClusterDecompositionModel<OWLEntity> model = ClusteringHelper
				.getSemanticPopularityClusterModel(o);
		List<Cluster<OWLEntity>> clusterList = model.getClusterList();
		System.out.println("Number of clusters: " + clusterList.size());
		MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = Utils
				.extractGeneralisationMap(model);
		System.out.println("Number of generalisations: "
				+ generalisationMap.keySet().size());
		double sumJustSimilarity = 0;
		for (OWLAxiom ax : generalisationMap.keySet()) {
			System.out.println("Generalisation " + ax);
			Set<OWLAxiom> entailments = Utils.extractAxioms(generalisationMap
					.get(ax));
			System.out.println("Entailments: " + entailments.size() + "\t"
					+ entailments);
			JustificationSimilarity just = new JustificationSimilarity(
					entailments, new FaCTPlusPlusReasonerFactory(), o);
			Set<Set<OWLAxiom>> isomorphicJustifications = just
					.getIsomorphicJustifications();
			assertTrue(isomorphicJustifications.size() > 0);
			System.out.println("Isomporphic justifications percentage: "
					+ just.getJustificationSimilarity());
			assertFalse(just.getJustificationSimilarity() > 1);
			// for (ArrayList<OWLAxiom> j : isomorphicJustifications) {
			// for (int k = 0; k < isomorphicJustifications.size(); k++) {
			// System.out.println(j.get(k));
			// }
			// System.out.println();
			// }
			sumJustSimilarity += just.getJustificationSimilarity();
		}
		double totalMeanJustSimilarity = sumJustSimilarity
				/ generalisationMap.keySet().size();
		System.out
				.println("TestJustificationSimilarity.PizzaJustificationTest() totalMeanJustificationSimilarity "
						+ totalMeanJustSimilarity);
		assertFalse(totalMeanJustSimilarity > 1);
		GeneralisationBasedJustificationSimilarity sim = new GeneralisationBasedJustificationSimilarity(
				new FaCTPlusPlusReasonerFactory(), o, generalisationMap);
		assertEquals(sim.getTotalMeanJustificationSimilarity(),
				totalMeanJustSimilarity, 0.001);
	}
}
