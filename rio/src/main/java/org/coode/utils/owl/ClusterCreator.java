package org.coode.utils.owl;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.coode.distance.Distance;
import org.coode.distance.wrapping.DistanceTableObject;
import org.coode.distance.wrapping.DistanceThresholdBasedFilter;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.pair.filter.PairFilter;
import org.coode.proximitymatrix.CentroidProximityMeasureFactory;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.proximitymatrix.cluster.commandline.Utility;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

/** @author Eleni Mikroyannidi */
public class ClusterCreator {
    private ClusteringProximityMatrix<DistanceTableObject<OWLEntity>> clusteringMatrix;

    /**
     * @param distance distance
     * @param entities entities
     * @param <P> type
     * @return clusters
     */
    public <P extends OWLEntity> Set<Cluster<OWLEntity>> agglomerateAll(
        Distance<OWLEntity> distance, List<OWLEntity> entities) {
        SimpleProximityMatrix<OWLEntity> baseDistanceMatrix =
            new SimpleProximityMatrix<>(entities, distance);
        MultiMap<OWLEntity, OWLEntity> equivalenceClasses =
            org.coode.distance.Utils.getEquivalenceClasses(entities, distance);
        // XXX: to remove
        System.out.println("Computed equivalent classes...");
        entities = new ArrayList<>(equivalenceClasses.keySet());
        final SimpleProximityMatrix<OWLEntity> distanceMatrix =
            new SimpleProximityMatrix<>(entities, distance);
        System.out.println(String.format("Finished computing distance between %d entities",
            distanceMatrix.getObjects().size()));
        final SimpleProximityMatrix<DistanceTableObject<OWLEntity>> wrappedMatrix =
            new SimpleProximityMatrix<>(
                DistanceTableObject.createDistanceTableObjectSet(distance,
                    distanceMatrix.getObjects()),
                (a, b) -> distanceMatrix.getDistance(a.getIndex(), b.getIndex()));
        Set<Collection<? extends DistanceTableObject<OWLEntity>>> newObjects =
            new LinkedHashSet<>();
        for (DistanceTableObject<OWLEntity> object : wrappedMatrix.getObjects()) {
            newObjects.add(Collections.singletonList(object));
        }
        Distance<Collection<? extends DistanceTableObject<OWLEntity>>> singletonDistance =
            (a, b) -> wrappedMatrix.getDistance(a.iterator().next(), b.iterator().next());
        // it passes the threshold for the distance (criterion for stopping
        // clustering)
        // In this case is 1.
        PairFilter<Collection<? extends DistanceTableObject<OWLEntity>>> filter =
            DistanceThresholdBasedFilter.build(distanceMatrix.getData(), 1);
        Set<Cluster<OWLEntity>> clusters = runClustering(wrappedMatrix, filter, singletonDistance,
            newObjects, equivalenceClasses, baseDistanceMatrix);
        return clusters;
    }

    private <P extends OWLEntity> Set<Cluster<OWLEntity>> runClustering(
        SimpleProximityMatrix<DistanceTableObject<OWLEntity>> wrappedMatrix,
        PairFilter<Collection<? extends DistanceTableObject<OWLEntity>>> filter,
        Distance<Collection<? extends DistanceTableObject<OWLEntity>>> singletonDistance,
        Set<Collection<? extends DistanceTableObject<OWLEntity>>> newObjects,
        MultiMap<OWLEntity, OWLEntity> equivalenceClasses,
        SimpleProximityMatrix<OWLEntity> baseDistanceMatrix) {
        // OWLOntologyManager manager = o.getOWLOntologyManager();
        System.out.println("Building clustering matrix....");
        clusteringMatrix =
            ClusteringProximityMatrix.build(wrappedMatrix, new CentroidProximityMeasureFactory(),
                filter, PairFilterBasedComparator.build(filter, newObjects, singletonDistance));
        System.out.println("Start clustering");
        int i = 1;
        Set<Collection<? extends DistanceTableObject<OWLEntity>>> leftOvers =
            new HashSet<>(clusteringMatrix.getObjects());
        clusteringMatrix = clusteringMatrix.reduce(filter);
        leftOvers.removeAll(clusteringMatrix.getObjects());
        Iterator<Collection<? extends DistanceTableObject<OWLEntity>>> iterator =
            leftOvers.iterator();
        while (iterator.hasNext()) {
            Collection<? extends DistanceTableObject<OWLEntity>> collection = iterator.next();
            Set<OWLEntity> unwrappedObjects = Utility.unwrapObjects(collection, equivalenceClasses);
            if (unwrappedObjects.size() <= 1) {
                iterator.remove();
            }
        }
        while (clusteringMatrix.getMinimumDistancePair() != null
            && filter.accept(clusteringMatrix.getMinimumDistancePair().getFirst(),
                clusteringMatrix.getMinimumDistancePair().getSecond())) {
            clusteringMatrix = clusteringMatrix.agglomerate(filter);
            Utility.printAgglomeration(clusteringMatrix, i);
            i++;
            if (clusteringMatrix.getMinimumDistancePair() != null) {
                print(clusteringMatrix);
            }
        }
        Set<Cluster<OWLEntity>> clusters =
            Utils.buildClusters(clusteringMatrix, baseDistanceMatrix, equivalenceClasses);
        System.out.println(String.format(
            "Finished clustering after %d agglomerations no of clusters %d", i, clusters.size()));
        return clusters;
    }

