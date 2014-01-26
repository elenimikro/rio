package org.coode.owl.atomicdecomposition.distance.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.coode.basetest.TestHelper;
import org.coode.distance.Distance;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecomposition;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecompositionMetrics;
import org.coode.utils.owl.ClusterCreator;
import org.coode.utils.owl.DistanceCreator;
import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.atomicdecomposition.Atom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposerOWLAPITOOLS;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposition;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

/** @author eleni */
@SuppressWarnings("javadoc")
public class AtomiDecompositionGeneralisationEvaluatorPizzaTest {
    private ClusterDecompositionModel<OWLEntity> model;
    private AtomicDecomposerOWLAPITOOLS ad;
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
        Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
            @Override
            public int compare(final OWLEntity o1, final OWLEntity o2) {
                return shortFormProvider.getShortForm(o1).compareTo(
                        shortFormProvider.getShortForm(o2));
            }
        });
        for (OWLOntology ontology : manager.getOntologies()) {
            entities.addAll(ontology.getSignature());
        }
        Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(distance, entities);
        model = clusterer.buildClusterDecompositionModel(pizza, clusters);
    }

    @Test
    public void testPizzaAtomiDecompositionGeneralisationEvaluator() {
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
        AtomicDecomposition gen_ad = new AtomicDecomposerOWLAPITOOLS(axiomlist,
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
        Map<Collection<OWLAxiom>, Atom> atomMap = evaluator.getGeneralisationAtomMap();
        assertNotNull(atomMap);
    }

    @Test
    public void pizzaGeneralisedADTest() {
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
        ToStringRenderer.getInstance().setRenderer(new ManchesterSyntaxRenderer());
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
            assertTrue(mergedAtoms.get(col).size() > 1);
        }
        Set<List<Atom>> patterns = gad.getPatterns();
        assertTrue(patterns.size() > 0);
        System.out
                .println("AtomiDecompositionGeneralisationEvaluatorPizzaTest.pizzaGeneralisedADTest() No of Patterns "
                        + patterns.size());
        System.out.println("\n\n\n");
        int count = 1;
        for (List<Atom> pattern : patterns) {
            System.out
                    .println("==========================================================");
            System.out.println("Pattern " + count + ":");
            System.out.println("Number of atoms: " + pattern.size());
            // Set<OWLAxiom> generalisations = new HashSet<OWLAxiom>();
            for (int i = 0; i < pattern.size(); i++) {
                System.out.println("---------------------------");
                System.out.println("\tAtom " + i + ":");
                Collection<OWLAxiom> axioms = gad.getAxioms(pattern.get(i));
                for (OWLAxiom axiom : axioms) {
                    System.out.println("\t\t" + axiom);
                }
            }
            count++;
        }
    }

    @Test
    public void testGeneralisedAtomicDecompositionStats() {
        GeneralisedAtomicDecomposition<OWLEntity> gad = new GeneralisedAtomicDecomposition<OWLEntity>(
                model, pizza);
        GeneralisedAtomicDecompositionMetrics gadstats = GeneralisedAtomicDecompositionMetrics
                .buildMetrics(gad);
        assertEquals(0.82, gadstats.getAtomicDecompositionCompression(), 0.1);
        System.out
                .println("GeneralisedAtomicDecompositionTest.testGeneralisedAtomicDecompositionStats() MeanMergedAxiomsPerGeneralisation: "
                        + gadstats.getMeanMergedAxiomsPerGeneralisation());
        System.out
                .println("GeneralisedAtomicDecompositionTest.testGeneralisedAtomicDecompositionStats() RatioOfMergedGeneralisations: "
                        + gadstats.getRatioOfMergedGeneralisations());
    }
}
