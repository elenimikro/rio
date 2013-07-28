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
package org.coode.proximitymatrix;

/**
 * @author Luigi Iannone
 * 
 */
public final class LanceWilliamsFormula {
	public static final LanceWilliamsFormula SINGLE_LINK = new LanceWilliamsFormula(.5,
			.5, 0, -0.5);
	public static final LanceWilliamsFormula COMPLETE_LINK = new LanceWilliamsFormula(.5,
			.5, 0, 0.5);
	private final double alphaA;
	private final double alphaB;
	private final double beta;
	private final double gamma;

	/**
	 * @param alphaA
	 * @param alphaB
	 * @param beta
	 * @param gamma
	 */
	public LanceWilliamsFormula(double alphaA, double alphaB, double beta, double gamma) {
		this.alphaA = alphaA;
		this.alphaB = alphaB;
		this.beta = beta;
		this.gamma = gamma;
	}

	public double distance(double distanceAQ, double distanceBQ, double distanceAB) {
		return this.alphaA * distanceAQ + this.alphaB * distanceBQ + this.beta
				* distanceAB + this.gamma * Math.abs(distanceAQ - distanceBQ);
	}
}
