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
package org.coode.distance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Luigi Iannone
 * 
 */
public final class TableDistance<O> implements Distance<O> {
	private final double[][] table;
	private final Set<O> objects = new LinkedHashSet<O>();
	private final Map<O, Integer> objectIndex = new HashMap<O, Integer>();

	public TableDistance(Collection<? extends O> objects, double[][] table) {
		if (table == null) {
			throw new NullPointerException("The table cannot be null");
		}
		if (objects == null) {
			throw new NullPointerException("The object colleciton cannot be null");
		}
		if (objects.isEmpty()) {
			throw new IllegalArgumentException("The object collection cannot be empty");
		}
		if (objects.size() != table.length) {
			throw new IllegalArgumentException(String.format(
					"The table is of the wrong size %d for table whose size is %d ",
					table.length, objects.size()));
		}
		this.table = new double[objects.size()][objects.size()];
		System.arraycopy(table, 0, this.table, 0, table.length);
		this.objects.addAll(objects);
		int i = 0;
		Iterator<? extends O> iterator = objects.iterator();
		while (iterator.hasNext()) {
			O o = iterator.next();
			this.objectIndex.put(o, i);
			i++;
		}
	}

	public double getDistance(O a, O b) {
		Integer index = this.objectIndex.get(a);
		int rowIndex = index == null ? -1 : index;
		if (rowIndex == -1) {
			throw new IllegalArgumentException(String.format(
					"%s is not contained in this table based distance", a));
		}
		index = this.objectIndex.get(b);
		int columnIndex = index == null ? -1 : index;
		if (columnIndex == -1) {
			throw new IllegalArgumentException(String.format(
					"%s is not contained in this table based distance", b));
		}
		return this.table[rowIndex][columnIndex];
	}

	public double getDistance(int i, int j) {
		return this.table[i][j];
	}
}
