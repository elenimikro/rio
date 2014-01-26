package org.coode.owl.distance.test;

import static org.junit.Assert.*;

import org.coode.distance.owl.StructuralAxiomRelevanceAxiomBasedDistance;
import org.coode.utils.owl.DistanceCreator;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/** @author eleni */
@SuppressWarnings("javadoc")
public class StructuralDistanceCharacterisationTest {
    private OWLDataFactory df;
    private OWLOntology o;

    @Before
    public void setUp() {
        try {
            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            o = m.createOntology();
            df = m.getOWLDataFactory();
            OWLClass a = createOWLClass("a");
            OWLClass b = createOWLClass("b");
            OWLClass c = createOWLClass("c");
            OWLClass d = createOWLClass("d");
            OWLClass a1 = createOWLClass("a1");
            OWLClass a2 = createOWLClass("a2");
            OWLClass c1 = createOWLClass("c1");
            OWLClass c2 = createOWLClass("c2");
            OWLClass d1 = createOWLClass("d1");
            OWLObjectProperty p1 = createOWLObjectProperty("p1");
            OWLObjectProperty p2 = createOWLObjectProperty("p2");
            OWLObjectSomeValuesFrom p1_some_a1 = df.getOWLObjectSomeValuesFrom(p1, a1);
            OWLObjectSomeValuesFrom p2_some_a2 = df.getOWLObjectSomeValuesFrom(p2, a2);
            OWLObjectSomeValuesFrom p1_some_a2 = df.getOWLObjectSomeValuesFrom(p1, a2);
            OWLObjectSomeValuesFrom p2_some_a1 = df.getOWLObjectSomeValuesFrom(p2, a1);
            OWLObjectSomeValuesFrom p1_some_c1 = df.getOWLObjectSomeValuesFrom(p1, c1);
            OWLObjectSomeValuesFrom p2_some_c2 = df.getOWLObjectSomeValuesFrom(p2, c2);
            OWLObjectSomeValuesFrom p1_some_d1 = df.getOWLObjectSomeValuesFrom(p1, d1);
            OWLSubClassOfAxiom aSub = createOWLSubClassOfAxiom(a,
                    df.getOWLObjectIntersectionOf(p1_some_a1, p2_some_a2));
            OWLSubClassOfAxiom bSub = createOWLSubClassOfAxiom(b,
                    df.getOWLObjectIntersectionOf(p1_some_a2, p2_some_a1));
            OWLSubClassOfAxiom cSub = createOWLSubClassOfAxiom(c,
                    df.getOWLObjectIntersectionOf(p1_some_c1, p2_some_c2));
            OWLSubClassOfAxiom dSub = createOWLSubClassOfAxiom(d,
                    df.getOWLObjectIntersectionOf(p1_some_d1, p1_some_c1, p2_some_c2, a1,
                            a2));
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }

    public OWLClass createOWLClass(String className) {
        OWLClass c = df.getOWLClass(IRI.create("urn:test#" + className));
        OWLDeclarationAxiom owlDeclarationAxiom = df.getOWLDeclarationAxiom(c);
        addAxiom(owlDeclarationAxiom);
        return c;
    }

    private void addAxiom(OWLAxiom axiom) {
        o.getOWLOntologyManager().addAxiom(o, axiom);
    }

    public OWLObjectProperty createOWLObjectProperty(String propertyName) {
        OWLObjectProperty owlObjectProperty = df.getOWLObjectProperty(IRI
                .create("urn:test#" + propertyName));
        OWLDeclarationAxiom axiom = df.getOWLDeclarationAxiom(owlObjectProperty);
        addAxiom(axiom);
        return owlObjectProperty;
    }

    public OWLSubClassOfAxiom createOWLSubClassOfAxiom(OWLClassExpression subClass,
            OWLClassExpression superClass) {
        OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(subClass, superClass);
        addAxiom(axiom);
        return axiom;
    }

    @Test
    public void testStructuralDistance() {
        StructuralAxiomRelevanceAxiomBasedDistance distance = (StructuralAxiomRelevanceAxiomBasedDistance) DistanceCreator
                .createStructuralAxiomRelevanceAxiomBasedDistance(o
                        .getOWLOntologyManager());
        OWLClass a = df.getOWLClass(IRI.create("urn:test#a"));
        OWLClass b = df.getOWLClass(IRI.create("urn:test#b"));
        OWLClass c = df.getOWLClass(IRI.create("urn:test#c"));
        OWLClass d = df.getOWLClass(IRI.create("urn:test#d"));
        System.out.println("Axioms of " + a + " " + distance.getAxioms(a));
        System.out.println("Axioms of " + d + " " + distance.getAxioms(d));
        assertTrue(distance.getDistance(a, b) == 0);
        assertTrue(distance.getDistance(a, c) == 0);
        assertTrue(distance.getDistance(b, c) == 0);
        assertFalse(distance.getDistance(a, d) == 0);
    }
}
