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
 * @param <O> metric type
 */
public abstract class AbstractMetricAggregation<O> implements Metric<O> {
    private final List<Metric<O>> metrics = new ArrayList<>();

    /**
     * @param metrics metrics
     */
    public AbstractMetricAggregation(List<Metric<O>> metrics) {
        if (metrics == null) {
            throw new NullPointerException("The metrics cannot be null");
        }
        this.metrics.addAll(metrics);
    }

    @Override
    public double getValue(O object) {
        double[] doubles = new double[metrics.size()];
        int i = 0;
        for (Metric<O> m : this.metrics) {
            doubles[i++] = m.getValue(object);
        }
        return this.aggregate(doubles);
    }

    protected abstract double aggregate(double... values);

    /** @return the metrics */
    public List<Metric<O>> getMetrics() {
        return new ArrayList<>(this.metrics);
    }

    /**
     * @param metrics metrics
     * @param <S> type
     * @return sum
     */
    public static <S> Metric<S> getSum(Collection<? extends Metric<S>> metrics) {
        return new AbstractMetricAggregation<S>(new ArrayList<Metric<S>>(metrics)) {
            @Override
            protected double aggregate(double... values) {
                double toReturn = 0d;
                for (double value : values) {
                    toReturn += value;
                }
                return toReturn;
            }
        };
    }

    /**
     * @param metrics metrics
     * @param <S> type
     * @return product
     */
    public static <S> Metric<S> getProduct(Collection<? extends Metric<S>> metrics) {
        return new AbstractMetricAggregation<S>(new ArrayList<Metric<S>>(metrics)) {
            @Override
            protected double aggregate(double... values) {
                double toReturn = 1d;
                for (double value : values) {
                    toReturn *= value;
                }
                return toReturn;
            }
        };
    }
}
