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

/** @author Luigi Iannone */
public class DefaultOWLEntityTypeRelevancePolicy implements RelevancePolicy<OWLEntity> {
    private final Set<EntityType<?>> types = new HashSet<>();
    private final boolean relevant;
    private final static RelevancePolicy<OWLEntity> OBJECT_PROPERTIES_ALWAYS_RELEVANT_POLICY =
        new DefaultOWLEntityTypeRelevancePolicy(true, EntityType.OBJECT_PROPERTY);
    private final static RelevancePolicy<OWLEntity> ALWAYS_IRRELEVANT_POLICY =
        new DefaultOWLEntityTypeRelevancePolicy(false);
    private final static RelevancePolicy<OWLEntity> ALWAYS_RELEVANT_POLICY =
        new DefaultOWLEntityTypeRelevancePolicy(true);
    private static final RelevancePolicy<OWLEntity> PROPERTIES_ALWAYS_RELEVANT_POLICY =
        new DefaultOWLEntityTypeRelevancePolicy(true, EntityType.OBJECT_PROPERTY,
            EntityType.DATA_PROPERTY, EntityType.DATATYPE, EntityType.ANNOTATION_PROPERTY);

    private DefaultOWLEntityTypeRelevancePolicy(boolean relevant, EntityType<?>... types) {
        for (EntityType<?> t : types) {
            this.types.add(t);
        }
        this.relevant = relevant;
    }

    @Override
    public boolean isRelevant(OWLEntity object) {
        if (types.contains(object.getEntityType()) || types.isEmpty()) {
            return relevant;
        } else {
            return !relevant;
        }
    }

    /** @return relevance policy */
    public static RelevancePolicy<OWLEntity> getPropertiesAlwaysRelevantPolicy() {
        return PROPERTIES_ALWAYS_RELEVANT_POLICY;
    }

    /** @return relevance policy */
    public static RelevancePolicy<OWLEntity> getObjectPropertyAlwaysRelevantPolicy() {
        return OBJECT_PROPERTIES_ALWAYS_RELEVANT_POLICY;
    }

    /** @return relevance policy */
    public static RelevancePolicy<OWLEntity> getAlwaysIrrelevantPolicy() {
        return ALWAYS_IRRELEVANT_POLICY;
    }

    /** @return relevance policy */
    public static RelevancePolicy<OWLEntity> getAlwaysRelevantPolicy() {
        return ALWAYS_RELEVANT_POLICY;
    }
}
