package org.coode.basetest;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class TestHelper {
    public static OWLOntology getPizza() {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o;
        try {
            o = m.loadOntologyFromOntologyDocument(TestHelper.class.getClassLoader()
                    .getResourceAsStream("org/coode/basetest/pizza.owl"));
            return o;
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(final String[] args) {
        System.out.println(getPizza());
    }
}
