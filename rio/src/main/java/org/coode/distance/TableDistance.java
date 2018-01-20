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

/**
 * @author Luigi Iannone
 * @param <O> type
 */
public class TableDistance<O> implements Distance<O> {
    private final SparseMatrix table;

    /**
     * @param objects objects
     * @param table table
     */
    public TableDistance(Collection<? extends O> objects, SparseMatrix table) {
        if (table == null) {
            throw new NullPointerException("The table cannot be null");
        }
        if (objects == null) {
            throw new NullPointerException("The object colleciton cannot be null");
        }
        if (objects.isEmpty()) {
            throw new IllegalArgumentException("The object collection cannot be empty");
        }
        if (objects.size() != table.length()) {
            throw new IllegalArgumentException("The table is of the wrong size");
        }
        this.table = SparseMatrixFactory.create(table);
        this.table.setKeys(objects);
    }

    @Override
    public double getDistance(O a, O b) {
        return this.table.get(a, b);
    }

    /**
     * @param i i
     * @param j j
     * @return distance
     */
    public double getDistance(int i, int j) {
        return this.table.get(i, j);
    }
}
