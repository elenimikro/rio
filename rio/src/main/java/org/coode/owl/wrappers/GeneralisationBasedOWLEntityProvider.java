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
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
public class GeneralisationBasedOWLEntityProvider extends OWLEntityProviderBase {
    private final Set<OWLAxiom> axioms = new HashSet<>();
    private final OWLOntologyChangeListener listener = changes -> {
        clear();
        loadDelegate();
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
        axioms.stream().flatMap(OWLAxiom::signature).forEach(this::add);
    }

    /** dispose */
    public void dispose() {
        getOntologyManager().removeOntologyChangeListener(listener);
    }
}
