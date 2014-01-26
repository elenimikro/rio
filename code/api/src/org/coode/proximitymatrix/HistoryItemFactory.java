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

import org.coode.pair.Pair;

/** @author Luigi Iannone
 * @param <O>
 *            type */
public interface HistoryItemFactory<O> {
    /** Creates a new HistoryItem give the input merged pair and the resulting
     * input clustering.
     * 
     * @param pair
     *            The pair merged in order to create the clustering represented
     *            by the output HistoryItem. Cannot be <code>null</code>.
     * @param clusters
     *            The resulting clustering. Cannot be <code>null</code>.
     * @return an HistoryItem
     * @throws NullPointerException
     *             if either input is <code>null</code>. */
    HistoryItem<O> create(Pair<O> pair, Collection<? extends O> clusters);
}
