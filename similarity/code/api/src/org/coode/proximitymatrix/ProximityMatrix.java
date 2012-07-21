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

import org.coode.metrics.SparseMatrix;
import org.coode.pair.Pair;
import org.coode.pair.filter.PairFilter;

public interface ProximityMatrix<O> {
    Collection<O> getObjects();

    public Pair<O> getMinimumDistancePair();

    public double getMinimumDistance();

    public int getRowIndex(O o);

    public int getColumnIndex(O o);

    public double getDistance(O anObject, O anotherObject);

    public double getDistance(int anObject, int anotherObject);

    public int[] getRows(Pair<O> pair);

    public int[] getColumns(Pair<O> pair);

    public ProximityMatrix<O> reduce(PairFilter<O> filter);

    public SparseMatrix getData();
}
