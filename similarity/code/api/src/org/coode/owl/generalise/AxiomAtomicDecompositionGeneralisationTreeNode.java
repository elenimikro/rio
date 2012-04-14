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
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.demost.ui.adextension.ChiaraAtomicDecomposition;
import edu.arizona.bio5.onto.decomposition.Atom;

public class AxiomAtomicDecompositionGeneralisationTreeNode extends
		DefaultTreeNode<OWLAxiom> implements GeneralisationTreeNode<OWLAxiom> {
	private final Set<OWLAxiomInstantiation> instantiations = new HashSet<OWLAxiomInstantiation>();
	private final ConstraintSystem constraintSystem;
	private final ChiaraAtomicDecomposition atomicDecomposition;
	private final OWLOntologyManager ontologyManager;

	/**
	 * New constructor that creates a generalisation Tree, which splits the
	 * variables according to the dependencies and influences of the atomic
	 * decomposition graph
	 * 
	 * @param userObject
	 * @param instantiations
	 * @param constraintSystem
	 * @param atomicDecompositions
	 */
	public AxiomAtomicDecompositionGeneralisationTreeNode(OWLAxiom userObject,
			Collection<? extends OWLAxiomInstantiation> instantiations,
			ConstraintSystem constraintSystem, OWLOntologyManager ontologyManager,
			ChiaraAtomicDecomposition atomicDecomposition) {
		super(userObject);
		if (instantiations == null) {
			throw new NullPointerException(
					"The colleciton of instantioations cannot be null");
		}
		if (constraintSystem == null) {
			throw new NullPointerException("The constraint system cannot be null");
		}
		if (atomicDecomposition == null) {
			throw new NullPointerException("The atomic decomposition cannot be null");
		}
		this.ontologyManager = ontologyManager;
		this.constraintSystem = constraintSystem;
		this.instantiations.addAll(instantiations);
		this.atomicDecomposition = atomicDecomposition;
		Set<OWLAxiom> extractedAxioms = Utils.extractAxioms(instantiations);
		// check the structural differences. if they differ in more than one
		// positions then doIt=true
		boolean doIt = instantiations.size() > 1
				&& this.areDifferencesWorthIt(extractedAxioms);
		if (doIt) {
			VariableExtractor variableExtractor = new VariableExtractor(constraintSystem,
					false);
			// the userObject is the generalised axiom: e.g. ?class_1 SubClassOf
			// ?class_2
			Set<Variable<?>> variables = variableExtractor.extractVariables(userObject);
			Map<Variable<?>, Set<BindingNode>> splitVaribales = new HashMap<Variable<?>, Set<BindingNode>>();
			// has all the information; variables, instantiated axioms, variable
			// bindings.
			Map<Variable<?>, MultiMap<BindingNode, OWLAxiomInstantiation>> instantiationsMap = new HashMap<Variable<?>, MultiMap<BindingNode, OWLAxiomInstantiation>>();
			// get the variables for every generalisation (e.g. ?class_1,
			// ?class_2, ?objectproperty_1 etc)
			for (Variable<?> inputVariable : variables) {
				Map<BindingNode, AssignmentMap> map = new HashMap<BindingNode, AssignmentMap>();
				Set<Variable<?>> otherVariables = new HashSet<Variable<?>>(variables);
				otherVariables.remove(inputVariable);
				Set<BindingNode> bindingNodes = new HashSet<BindingNode>();
				Set<BindingNode> agreeingBindingNodes = new HashSet<BindingNode>();
				for (OWLAxiomInstantiation owlAxiomInstantiation : instantiations) {
					// get the value of the variable
					OWLObject value = owlAxiomInstantiation.getSubstitutions()
							.get(inputVariable).iterator().next();
					// The BindingNode key includes a variable with its value
					// and the rest of the variables (e.g. [?class_3=Hot,
					// ?class_1, ?class_2, ?objectProperty].)
					BindingNode key = new BindingNode(
							Collections.singleton(new Assignment(inputVariable, value)),
							otherVariables);
					bindingNodes.add(key);
					MultiMap<BindingNode, OWLAxiomInstantiation> inputVariableInstantiationMap = instantiationsMap
							.get(inputVariable);
					if (inputVariableInstantiationMap == null) {
						inputVariableInstantiationMap = new MultiMap<BindingNode, OWLAxiomInstantiation>();
						instantiationsMap.put(inputVariable,
								inputVariableInstantiationMap);
					}
					// The inputVariableBindingInstantiations contain the
					// variable bindings along with the instantiations
					inputVariableInstantiationMap.put(key, owlAxiomInstantiation);
				}
				for (BindingNode bindingNode : bindingNodes) {
					Set<BindingNode> set = new HashSet<BindingNode>();
					for (OWLAxiomInstantiation owlAxiomInstantiation : instantiations) {
						if (owlAxiomInstantiation.agreesWith(bindingNode,
								new SimpleValueComputationParameters(constraintSystem,
										BindingNode.createNewEmptyBindingNode(),
										new QuickFailRuntimeExceptionHandler()))) {
							agreeingBindingNodes.add(bindingNode);
							for (Variable<?> otherVariable : otherVariables) {
								Set<Variable<?>> remainingVariables = new HashSet<Variable<?>>(
										otherVariables);
								remainingVariables.remove(otherVariable);
								OWLObject otherVariableValue = owlAxiomInstantiation
										.getSubstitutions().get(otherVariable).iterator()
										.next();
								set.add(new BindingNode(Collections
										.singleton(new Assignment(otherVariable,
												otherVariableValue)), remainingVariables));
							}
						}
					}
					map.put(bindingNode, new AssignmentMap(set));
				}
				// if (AssignmentMap.areDisjoint(map.values()) &&
				// agreeingBindingNodes.size() > 1 &&
				// haveEqualDependencies(map)) {
				// splitVaribales.put(inputVariable, agreeingBindingNodes);
				// }
				if (agreeingBindingNodes.size() > 1) {
					if (!haveEqualDependencies(agreeingBindingNodes)) {
						splitVaribales.put(inputVariable, agreeingBindingNodes);
					}
				}
			}
			Set<Variable<?>> keySet = splitVaribales.keySet();
			for (Variable<?> variable : keySet) {
				MultiMap<BindingNode, OWLAxiomInstantiation> map = instantiationsMap
						.get(variable);
				this.addChild(new VariableGeneralisationTreeNode(variable, userObject,
						map, constraintSystem));
			}
		}
	}

	private boolean haveEqualDependencies(Set<BindingNode> agreeingBindingNodes) {
		//if (entitiesToAtoms.isEmpty()) buildSignatureForAtoms();
		Set<OWLObject> assignmentSet = new HashSet<OWLObject>();
		for (BindingNode node : agreeingBindingNodes) {
			for (Assignment assignment : node.getAssignments()) {
				assignmentSet.add(assignment.getAssignment());
			}
		}
		//find dependencies for every assignment
		if (assignmentSet.size() > 1) {
			MultiMap<OWLObject, Atom> assignmentAtomDependencies = new MultiMap<OWLObject, Atom>();
			for (OWLObject assignment : assignmentSet) {
				if (assignment instanceof OWLEntity) {
					Collection<Atom> atoms = atomicDecomposition.getEntitiesToAtom().get(
							(OWLEntity) assignment);
					if (atoms != null) {
						Set<Atom> dependencies = new HashSet<Atom>();
						for (Atom atom : atoms) {
							dependencies
									.addAll(atomicDecomposition.getDependencies(atom));
						}
						dependencies.removeAll(atoms);
						assignmentAtomDependencies.setEntry(assignment, dependencies);
					}
				}
			}
			// check if all the objects have the same dependencies
			boolean equal = assignmentAtomDependencies.isValueSetsEqual();
			if (!equal) {
				return false;
			}
		}
		return true;
	}

	private boolean haveEqualDependenciesEx(Map<BindingNode, AssignmentMap> map,
			Set<Variable<?>> otherVariables) {
		Collection<AssignmentMap> assignmentMaps = map.values();
		for (AssignmentMap assignmentMap : assignmentMaps) {
			for (Variable<?> variable : otherVariables) {
				Set<OWLObject> assignments = assignmentMap.get(variable);
				if (assignments.size() > 1) {
					//find dependencies for every assignment
					MultiMap<OWLObject, Atom> assignmentAtomDependencies = new MultiMap<OWLObject, Atom>();
					for (OWLObject assignment : assignments) {
						if (assignment instanceof OWLEntity) {
							Collection<Atom> atoms = atomicDecomposition
									.getEntitiesToAtom().get((OWLEntity) assignment);
							if (atoms != null) {
								Set<Atom> dependencies = new HashSet<Atom>();
								for (Atom atom : atoms) {
									if (atomicDecomposition != null) {
										Set<Atom> set = atomicDecomposition
												.getDependencies(atom);
										if (set.size() > 0) dependencies.addAll(set);
									}
								}
								dependencies.removeAll(atoms);
								assignmentAtomDependencies.setEntry(assignment,
										dependencies);
							}
						}
					}
					// check if all the objects have the same dependencies
					boolean equal = assignmentAtomDependencies.isValueSetsEqual();
					if (!equal) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void accept(GeneralisationTreeNodeVisitor visitor) {
		visitor.visitAxiomAtomicDecompositionGeneralisationTreeNode(this);
	}

	public <P> P accept(GeneralisationTreeNodeVisitorEx<P> visitor) {
		return visitor.visitAxiomAtomicDecompositionGeneralisationTreeNode(this);
	}

	/**
	 * @return the instantiations
	 */
	public Set<OWLAxiomInstantiation> getInstantiations() {
		return new HashSet<OWLAxiomInstantiation>(this.instantiations);
	}

	/**
	 * @return the constraintSystem
	 */
	public ConstraintSystem getConstraintSystem() {
		return this.constraintSystem;
	}

	@Override
	public String render() {
		return this.getUserObject().toString();
	}

	private boolean areDifferencesWorthIt(Collection<? extends OWLAxiom> axioms) {
		if (axioms == null) {
			throw new NullPointerException("The collection cannot be null");
		}
		Set<List<StructuralDifferenceReport>> reports = new HashSet<List<StructuralDifferenceReport>>();
		boolean found = false;
		StructuralDifference structuralDifference = new StructuralDifference();
		Iterator<? extends OWLAxiom> iterator = axioms.iterator();
		while (!found && iterator.hasNext()) {
			OWLAxiom anAxiom = iterator.next();
			Iterator<? extends OWLAxiom> anotherIterator = axioms.iterator();
			while (!found && anotherIterator.hasNext()) {
				OWLAxiom anotherAxiom = anotherIterator.next();
				if (anAxiom != anotherAxiom) {
					reports.add(structuralDifference.getTopDifferences(anAxiom,
							anotherAxiom));
				}
				found = reports.size() > 1;
			}
		}
		return found;
	}
}
