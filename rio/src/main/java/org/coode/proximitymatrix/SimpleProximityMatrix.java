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
package org.coode.proximitymatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.distance.Distance;
import org.coode.distance.SparseMatrix;
import org.coode.distance.SparseMatrixFactory;
import org.coode.pair.Pair;
import org.coode.pair.SimplePair;
import org.coode.pair.filter.PairFilter;

/**
 * @author eleni
 * @param <O> type
 */
public class SimpleProximityMatrix<O> implements ProximityMatrix<O> {
    private final SparseMatrix delegate;
    private final List<O> objects = new ArrayList<>();
    private final Comparator<? super Pair<O>> comparator;
    private double minimumDistance = Double.MAX_VALUE;
    private Pair<O> minimumDistancePair = null;
    private final PairFilter<O> filter;
    private final Map<O, Integer> objectIndex;

    /**
     * @param objects objects
     * @param distances distances
     * @param filter filter
     * @param comparator comparator
     */
    public SimpleProximityMatrix(Collection<? extends O> objects, SparseMatrix distances,
        PairFilter<O> filter, Comparator<? super Pair<O>> comparator) {
        if (objects == null) {
            throw new NullPointerException("The object colleciton cannot be null");
        }
        if (objects.isEmpty()) {
            throw new IllegalArgumentException("The object collection cannot be empty");
        }
        if (distances == null) {
            throw new NullPointerException("The distances cannot be null");
        }
        if (distances.length() != objects.size()) {
            throw new IllegalArgumentException(
                String.format("The object collection size %d != distances dimension %d",
                    Integer.valueOf(objects.size()), Integer.valueOf(distances.length())));
        }
        if (filter == null) {
            throw new NullPointerException("The filter cannot be null");
        }
        if (comparator == null) {
            throw new NullPointerException("The comparator cannot be null");
        }
        this.comparator = comparator;
        this.objects.addAll(objects);
        this.filter = filter;
        this.objectIndex = new HashMap<>();
        int size = this.objects.size();
        for (int i = 0; i < size; i++) {
            O object = this.objects.get(i);
            this.objectIndex.put(object, Integer.valueOf(i));
            for (int j = 0; j < size; j++) {
                O anotherObject = this.objects.get(j);
                double distanceValue = distances.get(i, j);
                SimplePair<O> pair = new SimplePair<>(object, anotherObject);
                if (anotherObject != object && filter.accept(object, anotherObject)
                    && (distanceValue < this.minimumDistance
                        || distanceValue == this.minimumDistance && this.minimumDistancePair != null
                            && this.comparator.compare(pair, this.minimumDistancePair) < 0)) {
                    this.minimumDistance = distanceValue;
                    this.minimumDistancePair = pair;
                }
            }
        }
        this.delegate = SparseMatrixFactory.create(distances);
    }

    /**
     * @param objects objects
     * @param distance distance
     */
    public SimpleProximityMatrix(Collection<? extends O> objects, Distance<O> distance) {
        this(objects, distance, (PairFilter<O>) (first, second) -> true,
            (Comparator<Pair<O>>) (arg0, arg1) -> arg0.hashCode() - arg1.hashCode());
    }

    /**
     * @param objects objects
     * @param distance distance
     * @param filter filter
     * @param comparator comparator
     */
    public SimpleProximityMatrix(Collection<? extends O> objects, Distance<O> distance,
        PairFilter<O> filter, Comparator<? super Pair<O>> comparator) {
        if (objects == null) {
            throw new NullPointerException("The object colleciton cannot be null");
        }
        if (objects.isEmpty()) {
            throw new IllegalArgumentException("The object collection cannot be empty");
        }
        if (distance == null) {
            throw new NullPointerException("The distance cannot be null");
        }
        if (filter == null) {
            throw new NullPointerException("The filter cannot be null");
        }
        if (comparator == null) {
            throw new NullPointerException("The comparator cannot be null");
        }
        this.comparator = comparator;
        this.objects.addAll(objects);
        this.filter = filter;
        SparseMatrix distances = SparseMatrixFactory.create(objects.size());
        int i = 0;
        this.objectIndex = new HashMap<>();
        for (O object : objects) {
            int j = 0;
            this.objectIndex.put(object, Integer.valueOf(i));
            for (O anotherObject : objects) {
                double distanceValue = distance.getDistance(object, anotherObject);
                distances.set(i, j, distanceValue);
                SimplePair<O> pair = new SimplePair<>(object, anotherObject);
                if (!anotherObject.equals(object) && filter.accept(object, anotherObject)
                    && (distanceValue < this.minimumDistance
                        || distanceValue == this.minimumDistance
                            && this.getMinimumDistancePair() != null
                            && this.comparator.compare(pair, this.minimumDistancePair) < 0)) {
                    this.minimumDistance = distanceValue;
                    this.minimumDistancePair = pair;
                }
                j++;
            }
            i++;
        }
        this.delegate = SparseMatrixFactory.create(distances);
    }

