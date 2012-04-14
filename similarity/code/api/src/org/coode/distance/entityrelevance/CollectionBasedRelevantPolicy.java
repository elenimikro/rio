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

public abstract class CollectionBasedRelevantPolicy<O> implements RelevancePolicy<O> {
	private final Set<O> objects = new HashSet<O>();

	public CollectionBasedRelevantPolicy(Collection<? extends O> c) {
		if (c == null) {
			throw new NullPointerException("The collection of objects cannot be null");
		}
		this.objects.addAll(c);
	}

	public static final <P> CollectionBasedRelevantPolicy<P> allOf(
			Collection<? extends P> c) {
		return new CollectionBasedRelevantPolicy<P>(c) {
			public boolean isRelevant(P object) {
				return this.getObjects().contains(object);
			}
		};
	}

	public static final <P> CollectionBasedRelevantPolicy<P> noneOf(
			Collection<? extends P> c) {
		return new CollectionBasedRelevantPolicy<P>(c) {
			public boolean isRelevant(P object) {
				return !this.getObjects().contains(object);
			}
		};
	}

	/**
	 * @return the objects
	 */
	public Set<O> getObjects() {
		return new HashSet<O>(this.objects);
	}
}
