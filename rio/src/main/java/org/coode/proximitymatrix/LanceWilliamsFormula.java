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

/** @author Luigi Iannone */
public class LanceWilliamsFormula {
    private final double alphaA;
    private final double alphaB;
    private final double beta;
    private final double gamma;

    /**
     * @param alphaA alphaA
     * @param alphaB alphaB
     * @param beta beta
     * @param gamma gamma
     */
    public LanceWilliamsFormula(double alphaA, double alphaB, double beta, double gamma) {
        this.alphaA = alphaA;
        this.alphaB = alphaB;
        this.beta = beta;
        this.gamma = gamma;
    }

    /**
     * @param distanceAQ distanceAQ
     * @param distanceBQ distanceBQ
     * @param distanceAB distanceAB
     * @return distance
     */
    public double distance(double distanceAQ, double distanceBQ, double distanceAB) {
        return alphaA * distanceAQ + alphaB * distanceBQ + beta * distanceAB
            + gamma * Math.abs(distanceAQ - distanceBQ);
    }
}
