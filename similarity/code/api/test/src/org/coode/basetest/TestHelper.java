package org.coode.basetest;

import java.io.File;
import java.net.URI;
import java.util.Collection;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AutoIRIMapper;

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

    public static void loadIRIMappers(final Collection<IRI> iris,
            final OWLOntologyManager manager) throws OWLOntologyCreationException {
        for (IRI iri : iris) {
            URI uri = iri.toURI();
            if (uri.getScheme().startsWith("file") && uri.isAbsolute()) {
                File file = new File(uri);
                File parentFile = file.getParentFile();
                if (parentFile.isDirectory()) {
                    manager.addIRIMapper(new AutoIRIMapper(parentFile, true));
                }
            }
            manager.loadOntology(iri);
        }
    }
    
	public static OWLOntology loadIRIMappers(final IRI iri,
			final OWLOntologyManager manager)
			throws OWLOntologyCreationException {
		URI uri = iri.toURI();
		if (uri.getScheme().startsWith("file") && uri.isAbsolute()) {
			File file = new File(uri);
			File parentFile = file.getParentFile();
			if (parentFile.isDirectory()) {
				manager.addIRIMapper(new AutoIRIMapper(parentFile, true));
			}
		}
		return manager.loadOntology(iri);
	}
    
}
