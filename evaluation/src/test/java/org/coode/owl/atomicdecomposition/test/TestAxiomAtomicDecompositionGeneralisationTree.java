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
import java.util.Set;





import atomicdecomposition.generalise.AxiomAtomicDecompositionGeneralisationTreeNode;
import org.coode.basetest.TestHelper;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.structural.StructuralOWLObjectGeneralisation;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.coode.utils.Utils;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposerOWLAPITOOLS;

/** @author eleni */
@SuppressWarnings("javadoc")
public class TestAxiomAtomicDecompositionGeneralisationTree  {
    @Before
public void setUp() throws Exception {
//        super.setUp();
//        ToStringRenderer.getInstance().setRenderer(new ManchesterSyntaxRenderer());
    }
    @Test
    public void testAxiomAtomicDecompositionGeneralisationTree() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        int generalisationCount = 0;
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
        AxiomAtomicDecompositionGeneralisationTreeNode root = new AxiomAtomicDecompositionGeneralisationTreeNode(
                generalisation, generalisationMap.get(generalisation), constraintSystem,
                new AtomicDecomposerOWLAPITOOLS(ontology));
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
                new AtomicDecomposerOWLAPITOOLS(ontology));
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
}
