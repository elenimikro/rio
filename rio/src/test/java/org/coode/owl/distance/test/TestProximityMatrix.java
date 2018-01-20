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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.coode.basetest.TestHelper;
import org.coode.distance.Distance;
import org.coode.distance.entityrelevance.DefaultOWLEntityRelevancePolicy;
import org.coode.distance.owl.AxiomBasedDistance;
import org.coode.pair.filter.PairFilter;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.ProximityMatrix;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.WardsProximityMeasureFactory;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
@SuppressWarnings("javadoc")
public class TestProximityMatrix {
    @Test
    public void testAllDistances() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        Set<OWLOntology> ontologies = ontologyManager.getOntologies();
        AxiomBasedDistance axiomBasedDistance =
            new AxiomBasedDistance(ontologies, ontologyManager.getOWLDataFactory(),
                DefaultOWLEntityRelevancePolicy.getAlwaysRelevantPolicy(), ontologyManager);
        ProximityMatrix<OWLEntity> distanceMatrix =
            new SimpleProximityMatrix<>(ontology.getSignature(), axiomBasedDistance);
        for (OWLEntity owlEntity : ontology.getSignature()) {
            for (OWLEntity anotherOWLEntity : ontology.getSignature()) {
                double distance = axiomBasedDistance.getDistance(owlEntity, anotherOWLEntity);
                assertTrue(distanceMatrix.getDistance(owlEntity, anotherOWLEntity) == distance);
            }
        }
        axiomBasedDistance.dispose();
    }

    @Test
    public void testAllDistancesClustering() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        Set<OWLOntology> ontologies = ontologyManager.getOntologies();
        final AxiomBasedDistance axiomBasedDistance =
            new AxiomBasedDistance(ontologies, ontologyManager.getOWLDataFactory(),
                DefaultOWLEntityRelevancePolicy.getAlwaysRelevantPolicy(), ontologyManager);
        ProximityMatrix<OWLEntity> distanceMatrix =
            new SimpleProximityMatrix<>(ontology.getSignature(), axiomBasedDistance);
        PairFilter<Collection<? extends OWLEntity>> pairFilter = (first, second) -> true;
        Set<Collection<? extends OWLEntity>> newObjects = new LinkedHashSet<>();
        for (OWLEntity object : distanceMatrix.getObjects()) {
            newObjects.add(Collections.singleton(object));
        }
        Distance<Collection<? extends OWLEntity>> singletonDistance =
            (a, b) -> axiomBasedDistance.getDistance(a.iterator().next(), b.iterator().next());
        ClusteringProximityMatrix<OWLEntity> clusteringMatrix = ClusteringProximityMatrix.build(
            distanceMatrix, new WardsProximityMeasureFactory(), pairFilter,
            PairFilterBasedComparator.build(pairFilter, newObjects, singletonDistance));
        for (OWLEntity owlEntity : ontology.getSignature()) {
            for (OWLEntity anotherOWLEntity : ontology.getSignature()) {
                double distance = axiomBasedDistance.getDistance(owlEntity, anotherOWLEntity);
                assertTrue(clusteringMatrix.getDistance(Collections.singletonList(owlEntity),
                    Collections.singletonList(anotherOWLEntity)) == distance);
            }
        }
        axiomBasedDistance.dispose();
    }

    @Test
    public void testAllDistancesReduced() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        Set<OWLOntology> ontologies = ontologyManager.getOntologies();
        AxiomBasedDistance axiomBasedDistance =
            new AxiomBasedDistance(ontologies, ontologyManager.getOWLDataFactory(),
                DefaultOWLEntityRelevancePolicy.getAlwaysRelevantPolicy(), ontologyManager);
        ProximityMatrix<OWLEntity> distanceMatrix =
            new SimpleProximityMatrix<>(ontology.getSignature(), axiomBasedDistance);
        ProximityMatrix<OWLEntity> reduced = distanceMatrix.reduce((first, second) -> true);
        for (OWLEntity owlEntity : ontology.getSignature()) {
            for (OWLEntity anotherOWLEntity : ontology.getSignature()) {
                double distance = axiomBasedDistance.getDistance(owlEntity, anotherOWLEntity);
                assertTrue(reduced.getDistance(owlEntity, anotherOWLEntity) == distance);
            }
        }
        axiomBasedDistance.dispose();
    }
}
