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
package org.coode.metrics.owl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.coode.metrics.AbstractRanking;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/** @author Luigi Iannone */
public class OWLEntityPopularityRanking extends AbstractRanking<OWLEntity> {
    /** @param ontologies
     *            ontologies
     * @param objects
     *            objects */
    public OWLEntityPopularityRanking(Set<OWLEntity> objects,
            Collection<OWLOntology> ontologies) {
        super(new OWLEntityPopularity(ontologies), objects, OWLEntity.class);
    }

    @Override
    public boolean isAverageable() {
        return true;
    }

    /** @param ontologies
     *            ontologies
     * @return popularity ranking */
    public static OWLEntityPopularityRanking buildRanking(
            Collection<OWLOntology> ontologies) {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        for (OWLOntology owlOntology : ontologies) {
            entities.addAll(owlOntology.getSignature());
        }
        return new OWLEntityPopularityRanking(entities, ontologies);
    }
}
