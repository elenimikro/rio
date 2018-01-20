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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** @author eleni */
public class SomeDifferenceStructuralDifferenceReport implements StructuralDifferenceReport {
    private static Map<SomeDifferenceStructuralDifferenceReport, SomeDifferenceStructuralDifferenceReport> cache =
        new HashMap<>();
    private final List<Integer> position = new ArrayList<>();

    /**
     * @param position position
     * @return report
     */
    public static SomeDifferenceStructuralDifferenceReport build(List<? extends Integer> position) {
        SomeDifferenceStructuralDifferenceReport someDifferenceStructuralDifferenceReport =
            new SomeDifferenceStructuralDifferenceReport(position);
        SomeDifferenceStructuralDifferenceReport toReturn =
            cache.get(someDifferenceStructuralDifferenceReport);
        if (toReturn == null) {
            cache.put(someDifferenceStructuralDifferenceReport,
                someDifferenceStructuralDifferenceReport);
            toReturn = someDifferenceStructuralDifferenceReport;
        }
        return toReturn;
    }

    private SomeDifferenceStructuralDifferenceReport(List<? extends Integer> position) {
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

    /** @return the position */
    public List<Integer> getPosition() {
        return new ArrayList<>(position);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("Difference at position ");
        Iterator<Integer> iterator = position.iterator();
        while (iterator.hasNext()) {
            int i = iterator.next();
            out.append(i);
            if (iterator.hasNext()) {
                out.append(", ");
            }
        }
        return out.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (position == null ? 0 : position.hashCode());
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
        if (position == null) {
            if (other.position != null) {
                return false;
            }
        } else if (!position.equals(other.position)) {
            return false;
        }
        return true;
    }
}
