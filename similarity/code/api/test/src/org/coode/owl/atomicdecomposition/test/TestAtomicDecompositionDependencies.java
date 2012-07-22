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

import junit.framework.TestCase;

import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.demost.ui.adextension.ChiaraAtomicDecomposition;
import uk.ac.manchester.cs.demost.ui.adextension.ChiaraDecompositionAlgorithm;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import edu.arizona.bio5.onto.decomposition.Atom;

public class TestAtomicDecompositionDependencies extends TestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ToStringRenderer.getInstance().setRenderer(new ManchesterSyntaxRenderer());
    }

    public void testAxiomAtomicDecompositionDependencies() {
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;
        try {
            ontology = ontologyManager
                    .loadOntology(IRI
                            .create("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl"));
            // OPPLFactory factory = new OPPLFactory(ontologyManager, ontology,
            // null);
            // Set<OWLAxiom> axioms = ontology.getAxioms();
            ChiaraDecompositionAlgorithm chiaraDecompositionAlgorithm = new ChiaraDecompositionAlgorithm(
                    ModuleType.BOT);
            ChiaraAtomicDecomposition atomicDecomposition = (ChiaraAtomicDecomposition) chiaraDecompositionAlgorithm
                    .decompose(ontologyManager, ontology);
            OWLClass peperoniSausageTopping = ontologyManager
                    .getOWLDataFactory()
                    .getOWLClass(
                            IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#PeperoniSausageTopping"));
            OWLClass hotSpicedBeefTopping = ontologyManager
                    .getOWLDataFactory()
                    .getOWLClass(
                            IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#HotSpicedBeefTopping"));
            HashSet<Atom> peperoniSausageToppingAtoms = new HashSet<Atom>();
            HashSet<Atom> hotSpicedBeefToppingAtoms = new HashSet<Atom>();
            peperoniSausageToppingAtoms.addAll(atomicDecomposition.getEntitiesToAtom()
                    .get(peperoniSausageTopping));
            hotSpicedBeefToppingAtoms.addAll(atomicDecomposition.getEntitiesToAtom().get(
                    hotSpicedBeefTopping));
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
            hotSpicedBeefToppingAtomDependencies.removeAll(hotSpicedBeefToppingAtoms);
            assertFalse(peperoniSausageAtomDependencies.isEmpty());
            assertFalse(hotSpicedBeefToppingAtomDependencies.isEmpty());
            boolean haveEqualDependencies = peperoniSausageAtomDependencies
                    .equals(hotSpicedBeefToppingAtomDependencies);
            assertTrue(haveEqualDependencies);
            System.out.println("PeperoniSausage atom dependencies:");
            for (Atom atom : peperoniSausageAtomDependencies) {
                System.out.println(atom.getSignature());
            }
            System.out.println();
            System.out.println("HotSpicedBeefTopping atom dependencies:");
            for (Atom atom : hotSpicedBeefToppingAtomDependencies) {
                System.out.println(atom.getSignature());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testAtomicDecompositionDependenciesBetweenToppings() {
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;
        try {
            ontology = ontologyManager
                    .loadOntology(IRI
                            .create("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl"));
            // OPPLFactory factory = new OPPLFactory(ontologyManager, ontology,
            // null);
            // Set<OWLAxiom> axioms = ontology.getAxioms();
            ChiaraDecompositionAlgorithm chiaraDecompositionAlgorithm = new ChiaraDecompositionAlgorithm(
                    ModuleType.BOT);
            ChiaraAtomicDecomposition atomicDecomposition = (ChiaraAtomicDecomposition) chiaraDecompositionAlgorithm
                    .decompose(ontologyManager, ontology);
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
            peperoniSausageToppingAtoms.addAll(atomicDecomposition.getEntitiesToAtom()
                    .get(peperoniSausageTopping));
            hotSpicedBeefToppingAtoms.addAll(atomicDecomposition.getEntitiesToAtom().get(
                    hotSpicedBeefTopping));
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
            hotSpicedBeefToppingAtomDependencies.removeAll(hotSpicedBeefToppingAtoms);
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
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