    @Override
    public ProximityMatrix<O> reduce(PairFilter<O> f) {
        Set<O> reducedObjects = new HashSet<>();
        Iterator<O> iterator = this.getObjects().iterator();
        boolean found = false;
        while (iterator.hasNext()) {
            Iterator<O> anotherIterator = this.getObjects().iterator();
            O object = iterator.next();
            while (!found && anotherIterator.hasNext()) {
                O anotherObject = anotherIterator.next();
                found = anotherObject != object && f.accept(object, anotherObject);
            }
            if (found) {
                reducedObjects.add(object);
            }
            found = false;
        }
        if (!reducedObjects.isEmpty()) {
            SparseMatrix newDistances = SparseMatrixFactory.create(reducedObjects.size());
            List<O> list = new ArrayList<>(reducedObjects);
            int size = list.size();
            for (int i = 0; i < size; i++) {
                O a = list.get(i);
                for (int j = i + 1; j < size; j++) {
                    O b = list.get(j);
                    newDistances.set(i, j, a == b ? 0 : this.getDistance(a, b));
                }
            }
            return new SimpleProximityMatrix<>(reducedObjects, newDistances, this.getFilter(),
                comparator);
        }
        // the proximity matrix cannot be reduced any more so return the same
        // instance
        else {
            return this;
        }
    }

    private static Integer NULL = Integer.valueOf(-1);

    @Override
    public int getRowIndex(O o) {
        return this.objectIndex.getOrDefault(o, NULL).intValue();
    }

    @Override
    public int getColumnIndex(O o) {
        return this.objectIndex.getOrDefault(o, NULL).intValue();
    }

    @Override
    public Pair<O> getMinimumDistancePair() {
        return this.minimumDistancePair == null ? null : new SimplePair<>(this.minimumDistancePair);
    }

    @Override
    public double getDistance(O anObject, O anotherObject) {
        int row = this.getRowIndex(anObject);
        if (row == -1) {
            StringBuilder b = new StringBuilder();
            for (O e : objectIndex.keySet()) {
                b.append(e.toString()).append(" [type ").append(e.getClass().getName())
                    .append("] ");
            }
            throw new IllegalArgumentException(
                String.format("The object %s [type %s] is not contained in this matrix %s",
                    anObject, anObject.getClass().getName(), b.toString()));
        }
        int column = this.getColumnIndex(anotherObject);
        if (column == -1) {
            StringBuilder b = new StringBuilder();
            for (O e : objectIndex.keySet()) {
                b.append(e.toString()).append(" [type ").append(e.getClass().getName())
                    .append("] ");
            }
            throw new IllegalArgumentException(
                String.format("The object %s [type %s] is not contained in this matrix %s",
                    anotherObject, anotherObject.getClass().getName(), b.toString()));
        }
        return this.getDistance(row, column);
    }

    @Override
    public double getMinimumDistance() {
        return this.minimumDistance;
    }

    @Override
    public int[] getColumns(Pair<O> pair) {
        int[] cols = new int[] {getColumnIndex(pair.getFirst()), getColumnIndex(pair.getSecond())};
        return cols;
    }

    @Override
    public int[] getRows(Pair<O> pair) {
        int[] rows = new int[] {getRowIndex(pair.getFirst()), getRowIndex(pair.getSecond())};
        return rows;
    }

    @Override
    public SparseMatrix getData() {
        return this.delegate;
    }

    @Override
    public double getDistance(int row, int column) {
        return this.delegate.get(row, column);
    }

    /** @return the objects */
    @Override
    public Collection<O> getObjects() {
        return this.objects;
    }

    /** @return the filter */
    public PairFilter<O> getFilter() {
        return this.filter;
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }
}
