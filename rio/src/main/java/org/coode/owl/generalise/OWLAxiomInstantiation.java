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

/** @author Luigi Iannone */
public class OWLAxiomInstantiation {
    private final OWLAxiom axiom;
    private final AssignmentMap substitutions;

    /**
     * @param axiom axiom
     * @param assignmentMap assignmentMap
     */
    public OWLAxiomInstantiation(OWLAxiom axiom, AssignmentMap assignmentMap) {
        if (axiom == null) {
            throw new NullPointerException("The axiom cannot be null");
        }
        if (assignmentMap == null) {
            throw new NullPointerException("The assignment map cannot be null");
        }
        this.axiom = axiom;
        substitutions = new AssignmentMap(assignmentMap);
    }

    /** @return the axiom */
    public OWLAxiom getAxiom() {
        return axiom;
    }

    /** @return the assignmentMap */
    public AssignmentMap getSubstitutions() {
        return new AssignmentMap(substitutions);
    }

    /** @return input variables */
    public Set<InputVariable<?>> getInputVariables() {
        final Set<InputVariable<?>> toReturn = new HashSet<>();
        for (Variable<?> v : substitutions.keySet()) {
            v.accept(new VariableVisitor() {
                @Override
                public <P extends OWLObject> void visit(
                    RegexpGeneratedVariable<P> regExpGenerated) {}

                @Override
                public <P extends OWLObject> void visit(GeneratedVariable<P> gv) {}

                @Override
                public <P extends OWLObject> void visit(InputVariable<P> iv) {
                    toReturn.add(iv);
                }
            });
        }
        return toReturn;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (substitutions == null ? 0 : substitutions.hashCode());
        result = prime * result + (axiom == null ? 0 : axiom.hashCode());
        return result;
    }

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
        if (substitutions == null) {
            if (other.substitutions != null) {
                return false;
            }
        } else if (!substitutions.equals(other.substitutions)) {
            return false;
        }
        if (axiom == null) {
            if (other.axiom != null) {
                return false;
            }
        } else if (!axiom.equals(other.axiom)) {
            return false;
        }
        return true;
    }

    /**
     * @param bindingNode bindingNode
     * @param parameters parameters
     * @return true if in agreement
     */
    public boolean agreesWith(BindingNode bindingNode, ValueComputationParameters parameters) {
        if (bindingNode == null) {
            throw new NullPointerException("The binding node cannot be null");
        }
        if (parameters == null) {
            throw new NullPointerException("The value computation parameters cannot be null");
        }
        boolean found = bindingNode.assignedVariables()
            .anyMatch(v -> foundDisagreement(bindingNode, parameters, v));
        return !found;
    }

    protected boolean foundDisagreement(BindingNode bindingNode,
        ValueComputationParameters parameters, Variable<?> variable) {
        boolean found;
        OWLObject assignmentValue = bindingNode.getAssignmentValue(variable, parameters);
        Set<OWLObject> set = substitutions.get(variable);
        found = set == null || set.size() > 1 || !set.contains(assignmentValue);
        return found;
    }

    @Override
    public String toString() {
        return String.format("%s : %s", getAxiom(), substitutions);
    }
}
