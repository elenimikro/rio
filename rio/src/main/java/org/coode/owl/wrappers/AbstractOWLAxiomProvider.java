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
package org.coode.owl.wrappers;

import java.util.ArrayList;
import java.util.List;

/** @author eleni */
public abstract class AbstractOWLAxiomProvider implements OWLAxiomProvider {
    private final List<OWLAxiomsChangedListener> listeners = new ArrayList<>();

    @Override
    public void addOWLAxiomsChangedListener(OWLAxiomsChangedListener l) {
        if (l != null) {
            listeners.add(l);
        }
    }

    @Override
    public void removeOWLAxiomsChangedListener(OWLAxiomsChangedListener l) {
        listeners.remove(l);
    }

    protected void notifyListeners() {
        for (OWLAxiomsChangedListener l : listeners) {
            l.axiomsChanged();
        }
    }
}
