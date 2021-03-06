package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.distance.Distance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerNamedFillersImpl;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.utils.SimpleMetric;
import org.coode.utils.owl.DistanceCreator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasoner;
import uk.ac.manchester.cs.factplusplus.owlapiv3.OWLKnowledgeExplorationReasonerWrapper;

/** @author eleni */
public class SemanticClusteringOfBigOntologiesWithADEvaluationExperiment extends
        ClusteringWithADEvaluationExperimentBase {
    private final static String RESULTS_BASE = "similarity/experiment-results/semantic/";

    // private static MultiArrayMap<String, SimpleMetric<?>> metricMap = new
    // MultiArrayMap<String, SimpleMetric<?>>();
    // private static Map<String, MultiArrayMap<String, Number>>
    // detailedMetricMap = new HashMap<String, MultiArrayMap<String,Number>>();
    /** @param args
     *            args
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     * @throws OPPLException
     *             OPPLException
     * @throws ParserConfigurationException
     *             ParserConfigurationException
     * @throws FileNotFoundException
     *             FileNotFoundException
     * @throws TransformerFactoryConfigurationError
     *             TransformerFactoryConfigurationError
     * @throws TransformerException
     *             TransformerException */
    public static void main(String[] args) throws OWLOntologyCreationException,
            OPPLException, ParserConfigurationException, FileNotFoundException,
            TransformerFactoryConfigurationError, TransformerException {
        String base = "similarity/experiment-ontologies/";
        String[] input = new String[] { "amino-acid-original.owl", "flowers7.owl",
                "wine.owl", "sct-20100731-stated_Hypertension-subs_module.owl",
                "kupkb/kupkb.owl", "ChronicALLModule.owl", "generations.owl" };
        // String[] input = new String[] { "amino-acid-original.owl",
        // "flowers7.owl", "wine.owl",
        // "sct-20100731-stated_Hypertension-subs_module.owl",
        // "kupkb/kupkb.owl", "obi.owl", "ChronicALLModule.owl",
        // "tambis-full.owl", "galen.owl" };
        // long currentTimeMillis = System.currentTimeMillis();
        new File(RESULTS_BASE).mkdirs();
        File file = new File(RESULTS_BASE + "semantic-allstats.csv");
        setupClusteringExperiment(base, input, file);
    }

    /** @param baseDir
     *            baseDir
     * @param input
     *            input
     * @param allResultsFile
     *            allResultsFile
     * @throws FileNotFoundException
     *             FileNotFoundException
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     * @throws OPPLException
     *             OPPLException
     * @throws ParserConfigurationException
     *             ParserConfigurationException
     * @throws TransformerFactoryConfigurationError
     *             TransformerFactoryConfigurationError
     * @throws TransformerException
     *             TransformerException */
    public static void setupClusteringExperiment(String baseDir, String[] input,
            File allResultsFile) throws FileNotFoundException,
            OWLOntologyCreationException, OPPLException, ParserConfigurationException,
            TransformerFactoryConfigurationError, TransformerException {
        String current;
        for (String s : input) {
            ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
            System.out
                    .println("\n PopularityClusteringWithADEvaluationExperiment.main() \t "
                            + s);
            String substring = s.substring(s.indexOf("/") + 1);
            String filename = RESULTS_BASE + substring.replaceAll(".owl", ".csv");
            // String xml = RESULTS_BASE + substring.replaceAll(".owl", ".xml");
            File f = new File(filename);
            if (!f.exists()) {
                PrintStream singleOut = new PrintStream(f);
                current = baseDir + s;
                // load ontology and get general ontology metrics
                OWLOntologyManager m = OWLManager.createOWLOntologyManager();
                OWLOntology o = m.loadOntologyFromOntologyDocument(new File(current));
                ExperimentHelper.stripOntologyFromAnnotationAssertions(o);
                metrics.add(new SimpleMetric<String>("Ontology", s));
                metrics.addAll(getBasicOntologyMetrics(m));
                // get KE metrics
                KnowledgeExplorer ke = runFactplusplusKnowledgeExplorerReasoner(o);
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
                String clustering_type = "object-property-relevance";
                ClusterDecompositionModel<OWLEntity> model = SemanticClusteringWithADEvaluationExperiment
                        .run(clustering_type, metrics, singleOut, o, distance,
                                filteredSignature, entailments);
                SemanticClusteringWithADEvaluationExperiment.saveResults(clustering_type
                        + substring, model);
                // structural
                clustering_type = "structural-relevance";
                distance = DistanceCreator
                        .createStructuralKnowledgeExplorerAxiomRelevanceBasedDistance(o,
                                ke);
                model = SemanticClusteringWithADEvaluationExperiment.run(clustering_type,
                        metrics, singleOut, o, distance, ke.getEntities(), entailments);
                SemanticClusteringWithADEvaluationExperiment.saveResults(clustering_type
                        + substring, model);
                // popularity distance
                clustering_type = "popularity";
                distance = DistanceCreator
                        .createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(o, ke);
                model = SemanticClusteringWithADEvaluationExperiment.run(clustering_type,
                        metrics, singleOut, o, distance, ke.getEntities(), entailments);
                SemanticClusteringWithADEvaluationExperiment.saveResults(clustering_type
                        + substring, model);
                printMetrics(metrics, allResultsFile);
                firstTime = false;
            }
        }
    }

    /** @param o
     *            o
     * @return knowledge explorer */
    public static KnowledgeExplorer
            runFactplusplusKnowledgeExplorerReasoner(OWLOntology o) {
        OWLReasoner reasoner = new FaCTPlusPlusReasoner(o, new SimpleConfiguration(),
                BufferingMode.NON_BUFFERING);
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        KnowledgeExplorer ke = new KnowledgeExplorerNamedFillersImpl(reasoner,
                new OWLKnowledgeExplorationReasonerWrapper(new FaCTPlusPlusReasoner(o,
                        new SimpleConfiguration(), BufferingMode.NON_BUFFERING)));
        return ke;
    }
}
