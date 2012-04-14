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

import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLEntity;


/**
 * @author Luigi Iannone
 * 
 */
public final class DefaultOWLEntityTypeRelevancePolicy implements RelevancePolicy<OWLEntity> {
	private final EntityType<?> type;
	private final boolean relevant;
	private final static RelevancePolicy<OWLEntity> OBJECT_PROPERTIES_ALWAYS_RELEVANT_POLICY = new DefaultOWLEntityTypeRelevancePolicy(
			true, EntityType.OBJECT_PROPERTY);
	private final static RelevancePolicy<OWLEntity> ALWAYS_IRRELEVANT_POLICY = new DefaultOWLEntityTypeRelevancePolicy(
			false, null);
	private final static RelevancePolicy<OWLEntity> ALWAYS_RELEVANT_POLICY = new DefaultOWLEntityTypeRelevancePolicy(
			true, null);

	private DefaultOWLEntityTypeRelevancePolicy(boolean relevant, EntityType<?> type) {
		this.type = type;
		this.relevant = relevant;
	}

	public boolean isRelevant(OWLEntity object) {
		if(object.isType(type) || type == null)
			return this.relevant;
		else
			return !this.relevant;
	}

	public static RelevancePolicy<OWLEntity> getObjectPropertyAlwaysRelevantPolicy() {
		return OBJECT_PROPERTIES_ALWAYS_RELEVANT_POLICY;
	}

	public static RelevancePolicy<OWLEntity> getAlwaysIrrelevantPolicy() {
		return ALWAYS_IRRELEVANT_POLICY;
	}
	
	public static RelevancePolicy<OWLEntity> getAlwaysRelevantPolicy() {
		return ALWAYS_RELEVANT_POLICY;
	}
}
