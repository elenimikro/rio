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
package org.coode.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.util.MultiMap;

public abstract class AbstractRanking<O, R extends Comparable<R>> implements
        Ranking<O, R> {
    private final Metric<O, R> metric;
    // private SortedSet<R> ranking = new TreeSet<R>();
    private final List<R> valueList;
    private R max = null;
    // this multimap uses lists, not sets - we already know there are no
    // duplicates in the input.
    private final MultiMap<R, O> map = new MultiMap<R, O>(false, false);

    /** @param metric */
    public AbstractRanking(final Metric<O, R> metric, final Set<? extends O> objects) {
        if (metric == null) {
            throw new NullPointerException("The metric cannot be null");
        }
        if (objects == null) {
            throw new NullPointerException("The collection of obejcts cannot be null");
        }
        if (objects.isEmpty()) {
            throw new IllegalArgumentException(
                    "The collection of objects to rank cannot be empty");
        }
        this.metric = metric;
        this.valueList = new ArrayList<R>(objects.size());
        for (O o : objects) {
            R value = this.metric.getValue(o);
            if (max == null) {
                max = value;
            } else if (max.compareTo(value) < 0) {
                max = value;
            }
            this.valueList.add(value);
            this.map.put(value, o);
        }
    }

    /** @return the metric */
    public Metric<O, R> getMetric() {
        return this.metric;
    }

    public final R getTopValue() {
        return max;
    }

    public final R getBottomValue() {
        return getTopValue();
    }

    public final Set<O> getBottom() {
        return new HashSet<O>(this.map.get(this.getBottomValue()));
    }

    public final Set<O> getTop() {
        return new HashSet<O>(this.map.get(this.getTopValue()));
    }

    public R getAverageValue() {
        return isAverageable() ? this.computeAverage() : null;
    }

    protected abstract R computeAverage();

    public final Set<R> getValues() {
        return new LinkedHashSet<R>(this.valueList);
    }

    public final List<R> getValuesList() {
        return valueList;
    }

    private final Comparator<RankingSlot<O, R>> sorter = new Comparator<RankingSlot<O, R>>() {
        @Override
        public int compare(final RankingSlot<O, R> arg0, final RankingSlot<O, R> arg1) {
            return arg0.getValue().compareTo(arg1.getValue());
        }
    };
    private List<RankingSlot<O, R>> sortedList = null;

    public final List<RankingSlot<O, R>> getSortedRanking() {
        if (sortedList == null) {
            List<RankingSlot<O, R>> list = getUnorderedRanking();
            Collections.sort(list, sorter);
            sortedList = list;
        }
        return sortedList;
    }

    public final List<RankingSlot<O, R>> getUnorderedRanking() {
        List<R> values = this.getValuesList();
        final int size = values.size();
        List<RankingSlot<O, R>> rankingList = new ArrayList<RankingSlot<O, R>>(size);
        for (int i = 0; i < size; i++) {
            rankingList.add(new RankingSlot<O, R>(values.get(i), this.map.get(values
                    .get(i))));
        }
        // Collections.reverse(rankingList);
        return rankingList;
    }
}
