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
package org.coode.distance.entityrelevance;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;

/** @author eleni */
public abstract class CollectionBasedRelevantPolicy implements RelevancePolicy<OWLEntity> {
    protected final Set<OWLEntity> objects = new HashSet<>();

    /**
     * @param c c
     */
    public CollectionBasedRelevantPolicy(Collection<? extends OWLEntity> c) {
        if (c == null) {
            throw new NullPointerException("The collection of objects cannot be null");
        }
        objects.addAll(c);
    }

    /**
     * @param c c
     * @return relevance policy
     */
    public static CollectionBasedRelevantPolicy allOf(Collection<? extends OWLEntity> c) {
        return new CollectionBasedRelevantPolicy(c) {
            @Override
            public boolean isRelevant(OWLEntity object) {
                return objects.contains(object);
            }
        };
    }

    /**
     * @param c c
     * @return relevance policy
     */
    public static CollectionBasedRelevantPolicy noneOf(Collection<? extends OWLEntity> c) {
        return new CollectionBasedRelevantPolicy(c) {
            @Override
            public boolean isRelevant(OWLEntity object) {
                return !objects.contains(object);
            }
        };
    }

    /** @return the objects */
    public Set<OWLEntity> getObjects() {
        return new HashSet<>(objects);
    }
}
