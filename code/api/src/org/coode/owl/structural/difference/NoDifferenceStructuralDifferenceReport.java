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
/** @author eleni */
public class NoDifferenceStructuralDifferenceReport implements StructuralDifferenceReport {
	final static NoDifferenceStructuralDifferenceReport INSTANCE = new NoDifferenceStructuralDifferenceReport();

	private NoDifferenceStructuralDifferenceReport() {}

	@Override
    public void accept(StructuralDifferenceReportVisitor visitor) {
		visitor.visitNoDifferenceStructuralDifferenceReport(this);
	}

	@Override
    public <O> O accept(StructuralDifferenceReportVisitorEx<O> visitor) {
		return visitor.visitNoDifferenceStructuralDifferenceReport(this);
	}

	@Override
	public String toString() {
		return "No difference";
	}
}
