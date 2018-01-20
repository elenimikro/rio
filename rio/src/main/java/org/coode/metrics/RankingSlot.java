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

import java.util.Arrays;

/**
 * @author eleni
 * @param <O> type
 */
public class RankingSlot<O> {
    private final double value;
    private final O[] toReturn;
    private final int size;
    private int hashCode = 0;

    /**
     * @param value value
     * @param members members
     */
    public RankingSlot(double value, O... members) {
        if (members == null) {
            throw new NullPointerException("The members collection cannot be null");
        }
        if (members.length == 0) {
            throw new IllegalArgumentException("The members collection cannot be empty");
        }
        this.value = value;
        toReturn = members;
        size = toReturn.length;
    }

    /** @return the members */
    public O[] getMembers() {
        return toReturn;
    }

    /** @return member size */
    public int getMembersSize() {
        return this.size;
    }

    /** @return members hash code */
    public int getMembersHashCode() {
        if (hashCode == 0) {
            hashCode = Arrays.hashCode(toReturn);
        }
        return this.hashCode;
    }

    /** @return the value */
    public double getValue() {
        return this.value;
    }
}
