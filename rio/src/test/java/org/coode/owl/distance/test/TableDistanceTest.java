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
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.add;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.coode.basetest.TestHelper;
import org.coode.distance.Distance;
import org.coode.distance.TableDistance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.pair.Pair;
import org.coode.pair.filter.PairFilter;
import org.coode.pair.filter.commons.DistanceThresholdBasedFilter;
import org.coode.proximitymatrix.CentroidProximityMeasureFactory;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;
import org.coode.utils.EntityComparator;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
@SuppressWarnings("javadoc")
public class TableDistanceTest {
    @Test
    public void testSimpleDistanceMatrixVSCollectionSingleton() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        Set<OWLEntity> entities = new TreeSet<>(new EntityComparator());
        add(entities, ontologyManager.ontologies().flatMap(OWLOntology::signature));
        OWLEntityReplacer owlEntityReplacer =
            new OWLEntityReplacer(ontologyManager.getOWLDataFactory(),
                new ReplacementByKindStrategy(ontologyManager.getOWLDataFactory()));
        final AxiomRelevanceAxiomBasedDistance distance = new AxiomRelevanceAxiomBasedDistance(
            ontologyManager.ontologies(), owlEntityReplacer, ontologyManager);
        SimpleProximityMatrix<OWLEntity> distanceMatrix =
            new SimpleProximityMatrix<>(entities, distance);
        Set<Collection<? extends OWLEntity>> newObjects = new LinkedHashSet<>();
        for (OWLEntity object : distanceMatrix.getObjects()) {
            newObjects.add(Collections.singletonList(object));
        }
        Distance<Collection<? extends OWLEntity>> singletonDistance =
            (a, b) -> distance.getDistance(a.iterator().next(), b.iterator().next());
        for (OWLEntity owlEntity : entities) {
            for (OWLEntity otherEntity : entities) {
                assertTrue(String.format("Mismatch between %s and %s", owlEntity, otherEntity),
                    distanceMatrix.getDistance(owlEntity, otherEntity) == singletonDistance
                        .getDistance(Collections.singleton(owlEntity),
                            Collections.singleton(otherEntity)));
            }
        }
        distance.dispose();
    }

    @Test
    public void testSimpleDistanceMatrixVSTableDistance() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        Set<OWLEntity> entities = new TreeSet<>(new EntityComparator());
        add(entities, ontologyManager.ontologies().flatMap(OWLOntology::signature));
        OWLEntityReplacer owlEntityReplacer =
            new OWLEntityReplacer(ontologyManager.getOWLDataFactory(),
                new ReplacementByKindStrategy(ontologyManager.getOWLDataFactory()));
        AxiomRelevanceAxiomBasedDistance distance = new AxiomRelevanceAxiomBasedDistance(
            ontologyManager.ontologies(), owlEntityReplacer, ontologyManager);
        SimpleProximityMatrix<OWLEntity> distanceMatrix =
            new SimpleProximityMatrix<>(entities, distance);
        TableDistance<OWLEntity> tableDistance =
            new TableDistance<>(entities, distanceMatrix.getData());
        for (OWLEntity owlEntity : entities) {
            for (OWLEntity otherEntity : entities) {
                assertTrue(String.format("Mismatch between %s and %s", owlEntity, otherEntity),
                    distanceMatrix.getDistance(owlEntity, otherEntity) == tableDistance
                        .getDistance(owlEntity, otherEntity));
            }
        }
        distance.dispose();
    }

    @Test
    public void testClusteringMatrixVSTableDistance() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        Set<OWLEntity> entities = new TreeSet<>(new EntityComparator());
        add(entities, ontologyManager.ontologies().flatMap(OWLOntology::signature));
        OWLEntityReplacer owlEntityReplacer =
            new OWLEntityReplacer(ontologyManager.getOWLDataFactory(),
                new ReplacementByKindStrategy(ontologyManager.getOWLDataFactory()));
        final AxiomRelevanceAxiomBasedDistance distance = new AxiomRelevanceAxiomBasedDistance(
            ontologyManager.ontologies(), owlEntityReplacer, ontologyManager);
        SimpleProximityMatrix<OWLEntity> distanceMatrix =
            new SimpleProximityMatrix<>(entities, distance);
        TableDistance<OWLEntity> tableDistance =
            new TableDistance<>(entities, distanceMatrix.getData());
        Set<Collection<? extends OWLEntity>> newObjects = new LinkedHashSet<>();
        for (OWLEntity object : distanceMatrix.getObjects()) {
            newObjects.add(Collections.singletonList(object));
        }
        Distance<Collection<? extends OWLEntity>> singletonDistance =
            (a, b) -> distance.getDistance(a.iterator().next(), b.iterator().next());
        PairFilter<Collection<? extends OWLEntity>> filter = DistanceThresholdBasedFilter
            .build(new TableDistance<>(entities, distanceMatrix.getData()), 1);
        ClusteringProximityMatrix<OWLEntity> clusteringMatrix =
            ClusteringProximityMatrix.build(distanceMatrix, new CentroidProximityMeasureFactory(),
                filter, PairFilterBasedComparator.build(filter, newObjects, singletonDistance));
        for (OWLEntity owlEntity : entities) {
            for (OWLEntity otherEntity : entities) {
                assertTrue(String.format("Mismatch between %s and %s", owlEntity, otherEntity),
                    clusteringMatrix.getDistance(Collections.singletonList(owlEntity),
                        Collections.singletonList(otherEntity)) == tableDistance
                            .getDistance(owlEntity, otherEntity));
            }
        }
        distance.dispose();
    }

    @Test
    public void testClusteringMatrixVSTableDistanceAfterAgglomeration() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        Set<OWLEntity> entities = new TreeSet<>(new EntityComparator());
        add(entities, ontologyManager.ontologies().flatMap(OWLOntology::signature));
        OWLEntityReplacer owlEntityReplacer =
            new OWLEntityReplacer(ontologyManager.getOWLDataFactory(),
                new ReplacementByKindStrategy(ontologyManager.getOWLDataFactory()));
        final AxiomRelevanceAxiomBasedDistance distance = new AxiomRelevanceAxiomBasedDistance(
            ontologyManager.ontologies(), owlEntityReplacer, ontologyManager);
        SimpleProximityMatrix<OWLEntity> distanceMatrix =
            new SimpleProximityMatrix<>(entities, distance);
        TableDistance<OWLEntity> tableDistance =
            new TableDistance<>(entities, distanceMatrix.getData());
        Set<Collection<? extends OWLEntity>> newObjects = new LinkedHashSet<>();
        for (OWLEntity object : distanceMatrix.getObjects()) {
            newObjects.add(Collections.singletonList(object));
        }
        Distance<Collection<? extends OWLEntity>> singletonDistance =
            (a, b) -> distance.getDistance(a.iterator().next(), b.iterator().next());
        PairFilter<Collection<? extends OWLEntity>> filter = DistanceThresholdBasedFilter
            .build(new TableDistance<>(entities, distanceMatrix.getData()), 1);
        ClusteringProximityMatrix<OWLEntity> clusteringMatrix =
            ClusteringProximityMatrix.build(distanceMatrix, new CentroidProximityMeasureFactory(),
                filter, PairFilterBasedComparator.build(filter, newObjects, singletonDistance));
        Pair<Collection<? extends OWLEntity>> minimumDistancePair =
            clusteringMatrix.getMinimumDistancePair();
        int i = 1;
        while (minimumDistancePair != null
            && filter.accept(minimumDistancePair.getFirst(), minimumDistancePair.getSecond())) {
            clusteringMatrix = clusteringMatrix.agglomerate(filter);
            for (OWLEntity owlEntity : new ArrayList<>(entities)) {
                for (OWLEntity otherEntity : new ArrayList<>(entities)) {
                    if (!minimumDistancePair.getFirst().contains(owlEntity)
                        && !minimumDistancePair.getSecond().contains(owlEntity)
                        && !minimumDistancePair.getFirst().contains(otherEntity)
                        && !minimumDistancePair.getSecond().contains(otherEntity)) {
                        double clusteringMatrixDistance =
                            clusteringMatrix.getDistance(Collections.singletonList(owlEntity),
                                Collections.singletonList(otherEntity));
                        assertTrue(
                            String.format(" Agglomeration %d Mismatch between %s and %s",
                                Integer.valueOf(i), owlEntity, otherEntity),
                            clusteringMatrixDistance == tableDistance.getDistance(owlEntity,
                                otherEntity));
                    } else if (minimumDistancePair.getFirst().contains(owlEntity)
                        || minimumDistancePair.getSecond().contains(owlEntity)) {
                        entities.remove(owlEntity);
                    } else if (minimumDistancePair.getFirst().contains(otherEntity)
                        || minimumDistancePair.getSecond().contains(otherEntity)) {
                        entities.remove(otherEntity);
                    }
                }
            }
            minimumDistancePair = clusteringMatrix.getMinimumDistancePair();
            i++;
        }
        distance.dispose();
    }
}
