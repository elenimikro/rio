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
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.coode.distance.Distance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
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
import org.coode.proximitymatrix.History;
import org.coode.proximitymatrix.ProximityMatrix;
import org.coode.proximitymatrix.SimpleHistoryItemFactory;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;
import org.coode.proximitymatrix.cluster.SimpleCluster;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.proximitymatrix.ui.ClusterStatisticsTableModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.w3c.dom.Document;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

public class WrappingAgglomerateAll {
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
                    manager.getOntologies(), manager, owlEntityReplacer);
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
                    return wrappedMatrix.getDistance(a.iterator().next(), b.iterator()
                            .next());
                }
            };
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
            System.out.println(String.format(
                    "Finished clustering after %d agglomerations no of clusters %d", i,
                    clusteringMatrix.getObjects().size()));
            save(buildClusters(clusteringMatrix, distanceMatrix), manager, outfile,
                    Utility.unwrapHistory(clusteringMatrix.getHistory()));
        } else {
            System.out
                    .println(String
                            .format("Usage java -cp ... %s <saveResultFilePath> <ontology> ... <ontology>",
                                    WrappingAgglomerateAll.class.getCanonicalName()));
        }
    }

    private static String render(
            final Collection<? extends DistanceTableObject<OWLEntity>> cluster) {
        Formatter out = new Formatter();
        Iterator<? extends DistanceTableObject<OWLEntity>> iterator = cluster.iterator();
        while (iterator.hasNext()) {
            ManchesterOWLSyntaxOWLObjectRendererImpl renderer = new ManchesterOWLSyntaxOWLObjectRendererImpl();
            OWLEntity owlEntity = iterator.next().getObject();
            out.format("%s%s", renderer.render(owlEntity), iterator.hasNext() ? ", " : "");
        }
        return out.toString();
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

    private static <P extends OWLEntity> void save(
            final Collection<? extends Cluster<P>> clusters,
            final OWLOntologyManager manager, final File file,
            final History<Collection<? extends P>> history) {
        try {
            OWLOntology ontology = manager.getOntologies().iterator().next();
            OPPLFactory factory = new OPPLFactory(manager, ontology, null);
            ConstraintSystem constraintSystem = factory.createConstraintSystem();
            SortedSet<Cluster<P>> sortedClusters = new TreeSet<Cluster<P>>(
                    ClusterStatisticsTableModel.SIZE_COMPARATOR);
            sortedClusters.addAll(clusters);
            OWLObjectGeneralisation generalisation = Utils.getOWLObjectGeneralisation(
                    sortedClusters, manager.getOntologies(), constraintSystem);
            Document xml = Utils.toXML(sortedClusters, manager.getOntologies(),
                    new ManchesterOWLSyntaxOWLObjectRendererImpl(), generalisation,
                    new QuickFailRuntimeExceptionHandler());
            Transformer t = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(file);
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
            t.transform(new DOMSource(xml), result);
            System.out.println(String.format("Results saved in %s",
                    file.getAbsolutePath()));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (OPPLException e) {
            e.printStackTrace();
        }
    }
}
