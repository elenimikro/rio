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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.MultiMap;

public abstract class AbstractRanking implements Ranking {
    private final Metric<OWLEntity> metric;
    // private SortedSet<R> ranking = new TreeSet<R>();
    // private final double[] valueList;
    private double max = -1D;

    static class Entry {
        double key;
        OWLEntity[] set;

        public Entry(double d, Collection<OWLEntity> s) {
            key = d;
            set = new OWLEntity[s.size()];
            int i = 0;
            Iterator<OWLEntity> it = s.iterator();
            while (it.hasNext()) {
                set[i] = it.next();
            }
        }
    }

    static class DoubleMap {
        final List<Entry> list;
        final int size;

        public DoubleMap(int i) {
            list = new ArrayList<Entry>(i);
            size = i;
        }

        void put(Entry e) {
            list.add(e);
        }

        OWLEntity[] get(double key) {
            for (int i = 0; i < size; i++) {
                if (list.get(i).key == key) {
                    return list.get(i).set;
                }
            }
            return empty;
        }

        final static OWLEntity[] empty = new OWLEntity[0];

        public double[] keys() {
            double[] keys = new double[list.size()];
            for (int i = 0; i < size; i++) {
                keys[i] = list.get(i).key;
            }
            return keys;
        }

        public void collect(Set<Double> set) {
            for (int i = 0; i < size; i++) {
                set.add(list.get(i).key);
            }
        }
    }

    static class TinyDoubleMap {
        final double[] list;
        final int[] occs;
        final double[] keyset;
        int size;

        public TinyDoubleMap(double[] all, Collection<Double> keys) {
            list = all;
            occs = new int[all.length];
            size = 0;
            keyset = new double[keys.size()];
            Iterator<Double> it = keys.iterator();
            for (int i = 0; i < keyset.length; i++) {
                keyset[i] = it.next();
            }
        }

        void add(double e, int occ) {
            for (int i = 0; i < list.length; i++) {
                if (list[i] == e) {
                    occs[i] = occ;
                }
            }
        }

        int get(double d) {
            for (int i = 0; i < size; i++) {
                if (list[i] == d) {
                    return occs[i];
                }
            }
            return 0;
        }

        public double[] keys() {
            return keyset;
        }

        public double[] valueList() {
            return list;
        }

        public void collect(Set<Double> set) {
            for (int i = 0; i < size; i++) {
                set.add(keyset[i]);
            }
        }
    }

    private final TinyDoubleMap dmap;
    private final OWLEntity[] maxEntities;

    /** @param metric */
    public AbstractRanking(final Metric<OWLEntity> metric,
            final Set<? extends OWLEntity> objects) {
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
        double[] valueList = new double[objects.size()];
        int i = 0;
        MultiMap<Double, OWLEntity> map = new MultiMap<Double, OWLEntity>(false, false);
        for (OWLEntity o : objects) {
            double value = this.metric.getValue(o);
            if (max < value) {
                max = value;
            }
            valueList[i++] = value;
            map.put(value, o);
        }
        dmap = new TinyDoubleMap(valueList, map.keySet());
        for (double key : map.keySet()) {
            dmap.add(key, map.get(key).size());
        }
        Collection<OWLEntity> m = map.get(max);
        maxEntities = new OWLEntity[m.size()];
        Iterator<OWLEntity> it = m.iterator();
        for (int x = 0; x < maxEntities.length; x++) {
            maxEntities[x] = it.next();
        }
    }

    /** @return the metric */
    public Metric<OWLEntity> getMetric() {
        return metric;
    }

    @Override
    public final double getTopValue() {
        return max;
    }

    @Override
    public final double getBottomValue() {
        return getTopValue();
    }

    @Override
    public final OWLEntity[] getBottom() {
        return maxEntities;
    }

    @Override
    public final OWLEntity[] getTop() {
        return maxEntities;
    }

    @Override
    public double getAverageValue() {
        return isAverageable() ? computeAverage() : 0D;
    }

    @Override
    public final double[] getValues() {
        return dmap.keys();
    }

    public void collect(Set<Double> set) {
        dmap.collect(set);
    }

    private final Comparator<RankingSlot<OWLEntity>> sorter = new Comparator<RankingSlot<OWLEntity>>() {
        @Override
        public int compare(final RankingSlot<OWLEntity> arg0,
                final RankingSlot<OWLEntity> arg1) {
            return (int) Math.signum(arg0.getValue() - arg1.getValue());
        }
    };
    private List<RankingSlot<OWLEntity>> sortedList = null;

    @Override
    public final List<RankingSlot<OWLEntity>> getSortedRanking() {
        if (sortedList == null) {
            List<RankingSlot<OWLEntity>> list = getUnorderedRanking();
            Collections.sort(list, sorter);
            sortedList = list;
        }
        return sortedList;
    }

    @Override
    public final List<RankingSlot<OWLEntity>> getUnorderedRanking() {
        return null;
        // final int size = valueList.length;
        // List<RankingSlot<OWLEntity>> rankingList = new
        // ArrayList<RankingSlot<OWLEntity>>(
        // size);
        // for (int i = 0; i < size; i++) {
        // rankingList.add(new RankingSlot<OWLEntity>(valueList[i], dmap
        // .get(valueList[i])));
        // }
        // // Collections.reverse(rankingList);
        // return rankingList;
    }

    public double computeStandardDeviation() {
        StandardDeviation sd = new StandardDeviation();
        for (double i : dmap.list) {
            sd.increment(i);
        }
        return sd.getResult();
    }

    public double computeAverage() {
        final int size = dmap.list.length;
        if (size == 0) {
            return 0D;
        }
        double total = 0;
        for (double d : dmap.list) {
            total += d;
        }
        return total / size;
    }

    public final int computeSampleSize() {
        int toReturn = 0;
        for (int i : dmap.occs) {
            toReturn += i;
        }
        return toReturn;
    }

    public final double computeMean() {
        final int size = dmap.list.length;
        int counter = 0;
        double mean = 0;
        for (int i = 0; i < size; i++) {
            counter += dmap.occs[i];
            mean += dmap.list[i] * dmap.occs[i];
        }
        if (counter > 0) {
            return mean / counter;
        }
        return 0;
    }
}
