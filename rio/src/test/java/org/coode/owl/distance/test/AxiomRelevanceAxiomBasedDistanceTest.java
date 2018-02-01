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
package org.coode.owl.distance.test;

import java.util.List;

import org.coode.basetest.TestHelper;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.owl.AbstractAxiomBasedDistance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/** @author eleni */
@SuppressWarnings("javadoc")
public class AxiomRelevanceAxiomBasedDistanceTest extends DistanceTestCase {
    @Override
    protected DistanceBuilder getDistanceBuilder() {
        return new DistanceBuilder() {
            @Override
            public AbstractAxiomBasedDistance getDistance(OWLOntology o) {
                final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
                    o.getOWLOntologyManager().getOWLDataFactory(),
                    new ReplacementByKindStrategy(o.getOWLOntologyManager().getOWLDataFactory()));
                return new AxiomRelevanceAxiomBasedDistance(o.importsClosure(), owlEntityReplacer,
                    o.getOWLOntologyManager());
            }

            @Override
            public AbstractAxiomBasedDistance getDistance(OWLOntology o,
                RelevancePolicy<OWLEntity> rp) {
                return null;
            }
        };
    }

    public void testGetAxiomsMozzarellaTopping() {
        OWLOntology o = TestHelper.getPizza();
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o);
        List<OWLClass> classes = getClasses(pizza_ns + "MozzarellaTopping");
        properTest(distance, classes);
    }

    public void testGetAxiomsPepperTopping() {
        OWLOntology o = TestHelper.getPizza();
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o);
        List<OWLClass> classes = getClasses(pizza_ns + "PepperTopping");
        properTest(distance, classes);
    }

    public void testDistanceGarlicToppingPizza() {
        OWLOntology o = TestHelper.getPizza();
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o);
        List<OWLClass> classes = getClasses(pizza_ns + "GarlicTopping", pizza_ns + "Pizza");
        properTest(distance, classes);
    }
}
