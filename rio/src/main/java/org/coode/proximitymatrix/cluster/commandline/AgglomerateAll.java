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
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.coode.distance.Distance;
import org.coode.distance.TableDistance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.pair.filter.PairFilter;
import org.coode.pair.filter.commons.DistanceThresholdBasedFilter;
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

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

/** @author eleni */
public class AgglomerateAll extends AgglomeratorBase {
    /** @param args
     *            args
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException */
    public static void main(String[] args) throws OWLOntologyCreationException {
        AgglomerateAll agglomerator = new AgglomerateAll();
        agglomerator.checkArgumentsAndRun(args);
    }

    @Override
    public void run(File outfile, List<IRI> iris) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        IOUtils.loadIRIMappers(iris, manager);
        Set<OWLEntity> entities = new TreeSet<OWLEntity>(new EntityComparator());
        for (OWLOntology ontology : manager.getOntologies()) {
            entities.addAll(ontology.getSignature());
        }
        OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
                manager.getOWLDataFactory(), new ReplacementByKindStrategy(
                        manager.getOWLDataFactory()));
        final Distance<OWLEntity> distance = new AxiomRelevanceAxiomBasedDistance(
                manager.getOntologies(), owlEntityReplacer, manager);
        SimpleProximityMatrix<OWLEntity> distanceMatrix = new SimpleProximityMatrix<OWLEntity>(
                entities, distance);
        System.out.println(String.format(
                "Finished computing distance between %d entities", distanceMatrix
                        .getObjects().size()));
        Set<Collection<? extends OWLEntity>> newObjects = new LinkedHashSet<Collection<? extends OWLEntity>>();
        for (OWLEntity object : distanceMatrix.getObjects()) {
            newObjects.add(Collections.singletonList(object));
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
        Set<Cluster<OWLEntity>> clusters = buildClusters(clusteringMatrix, distanceMatrix);
        System.out.println(String.format(
                "Finished clustering after %d agglomerations no of clusters %d", i,
                clusters.size()));
        Utils.save(clusters, manager, outfile);
    }

    @Override
    public Distance<OWLEntity> getDistance(OWLOntologyManager manager) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void print(ClusteringProximityMatrix<?> clusteringMatrix) {
        System.out.println(String.format("Next Pair %s %s %f",
                render((Collection<? extends OWLEntity>) clusteringMatrix
                        .getMinimumDistancePair().getFirst()),
                render((Collection<? extends OWLEntity>) clusteringMatrix
                        .getMinimumDistancePair().getSecond()), clusteringMatrix
                        .getMinimumDistance()));
    }

    private static String render(Collection<? extends OWLEntity> cluster) {
        Formatter out = new Formatter();
        Iterator<? extends OWLEntity> iterator = cluster.iterator();
        while (iterator.hasNext()) {
            ManchesterOWLSyntaxOWLObjectRendererImpl renderer = new ManchesterOWLSyntaxOWLObjectRendererImpl();
            OWLEntity owlEntity = iterator.next();
            out.format("%s%s", renderer.render(owlEntity), iterator.hasNext() ? ", " : "");
        }
        return out.toString();
    }

    private static Set<Cluster<OWLEntity>> buildClusters(
            ClusteringProximityMatrix<OWLEntity> clusteringMatrix,
            ProximityMatrix<OWLEntity> distanceMatrix) {
        Collection<Collection<? extends OWLEntity>> objects = clusteringMatrix
                .getObjects();
        Set<Cluster<OWLEntity>> toReturn = new HashSet<Cluster<OWLEntity>>(objects.size());
        for (Collection<? extends OWLEntity> collection : objects) {
            toReturn.add(new SimpleCluster<OWLEntity>(collection, distanceMatrix));
        }
        return toReturn;
    }
}
