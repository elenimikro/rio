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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.MultiMap;

public abstract class AbstractRanking<T> implements Ranking<T> {
	private final Metric<T> metric;
	private Double max = -1D;

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
		private final SlowDoubleBag bag;
		private final SlowDoubleBag occurrences;
		final double[] keyset;
		int size;
		int total_occurrences = 0;

		public TinyDoubleMap(double[] all, List<Double> keys) {
			bag = new SlowDoubleBag(all, keys);
			occurrences = new SlowDoubleBag(all.length, keys);
			size = 0;
			keyset = new double[keys.size()];
			Iterator<Double> it = keys.iterator();
			for (int i = 0; i < keyset.length; i++) {
				keyset[i] = it.next();
			}
		}

		void add(double e, int occ) {
			int theOccs = bag.occurrences(e);
			total_occurrences += theOccs * occ;
			occurrences.setOccurrence(e, occ);
		}

		public double mean() {
			double meanTest = 0;
			for (int i = 0; i < size; i++) {
				double d = bag.get(i);
				meanTest += d * bag.occurrences(d) * occurrences.occurrences(d);
			}
			return meanTest;
		}

		public double[] keys() {
			return keyset;
		}

		public void collect(Set<Double> set) {
			for (int i = 0; i < size; i++) {
				set.add(keyset[i]);
			}
		}

		public int addOccurrences() {
			return total_occurrences;
		}

		public Iterable<Double> iterate() {
			return bag;
		}
	}

	private final TinyDoubleMap dmap;
	private final T[] maxEntities;

	/** @param metric */
	public AbstractRanking(Metric<T> metric, Collection<T> objects,
			Class<T> clazz) {
		if (metric == null) {
			throw new NullPointerException("The metric cannot be null");
		}
		if (objects == null) {
			throw new NullPointerException(
					"The collection of obejcts cannot be null");
		}
		if (objects.isEmpty()) {
			throw new IllegalArgumentException(
					"The collection of objects to rank cannot be empty");
		}
		this.metric = metric;
		double[] valueList = new double[objects.size()];
		int i = 0;
		MultiMap<Double, T> map = new MultiMap<Double, T>(false, false);
		for (T o : objects) {
			Double value = filter(this.metric.getValue(o));
			if (max.compareTo(value) < 0) {
				max = value;
			}
			valueList[i++] = value;
			map.put(value, o);
		}
		// System.out.println("AbstractRanking.AbstractRanking() " +
		// valueList.length + "\t"
		// + map.keySet().size() + "\t" + size(valueList));
		dmap = new TinyDoubleMap(valueList, new ArrayList<Double>(map.keySet()));
		for (Double key : map.keySet()) {
			dmap.add(key, map.get(key).size());
		}
		Collection<T> m = map.get(max);
		maxEntities = (T[]) Array.newInstance(clazz, m.size());
		Iterator<T> it = m.iterator();
		for (int x = 0; x < maxEntities.length; x++) {
			maxEntities[x] = it.next();
		}
	}

	private int size(double[] valueList) {
		Set<Double> set = new HashSet<Double>();
		for (double d : valueList) {
			set.add(d);
		}
		return set.size();
	}

	private static final Double[] doubles = getDoubles();

	private static Double[] getDoubles() {
		Double[] d = new Double[1001];
		for (int i = 0; i < 1001; i++) {
			d[i] = Math.rint(i) / 1000;
		}
		return d;
	}

	private Double filter(double value) {
		return doubles[(int) Math.rint(value * 1000)];
	}

	/** @return the metric */
	public Metric<T> getMetric() {
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
	public final T[] getBottom() {
		return maxEntities;
	}

	@Override
	public final T[] getTop() {
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

	private final Comparator<RankingSlot<T>> sorter = new Comparator<RankingSlot<T>>() {
		@Override
		public int compare(final RankingSlot<T> arg0, final RankingSlot<T> arg1) {
			return (int) Math.signum(arg0.getValue() - arg1.getValue());
		}
	};
	private List<RankingSlot<T>> sortedList = null;

	@Override
	public final List<RankingSlot<T>> getSortedRanking() {
		if (sortedList == null) {
			List<RankingSlot<T>> list = getUnorderedRanking();
			Collections.sort(list, sorter);
			sortedList = list;
		}
		return sortedList;
	}

	@Override
	public final List<RankingSlot<T>> getUnorderedRanking() {
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
		for (double i : dmap.iterate()) {
			sd.increment(i);
		}
		return sd.getResult();
	}

	public double computeAverage() {
		final int size = dmap.size;
		if (size == 0) {
			return 0D;
		}
		double total = 0;
		for (double d : dmap.iterate()) {
			total += d;
		}
		return total / size;
	}

	public final int computeSampleSize() {
		return dmap.addOccurrences();
		// int toReturn = 0;
		// for (int i : dmap.occs) {
		// toReturn += i;
		// }
		// return toReturn;
	}

	public final double computeMean() {
		// final int size = dmap.size;
		int counter = dmap.addOccurrences();
		if (counter == 0) {
			return 0;
		}
		double mean = dmap.mean();
		// for (int i = 0; i < size; i++) {
		// // counter += dmap.occs[i];
		// mean += dmap.valueListTimesOccurrences(i);
		// }
		if (counter > 0) {
			return mean / counter;
		}
		return 0;
	}
}
