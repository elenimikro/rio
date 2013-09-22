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

public interface Ranking<O> {
	double getTopValue();

	double getBottomValue();

	O[] getTop();

	O[] getBottom();

	double[] getValues();

	boolean isAverageable();

	double getAverageValue();

	List<RankingSlot<O>> getSortedRanking();

	List<RankingSlot<O>> getUnorderedRanking();
}
