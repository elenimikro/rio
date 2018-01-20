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

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

/** @author eleni */
public interface OWLAxiomProvider extends Set<OWLAxiom> {
    /**
     * @param l l
     */
    public void addOWLAxiomsChangedListener(OWLAxiomsChangedListener l);

    /**
     * @param l l
     */
    public void removeOWLAxiomsChangedListener(OWLAxiomsChangedListener l);

    /** @return signature */
    public Set<OWLEntity> getSignature();
}
