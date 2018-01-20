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
package org.coode.distance;

import org.semanticweb.owlapi.model.OWLObject;

/** @author eleni */
public interface ReplacementStrategy {
    /**
     * @param owlObject owlObject
     * @param <O> type
     * @return replaced object
     */
    <O extends OWLObject> O replace(O owlObject);
}
