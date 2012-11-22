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

import org.apache.commons.math.linear.MatrixIndexException;
import org.coode.distance.SparseMatrix;
import org.coode.distance.SparseMatrixFactory;
import org.coode.distance.TableDistance;
import org.coode.pair.Pair;
import org.coode.pair.filter.PairFilter;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;

public class ClusteringProximityMatrix<O> implements
        ProximityMatrix<Collection<? extends O>> {
    private final ProximityMatrix<Collection<? extends O>> delegate;
    private final ProximityMeasureFactory proximityMeasureFactory;

    // private final History<Collection<? extends O>> history;
    // private HistoryItemFactory<Collection<? extends O>> historyItemFactory;

    public static <P> ClusteringProximityMatrix<P> build(
            final ProximityMatrix<P> initialMatrix,
            final ProximityMeasureFactory proximityMeasureFactory,
            final PairFilter<Collection<? extends P>> filter,
            final Comparator<? super Pair<Collection<? extends P>>> comparator,
            final HistoryItemFactory<Collection<? extends P>> historyItemFactory) {
        return build(initialMatrix, proximityMeasureFactory, filter, comparator,
                new History<Collection<? extends P>>(), historyItemFactory);
    }

    public static <P> ClusteringProximityMatrix<P> build(
            final ProximityMatrix<P> initialMatrix,
            final ProximityMeasureFactory proximityMeasureFactory,
            final PairFilter<Collection<? extends P>> filter,
            final Comparator<? super Pair<Collection<? extends P>>> comparator,
            final History<Collection<? extends P>> history,
            final HistoryItemFactory<Collection<? extends P>> historyItemFactory) {
        if (history == null) {
            throw new NullPointerException("The history cannot be null");
        }
        Set<Collection<? extends P>> newObjects = new LinkedHashSet<Collection<? extends P>>();
        for (P object : initialMatrix.getObjects()) {
            newObjects.add(Collections.singletonList(object));
        }
        ProximityMatrix<Collection<? extends P>> newDelegate = new SimpleProximityMatrix<Collection<? extends P>>(
                newObjects, initialMatrix.getData(), filter, comparator);
        return new ClusteringProximityMatrix<P>(newDelegate, proximityMeasureFactory,
                history, historyItemFactory);
    }

    /** @param delegate */
    public ClusteringProximityMatrix(
            final ProximityMatrix<Collection<? extends O>> delegate,
            final ProximityMeasureFactory proximityMeasureFactory,
            final History<Collection<? extends O>> history,
            final HistoryItemFactory<Collection<? extends O>> historyItemFactory) {
        if (delegate == null) {
            throw new NullPointerException("The delegate matrix cannot be null");
        }
        // if (history == null) {
        // throw new NullPointerException("The history cannot be null");
        // }
        if (proximityMeasureFactory == null) {
            throw new NullPointerException("The proximity measure factory cannot be null");
        }
        // if (historyItemFactory == null) {
        // throw new
        // NullPointerException("The history item factory cannot be null");
        // }
        this.delegate = delegate;
        this.proximityMeasureFactory = proximityMeasureFactory;
        // this.history = history;
        // this.historyItemFactory = historyItemFactory;
    }

    public ProximityMeasureFactory getProximityMeasureFactory() {
        return this.proximityMeasureFactory;
    }

    public ClusteringProximityMatrix<O> agglomerate(
            final PairFilter<Collection<? extends O>> filter) {
        Collection<Collection<? extends O>> objects = this.getObjects();
        Pair<Collection<? extends O>> minimumDistancePair = this.getMinimumDistancePair();
        Collection<? extends O> a = minimumDistancePair.getFirst();
        Collection<? extends O> b = minimumDistancePair.getSecond();
        objects.remove(a);
        objects.remove(b);
        List<Collection<? extends O>> newObjects = new ArrayList<Collection<? extends O>>(
                objects);
        List<O> merger = new ArrayList<O>();
        merger.addAll(minimumDistancePair.getFirst());
        merger.addAll(minimumDistancePair.getSecond());
        newObjects.add(merger);
        int[] positions = new int[newObjects.size()];
        for (int index = 0; index < newObjects.size(); index++) {
            positions[index] = getRowIndex(newObjects.get(index));
        }
        SparseMatrix newDistances = SparseMatrixFactory.create(newObjects.size());
        int i = 0;
        final int size = newObjects.size();
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
                    Collection<? extends O> q = rowIndex != -1 ? aCollection
                            : anotherCollection;
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
        SimpleProximityMatrix<Collection<? extends O>> simpleProximityMatrix = new SimpleProximityMatrix<Collection<? extends O>>(
                newObjects, newDistances, filter, PairFilterBasedComparator.build(filter,
                        newObjects, new TableDistance<Collection<? extends O>>(
                                newObjects, newDistances)));
        // History<Collection<? extends O>> newHistory = this.getHistory();
        // HistoryItem<Collection<? extends O>> newItem =
        // this.getHistoryItemFactory()
        // .create(minimumDistancePair, newObjects);
        // newHistory.add(newItem);
        ClusteringProximityMatrix<O> toReturn = new ClusteringProximityMatrix<O>(
                simpleProximityMatrix, this.getProximityMeasureFactory(), null, null
        // newHistory,
        // this.getHistoryItemFactory()
        );
        return toReturn;
    }

    @Override
    public int[] getColumns(final Pair<Collection<? extends O>> pair) {
        return this.delegate.getRows(pair);
    }

    @Override
    public int[] getRows(final Pair<Collection<? extends O>> pair) {
        return this.getColumns(pair);
    }

    @Override
    public ClusteringProximityMatrix<O> reduce(
            final PairFilter<Collection<? extends O>> filter) {
        ProximityMatrix<Collection<? extends O>> reduced = this.delegate.reduce(filter);
        return new ClusteringProximityMatrix<O>(reduced,
                this.getProximityMeasureFactory(), null, null)
        // this.getHistory(),
        // this.getHistoryItemFactory())
        ;
    }

    /** @return
     * @see org.coode.proximitymatrix.ProximityMatrix#getObjects() */
    @Override
    public Collection<Collection<? extends O>> getObjects() {
        return this.delegate.getObjects();
    }

    /** @return
     * @see org.coode.proximitymatrix.ProximityMatrix#getMinimumDistancePair() */
    @Override
    public Pair<Collection<? extends O>> getMinimumDistancePair() {
        return this.delegate.getMinimumDistancePair();
    }

    /** @return
     * @see org.coode.proximitymatrix.ProximityMatrix#getMinimumDistance() */
    @Override
    public double getMinimumDistance() {
        return this.delegate.getMinimumDistance();
    }

    /** @param o
     * @return
     * @see org.coode.proximitymatrix.ProximityMatrix#getRowIndex(java.lang.Object) */
    @Override
    public int getRowIndex(final Collection<? extends O> o) {
        return this.delegate.getRowIndex(o);
    }

    /** @param o
     * @return
     * @see org.coode.proximitymatrix.ProximityMatrix#getColumnIndex(java.lang.Object) */
    @Override
    public int getColumnIndex(final Collection<? extends O> o) {
        return this.delegate.getColumnIndex(o);
    }

    /** @param anObject
     * @param anotherObject
     * @return
     * @see org.coode.proximitymatrix.ProximityMatrix#getDistance(java.lang.Object,
     *      java.lang.Object) */
    @Override
    public double getDistance(final Collection<? extends O> anObject,
            final Collection<? extends O> anotherObject) {
        return this.delegate.getDistance(anObject, anotherObject);
    }

    /** @return
     * @see org.apache.commons.math.linear.RealMatrix#getData() */
    @Override
    public SparseMatrix getData() {
        return this.delegate.getData();
    }

    /** @param row
     * @param column
     * @return
     * @throws MatrixIndexException
     * @see org.apache.commons.math.linear.RealMatrix#getEntry(int, int) */
    @Override
    public double getDistance(final int row, final int column)
            throws MatrixIndexException {
        return this.delegate.getData().get(row, column);
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    // /** @return the history */
    // public History<Collection<? extends O>> getHistory() {
    // try {
    // return this.history.clone();
    // } catch (CloneNotSupportedException e) {
    // return this.history;
    // }
    // }

    // /** @return the historyItemFactory */
    // public HistoryItemFactory<Collection<? extends O>>
    // getHistoryItemFactory() {
    // return this.historyItemFactory;
    // }
    //
    // /** @param historyItemFactory
    // * the historyItemFactory to set */
    // public void setHistoryItemFactory(
    // final HistoryItemFactory<Collection<? extends O>> historyItemFactory) {
    // this.historyItemFactory = historyItemFactory;
    // }
}
