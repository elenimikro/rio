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
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

public class AgglomerateAll {
    /** @param args */
    public static void main(final String[] args) {
        if (args.length >= 2) {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            List<IRI> iris = new ArrayList<IRI>(args.length);
            File outfile = new File(args[0]);
            for (int i = 1; i < args.length; i++) {
                iris.add(IRI.create(args[i]));
            }
            for (IRI iri : iris) {
                try {
                    URI uri = iri.toURI();
                    if (uri.getScheme().startsWith("file") && uri.isAbsolute()) {
                        File file = new File(uri);
                        File parentFile = file.getParentFile();
                        if (parentFile.isDirectory()) {
                            manager.addIRIMapper(new AutoIRIMapper(parentFile, true));
                        }
                    }
                    manager.loadOntology(iri);
                } catch (OWLOntologyCreationException e) {
                    e.printStackTrace();
                }
            }
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
            final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
                    manager.getOWLDataFactory(), new ReplacementByKindStrategy(
                            manager.getOWLDataFactory()));
            final Distance<OWLEntity> distance = new AxiomRelevanceAxiomBasedDistance(
                    manager.getOntologies(), owlEntityReplacer, manager);
            final SimpleProximityMatrix<OWLEntity> distanceMatrix = new SimpleProximityMatrix<OWLEntity>(
                    entities, distance);
            System.out.println(String.format(
                    "Finished computing distance between %d entities", distanceMatrix
                            .getObjects().size()));
            Set<Collection<? extends OWLEntity>> newObjects = new LinkedHashSet<Collection<? extends OWLEntity>>();
            for (OWLEntity object : distanceMatrix.getObjects()) {
                newObjects.add(Collections.singletonList(object));
            }
            Distance<Collection<? extends OWLEntity>> singletonDistance = new Distance<Collection<? extends OWLEntity>>() {
                public double getDistance(final Collection<? extends OWLEntity> a,
                        final Collection<? extends OWLEntity> b) {
                    return distance.getDistance(a.iterator().next(), b.iterator().next());
                }
            };
            PairFilter<Collection<? extends OWLEntity>> filter = DistanceThresholdBasedFilter
                    .build(new TableDistance<OWLEntity>(entities, distanceMatrix
                            .getData()), 1);
            System.out.println("Building clustering matrix....");
            ClusteringProximityMatrix<OWLEntity> clusteringMatrix = ClusteringProximityMatrix
                    .build(distanceMatrix,
                            new CentroidProximityMeasureFactory(),
                            filter,
                            PairFilterBasedComparator.build(filter, newObjects,
                                    singletonDistance),
                            new SimpleHistoryItemFactory<Collection<? extends OWLEntity>>());
            System.out.println("Start clustering");
            int i = 1;
            clusteringMatrix = clusteringMatrix.reduce(filter);
            while (clusteringMatrix.getMinimumDistancePair() != null
                    && filter.accept(
                            clusteringMatrix.getMinimumDistancePair().getFirst(),
                            clusteringMatrix.getMinimumDistancePair().getSecond())) {
                clusteringMatrix = clusteringMatrix.agglomerate(filter);
                System.out.println(String.format("Agglomerations: %d for %d clusters",
                        i++, clusteringMatrix.getObjects().size()));
                if (clusteringMatrix.getMinimumDistancePair() != null) {
                    System.out.println(String
                            .format("Next Pair %s %s %f", render(clusteringMatrix
                                    .getMinimumDistancePair().getFirst()),
                                    render(clusteringMatrix.getMinimumDistancePair()
                                            .getSecond()), clusteringMatrix
                                            .getMinimumDistance()));
                }
            }
            Set<Cluster<OWLEntity>> clusters = buildClusters(clusteringMatrix,
                    distanceMatrix);
            System.out.println(String.format(
                    "Finished clustering after %d agglomerations no of clusters %d", i,
                    clusters.size()));
            Utils.save(clusters, manager, outfile);
        } else {
            System.out
                    .println("Usage java -cp ... org.coode.owl.distance.test.AgglomerateAll <saveResultFilePath> <ontology> ... <ontology>");
        }
    }

    private static String render(final Collection<? extends OWLEntity> cluster) {
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
            final ClusteringProximityMatrix<OWLEntity> clusteringMatrix,
            final ProximityMatrix<OWLEntity> distanceMatrix) {
        Collection<Collection<? extends OWLEntity>> objects = clusteringMatrix
                .getObjects();
        Set<Cluster<OWLEntity>> toReturn = new HashSet<Cluster<OWLEntity>>(objects.size());
        for (Collection<? extends OWLEntity> collection : objects) {
            toReturn.add(new SimpleCluster<OWLEntity>(collection, distanceMatrix));
        }
        return toReturn;
    }
}
