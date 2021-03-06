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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.coode.distance.SparseMatrix;
import org.coode.distance.SparseMatrixFactory;
import org.coode.distance.TableDistance;
import org.coode.pair.Pair;
import org.coode.pair.filter.PairFilter;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;

/**
 * @author eleni
 * @param <O> type
 */
public class ClusteringProximityMatrix<O> implements ProximityMatrix<Collection<? extends O>> {
    private final ProximityMatrix<Collection<? extends O>> delegate;
    private final ProximityMeasureFactory proximityMeasureFactory;

    /**
     * @param initialMatrix initialMatrix
     * @param proximityMeasureFactory proximityMeasureFactory
     * @param filter filter
     * @param comparator comparator
     * @param <P> type
     * @return clustering proximity matrix
     */
    public static <P> ClusteringProximityMatrix<P> build(ProximityMatrix<P> initialMatrix,
        ProximityMeasureFactory proximityMeasureFactory, PairFilter<Collection<? extends P>> filter,
        Comparator<? super Pair<Collection<? extends P>>> comparator) {
        Set<Collection<? extends P>> newObjects = new LinkedHashSet<>();
        for (P object : initialMatrix.getObjects()) {
            newObjects.add(Collections.singletonList(object));
        }
        ProximityMatrix<Collection<? extends P>> newDelegate =
            new SimpleProximityMatrix<>(newObjects, initialMatrix.getData(), filter, comparator);
        return new ClusteringProximityMatrix<>(newDelegate, proximityMeasureFactory);
    }

    /**
     * @param delegate delegate
     * @param proximityMeasureFactory proximityMeasureFactory
     */
    public ClusteringProximityMatrix(ProximityMatrix<Collection<? extends O>> delegate,
        ProximityMeasureFactory proximityMeasureFactory) {
        if (delegate == null) {
            throw new NullPointerException("The delegate matrix cannot be null");
        }
        if (proximityMeasureFactory == null) {
            throw new NullPointerException("The proximity measure factory cannot be null");
        }
        this.delegate = delegate;
        this.proximityMeasureFactory = proximityMeasureFactory;
    }

    /** @return proximity measure factory */
    public ProximityMeasureFactory getProximityMeasureFactory() {
        return this.proximityMeasureFactory;
    }

    /**
     * @param filter filter
     * @return clustering proximity matrix
     */
    public ClusteringProximityMatrix<O> agglomerate(PairFilter<Collection<? extends O>> filter) {
        Collection<Collection<? extends O>> objects = this.getObjects();
        Pair<Collection<? extends O>> minimumDistancePair = this.getMinimumDistancePair();
        Collection<? extends O> a = minimumDistancePair.getFirst();
        Collection<? extends O> b = minimumDistancePair.getSecond();
        objects.remove(a);
        objects.remove(b);
        List<Collection<? extends O>> newObjects = new ArrayList<>(objects);
        List<O> merger = new ArrayList<>();
        merger.addAll(minimumDistancePair.getFirst());
        merger.addAll(minimumDistancePair.getSecond());
        newObjects.add(merger);
        int[] positions = new int[newObjects.size()];
        for (int index = 0; index < newObjects.size(); index++) {
            positions[index] = getRowIndex(newObjects.get(index));
        }
        SparseMatrix newDistances = SparseMatrixFactory.create(newObjects.size());
        int i = 0;
        int size = newObjects.size();
        for (int index = 0; index < size; index++) {
            Collection<? extends O> aCollection = newObjects.get(index);
            int rowIndex = aCollection == merger ? -1 : positions[index];// this.getRowIndex(aCollection);
            newDistances.set(i, i, 0D);
            for (int j = i + 1; j < size; j++) {
                Collection<? extends O> anotherCollection = newObjects.get(j);
                int columnIndex = anotherCollection == merger ? -1 : positions[j];// this
                                                                                  // .getColumnIndex(anotherCollection);
                // System.out.println("ClusteringProximityMatrix.agglomerate() "
                // + i + " "
                // + rowIndex + " " + j + " " + columnIndex);
                if (rowIndex != -1 && columnIndex != -1) {
                    newDistances.set(i, j, this.getDistance(rowIndex, columnIndex));
                } else {
                    // Apply the formula
                    Collection<? extends O> q = rowIndex != -1 ? aCollection : anotherCollection;
                    double distanceAB = this.getDistance(a, b);
                    double distanceAQ = this.getDistance(a, q);
                    double distanceBQ = this.getDistance(b, q);
                    double newProximity = this.getProximityMeasureFactory()
                        .getProximityMeasure(a.size(), b.size(), q.size())
                        .distance(distanceAQ, distanceBQ, distanceAB);
                    newDistances.set(i, j, newProximity);
                }
            }
            i++;
        }
        SimpleProximityMatrix<Collection<? extends O>> simpleProximityMatrix =
            new SimpleProximityMatrix<>(newObjects, newDistances, filter, PairFilterBasedComparator
                .build(filter, newObjects, new TableDistance<>(newObjects, newDistances)));
        // History<Collection<? extends O>> newHistory = this.getHistory();
        // HistoryItem<Collection<? extends O>> newItem =
        // this.getHistoryItemFactory()
        // .create(minimumDistancePair, newObjects);
        // newHistory.add(newItem);
        ClusteringProximityMatrix<O> toReturn = new ClusteringProximityMatrix<>(
            simpleProximityMatrix, this.getProximityMeasureFactory());
        return toReturn;
    }

    @Override
    public int[] getColumns(Pair<Collection<? extends O>> pair) {
        return this.delegate.getRows(pair);
    }

    @Override
    public int[] getRows(Pair<Collection<? extends O>> pair) {
        return this.getColumns(pair);
    }

    @Override
    public ClusteringProximityMatrix<O> reduce(PairFilter<Collection<? extends O>> filter) {
        ProximityMatrix<Collection<? extends O>> reduced = this.delegate.reduce(filter);
        return new ClusteringProximityMatrix<>(reduced, this.getProximityMeasureFactory());
    }

    @Override
    public Collection<Collection<? extends O>> getObjects() {
        return this.delegate.getObjects();
    }

    @Override
    public Pair<Collection<? extends O>> getMinimumDistancePair() {
        return this.delegate.getMinimumDistancePair();
    }

    @Override
    public double getMinimumDistance() {
        return this.delegate.getMinimumDistance();
    }

    @Override
    public int getRowIndex(Collection<? extends O> o) {
        return this.delegate.getRowIndex(o);
    }

    @Override
    public int getColumnIndex(Collection<? extends O> o) {
        return this.delegate.getColumnIndex(o);
    }

    @Override
    public double getDistance(Collection<? extends O> anObject,
        Collection<? extends O> anotherObject) {
        return this.delegate.getDistance(anObject, anotherObject);
    }

    @Override
    public SparseMatrix getData() {
        return this.delegate.getData();
    }

    @Override
    public double getDistance(int row, int column) {
        return this.delegate.getData().get(row, column);
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }
}
