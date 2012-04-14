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
package org.coode.distance.wrapping;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.coode.pair.filter.PairFilter;

public class DistanceThresholdBasedFilter<O> implements
		PairFilter<Collection<? extends DistanceTableObject<O>>> {
	private final double[][] distance;
	private final double threshold;

	private DistanceThresholdBasedFilter(double[][] distance, double threshold) {
		if (distance == null) {
			throw new NullPointerException("The distance cannot be null");
		}
		this.distance = distance;
		this.threshold = threshold;
	}

	/**
	 * @see org.coode.pair.filter.PairFilter#accept(java.lang.Objefilteredjava.lang.Object)
	 */
	public boolean accept(Collection<? extends DistanceTableObject<O>> first,
			Collection<? extends DistanceTableObject<O>> second) {
		if (first instanceof List && second instanceof List) {
			return listAccept((List<DistanceTableObject<O>>) first,
					(List<DistanceTableObject<O>>) second);
		}
		Iterator<? extends DistanceTableObject<O>> iterator = first.iterator();
		boolean found = false;
		while (!found && iterator.hasNext()) {
			DistanceTableObject<O> object = iterator.next();
			Iterator<? extends DistanceTableObject<O>> anotherIterator = second
					.iterator();
			while (!found && anotherIterator.hasNext()) {
				DistanceTableObject<O> anotherObject = anotherIterator.next();
				found = this.getDistance()[object.getIndex()][anotherObject.getIndex()] >= this
						.getThreshold();
			}
		}
		return !found;
	}

	private boolean listAccept(List<? extends DistanceTableObject<O>> first,
			List<? extends DistanceTableObject<O>> second) {
		boolean found = false;
		final int size = first.size();
		for (int i = 0; i < size && !found; i++) {
			DistanceTableObject<O> object = first.get(i);
			final int secondSize = second.size();
			for (int j = 0; j < secondSize && !found; j++) {
				DistanceTableObject<O> anotherObject = second.get(j);
				found = this.getDistance()[object.getIndex()][anotherObject.getIndex()] >= this
						.getThreshold();
			}
		}
		return !found;
	}

	public static <P> DistanceThresholdBasedFilter<P> build(double[][] distance,
			double threshold) {
		return new DistanceThresholdBasedFilter<P>(distance, threshold);
	}

	/**
	 * @return the threshold
	 */
	protected double getThreshold() {
		return this.threshold;
	}

	/**
	 * @return the distance
	 */
	protected double[][] getDistance() {
		return this.distance;
	}
}
