package org.coode.knowledgeexplorer.distance.test;

import static org.junit.Assert.*;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.coode.distance.Distance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillersImpl;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.utils.owl.ClusterCreator;
import org.coode.utils.owl.DistanceCreator;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasoner;
import uk.ac.manchester.cs.factplusplus.owlapiv3.OWLKnowledgeExplorationReasonerWrapper;
import uk.ac.manchester.cs.jfact.JFactReasoner;

/** @author eleni */
@SuppressWarnings("javadoc")
public class KnowledgeExplorerDistanceTest {
    private OWLOntology o;
    private OWLOntologyManager m;

    @Before
    public void setUp() throws OWLOntologyCreationException {
        m = OWLManager.createOWLOntologyManager();
        o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLClass c1 = f.getOWLClass(IRI.create("urn:test#C1"));
        OWLClass c2 = f.getOWLClass(IRI.create("urn:test#C2"));
        OWLClass c3 = f.getOWLClass(IRI.create("urn:test#C3"));
        OWLObjectProperty p1 = f.getOWLObjectProperty(IRI.create("urn:test#p1"));
        OWLClass c4 = f.getOWLClass(IRI.create("urn:test#C4"));
        OWLClass c5 = f.getOWLClass(IRI.create("urn:test#C5"));
        OWLClass c6 = f.getOWLClass(IRI.create("urn:test#C6"));
        OWLClass c7 = f.getOWLClass(IRI.create("urn:test#C7"));
        OWLClass c8 = f.getOWLClass(IRI.create("urn:test#C8"));
        OWLClass c9 = f.getOWLClass(IRI.create("urn:test#C9"));
        OWLClass c10 = f.getOWLClass(IRI.create("urn:test#C10"));
        OWLClass c11 = f.getOWLClass(IRI.create("urn:test#C11"));
        OWLClass c12 = f.getOWLClass(IRI.create("urn:test#C12"));
        OWLClassExpression p1c3 = f.getOWLObjectSomeValuesFrom(p1, c3);
        OWLEquivalentClassesAxiom c1equiv = f.getOWLEquivalentClassesAxiom(c1,
                f.getOWLObjectIntersectionOf(c2, p1c3));
        OWLClassExpression p1c6 = f.getOWLObjectSomeValuesFrom(p1, c6);
        OWLEquivalentClassesAxiom c4equiv = f.getOWLEquivalentClassesAxiom(c4,
                f.getOWLObjectIntersectionOf(c5, p1c6));
        OWLSubClassOfAxiom c7Subc8 = f.getOWLSubClassOfAxiom(c7, c8);
        OWLSubClassOfAxiom c8Subc9 = f.getOWLSubClassOfAxiom(c8, c9);
        OWLSubClassOfAxiom c10Subc11 = f.getOWLSubClassOfAxiom(c10, c11);
        OWLSubClassOfAxiom c11Subc9 = f.getOWLSubClassOfAxiom(c11, c9);
        OWLSubClassOfAxiom c12Subc11 = f.getOWLSubClassOfAxiom(c12, c11);
        // m.addAxiom(o, c1equiv);
        // m.addAxiom(o, c4equiv);
        m.addAxiom(o, c7Subc8);
        m.addAxiom(o, c8Subc9);
        m.addAxiom(o, c10Subc11);
        m.addAxiom(o, c11Subc9);
        m.addAxiom(o, c12Subc11);
    }

    @Test
    public void knowledgeExplorerTest() {
        assertEquals(5, o.getAxiomCount());
        System.out
                .println("KnowledgeExplorerDistanceTest.knowledgeExplorerTest() Axioms "
                        + o.getAxioms());
        JFactReasoner reasoner = new JFactReasoner(o, new SimpleConfiguration(),
                BufferingMode.NON_BUFFERING);
        reasoner.precomputeInferences();
        KnowledgeExplorer ke = new KnowledgeExplorerMaxFillersImpl(reasoner,
                new OWLKnowledgeExplorationReasonerWrapper(new FaCTPlusPlusReasoner(o,
                        new SimpleConfiguration(), BufferingMode.NON_BUFFERING)));
        Set<OWLEntity> set = ke.getEntities();
        assertNotNull(set);
    }

    @Test
    public void knowledgeExplorerDistanceTest() throws OPPLException {
        // assertEquals(7, o.getAxiomCount());
        JFactReasoner reasoner = new JFactReasoner(o, new SimpleConfiguration(),
                BufferingMode.NON_BUFFERING);
        reasoner.precomputeInferences();
        KnowledgeExplorer ke = new KnowledgeExplorerMaxFillersImpl(reasoner,
                new OWLKnowledgeExplorationReasonerWrapper(new FaCTPlusPlusReasoner(o,
                        new SimpleConfiguration(), BufferingMode.NON_BUFFERING)));
        Distance<OWLEntity> distance = DistanceCreator
                .createKnowledgeExplorerOWLEntityRelevanceBasedDistance(o, ke);
        final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
            @Override
            public int compare(OWLEntity o1, OWLEntity o2) {
                return shortFormProvider.getShortForm(o1).compareTo(
                        shortFormProvider.getShortForm(o2));
            }
        });
        Set<OWLEntity> set = ke.getEntities();
        assertNotNull(set);
        for (OWLEntity e : set) {
            if (!e.isType(EntityType.OBJECT_PROPERTY)) {
                entities.add(e);
            }
        }
        ClusterCreator clusterer = new ClusterCreator();
        Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(distance, entities);
        ClusterDecompositionModel<OWLEntity> model = clusterer
                .buildClusterDecompositionModel(o, clusters);
        assertNotNull(model);
    }
}
