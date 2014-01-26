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
package org.coode.distance;

import java.util.ArrayList;
import java.util.Collection;

import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
public class Utils {
    /** @param objects
     *            objects
     * @param distance
     *            distance
     * @return equivalence classes */
    public static <P> MultiMap<P, P> getEquivalenceClasses(
            Collection<? extends P> objects, Distance<P> distance) {
        MultiMap<P, P> toReturn = new MultiMap<P, P>();
        for (P p : objects) {
            boolean found = false;
            for (P key : new ArrayList<P>(toReturn.keySet())) {
                if (distance.getDistance(p, key) == 0) {
                    toReturn.put(key, p);
                    found = true;
                }
            }
            // The element will form a new equivalence class on its own for the
            // time being
            if (!found) {
                toReturn.put(p, p);
            }
        }
        return toReturn;
    }
}
