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

/**
 * @author eleni
 * @param <O> type
 */
public class MetricComparator<O> implements Comparator<O> {
    private final Metric<O> metric;

    /**
     * @param metric metric
     */
    private MetricComparator(Metric<O> metric) {
        if (metric == null) {
            throw new NullPointerException("The metric cannot be null");
        }
        this.metric = metric;
    }

    @Override
    public int compare(O o1, O o2) {
        int difference =
            (int) Math.signum(this.getMetric().getValue(o1) - this.getMetric().getValue(o2));
        return difference == 0 ? o1.hashCode() - o2.hashCode() : difference;
    }

    /** @return the metric */
    public Metric<O> getMetric() {
        return this.metric;
    }

    /**
     * @param metric metric
     * @param <R> type
     * @return metric comparator
     */
    public static <R> MetricComparator<R> build(Metric<R> metric) {
        return new MetricComparator<>(metric);
    }
}
