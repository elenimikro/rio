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
package org.coode.owl.distance.test;

import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.owl.AbstractAxiomBasedDistance;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/** @author eleni */
public interface DistanceBuilder {
    /** @param o
     *            o
     * @return distance */
    AbstractAxiomBasedDistance getDistance(OWLOntology o);

    /** @param o
     *            o
     * @param rp
     *            rp
     * @return distance */
    AbstractAxiomBasedDistance getDistance(OWLOntology o, RelevancePolicy<OWLEntity> rp);
}
