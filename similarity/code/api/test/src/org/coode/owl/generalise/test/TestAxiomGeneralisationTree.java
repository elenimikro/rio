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
import java.util.Set;

import junit.framework.TestCase;

import org.coode.basetest.TestHelper;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.AxiomGeneralisationTreeNode;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.structural.StructuralOWLObjectGeneralisation;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.coode.utils.Utils;
import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

public class TestAxiomGeneralisationTree extends TestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ToStringRenderer.getInstance().setRenderer(new ManchesterSyntaxRenderer());
    }

    public void testAxiomGeneralisationTree() throws OWLOntologyCreationException {
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
        for (OWLAxiom owlAxiom : generalisationMap.keySet()) {
            System.out.println(owlAxiom);
        }
        OWLAxiom generalisation = new ArrayList<OWLAxiom>(generalisationMap.keySet())
                .get(2);
        System.out.println(generalisation);
        AxiomGeneralisationTreeNode root = new AxiomGeneralisationTreeNode(
                generalisation, generalisationMap.get(generalisation), constraintSystem);
        assertFalse(root.getChildren().isEmpty());
        Utils.printNode(root, System.out);
    }
}
