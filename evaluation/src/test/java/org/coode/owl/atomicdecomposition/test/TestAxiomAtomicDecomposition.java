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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;





import atomicdecomposition.generalise.AxiomAtomicDecompositionGeneralisationTreeNode;
import org.coode.basetest.TestHelper;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.structural.StructuralOWLObjectGeneralisation;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.coode.utils.Utils;
import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.atomicdecomposition.Atom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposerOWLAPITOOLS;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposition;

/** @author eleni */
@SuppressWarnings("javadoc")
public class TestAxiomAtomicDecomposition {
    @Before
public void setUp() throws Exception {
        super.setUp();
        ToStringRenderer.getInstance().setRenderer(new ManchesterSyntaxRenderer());
    }

    private AtomicDecomposition atomicDecomposition;
    @Test
    public void testAxiomAtomicDecompositionDependencies() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        atomicDecomposition = new AtomicDecomposerOWLAPITOOLS(ontology);
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
        Map<OWLEntity, Set<Atom>> termBasedIndex = atomicDecomposition
                .getTermBasedIndex();
        peperoniSausageToppingAtoms.addAll(termBasedIndex.get(peperoniSausageTopping));
        hotSpicedBeefToppingAtoms.addAll(termBasedIndex.get(hotSpicedBeefTopping));
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
    }
    @Test
    public void testSubClassAxiomAtomicDecompositionGeneralisationTree() {
        int generalisationCount = 0;
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
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
        AxiomAtomicDecompositionGeneralisationTreeNode root = new AxiomAtomicDecompositionGeneralisationTreeNode(
                generalisation, generalisationMap.get(generalisation), constraintSystem,
                atomicDecomposition);
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
    }
    @Test
    public void testAllNodesOfAxiomAtomicDecompositionGeneralisationTree() {
        int generalisationCount = 0;
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
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
        for (OWLAxiom generalisation : generalisations) {
            AxiomAtomicDecompositionGeneralisationTreeNode root = new AxiomAtomicDecompositionGeneralisationTreeNode(
                    generalisation, generalisationMap.get(generalisation),
                    constraintSystem, new AtomicDecomposerOWLAPITOOLS(ontology));
            // assertTrue(root.getChildren().isEmpty());
            Utils.printNode(root, System.out);
        }
    }
}
