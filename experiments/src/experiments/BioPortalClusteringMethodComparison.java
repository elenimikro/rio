package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.proximitymatrix.cluster.GeneralisationDecompositionModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.SimpleMetric;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.util.MultiMap;
import org.xml.sax.SAXException;

/** @author eleni */
public class BioPortalClusteringMethodComparison {
    private final static String RESULTS_BASE = "previva-experiment/";
    private final static ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();

    /** @param args
     *            args */
    public static void main(String[] args) {
        try {
            String bioportalList = "BioPortal_relativeRepositoryIRIs.txt";
            BufferedReader d = new BufferedReader(new FileReader(new File(bioportalList)));
            ArrayList<String> inputList = new ArrayList<String>();
            String s = "";
            while ((s = d.readLine()) != null) {
                inputList.add(s);
            }
            d.close();
            runComparison(inputList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (UnknownOWLOntologyException e) {
            e.printStackTrace();
        }
    }

    /** @param input
     *            input
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     * @throws FileNotFoundException
     *             FileNotFoundException
     * @throws ParserConfigurationException
     *             ParserConfigurationException
     * @throws SAXException
     *             SAXException
     * @throws IOException
     *             IOException
     * @throws UnknownOWLOntologyException
     *             UnknownOWLOntologyException */
    public static void runComparison(ArrayList<String> input)
            throws OWLOntologyCreationException, FileNotFoundException,
            ParserConfigurationException, SAXException, IOException,
            UnknownOWLOntologyException {
        File fout = new File(RESULTS_BASE + "bioportal_cluster_similarity_total.csv");
        for (String s : input) {
            metrics.clear();
            List<SimpleMetric<?>> indi_metrics = new ArrayList<SimpleMetric<?>>();
            String substring = s.substring(s.lastIndexOf("/") + 1);
            File indi_file = new File(RESULTS_BASE + substring.replaceAll(".owl", ".txt"));
            String xmlname = RESULTS_BASE + "_popularity_" + "-"
                    + substring.replaceAll(".owl", ".xml");
            File popularity_xml = new File(xmlname);
            File structural_xml = new File(RESULTS_BASE + "_structural_" + "-"
                    + substring.replaceAll(".owl", ".xml"));
            File property_xml = new File(RESULTS_BASE + "_property_" + "-"
                    + substring.replaceAll(".owl", ".xml"));
            if (popularity_xml.exists() && structural_xml.exists()
                    && property_xml.exists()) {
                System.out
                        .println("BioPortalClusteringMethodComparison.openFilesAndBuildClusters() Ontology "
                                + s);
                OWLOntologyManager m = OWLManager.createOWLOntologyManager();
                OWLOntology o = m.loadOntologyFromOntologyDocument(new File(s));
                metrics.add(new SimpleMetric<String>("Ontology", s));
                Set<Set<OWLEntity>> popularity_clusters = Utils.readFromXML(
                        new FileInputStream(popularity_xml), o.getOWLOntologyManager());
                GeneralisationDecompositionModel<OWLEntity> popularity_model = new GeneralisationDecompositionModel<OWLEntity>(
                        popularity_clusters, o);
                Set<Set<OWLEntity>> structural_clusters = Utils.readFromXML(
                        new FileInputStream(structural_xml), o.getOWLOntologyManager());
                GeneralisationDecompositionModel<OWLEntity> structural_model = new GeneralisationDecompositionModel<OWLEntity>(
                        structural_clusters, o);
                Set<Set<OWLEntity>> property_clusters = Utils.readFromXML(
                        new FileInputStream(property_xml), o.getOWLOntologyManager());
                GeneralisationDecompositionModel<OWLEntity> property_model = new GeneralisationDecompositionModel<OWLEntity>(
                        property_clusters, o);
                indi_metrics.addAll(compareClusters(popularity_model, structural_model,
                        property_model, o));
                indi_metrics.addAll(compareGeneralisation(popularity_model,
                        structural_model, property_model));
                printDetailedMetrics(indi_metrics, indi_file);
                ClusteringWithADEvaluationExperimentBase.printMetrics(metrics, fout);
                ClusteringWithADEvaluationExperimentBase.firstTime = false;
            }
        }
    }

    private static void printDetailedMetrics(List<SimpleMetric<?>> indi_metrics,
            File indi_file) {
        try {
            PrintStream out = new PrintStream(indi_file);
            for (SimpleMetric<?> metric : indi_metrics) {
                out.println(metric.getName() + ": " + metric.getValue());
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static List<SimpleMetric<?>> compareGeneralisation(
            GeneralisationDecompositionModel<OWLEntity> popularity_model,
            GeneralisationDecompositionModel<OWLEntity> structural_model,
            GeneralisationDecompositionModel<OWLEntity> property_model) {
        List<SimpleMetric<?>> indi_metrics = new ArrayList<SimpleMetric<?>>();
        MultiMap<OWLAxiom, OWLAxiomInstantiation> popularityGeneralisationMap = popularity_model
                .getGeneralisationMap();
        MultiMap<OWLAxiom, OWLAxiomInstantiation> structuralGeneralisationMap = structural_model
                .getGeneralisationMap();
        MultiMap<OWLAxiom, OWLAxiomInstantiation> propertyGeneralisationMap = property_model
                .getGeneralisationMap();
        // Generalisations similarity
        GeneralisationComparison comparator = new GeneralisationComparison();
        double similarity = comparator.getGeneralisationSimilarity(
                popularityGeneralisationMap, structuralGeneralisationMap);
        indi_metrics.add(new SimpleMetric<Double>("GeneralisationSimilarity_pop-struc",
                similarity));
        metrics.add(new SimpleMetric<Double>("GeneralisationSimilarity_pop-struc",
                similarity));
        similarity = comparator.getGeneralisationSimilarity(popularityGeneralisationMap,
                propertyGeneralisationMap);
        indi_metrics.add(new SimpleMetric<Double>("GeneralisationSimilarity_pop-prop",
                similarity));
        metrics.add(new SimpleMetric<Double>("GeneralisationSimilarity_pop-prop",
                similarity));
        similarity = comparator.getGeneralisationSimilarity(structuralGeneralisationMap,
                propertyGeneralisationMap);
        indi_metrics.add(new SimpleMetric<Double>("GeneralisationSimilarity_prop-struc",
                similarity));
        metrics.add(new SimpleMetric<Double>("GeneralisationSimilarity_prop-struc",
                similarity));
        // Instantiation similarity
        similarity = comparator.getOWLInstantiationSimilarity(
                popularityGeneralisationMap, structuralGeneralisationMap);
        indi_metrics.add(new SimpleMetric<Double>("InstantiationSimilarity_pop-struc",
                similarity));
        metrics.add(new SimpleMetric<Double>("InstantiationSimilarity_pop-struc",
                similarity));
        similarity = comparator.getOWLInstantiationSimilarity(
                popularityGeneralisationMap, propertyGeneralisationMap);
        indi_metrics.add(new SimpleMetric<Double>("InstantiationSimilarity_pop-prop",
                similarity));
        metrics.add(new SimpleMetric<Double>("InstantiationSimilarity_pop-prop",
                similarity));
        similarity = comparator.getOWLInstantiationSimilarity(
                structuralGeneralisationMap, propertyGeneralisationMap);
        indi_metrics.add(new SimpleMetric<Double>("InstantiationSimilarity_prop-struc",
                similarity));
        metrics.add(new SimpleMetric<Double>("InstantiationSimilarity_prop-struc",
                similarity));
        // ////////////////////Intersections///////////////////////
        // Generalisation
        indi_metrics.add(new SimpleMetric<String>("GeneralisationIntersection_pop-struc",
                comparator
                        .getGeneralisationIntersection(popularityGeneralisationMap,
                                structuralGeneralisationMap).toString()
                        .replaceAll(",", "\t")));
        indi_metrics.add(new SimpleMetric<String>("GeneralisationIntersection_pop-prop",
                comparator
                        .getGeneralisationIntersection(popularityGeneralisationMap,
                                propertyGeneralisationMap).toString()
                        .replaceAll(",", "\t")));
        indi_metrics.add(new SimpleMetric<String>(
                "GeneralisationIntersection_struc-prop", comparator
                        .getGeneralisationIntersection(structuralGeneralisationMap,
                                propertyGeneralisationMap).toString()
                        .replaceAll(",", "\t")));
        // OWLInstantiations
        indi_metrics.add(new SimpleMetric<String>("InstantiationIntersection_pop-struc",
                comparator.getOWLInstantiationIntersection(popularityGeneralisationMap,
                        structuralGeneralisationMap).toString()));
        indi_metrics.add(new SimpleMetric<String>("InstantiationIntersection_pop-prop",
                comparator.getOWLInstantiationIntersection(popularityGeneralisationMap,
                        propertyGeneralisationMap).toString()));
        indi_metrics.add(new SimpleMetric<String>("InstantiationIntersection_struc-prop",
                comparator.getOWLInstantiationIntersection(structuralGeneralisationMap,
                        propertyGeneralisationMap).toString()));
        return indi_metrics;
    }

    private static List<SimpleMetric<?>> compareClusters(
            GeneralisationDecompositionModel<OWLEntity> popularity_model,
            GeneralisationDecompositionModel<OWLEntity> structural_model,
            GeneralisationDecompositionModel<OWLEntity> property_model, OWLOntology o) {
        List<SimpleMetric<?>> indi_metrics = new ArrayList<SimpleMetric<?>>();
        List<Set<OWLEntity>> popularityClusterList = popularity_model.getClusterList();
        List<Set<OWLEntity>> structuralClusterList = structural_model.getClusterList();
        List<Set<OWLEntity>> propertyClusterList = property_model.getClusterList();
        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        for (OWLOntology onto : o.getImportsClosure()) {
            entities.addAll(onto.getSignature());
        }
        // map entities to clusters
        Map<OWLEntity, Integer> popularityMap = new HashMap<OWLEntity, Integer>();
        Map<OWLEntity, Integer> structuralMap = new HashMap<OWLEntity, Integer>();
        Map<OWLEntity, Integer> propertyMap = new HashMap<OWLEntity, Integer>();
        for (OWLEntity e : entities) {
            for (int i = 0; i < popularityClusterList.size(); i++) {
                if (popularityClusterList.get(i).contains(e)) {
                    popularityMap.put(e, i);
                }
            }
            for (int i = 0; i < structuralClusterList.size(); i++) {
                if (structuralClusterList.get(i).contains(e)) {
                    structuralMap.put(e, i);
                }
            }
            for (int i = 0; i < propertyClusterList.size(); i++) {
                if (propertyClusterList.get(i).contains(e)) {
                    propertyMap.put(e, i);
                }
            }
        }
        double popularityStructuralSimilarity = compareSimilarities(
                popularityClusterList, structuralClusterList, popularityMap,
                structuralMap, entities, "popularity", "structural", indi_metrics);
        metrics.add(new SimpleMetric<Double>("ClusterSimilarity_pop-struc",
                popularityStructuralSimilarity));
        double popularityPropertySimilarity = compareSimilarities(popularityClusterList,
                propertyClusterList, popularityMap, propertyMap, entities, "popularity",
                "property", indi_metrics);
        metrics.add(new SimpleMetric<Double>("ClusterSimilarity_pop-prop",
                popularityPropertySimilarity));
        double structuralPropertySimilarity = compareSimilarities(structuralClusterList,
                propertyClusterList, structuralMap, propertyMap, entities, "structural",
                "property", indi_metrics);
        metrics.add(new SimpleMetric<Double>("ClusterSimilarity_struc-prop",
                structuralPropertySimilarity));
        // out.println(popularityStructuralSimilarity + ","
        // + popularityPropertySimilarity + ","
        // + structuralPropertySimilarity);
        return indi_metrics;
    }

    private static double compareSimilarities(List<Set<OWLEntity>> clusterListA,
            List<Set<OWLEntity>> clusterListB, Map<OWLEntity, Integer> mapA,
            Map<OWLEntity, Integer> mapB, Set<OWLEntity> entities, String clusteringA,
            String clusteringB, List<SimpleMetric<?>> indi_metrics) {
        Set<OWLEntity> visitedEntities = new HashSet<OWLEntity>();
        ClusterComparison<OWLEntity> comparator = new ClusterComparison<OWLEntity>();
        double totalSimilarity = 0;
        double counter = 0;
        // compute similarity
        for (OWLEntity s : entities) {
            if (!visitedEntities.contains(s)) {
                // A to B
                Integer indexA = mapA.get(s);
                Integer indexB = mapB.get(s);
                if (indexA != null && indexB != null) {
                    double clusterSimilarity = comparator.getClusterSimilarity(
                            clusterListA.get(indexA), clusterListB.get(indexB));
                    // indi_out.print(clusteringA + " Cluster " + indexA + " "
                    // + clusteringB + " Cluster " + indexB + ","
                    // + clusterSimilarity + ",");
                    indi_metrics.add(new SimpleMetric<Double>(clusteringA + " Cluster "
                            + indexA + " " + clusteringB + " Cluster " + indexB,
                            clusterSimilarity));
                    Set<OWLEntity> intersection = comparator.getIntersection(
                            clusterListA.get(indexA), clusterListB.get(indexB));
                    visitedEntities.addAll(intersection);
                    indi_metrics.add(new SimpleMetric<String>("Intersection",
                            intersection.toString()));
                    // indi_out.println("Intersection, " + intersection);
                    totalSimilarity += clusterSimilarity;
                    counter++;
                }
            }
        }
        double toReturn = totalSimilarity / counter;
        return toReturn;
    }
}
