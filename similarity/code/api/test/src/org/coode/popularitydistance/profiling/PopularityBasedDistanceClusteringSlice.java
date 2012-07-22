package org.coode.popularitydistance.profiling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.coode.aiontologygeneration.ClusteringUtils;
import org.coode.distance.Distance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.distance.wrapping.DistanceTableObject;
import org.coode.distance.wrapping.DistanceThresholdBasedFilter;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
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
import org.coode.proximitymatrix.cluster.commandline.Utility;
import org.coode.proximitymatrix.ui.ClusterStatisticsTableModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.w3c.dom.Document;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

public class PopularityBasedDistanceClusteringSlice {
    /** This program takes an ontology as an input, computes pairwise distances
     * between all entities in the signature of the ontology, and build the
     * proximity matrix. The distance is computed with the use of popularity
     * ranking of an entity.
     * 
     * @param args */
    // XXX: Change path!
    private final static String nci_iri = "profiling_ontologies/nci-2012.owl.xml";
    private final static String obi_iri = "profiling_ontologies/obi.owl";

    public static void main(final String[] args) throws Exception {
        File ontologyList = new File(args[0]);
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(
                ontologyList)));
        String line = r.readLine();
        boolean correct = true;
        while (line != null && !line.trim().isEmpty() && correct) {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File ontology = new File(line);
            Calendar c = Calendar.getInstance();
            String saveTo = "results/" + ontology.getName() + "_"
                    + c.get(Calendar.DAY_OF_MONTH) + "_" + c.get(Calendar.HOUR) + ".xml";
            String compareTo = "results/" + ontology.getName()
                    + "_clustering_results.xml";
            line = r.readLine();
            IRI iri = IRI.create(ontology);
            manager.addIRIMapper(new AutoIRIMapper(ontology.getParentFile(), true));
            try {
                OWLOntology onto = manager.loadOntology(iri);
                // System.out.println("PopularityDistanceSlice.main() Ontology "
                // + onto.getOntologyID() + " was loaded");
                System.out.println(line + " " + new Date());
                final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
                final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
                        manager.getOWLDataFactory(), new ReplacementByKindStrategy(
                                manager.getOWLDataFactory()));
                final AxiomRelevanceAxiomBasedDistance distance = new AxiomRelevanceAxiomBasedDistance(
                        onto.getImportsClosure(), owlEntityReplacer, manager);
                // System.out
                // .println("PopularityDistanceSlice.main() Distance measure was built");
                Set<OWLEntity> entities = new TreeSet<OWLEntity>(
                        new Comparator<OWLEntity>() {
                            public int compare(final OWLEntity o1, final OWLEntity o2) {
                                return shortFormProvider.getShortForm(o1).compareTo(
                                        shortFormProvider.getShortForm(o2));
                            }
                        });
                for (OWLOntology o : onto.getImportsClosure()) {
                    Set<OWLEntity> signature = o.getSignature();
                    for (OWLEntity e : signature) {
                        if (!e.isOWLObjectProperty()) {
                            entities.add(e);
                        }
                    }
                }
                // System.out
                // .println("PopularityDistanceSlice.main() Creating baseDistanceMatrix...");
                // the baseDistanceMatrix is needed for clustering
                // SimpleProximityMatrix<OWLEntity> baseDistanceMatrix = new
                // SimpleProximityMatrix<OWLEntity>(
                // entities, distance);
                // System.out
                // .println("PopularityDistanceSlice.main() baseDistanceMatrix was created");
                // reduce entities wherever distance = 0 -> equivalent classes
                // System.out
                // .println("PopularityDistanceSlice.main() building equivalent classes...");
                MultiMap<OWLEntity, OWLEntity> equivalenceClasses = org.coode.distance.Utils
                        .getEquivalenceClasses(entities, distance);
                entities = new HashSet<OWLEntity>(equivalenceClasses.keySet());
                // System.out
                // .println("PopularityDistanceSlice.main() building distanceMatrix...");
                final SimpleProximityMatrix<OWLEntity> distanceMatrix = new SimpleProximityMatrix<OWLEntity>(
                        entities, distance);
                // System.out.println(String.format(
                // "Finished computing distance between %d entities",
                // distanceMatrix
                // .getObjects().size()));
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
                        return wrappedMatrix.getDistance(a.iterator().next().getIndex(),
                                b.iterator().next().getIndex());
                    }
                };
                PairFilter<Collection<? extends DistanceTableObject<OWLEntity>>> filter = DistanceThresholdBasedFilter
                        .build(distanceMatrix.getData(), 1);
                // System.out.println("Building clustering matrix....");
                ClusteringProximityMatrix<DistanceTableObject<OWLEntity>> clusteringMatrix = ClusteringProximityMatrix
                        .build(wrappedMatrix,
                                new CentroidProximityMeasureFactory(),
                                filter,
                                PairFilterBasedComparator.build(filter, newObjects,
                                        singletonDistance),
                                new SimpleHistoryItemFactory<Collection<? extends DistanceTableObject<OWLEntity>>>());
                // System.out.println(String.format(
                // "Finished building clustering matrix of %d entities",
                // clusteringMatrix.getObjects().size()));
                // System.out.println("Start clustering");
                // int i = 1;
                clusteringMatrix = clusteringMatrix.reduce(filter);
                while (clusteringMatrix.getMinimumDistancePair() != null
                        && filter.accept(clusteringMatrix.getMinimumDistancePair()
                                .getFirst(), clusteringMatrix.getMinimumDistancePair()
                                .getSecond())) {
                    clusteringMatrix = clusteringMatrix.agglomerate(filter);
                }
                // System.out.println(String.format(
                // "Finished clustering after %d agglomerations no of clusters %d",
                // i, clusteringMatrix.getObjects().size()));
                // file for saving results
                File outfile = new File(saveTo);
                final Set<Cluster<OWLEntity>> buildClusters = buildClusters(
                        clusteringMatrix, distanceMatrix);
                System.out.println(line + " " + new Date());
                save(buildClusters, manager, outfile,
                        Utility.unwrapHistory(clusteringMatrix.getHistory()));
                correct &= ClusteringUtils.check(onto, saveTo, compareTo);
            } catch (Throwable e) {
                System.out
                        .println("PopularityBasedDistanceClusteringSlice.main() errors with: "
                                + line);
                e.printStackTrace(System.out);
            }
        }
        System.out.println("correct? " + correct);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
