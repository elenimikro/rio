package experiments;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;

import org.coode.distance.Distance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.utils.SimpleMetric;
import org.coode.utils.owl.DistanceCreator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
public class SnomedSemanticClusteringWithADEvaluationExperiment extends
        ClusteringWithADEvaluationExperimentBase {
    private final static String root = "similarity/experiment-results/semantic/";
    private final static String base = "snomed/";

    /** @param args
     *            args
     * @throws Exception
     *             Exception */
    public static void main(String[] args) throws Exception {
        String[] input = new String[] { "chronic_module.owl", "acute_module.owl",
                "present_clinical_finding_module.owl" };
        new File(root).mkdirs();
        String suffix = "semantic-allstats.csv";
        // File file = new File(root + suffix);
        System.out
                .println("SnomedSemanticClusteringWithADEvaluationExperiment.main() press enter to start");
        System.in.read();
        String pop = "_popularity_";
        String struc = "_structural_";
        String prop = "_objectproperty_";
        String i2 = input[2];
        String i1 = input[1];
        String i0 = input[0];
        setupClusteringExperimentPopularity(pop, i2, new File(root + i2 + pop + suffix));
        setupClusteringExperimentStructural(struc, i2, new File(root + i2 + struc
                + suffix));
        setupClusteringExperimentProperty(prop, i2, new File(root + i2 + prop + suffix));
        setupClusteringExperimentPopularity(pop, i1, new File(root + i1 + pop + suffix));
        setupClusteringExperimentStructural(struc, i1, new File(root + i1 + struc
                + suffix));
        setupClusteringExperimentProperty(prop, i1, new File(root + i1 + prop + suffix));
        setupClusteringExperimentStructural(struc, i0, new File(root + i0 + struc
                + suffix));
        setupClusteringExperimentProperty(prop, i0, new File(root + i0 + prop + suffix));
        setupClusteringExperimentPopularity(pop, i0, new File(root + i0 + pop + suffix));
    }

    /** @param type
     *            type
     * @param current
     *            current
     * @param allResultsFile
     *            allResultsFile
     * @throws Exception
     *             Exception */
    public static void setupClusteringExperimentPopularity(String type, String current,
            File allResultsFile) throws Exception {
        ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
        String filename = root + current.replaceAll(".owl", "") + type + ".csv";
        File f = new File(filename);
        if (!f.exists()) {
            // load ontology and get general ontology metrics
            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            OWLOntology o = m.loadOntologyFromOntologyDocument(new File(base + current));
            ExperimentHelper.stripOntologyFromAnnotationAssertions(o);
            metrics.add(new SimpleMetric<String>("Ontology", current));
            metrics.addAll(getBasicOntologyMetrics(m));
            // get KE metrics
            KnowledgeExplorer ke = ExperimentUtils
                    .runFactplusplusKnowledgeExplorerReasoner(o);
            Set<OWLAxiom> entailments = ke.getAxioms();
            System.out.println("Entailments " + entailments.size());
            metrics.add(new SimpleMetric<Integer>("#Entailments", entailments.size()));
            // popularity distance
            Distance<OWLEntity> distance = DistanceCreator
                    .createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(o, ke);
            PrintStream singleOut = new PrintStream(f);
            ClusterDecompositionModel<OWLEntity> model = SemanticClusteringWithADEvaluationExperiment
                    .run(type, metrics, singleOut, o, distance, ke.getEntities(),
                            entailments);
            singleOut.close();
            SemanticClusteringWithADEvaluationExperiment.saveResults(current + type,
                    model);
            printMetrics(metrics, allResultsFile);
            firstTime = false;
        } else {
            System.out
                    .println("SnomedSemanticClusteringWithADEvaluationExperiment.setupClusteringExperimentPopularity() already done");
        }
    }

    /** @param type
     *            type
     * @param current
     *            current
     * @param allResultsFile
     *            allResultsFile
     * @throws Exception
     *             Exception */
    public static void setupClusteringExperimentStructural(String type, String current,
            File allResultsFile) throws Exception {
        ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
        String filename = root + current.replaceAll(".owl", "") + type + ".csv";
        File f = new File(filename);
        if (!f.exists()) {
            // load ontology and get general ontology metrics
            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            OWLOntology o = m.loadOntologyFromOntologyDocument(new File(base + current));
            ExperimentHelper.stripOntologyFromAnnotationAssertions(o);
            metrics.add(new SimpleMetric<String>("Ontology", current));
            metrics.addAll(getBasicOntologyMetrics(m));
            // get KE metrics
            KnowledgeExplorer ke = ExperimentUtils
                    .runFactplusplusKnowledgeExplorerReasoner(o);
            Set<OWLAxiom> entailments = ke.getAxioms();
            System.out
                    .println("SemanticClusteringOfBigOntologiesWithADEvaluationExperiment.setupClusteringExperiment() Entailments "
                            + entailments.size());
            metrics.add(new SimpleMetric<Integer>("#Entailments", entailments.size()));
            // structural
            Distance<OWLEntity> distance = DistanceCreator
                    .createStructuralKnowledgeExplorerAxiomRelevanceBasedDistance(o, ke);
            PrintStream singleOut = new PrintStream(f);
            ClusterDecompositionModel<OWLEntity> model = SemanticClusteringWithADEvaluationExperiment
                    .run(type, metrics, singleOut, o, distance, ke.getEntities(),
                            entailments);
            singleOut.close();
            SemanticClusteringWithADEvaluationExperiment.saveResults(current + type,
                    model);
            printMetrics(metrics, allResultsFile);
            firstTime = false;
        } else {
            System.out
                    .println("SnomedSemanticClusteringWithADEvaluationExperiment.setupClusteringExperimentStructural() already done");
        }
    }

    /** @param type
     *            type
     * @param current
     *            current
     * @param allResultsFile
     *            allResultsFile
     * @throws Exception
     *             Exception */
    public static void setupClusteringExperimentProperty(String type, String current,
            File allResultsFile) throws Exception {
        ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
        String filename = root + current.replaceAll(".owl", "") + type + ".csv";
        File f = new File(filename);
        if (!f.exists()) {
            // load ontology and get general ontology metrics
            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            OWLOntology o = m.loadOntologyFromOntologyDocument(new File(base + current));
            ExperimentHelper.stripOntologyFromAnnotationAssertions(o);
            metrics.add(new SimpleMetric<String>("Ontology", current));
            metrics.addAll(getBasicOntologyMetrics(m));
            // get KE metrics
            KnowledgeExplorer ke = ExperimentUtils
                    .runFactplusplusKnowledgeExplorerReasoner(o);
            Set<OWLAxiom> entailments = ke.getAxioms();
            System.out
                    .println("SemanticClusteringOfBigOntologiesWithADEvaluationExperiment.setupClusteringExperiment() Entailments "
                            + entailments.size());
            metrics.add(new SimpleMetric<Integer>("#Entailments", entailments.size()));
            // property relevance
            Set<OWLEntity> filteredSignature = SemanticClusteringWithADEvaluationExperiment
                    .getSignatureWithoutProperties(ke);
            Distance<OWLEntity> distance = DistanceCreator
                    .createKnowledgeExplorerOWLEntityRelevanceBasedDistance(o, ke);
            PrintStream singleOut = new PrintStream(f);
            ClusterDecompositionModel<OWLEntity> model = SemanticClusteringWithADEvaluationExperiment
                    .run(type, metrics, singleOut, o, distance, filteredSignature,
                            entailments);
            singleOut.close();
            SemanticClusteringWithADEvaluationExperiment.saveResults(current + type,
                    model);
            printMetrics(metrics, allResultsFile);
            firstTime = false;
        } else {
            System.out
                    .println("SnomedSemanticClusteringWithADEvaluationExperiment.setupClusteringExperimentProperty() already done");
        }
    }
}
