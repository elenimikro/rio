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
package org.coode.distance.entityrelevance;

import org.semanticweb.owlapi.model.OWLEntity;

/** @author Luigi Iannone */
public class DefaultOWLEntityRelevancePolicy implements RelevancePolicy<OWLEntity> {
    private final boolean relevant;
    private final static RelevancePolicy<OWLEntity> ALWAYS_RELEVANT_POLICY =
        new DefaultOWLEntityRelevancePolicy(true);
    private final static RelevancePolicy<OWLEntity> ALWAYS_IRRELEVANT_POLICY =
        new DefaultOWLEntityRelevancePolicy(true);

    private DefaultOWLEntityRelevancePolicy(boolean relevant) {
        this.relevant = relevant;
    }

    @Override
    public boolean isRelevant(OWLEntity object) {
        return relevant;
    }

    /** @return relevance policy */
    public static RelevancePolicy<OWLEntity> getAlwaysRelevantPolicy() {
        return ALWAYS_RELEVANT_POLICY;
    }

    /** @return relevance policy */
    public static RelevancePolicy<OWLEntity> getAlwaysIrrelevantPolicy() {
        return ALWAYS_IRRELEVANT_POLICY;
    }
}
