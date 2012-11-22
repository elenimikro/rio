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

public class SimpleHistoryItemFactory<O> implements HistoryItemFactory<O> {
	@Override
    public HistoryItem<O> create(Pair<O> pair, Collection<? extends O> clusters) {
		return new HistoryItem<O>(pair, clusters);
	}
}
