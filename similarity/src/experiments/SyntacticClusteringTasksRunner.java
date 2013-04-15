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
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class SyntacticClusteringTasksRunner extends
		SyntacticClusteringWithADEvaluationExperiment {

	/**
	 * @param args
	 */
	private final static String RESULTS_BASE = "similarity/experiment-results/syntactic/extra/";

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
		File output = new File(RESULTS_BASE + "syntactic-allstats.csv");
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
			System.out.println("\n SyntacticClusteringTasksRunner \t " + s);
			String substring = s.substring(s.lastIndexOf("/") + 1);
			String filename = RESULTS_BASE
					+ substring.replaceAll(".owl", ".csv");
			System.out
					.println("SyntacticClusteringTasksRunner.setupClusteringExperiment() "
							+ substring);
			String xml = RESULTS_BASE + substring.replaceAll(".owl", ".xml");
			File f = new File(filename);
			if (!f.exists()) {

				final PrintStream singleOut = new PrintStream(f);

				// load ontology and get general ontology metrics
				OWLOntologyManager m = OWLManager.createOWLOntologyManager();
				OWLOntology o = m.loadOntologyFromOntologyDocument(new File(s));
				ExperimentHelper.stripOntologyFromAnnotationAssertions(o);
				metrics.add(new SimpleMetric<String>("Ontology", s));
				metrics.addAll(ClusteringWithADEvaluationExperimentBase
						.getBasicOntologyMetrics(m));

				// popularity distance
				Distance<OWLEntity> distance = DistanceCreator
						.createAxiomRelevanceAxiomBasedDistance(m);
				String clustering_type = "popularity";
				ClusterDecompositionModel<OWLEntity> model = run(
						clustering_type, metrics, singleOut, o, distance, null);
				SyntacticClusteringWithADEvaluationExperiment.saveResults(
						substring, clustering_type, model);

				// structural
				distance = DistanceCreator
						.createStructuralAxiomRelevanceAxiomBasedDistance(m);
				clustering_type = "structural";
				model = run(clustering_type, metrics, singleOut, o, distance,
						null);
				SyntacticClusteringWithADEvaluationExperiment.saveResults(
						substring, clustering_type, model);
				//
				// // property relevance
				Set<OWLEntity> set = getSignatureWithoutProperties(o);
				distance = DistanceCreator
						.createOWLEntityRelevanceAxiomBasedDistance(m);
				clustering_type = "object-property-relevance";
				model = run(clustering_type, metrics, singleOut, o, distance,
						set);
				SyntacticClusteringWithADEvaluationExperiment.saveResults(
						substring, clustering_type, model);

				printMetrics(metrics, allResultsFile);

				firstTime = false;
			}
		}
	}

}
