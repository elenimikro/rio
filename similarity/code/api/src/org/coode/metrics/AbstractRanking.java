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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.semanticweb.owlapi.util.MultiMap;

public abstract class AbstractRanking<O, R extends Comparable<R>> implements
		Ranking<O, R> {
	private final Metric<O, R> metric;
	private SortedSet<R> ranking = new TreeSet<R>();
	private final MultiMap<R, O> map = new MultiMap<R, O>();

	/**
	 * @param metric
	 */
	public AbstractRanking(Metric<O, R> metric, Collection<? extends O> objects) {
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
		for (O o : objects) {
			R value = this.getMetric().getValue(o);
			this.ranking.add(value);
			this.map.put(value, o);
		}
	}

	/**
	 * @return the metric
	 */
	public Metric<O, R> getMetric() {
		return this.metric;
	}

	public final R getTopValue() {
		return this.ranking.last();
	}

	public final R getBottomValue() {
		return this.ranking.last();
	}

	public final Set<O> getBottom() {
		return new HashSet<O>(this.map.get(this.getBottomValue()));
	}

	public final Set<O> getTop() {
		return new HashSet<O>(this.map.get(this.getTopValue()));
	}

	public R getAverageValue() {
		return this.isAverageable() ? this.computeAverage() : null;
	}

	protected abstract R computeAverage();

	public final Set<R> getValues() {
		return new LinkedHashSet<R>(this.ranking);
	}

	public final List<RankingSlot<O, R>> getRanking() {
		Set<R> values = this.getValues();
		List<RankingSlot<O, R>> rankingList = new ArrayList<RankingSlot<O, R>>(
				values.size());
		for (R r : values) {
			rankingList.add(new RankingSlot<O, R>(r, this.map.get(r)));
		}
		Collections.reverse(rankingList);
		return rankingList;
	}
}
