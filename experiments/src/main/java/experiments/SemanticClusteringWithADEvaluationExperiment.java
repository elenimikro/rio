package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.distance.Distance;
import org.coode.knowledgeexplorer.ChainsawKnowledgeExplorerMaxFillersImpl;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecomposition;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.SimpleMetric;
import org.coode.utils.owl.DistanceCreator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

import uk.ac.manchester.cs.chainsaw.ChainsawReasoner;
import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasoner;
import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory;

/** @author eleni */
public class SemanticClusteringWithADEvaluationExperiment extends
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
                KnowledgeExplorer ke = ExperimentUtils
                        .runFactplusplusKnowledgeExplorerReasoner(o);
                Set<OWLAxiom> entailments = ke.getAxioms();
                System.out
                        .println("SemanticClusteringOfBigOntologiesWithADEvaluationExperiment.setupClusteringExperiment() Entailments "
                                + entailments.size());
                metrics.add(new SimpleMetric<Integer>("#Entailments", entailments.size()));
                // property relevance
                Set<OWLEntity> filteredSignature = getSignatureWithoutProperties(ke);
                Distance<OWLEntity> distance = DistanceCreator
                        .createKnowledgeExplorerOWLEntityRelevanceBasedDistance(o, ke);
                String clustering_type = "object-property-relevance";
                ClusterDecompositionModel<OWLEntity> model = run(clustering_type,
                        metrics, singleOut, o, distance, filteredSignature, entailments);
                saveResults(clustering_type + substring, model);
                // structural
                clustering_type = "structural-relevance";
                distance = DistanceCreator
                        .createStructuralKnowledgeExplorerAxiomRelevanceBasedDistance(o,
                                ke);
                model = run(clustering_type, metrics, singleOut, o, distance,
                        ke.getEntities(), entailments);
                saveResults(clustering_type + substring, model);
                // popularity distance
                clustering_type = "popularity";
                distance = DistanceCreator
                        .createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(o, ke);
                model = run(clustering_type, metrics, singleOut, o, distance,
                        ke.getEntities(), entailments);
                saveResults(clustering_type + substring, model);
                distance = null;
                printMetrics(metrics, allResultsFile);
                firstTime = false;
            }
        }
    }

    /** @param distanceType
     *            distanceType
     * @param metrics
     *            metrics
     * @param singleOut
     *            singleOut
     * @param o
     *            o
     * @param distance
     *            distance
     * @param clusteringSignature
     *            clusteringSignature
     * @param entailments
     *            entailments
     * @return cluster decomposition model
     * @throws OPPLException
     *             OPPLException
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException */
    public static ClusterDecompositionModel<OWLEntity> run(String distanceType,
            ArrayList<SimpleMetric<?>> metrics, PrintStream singleOut, OWLOntology o,
            Distance<OWLEntity> distance, Set<OWLEntity> clusteringSignature,
            Set<OWLAxiom> entailments) throws OPPLException, OWLOntologyCreationException {
        System.out.println("ClusteringWithADEvaluationExperiment.main() \t "
                + distanceType);
        metrics.add(new SimpleMetric<String>("Clustering-type", distanceType));
        ClusterDecompositionModel<OWLEntity> model = ExperimentHelper
                .startSemanticClustering(o, entailments, distance, clusteringSignature);
        OWLOntology inferedOnto = OWLManager.createOWLOntologyManager().createOntology(
                entailments);
        metrics.addAll(getMetrics(singleOut, inferedOnto, model));
        return model;
    }

    private static Collection<? extends SimpleMetric<?>> getMetrics(
            PrintStream singleOut, OWLOntology o,
            ClusterDecompositionModel<OWLEntity> model) {
        ArrayList<SimpleMetric<?>> toReturn = new ArrayList<SimpleMetric<?>>();
        GeneralisedAtomicDecomposition<OWLEntity> gad = new GeneralisedAtomicDecomposition<OWLEntity>(
                model, o);
        toReturn.addAll(getADMetrics(gad.getAtomicDecomposer()));
        toReturn.addAll(ExperimentHelper.getClusteringMetrics(model));
        toReturn.addAll(ExperimentHelper.getAtomicDecompositionGeneralisedMetrics(gad));
        toReturn.addAll(getClusteringStats(singleOut, model.getClusterList()));
        return toReturn;
    }

    /** @param ke
     *            ke
     * @return signature */
    public static Set<OWLEntity> getSignatureWithoutProperties(KnowledgeExplorer ke) {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        // exclude all properties
        for (OWLEntity e : ke.getEntities()) {
            if (!e.isType(EntityType.OBJECT_PROPERTY)
                    && !e.isType(EntityType.DATA_PROPERTY)
                    && !e.isType(EntityType.ANNOTATION_PROPERTY)
                    && !e.isType(EntityType.DATATYPE)) {
                entities.add(e);
            }
        }
        return entities;
    }

    /** @param o
     *            o
     * @return knowledge explorer */
    public static KnowledgeExplorer runChainsawFactplusplusKnowledgeExplorerReasoner(
            OWLOntology o) {
        OWLReasoner reasoner = new FaCTPlusPlusReasoner(o, new SimpleConfiguration(),
                BufferingMode.NON_BUFFERING);
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        KnowledgeExplorer chainke = new ChainsawKnowledgeExplorerMaxFillersImpl(reasoner,
                new ChainsawReasoner(new FaCTPlusPlusReasonerFactory(), o,
                        new SimpleConfiguration()));
        return chainke;
    }

    protected static File saveResults(String xmlPrefix,
            ClusterDecompositionModel<OWLEntity> model
    // ,Distance<OWLEntity> distance
            ) throws ParserConfigurationException, TransformerFactoryConfigurationError,
                    TransformerException, FileNotFoundException {
        String xmlname = RESULTS_BASE + xmlPrefix + ".xml";
        File xml = new File(xmlname);
        PrintStream out = new PrintStream(new File(xmlname + ".txt"));
        ClusterResultsExploitationUtils.printGeneralisationStats(model, out, xmlname);
        out.close();
        // ClusteringProximityMatrix<DistanceTableObject<OWLEntity>>
        // clusteringMatrix = ExperimentHelper
        // .getClusteringMatrix();
        // ClusterResultsExploitationUtils.filterResults(clusteringMatrix,
        // model,
        // distance, out);
        Utils.saveToXML(model, xml);
        return xml;
    }
}
