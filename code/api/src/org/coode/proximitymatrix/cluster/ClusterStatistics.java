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
package org.coode.proximitymatrix.cluster;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.coode.pair.Pair;
import org.coode.pair.SimplePair;

/** @author Luigi Iannone
 * @param <O>
 *            type */
public class ClusterStatistics<O> {
    private final Cluster<O> cluster;

    /** @param cluster
     *            cluster */
    private ClusterStatistics(final Cluster<O> cluster) {
        if (cluster == null) {
            throw new NullPointerException("The cluster cannot be null");
        }
        this.cluster = cluster;
    }

    /** @return the cluster */
    public Cluster<O> getCluster() {
        return this.cluster;
    }

    /** @return min internal distance */
    public double getMinInternalDistance() {
        double min = this.getCluster().size() <= 1 ? 0 : Double.MAX_VALUE;
        for (O object : this.getCluster()) {
            for (O anotherObject : this.getCluster()) {
                if (object != anotherObject) {
                    double distance = this.getCluster().getProximityMatrix()
                            .getDistance(object, anotherObject);
                    min = min < distance ? min : distance;
                }
            }
        }
        return min;
    }

    /** @return min external distance */
    public double getMinExternalDistance() {
        double min = this.getCluster().size() <= 1 ? 0 : Double.MAX_VALUE;
        Set<O> externalObjects = this.getExternalObjects();
        for (O object : this.getCluster()) {
            for (O anotherObject : externalObjects) {
                double distance = this.getCluster().getProximityMatrix()
                        .getDistance(object, anotherObject);
                min = min < distance ? min : distance;
            }
        }
        return min;
    }

    private Set<O> getExternalObjects() {
        Set<O> externalObjects = new LinkedHashSet<O>(this.getCluster()
                .getProximityMatrix().getObjects());
        externalObjects.removeAll(this.getCluster());
        return externalObjects;
    }

    /** @return max internal distance */
    public double getMaxInternalDistance() {
        double max = 0;
        for (O object : this.getCluster()) {
            for (O anotherObject : this.getCluster()) {
                if (object != anotherObject) {
                    double distance = this.getCluster().getProximityMatrix()
                            .getDistance(object, anotherObject);
                    max = max > distance ? max : distance;
                }
            }
        }
        return max;
    }

    /** @return max external distance */
    public double getMaxExternalDistance() {
        double max = 0;
        for (O object : this.getCluster()) {
            for (O anotherObject : this.getExternalObjects()) {
                double distance = this.getCluster().getProximityMatrix()
                        .getDistance(object, anotherObject);
                max = max > distance ? max : distance;
            }
        }
        return max;
    }

    /** @return average internal distance */
    public double getAverageInternalDistance() {
        double sum = 0;
        Set<Pair<O>> pairs = new HashSet<Pair<O>>();
        for (O object : this.getCluster()) {
            for (O anotherObject : this.getCluster()) {
                SimplePair<O> pair = SimplePair.build(object, anotherObject);
                if (object != anotherObject && !pairs.contains(pair)) {
                    double distance = this.getCluster().getProximityMatrix()
                            .getDistance(object, anotherObject);
                    sum += distance;
                    pairs.add(pair);
                }
            }
        }
        return this.getCluster().size() <= 1 ? 0 : sum / pairs.size();
    }

    /** @return average external distance */
    public double getAverageExternalDistance() {
        double sum = 0;
        Set<Pair<O>> pairs = new HashSet<Pair<O>>();
        for (O object : this.getCluster()) {
            for (O anotherObject : this.getExternalObjects()) {
                SimplePair<O> pair = SimplePair.build(object, anotherObject);
                if (!pairs.contains(pair)) {
                    double distance = this.getCluster().getProximityMatrix()
                            .getDistance(object, anotherObject);
                    sum += distance;
                    pairs.add(pair);
                }
            }
        }
        return pairs.size() <= 1 ? 0 : sum / pairs.size();
    }

    /** @param cluster
     *            cluster
     * @return ststistics */
    public static <P> ClusterStatistics<P> buildStatistics(final Cluster<P> cluster) {
        return new ClusterStatistics<P>(cluster);
    }
}
