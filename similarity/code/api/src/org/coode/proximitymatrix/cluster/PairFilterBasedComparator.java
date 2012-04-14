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
package org.coode.proximitymatrix.cluster;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.coode.distance.Distance;
import org.coode.pair.Pair;
import org.coode.pair.filter.PairFilter;

public final class PairFilterBasedComparator<O> implements Comparator<Pair<O>> {
	private final PairFilter<O> filter;
	private final Set<O> objects = new LinkedHashSet<O>();
	private final Distance<O> distance;
	private final static Map<Pair<?>, Integer> scores = new HashMap<Pair<?>, Integer>();

	/**
	 * @param filter
	 */
	private PairFilterBasedComparator(PairFilter<O> filter,
			Collection<? extends O> objects, Distance<O> distance) {
		if (filter == null) {
			throw new NullPointerException("The filter cannot be null");
		}
		if (objects == null) {
			throw new NullPointerException("The objects collection  cannot be null");
		}
		if (distance == null) {
			throw new NullPointerException("The distance cannot be null");
		}
		this.filter = filter;
		this.objects.addAll(objects);
		this.distance = distance;
	}

	/**
	 * @return the filter
	 */
	public PairFilter<O> getFilter() {
		return this.filter;
	}

	public int compare(Pair<O> o1, Pair<O> o2) {
		double firstPairDistance = this.getDistance().getDistance(o1.getFirst(),
				o1.getSecond());
		double secondPairDistance = this.getDistance().getDistance(o2.getFirst(),
				o2.getSecond());
		int difference = (int) Math.signum(firstPairDistance - secondPairDistance);
		return difference != 0 ? difference : this.getScore(o1) - this.getScore(o2);
	}

	private int getScore(Pair<O> pair) {
		Integer cached = scores.get(pair);
		if (cached == null) {
			O first = pair.getFirst();
			O second = pair.getSecond();
			int unionCount = 0;
			int intersectionCount = 0;
			for (O o : this.objects) {
				if (!this.getFilter().accept(first, o)) {
					unionCount++;
					if (!this.getFilter().accept(second, o)) {
						intersectionCount++;
					}
				}
				if (!this.getFilter().accept(second, o)) {
					unionCount++;
				}
			}
			cached = unionCount - intersectionCount;
			scores.put(pair, cached);
		}
		return cached;
	}

	public static <P> PairFilterBasedComparator<P> build(PairFilter<P> filter,
			Collection<? extends P> objects, Distance<P> distance) {
		return new PairFilterBasedComparator<P>(filter, objects, distance);
	}

	/**
	 * @return the distance
	 */
	public Distance<O> getDistance() {
		return this.distance;
	}
}
