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
/**
 * 
 */
package org.coode.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class AbstractMetricAggregation<O, R, Q> implements Metric<O, R> {
	private final List<Metric<O, ? extends Q>> metrics = new ArrayList<Metric<O, ? extends Q>>();

	/**
	 * @param metrics
	 */
	public AbstractMetricAggregation(List<Metric<O, ? extends Q>> metrics) {
		if (metrics == null) {
			throw new NullPointerException("The metrics cannot be null");
		}
		this.metrics.addAll(metrics);
	}

	/**
	 * @see org.coode.metrics.Metric#getValue(java.lang.Object)
	 */
	public R getValue(O object) {
		List<Q> values = new ArrayList<Q>();
		for (Metric<O, ? extends Q> m : this.getMetrics()) {
			values.add(m.getValue(object));
		}
		return this.aggregate(values);
	}

	protected abstract R aggregate(Collection<? extends Q> values);

	/**
	 * @return the metrics
	 */
	public List<Metric<O, ? extends Q>> getMetrics() {
		return new ArrayList<Metric<O, ? extends Q>>(this.metrics);
	}

	public static <S, T extends Number> Metric<S, Double> getSum(
			Collection<? extends Metric<S, T>> metrics) {
		return new AbstractMetricAggregation<S, Double, T>(
				new ArrayList<Metric<S, ? extends T>>(metrics)) {
			@Override
			protected Double aggregate(Collection<? extends T> values) {
				double toReturn = 0d;
				for (T value : values) {
					if (value != null) {
						toReturn += value.doubleValue();
					}
				}
				return toReturn;
			}
		};
	}

	public static <S, T extends Number> Metric<S, Double> getProduct(
			Collection<? extends Metric<S, ? extends T>> metrics) {
		return new AbstractMetricAggregation<S, Double, T>(
				new ArrayList<Metric<S, ? extends T>>(metrics)) {
			@Override
			protected Double aggregate(Collection<? extends T> values) {
				double toReturn = 1d;
				for (T value : values) {
					if (value != null) {
						toReturn *= value.doubleValue();
					}
				}
				return toReturn;
			}
		};
	}
}
