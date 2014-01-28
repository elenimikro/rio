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

import java.util.List;

/** @author eleni
 * @param <O>
 *            type */
public interface Ranking<O> {
    /** @return top value */
    double getTopValue();

    /** @return bottom value */
    double getBottomValue();

    /** @return top */
    O[] getTop();

    /** @return bottom */
    O[] getBottom();

    /** @return values */
    double[] getValues();

    /** @return true if averageable */
    boolean isAverageable();

    /** @return average */
    double getAverageValue();

    /** @return sorted ranking */
    List<RankingSlot<O>> getSortedRanking();

    /** @return unordered ranking */
    List<RankingSlot<O>> getUnorderedRanking();
}
