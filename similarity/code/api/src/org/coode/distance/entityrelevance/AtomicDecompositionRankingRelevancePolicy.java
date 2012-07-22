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
import java.util.List;
import java.util.Map;

import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.coode.metrics.AbstractRanking;
import org.coode.metrics.RankingSlot;

public final class AtomicDecompositionRankingRelevancePolicy<P> implements
        RelevancePolicy<P> {
    private final AbstractRanking<P, Double> ranking;
    private final double standardDeviation;
    private final double mean;
    private final Map<P, Boolean> cache = new HashMap<P, Boolean>();
    // private final boolean anyRelenant;
    private final static Map<Integer, Double> zetaTable95 = new HashMap<Integer, Double>();
    private final double zeta;
    private final int sampleSize;
    private final double upperLimit;
    private final double lowerLimit;
    /*
     * From http://onlinestatbook.com/chapter8/mean.html 2 4.303 9.925 3 3.182
     * 5.841 4 2.776 4.604 5 2.571 4.032 8 2.306 3.355 10 2.228 3.169 20 2.086
     * 2.845 50 2.009 2.678 100 1.984 2.626
     */
    static {
        zetaTable95.put(2, 4.303d);
        zetaTable95.put(3, 3.182d);
        zetaTable95.put(4, 2.776d);
        zetaTable95.put(5, 2.571d);
        zetaTable95.put(8, 2.306d);
        zetaTable95.put(10, 2.228d);
        zetaTable95.put(20, 2.086d);
        zetaTable95.put(50, 2.009d);
        zetaTable95.put(100, 1.984d);
    }

    /** @param ranking */
    private AtomicDecompositionRankingRelevancePolicy(
            final AbstractRanking<P, Double> ranking) {
        if (ranking == null) {
            throw new NullPointerException("The ranking cannot be null");
        }
        this.ranking = ranking;
        this.standardDeviation = this.computeStandardDeviation();
        this.mean = this.computeMean();
        this.sampleSize = this.computeSampleSize();
        this.zeta = AtomicDecompositionRankingRelevancePolicy.getZeta(sampleSize);
        this.upperLimit = this.computeUpperLimit();
        this.lowerLimit = this.computeLowerLimit();
        // this.anyRelenant = ranking.getTopValue() < this.getLowerLimit();
    }

    private double computeUpperLimit() {
        return this.getMean() + this.getZeta() * this.getStandardDeviation()
                / Math.sqrt(this.getSampleSize());
    }

    private double computeLowerLimit() {
        return this.getMean() - this.getZeta() * this.getStandardDeviation()
                / Math.sqrt(this.getSampleSize());
    }

    public static double getZeta(final int sample) {
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

    public boolean isRelevant(final P object) {
        boolean isCached = this.cache.containsKey(object);
        return isCached ? this.cache.get(object) : this.computeIsRelevant(object);
    }

    /** @param object
     * @return */
    public boolean computeIsRelevant(final P object) {
        // boolean isRelevant = !this.anyRelenant;
        // if (!isRelevant) {
        double value = ranking.getMetric().getValue(object);
        double difference = this.getMean() - value;
        // double difference = this.getLowerLimit() - value;
        if (difference > 0) {
            this.cache.put(object, true);
            return true;
        } else {
            this.cache.put(object, false);
            return false;
        }
        // return isRelevant;
    }

    private double computeStandardDeviation() {
        StandardDeviation sd = new StandardDeviation();
        List<Double> rankingValues = this.getRanking().getValuesList();
        final int size = rankingValues.size();
        for (int i = 0; i < size; i++) {
            sd.increment(rankingValues.get(i));
        }
        return sd.getResult();
    }

    private final int computeSampleSize() {
        int size = 0;
        for (RankingSlot<P, Double> slot : this.getRanking().getUnorderedRanking()) {
            size += slot.getMembersSize();
        }
        return size;
    }

    private final double computeMean() {
        return this.getRanking().getAverageValue();
    }

    /** @return the ranking */
    public AbstractRanking<P, Double> getRanking() {
        return this.ranking;
    }

    /** @return the standardDeviation */
    public double getStandardDeviation() {
        return this.standardDeviation;
    }

    @Override
    public String toString() {
        return String
                .format("OWL Entity atomic decomposition based relevance policy (Mean %f Standard deviation: %f)",
                        this.getMean(), this.getStandardDeviation());
    }

    /** @return the mean */
    public double getMean() {
        return this.mean;
    }

    public static <O> AtomicDecompositionRankingRelevancePolicy<O>
            getAbstractRankingRelevancePolicy(final AbstractRanking<O, Double> ranking) {
        return new AtomicDecompositionRankingRelevancePolicy<O>(ranking);
    }

    /** @return the sampleSize */
    public double getSampleSize() {
        return this.sampleSize;
    }

    /** @return the zeta */
    public double getZeta() {
        return this.zeta;
    }

    /** @return the upperLimit */
    public double getUpperLimit() {
        return this.upperLimit;
    }

    public Interval<Double> getConfidenceInterval() {
        return new Interval<Double>() {
            public Double getUpperBound() {
                return AtomicDecompositionRankingRelevancePolicy.this.upperLimit;
            }

            public Double getLowerBound() {
                return AtomicDecompositionRankingRelevancePolicy.this.lowerLimit;
            }

            @Override
            public String toString() {
                return String.format("[%s, %s]", this.getLowerBound(),
                        this.getUpperBound());
            }
        };
    }

    /** @return the lowerLimit */
    public double getLowerLimit() {
        return this.lowerLimit;
    }
}
