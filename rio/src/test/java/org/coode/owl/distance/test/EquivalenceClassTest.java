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

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.coode.basetest.TestHelper;
import org.coode.distance.Utils;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.utils.OntologyManagerUtils;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
@SuppressWarnings("javadoc")
public class EquivalenceClassTest {
    @Test
    public void testGetEquivalenceClassesPizza() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager m = ontology.getOWLOntologyManager();
        List<OWLEntity> entities = org.coode.proximitymatrix.cluster.Utils.getSortedSignature(m);
        OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(m.getOWLDataFactory(),
            new ReplacementByKindStrategy(m.getOWLDataFactory()));
        AxiomRelevanceAxiomBasedDistance distance =
            new AxiomRelevanceAxiomBasedDistance(m.ontologies(), owlEntityReplacer, m);
        MultiMap<OWLEntity, OWLEntity> equivalenceClasses =
            Utils.getEquivalenceClasses(entities, distance);
        for (OWLEntity key : equivalenceClasses.keySet()) {
            Collection<OWLEntity> set = equivalenceClasses.get(key);
            for (OWLEntity anotherKey : equivalenceClasses.keySet()) {
                if (key != anotherKey) {
                    Collection<OWLEntity> anotherSet = equivalenceClasses.get(anotherKey);
                    Set<OWLEntity> intersection = new HashSet<>(set);
                    intersection.retainAll(anotherSet);
                    assertTrue(intersection.isEmpty());
                }
            }
        }
        for (OWLEntity owlEntity : entities) {
            boolean found = false;
            Iterator<OWLEntity> iterator = equivalenceClasses.keySet().iterator();
            while (!found && iterator.hasNext()) {
                OWLEntity key = iterator.next();
                Iterator<OWLEntity> it = equivalenceClasses.get(key).iterator();
                while (!found && it.hasNext()) {
                    OWLEntity memeber = it.next();
                    found = memeber == owlEntity;
                }
            }
            assertTrue(String.format("Entity %s is  missing", owlEntity), found);
        }
        distance.dispose();
    }

    @Test
    public void testGetEquivalenceClassesSameDistancePizza() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager m = ontology.getOWLOntologyManager();
        List<OWLEntity> entities = org.coode.proximitymatrix.cluster.Utils.getSortedSignature(m);
        OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(m.getOWLDataFactory(),
            new ReplacementByKindStrategy(m.getOWLDataFactory()));
        AxiomRelevanceAxiomBasedDistance distance =
            new AxiomRelevanceAxiomBasedDistance(m.ontologies(), owlEntityReplacer, m);
        MultiMap<OWLEntity, OWLEntity> equivalenceClasses =
            Utils.getEquivalenceClasses(entities, distance);
        for (OWLEntity key : equivalenceClasses.keySet()) {
            Collection<OWLEntity> set = equivalenceClasses.get(key);
            for (OWLEntity anotherEntity : entities) {
                double d = -1;
                for (OWLEntity owlEntity : set) {
                    double equivalentClassDistance = distance.getDistance(owlEntity, anotherEntity);
                    assertTrue(d == -1 || d == equivalentClassDistance);
                    d = equivalentClassDistance;
                }
            }
        }
        distance.dispose();
    }

    public void testGetEquivalenceClassesTravel() throws Exception {
        OWLOntologyManager m = OntologyManagerUtils.ontologyManager();
        // File file = new File("code/test/resources//c16.rdf.owl");
        // IOUtils.loadIRIMappers(Collections.singleton(IRI.create(file)),
        // ontologyManager);
        m.loadOntologyFromOntologyDocument(getClass().getResourceAsStream("/c16.rdf.owl"));
        List<OWLEntity> entities = org.coode.proximitymatrix.cluster.Utils.getSortedSignature(m);
        OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(m.getOWLDataFactory(),
            new ReplacementByKindStrategy(m.getOWLDataFactory()));
        AxiomRelevanceAxiomBasedDistance distance =
            new AxiomRelevanceAxiomBasedDistance(m.ontologies(), owlEntityReplacer, m);
        MultiMap<OWLEntity, OWLEntity> equivalenceClasses =
            Utils.getEquivalenceClasses(entities, distance);
        for (OWLEntity key : equivalenceClasses.keySet()) {
            Collection<OWLEntity> set = equivalenceClasses.get(key);
            for (OWLEntity anotherKey : equivalenceClasses.keySet()) {
                if (key != anotherKey) {
                    Collection<OWLEntity> anotherSet = equivalenceClasses.get(anotherKey);
                    Set<OWLEntity> intersection = new HashSet<>(set);
                    intersection.retainAll(anotherSet);
                    assertTrue(intersection.isEmpty());
                }
            }
        }
        distance.dispose();
    }
}
