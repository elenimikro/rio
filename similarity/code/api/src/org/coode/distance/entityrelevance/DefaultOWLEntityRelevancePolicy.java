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

/**
 * @author Luigi Iannone
 * 
 */
public final class DefaultOWLEntityRelevancePolicy implements RelevancePolicy<OWLEntity> {
	private final boolean relevant;
	private final static RelevancePolicy<OWLEntity> ALWAYS_RELEVANT_POLICY = new DefaultOWLEntityRelevancePolicy(
			true);
	private final static RelevancePolicy<OWLEntity> ALWAYS_IRRELEVANT_POLICY = new DefaultOWLEntityRelevancePolicy(
			true);

	private DefaultOWLEntityRelevancePolicy(boolean relevant) {
		this.relevant = relevant;
	}

	public boolean isRelevant(OWLEntity object) {
		return this.relevant;
	}

	public static RelevancePolicy<OWLEntity> getAlwaysRelevantPolicy() {
		return ALWAYS_RELEVANT_POLICY;
	}

	public static RelevancePolicy<OWLEntity> getAlwaysIrrelevantPolicy() {
		return ALWAYS_IRRELEVANT_POLICY;
	}
}
