/*******************************************************************************
 * Copyright (c) 2012 Eleni Mikroyannidi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Eleni Mikroyannidi, Luigi Iannone - initial API and implementation
 ******************************************************************************/
package org.coode.owl.wrappers;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
public class OntologyManagerBasedOWLEntityProvider extends OWLEntityProviderBase
        implements OWLEntityProvider {
    private final OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
        @Override
        public void ontologiesChanged(final List<? extends OWLOntologyChange> changes)
                throws OWLException {
            clear();
            OntologyManagerBasedOWLEntityProvider.this.loadDelegate();
        }
    };

    /** @param ontologyManager
     *            ontologyManager */
    public OntologyManagerBasedOWLEntityProvider(final OWLOntologyManager ontologyManager) {
        super(ontologyManager);
        // getOntologyManager().addOntologyChangeListener(listener);
        loadDelegate();
    }

    protected void loadDelegate() {
        Set<OWLOntology> ontologies = getOntologyManager().getOntologies();
        for (OWLOntology ontology : ontologies) {
            addAll(ontology.getSignature());
        }
    }

    /** dispose */
    public void dispose() {
        getOntologyManager().removeOntologyChangeListener(listener);
    }
}
