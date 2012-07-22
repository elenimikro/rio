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

import java.io.File;

import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.owl.AbstractAxiomBasedDistance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

public class AxiomRelevanceAxiomBasedDistanceTest extends DistanceTestCase {
    @Override
    protected DistanceBuilder getDistanceBuilder() {
        return new DistanceBuilder() {
            @Override
            public AbstractAxiomBasedDistance getDistance(final OWLOntology o) {
                final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(o
                        .getOWLOntologyManager().getOWLDataFactory(),
                        new ReplacementByKindStrategy(o.getOWLOntologyManager()
                                .getOWLDataFactory()));
                return new AxiomRelevanceAxiomBasedDistance(o.getImportsClosure(),
                        o.getOWLOntologyManager(), owlEntityReplacer);
            }

            @Override
            @SuppressWarnings("unused")
            public AbstractAxiomBasedDistance getDistance(final OWLOntology o,
                    final RelevancePolicy<OWLEntity> rp) {
                return null;
            }
        };
    }

    public void testGetAxiomsMozzarellaTopping() {
        OWLOntology o = getOntology(new File("code/api/test/resources/pizza.owl"));
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o);
        OWLClass[] classes = getClasses(pizza_ns + "MozzarellaTopping");
        properTest(distance, o, classes);
    }

    public void testGetAxiomsPepperTopping() {
        OWLOntology o = getOntology(pizza_iri);
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o);
        OWLClass[] classes = getClasses(pizza_ns + "PepperTopping");
        properTest(distance, o, classes);
    }

    public void testDistanceGarlicToppingPizza() {
        OWLOntology o = getOntology(pizza_iri);
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o);
        OWLClass[] classes = getClasses(pizza_ns + "GarlicTopping", pizza_ns + "Pizza");
        properTest(distance, o, classes);
    }
}
