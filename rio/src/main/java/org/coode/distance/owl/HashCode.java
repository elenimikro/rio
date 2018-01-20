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

import org.semanticweb.owlapi.model.OWLObject;

/** @author Luigi Iannone */
public interface HashCode {
    /**
     * Returns the hash code for the input OWLObject
     * 
     * @param owlObject The OWL Object whose hash code will be computed. Cannot be {@code null}.
     * @return an {@code int}.
     * @throws NullPointerException if the input is {@code null}.
     */
    public int hashCode(OWLObject owlObject);
}
