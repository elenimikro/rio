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
import org.coode.owl.wrappers.OWLAtomicDecompositionMap;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/** @author Eleni Mikroyannidi */
public class AtomicDecompositionRanking extends AbstractRanking<OWLEntity, Double> {
    /** @param metric
     * @param objects */
    public AtomicDecompositionRanking(final Set<? extends OWLEntity> objects,
            final Collection<? extends OWLOntology> ontologies,
            final OWLAtomicDecompositionMap map) {
        super(new AtomicDecompositionMetric(ontologies, map), objects);
    }

    /** @see org.coode.metrics.Ranking#isAverageable() */
    public boolean isAverageable() {
        return true;
    }

    /** @see org.coode.metrics.AbstractRanking#computeAverage() */
    @Override
    protected Double computeAverage() {
        Set<Double> values = getValues();
        double sum = 0;
        for (Double value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    public static AtomicDecompositionRanking buildRanking(
            final Collection<? extends OWLOntology> ontologies,
            final OWLAtomicDecompositionMap map) {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        for (OWLOntology owlOntology : ontologies) {
            entities.addAll(owlOntology.getSignature());
        }
        return new AtomicDecompositionRanking(entities, ontologies, map);
    }
}
