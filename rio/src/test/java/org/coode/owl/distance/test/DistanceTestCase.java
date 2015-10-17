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

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import org.coode.distance.owl.AbstractAxiomBasedDistance;
import org.junit.Before;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
public abstract class DistanceTestCase {
    protected static final String pizza_ns = "http://www.co-ode.org/ontologies/pizza/pizza.owl#";

    protected abstract DistanceBuilder getDistanceBuilder();

    private OWLOntologyManager ontologyManager;

    protected OWLOntologyManager getOntologyManager() {
        return ontologyManager;
    }

    @Before
    public void setUp() {
        // ToStringRenderer.getInstance().setRenderer(new
        // ManchesterSyntaxRenderer());
    }

    public void properTest(AbstractAxiomBasedDistance distance, OWLEntity... entities) {
        for (OWLEntity c : entities) {
            System.out.println(c.toString());
            System.out.println(distance.getAxioms(c));
        }
        for (int i = 0; i < entities.length; i++) {
            for (int j = 0; j < entities.length; j++) {
                double d = distance.getDistance(entities[i], entities[j]);
                if (i == j) {
                    assertTrue(Double.compare(d, 0D) == 0);
                    assertTrue(Double.compare(
                            distance.getDistance(entities[j], entities[i]), 0D) == 0);
                } else {
                    Collection<OWLAxiom> axioms_i = distance.getAxioms(entities[i]);
                    Collection<OWLAxiom> axioms_j = distance.getAxioms(entities[j]);
                    Collection<OWLAxiom> union = new HashSet<OWLAxiom>(
                            distance.getAxioms(entities[i]));
                    union.addAll(axioms_j);
                    axioms_i.retainAll(axioms_j);
                    if (axioms_i.isEmpty()) {
                        assertTrue(entities[i] + "\t" + entities[j]
                                + " expected: 1; actual: " + d,
                                Double.compare(d, 1D) == 0);
                        assertTrue(Double.compare(
                                distance.getDistance(entities[j], entities[i]), 1D) == 0);
                    } else {
                        double inverse_d = distance.getDistance(entities[j], entities[i]);
                        System.out.println("Intersection between " + entities[i]
                                + " and " + entities[j] + " of size " + axioms_i.size());
                        for (OWLAxiom ax : axioms_i) {
                            System.out.println(ax);
                        }
                        System.out.println();
                        System.out.println("Union between " + entities[i] + " and "
                                + entities[j] + " of size " + union.size());
                        for (OWLAxiom ax : union) {
                            System.out.println(ax);
                        }
                        assertTrue("Expected positive was " + d,
                                Double.compare(0D, d) <= 0);
                        assertTrue("Expected <1 was " + d, Double.compare(d, 1D) < 0);
                        assertTrue("Expected equal was " + d + " " + inverse_d,
                                Double.compare(d, inverse_d) == 0);
                    }
                }
            }
        }
    }

    protected OWLOntology getOntology(File f) {
        ontologyManager = OWLManager.createOWLOntologyManager();
        try {
            return ontologyManager.loadOntologyFromOntologyDocument(f);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace(System.out);
            fail("Cannot load ontology: " + f.getName());
            return null;
        }
    }

    protected OWLOntology getOntology(String iri) {
        ontologyManager = OWLManager.createOWLOntologyManager();
        try {
            return ontologyManager.loadOntology(IRI.create(iri));
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace(System.out);
            fail("Cannot load ontology: " + iri);
            return null;
        }
    }

    protected OWLClass[] getClasses(String... strings) {
        OWLDataFactory f = OWLManager.getOWLDataFactory();
        OWLClass[] toReturn = new OWLClass[strings.length];
        for (int i = 0; i < strings.length; i++) {
            toReturn[i] = f.getOWLClass(IRI.create(strings[i]));
        }
        return toReturn;
    }

    protected OWLObjectProperty[] getObjectProperties(String... strings) {
        OWLDataFactory f = OWLManager.getOWLDataFactory();
        OWLObjectProperty[] toReturn = new OWLObjectProperty[strings.length];
        for (int i = 0; i < strings.length; i++) {
            toReturn[i] = f.getOWLObjectProperty(IRI.create(strings[i]));
        }
        return toReturn;
    }

    protected OWLDataProperty[] getDataProperties(String... strings) {
        OWLDataFactory f = OWLManager.getOWLDataFactory();
        OWLDataProperty[] toReturn = new OWLDataProperty[strings.length];
        for (int i = 0; i < strings.length; i++) {
            toReturn[i] = f.getOWLDataProperty(IRI.create(strings[i]));
        }
        return toReturn;
    }

    protected OWLNamedIndividual[] getNamedIndividuals(String... strings) {
        OWLDataFactory f = OWLManager.getOWLDataFactory();
        OWLNamedIndividual[] toReturn = new OWLNamedIndividual[strings.length];
        for (int i = 0; i < strings.length; i++) {
            toReturn[i] = f.getOWLNamedIndividual(IRI.create(strings[i]));
        }
        return toReturn;
    }
}