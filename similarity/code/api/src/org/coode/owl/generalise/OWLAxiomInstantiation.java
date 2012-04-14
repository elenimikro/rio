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
package org.coode.owl.generalise;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.coode.oppl.Variable;
import org.coode.oppl.VariableVisitor;
import org.coode.oppl.bindingtree.AssignmentMap;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.oppl.function.ValueComputationParameters;
import org.coode.oppl.generated.GeneratedVariable;
import org.coode.oppl.generated.RegexpGeneratedVariable;
import org.coode.oppl.variabletypes.InputVariable;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * @author Luigi Iannone
 * 
 */
public class OWLAxiomInstantiation {
	private final OWLAxiom axiom;
	private final AssignmentMap substitutions;

	/**
	 * @param axiom
	 * @param assignmentMap
	 */
	public OWLAxiomInstantiation(OWLAxiom axiom, AssignmentMap assignmentMap) {
		if (axiom == null) {
			throw new NullPointerException("The axiom cannot be null");
		}
		if (assignmentMap == null) {
			throw new NullPointerException("The assignment map cannot be null");
		}
		this.axiom = axiom;
		this.substitutions = new AssignmentMap(assignmentMap);
	}

	/**
	 * @return the axiom
	 */
	public OWLAxiom getAxiom() {
		return this.axiom;
	}

	/**
	 * @return the assignmentMap
	 */
	public AssignmentMap getSubstitutions() {
		return new AssignmentMap(this.substitutions);
	}

	public Set<InputVariable<?>> getInputVariables() {
		final Set<InputVariable<?>> toReturn = new HashSet<InputVariable<?>>();
		for (Variable<?> v : this.substitutions.keySet()) {
			v.accept(new VariableVisitor() {
				public <P extends OWLObject> void visit(
						RegexpGeneratedVariable<P> regExpGenerated) {}

				public <P extends OWLObject> void visit(GeneratedVariable<P> v) {}

				public <P extends OWLObject> void visit(InputVariable<P> v) {
					toReturn.add(v);
				}
			});
		}
		return toReturn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (this.substitutions == null ? 0 : this.substitutions.hashCode());
		result = prime * result + (this.axiom == null ? 0 : this.axiom.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		OWLAxiomInstantiation other = (OWLAxiomInstantiation) obj;
		if (this.substitutions == null) {
			if (other.substitutions != null) {
				return false;
			}
		} else if (!this.substitutions.equals(other.substitutions)) {
			return false;
		}
		if (this.axiom == null) {
			if (other.axiom != null) {
				return false;
			}
		} else if (!this.axiom.equals(other.axiom)) {
			return false;
		}
		return true;
	}

	public boolean agreesWith(BindingNode bindingNode,
			ValueComputationParameters parameters) {
		if (bindingNode == null) {
			throw new NullPointerException("The binding node cannot be null");
		}
		if (parameters == null) {
			throw new NullPointerException(
					"The value computation parameters cannot be null");
		}
		boolean found = false;
		Iterator<Variable<?>> iterator = bindingNode.getAssignedVariables().iterator();
		while (!found && iterator.hasNext()) {
			Variable<?> variable = iterator.next();
			OWLObject assignmentValue = bindingNode.getAssignmentValue(variable,
					parameters);
			Set<OWLObject> set = this.getSubstitutions().get(variable);
			found = set == null || set.size() > 1 || !set.contains(assignmentValue);//iterator().next().equals(assignmentValue);
		}
		return !found;
	}

	@Override
	public String toString() {
		return String.format("%s : %s", this.getAxiom(), this.substitutions);
	}
}
