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
package org.coode.proximitymatrix.cluster;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.coode.proximitymatrix.ProximityMatrix;

/** @author eleni
 * @param <O>
 *            type */
public class SimpleCluster<O> implements Cluster<O> {
    private final Set<O> delegate = new HashSet<O>();
    private final ProximityMatrix<O> proximityMatrix;

    /** @param members
     *            members
     * @param proximityMatrix
     *            proximityMatrix */
    public SimpleCluster(Collection<? extends O> members,
            ProximityMatrix<O> proximityMatrix) {
        if (members == null) {
            throw new NullPointerException("The members' collection cannot be null");
        }
        if (proximityMatrix == null) {
            throw new NullPointerException("The proximity matrix cannot be null");
        }
        if (!proximityMatrix.getObjects().containsAll(members)) {
            throw new IllegalArgumentException(
                    "The proximity matrix does not contain the necessary information about all the cluster members");
        }
        this.proximityMatrix = proximityMatrix;
        this.delegate.addAll(members);
    }

    @Override
    public boolean add(O e) {
        return this.delegate.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends O> c) {
        return this.delegate.addAll(c);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.delegate.containsAll(c);
    }

    @Override
    public boolean equals(Object o) {
        return this.delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public Iterator<O> iterator() {
        return this.delegate.iterator();
    }

    @Override
    public boolean remove(Object o) {
        return this.delegate.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.delegate.retainAll(c);
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.delegate.toArray(a);
    }

    /** @return the proximityMatrix */
    @Override
    public ProximityMatrix<O> getProximityMatrix() {
        return this.proximityMatrix;
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }
}
