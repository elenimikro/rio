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
	private final List<OWLAxiomsChangedListener> listeners = new ArrayList<OWLAxiomsChangedListener>();

	@Override
    public void addOWLAxiomsChangedListener(OWLAxiomsChangedListener l) {
		if (l != null) {
			this.listeners.add(l);
		}
	}

	@Override
    public void removeOWLAxiomsChangedListener(OWLAxiomsChangedListener l) {
		this.listeners.remove(l);
	}

	protected final void notifyListeners() {
		for (OWLAxiomsChangedListener l : this.listeners) {
			l.axiomsChanged();
		}
	}
}
