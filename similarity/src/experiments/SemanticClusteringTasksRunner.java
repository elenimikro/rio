package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillersFactplusplusImpl;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

import uk.ac.manchester.cs.jfact.JFactReasoner;

public class SemanticClusteringTasksRunner extends
		SyntacticClusteringWithADEvaluationExperiment {

	/**
	 * @param args
	 */
	private final static String RESULTS_BASE = "similarity/experiment-results/semantic/extra/";

	public static void main(String[] args) throws OWLOntologyCreationException,
			OPPLException, ParserConfigurationException, FileNotFoundException,
			TransformerFactoryConfigurationError, TransformerException {

		BufferedReader d = new BufferedReader(new FileReader(new File(args[0])));
		ArrayList<String> inputList = new ArrayList<String>();
		try {
			String s = "";
			while ((s = d.readLine()) != null) {
				inputList.add(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		new File(RESULTS_BASE).mkdirs();
		File output = new File(RESULTS_BASE + "semantic-allstats.csv");
		for (int i = 0; i < inputList.size(); i++) {
			System.out.println(inputList.get(i));
		}

		setupClusteringExperiment(inputList, output);
	}

	public static void setupClusteringExperiment(ArrayList<String> input,
			File allResultsFile) throws FileNotFoundException,
			OWLOntologyCreationException, OPPLException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {
		for (final String s : input) {
			final ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
			System.out.println("\n SemanticClusteringTasksRunner \t " + s);
			String substring = s.substring(s.lastIndexOf("/") + 1);
			String filename = RESULTS_BASE
					+ substring.replaceAll(".owl", ".csv");
			System.out
					.println("SemanticClusteringTasksRunner.setupClusteringExperiment() "
							+ substring);
			File f = new File(filename);
			if (!f.exists()) {

				final PrintStream singleOut = new PrintStream(f);

				OWLOntologyManager m = OWLManager.createOWLOntologyManager();
				OWLOntology o = m.loadOntologyFromOntologyDocument(new File(s));
				ExperimentHelper.stripOntologyFromAnnotationAssertions(o);
				metrics.add(new SimpleMetric<String>("Ontology", s));
				metrics.addAll(getBasicOntologyMetrics(m));

				JFactReasoner reasoner = new JFactReasoner(o,
						new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
				reasoner.precomputeInferences();
				KnowledgeExplorer ke = new KnowledgeExplorerMaxFillersFactplusplusImpl(
						reasoner);
				Set<OWLAxiom> entailments = ke.getAxioms();

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
				clustering_type = "structural";
				distance = DistanceCreator
						.createStructuralKnowledgeExplorerAxiomRelevanceBasedDistance(
								o, ke);
				model = SemanticClusteringWithADEvaluationExperiment.run(
						clustering_type, metrics, singleOut, o, distance,
						ke.getEntities(), entailments);
				SemanticClusteringWithADEvaluationExperiment.saveResults(
						substring, o, clustering_type, model, distance);

				// property relevance
				clustering_type = "property";
				Set<OWLEntity> filteredSignature = SemanticClusteringWithADEvaluationExperiment
						.getSignatureWithoutProperties(ke);
				distance = DistanceCreator
						.createKnowledgeExplorerOWLEntityRelevanceBasedDistance(
								m, ke);
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
