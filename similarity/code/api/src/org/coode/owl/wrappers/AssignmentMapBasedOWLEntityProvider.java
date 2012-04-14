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

public class AssignmentMapBasedOWLEntityProvider implements OWLEntityProvider {
	private final Set<OWLEntity> delegate = new HashSet<OWLEntity>();
	private final AssignmentMap assignmentMap;

	public AssignmentMapBasedOWLEntityProvider(AssignmentMap assignmentMap) {
		if (assignmentMap == null) {
			throw new NullPointerException("The assignment map cannot be null");
		}
		this.assignmentMap = assignmentMap;
		this.loadDelegate();
	}

	private void loadDelegate() {
		AssignmentMap assignmentMap = this.getAssignmentMap();
		Set<Variable<?>> variables = assignmentMap.getVariables();
		for (Variable<?> variable : variables) {
			Set<OWLObject> set = assignmentMap.get(variable);
			for (OWLObject owlObject : set) {
				owlObject.accept(new OWLObjectVisitorAdapter() {
					@Override
					public void visit(OWLClass desc) {
						this.addEntity(desc);
					}

					@Override
					public void visit(OWLAnnotationProperty property) {
						this.addEntity(property);
					}

					@Override
					public void visit(OWLDataProperty property) {
						this.addEntity(property);
					}

					@Override
					public void visit(OWLObjectProperty property) {
						this.addEntity(property);
					}

					@Override
					public void visit(OWLNamedIndividual individual) {
						this.addEntity(individual);
					}

					private void addEntity(OWLEntity desc) {
						AssignmentMapBasedOWLEntityProvider.this.delegate.add(desc);
					}
				});
			}
		}
	}

	/**
	 * @param e
	 * @return
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(OWLEntity e) {
		return this.delegate.add(e);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends OWLEntity> c) {
		return this.delegate.addAll(c);
	}

	/**
	 * 
	 * @see java.util.Set#clear()
	 */
	public void clear() {
		this.delegate.clear();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return this.delegate.contains(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		return this.delegate.containsAll(c);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return this.delegate.equals(o);
	}

	/**
	 * @return
	 * @see java.util.Set#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.delegate.hashCode();
	}

	/**
	 * @return
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return this.delegate.isEmpty();
	}

	/**
	 * @return
	 * @see java.util.Set#iterator()
	 */
	public Iterator<OWLEntity> iterator() {
		return this.delegate.iterator();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return this.delegate.remove(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		return this.delegate.removeAll(c);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		return this.delegate.retainAll(c);
	}

	/**
	 * @return
	 * @see java.util.Set#size()
	 */
	public int size() {
		return this.delegate.size();
	}

	/**
	 * @return
	 * @see java.util.Set#toArray()
	 */
	public Object[] toArray() {
		return this.delegate.toArray();
	}

	/**
	 * @param <T>
	 * @param a
	 * @return
	 * @see java.util.Set#toArray(T[])
	 */
	public <T> T[] toArray(T[] a) {
		return this.delegate.toArray(a);
	}

	/**
	 * @return the assignmentMap
	 */
	public AssignmentMap getAssignmentMap() {
		return new AssignmentMap(this.assignmentMap);
	}
}
