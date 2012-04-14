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
package org.coode.owl.generalise;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.Variable;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.variabletypes.VariableType;
import org.coode.oppl.variabletypes.VariableTypeFactory;
import org.coode.owl.wrappers.OWLEntityProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

public abstract class VariableProvider {
	private final ConstraintSystem constraintSystem;
	private final OWLEntityProvider entityProvider;

	/**
	 * @param structuralOWLObjectGeneralisation
	 */
	public VariableProvider(OWLEntityProvider entityProvider,
			ConstraintSystem constraintSystem) {
		if (constraintSystem == null) {
			throw new NullPointerException("The constraint system cannot be null");
		}
		if (entityProvider == null) {
			throw new NullPointerException("The entity provider cannot be null");
		}
		this.constraintSystem = constraintSystem;
		this.entityProvider = entityProvider;
	}

	private final Map<OWLObject, Variable<?>> cache = new HashMap<OWLObject, Variable<?>>();
	private final Map<VariableType<?>, Variable<?>> variables = new HashMap<VariableType<?>, Variable<?>>();
	private final Map<VariableType<?>, Integer> variableIndex = new HashMap<VariableType<?>, Integer>();

	public Variable<?> get(OWLObject owlObject) {
		Variable<?> toReturn = this.cache.get(owlObject);
		if (toReturn == null) {
			VariableType<?> variableType = VariableTypeFactory.getVariableType(owlObject);
			toReturn = this.variables.get(variableType);
			if (toReturn != null) {
				this.cache.put(owlObject, toReturn);
			}
		}
		return toReturn;
	}

	public void newVariable(VariableType<?> variableType) {
		Integer integer = this.variableIndex.get(variableType);
		if (integer == null) {
			integer = 0;
		}
		this.variableIndex.put(variableType, integer + 1);
		Variable<?> v;
		try {
			v = this.getConstraintSystem().createVariable(this.createName(variableType),
					variableType, null);
			this.variables.put(variableType, v);
		} catch (OPPLException e) {
			e.printStackTrace();
		}
	}

	private String createName(VariableType<?> variableType) {
		return String.format("?%s_%d", variableType.toString().toLowerCase(),
				this.variableIndex.get(variableType).intValue());
	}

	// protected abstract Variable<?> getAbstractingVariable(final IRI iri);
	protected abstract Variable<?> getAbstractingVariable(OWLObject owlObject);

	/**
	 * @return the constraintSystem
	 */
	public ConstraintSystem getConstraintSystem() {
		return this.constraintSystem;
	}

	protected OWLObject getOWLEntity(final IRI iri) {
		boolean found = false;
		OWLObject toReturn = null;
		OWLObject owlObject = null;
		Iterator<OWLEntity> iterator = this.getEntityProvider().iterator();
		while (!found && iterator.hasNext()) {
			owlObject = iterator.next();
			toReturn = owlObject.accept(new OWLObjectVisitorExAdapter<OWLObject>() {
				private boolean matchEntity(OWLEntity desc) {
					return desc.getIRI().equals(iri);
				}

				@Override
				public OWLObject visit(OWLClass desc) {
					return this.matchEntity(desc) ? desc : null;
				}

				@Override
				public OWLObject visit(OWLDataProperty property) {
					return this.matchEntity(property) ? property : null;
				}

				@Override
				public OWLObject visit(OWLObjectProperty property) {
					return this.matchEntity(property) ? property : null;
				}

				@Override
				public OWLObject visit(OWLAnnotationProperty property) {
					return this.matchEntity(property) ? property : null;
				}

				@Override
				public OWLObject visit(OWLNamedIndividual individual) {
					return this.matchEntity(individual) ? individual : null;
				}
			});
			found = toReturn != null;
		}
		toReturn = found ? toReturn : null;
		return toReturn;
	}

	/**
	 * @return the entityProvider
	 */
	public OWLEntityProvider getEntityProvider() {
		return this.entityProvider;
	}
}
