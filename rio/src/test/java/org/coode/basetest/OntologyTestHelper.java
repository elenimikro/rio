package org.coode.basetest;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/** @author eleni */
public class OntologyTestHelper {
    /** @return ontology */
    public static OWLOntology getSmallTestOntology() {
        try {
            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            OWLOntology o = m.createOntology();
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
            return o;
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }

    /** @return ontology */
    public static OWLOntology getSmallMeaningfullTestOntology() {
        try {
            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            OWLOntology o = m.createOntology();
            OWLDataFactory factory = m.getOWLDataFactory();
            OWLClass a = factory.getOWLClass(IRI.create("urn:test#Freestyle_swimming"));
            OWLClass b = factory.getOWLClass(IRI.create("urn:test#Swimming"));
            OWLClass c = factory.getOWLClass(IRI.create("urn:test#Sport"));
            OWLClass d = factory.getOWLClass(IRI.create("urn:test#Breaststroke_swimming"));
            OWLClass e = factory.getOWLClass(IRI.create("urn:test#Cycling"));
            OWLClass f = factory.getOWLClass(IRI.create("urn:test#Watercolor_painting"));
            OWLClass g = factory.getOWLClass(IRI.create("urn:test#Painting"));
            OWLClass i = factory.getOWLClass(IRI.create("urn:test#Hobby"));
            OWLClass j = factory.getOWLClass(IRI.create("urn:test#Oil_painting"));
            OWLClass k = factory.getOWLClass(IRI.create("urn:test#Sightseeing"));
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
            return o;
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }
}
