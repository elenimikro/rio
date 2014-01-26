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

import org.coode.distance.SparseMatrix;
import org.coode.pair.filter.PairFilter;

/** @author eleni
 * @param <O>
 *            type */
public class DistanceThresholdBasedFilter<O> implements
        PairFilter<Collection<? extends DistanceTableObject<O>>> {
    private final SparseMatrix distance;
    private final double threshold;

    private DistanceThresholdBasedFilter(final SparseMatrix distance,
            final double threshold) {
        if (distance == null) {
            throw new NullPointerException("The distance cannot be null");
        }
        this.distance = distance;
        this.threshold = threshold;
    }

    Collection<? extends DistanceTableObject<O>> lastfirst;
    Collection<? extends DistanceTableObject<O>> lastsecond;
    boolean lastResult;

    @Override
    public boolean accept(final Collection<? extends DistanceTableObject<O>> first,
            final Collection<? extends DistanceTableObject<O>> second) {
        if (lastfirst == first && lastsecond == second) {
            return lastResult;
        }
        lastfirst = first;
        lastsecond = second;
        if (first instanceof List && second instanceof List) {
            lastResult = listAccept((List<DistanceTableObject<O>>) first,
                    (List<DistanceTableObject<O>>) second);
        } else {
            Iterator<? extends DistanceTableObject<O>> iterator = first.iterator();
            boolean found = false;
            while (!found && iterator.hasNext()) {
                DistanceTableObject<O> object = iterator.next();
                Iterator<? extends DistanceTableObject<O>> anotherIterator = second
                        .iterator();
                while (!found && anotherIterator.hasNext()) {
                    DistanceTableObject<O> anotherObject = anotherIterator.next();
                    found = this.distance
                            .get(object.getIndex(), anotherObject.getIndex()) >= this.threshold;
                }
            }
            lastResult = !found;
        }
        return lastResult;
    }

    private boolean listAccept(final List<? extends DistanceTableObject<O>> first,
            final List<? extends DistanceTableObject<O>> second) {
        boolean found = false;
        final int size = first.size();
        for (int i = 0; i < size && !found; i++) {
            DistanceTableObject<O> object = first.get(i);
            final int secondSize = second.size();
            for (int j = 0; j < secondSize && !found; j++) {
                found = this.distance.get(object.getIndex(), second.get(j).getIndex()) >= this.threshold;
            }
        }
        return !found;
    }

    /** @param distance
     *            distance
     * @param threshold
     *            threshold
     * @return distance filter */
    public static <P> DistanceThresholdBasedFilter<P> build(final SparseMatrix distance,
            final double threshold) {
        return new DistanceThresholdBasedFilter<P>(distance, threshold);
    }
}
