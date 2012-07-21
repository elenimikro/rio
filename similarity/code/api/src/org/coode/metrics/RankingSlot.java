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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RankingSlot<O, R> {
	private final R value;
	private final Collection<? extends O> toReturn;
	private final int size;
	   private int hashCode=0;

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
		toReturn=members;
		size=toReturn.size();
	}

	/**
	 * @return the members
	 */
	public Collection<? extends O> getMembers() {
		return toReturn;
	}

	public int getMembersSize() {
		return this.size;
	}

	public int getMembersHashCode() {
	    if(hashCode==0) {
	        hashCode=toReturn.hashCode();
	    }
		return this.hashCode;
	}
	/**
	 * @return the value
	 */
	public R getValue() {
		return this.value;
	}
}
