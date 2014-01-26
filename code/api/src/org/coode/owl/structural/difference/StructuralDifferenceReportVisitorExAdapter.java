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

/** @author eleni
 * @param <O>
 *            type */
public class StructuralDifferenceReportVisitorExAdapter<O> implements
        StructuralDifferenceReportVisitorEx<O> {
    private O defaultValue;

    /**
     * 
     */
    public StructuralDifferenceReportVisitorExAdapter() {
        this(null);
    }

    /** @param defaultValue
     *            defaultValue */
    public StructuralDifferenceReportVisitorExAdapter(O defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public
            O
            visitNoDifferenceStructuralDifferenceReport(
                    NoDifferenceStructuralDifferenceReport noDifferenceStructuralDifferenceReport) {
        return this.getDefaultValue(noDifferenceStructuralDifferenceReport);
    }

    @Override
    public
            O
            visitIncomparableObjectsStructuralDifferenceReport(
                    IncomparableObjectsStructuralDifferenceReport incomparableObjectsStructuralDifferenceReport) {
        return this.getDefaultValue(incomparableObjectsStructuralDifferenceReport);
    }

    @Override
    public
            O
            visitSomeDifferenceStructuralDifferenceReport(
                    SomeDifferenceStructuralDifferenceReport someDifferenceStructuralDifferenceReport) {
        return this.getDefaultValue(someDifferenceStructuralDifferenceReport);
    }

    @SuppressWarnings("unused")
    protected O getDefaultValue(StructuralDifferenceReport structuralDifferenceReport) {
        return this.defaultValue;
    }
}
