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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * @author Luigi Iannone
 * 
 */
public final class DefaultOWLEntityTypeRelevancePolicy implements
		RelevancePolicy<OWLEntity> {
	// private EntityType<?> type;
	private final Set<EntityType<?>> types = new HashSet<EntityType<?>>();
	private final boolean relevant;
	private final static RelevancePolicy OBJECT_PROPERTIES_ALWAYS_RELEVANT_POLICY = new DefaultOWLEntityTypeRelevancePolicy(
			true, EntityType.OBJECT_PROPERTY);
	private final static RelevancePolicy ALWAYS_IRRELEVANT_POLICY = new DefaultOWLEntityTypeRelevancePolicy(
			false);
	private final static RelevancePolicy ALWAYS_RELEVANT_POLICY = new DefaultOWLEntityTypeRelevancePolicy(
			true);
	private static final RelevancePolicy PROPERTIES_ALWAYS_RELEVANT_POLICY = new DefaultOWLEntityTypeRelevancePolicy(
			true, EntityType.OBJECT_PROPERTY, EntityType.DATA_PROPERTY,
			EntityType.DATATYPE, EntityType.ANNOTATION_PROPERTY);

	private DefaultOWLEntityTypeRelevancePolicy(boolean relevant,
			EntityType<?>... types) {
		if (types.length != 0) {
			for (EntityType<?> t : types) {
				this.types.add(t);
			}
		}
		this.relevant = relevant;
	}

	@Override
	public boolean isRelevant(OWLEntity object) {
		if (types == null || types.contains(object.getEntityType())
				|| types.isEmpty()) {
			return relevant;
		} else {
			return !relevant;
		}
	}

	public static RelevancePolicy getPropertiesAlwaysRelevantPolicy() {
		return PROPERTIES_ALWAYS_RELEVANT_POLICY;
	}

	public static RelevancePolicy getObjectPropertyAlwaysRelevantPolicy() {
		return OBJECT_PROPERTIES_ALWAYS_RELEVANT_POLICY;
	}

	public static RelevancePolicy getAlwaysIrrelevantPolicy() {
		return ALWAYS_IRRELEVANT_POLICY;
	}

	public static RelevancePolicy getAlwaysRelevantPolicy() {
		return ALWAYS_RELEVANT_POLICY;
	}
}
