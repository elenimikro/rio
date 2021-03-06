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

/**
 * Generic Structural difference report.
 * 
 * @author Luigi Iannone
 */
public interface StructuralDifferenceReport {
    /** no difference report */
    public static final NoDifferenceStructuralDifferenceReport NO_DIFFERENCE =
        NoDifferenceStructuralDifferenceReport.INSTANCE;
    /** incomparable report */
    public static final IncomparableObjectsStructuralDifferenceReport INCOMPARABLE =
        IncomparableObjectsStructuralDifferenceReport.INSTANCE;

    /**
     * @param visitor visitor
     */
    void accept(StructuralDifferenceReportVisitor visitor);

    /**
     * @param visitor visitor
     * @param <O> type
     * @return visitor value
     */
    <O> O accept(StructuralDifferenceReportVisitorEx<O> visitor);
}
