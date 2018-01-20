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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
public class GeneralisationBasedOWLEntityProvider extends OWLEntityProviderBase
    implements OWLEntityProvider {
    private final Set<OWLAxiom> axioms = new HashSet<>();
    private final OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
        @Override
        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
            clear();
            GeneralisationBasedOWLEntityProvider.this.loadDelegate();
        }
    };

    /**
     * @param ontologyManager ontologyManager
     * @param generalisations generalisations
     */
    public GeneralisationBasedOWLEntityProvider(OWLOntologyManager ontologyManager,
        Set<OWLAxiom> generalisations) {
        super(ontologyManager);
        axioms.addAll(generalisations);
        getOntologyManager().addOntologyChangeListener(listener);
        loadDelegate();
    }

    protected void loadDelegate() {
        for (OWLAxiom ax : axioms) {
            addAll(ax.getSignature());
        }
    }

    /** dispose */
    public void dispose() {
        getOntologyManager().removeOntologyChangeListener(listener);
    }
}
