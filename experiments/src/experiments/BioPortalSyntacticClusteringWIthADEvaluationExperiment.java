package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.coode.distance.Distance;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.utils.SimpleMetric;
import org.coode.utils.owl.DistanceCreator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class BioPortalSyntacticClusteringWIthADEvaluationExperiment extends
		SyntacticClusteringWithADEvaluationExperiment {

	private final static String RESULTS_BASE = "experiment-results/syntactic/bioportal/previva-experiment/";

	/**
	 * @param args
	 * @throws OWLOntologyCreationException
	 * @throws OPPLException
	 * @throws ParserConfigurationException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws OWLOntologyCreationException,
			OPPLException, ParserConfigurationException, FileNotFoundException {
		if (args.length >= 2) {
			// input file with ontology lists
			String bioportalList = args[0];
			// BufferedReader d = new BufferedReader(new InputStreamReader(
			// new FileInputStream(new File(bioportalList))));
			ArrayList<String> inputList = ExperimentUtils
					.extractListFromFile(bioportalList);
			new File(RESULTS_BASE).mkdirs();
			File output = new File(RESULTS_BASE + "bioportal-syntactic.csv");
			for (int i = 0; i < inputList.size(); i++) {
				System.out.println(inputList.get(i));
			}
			int timeout = Integer.parseInt(args[1]);
			System.out.println("Timeout task " + timeout);
			setupClusteringExperiment(inputList, output, timeout);
		} else {
			System.out
					.println(String
							.format("Usage java -cp ... %s <ontologyListTextFile> <computationTimeoutinMins>",
									BioPortalSyntacticClusteringWIthADEvaluationExperiment.class
											.getCanonicalName()));
		}
	}

	public static void setupClusteringExperiment(ArrayList<String> input,
			File allResultsFile, int timeout) throws FileNotFoundException {
		for (final String s : input) {
			final ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
			System.out
					.println("\n PopularityClusteringWithADEvaluationExperiment.main() \t "
							+ s);
			final String substring = s.substring(s.lastIndexOf("/") + 1);
			String filename = RESULTS_BASE
					+ substring.replaceAll(".owl", ".csv");
			System.out
					.println("BioPortalSyntacticClusteringWIthADEvaluationExperiment.setupClusteringExperiment() "
							+ substring);
			// String xml = RESULTS_BASE + substring.replaceAll(".owl", ".xml");
			File f = new File(filename);
			if (!f.exists()) {

				final PrintStream singleOut = new PrintStream(f);
				Callable<Object> task1 = new Callable<Object>() {
					@Override
					public Object call() throws OWLOntologyCreationException,
							OPPLException, ParserConfigurationException,
							FileNotFoundException, TransformerException {
						// load ontology and get general ontology metrics
						OWLOntologyManager m = OWLManager
								.createOWLOntologyManager();
						OWLOntology o = m
								.loadOntologyFromOntologyDocument(new File(s));
						ExperimentHelper
								.stripOntologyFromAnnotationAssertions(o);
						metrics.add(new SimpleMetric<String>("Ontology", s));
						metrics.addAll(ClusteringWithADEvaluationExperimentBase
								.getBasicOntologyMetrics(m));

						// popularity distance
						Distance<OWLEntity> distance = DistanceCreator
								.createAxiomRelevanceAxiomBasedDistance(m);
						ClusterDecompositionModel<OWLEntity> model = SyntacticClusteringWithADEvaluationExperiment
								.run("popularity", metrics, singleOut, o,
										distance, null);
						SyntacticClusteringWithADEvaluationExperiment
								.saveResults(substring, "_popularity_", model);

						// structural
						distance = DistanceCreator
								.createStructuralAxiomRelevanceAxiomBasedDistance(m);
						model = SyntacticClusteringWithADEvaluationExperiment
								.run("structural", metrics, singleOut, o,
										distance, null);
						SyntacticClusteringWithADEvaluationExperiment
								.saveResults(substring, "_structural_", model);

						// property relevance
						Set<OWLEntity> set = SyntacticClusteringWithADEvaluationExperiment
								.getSignatureWithoutProperties(o);
						distance = DistanceCreator
								.createOWLEntityRelevanceAxiomBasedDistance(m);
						model = SyntacticClusteringWithADEvaluationExperiment
								.run("property", metrics, singleOut, o,
										distance, set);
						SyntacticClusteringWithADEvaluationExperiment
								.saveResults(substring, "_property_", model);

						return null;
					}
				};
				runTaskWithTimeout(task1, timeout, TimeUnit.MINUTES);

				printMetrics(metrics, allResultsFile);
				firstTime = false;
			}
		}
	}

	public static void runTaskWithTimeout(Callable<Object> task, long timeout,
			TimeUnit timeUnit) {
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<Object> future = executor.submit(task);
		try {
			future.get(timeout, timeUnit);
		} catch (TimeoutException ex) {
			System.out.println("Took too long!");
		} catch (InterruptedException e) {
			System.out.println("Interrupted!");
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			future.cancel(false); // may or may not desire this
		}
	}

}
