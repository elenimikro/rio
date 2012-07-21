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
import java.util.List;
import java.util.Set;

/**
 * @author Luigi Iannone
 *
 */
public class SimplePair<O> implements Pair<O> {
	private final O first;
	private final O second;

	public SimplePair(O first, O second) {
	    if(first==null) {
            throw new IllegalArgumentException("first cannot be null");
        }if(second==null) {
            throw new IllegalArgumentException("second cannot be null");
        }
		this.first = first;
		this.second = second;
	}

	public SimplePair(Pair<O> pair) {
	    this( pair.getFirst(), pair.getSecond());
	}

	public List<O> getElements() {
		return Arrays.<O> asList(this.first, this.second);
	}

	public boolean contains(Object o) {
	    return first.equals(o)||second.equals(o);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.first == null ? 0 : this.first.hashCode());
		result = prime * result + (this.second == null ? 0 : this.second.hashCode());
		return result;
	}


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
