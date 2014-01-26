package org.coode.basetest;

import java.io.File;
import java.net.URI;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AutoIRIMapper;

/** @author eleni */
public class TestHelper {
    /** @return ontology */
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

    /** @param iri
     *            iri
     * @param manager
     *            manager
     * @return ontology
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException */
    public static OWLOntology loadIRIMappers(IRI iri, OWLOntologyManager manager)
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

    /** @param file
     *            file
     * @param manager
     *            manager
     * @return ontology
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException */
    public static OWLOntology loadFileMappers(File file, OWLOntologyManager manager)
            throws OWLOntologyCreationException {
        File parentFile = file.getParentFile();
        if (parentFile.isDirectory()) {
            manager.addIRIMapper(new AutoIRIMapper(parentFile, true));
        }
        return manager.loadOntologyFromOntologyDocument(file);
    }
}
