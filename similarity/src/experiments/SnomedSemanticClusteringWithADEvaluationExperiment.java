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
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class SnomedSemanticClusteringWithADEvaluationExperiment extends
		ClusteringWithADEvaluationExperimentBase {

	private final static String RESULTS_BASE = "similarity/experiment-results/semantic/";

	public static void main(String[] args) throws OWLOntologyCreationException,
			OPPLException, ParserConfigurationException, FileNotFoundException,
			TransformerFactoryConfigurationError, TransformerException {
		String base = "snomed/";
		String[] input = new String[] { "chronic_module.owl",
				"acute_module.owl", "present_clinical_finding_module.owl" };

		new File(RESULTS_BASE).mkdirs();
		File file = new File(RESULTS_BASE + "semantic-allstats.csv");
		SemanticClusteringWithADEvaluationExperiment.setupClusteringExperiment(
				base, input, file);
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
				KnowledgeExplorer ke = SemanticClusteringWithADEvaluationExperiment
						.runFactplusplusKnowledgeExplorerReasoner(o);
				Set<OWLAxiom> entailments = ke.getAxioms();
				System.out
						.println("SemanticClusteringOfBigOntologiesWithADEvaluationExperiment.setupClusteringExperiment() Entailments "
								+ entailments.size());
				metrics.add(new SimpleMetric<Integer>("#Entailments",
						entailments.size()));

				// popularity distance
				String clustering_type = "popularity";
				Distance<OWLEntity> distance = DistanceCreator
						.createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(
								m, ke);
				ClusterDecompositionModel<OWLEntity> model = SemanticClusteringWithADEvaluationExperiment
						.run(clustering_type, metrics, singleOut, o, distance,
								ke.getEntities(), entailments);
				SemanticClusteringWithADEvaluationExperiment.saveResults(
						substring, o, clustering_type, model, distance);

				// structural
				clustering_type = "structural-relevance";
				distance = DistanceCreator
						.createStructuralKnowledgeExplorerAxiomRelevanceBasedDistance(
								o, ke);
				model = SemanticClusteringWithADEvaluationExperiment.run(
						clustering_type, metrics, singleOut, o, distance,
						ke.getEntities(), entailments);
				SemanticClusteringWithADEvaluationExperiment.saveResults(
						substring, o, clustering_type, model, distance);

				// property relevance
				Set<OWLEntity> filteredSignature = SemanticClusteringWithADEvaluationExperiment
						.getSignatureWithoutProperties(ke);
				distance = DistanceCreator
						.createKnowledgeExplorerOWLEntityRelevanceBasedDistance(
								m, ke);
				clustering_type = "object-property-relevance";
				model = SemanticClusteringWithADEvaluationExperiment.run(
						clustering_type, metrics, singleOut, o, distance,
						filteredSignature, entailments);
				SemanticClusteringWithADEvaluationExperiment.saveResults(
						substring, o, clustering_type, model, distance);

				printMetrics(metrics, allResultsFile);

				firstTime = false;
			}
		}
	}
}
