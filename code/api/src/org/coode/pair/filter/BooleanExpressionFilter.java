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
package org.coode.pair.filter;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class BooleanExpressionFilter<O> implements PairFilter<O> {
	private final PairFilter<O>[] filters;

	/**
	 * @param filters
	 */
	public BooleanExpressionFilter(PairFilter<O>... filters) {
		if (filters == null) {
			throw new NullPointerException("The filters cannot be null");
		}
		if (filters.length < 1) {
			throw new IllegalArgumentException(String.format(
					"The number of filters (%d) is not >1", filters.length));
		}
		this.filters = filters;
	}

	/**
	 * @return the filters
	 */
	protected PairFilter<O>[] getFilters() {
		return this.filters;
	}
}
