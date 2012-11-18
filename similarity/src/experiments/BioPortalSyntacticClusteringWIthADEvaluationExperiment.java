package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.oppl.exceptions.OPPLException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class BioPortalSyntacticClusteringWIthADEvaluationExperiment extends
		SyntacticClusteringWithADEvaluationExperiment {

	private final static String RESULTS_BASE = "similarity/experiment-results/syntactic/bioportal/";

	public static void main(String[] args) throws OWLOntologyCreationException,
			OPPLException, ParserConfigurationException, FileNotFoundException {

		String bioportalList = "similarity/BioPortal_relativeRepositoryIRIs.txt";
		// BufferedReader d = new BufferedReader(new InputStreamReader(
		// new FileInputStream(new File(bioportalList))));
		BufferedReader d = new BufferedReader(new FileReader(new File(
				bioportalList)));
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
		File output = new File(RESULTS_BASE + "bioportal-syntactic.csv");
		for (int i = 0; i < inputList.size(); i++) {
			System.out.println(inputList.get(i));
		}
		setupClusteringExperiment(inputList, output);
	}

	public static void setupClusteringExperiment(ArrayList<String> input,
			File allResultsFile) throws FileNotFoundException,
			OWLOntologyCreationException, OPPLException,
			ParserConfigurationException {
		for (final String s : input) {
			final ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
			System.out
					.println("\n PopularityClusteringWithADEvaluationExperiment.main() \t "
							+ s);
			String substring = s.substring(s.lastIndexOf("/") + 1);
			String filename = RESULTS_BASE
					+ substring.replaceAll(".owl", ".csv");
			System.out
					.println("BioPortalSyntacticClusteringWIthADEvaluationExperiment.setupClusteringExperiment() "
							+ substring);
			String xml = RESULTS_BASE + substring.replaceAll(".owl", ".xml");
			File f = new File(filename);
			if (!f.exists()) {

				final PrintStream singleOut = new PrintStream(f);
				Callable<Object> task1 = new Callable<Object>() {
					@Override
					public Object call() throws OWLOntologyCreationException,
							OPPLException, ParserConfigurationException {
						// load ontology and get general ontology metrics
						OWLOntologyManager m = OWLManager
								.createOWLOntologyManager();
						OWLOntology o = m
								.loadOntologyFromOntologyDocument(new File(s));
						ExperimentHelper
								.stripOntologyFromAnnotationAssertions(o);
						metrics.add(new SimpleMetric<String>("Ontology", s));
						metrics.addAll(SyntacticClusteringWithADEvaluationExperiment
								.getBasicOntologyMetrics(m));

						// popularity distance
						Distance<OWLEntity> distance = DistanceCreator
								.createAxiomRelevanceAxiomBasedDistance(m);
						SyntacticClusteringWithADEvaluationExperiment.run(
								"popularity", metrics, singleOut, o, distance,
								null);

						// property relevance
						Set<OWLEntity> set = SyntacticClusteringWithADEvaluationExperiment
								.getSignatureWithoutProperties(o);
						distance = DistanceCreator
								.createOWLEntityRelevanceAxiomBasedDistance(m);
						SyntacticClusteringWithADEvaluationExperiment.run(
								"object-property-relevance", metrics,
								singleOut, o, distance, set);

						distance = DistanceCreator
								.createStructuralAxiomRelevanceAxiomBasedDistance(m);
						SyntacticClusteringWithADEvaluationExperiment.run(
								"structural", metrics, singleOut, o, distance,
								null);
						return null;
					}
				};
				runTaskWithTimeout(task1, 45, TimeUnit.MINUTES);

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
			Object result = future.get(timeout, timeUnit);
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
