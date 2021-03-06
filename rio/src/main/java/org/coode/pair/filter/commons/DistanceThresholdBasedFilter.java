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
package org.coode.pair.filter.commons;

import java.util.Collection;
import java.util.Iterator;

import org.coode.distance.Distance;
import org.coode.pair.filter.PairFilter;

/**
 * @author eleni
 * @param <O> type
 */
public class DistanceThresholdBasedFilter<O> implements PairFilter<Collection<? extends O>> {
    private final Distance<O> distance;
    private final double threshold;

    private DistanceThresholdBasedFilter(Distance<O> distance, double threshold) {
        if (distance == null) {
            throw new NullPointerException("The distance cannot be null");
        }
        this.distance = distance;
        this.threshold = threshold;
    }

    @Override
    public boolean accept(Collection<? extends O> first, Collection<? extends O> second) {
        Iterator<? extends O> iterator = first.iterator();
        boolean found = false;
        while (!found && iterator.hasNext()) {
            O object = iterator.next();
            Iterator<? extends O> anotherIterator = second.iterator();
            while (!found && anotherIterator.hasNext()) {
                O anotherObject = anotherIterator.next();
                found =
                    this.getDistance().getDistance(object, anotherObject) >= this.getThreshold();
            }
        }
        return !found;
    }

    /**
     * @param distance distance
     * @param threshold threshold
     * @param <P> type
     * @return distance filter
     */
    public static <P> DistanceThresholdBasedFilter<P> build(Distance<P> distance,
        double threshold) {
        return new DistanceThresholdBasedFilter<>(distance, threshold);
    }

    /** @return the threshold */
    protected double getThreshold() {
        return this.threshold;
    }

    /** @return the distance */
    protected Distance<O> getDistance() {
        return this.distance;
    }
}
