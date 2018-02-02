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
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.add;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.coode.basetest.TestHelper;
import org.coode.distance.Distance;
import org.coode.distance.TableDistance;
import org.coode.distance.entityrelevance.DefaultOWLEntityRelevancePolicy;
import org.coode.distance.owl.AxiomBasedDistance;
import org.coode.pair.filter.OrPairFilter;
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

/** @author eleni */
@SuppressWarnings("javadoc")
public class ClusteringProximityMatrixTest {
    @Test
    public void testReduce() {
        OWLOntology ontology = TestHelper.getPizza();
        final AxiomBasedDistance distance = new AxiomBasedDistance(Stream.of(ontology),
            ontology.getOWLOntologyManager().getOWLDataFactory(),
            DefaultOWLEntityRelevancePolicy.getAlwaysIrrelevantPolicy(),
            ontology.getOWLOntologyManager());
        Set<OWLEntity> entities = new TreeSet<>(new EntityComparator());
        add(entities, ontology.signature());
        SimpleProximityMatrix<OWLEntity> distanceMatrix =
            new SimpleProximityMatrix<>(entities, distance);
        Set<Collection<? extends OWLEntity>> newObjects = new LinkedHashSet<>();
        for (OWLEntity object : distanceMatrix.getObjects()) {
            newObjects.add(Collections.singleton(object));
        }
        Distance<Collection<? extends OWLEntity>> singletonDistance =
            (a, b) -> distance.getDistance(a.iterator().next(), b.iterator().next());
        PairFilter<Collection<? extends OWLEntity>> filter = DistanceThresholdBasedFilter
            .build(new TableDistance<>(entities, distanceMatrix.getData()), 1);
        ClusteringProximityMatrix<OWLEntity> clusteringMatrix =
            ClusteringProximityMatrix.build(distanceMatrix, new CentroidProximityMeasureFactory(),
                filter, PairFilterBasedComparator.build(filter, newObjects, singletonDistance));
        ClusteringProximityMatrix<OWLEntity> reducedSingleFilter = clusteringMatrix.reduce(filter);
        ClusteringProximityMatrix<OWLEntity> reducedMultipleFilter = clusteringMatrix
            .reduce(OrPairFilter.build((first, second) -> first.size() > 1, filter));
        List<Collection<? extends OWLEntity>> difference =
            new ArrayList<>(reducedMultipleFilter.getObjects());
        difference.removeAll(reducedSingleFilter.getObjects());
        assertTrue(String.format("Non identical, the difference is %s", difference),
            reducedSingleFilter.getObjects().size() == reducedMultipleFilter.getObjects().size());
    }
}
