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
package org.coode.owl.atomicdecomposition.test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.coode.basetest.TestHelper;
import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.atomicdecomposition.Atom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposerOWLAPITOOLS;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposition;

public class TestAtomicDecompositionDependencies extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ToStringRenderer.getInstance().setRenderer(
				new ManchesterSyntaxRenderer());
	}

	public void testAtomicDecompositionDependenciesBetweenToppings() {
		OWLOntology ontology = TestHelper.getPizza();
		OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
		AtomicDecomposition atomicDecomposition = new AtomicDecomposerOWLAPITOOLS(
				ontology);
		OWLClass peperoniSausageTopping = ontologyManager
				.getOWLDataFactory()
				.getOWLClass(
						IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#MeatTopping"));
		OWLClass hotSpicedBeefTopping = ontologyManager
				.getOWLDataFactory()
				.getOWLClass(
						IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#CheeseTopping"));
		HashSet<Atom> peperoniSausageToppingAtoms = new HashSet<Atom>();
		HashSet<Atom> hotSpicedBeefToppingAtoms = new HashSet<Atom>();
		Map<OWLEntity, Set<Atom>> termBasedIndex = atomicDecomposition
				.getTermBasedIndex();
		peperoniSausageToppingAtoms.addAll(termBasedIndex
				.get(peperoniSausageTopping));
		hotSpicedBeefToppingAtoms.addAll(termBasedIndex
				.get(hotSpicedBeefTopping));
		assertFalse(peperoniSausageToppingAtoms.isEmpty());
		assertFalse(hotSpicedBeefToppingAtoms.isEmpty());
		HashSet<Atom> peperoniSausageAtomDependencies = new HashSet<Atom>();
		HashSet<Atom> hotSpicedBeefToppingAtomDependencies = new HashSet<Atom>();
		for (Atom atom : peperoniSausageToppingAtoms) {
			peperoniSausageAtomDependencies.addAll(atomicDecomposition
					.getDependencies(atom));
		}
		peperoniSausageAtomDependencies.removeAll(peperoniSausageToppingAtoms);
		for (Atom atom : hotSpicedBeefToppingAtoms) {
			hotSpicedBeefToppingAtomDependencies.addAll(atomicDecomposition
					.getDependencies(atom));
		}
		hotSpicedBeefToppingAtomDependencies
				.removeAll(hotSpicedBeefToppingAtoms);
		assertFalse(peperoniSausageAtomDependencies.isEmpty());
		assertFalse(hotSpicedBeefToppingAtomDependencies.isEmpty());
		boolean haveEqualDependencies = peperoniSausageAtomDependencies
				.equals(hotSpicedBeefToppingAtomDependencies);
		assertFalse(haveEqualDependencies);
		System.out.println("MeatTopping atom dependencies:");
		for (Atom atom : peperoniSausageAtomDependencies) {
			System.out.println(atom.getSignature());
		}
		System.out.println();
		System.out.println("CheeseTopping atom dependencies:");
		for (Atom atom : hotSpicedBeefToppingAtomDependencies) {
			System.out.println(atom.getSignature());
		}
	}
}
