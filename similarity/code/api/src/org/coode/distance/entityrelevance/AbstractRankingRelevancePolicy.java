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

import org.coode.metrics.AbstractRanking;
import org.coode.metrics.RankingSlot;
import org.semanticweb.owlapi.model.OWLEntity;

public final class AbstractRankingRelevancePolicy implements RelevancePolicy {
    private final AbstractRanking ranking;
    private final double standardDeviation;
    private final double mean;
    private final Map<OWLEntity, Boolean> cache = new HashMap<OWLEntity, Boolean>();
    private final boolean anyRelenant;
    // private final static Map<Integer, Double> zetaTable95 = new
    // HashMap<Integer, Double>();
    // private final double zeta;
    private final int sampleSize;
    final double upperLimit;
    final double lowerLimit;

    /*
     * From http://onlinestatbook.com/chapter8/mean.html 2 4.303 9.925 3 3.182
     * 5.841 4 2.776 4.604 5 2.571 4.032 8 2.306 3.355 10 2.228 3.169 20 2.086
     * 2.845 50 2.009 2.678 100 1.984 2.626
     */
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

    /** @param ranking */
    private AbstractRankingRelevancePolicy(final AbstractRanking ranking) {
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
    public boolean isRelevant(OWLEntity object) {
        boolean isCached = cache.containsKey(object);
        return isCached ? cache.get(object) : computeIsRelevant(object);
    }

    /** @param object
     * @return */
    public boolean computeIsRelevant(OWLEntity object) {
        boolean isRelevant = !anyRelenant;
        if (!isRelevant) {
            double value = ranking.getMetric().getValue(object);
            double difference = value - getUpperLimit();
            isRelevant = difference > 0;
            cache.put(object, isRelevant);
        }
        return isRelevant;
    }

    private final int computeSampleSize(final List<RankingSlot<OWLEntity>> list) {
        int size = 0;
        final int listsize = list.size();
        for (int i = 0; i < listsize; i++) {
            size += list.get(i).getMembersSize();
        }
        return size;
    }

    private final double computeMean(final List<RankingSlot<OWLEntity>> list) {
        final int size = list.size();
        // double mean = 0;
        int counter = 0;
        double mean = 0;
        for (int i = 0; i < size; i++) {
            RankingSlot<OWLEntity> slot = list.get(i);
            double value = slot.getValue();
            for (int j = 0; j < slot.getMembersSize(); j++) {
                counter++;
                mean += value;
                // mean = mean + (value - mean) / counter;
            }
        }
        if (counter > 0) {
            return mean / counter;
        }
        return 0;
    }

    /** @return the ranking */
    public AbstractRanking getRanking() {
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

    public static AbstractRankingRelevancePolicy
            getAbstractRankingRelevancePolicy(final AbstractRanking ranking) {
        return new AbstractRankingRelevancePolicy(ranking);
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
                return String.format("[%s, %s]", getLowerBound(),
                        getUpperBound());
            }
        };
    }

    /** @return the lowerLimit */
    public double getLowerLimit() {
        return lowerLimit;
    }
}
