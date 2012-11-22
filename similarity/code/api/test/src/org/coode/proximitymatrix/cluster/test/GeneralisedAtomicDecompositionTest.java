package org.coode.proximitymatrix.cluster.test;

import static junit.framework.Assert.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.coode.basetest.ClusterCreator;
import org.coode.basetest.DistanceCreator;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecomposition;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecompositionMetrics;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.atomicdecomposition.Atom;

public class GeneralisedAtomicDecompositionTest {

	private ClusterDecompositionModel<OWLEntity> model;
    // private AtomicDecomposer ad;
	private OWLOntology o;

	@Before
	public ClusterDecompositionModel<OWLEntity> setUp() throws Exception {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		o = m.createOntology();
		OWLDataFactory factory = m.getOWLDataFactory();
		OWLClass a = factory.getOWLClass(IRI.create("urn:test#A"));
		OWLClass b = factory.getOWLClass(IRI.create("urn:test#B"));
		OWLClass c = factory.getOWLClass(IRI.create("urn:test#C"));

		OWLClass d = factory.getOWLClass(IRI.create("urn:test#D"));
		OWLClass e = factory.getOWLClass(IRI.create("urn:test#E"));
		OWLClass f = factory.getOWLClass(IRI.create("urn:test#F"));
		OWLClass g = factory.getOWLClass(IRI.create("urn:test#G"));
		OWLClass i = factory.getOWLClass(IRI.create("urn:test#I"));
		OWLClass j = factory.getOWLClass(IRI.create("urn:test#J"));
		OWLClass k = factory.getOWLClass(IRI.create("urn:test#K"));

		OWLSubClassOfAxiom ab = factory.getOWLSubClassOfAxiom(a, b);
		OWLSubClassOfAxiom bc = factory.getOWLSubClassOfAxiom(b, c);
		OWLSubClassOfAxiom db = factory.getOWLSubClassOfAxiom(d, b);
		OWLSubClassOfAxiom ec = factory.getOWLSubClassOfAxiom(e, c);

		OWLSubClassOfAxiom fg = factory.getOWLSubClassOfAxiom(f, g);
		OWLSubClassOfAxiom gi = factory.getOWLSubClassOfAxiom(g, i);
		OWLSubClassOfAxiom jg = factory.getOWLSubClassOfAxiom(j, g);
		OWLSubClassOfAxiom ki = factory.getOWLSubClassOfAxiom(k, i);

		m.addAxiom(o, ab);
		m.addAxiom(o, bc);
		m.addAxiom(o, db);
		m.addAxiom(o, ec);
		m.addAxiom(o, fg);
		m.addAxiom(o, gi);
		m.addAxiom(o, jg);
		m.addAxiom(o, ki);

        // ad = new AtomicDecomposerOWLAPITOOLS(o);

		assertEquals(10, o.getSignature().size());

		AxiomRelevanceAxiomBasedDistance distance = (AxiomRelevanceAxiomBasedDistance) DistanceCreator
				.createAxiomRelevanceAxiomBasedDistance(m);
		System.out
				.println("GeneralisationAtomicDecompositionTest.testGeneralisationAtomicDecomposition() "
						+ distance.getDistance(a, d));
		System.out
				.println("GeneralisationAtomicDecompositionTest.testGeneralisationAtomicDecomposition()"
						+ distance.getAxioms(a));
		ClusterCreator clusterer = new ClusterCreator();

		final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		Set<OWLEntity> entities = new TreeSet<OWLEntity>(
				new Comparator<OWLEntity>() {
					@Override
					public int compare(final OWLEntity o1, final OWLEntity o2) {
						return shortFormProvider.getShortForm(o1).compareTo(
								shortFormProvider.getShortForm(o2));
					}
				});
		for (OWLOntology ontology : m.getOntologies()) {
			entities.addAll(ontology.getSignature());
		}
		Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(o,
				distance, entities);
		model = clusterer.buildClusterDecompositionModel(o, m, clusters);

		List<Cluster<OWLEntity>> clusterList = model.getClusterList();
		for (int counter = 0; counter < clusterList.size(); counter++) {
			MultiMap<OWLAxiom, OWLAxiomInstantiation> multiMap = model
					.get(clusterList.get(counter));
			for (OWLAxiom ax : multiMap.keySet()) {
				System.out.println("Generalisation: " + ax);
				System.out.println("Instantiations:");
				for (OWLAxiomInstantiation inst : multiMap.get(ax)) {
					System.out.println("\t" + inst);
				}
			}
		}
		return model;
	}

	@Test
	public void testGeneralisationAtomicDecomposition()
 {
		GeneralisedAtomicDecomposition<OWLEntity> gad = new GeneralisedAtomicDecomposition<OWLEntity>(
				model, o);

		assertNotNull(gad.getAtoms());
		assertEquals(4, gad.getAtoms().size());

		assertEquals(8, gad.getDirtyMap().keySet().size());
		assertEquals(4, gad.getMergedAtoms().keySet().size());
		MultiMap<Collection<OWLAxiom>, Atom> mergedAtoms = gad.getMergedAtoms();
		// assertEquals(ad.getAtoms().size() - gad.getAtoms().size(),
		// mergedAtoms.keySet().size());
		for (Collection<OWLAxiom> col : mergedAtoms.keySet()) {
			System.out
					.println("GeneralisationAtomicDecompositionTest.testGeneralisationAtomicDecomposition() Duplicate axioms ");
			for (OWLAxiom ax : col) {
				System.out
						.println("GeneralisationAtomicDecompositionTest.testGeneralisationAtomicDecomposition() \t"
								+ ax);
			}
			System.out
					.println("GeneralisationAtomicDecompositionTest.testGeneralisationAtomicDecomposition() Duplicate Atoms "
							+ mergedAtoms.get(col));
		}
	}

	@Test
	public void testGeneralisedAtomicDecompositionStats() {
		GeneralisedAtomicDecomposition<OWLEntity> gad = new GeneralisedAtomicDecomposition<OWLEntity>(
				model, o);
		GeneralisedAtomicDecompositionMetrics gadstats = GeneralisedAtomicDecompositionMetrics
				.buildMetrics(gad);
		assertEquals(0.5, gadstats.getAtomicDecompositionCompression());
		System.out
				.println("GeneralisedAtomicDecompositionTest.testGeneralisedAtomicDecompositionStats() MeanMergedAxiomsPerGeneralisation: "
						+ gadstats.getMeanMergedAxiomsPerGeneralisation());
		System.out
				.println("GeneralisedAtomicDecompositionTest.testGeneralisedAtomicDecompositionStats() RatioOfMergedGeneralisations: "
						+ gadstats.getRatioOfMergedGeneralisations());
	}

}