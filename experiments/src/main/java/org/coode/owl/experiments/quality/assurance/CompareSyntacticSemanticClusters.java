package org.coode.owl.experiments.quality.assurance;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.coode.proximitymatrix.cluster.GeneralisationDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisationStatistics;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.SimpleMetric;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

import experiments.ExperimentUtils;

/** @author eleni */
public class CompareSyntacticSemanticClusters {
    private static final String ONTOLOGY_BASE = "/Volumes/Passport-mac/Expeiments/snomed/2013/";
    private static final String SEMANTIC_BASE = "/Volumes/Passport-mac/Expeiments/inferred_Results_20130506/";
    private static final String VARIABLE_NAME_INVALID_CHARACTERS_REGEXP = "[[^\\?]&&[^\\p{Alnum}]&&[^-_]]";

    /** @param args
     *            args
     * @throws Exception
     *             Exception */
    public static void main(String[] args) throws Exception {
        ArrayList<String> keywords = ExperimentUtils
                .extractListFromFile("processedSemanticKeywords.txt");
        File toSaveMetrics = new File("semantic_general_metrics.csv");
        for (String keyword : keywords) {
            List<SimpleMetric<?>> stats = new ArrayList<SimpleMetric<?>>();
            // File readSyntacticXML = new File(keyword + "xml");
            System.out.println("Keyword " + keyword);
            stats.add(new SimpleMetric<String>("keyword", keyword));
            String xml = SEMANTIC_BASE
                    + keyword.replaceAll(VARIABLE_NAME_INVALID_CHARACTERS_REGEXP, "_")
                    + "_regularities.xml";
            System.out.println(xml);
            stats.add(new SimpleMetric<String>("xml", xml));
            File readXML = new File(xml);
            System.out.println("Loading ontology...");
            OWLOntology o = ExperimentUtils.loadOntology(new File(ONTOLOGY_BASE
                    + keyword.replaceAll(" ", "_") + "_usage.owl"));
            stats.add(new SimpleMetric<Integer>("Entailment Number", o
                    .getLogicalAxiomCount()));
            System.out.println("Loading regularities...");
            Set<Set<OWLEntity>> clusters = Utils.readFromXML(
                    new FileInputStream(readXML), o.getOWLOntologyManager());
            System.out.println(clusters);
            System.out.println("Computing stats...");
            GeneralisationDecompositionModel<OWLEntity> model = new GeneralisationDecompositionModel<OWLEntity>(
                    clusters, o);
            model.getGeneralisationMap();
            stats.addAll(GeneralisationStatistics.buildStatistics(model).getStats());
            ExperimentUtils.printMetrics(stats, toSaveMetrics);
        }
    }

    /** @param clusterList
     *            clusterList
     * @param signature
     *            signature
     * @return entities to cluster */
    public static MultiMap<OWLEntity, Integer> mapEntitiesToCluster(
            List<Set<OWLEntity>> clusterList, Set<OWLEntity> signature) {
        MultiMap<OWLEntity, Integer> map = new MultiMap<OWLEntity, Integer>();
        for (OWLEntity e : signature) {
            for (int i = 0; i < clusterList.size(); i++) {
                if (clusterList.get(i).contains(e)) {
                    map.put(e, i);
                }
            }
        }
        return map;
    }
}
