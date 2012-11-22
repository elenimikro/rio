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
public class OrPairFilter<O> extends BooleanExpressionFilter<O> {
	/**
	 * @param filters
	 */
	public OrPairFilter(PairFilter<O>... filters) {
		super(filters);
	}

	/**
	 * @see org.coode.pair.filter.PairFilter#accept(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
    public boolean accept(O first, O second) {
		boolean toReturn = false;
		for (PairFilter<O> filter : this.getFilters()) {
			toReturn = toReturn || filter.accept(first, second);
		}
		return toReturn;
	}

	public static <P> OrPairFilter<P> build(PairFilter<P>... filters) {
		return new OrPairFilter<P>(filters);
	}
}
