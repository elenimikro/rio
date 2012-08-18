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

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.coode.basetest.TestHelper;
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
import org.coode.proximitymatrix.SimpleHistoryItemFactory;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;
import org.coode.proximitymatrix.cluster.SimpleCluster;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class WrappingAgglomerateAll extends AgglomeratorBase {
    /** @param args
     * @throws OWLOntologyCreationException */
    public static void main(final String[] args) throws OWLOntologyCreationException {
        WrappingAgglomerateAll agglomerator = new WrappingAgglomerateAll();
        agglomerator.checkArgumentsAndRun(args);
    }

    public void run(final File outfile, final java.util.List<IRI> iris)
            throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        TestHelper.loadIRIMappers(iris, manager);
        final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
            public int compare(final OWLEntity o1, final OWLEntity o2) {
                return shortFormProvider.getShortForm(o1).compareTo(
                        shortFormProvider.getShortForm(o2));
            }
        });
        for (OWLOntology ontology : manager.getOntologies()) {
            entities.addAll(ontology.getSignature());
        }
        final Distance<OWLEntity> distance = getDistance(manager);
        final SimpleProximityMatrix<OWLEntity> distanceMatrix = new SimpleProximityMatrix<OWLEntity>(
                entities, distance);
        System.out.println(String.format(
                "Finished computing distance between %d entities", distanceMatrix
                        .getObjects().size()));
        final SimpleProximityMatrix<DistanceTableObject<OWLEntity>> wrappedMatrix = new SimpleProximityMatrix<DistanceTableObject<OWLEntity>>(
                DistanceTableObject.createDistanceTableObjectSet(distance,
                        distanceMatrix.getObjects()),
                new Distance<DistanceTableObject<OWLEntity>>() {
                    public double getDistance(final DistanceTableObject<OWLEntity> a,
                            final DistanceTableObject<OWLEntity> b) {
                        return distanceMatrix.getDistance(a.getIndex(), b.getIndex());
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
                return wrappedMatrix
                        .getDistance(a.iterator().next(), b.iterator().next());
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
        clusteringMatrix = clusteringMatrix.reduce(filter);
        while (clusteringMatrix.getMinimumDistancePair() != null
                && filter.accept(clusteringMatrix.getMinimumDistancePair().getFirst(),
                        clusteringMatrix.getMinimumDistancePair().getSecond())) {
            clusteringMatrix = clusteringMatrix.agglomerate(filter);
            System.out.println(String.format("Agglomerations: %d for %d clusters", i++,
                    clusteringMatrix.getObjects().size()));
            if (clusteringMatrix.getMinimumDistancePair() != null) {
                print(clusteringMatrix);
            }
        }
        System.out.println(String.format(
                "Finished clustering after %d agglomerations no of clusters %d", i,
                clusteringMatrix.getObjects().size()));
        Utils.save(buildClusters(clusteringMatrix, distanceMatrix), manager, outfile);
    }

    @Override
    public Distance<OWLEntity> getDistance(final OWLOntologyManager manager) {
        final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
                manager.getOWLDataFactory(), new ReplacementByKindStrategy(
                        manager.getOWLDataFactory()));
        final Distance<OWLEntity> distance = new AxiomRelevanceAxiomBasedDistance(
                manager.getOntologies(), owlEntityReplacer, manager);
        return distance;
    }

    @Override
    public void print(final ClusteringProximityMatrix<?> clusteringMatrix) {
        System.out
                .println(String.format(
                        "Next Pair %s %s %f",
                        Utils.render((Collection<DistanceTableObject<OWLEntity>>) clusteringMatrix
                                .getMinimumDistancePair().getFirst()),
                        Utils.render((Collection<DistanceTableObject<OWLEntity>>) (Collection<? extends OWLEntity>) clusteringMatrix
                                .getMinimumDistancePair().getSecond()), clusteringMatrix
                                .getMinimumDistance()));
    }

    private static <P> Set<Cluster<P>> buildClusters(
            final ClusteringProximityMatrix<DistanceTableObject<P>> clusteringMatrix,
            final ProximityMatrix<P> distanceMatrix) {
        Collection<Collection<? extends DistanceTableObject<P>>> objects = clusteringMatrix
                .getObjects();
        Set<Cluster<P>> toReturn = new HashSet<Cluster<P>>(objects.size());
        for (Collection<? extends DistanceTableObject<P>> collection : objects) {
            toReturn.add(new SimpleCluster<P>(Utility.unwrapObjects(collection),
                    distanceMatrix));
        }
        return toReturn;
    }
}
