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
package org.coode.pair;

/** @author Luigi Iannone
 * @param <O>
 *            type */
public class SimplePair<O> implements Pair<O> {
    private final O first;
    private final O second;

    /** @param first
     *            first
     * @param second
     *            second */
    public SimplePair(O first, O second) {
        if (first == null) {
            throw new IllegalArgumentException("first cannot be null");
        }
        if (second == null) {
            throw new IllegalArgumentException("second cannot be null");
        }
        this.first = first;
        this.second = second;
    }

    /** @param pair
     *            pair */
    public SimplePair(Pair<O> pair) {
        this(pair.getFirst(), pair.getSecond());
    }

    @Override
    public boolean contains(Object o) {
        return first.equals(o) || second.equals(o);
    }

    /** @param object
     *            object
     * @param anotherObject
     *            anotherObject
     * @param <P>
     *            type
     * @return pair */
    public static <P> SimplePair<P> build(P object, P anotherObject) {
        return new SimplePair<P>(object, anotherObject);
    }

    @Override
    public String toString() {
        return String.format("%s, %s ", this.first, this.second);
    }

    @Override
    public O getFirst() {
        return this.first;
    }

    @Override
    public O getSecond() {
        return this.second;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.first == null ? 0 : this.first.hashCode());
        result = prime * result + (this.second == null ? 0 : this.second.hashCode());
        return result;
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
        SimplePair<?> other = (SimplePair<?>) obj;
        return this.first.equals(other.first) && this.second.equals(other.second)
                || this.second.equals(other.first) && this.first.equals(other.second);
    }
}
