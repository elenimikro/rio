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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.OWLEntity;

/** @author eleni */
public class OWLEntityProvider {
    private Set<OWLEntity> delegate = new HashSet<>();

    /**
     * @param e entity to add
     * @return true if added
     */
    public boolean add(OWLEntity e) {
        return delegate.add(e);
    }

    /**
     * @param c entities to add
     * @return true if added
     */
    public boolean addAll(Collection<OWLEntity> c) {
        return delegate.addAll(c);
    }

    /**
     * Clear the collection.
     */
    public void clear() {
        delegate.clear();
    }

    /**
     * @return Stream of entities
     */
    public Stream<OWLEntity> stream() {
        return delegate.stream();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

}
