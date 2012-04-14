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

import java.util.Comparator;

public class MetricComparator<O, P extends Comparable<P>> implements Comparator<O> {
	private final Metric<O, P> metric;

	/**
	 * @param metric
	 */
	private MetricComparator(Metric<O, P> metric) {
		if (metric == null) {
			throw new NullPointerException("The metric cannot be null");
		}
		this.metric = metric;
	}

	public int compare(O o1, O o2) {
		int difference = this.getMetric().getValue(o1)
				.compareTo(this.getMetric().getValue(o2));
		return difference == 0 ? o1.hashCode() - o2.hashCode() : difference;
	}

	/**
	 * @return the metric
	 */
	public Metric<O, P> getMetric() {
		return this.metric;
	}

	public static <R, S extends Comparable<S>> MetricComparator<R, S> build(
			Metric<R, S> metric) {
		return new MetricComparator<R, S>(metric);
	}
}
