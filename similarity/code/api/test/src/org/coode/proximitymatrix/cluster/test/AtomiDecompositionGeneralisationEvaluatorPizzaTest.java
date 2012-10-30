package org.coode.proximitymatrix.cluster.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.basetest.ClusterCreator;
import org.coode.basetest.DistanceCreator;
import org.coode.basetest.TestHelper;
import org.coode.distance.Distance;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecomposition;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecompositionMetrics;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.atomicdecomposition.Atom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposer;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposerOWLAPITOOLS;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

public class AtomiDecompositionGeneralisationEvaluatorPizzaTest {

	private ClusterDecompositionModel<OWLEntity> model;
	private AtomicDecomposer ad;
	private OWLOntology pizza;

	@Before
	public void setUp() throws Exception {
		pizza = TestHelper.getPizza();
		OWLOntologyManager manager = pizza.getOWLOntologyManager();
		ad = new AtomicDecomposerOWLAPITOOLS(pizza);

		Distance<OWLEntity> distance = DistanceCreator
				.createAxiomRelevanceAxiomBasedDistance(manager);
		ClusterCreator clusterer = new ClusterCreator();

		final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		Set<OWLEntity> entities = new TreeSet<OWLEntity>(
				new Comparator<OWLEntity>() {
					public int compare(final OWLEntity o1, final OWLEntity o2) {
						return shortFormProvider.getShortForm(o1).compareTo(
								shortFormProvider.getShortForm(o2));
					}
				});
		for (OWLOntology ontology : manager.getOntologies()) {
			entities.addAll(ontology.getSignature());
		}
		Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(pizza, distance, entities);
		model = clusterer.buildClusterDecompositionModel(pizza, manager, clusters);
	}

	@Test
	public void testPizzaAtomiDecompositionGeneralisationEvaluator()
			throws OPPLException, ParserConfigurationException {
		assertNotNull(model);
		MultiMap<OWLAxiom, OWLAxiomInstantiation> genmap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
		List<Cluster<OWLEntity>> clusterList = model.getClusterList();
		assertEquals(21, clusterList.size());

		for (int i = 0; i < clusterList.size(); i++) {
			genmap.putAll(model.get(clusterList.get(i)));
		}
		Set<OWLAxiom> keySet = genmap.keySet();
		assertNotNull(genmap);
		assertTrue(keySet.size() > 0);

		Set<OWLAxiom> logicalAxioms = new HashSet<OWLAxiom>();
		for (OWLAxiom ax : keySet) {
			if (ax.isLogicalAxiom()) {
				logicalAxioms.add(ax);
			}
		}
		assertNotNull(logicalAxioms);
		assertTrue(logicalAxioms.size() > 0);
		List<OWLAxiom> axiomlist = new ArrayList<OWLAxiom>(genmap.keySet());
		AtomicDecomposer gen_ad = new AtomicDecomposerOWLAPITOOLS(axiomlist,
				ModuleType.BOT);
		// assertTrue(gen_ad.getAtoms().size()>0);
		System.out
				.println("AtomiDecompositionGeneralisationEvaluatorTest.testPizzaAtomiDecompositionGeneralisationEvaluator() Initial AD size: "
						+ ad.getAtoms().size());
		System.out
				.println("AtomiDecompositionGeneralisationEvaluatorTest.testPizzaAtomiDecompositionGeneralisationEvaluator() Generalised AD size "
						+ gen_ad.getAtoms().size());
	}

	@Test
	public void getGeneralisationAtomMapTest() {
		GeneralisedAtomicDecomposition<OWLEntity> evaluator = new GeneralisedAtomicDecomposition<OWLEntity>(
				model, pizza);
		Map<Collection<OWLAxiom>, Atom> atomMap = evaluator
				.getGeneralisationAtomMap();
		assertNotNull(atomMap);

	}

	@Test
	public void pizzaGeneralisedADTest() throws OPPLException,
			ParserConfigurationException {
		GeneralisedAtomicDecomposition<OWLEntity> gad = new GeneralisedAtomicDecomposition<OWLEntity>(
				model, pizza);
		assertTrue(ad.getAtoms().size() > gad.getAtoms().size());
		for (Atom a : gad.getAtoms()) {
			assertNotNull(a);
		}
		System.out
				.println("AtomiDecompositionGeneralisationEvaluatorTest.getGeneralisationAtomMapTest() Initial AD size: "
						+ ad.getAtoms().size());
		System.out
				.println("AtomiDecompositionGeneralisationEvaluatorTest.getGeneralisationAtomMapTest() Generalised AD size "
						+ gad.getAtoms().size());
		MultiMap<Collection<OWLAxiom>, Atom> mergedAtoms = gad.getMergedAtoms();
		
		for (Collection<OWLAxiom> col : mergedAtoms.keySet()) {
			System.out
					.println("GeneralisationAtomicDecompositionTest.testGeneralisationAtomicDecomposition() Atom Pattern");
			for (OWLAxiom ax : col) {
				System.out
						.println("GeneralisationAtomicDecompositionTest.testGeneralisationAtomicDecomposition() \t"
								+ ax);
			}
			System.out
					.println("GeneralisationAtomicDecompositionTest.testGeneralisationAtomicDecomposition() Merged Atoms "
							+ mergedAtoms.get(col));
			assertTrue(mergedAtoms.get(col).size()>1);
		}
	}
	
	@Test
	public void testGeneralisedAtomicDecompositionStats(){
		GeneralisedAtomicDecomposition<OWLEntity> gad = new GeneralisedAtomicDecomposition<OWLEntity>(
				model, pizza);
		GeneralisedAtomicDecompositionMetrics gadstats = GeneralisedAtomicDecompositionMetrics.buildMetrics(gad);
		assertEquals(0.82, gadstats.getAtomicDecompositionCompression(), 0.1);
		System.out
				.println("GeneralisedAtomicDecompositionTest.testGeneralisedAtomicDecompositionStats() MeanMergedAxiomsPerGeneralisation: " 
		+ gadstats.getMeanMergedAxiomsPerGeneralisation());
		System.out
				.println("GeneralisedAtomicDecompositionTest.testGeneralisedAtomicDecompositionStats() RatioOfMergedGeneralisations: " +
		gadstats.getRatioOfMergedGeneralisations());
	}

}
