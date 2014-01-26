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
package org.coode.distance.wrapping;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.coode.distance.Distance;

/** @author eleni
 * @param <O>
 *            type */
public class DistanceTableObject<O> {
    private final O object;
    private final int index;
    private final Distance<O> distance;

    /** @param object
     *            object
     * @param index
     *            index
     * @param distance
     *            distance */
    public DistanceTableObject(O object, int index, Distance<O> distance) {
        if (object == null) {
            throw new NullPointerException("The object cannot be null");
        }
        if (distance == null) {
            throw new NullPointerException("The distance cannot be null");
        }
        this.object = object;
        this.index = index;
        this.distance = distance;
    }

    /** @return the object */
    public O getObject() {
        return this.object;
    }

    /** @return the distance */
    public Distance<O> getDistance() {
        return this.distance;
    }

    @Override
    public int hashCode() {
        return this.index;
    }

    /** @param distance
     *            distance
     * @param objects
     *            objects
     * @return distance table */
    public static <P> Set<DistanceTableObject<P>> createDistanceTableObjectSet(
            Distance<P> distance, Collection<? extends P> objects) {
        Set<DistanceTableObject<P>> toReturn = new LinkedHashSet<DistanceTableObject<P>>();
        int i = 0;
        for (P p : objects) {
            toReturn.add(new DistanceTableObject<P>(p, i, distance));
            i++;
        }
        return toReturn;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        DistanceTableObject<?> other = (DistanceTableObject<?>) obj;
        if (this.distance == null) {
            if (other.distance != null) {
                return false;
            }
        } else if (!this.distance.equals(other.distance)) {
            return false;
        }
        if (this.object == null) {
            if (other.object != null) {
                return false;
            }
        } else if (!this.object.equals(other.object)) {
            return false;
        }
        if (this.index != other.index) {
            return false;
        }
        return true;
    }

    /** @return the index */
    public int getIndex() {
        return this.index;
    }
}
