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

public abstract class CollectionBasedRelevantPolicy implements RelevancePolicy {
    protected final Set<OWLEntity> objects = new HashSet<OWLEntity>();

    public CollectionBasedRelevantPolicy(Collection<? extends OWLEntity> c) {
		if (c == null) {
			throw new NullPointerException("The collection of objects cannot be null");
		}
		objects.addAll(c);
	}

    public static final CollectionBasedRelevantPolicy allOf(
            Collection<? extends OWLEntity> c) {
        return new CollectionBasedRelevantPolicy(c) {
			@Override
            public boolean isRelevant(OWLEntity object) {
                return objects.contains(object);
			}
		};
	}

    public static final CollectionBasedRelevantPolicy noneOf(
            Collection<? extends OWLEntity> c) {
        return new CollectionBasedRelevantPolicy(c) {
			@Override
            public boolean isRelevant(OWLEntity object) {
                return !objects.contains(object);
			}
		};
	}

	/**
	 * @return the objects
	 */
    public Set<OWLEntity> getObjects() {
        return new HashSet<OWLEntity>(objects);
	}
}
