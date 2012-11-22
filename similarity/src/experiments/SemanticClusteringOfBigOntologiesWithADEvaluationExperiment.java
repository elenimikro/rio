package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerNamedFillersImpl;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
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

public class SemanticClusteringOfBigOntologiesWithADEvaluationExperiment extends
		ClusteringWithADEvaluationExperimentBase {

	private final static String RESULTS_BASE = "similarity/experiment-results/semantic/";

	// private static MultiArrayMap<String, SimpleMetric<?>> metricMap = new
	// MultiArrayMap<String, SimpleMetric<?>>();
	// private static Map<String, MultiArrayMap<String, Number>>
	// detailedMetricMap = new HashMap<String, MultiArrayMap<String,Number>>();

	public static void main(String[] args) throws OWLOntologyCreationException,
			OPPLException, ParserConfigurationException, FileNotFoundException,
			TransformerFactoryConfigurationError, TransformerException {
		String base = "similarity/experiment-ontologies/";
		String[] input = new String[] { "amino-acid-original.owl",
				"flowers7.owl", "wine.owl",
				"sct-20100731-stated_Hypertension-subs_module.owl",
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

	public static void setupClusteringExperiment(String baseDir,
			String[] input, File allResultsFile) throws FileNotFoundException,
			OWLOntologyCreationException, OPPLException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {
		String current;
		for (String s : input) {
			ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
			System.out
					.println("\n PopularityClusteringWithADEvaluationExperiment.main() \t "
							+ s);
			String substring = s.substring(s.indexOf("/") + 1);
			String filename = RESULTS_BASE
					+ substring.replaceAll(".owl", ".csv");
			// String xml = RESULTS_BASE + substring.replaceAll(".owl", ".xml");
			File f = new File(filename);
			if (!f.exists()) {
				PrintStream singleOut = new PrintStream(f);
				current = baseDir + s;

				// load ontology and get general ontology metrics
				OWLOntologyManager m = OWLManager.createOWLOntologyManager();
				OWLOntology o = m.loadOntologyFromOntologyDocument(new File(
						current));
				ExperimentHelper.stripOntologyFromAnnotationAssertions(o);
				metrics.add(new SimpleMetric<String>("Ontology", s));
				metrics.addAll(getBasicOntologyMetrics(m));

				// get KE metrics
				KnowledgeExplorer ke = runFactplusplusKnowledgeExplorerReasoner(o);
				Set<OWLAxiom> entailments = ke.getAxioms();
				System.out
						.println("SemanticClusteringOfBigOntologiesWithADEvaluationExperiment.setupClusteringExperiment() Entailments "
								+ entailments.size());
				metrics.add(new SimpleMetric<Integer>("#Entailments",
						entailments.size()));

				// property relevance
				Set<OWLEntity> filteredSignature = SemanticClusteringWithADEvaluationExperiment
						.getSignatureWithoutProperties(ke);
				Distance<OWLEntity> distance = DistanceCreator
						.createKnowledgeExplorerOWLEntityRelevanceBasedDistance(
								m, ke);
				String clustering_type = "object-property-relevance";
				ClusterDecompositionModel<OWLEntity> model = SemanticClusteringWithADEvaluationExperiment
						.run(clustering_type, metrics, singleOut, o, distance,
								filteredSignature, entailments);
				SemanticClusteringWithADEvaluationExperiment.saveResults(
substring,
                        clustering_type, model);

				// structural
				clustering_type = "structural-relevance";
				distance = DistanceCreator
						.createStructuralKnowledgeExplorerAxiomRelevanceBasedDistance(
								o, ke);
				model = SemanticClusteringWithADEvaluationExperiment.run(
						clustering_type, metrics, singleOut, o, distance,
						ke.getEntities(), entailments);
				SemanticClusteringWithADEvaluationExperiment.saveResults(
substring,
                        clustering_type, model);

				// popularity distance
				clustering_type = "popularity";
				distance = DistanceCreator
						.createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(
								m, ke);
				model = SemanticClusteringWithADEvaluationExperiment.run(
						clustering_type, metrics, singleOut, o, distance,
						ke.getEntities(), entailments);
				SemanticClusteringWithADEvaluationExperiment.saveResults(
substring,
                        clustering_type, model);

				printMetrics(metrics, allResultsFile);

				firstTime = false;
			}
		}
	}

	public static KnowledgeExplorer runFactplusplusKnowledgeExplorerReasoner(
			OWLOntology o) {
		OWLReasoner reasoner = new FaCTPlusPlusReasoner(o,
				new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		KnowledgeExplorer ke = new KnowledgeExplorerNamedFillersImpl(reasoner,
				new OWLKnowledgeExplorationReasonerWrapper(
						new FaCTPlusPlusReasoner(o, new SimpleConfiguration(),
								BufferingMode.NON_BUFFERING)));

		return ke;
	}

}
