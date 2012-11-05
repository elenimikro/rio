package org.coode.knowledgeexplorer.distance.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerChainsawJFactImpl;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillerJFactImpl;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
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

import uk.ac.manchester.cs.jfact.JFactReasoner;

public class ChainsawKnowledgeExplorerTest {

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
		OWLObjectProperty p1 = f
				.getOWLObjectProperty(IRI.create("urn:test#p1"));

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
	public void chainsawKnowledgeExplorerTest() {
		// compare results of chainsaw with normal JFact...
		assertEquals(5, o.getAxiomCount());
		JFactReasoner reasoner = new JFactReasoner(o,
				new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
		reasoner.precomputeInferences();
		KnowledgeExplorer ke = new KnowledgeExplorerMaxFillerJFactImpl(reasoner);
		Set<OWLEntity> set = ke.getEntities();
		assertNotNull(set);

		KnowledgeExplorer chainke = new KnowledgeExplorerChainsawJFactImpl(
				reasoner);
		assertEquals(ke.getAxioms().size(), chainke.getAxioms().size());
	}

}
