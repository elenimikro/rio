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
import java.util.HashSet;
import java.util.Set;

import org.coode.pair.Pair;

public class HistoryItem<O> implements Cloneable {
	private final Pair<O> pair;
	private final Set<O> items = new HashSet<O>();

	public HistoryItem(Pair<O> pair, Collection<? extends O> clusters) {
		if (pair == null) {
			throw new NullPointerException("The pair cannot be null");
		}
		if (clusters == null) {
			throw new NullPointerException("The cluster collection cannot be null");
		}
		this.pair = pair;
		this.items.addAll(clusters);
	}

	/**
	 * @return the pair
	 */
	public Pair<O> getPair() {
		return this.pair;
	}

	@Override
	public HistoryItem<O> clone() throws CloneNotSupportedException {
		return new HistoryItem<O>(this.getPair(), this.getItems());
	}

	/**
	 * @return the items
	 */
	public Set<O> getItems() {
		return new HashSet<O>(this.items);
	}
}
