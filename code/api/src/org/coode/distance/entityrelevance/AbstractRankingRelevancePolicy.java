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
package org.coode.distance.entityrelevance;

import java.util.HashMap;
import java.util.Map;

import org.coode.metrics.AbstractRanking;

/** @author eleni
 * @param <T>
 *            type */
public class AbstractRankingRelevancePolicy<T> implements RelevancePolicy<T> {
    private final AbstractRanking<T> ranking;
    private final double standardDeviation;
    private final double mean;
    private final Map<T, Boolean> cache = new HashMap<T, Boolean>();
    private final boolean anyRelenant;
    private final int sampleSize;
    protected final double upperLimit;
    protected final double lowerLimit;

    /*
     * From http://onlinestatbook.com/chapter8/mean.html 2 4.303 9.925 3 3.182
     * 5.841 4 2.776 4.604 5 2.571 4.032 8 2.306 3.355 10 2.228 3.169 20 2.086
     * 2.845 50 2.009 2.678 100 1.984 2.626
     */
    /** @param sample
     *            sample
     * @return zeta */
    public static double getZeta(int sample) {
        if (sample <= 2) {
            return 4.303d;
        }
        if (sample == 3) {
            return 3.182d;
        }
        if (sample == 4) {
            return 2.776d;
        }
        if (sample <= 6) {
            return 2.571d;
        }
        if (sample <= 9) {
            return 2.306d;
        }
        if (sample <= 15) {
            return 2.228d;
        }
        if (sample <= 35) {
            return 2.086d;
        }
        if (sample <= 75) {
            return 2.009d;
        }
        return 1.984d;
    }

    /** @param ranking
     *            ranking */
    private AbstractRankingRelevancePolicy(AbstractRanking<T> ranking) {
        if (ranking == null) {
            throw new NullPointerException("The ranking cannot be null");
        }
        this.ranking = ranking;
        standardDeviation = ranking.computeStandardDeviation();
        mean = ranking.computeMean();
        sampleSize = ranking.computeSampleSize();
        // zeta = getZeta(sampleSize);
        upperLimit = computeUpperLimit();
        lowerLimit = computeLowerLimit();
        anyRelenant = ranking.getTopValue() > getUpperLimit();
    }

    private double computeUpperLimit() {
        return getMean() + this.getZeta() * getStandardDeviation()
                / Math.sqrt(getSampleSize());
    }

    private double computeLowerLimit() {
        return getMean() - this.getZeta() * getStandardDeviation()
                / Math.sqrt(getSampleSize());
    }

    @Override
    public boolean isRelevant(T object) {
        boolean isCached = cache.containsKey(object);
        return isCached ? cache.get(object) : computeIsRelevant(object);
    }

    /** @param object
     *            object
     * @return true if relevant */
    public boolean computeIsRelevant(T object) {
        boolean isRelevant = !anyRelenant;
        if (!isRelevant) {
            double value = ranking.getMetric().getValue(object);
            double difference = value - getUpperLimit();
            isRelevant = difference > 0;
            cache.put(object, isRelevant);
        }
        return isRelevant;
    }

    /** @return the ranking */
    public AbstractRanking<T> getRanking() {
        return ranking;
    }

    /** @return the standardDeviation */
    public double getStandardDeviation() {
        return standardDeviation;
    }

    @Override
    public String toString() {
        return String
                .format("OWL Entity popularity Based relevance policy (Mean %f Standard deviation: %f)",
                        getMean(), getStandardDeviation());
    }

    /** @return the mean */
    public double getMean() {
        return mean;
    }

    /** @param ranking
     *            ranking
     * @return ranking relevance policy */
    public static <T> AbstractRankingRelevancePolicy<T>
            getAbstractRankingRelevancePolicy(AbstractRanking<T> ranking) {
        return new AbstractRankingRelevancePolicy<T>(ranking);
    }

    /** @return the sampleSize */
    public int getSampleSize() {
        return sampleSize;
    }

    /** @return the zeta */
    public double getZeta() {
        return getZeta(sampleSize);
    }

    /** @return the upperLimit */
    public double getUpperLimit() {
        return upperLimit;
    }

    /** @return confidence interval */
    public Interval getConfidenceInterval() {
        return new Interval() {
            @Override
            public double getUpperBound() {
                return upperLimit;
            }

            @Override
            public double getLowerBound() {
                return lowerLimit;
            }

            @Override
            public String toString() {
                return String.format("[%s, %s]", getLowerBound(), getUpperBound());
            }
        };
    }

    /** @return the lowerLimit */
    public double getLowerLimit() {
        return lowerLimit;
    }
}
