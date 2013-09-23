package org.coode.utils.owl;

import java.io.File;
import java.net.URI;
import java.util.Collection;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AutoIRIMapper;

public class IOUtils {

	public static void loadIRIMappers(final Collection<IRI> iris,
			final OWLOntologyManager manager)
			throws OWLOntologyCreationException {
		for (IRI iri : iris) {
			URI uri = iri.toURI();
			System.out.println(uri);
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

}
