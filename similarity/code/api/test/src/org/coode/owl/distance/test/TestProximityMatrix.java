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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.coode.distance.Distance;
import org.coode.distance.entityrelevance.DefaultOWLEntityRelevancePolicy;
import org.coode.distance.owl.AxiomBasedDistance;
import org.coode.pair.filter.PairFilter;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.ProximityMatrix;
import org.coode.proximitymatrix.SimpleHistoryItemFactory;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.WardsProximityMeasureFactory;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class TestProximityMatrix extends TestCase {
	public void testAllDistances() {
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		try {
			OWLOntology ontology = ontologyManager
					.loadOntology(IRI
							.create("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl"));
			Set<OWLOntology> ontologies = ontologyManager.getOntologies();
			AxiomBasedDistance axiomBasedDistance = new AxiomBasedDistance(ontologies,
					ontologyManager.getOWLDataFactory(),
					DefaultOWLEntityRelevancePolicy.getAlwaysRelevantPolicy(),
					ontologyManager);
			ProximityMatrix<OWLEntity> distanceMatrix = new SimpleProximityMatrix<OWLEntity>(
					ontology.getSignature(), axiomBasedDistance);
			for (OWLEntity owlEntity : ontology.getSignature()) {
				for (OWLEntity anotherOWLEntity : ontology.getSignature()) {
					double distance = axiomBasedDistance.getDistance(owlEntity,
							anotherOWLEntity);
					assertTrue(distanceMatrix.getDistance(owlEntity, anotherOWLEntity) == distance);
				}
			}
			axiomBasedDistance.dispose();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testAllDistancesClustering() {
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		try {
			OWLOntology ontology = ontologyManager
					.loadOntology(IRI
							.create("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl"));
			Set<OWLOntology> ontologies = ontologyManager.getOntologies();
			final AxiomBasedDistance axiomBasedDistance = new AxiomBasedDistance(
					ontologies, ontologyManager.getOWLDataFactory(),
					DefaultOWLEntityRelevancePolicy.getAlwaysRelevantPolicy(),
					ontologyManager);
			ProximityMatrix<OWLEntity> distanceMatrix = new SimpleProximityMatrix<OWLEntity>(
					ontology.getSignature(), axiomBasedDistance);
			PairFilter<Collection<? extends OWLEntity>> pairFilter = new PairFilter<Collection<? extends OWLEntity>>() {
				public boolean accept(Collection<? extends OWLEntity> first,
						Collection<? extends OWLEntity> second) {
					return true;
				}
			};
			Set<Collection<? extends OWLEntity>> newObjects = new LinkedHashSet<Collection<? extends OWLEntity>>();
			for (OWLEntity object : distanceMatrix.getObjects()) {
				newObjects.add(Collections.singleton(object));
			}
			Distance<Collection<? extends OWLEntity>> singletonDistance = new Distance<Collection<? extends OWLEntity>>() {
				public double getDistance(Collection<? extends OWLEntity> a,
						Collection<? extends OWLEntity> b) {
					return axiomBasedDistance.getDistance(a.iterator().next(), b
							.iterator().next());
				}
			};
			ClusteringProximityMatrix<OWLEntity> clusteringMatrix = ClusteringProximityMatrix
					.build(distanceMatrix,
							new WardsProximityMeasureFactory(),
							pairFilter,
							PairFilterBasedComparator.build(pairFilter, newObjects,
									singletonDistance),
							new SimpleHistoryItemFactory<Collection<? extends OWLEntity>>());
			for (OWLEntity owlEntity : ontology.getSignature()) {
				for (OWLEntity anotherOWLEntity : ontology.getSignature()) {
					double distance = axiomBasedDistance.getDistance(owlEntity,
							anotherOWLEntity);
					assertTrue(clusteringMatrix.getDistance(
							Collections.singleton(owlEntity),
							Collections.singleton(anotherOWLEntity)) == distance);
				}
			}
			axiomBasedDistance.dispose();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testAllDistancesReduced() {
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		try {
			OWLOntology ontology = ontologyManager
					.loadOntology(IRI
							.create("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl"));
			Set<OWLOntology> ontologies = ontologyManager.getOntologies();
			AxiomBasedDistance axiomBasedDistance = new AxiomBasedDistance(ontologies,
					ontologyManager.getOWLDataFactory(),
					DefaultOWLEntityRelevancePolicy.getAlwaysRelevantPolicy(),
					ontologyManager);
			ProximityMatrix<OWLEntity> distanceMatrix = new SimpleProximityMatrix<OWLEntity>(
					ontology.getSignature(), axiomBasedDistance);
			ProximityMatrix<OWLEntity> reduced = distanceMatrix
					.reduce(new PairFilter<OWLEntity>() {
						public boolean accept(OWLEntity first, OWLEntity second) {
							return true;
						}
					});
			for (OWLEntity owlEntity : ontology.getSignature()) {
				for (OWLEntity anotherOWLEntity : ontology.getSignature()) {
					double distance = axiomBasedDistance.getDistance(owlEntity,
							anotherOWLEntity);
					assertTrue(reduced.getDistance(owlEntity, anotherOWLEntity) == distance);
				}
			}
			axiomBasedDistance.dispose();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
