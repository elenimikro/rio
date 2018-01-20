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
package org.coode.proximitymatrix;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.coode.pair.Pair;

/**
 * @author eleni
 * @param <O> type
 */
public class AddLeftOversHistoryItemFactory<O> implements HistoryItemFactory<O> {
    private final Set<O> leftOvers = new HashSet<>();

    /**
     * @param leftOvers leftOvers
     */
    public AddLeftOversHistoryItemFactory(Collection<? extends O> leftOvers) {
        if (leftOvers == null) {
            throw new NullPointerException("The left over set cannot be null");
        }
        this.leftOvers.addAll(leftOvers);
    }

    @Override
    public HistoryItem<O> create(Pair<O> pair, Collection<? extends O> clusters) {
        Set<O> newClusters = this.getLeftOvers();
        newClusters.addAll(clusters);
        return new HistoryItem<>(pair, newClusters);
    }

    /** @return the leftOvers */
    public Set<O> getLeftOvers() {
        return new HashSet<>(this.leftOvers);
    }

    /**
     * @param leftOvers leftOvers
     * @param <P> type
     * @return leftovers
     */
    public static <P> AddLeftOversHistoryItemFactory<P> build(Collection<? extends P> leftOvers) {
        if (leftOvers == null) {
            throw new NullPointerException("The leftOvers cannot be null");
        }
        return new AddLeftOversHistoryItemFactory<>(leftOvers);
    }
}
