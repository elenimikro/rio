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
package org.coode.owl.generalise.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.AxiomAtomicDecompositionGeneralisationTreeNode;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.structural.StructuralOWLObjectGeneralisation;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.coode.utils.Utils;
import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.demost.ui.adextension.ChiaraAtomicDecomposition;
import uk.ac.manchester.cs.demost.ui.adextension.ChiaraDecompositionAlgorithm;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

public class TestAxiomAtomicDecompositionGeneralisationTree extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ToStringRenderer.getInstance().setRenderer(new ManchesterSyntaxRenderer());
	}

	public void testAxiomAtomicDecompositionGeneralisationTree() {
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology;
		int generalisationCount = 0;
		try {
			ontology = ontologyManager
					.loadOntology(IRI
							.create("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl"));
			OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
			Set<OWLAxiom> axioms = ontology.getAxioms();
			MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
			ConstraintSystem constraintSystem = factory.createConstraintSystem();
			for (OWLAxiom axiom : axioms) {
				if (!axiom.getAxiomType().equals(AxiomType.DECLARATION)) {
					StructuralOWLObjectGeneralisation generalisation = new StructuralOWLObjectGeneralisation(
							new OntologyManagerBasedOWLEntityProvider(ontologyManager),
							constraintSystem);
					OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
					generalisationMap.put(generalised, new OWLAxiomInstantiation(axiom,
							generalisation.getSubstitutions()));
					generalisationCount++;
				}
			}
			assertTrue(generalisationCount > 1);
			System.out.printf("Generalised over %d axioms\n", generalisationCount);
			Set<OWLAxiom> equivalentOWLAxioms = new HashSet<OWLAxiom>();
			for (OWLAxiom owlAxiom : generalisationMap.keySet()) {
				if (owlAxiom.isOfType(AxiomType.EQUIVALENT_CLASSES)) {
					equivalentOWLAxioms.add(owlAxiom);
					System.out.println(owlAxiom);
				}
			}
			OWLAxiom generalisation = new ArrayList<OWLAxiom>(equivalentOWLAxioms).get(7);
			System.out.println();
			System.out.println(generalisation);
			ChiaraDecompositionAlgorithm chiaraDecompositionAlgorithm = new ChiaraDecompositionAlgorithm(
					ModuleType.BOT);
			AxiomAtomicDecompositionGeneralisationTreeNode root = new AxiomAtomicDecompositionGeneralisationTreeNode(
					generalisation, generalisationMap.get(generalisation),
					constraintSystem, ontologyManager,
					(ChiaraAtomicDecomposition) chiaraDecompositionAlgorithm.decompose(
							ontologyManager, ontology));
			assertFalse(root.getChildren().isEmpty());
			Utils.printNode(root, System.out);
			System.out.println(">>Instantiations:");
			Set<OWLAxiomInstantiation> instantiations = root.getInstantiations();
			int i = 1;
			for (OWLAxiomInstantiation instantiation : instantiations) {
				System.out.print(i + ". ");
				System.out.println(instantiation.getAxiom());
				i++;
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testSubClassAxiomAtomicDecompositionGeneralisationTree() {
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology;
		int generalisationCount = 0;
		try {
			ontology = ontologyManager
					.loadOntology(IRI
							.create("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl"));
			OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
			Set<OWLAxiom> axioms = ontology.getAxioms();
			MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
			ConstraintSystem constraintSystem = factory.createConstraintSystem();
			for (OWLAxiom axiom : axioms) {
				if (!axiom.getAxiomType().equals(AxiomType.DECLARATION)) {
					StructuralOWLObjectGeneralisation generalisation = new StructuralOWLObjectGeneralisation(
							new OntologyManagerBasedOWLEntityProvider(ontologyManager),
							constraintSystem);
					OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
					generalisationMap.put(generalised, new OWLAxiomInstantiation(axiom,
							generalisation.getSubstitutions()));
					generalisationCount++;
				}
			}
			assertTrue(generalisationCount > 1);
			System.out.printf("Generalised over %d axioms\n", generalisationCount);
			Set<OWLAxiom> owlSubClassAxioms = new HashSet<OWLAxiom>();
			for (OWLAxiom owlAxiom : generalisationMap.keySet()) {
				if (owlAxiom.isOfType(AxiomType.SUBCLASS_OF)) {
					owlSubClassAxioms.add(owlAxiom);
					System.out.println(owlAxiom);
				}
			}
			OWLAxiom generalisation = new ArrayList<OWLAxiom>(owlSubClassAxioms).get(2);
			System.out.println();
			System.out.println(generalisation);
			ChiaraDecompositionAlgorithm chiaraDecompositionAlgorithm = new ChiaraDecompositionAlgorithm(
					ModuleType.BOT);
			AxiomAtomicDecompositionGeneralisationTreeNode root = new AxiomAtomicDecompositionGeneralisationTreeNode(
					generalisation, generalisationMap.get(generalisation),
					constraintSystem, ontologyManager,
					(ChiaraAtomicDecomposition) chiaraDecompositionAlgorithm.decompose(
							ontologyManager, ontology));
			assertFalse(root.getChildren().isEmpty());
			Utils.printNode(root, System.out);
			System.out.println(">>Instantiations:");
			Set<OWLAxiomInstantiation> instantiations = root.getInstantiations();
			int i = 1;
			for (OWLAxiomInstantiation instantiation : instantiations) {
				System.out.print(i + ". ");
				System.out.println(instantiation.getAxiom());
				i++;
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testAllNodesOfAxiomAtomicDecompositionGeneralisationTree() {
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology;
		int generalisationCount = 0;
		try {
			ontology = ontologyManager
					.loadOntology(IRI
							.create("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl"));
			OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
			Set<OWLAxiom> axioms = ontology.getAxioms();
			MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
			ConstraintSystem constraintSystem = factory.createConstraintSystem();
			for (OWLAxiom axiom : axioms) {
				if (!axiom.getAxiomType().equals(AxiomType.DECLARATION)) {
					StructuralOWLObjectGeneralisation generalisation = new StructuralOWLObjectGeneralisation(
							new OntologyManagerBasedOWLEntityProvider(ontologyManager),
							constraintSystem);
					OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
					generalisationMap.put(generalised, new OWLAxiomInstantiation(axiom,
							generalisation.getSubstitutions()));
					generalisationCount++;
				}
			}
			assertTrue(generalisationCount > 1);
			System.out.printf("Generalised over %d axioms\n", generalisationCount);
			Set<OWLAxiom> generalisations = new HashSet<OWLAxiom>();
			for (OWLAxiom owlAxiom : generalisationMap.keySet()) {
				generalisations.add(owlAxiom);
				System.out.println(owlAxiom);
			}
			ChiaraDecompositionAlgorithm chiaraDecompositionAlgorithm = new ChiaraDecompositionAlgorithm(
					ModuleType.BOT);
			for (OWLAxiom generalisation : generalisations) {
				AxiomAtomicDecompositionGeneralisationTreeNode root = new AxiomAtomicDecompositionGeneralisationTreeNode(
						generalisation, generalisationMap.get(generalisation),
						constraintSystem, ontologyManager,
						(ChiaraAtomicDecomposition) chiaraDecompositionAlgorithm
								.decompose(ontologyManager, ontology));
				//assertTrue(root.getChildren().isEmpty());
				Utils.printNode(root, System.out);
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
