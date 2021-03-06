package org.coode.utils.owl;

import java.io.File;
import java.net.URI;
import java.util.Collection;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AutoIRIMapper;

/** @author Eleni Mikroyannidi */
public class IOUtils {
    /**
     * @param iris iris
     * @param manager manager
     * @throws OWLOntologyCreationException OWLOntologyCreationException
     */
    public static void loadIRIMappers(Collection<IRI> iris, OWLOntologyManager manager)
        throws OWLOntologyCreationException {
        for (IRI iri : iris) {
            URI uri = iri.toURI();
            if (uri.getScheme().startsWith("file") && uri.isAbsolute()) {
                File file = new File(uri);
                File parentFile = file.getParentFile();
                if (parentFile.isDirectory()) {
                    manager.getIRIMappers().add(new AutoIRIMapper(parentFile, true));
                }
            }
            manager.loadOntology(iri);
        }
    }
}
