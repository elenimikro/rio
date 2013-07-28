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

public class IncomparableObjectsStructuralDifferenceReport implements
		StructuralDifferenceReport {
	static final IncomparableObjectsStructuralDifferenceReport INSTANCE = new IncomparableObjectsStructuralDifferenceReport();

	private IncomparableObjectsStructuralDifferenceReport() {}

	@Override
    public void accept(StructuralDifferenceReportVisitor visitor) {
		visitor.visitIncomparableObjectsStructuralDifferenceReport(this);
	}

	@Override
    public <O> O accept(StructuralDifferenceReportVisitorEx<O> visitor) {
		return visitor.visitIncomparableObjectsStructuralDifferenceReport(this);
	}

	@Override
	public String toString() {
		return "Incomparable objects";
	}
}
