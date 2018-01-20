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

import java.util.Collection;

import org.coode.distance.SparseMatrix;
import org.coode.pair.Pair;
import org.coode.pair.filter.PairFilter;

/**
 * @author eleni
 * @param <O> type
 */
public interface ProximityMatrix<O> {
    /** @return objects */
    Collection<O> getObjects();

    /** @return minimum distance pair */
    public Pair<O> getMinimumDistancePair();

    /** @return min distance */
    public double getMinimumDistance();

    /**
     * @param o o
     * @return row index
     */
    public int getRowIndex(O o);

    /**
     * @param o o
     * @return column index
     */
    public int getColumnIndex(O o);

    /**
     * @param anObject anObject
     * @param anotherObject anotherObject
     * @return distance
     */
    public double getDistance(O anObject, O anotherObject);

    /**
     * @param anObject anObject
     * @param anotherObject anotherObject
     * @return distance
     */
    public double getDistance(int anObject, int anotherObject);

    /**
     * @param pair pair
     * @return rows
     */
    public int[] getRows(Pair<O> pair);

    /**
     * @param pair pair
     * @return columns
     */
    public int[] getColumns(Pair<O> pair);

    /**
     * @param filter filter
     * @return filter
     */
    public ProximityMatrix<O> reduce(PairFilter<O> filter);

    /** @return data */
    public SparseMatrix getData();
}
