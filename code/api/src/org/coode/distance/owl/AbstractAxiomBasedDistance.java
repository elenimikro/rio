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
package org.coode.distance.owl;

import java.util.Set;

import org.coode.distance.Distance;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

/** @author eleni */
public interface AbstractAxiomBasedDistance extends Distance<OWLEntity> {
    /** @param owlEntity
     *            owlEntity
     * @return axioms */
    public Set<OWLAxiom> getAxioms(OWLEntity owlEntity);
}
