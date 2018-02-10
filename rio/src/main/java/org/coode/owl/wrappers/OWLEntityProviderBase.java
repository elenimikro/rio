package org.coode.owl.wrappers;

import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
public abstract class OWLEntityProviderBase extends OWLEntityProvider {
    private final OWLOntologyManager ontologyManager;

    /**
     * @param ontologyManager ontologyManager
     */
    public OWLEntityProviderBase(OWLOntologyManager ontologyManager) {
        if (ontologyManager == null) {
            throw new NullPointerException("The ontology manager cannot be null");
        }
        this.ontologyManager = ontologyManager;
    }

    /** @return the ontologyManager */
    public OWLOntologyManager getOntologyManager() {
        return ontologyManager;
    }
}
