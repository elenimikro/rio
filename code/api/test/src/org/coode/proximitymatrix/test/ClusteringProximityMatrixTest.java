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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
import org.coode.utils.owl.IOUtils;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
@SuppressWarnings("javadoc")
public class ClusteringProximityMatrixTest {
    @Test
    public void testReduce() throws OWLOntologyCreationException {
        String[] args = new String[] { "http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl" };
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        List<IRI> iris = new ArrayList<IRI>(args.length);
        for (String string : args) {
            iris.add(IRI.create(string));
        }
        IOUtils.loadIRIMappers(iris, manager);
        Set<OWLEntity> entities = new TreeSet<OWLEntity>(new EntityComparator());
        for (OWLOntology ontology : manager.getOntologies()) {
            entities.addAll(ontology.getSignature());
        }
        final AxiomBasedDistance distance = new AxiomBasedDistance(
                manager.getOntologies(), manager.getOWLDataFactory(),
                DefaultOWLEntityRelevancePolicy.getAlwaysIrrelevantPolicy(), manager);
        SimpleProximityMatrix<OWLEntity> distanceMatrix = new SimpleProximityMatrix<OWLEntity>(
                entities, distance);
        System.out.println(String.format(
                "Finished computing distance between %d entities", distanceMatrix
                        .getObjects().size()));
        Set<Collection<? extends OWLEntity>> newObjects = new LinkedHashSet<Collection<? extends OWLEntity>>();
        for (OWLEntity object : distanceMatrix.getObjects()) {
            newObjects.add(Collections.singleton(object));
        }
        Distance<Collection<? extends OWLEntity>> singletonDistance = new Distance<Collection<? extends OWLEntity>>() {
            @Override
            public double getDistance(Collection<? extends OWLEntity> a,
                    Collection<? extends OWLEntity> b) {
                return distance.getDistance(a.iterator().next(), b.iterator().next());
            }
        };
        PairFilter<Collection<? extends OWLEntity>> filter = DistanceThresholdBasedFilter
                .build(new TableDistance<OWLEntity>(entities, distanceMatrix.getData()),
                        1);
        System.out.println("Building clustering matrix....");
        ClusteringProximityMatrix<OWLEntity> clusteringMatrix = ClusteringProximityMatrix
                .build(distanceMatrix, new CentroidProximityMeasureFactory(), filter,
                        PairFilterBasedComparator.build(filter, newObjects,
                                singletonDistance));
        ClusteringProximityMatrix<OWLEntity> reducedSingleFilter = clusteringMatrix
                .reduce(filter);
        ClusteringProximityMatrix<OWLEntity> reducedMultipleFilter = clusteringMatrix
                .reduce(OrPairFilter.build(
                        new PairFilter<Collection<? extends OWLEntity>>() {
                            @Override
                            public boolean accept(Collection<? extends OWLEntity> first,
                                    Collection<? extends OWLEntity> second) {
                                return first.size() > 1;
                            }
                        }, filter));
        Set<Collection<? extends OWLEntity>> difference = new HashSet<Collection<? extends OWLEntity>>(
                reducedMultipleFilter.getObjects());
        difference.removeAll(reducedSingleFilter.getObjects());
        assertTrue(String.format("Non identical, the difference is %s", difference),
                reducedSingleFilter.getObjects().size() == reducedMultipleFilter
                        .getObjects().size());
    }
}
