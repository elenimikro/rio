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
import java.util.Collection;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.coode.distance.Utils;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

public class EquivalenceClassTest extends TestCase {
    public void testGetEquivalenceClassesPizza() {
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        try {
            ontologyManager
                    .loadOntology(IRI
                            .create("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl"));
            Set<OWLOntology> ontologies = ontologyManager.getOntologies();
            final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
            Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
                public int compare(final OWLEntity o1, final OWLEntity o2) {
                    return shortFormProvider.getShortForm(o1).compareTo(
                            shortFormProvider.getShortForm(o2));
                }
            });
            for (OWLOntology ontology : ontologyManager.getOntologies()) {
                entities.addAll(ontology.getSignature());
            }
            final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
                    ontologyManager.getOWLDataFactory(), new ReplacementByKindStrategy(
                            ontologyManager.getOWLDataFactory()));
            final AxiomRelevanceAxiomBasedDistance distance = new AxiomRelevanceAxiomBasedDistance(
                    ontologies, ontologyManager, owlEntityReplacer);
            MultiMap<OWLEntity, OWLEntity> equivalenceClasses = Utils
                    .getEquivalenceClasses(entities, distance);
            int i = 0;
            for (OWLEntity key : equivalenceClasses.keySet()) {
                Collection<OWLEntity> set = equivalenceClasses.get(key);
                System.out.println(String.format("Equivalence class no %d %s", i++,
                        render(set)));
            }
            for (OWLEntity key : equivalenceClasses.keySet()) {
                Collection<OWLEntity> set = equivalenceClasses.get(key);
                for (OWLEntity anotherKey : equivalenceClasses.keySet()) {
                    if (key != anotherKey) {
                        Collection<OWLEntity> anotherSet = equivalenceClasses
                                .get(anotherKey);
                        Set<OWLEntity> intersection = new HashSet<OWLEntity>(set);
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
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testGetEquivalenceClassesSameDistancePizza() {
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        try {
            ontologyManager
                    .loadOntology(IRI
                            .create("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl"));
            Set<OWLOntology> ontologies = ontologyManager.getOntologies();
            final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
            Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
                public int compare(final OWLEntity o1, final OWLEntity o2) {
                    return shortFormProvider.getShortForm(o1).compareTo(
                            shortFormProvider.getShortForm(o2));
                }
            });
            for (OWLOntology ontology : ontologyManager.getOntologies()) {
                entities.addAll(ontology.getSignature());
            }
            final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
                    ontologyManager.getOWLDataFactory(), new ReplacementByKindStrategy(
                            ontologyManager.getOWLDataFactory()));
            final AxiomRelevanceAxiomBasedDistance distance = new AxiomRelevanceAxiomBasedDistance(
                    ontologies, ontologyManager, owlEntityReplacer);
            MultiMap<OWLEntity, OWLEntity> equivalenceClasses = Utils
                    .getEquivalenceClasses(entities, distance);
            int i = 0;
            for (OWLEntity key : equivalenceClasses.keySet()) {
                Collection<OWLEntity> set = equivalenceClasses.get(key);
                System.out.println(String.format("Equivalence class no %d %s", i++,
                        render(set)));
            }
            for (OWLEntity key : equivalenceClasses.keySet()) {
                Collection<OWLEntity> set = equivalenceClasses.get(key);
                for (OWLEntity anotherEntity : entities) {
                    double d = -1;
                    for (OWLEntity owlEntity : set) {
                        double equivalentClassDistance = distance.getDistance(owlEntity,
                                anotherEntity);
                        assertTrue(d == -1 || d == equivalentClassDistance);
                        d = equivalentClassDistance;
                    }
                }
            }
            distance.dispose();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testGetEquivalenceClassesTravel() throws Exception {
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        File file = new File("code/test/resources/c16.rdf.owl");
        File parentFile = file.getParentFile();
        if (parentFile.isDirectory()) {
            ontologyManager.addIRIMapper(new AutoIRIMapper(parentFile, true));
        }
        ontologyManager.loadOntologyFromOntologyDocument(file);
        Set<OWLOntology> ontologies = ontologyManager.getOntologies();
        final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
            public int compare(final OWLEntity o1, final OWLEntity o2) {
                return shortFormProvider.getShortForm(o1).compareTo(
                        shortFormProvider.getShortForm(o2));
            }
        });
        for (OWLOntology ontology : ontologyManager.getOntologies()) {
            entities.addAll(ontology.getSignature());
        }
        final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
                ontologyManager.getOWLDataFactory(), new ReplacementByKindStrategy(
                        ontologyManager.getOWLDataFactory()));
        final AxiomRelevanceAxiomBasedDistance distance = new AxiomRelevanceAxiomBasedDistance(
                ontologies, ontologyManager, owlEntityReplacer);
        MultiMap<OWLEntity, OWLEntity> equivalenceClasses = Utils.getEquivalenceClasses(
                entities, distance);
        int i = 0;
        for (OWLEntity key : equivalenceClasses.keySet()) {
            Collection<OWLEntity> set = equivalenceClasses.get(key);
            System.out.println(String.format("Equivalence class no %d %s", i++,
                    render(set)));
        }
        for (OWLEntity key : equivalenceClasses.keySet()) {
            Collection<OWLEntity> set = equivalenceClasses.get(key);
            for (OWLEntity anotherKey : equivalenceClasses.keySet()) {
                if (key != anotherKey) {
                    Collection<OWLEntity> anotherSet = equivalenceClasses.get(anotherKey);
                    Set<OWLEntity> intersection = new HashSet<OWLEntity>(set);
                    intersection.retainAll(anotherSet);
                    assertTrue(intersection.isEmpty());
                }
            }
        }
        distance.dispose();
    }

    private static String render(final Collection<? extends OWLEntity> cluster) {
        Formatter out = new Formatter();
        Iterator<? extends OWLEntity> iterator = cluster.iterator();
        while (iterator.hasNext()) {
            ManchesterOWLSyntaxOWLObjectRendererImpl renderer = new ManchesterOWLSyntaxOWLObjectRendererImpl();
            OWLEntity owlEntity = iterator.next();
            out.format("%s%s", renderer.render(owlEntity), iterator.hasNext() ? ", " : "");
        }
        return out.toString();
    }
}
