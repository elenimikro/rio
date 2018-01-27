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
/**
 * 
 */
package org.coode.proximitymatrix.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.semanticweb.owlapi.model.OWLAxiom;

/** @author Luigi Iannone */
public class OWLAxiomListItem {
    private final OWLAxiom axiom;
    private final Set<OWLAxiomInstantiation> instantiations = new HashSet<>();

    /**
     * @param axiom axiom
     * @param instantiations instantiations
     */
    public OWLAxiomListItem(OWLAxiom axiom,
        Collection<? extends OWLAxiomInstantiation> instantiations) {
        if (axiom == null) {
            throw new NullPointerException("The axiom cannot be null");
        }
        if (instantiations == null) {
            throw new NullPointerException("The instantiations cannot be null");
        }
        this.axiom = axiom;
        this.instantiations.addAll(instantiations);
    }

    /** @return the axiom */
    public OWLAxiom getAxiom() {
        return axiom;
    }

    /** @return the count */
    public int getCount() {
        return instantiations.size();
    }

    @Override
    public String toString() {
        return String.format("%s[%d]", getAxiom(), Integer.valueOf(getCount()));
    }

    /** @return the instantiations */
    public Set<OWLAxiomInstantiation> getInstantiations() {
        return new HashSet<>(instantiations);
    }
}
