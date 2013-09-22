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
package org.coode.distance.entityrelevance.owl;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.coode.distance.entityrelevance.AbstractRankingRelevancePolicy;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.metrics.AbstractRanking;
import org.coode.metrics.Metric;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

/** @author Luigi Iannone */
public class AxiomRelevancePolicy implements RelevancePolicy {
    private final RelevancePolicy relevance;

    /** @param axiom */
    public AxiomRelevancePolicy(final OWLAxiom replacedAxiom, final AxiomMap axiomMap) {
        relevance = AbstractRankingRelevancePolicy
                .getAbstractRankingRelevancePolicy(buildRanking(replacedAxiom, axiomMap));
    }

    public static AbstractRanking buildRanking(final OWLAxiom replacedAxiom,
            final AxiomMap axiomMap) {
        final Map<OWLEntity, AtomicInteger> entityMap = axiomMap.get(replacedAxiom);
        Metric<OWLEntity> m = new Metric<OWLEntity>() {
            @Override
            public double getValue(final OWLEntity object) {
                AtomicInteger value = entityMap.get(object);
                double d = 0;
                if (value != null) {
                    d = value.doubleValue();
                }
                double total = axiomMap.getAxiomCount(replacedAxiom);
                return d / total;
            }
        };
        AbstractRanking ranking = new AbstractRanking(
                m, entityMap.keySet()) {
            @Override
            public boolean isAverageable() {
                return true;
            }


        };
        return ranking;
    }

    /** @see org.coode.distance.entityrelevance.RelevancePolicy#isRelevant(java.lang
     *      .Object) */
    @Override
    public boolean isRelevant(final OWLEntity object) {
        return relevance.isRelevant(object);
    }
}