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

/** @author Luigi Iannone
 * @param <O>
 *            type */
public class AndPairFilter<O> extends BooleanExpressionFilter<O> {
    /** @param filters
     *            filters */
    public AndPairFilter(PairFilter<O>... filters) {
        super(filters);
    }

    @Override
    public boolean accept(O first, O second) {
        boolean toReturn = true;
        for (PairFilter<O> filter : getFilters()) {
            toReturn = toReturn && filter.accept(first, second);
        }
        return toReturn;
    }

    /** @param filters
     *            filters
     * @param <P>
     *            type
     * @return and filter */
    public static <P> AndPairFilter<P> build(PairFilter<P>... filters) {
        return new AndPairFilter<P>(filters);
    }
}
