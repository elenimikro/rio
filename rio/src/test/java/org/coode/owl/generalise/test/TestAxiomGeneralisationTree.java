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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.coode.basetest.TestHelper;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.AxiomGeneralisationTreeNode;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.structural.StructuralOWLObjectGeneralisation;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.coode.proximitymatrix.cluster.Utils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
@SuppressWarnings("javadoc")
public class TestAxiomGeneralisationTree {
    @Before
    public void setUp() {
        // super.setUp();
        // ToStringRenderer.getInstance().setRenderer(new
        // ManchesterSyntaxRenderer());
    }

    @Ignore
    @Test
    public void testAxiomGeneralisationTree() {
        AtomicInteger generalisationCount = new AtomicInteger(0);
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
        MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<>();
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        ontology.axioms().filter(Utils::NOT_DECLARATION).forEach(axiom -> {
            StructuralOWLObjectGeneralisation generalisation =
                new StructuralOWLObjectGeneralisation(
                    new OntologyManagerBasedOWLEntityProvider(ontologyManager), constraintSystem);
            OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
            generalisationMap.put(generalised,
                new OWLAxiomInstantiation(axiom, generalisation.getSubstitutions()));
            generalisationCount.incrementAndGet();
        });

        assertTrue(generalisationCount.get() > 1);

        OWLAxiom generalisation = new ArrayList<>(generalisationMap.keySet()).get(2);
        AxiomGeneralisationTreeNode root = new AxiomGeneralisationTreeNode(generalisation,
            generalisationMap.get(generalisation).stream(), constraintSystem);
        assertFalse(root.getChildren().isEmpty());
    }
}
