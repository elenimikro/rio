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

public class AddLeftOversHistoryItemFactory<O> implements HistoryItemFactory<O> {
	private final Set<O> leftOvers = new HashSet<O>();

	public AddLeftOversHistoryItemFactory(Collection<? extends O> leftOvers) {
		if (leftOvers == null) {
			throw new NullPointerException("The left over set cannot be null");
		}
		this.leftOvers.addAll(leftOvers);
	}

	public HistoryItem<O> create(Pair<O> pair, Collection<? extends O> clusters) {
		Set<O> newClusters = this.getLeftOvers();
		newClusters.addAll(clusters);
		return new HistoryItem<O>(pair, newClusters);
	}

	/**
	 * @return the leftOvers
	 */
	public Set<O> getLeftOvers() {
		return new HashSet<O>(this.leftOvers);
	}

	public static <P> AddLeftOversHistoryItemFactory<P> build(
			Collection<? extends P> leftOvers) {
		if (leftOvers == null) {
			throw new NullPointerException("The leftOvers cannot be null");
		}
		return new AddLeftOversHistoryItemFactory<P>(leftOvers);
	}
}
