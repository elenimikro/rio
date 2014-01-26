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

import org.coode.oppl.Variable;
import org.coode.oppl.bindingtree.AssignmentMap;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.util.OWLObjectVisitorAdapter;

/** @author eleni */
public class AssignmentMapBasedOWLEntityProvider implements OWLEntityProvider {
    protected final Set<OWLEntity> delegate = new HashSet<OWLEntity>();
    private final AssignmentMap assignmentMap;

    /** @param assignmentMap
     *            assignmentMap */
    public AssignmentMapBasedOWLEntityProvider(AssignmentMap assignmentMap) {
        if (assignmentMap == null) {
            throw new NullPointerException("The assignment map cannot be null");
        }
        this.assignmentMap = assignmentMap;
        loadDelegate();
    }

    private void loadDelegate() {
        Set<Variable<?>> variables = assignmentMap.getVariables();
        for (Variable<?> variable : variables) {
            Set<OWLObject> set = assignmentMap.get(variable);
            for (OWLObject owlObject : set) {
                owlObject.accept(new OWLObjectVisitorAdapter() {
                    @Override
                    public void visit(OWLClass desc) {
                        addEntity(desc);
                    }

                    @Override
                    public void visit(OWLAnnotationProperty property) {
                        addEntity(property);
                    }

                    @Override
                    public void visit(OWLDataProperty property) {
                        addEntity(property);
                    }

                    @Override
                    public void visit(OWLObjectProperty property) {
                        addEntity(property);
                    }

                    @Override
                    public void visit(OWLNamedIndividual individual) {
                        addEntity(individual);
                    }

                    private void addEntity(OWLEntity desc) {
                        delegate.add(desc);
                    }
                });
            }
        }
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
