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

import org.coode.distance.entityrelevance.AbstractRankingRelevancePolicy;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.metrics.AbstractRanking;
import org.coode.metrics.Metric;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import gnu.trove.map.TObjectIntMap;

/** @author Luigi Iannone */
public class AxiomRelevancePolicy implements RelevancePolicy<OWLEntity> {
    private final RelevancePolicy<OWLEntity> relevance;

    /**
     * @param replacedAxiom replacedAxiom
     * @param axiomMap axiomMap
     */
    public AxiomRelevancePolicy(OWLAxiom replacedAxiom, AxiomMap axiomMap) {
        relevance = AbstractRankingRelevancePolicy
            .getAbstractRankingRelevancePolicy(buildRanking(replacedAxiom, axiomMap));
    }

    /**
     * @param replacedAxiom replacedAxiom
     * @param axiomMap axiomMap
     * @return ranking
     */
    public static AbstractRanking<OWLEntity> buildRanking(final OWLAxiom replacedAxiom,
        final AxiomMap axiomMap) {
        final TObjectIntMap<OWLEntity> entityMap = axiomMap.get(replacedAxiom);
        Metric<OWLEntity> m =
            o -> (double) entityMap.get(o) / axiomMap.getAxiomCount(replacedAxiom);
        return new AbstractRanking<OWLEntity>(m, entityMap.keySet(), OWLEntity.class) {
            @Override
            public boolean isAverageable() {
                return true;
            }
        };
    }

    @Override
    public boolean isRelevant(OWLEntity object) {
        return relevance.isRelevant(object);
    }
}
