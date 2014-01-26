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
public class AxiomRelevancePolicy implements RelevancePolicy<OWLEntity> {
    private final RelevancePolicy<OWLEntity> relevance;

    /** @param replacedAxiom
     *            replacedAxiom
     * @param axiomMap
     *            axiomMap */
    public AxiomRelevancePolicy(final OWLAxiom replacedAxiom, final AxiomMap axiomMap) {
        relevance = AbstractRankingRelevancePolicy
                .getAbstractRankingRelevancePolicy(buildRanking(replacedAxiom, axiomMap));
    }

    /** @param replacedAxiom
     *            replacedAxiom
     * @param axiomMap
     *            axiomMap
     * @return ranking */
    public static AbstractRanking<OWLEntity> buildRanking(final OWLAxiom replacedAxiom,
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
        AbstractRanking<OWLEntity> ranking = new AbstractRanking<OWLEntity>(m,
                entityMap.keySet(), OWLEntity.class) {
            @Override
            public boolean isAverageable() {
                return true;
            }
        };
        return ranking;
    }

    @Override
    public boolean isRelevant(final OWLEntity object) {
        return relevance.isRelevant(object);
    }
}
