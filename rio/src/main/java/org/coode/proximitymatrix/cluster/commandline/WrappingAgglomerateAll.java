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
package org.coode.proximitymatrix.cluster.commandline;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.add;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.coode.distance.Distance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.distance.wrapping.DistanceTableObject;
import org.coode.distance.wrapping.DistanceThresholdBasedFilter;
import org.coode.pair.filter.PairFilter;
import org.coode.proximitymatrix.CentroidProximityMeasureFactory;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.ProximityMatrix;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;
import org.coode.proximitymatrix.cluster.SimpleCluster;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.EntityComparator;
import org.coode.utils.owl.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
public class WrappingAgglomerateAll extends AgglomeratorBase {
    /**
     * @param args args
     * @throws OWLOntologyCreationException OWLOntologyCreationException
     */
    public static void main(String[] args) throws OWLOntologyCreationException {
        WrappingAgglomerateAll agglomerator = new WrappingAgglomerateAll();
        agglomerator.checkArgumentsAndRun(args);
    }

    @Override
    public void run(File outfile, java.util.List<IRI> iris) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        IOUtils.loadIRIMappers(iris, manager);
        Set<OWLEntity> entities = new TreeSet<>(new EntityComparator());
        add(entities, manager.ontologies().flatMap(OWLOntology::signature));
        Distance<OWLEntity> distance = getDistance(manager);
        final SimpleProximityMatrix<OWLEntity> distanceMatrix =
            new SimpleProximityMatrix<>(entities, distance);
        System.out.println(String.format("Finished computing distance between %d entities",
            Integer.valueOf(distanceMatrix.getObjects().size())));
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
        System.out.println("Building clustering matrix....");
        ClusteringProximityMatrix<DistanceTableObject<OWLEntity>> clusteringMatrix =
            ClusteringProximityMatrix.build(wrappedMatrix, new CentroidProximityMeasureFactory(),
                filter, PairFilterBasedComparator.build(filter, newObjects, singletonDistance));
        System.out.println("Start clustering");
        int i = 1;
        clusteringMatrix = clusteringMatrix.reduce(filter);
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
        System.out
            .println(String.format("Finished clustering after %d agglomerations no of clusters %d",
                Integer.valueOf(i), Integer.valueOf(clusteringMatrix.getObjects().size())));
        Utils.save(buildClusters(clusteringMatrix, distanceMatrix), manager, outfile);
    }

    @Override
    public Distance<OWLEntity> getDistance(OWLOntologyManager manager) {
        OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(manager.getOWLDataFactory(),
            new ReplacementByKindStrategy(manager.getOWLDataFactory()));
        Distance<OWLEntity> distance = new AxiomRelevanceAxiomBasedDistance(manager.getOntologies(),
            owlEntityReplacer, manager);
        return distance;
    }

    @Override
    public void print(ClusteringProximityMatrix<?> clusteringMatrix) {
        Utility.print(clusteringMatrix);
    }

    private static <P> Set<Cluster<P>> buildClusters(
        ClusteringProximityMatrix<DistanceTableObject<P>> clusteringMatrix,
        ProximityMatrix<P> distanceMatrix) {
        Collection<Collection<? extends DistanceTableObject<P>>> objects =
            clusteringMatrix.getObjects();
        Set<Cluster<P>> toReturn = new HashSet<>(objects.size());
        for (Collection<? extends DistanceTableObject<P>> collection : objects) {
            toReturn.add(new SimpleCluster<>(Utility.unwrapObjects(collection), distanceMatrix));
        }
        return toReturn;
    }
}
