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
package org.coode.pair;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Luigi Iannone
 * 
 */
public class SimplePair<O> implements Pair<O> {
	private final O first;
	private final O second;

	public SimplePair(O first, O second) {
		this.first = first;
		this.second = second;
	}

	public SimplePair(Pair<O> pair) {
		this.first = pair.getFirst();
		this.second = pair.getSecond();
	}

	public Set<O> getElements() {
		return new LinkedHashSet<O>(Arrays.<O> asList(this.first, this.second));
	}

	public boolean contains(Object o) {
		return this.getElements().contains(o);
	}

	public static <P> SimplePair<P> build(P object, P anotherObject) {
		return new SimplePair<P>(object, anotherObject);
	}

	@Override
	public String toString() {
		return String.format("%s, %s ", this.first, this.second);
	}

	/**
	 * @return the first
	 */
	public O getFirst() {
		return this.first;
	}

	/**
	 * @return the second
	 */
	public O getSecond() {
		return this.second;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.first == null ? 0 : this.first.hashCode());
		result = prime * result + (this.second == null ? 0 : this.second.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		SimplePair<?> other = (SimplePair<?>) obj;
		return this.first.equals(other.first) && this.second.equals(other.second)
				|| this.second.equals(other.first) && this.first.equals(other.second);
	}
}