    /**
     * @param o o
     * @param clusters clusters
     * @return cluster decomposition model
     * @param <P> type
     * @throws OPPLException OPPLException
     */
    public <P extends OWLEntity> ClusterDecompositionModel<P> buildClusterDecompositionModel(
        OWLOntology o, Set<Cluster<OWLEntity>> clusters) throws OPPLException {
        ConstraintSystem constraintSystem =
            new OPPLFactory(o.getOWLOntologyManager(), o, null).createConstraintSystem();
        System.out.println("ClusterCreator.buildClusterDecompositionModel() clusters " + clusters);
        List<OWLOntology> importsClosure = asList(o.importsClosure());
        OWLObjectGeneralisation generalisation =
            Utils.getOWLObjectGeneralisation(clusters, importsClosure, constraintSystem);
        ClusterDecompositionModel<P> clusterModel = (ClusterDecompositionModel<P>) Utils
            .toClusterDecompositionModel(clusters, importsClosure, generalisation);
        return clusterModel;
    }

    /**
     * @param o o
     * @param entailements entailements
     * @param manager manager
     * @param clusters clusters
     * @return cluster decomposition model
     * @param <P> type
     * @throws OPPLException OPPLException
     */
    public <P extends OWLEntity> ClusterDecompositionModel<P> buildKnowledgeExplorerClusterDecompositionModel(
        OWLOntology o, Set<OWLAxiom> entailements, OWLOntologyManager manager,
        Set<Cluster<OWLEntity>> clusters) throws OPPLException {
        ConstraintSystem constraintSystem =
            new OPPLFactory(manager, o, null).createConstraintSystem();
        List<OWLOntology> importsClosure = asList(o.importsClosure());
        OWLObjectGeneralisation generalisation =
            Utils.getOWLObjectGeneralisation(clusters, importsClosure, constraintSystem);
        ClusterDecompositionModel<P> clusterModel =
            (ClusterDecompositionModel<P>) Utils.toKnowledgeExplorerClusterDecompositionModel(
                clusters, importsClosure, entailements, generalisation);
        return clusterModel;
    }

    /**
     * @param distance distance
     * @param entities entities
     * @param <P> type
     * @return clusters
     */
    public <P extends OWLEntity> Set<Cluster<OWLEntity>> agglomerateZeros(
        Distance<OWLEntity> distance, Set<OWLEntity> entities) {
        SimpleProximityMatrix<OWLEntity> baseDistanceMatrix =
            new SimpleProximityMatrix<>(entities, distance);
        MultiMap<OWLEntity, OWLEntity> equivalenceClasses =
            org.coode.distance.Utils.getEquivalenceClasses(entities, distance);
        entities = new HashSet<>(equivalenceClasses.keySet());
        final SimpleProximityMatrix<OWLEntity> distanceMatrix =
            new SimpleProximityMatrix<>(entities, distance);
        System.out.println(String.format("Finished computing distance between %d entities",
            distanceMatrix.getObjects().size()));
        final SimpleProximityMatrix<DistanceTableObject<OWLEntity>> wrappedMatrix =
            new SimpleProximityMatrix<>(
                DistanceTableObject.createDistanceTableObjectSet(distance,
                    distanceMatrix.getObjects()),
                (a, b) -> distanceMatrix.getDistance(a.getIndex(), b.getIndex()));
        Set<Collection<? extends DistanceTableObject<OWLEntity>>> newObjects =
            new LinkedHashSet<>();
        for (DistanceTableObject<OWLEntity> object : wrappedMatrix.getObjects()) {
            newObjects.add(Collections.singletonList(object));
        }
        Distance<Collection<? extends DistanceTableObject<OWLEntity>>> singletonDistance =
            (a, b) -> wrappedMatrix.getDistance(a.iterator().next(), b.iterator().next());
        // it passes the threshold for the distance (criterion for stopping
        // clustering)
        // In this case is 0. -> mapped to agglomerateZeros
        PairFilter<Collection<? extends DistanceTableObject<OWLEntity>>> filter =
            DistanceThresholdBasedFilter.build(distanceMatrix.getData(), 0);
        Set<Cluster<OWLEntity>> clusters = runClustering(wrappedMatrix, filter, singletonDistance,
            newObjects, equivalenceClasses, baseDistanceMatrix);
        return clusters;
    }

    /**
     * @param cm cm
     */
    public void print(ClusteringProximityMatrix<?> cm) {
        Utility.print1(cm);
    }

    /** @return cluster matrix */
    public ClusteringProximityMatrix<DistanceTableObject<OWLEntity>> getClusteringMatrix() {
        return clusteringMatrix;
    }
}
