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
package atomicdecomposition.distance.entityrelevance;

import java.util.HashMap;
import java.util.Map;

import org.coode.distance.entityrelevance.Interval;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.metrics.AbstractRanking;
import org.semanticweb.owlapi.model.OWLEntity;

/** @author eleni */
public class AtomicDecompositionRankingRelevancePolicy implements
        RelevancePolicy<OWLEntity> {
    private final AbstractRanking<OWLEntity> ranking;
    private final double standardDeviation;
    private final double mean;
    private final Map<OWLEntity, Boolean> cache = new HashMap<OWLEntity, Boolean>();
    private final int sampleSize;
    protected final double upperLimit;
    protected final double lowerLimit;

    /** @param ranking
     *            ranking */
    private AtomicDecompositionRankingRelevancePolicy(AbstractRanking<OWLEntity> ranking) {
        if (ranking == null) {
            throw new NullPointerException("The ranking cannot be null");
        }
        this.ranking = ranking;
        standardDeviation = ranking.computeStandardDeviation();
        mean = computeMean();
        sampleSize = computeSampleSize();
        // zeta = getZeta(sampleSize);
        upperLimit = computeUpperLimit();
        lowerLimit = computeLowerLimit();
    }

    private double computeUpperLimit() {
        return getMean() + this.getZeta() * getStandardDeviation()
                / Math.sqrt(getSampleSize());
    }

    private double computeLowerLimit() {
        return getMean() - this.getZeta() * getStandardDeviation()
                / Math.sqrt(getSampleSize());
    }

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

    @Override
    public boolean isRelevant(OWLEntity object) {
        boolean isCached = cache.containsKey(object);
        return isCached ? cache.get(object) : computeIsRelevant(object);
    }

    /** @param object
     *            object
     * @return true if relevant */
    public boolean computeIsRelevant(OWLEntity object) {
        // boolean isRelevant = !this.anyRelenant;
        // if (!isRelevant) {
        double value = ranking.getMetric().getValue(object);
        double difference = getMean() - value;
        // double difference = this.getLowerLimit() - value;
        if (difference > 0) {
            cache.put(object, Boolean.TRUE);
            return true;
        } else {
            cache.put(object, Boolean.FALSE);
            return false;
        }
        // return isRelevant;
    }

    private int computeSampleSize() {
        return getRanking().computeSampleSize();
    }

    private double computeMean() {
        return getRanking().getAverageValue();
    }

    /** @return the ranking */
    public AbstractRanking<OWLEntity> getRanking() {
        return ranking;
    }

    /** @return the standardDeviation */
    public double getStandardDeviation() {
        return standardDeviation;
    }

    @Override
    public String toString() {
        return String
                .format("OWL Entity atomic decomposition based relevance policy (Mean %f Standard deviation: %f)",
                        getMean(), getStandardDeviation());
    }

    /** @return the mean */
    public double getMean() {
        return mean;
    }

    /** @param ranking
     *            ranking
     * @return relevance policy */
    public static AtomicDecompositionRankingRelevancePolicy
            getAbstractRankingRelevancePolicy(AbstractRanking<OWLEntity> ranking) {
        return new AtomicDecompositionRankingRelevancePolicy(ranking);
    }

    /** @return the sampleSize */
    public double getSampleSize() {
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

    /** @return interval */
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
