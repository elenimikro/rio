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
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.coode.basetest.TestHelper;
import org.coode.distance.Utils;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.utils.EntityComparator;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
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
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        Set<OWLOntology> ontologies = ontologyManager.getOntologies();
        Set<OWLEntity> entities = new TreeSet<>(new EntityComparator());
        for (OWLOntology o : ontologyManager.getOntologies()) {
            entities.addAll(o.getSignature());
        }
        OWLEntityReplacer owlEntityReplacer =
            new OWLEntityReplacer(ontologyManager.getOWLDataFactory(),
                new ReplacementByKindStrategy(ontologyManager.getOWLDataFactory()));
        AxiomRelevanceAxiomBasedDistance distance =
            new AxiomRelevanceAxiomBasedDistance(ontologies, owlEntityReplacer, ontologyManager);
        MultiMap<OWLEntity, OWLEntity> equivalenceClasses =
            Utils.getEquivalenceClasses(entities, distance);
        int i = 0;
        for (OWLEntity key : equivalenceClasses.keySet()) {
            Collection<OWLEntity> set = equivalenceClasses.get(key);
            System.out.println(String.format("Equivalence class no %d %s", i++, render(set)));
        }
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
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        Set<OWLOntology> ontologies = ontologyManager.getOntologies();
        Set<OWLEntity> entities = new TreeSet<>(new EntityComparator());
        for (OWLOntology o : ontologyManager.getOntologies()) {
            entities.addAll(o.getSignature());
        }
        OWLEntityReplacer owlEntityReplacer =
            new OWLEntityReplacer(ontologyManager.getOWLDataFactory(),
                new ReplacementByKindStrategy(ontologyManager.getOWLDataFactory()));
        AxiomRelevanceAxiomBasedDistance distance =
            new AxiomRelevanceAxiomBasedDistance(ontologies, owlEntityReplacer, ontologyManager);
        MultiMap<OWLEntity, OWLEntity> equivalenceClasses =
            Utils.getEquivalenceClasses(entities, distance);
        int i = 0;
        for (OWLEntity key : equivalenceClasses.keySet()) {
            Collection<OWLEntity> set = equivalenceClasses.get(key);
            System.out.println(String.format("Equivalence class no %d %s", i++, render(set)));
        }
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
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        // File file = new File("code/test/resources//c16.rdf.owl");
        // IOUtils.loadIRIMappers(Collections.singleton(IRI.create(file)),
        // ontologyManager);
        ontologyManager
            .loadOntologyFromOntologyDocument(getClass().getResourceAsStream("/c16.rdf.owl"));
        Set<OWLOntology> ontologies = ontologyManager.getOntologies();
        Set<OWLEntity> entities = new TreeSet<>(new EntityComparator());
        for (OWLOntology ontology : ontologyManager.getOntologies()) {
            entities.addAll(ontology.getSignature());
        }
        OWLEntityReplacer owlEntityReplacer =
            new OWLEntityReplacer(ontologyManager.getOWLDataFactory(),
                new ReplacementByKindStrategy(ontologyManager.getOWLDataFactory()));
        AxiomRelevanceAxiomBasedDistance distance =
            new AxiomRelevanceAxiomBasedDistance(ontologies, owlEntityReplacer, ontologyManager);
        MultiMap<OWLEntity, OWLEntity> equivalenceClasses =
            Utils.getEquivalenceClasses(entities, distance);
        int i = 0;
        for (OWLEntity key : equivalenceClasses.keySet()) {
            Collection<OWLEntity> set = equivalenceClasses.get(key);
            System.out.println(String.format("Equivalence class no %d %s", i++, render(set)));
        }
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

    private static String render(Collection<? extends OWLEntity> cluster) {
        Formatter out = new Formatter();
        Iterator<? extends OWLEntity> iterator = cluster.iterator();
        while (iterator.hasNext()) {
            ManchesterOWLSyntaxOWLObjectRendererImpl renderer =
                new ManchesterOWLSyntaxOWLObjectRendererImpl();
            OWLEntity owlEntity = iterator.next();
            out.format("%s%s", renderer.render(owlEntity), iterator.hasNext() ? ", " : "");
        }
        return out.toString();
    }
}
