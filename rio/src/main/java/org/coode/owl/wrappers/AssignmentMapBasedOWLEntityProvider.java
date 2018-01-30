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
import java.util.Iterator;
import java.util.Set;

import org.coode.oppl.bindingtree.AssignmentMap;
import org.semanticweb.owlapi.model.OWLEntity;

/** @author eleni */
public class AssignmentMapBasedOWLEntityProvider implements OWLEntityProvider {
    protected final Set<OWLEntity> delegate = new HashSet<>();
    private final AssignmentMap assignmentMap;

    /**
     * @param assignmentMap assignmentMap
     */
    public AssignmentMapBasedOWLEntityProvider(AssignmentMap assignmentMap) {
        if (assignmentMap == null) {
            throw new NullPointerException("The assignment map cannot be null");
        }
        this.assignmentMap = assignmentMap;
        loadDelegate();
    }

    private void loadDelegate() {
        assignmentMap.variables().forEach(v -> assignmentMap.get(v).stream()
            .filter(a -> a instanceof OWLEntity).forEach(a -> delegate.add((OWLEntity) a)));
    }

    @Override
    public boolean add(OWLEntity e) {
        return delegate.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends OWLEntity> c) {
        return delegate.addAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Iterator<OWLEntity> iterator() {
        return delegate.iterator();
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    /** @return the assignmentMap */
    public AssignmentMap getAssignmentMap() {
        return new AssignmentMap(assignmentMap);
    }
}
