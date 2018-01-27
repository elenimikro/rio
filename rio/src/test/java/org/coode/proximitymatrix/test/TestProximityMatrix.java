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
package org.coode.proximitymatrix.test;

import static org.junit.Assert.assertTrue;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.coode.basetest.TestHelper;
import org.coode.distance.Distance;
import org.coode.distance.entityrelevance.AbstractRankingRelevancePolicy;
import org.coode.distance.owl.AxiomBasedDistance;
import org.coode.metrics.owl.OWLEntityPopularityRanking;
import org.coode.pair.Pair;
import org.coode.pair.filter.PairFilter;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.WardsProximityMeasureFactory;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
@SuppressWarnings("javadoc")
public class TestProximityMatrix {
    @Ignore
    @Test
    public void testAgglomerate() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        List<OWLOntology> ontologies = asList(ontology.importsClosure());
        Set<OWLEntity> signature = asSet(ontology.signature());
        final AxiomBasedDistance axiomBasedDistance =
            new AxiomBasedDistance(ontologies, ontologyManager.getOWLDataFactory(),
                AbstractRankingRelevancePolicy.getAbstractRankingRelevancePolicy(
                    new OWLEntityPopularityRanking(signature, ontologies)),
                ontologyManager);
        final SimpleProximityMatrix<OWLEntity> distanceMatrix =
            new SimpleProximityMatrix<>(signature, axiomBasedDistance);
        PairFilter<Collection<? extends OWLEntity>> filter = (first, second) -> {
            Iterator<? extends OWLEntity> iterator = first.iterator();
            boolean found = false;
            while (!found && iterator.hasNext()) {
                OWLEntity owlEntity = iterator.next();
                Iterator<? extends OWLEntity> anotherIterator = second.iterator();
                while (!found && anotherIterator.hasNext()) {
                    OWLEntity anotherOWLEntity = anotherIterator.next();
                    found = !owlEntity.equals(anotherOWLEntity)
                        && distanceMatrix.getDistance(owlEntity, anotherOWLEntity) >= 1;
                }
            }
            return !found;
        };
        Set<Collection<? extends OWLEntity>> newObjects = new LinkedHashSet<>();
        for (OWLEntity object : distanceMatrix.getObjects()) {
            newObjects.add(Collections.singleton(object));
        }
        Distance<Collection<? extends OWLEntity>> singletonDistance =
            (a, b) -> axiomBasedDistance.getDistance(a.iterator().next(), b.iterator().next());
        ClusteringProximityMatrix<OWLEntity> clusteringMatrix =
            ClusteringProximityMatrix.build(distanceMatrix, new WardsProximityMeasureFactory(),
                filter, PairFilterBasedComparator.build(filter, newObjects, singletonDistance));
        List<Collection<? extends OWLEntity>> clusteringMatrixObjects =
            new ArrayList<>(clusteringMatrix.getObjects());
        List<OWLEntity> distanceMatrixObjects = new ArrayList<>(distanceMatrix.getObjects());
        assertTrue(clusteringMatrixObjects.size() == distanceMatrixObjects.size());
        for (int i = 0; i < clusteringMatrixObjects.size(); i++) {
            Collection<? extends OWLEntity> collection = clusteringMatrixObjects.get(i);
            assertTrue(
                String.format("Wrong order %s does not match with %s", collection,
                    distanceMatrixObjects.get(i)),
                collection.contains(distanceMatrixObjects.get(i)));
        }
        Pair<Collection<? extends OWLEntity>> minimumDistancePair =
            clusteringMatrix.getMinimumDistancePair();
        System.out.printf("%s distance %f\n", minimumDistancePair,
            Double.valueOf(clusteringMatrix.getMinimumDistance()));
        ClusteringProximityMatrix<OWLEntity> agglomerated = clusteringMatrix.agglomerate(filter);
        for (OWLEntity owlEntity : signature) {
            for (OWLEntity anotherOWLEntity : signature) {
                double distance = distanceMatrix.getDistance(owlEntity, anotherOWLEntity);
                double agglomeratedDistance = -1;
                if (minimumDistancePair.contains(Collections.singletonList(owlEntity))
                    || minimumDistancePair.contains(Collections.singleton(anotherOWLEntity))) {
                    agglomeratedDistance = 0;
                } else {
                    agglomeratedDistance =
                        agglomerated.getDistance(Collections.singletonList(owlEntity),
                            Collections.singletonList(anotherOWLEntity));
                }
                assertTrue(String.format(
                    "Non corresponding distance between %s (rowIndex distance matrix = %d, agglomerated = %d) and %s  (columnIndex distance matrix = %d, agglomerated = %d) starting distance %f agglomerated %f",
                    owlEntity, Integer.valueOf(distanceMatrix.getRowIndex(owlEntity)),
                    Integer.valueOf(agglomerated.getRowIndex(Collections.singleton(owlEntity))),
                    anotherOWLEntity,
                    Integer.valueOf(distanceMatrix.getColumnIndex(anotherOWLEntity)),
                    Integer.valueOf(
                        agglomerated.getColumnIndex(Collections.singleton(anotherOWLEntity))),
                    Double.valueOf(distance), Double.valueOf(agglomeratedDistance)),
                    minimumDistancePair.contains(Collections.singleton(owlEntity))
                        || minimumDistancePair.contains(Collections.singleton(anotherOWLEntity))
                        || distance == agglomeratedDistance);
            }
        }
        axiomBasedDistance.dispose();
    }
}
