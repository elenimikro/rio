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
import org.coode.pair.filter.PairFilter;
import org.coode.proximitymatrix.CentroidProximityMeasureFactory;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.OntologyManagerUtils;
import org.coode.utils.owl.IOUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
public abstract class AgglomeratorBase implements Agglomerator {
    @Override
    public void checkArgumentsAndRun(String[] args) throws OWLOntologyCreationException {
        if (args.length >= 2) {
            List<IRI> iris = new ArrayList<>(args.length);
            File outfile = new File(args[0]);
            if (!args[1].startsWith("http") && !args[1].startsWith("file")) {
                iris.add(IRI.create(new File(args[1])));
            } else {
                for (int i = 1; i < args.length; i++) {
                    iris.add(IRI.create(args[i]));
                }
            }
            run(outfile, iris);
        } else {
            System.out.println(String.format(
                "Usage java -cp ... %s <saveResultFilePath> <ontology> ... <ontology>",
                this.getClass().getCanonicalName()));
        }
    }

    @Override
    public void run(File outfile, List<IRI> iris) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OntologyManagerUtils.ontologyManager();
        IOUtils.loadIRIMappers(iris, manager);
        // Set the policy and the distance
        Distance<OWLEntity> distance = getDistance(manager);
        List<OWLEntity> entities = Utils.getSortedSignature(manager);
        SimpleProximityMatrix<OWLEntity> baseDistanceMatrix =
            new SimpleProximityMatrix<>(entities, distance);
        MultiMap<OWLEntity, OWLEntity> equivalenceClasses =
            org.coode.distance.Utils.getEquivalenceClasses(entities, distance);
        final SimpleProximityMatrix<OWLEntity> distanceMatrix =
            new SimpleProximityMatrix<>(equivalenceClasses.keySet(), distance);
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
        // uncomment if we want to store the history
        // clusteringMatrix.setHistoryItemFactory(AddLeftOversHistoryItemFactory
        // .build(leftOvers));
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
        System.out
            .println(String.format("Finished clustering after %d agglomerations no of clusters %d",
                Integer.valueOf(i), Integer.valueOf(clusters.size())));
        Utils.save(clusters, manager, outfile);
    }
}
