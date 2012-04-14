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

import java.util.List;
import java.util.Set;

import org.apache.commons.math.linear.RealMatrix;
import org.coode.pair.Pair;
import org.coode.pair.filter.PairFilter;

public interface ProximityMatrix<O> extends RealMatrix {
	Set<O> getObjects();

	public Pair<O> getMinimumDistancePair();

	public double getMinimumDistance();

	public int getRowIndex(O o);

	public int getColumnIndex(O o);

	public double getDistance(O anObject, O anotherObject);

	public List<Integer> getRows(Pair<O> pair);

	public List<Integer> getColumns(Pair<O> pair);

	public ProximityMatrix<O> copy();

	public ProximityMatrix<O> reduce(PairFilter<O> filter);
}
