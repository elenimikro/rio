package org.coode.popularitydistance.profiling;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
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
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.pair.filter.PairFilter;
import org.coode.proximitymatrix.CentroidProximityMeasureFactory;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.ProximityMatrix;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;
import org.coode.proximitymatrix.cluster.SimpleCluster;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.proximitymatrix.cluster.commandline.Utility;
import org.coode.proximitymatrix.ui.ClusterStatisticsTableModel;
import org.coode.utils.OntologyManagerUtils;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;
import org.w3c.dom.Document;

/** @author eleni */
public class PopularityBasedDistanceClusteringSlice {
    /**
     * This program takes an ontology as an input, computes pairwise distances between all entities
     * in the signature of the ontology, and build the proximity matrix. The distance is computed
     * with the use of popularity ranking of an entity.
     * 
     * @param args args
     * @throws Exception Exception
     */
    public static void main(String[] args) throws Exception {
        File ontologyList = new File(args[0]);
        BufferedReader r =
            new BufferedReader(new InputStreamReader(new FileInputStream(ontologyList)));
        String line = r.readLine();
        System.out.println("PopularityBasedDistanceClusteringSlice.main() line: \t" + line);
        boolean correct = true;
        while (line != null && !line.trim().isEmpty()) {
            OWLOntologyManager manager = OntologyManagerUtils.ontologyManager();
            File ontology = new File(line);
            Calendar c = Calendar.getInstance();
            String saveTo = "results/" + ontology.getName() + "_" + c.get(Calendar.DAY_OF_MONTH)
                + "_" + c.get(Calendar.HOUR) + ".xml";
            String compareTo =
                "profiling_data/results/" + ontology.getName() + "_clustering_results.xml";
            line = r.readLine();
            IRI iri = IRI.create(ontology);
            try {
                OWLOntology onto = manager.loadOntology(iri);
                System.out.println(line + " " + new Date());
                OWLEntityReplacer owlEntityReplacer =
                    new OWLEntityReplacer(manager.getOWLDataFactory(),
                        new ReplacementByKindStrategy(manager.getOWLDataFactory()));
                AxiomRelevanceAxiomBasedDistance distance = new AxiomRelevanceAxiomBasedDistance(
                    onto.importsClosure(), owlEntityReplacer, manager);
                Collection<OWLEntity> entities = asList(
                    Utils.getSortedSignature(onto).stream().filter(p -> !p.isOWLObjectProperty()));
                MultiMap<OWLEntity, OWLEntity> equivalenceClasses =
                    org.coode.distance.Utils.getEquivalenceClasses(entities, distance);
                entities = new HashSet<>(equivalenceClasses.keySet());
                final SimpleProximityMatrix<OWLEntity> distanceMatrix =
                    new SimpleProximityMatrix<>(entities, distance);
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
                    (a, b) -> wrappedMatrix.getDistance(a.iterator().next().getIndex(),
                        b.iterator().next().getIndex());
                PairFilter<Collection<? extends DistanceTableObject<OWLEntity>>> filter =
                    DistanceThresholdBasedFilter.build(distanceMatrix.getData(), 1);
                // System.out.println("Building clustering matrix....");
                ClusteringProximityMatrix<DistanceTableObject<OWLEntity>> clusteringMatrix =
                    ClusteringProximityMatrix.build(wrappedMatrix,
                        new CentroidProximityMeasureFactory(), filter,
                        PairFilterBasedComparator.build(filter, newObjects, singletonDistance));
                // System.out.println(String.format(
                // "Finished building clustering matrix of %d entities",
                // clusteringMatrix.getObjects().size()));
                // System.out.println("Start clustering");
                // int i = 1;
                clusteringMatrix = clusteringMatrix.reduce(filter);
                while (clusteringMatrix.getMinimumDistancePair() != null
                    && filter.accept(clusteringMatrix.getMinimumDistancePair().getFirst(),
                        clusteringMatrix.getMinimumDistancePair().getSecond())) {
                    clusteringMatrix = clusteringMatrix.agglomerate(filter);
                }
                // System.out.println(String.format(
                // "Finished clustering after %d agglomerations no of clusters %d",
                // i, clusteringMatrix.getObjects().size()));
                // file for saving results
                File outfile = new File(saveTo);
                Set<Cluster<OWLEntity>> buildClusters =
                    buildClusters(clusteringMatrix, distanceMatrix);
                System.out.println(line + " " + new Date());
                save(buildClusters, manager, outfile);
                // , Utility.unwrapHistory(clusteringMatrix.getHistory())
                correct &= ClusteringUtils.check(onto, saveTo, compareTo);
                System.out.println("correct? " + correct);
            } catch (Throwable e) {
                System.out
                    .println("PopularityBasedDistanceClusteringSlice.main() errors with: " + line);
                e.printStackTrace(System.out);
            }
        }
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

    private static <P extends OWLEntity> void save(Collection<? extends Cluster<P>> clusters,
        OWLOntologyManager manager, File file) {
        try {
            List<OWLOntology> ontologies = asList(manager.ontologies());
            OWLOntology ontology = ontologies.get(0);
            OPPLFactory factory = new OPPLFactory(manager, ontology, null);
            ConstraintSystem constraintSystem = factory.createConstraintSystem();
            SortedSet<Cluster<P>> sortedClusters =
                new TreeSet<>(ClusterStatisticsTableModel.SIZE_COMPARATOR);
            sortedClusters.addAll(clusters);
            OWLObjectGeneralisation generalisation =
                Utils.getOWLObjectGeneralisation(sortedClusters, ontologies, constraintSystem);
            Document xml = Utils.toXML(sortedClusters, ontologies,
                new ManchesterOWLSyntaxOWLObjectRendererImpl(), generalisation);
            Transformer t = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(file);
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
            t.transform(new DOMSource(xml), result);
            System.out.println(String.format("Results saved in %s", file.getAbsolutePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
