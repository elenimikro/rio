package org.coode.basetest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.distance.Distance;
import org.coode.distance.wrapping.DistanceTableObject;
import org.coode.distance.wrapping.DistanceThresholdBasedFilter;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.pair.filter.PairFilter;
import org.coode.proximitymatrix.CentroidProximityMeasureFactory;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.SimpleHistoryItemFactory;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.proximitymatrix.cluster.commandline.Utility;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

public class ClusterCreator {

	public <P extends OWLEntity> ClusterDecompositionModel<P> runClustering(
			OWLOntology o, Distance<OWLEntity> distance, Set<OWLEntity> entities)
			throws OPPLException, ParserConfigurationException {

		OWLOntologyManager manager = o.getOWLOntologyManager();

		SimpleProximityMatrix<OWLEntity> baseDistanceMatrix = new SimpleProximityMatrix<OWLEntity>(
				entities, distance);
		MultiMap<OWLEntity, OWLEntity> equivalenceClasses = org.coode.distance.Utils
				.getEquivalenceClasses(entities, distance);
		entities = new HashSet<OWLEntity>(equivalenceClasses.keySet());
		final SimpleProximityMatrix<OWLEntity> distanceMatrix = new SimpleProximityMatrix<OWLEntity>(
				entities, distance);
		System.out.println(String.format(
				"Finished computing distance between %d entities",
				distanceMatrix.getObjects().size()));
		final SimpleProximityMatrix<DistanceTableObject<OWLEntity>> wrappedMatrix = new SimpleProximityMatrix<DistanceTableObject<OWLEntity>>(
				DistanceTableObject.createDistanceTableObjectSet(distance,
						distanceMatrix.getObjects()),
				new Distance<DistanceTableObject<OWLEntity>>() {
					public double getDistance(
							final DistanceTableObject<OWLEntity> a,
							final DistanceTableObject<OWLEntity> b) {
						return distanceMatrix.getDistance(a.getIndex(),
								b.getIndex());
					}
				});
		Set<Collection<? extends DistanceTableObject<OWLEntity>>> newObjects = new LinkedHashSet<Collection<? extends DistanceTableObject<OWLEntity>>>();
		for (DistanceTableObject<OWLEntity> object : wrappedMatrix.getObjects()) {
			newObjects.add(Collections.singletonList(object));
		}
		Distance<Collection<? extends DistanceTableObject<OWLEntity>>> singletonDistance = new Distance<Collection<? extends DistanceTableObject<OWLEntity>>>() {
			public double getDistance(
					final Collection<? extends DistanceTableObject<OWLEntity>> a,
					final Collection<? extends DistanceTableObject<OWLEntity>> b) {
				return wrappedMatrix.getDistance(a.iterator().next(), b
						.iterator().next());
			}
		};
		// it passes the threshold for the distance (criterion for stopping
		// clustering)
		// In this case is 1.
		PairFilter<Collection<? extends DistanceTableObject<OWLEntity>>> filter = DistanceThresholdBasedFilter
				.build(distanceMatrix.getData(), 1);
		System.out.println("Building clustering matrix....");
		ClusteringProximityMatrix<DistanceTableObject<OWLEntity>> clusteringMatrix = ClusteringProximityMatrix
				.build(wrappedMatrix,
						new CentroidProximityMeasureFactory(),
						filter,
						PairFilterBasedComparator.build(filter, newObjects,
								singletonDistance),
						new SimpleHistoryItemFactory<Collection<? extends DistanceTableObject<OWLEntity>>>());
		System.out.println("Start clustering");
		int i = 1;
		Set<Collection<? extends DistanceTableObject<OWLEntity>>> leftOvers = new HashSet<Collection<? extends DistanceTableObject<OWLEntity>>>(
				clusteringMatrix.getObjects());
		clusteringMatrix = clusteringMatrix.reduce(filter);
		leftOvers.removeAll(clusteringMatrix.getObjects());
		Iterator<Collection<? extends DistanceTableObject<OWLEntity>>> iterator = leftOvers
				.iterator();
		while (iterator.hasNext()) {
			Collection<? extends DistanceTableObject<OWLEntity>> collection = iterator
					.next();
			Set<OWLEntity> unwrappedObjects = Utility.unwrapObjects(collection,
					equivalenceClasses);
			if (unwrappedObjects.size() <= 1) {
				iterator.remove();
			}
		}
		while (clusteringMatrix.getMinimumDistancePair() != null
				&& filter.accept(clusteringMatrix.getMinimumDistancePair()
						.getFirst(), clusteringMatrix.getMinimumDistancePair()
						.getSecond())) {
			clusteringMatrix = clusteringMatrix.agglomerate(filter);
			System.out.println(String.format(
					"Agglomerations: %d for %d clusters", i++, clusteringMatrix
							.getObjects().size()));
			if (clusteringMatrix.getMinimumDistancePair() != null) {
				print(clusteringMatrix);
			}
		}
		Set<Cluster<OWLEntity>> clusters = Utils.buildClusters(
				clusteringMatrix, baseDistanceMatrix, equivalenceClasses);
		System.out
				.println(String
						.format("Finished clustering after %d agglomerations no of clusters %d",
								i, clusters.size()));

		ConstraintSystem constraintSystem = new OPPLFactory(manager, o, null)
				.createConstraintSystem();
		OWLObjectGeneralisation generalisation = Utils
				.getOWLObjectGeneralisation(clusters, o.getImportsClosure(),
						constraintSystem);
		ClusterDecompositionModel<P> clusterModel = (ClusterDecompositionModel<P>) Utils
				.toClusterDecompositionModel(
						clusters, o.getImportsClosure(),
						generalisation, new QuickFailRuntimeExceptionHandler());

		return clusterModel;
	}

	public void print(final ClusteringProximityMatrix<?> clusteringMatrix) {
		System.out
				.println(String.format(
						"Next Pair %s %s %f",
						Utils.render((Collection<DistanceTableObject<OWLEntity>>) clusteringMatrix
								.getMinimumDistancePair().getFirst()),
						Utils.render((Collection<DistanceTableObject<OWLEntity>>) (Collection<? extends OWLEntity>) clusteringMatrix
								.getMinimumDistancePair().getSecond()),
						clusteringMatrix.getMinimumDistance()));
	}
}
