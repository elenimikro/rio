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

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.io.File;
import java.util.List;

import org.coode.basetest.TestHelper;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.owl.AbstractAxiomBasedDistance;
import org.coode.distance.owl.StructuralAxiomRelevanceAxiomBasedDistance;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/** @author eleni */
@SuppressWarnings("javadoc")
public class StructuralAxiomRelevanceAxiomBasedBasedDistanceTest extends DistanceTestCase {
    @Override
    protected DistanceBuilder getDistanceBuilder() {
        return new DistanceBuilder() {
            @Override
            public AbstractAxiomBasedDistance getDistance(OWLOntology o) {
                return new StructuralAxiomRelevanceAxiomBasedDistance(asList(o.importsClosure()),
                    o.getOWLOntologyManager().getOWLDataFactory(), o.getOWLOntologyManager());
            }

            @Override
            public AbstractAxiomBasedDistance getDistance(OWLOntology o,
                RelevancePolicy<OWLEntity> rp) {
                return null;
            }
        };
    }

    public void testAminoAcid() throws OWLOntologyCreationException {
        String ns = "http://www.co-ode.org/ontologies/amino-acid/2006/05/18/amino-acid.owl#";
        OWLOntology o = getOntology(new File("eswc-ontologies/amino-acid-original.owl"));
        List<OWLClass> classes = getClasses(ns + "Non-Polar", ns + "A");
        properTest(getDistanceBuilder().getDistance(o), classes);
    }

    public void testGetDistance() {
        OWLOntology o = TestHelper.getPizza();
        List<OWLClass> classes = getClasses(pizza_ns + "Margherita", pizza_ns + "Capricciosa",
            pizza_ns + "MozzarellaTopping");
        properTest(getDistanceBuilder().getDistance(o), classes);
    }
}
