package org.coode.utils;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.concurrent.Concurrency;

public class OntologyManagerUtils {
    private static Object injector = OWLManager.createInjector(Concurrency.NON_CONCURRENT);

    public static OWLOntologyManager ontologyManager() {
        return OWLManager.createOWLOntologyManager(injector);
    }

    public static OWLDataFactory dataFactory() {
        return OWLManager.getOWLDataFactory(injector);
    }
}
