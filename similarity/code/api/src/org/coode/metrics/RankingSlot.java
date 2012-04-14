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
package org.coode.metrics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RankingSlot<O, R> {
	private final Set<O> members = new HashSet<O>();
	private final R value;

	public RankingSlot(R value, Collection<? extends O> members) {
		if (value == null) {
			throw new NullPointerException("The value cannot be null");
		}
		if (members == null) {
			throw new NullPointerException("The members collection cannot be null");
		}
		if (members.isEmpty()) {
			throw new IllegalArgumentException("The members collection cannot be empty");
		}
		this.value = value;
		this.members.addAll(members);
	}

	/**
	 * @return the members
	 */
	public Set<O> getMembers() {
		return new HashSet<O>(this.members);
	}

	/**
	 * @return the value
	 */
	public R getValue() {
		return this.value;
	}
}
