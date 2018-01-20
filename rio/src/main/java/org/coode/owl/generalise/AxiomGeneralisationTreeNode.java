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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.Variable;
import org.coode.oppl.bindingtree.Assignment;
import org.coode.oppl.bindingtree.AssignmentMap;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
import org.coode.oppl.function.SimpleValueComputationParameters;
import org.coode.oppl.utils.VariableExtractor;
import org.coode.owl.structural.difference.StructuralDifference;
import org.coode.owl.structural.difference.StructuralDifferenceReport;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.DefaultTreeNode;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
public class AxiomGeneralisationTreeNode extends DefaultTreeNode<OWLAxiom>
    implements GeneralisationTreeNode<OWLAxiom> {
    private final Set<OWLAxiomInstantiation> instantiations = new HashSet<>();
    private final ConstraintSystem constraintSystem;

    /**
     * @param userObject userObject
     * @param instantiations instantiations
     * @param constraintSystem constraintSystem
     */
    public AxiomGeneralisationTreeNode(OWLAxiom userObject,
        Collection<? extends OWLAxiomInstantiation> instantiations,
        ConstraintSystem constraintSystem) {
        super(userObject);
        if (instantiations == null) {
            throw new NullPointerException("The colleciton of instantioations cannot be null");
        }
        if (constraintSystem == null) {
            throw new NullPointerException("The constraint system cannot be null");
        }
        this.constraintSystem = constraintSystem;
        this.instantiations.addAll(instantiations);
        Set<OWLAxiom> extractedAxioms = Utils.extractAxioms(instantiations);
        // check the structural differences. if they differ in more than one
        // positions then doIt=true
        boolean doIt = instantiations.size() > 1 && areDifferencesWorthIt(extractedAxioms);
        if (doIt) {
            VariableExtractor variableExtractor = new VariableExtractor(constraintSystem, false);
            // the userObject is the generalised axiom: e.g. ?class_1 SubClassOf
            // ?class_2
            Set<Variable<?>> variables = variableExtractor.extractVariables(userObject);
            MultiMap<Variable<?>, BindingNode> splitVaribales = new MultiMap<>();
            // has all the information; variables, instantiated axioms, variable
            // bindings.
            Map<Variable<?>, MultiMap<BindingNode, OWLAxiomInstantiation>> instantiationsMap =
                new HashMap<>();
            // get the variables for every generalisation (e.g. ?class_1,
            // ?class_2, ?objectproperty_1 etc)
            for (Variable<?> inputVariable : variables) {
                Map<BindingNode, AssignmentMap> map = new HashMap<>();
                Set<Variable<?>> otherVariables = new HashSet<>(variables);
                otherVariables.remove(inputVariable);
                Set<BindingNode> bindingNodes = new HashSet<>();
                Set<BindingNode> agreeingBindingNodes = new HashSet<>();
                for (OWLAxiomInstantiation owlAxiomInstantiation : instantiations) {
                    // get the value of the variable
                    OWLObject value = owlAxiomInstantiation.getSubstitutions().get(inputVariable)
                        .iterator().next();
                    // The BindingNode key includes a variable with its value
                    // and the rest of the variables (e.g. [?class_3=Hot,
                    // ?class_1, ?class_2, ?objectProperty].
                    BindingNode key =
                        new BindingNode(Collections.singleton(new Assignment(inputVariable, value)),
                            otherVariables);
                    bindingNodes.add(key);
                    MultiMap<BindingNode, OWLAxiomInstantiation> inputVariableInstantiationMap =
                        instantiationsMap.get(inputVariable);
                    if (inputVariableInstantiationMap == null) {
                        inputVariableInstantiationMap = new MultiMap<>();
                        instantiationsMap.put(inputVariable, inputVariableInstantiationMap);
                    }
                    // The inputVariableBindingInstantiations contain the
                    // variable bindings along with the instantiations
                    inputVariableInstantiationMap.put(key, owlAxiomInstantiation);
                }
                for (BindingNode bindingNode : bindingNodes) {
                    Set<BindingNode> set = new HashSet<>();
                    for (OWLAxiomInstantiation owlAxiomInstantiation : instantiations) {
                        if (owlAxiomInstantiation.agreesWith(bindingNode,
                            new SimpleValueComputationParameters(constraintSystem,
                                BindingNode.createNewEmptyBindingNode(),
                                new QuickFailRuntimeExceptionHandler()))) {
                            agreeingBindingNodes.add(bindingNode);
                            for (Variable<?> otherVariable : otherVariables) {
                                Set<Variable<?>> remainingVariables = new HashSet<>(otherVariables);
                                remainingVariables.remove(otherVariable);
                                OWLObject otherVariableValue = owlAxiomInstantiation
                                    .getSubstitutions().get(otherVariable).iterator().next();
                                set.add(new BindingNode(
                                    Collections.singleton(
                                        new Assignment(otherVariable, otherVariableValue)),
                                    remainingVariables));
                            }
                        }
                    }
                    map.put(bindingNode, new AssignmentMap(set));
                }
                if (AssignmentMap.areDisjoint(map.values()) && agreeingBindingNodes.size() > 1) {
                    splitVaribales.setEntry(inputVariable, agreeingBindingNodes);
                }
            }
            Set<Variable<?>> keySet = splitVaribales.keySet();
            for (Variable<?> variable : keySet) {
                MultiMap<BindingNode, OWLAxiomInstantiation> map = instantiationsMap.get(variable);
                this.addChild(new VariableGeneralisationTreeNode(variable, userObject, map,
                    constraintSystem));
            }
        }
    }

    @Override
    public void accept(GeneralisationTreeNodeVisitor visitor) {
        visitor.visitAxiomGeneralisationTreeNode(this);
    }

    @Override
    public <P> P accept(GeneralisationTreeNodeVisitorEx<P> visitor) {
        return visitor.visitAxiomGeneralisationTreeNode(this);
    }

    /** @return the instantiations */
    public Set<OWLAxiomInstantiation> getInstantiations() {
        return new HashSet<>(instantiations);
    }

    /** @return the constraintSystem */
    public ConstraintSystem getConstraintSystem() {
        return constraintSystem;
    }

    @Override
    public String render() {
        return getUserObject().toString();
    }

    private boolean areDifferencesWorthIt(Collection<? extends OWLAxiom> axioms) {
        if (axioms == null) {
            throw new NullPointerException("The collection cannot be null");
        }
        Set<List<StructuralDifferenceReport>> reports = new HashSet<>();
        boolean found = false;
        StructuralDifference structuralDifference = new StructuralDifference();
        Iterator<? extends OWLAxiom> iterator = axioms.iterator();
        while (!found && iterator.hasNext()) {
            OWLAxiom anAxiom = iterator.next();
            Iterator<? extends OWLAxiom> anotherIterator = axioms.iterator();
            while (!found && anotherIterator.hasNext()) {
                OWLAxiom anotherAxiom = anotherIterator.next();
                if (anAxiom != anotherAxiom) {
                    reports.add(structuralDifference.getTopDifferences(anAxiom, anotherAxiom));
                }
                found = reports.size() > 1;
            }
        }
        return found;
    }
}
