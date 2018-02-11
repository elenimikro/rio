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
package org.coode.owl.structural.difference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/** @author eleni */
public class SomeDifferenceStructuralDifferenceReport implements StructuralDifferenceReport {
    private static Map<SomeDifferenceStructuralDifferenceReport, SomeDifferenceStructuralDifferenceReport> cache =
        new HashMap<>();
    protected final TIntList position = new TIntArrayList();

    /**
     * @param position position
     * @return report
     */
    public static SomeDifferenceStructuralDifferenceReport build(TIntList position) {
        SomeDifferenceStructuralDifferenceReport report =
            new SomeDifferenceStructuralDifferenceReport(position);
        SomeDifferenceStructuralDifferenceReport toReturn = cache.get(report);
        if (toReturn == null) {
            cache.put(report, report);
            toReturn = report;
        }
        return toReturn;
    }

    private SomeDifferenceStructuralDifferenceReport(TIntList position) {
        if (position == null) {
            throw new NullPointerException("The position cannot be null");
        }
        if (position.isEmpty()) {
            throw new IllegalArgumentException("The position must contain at least one index");
        }
        this.position.addAll(position);
    }

    @Override
    public void accept(StructuralDifferenceReportVisitor visitor) {
        visitor.visitSomeDifferenceStructuralDifferenceReport(this);
    }

    @Override
    public <O> O accept(StructuralDifferenceReportVisitorEx<O> visitor) {
        return visitor.visitSomeDifferenceStructuralDifferenceReport(this);
    }

    @Override
    public String toString() {
        return "Difference at position " + Arrays.stream(position.toArray())
            .mapToObj(i -> Integer.toString(i)).collect(Collectors.joining(", "));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + position.hashCode();
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
        SomeDifferenceStructuralDifferenceReport other =
            (SomeDifferenceStructuralDifferenceReport) obj;
        return position.equals(other.position);
    }
}
