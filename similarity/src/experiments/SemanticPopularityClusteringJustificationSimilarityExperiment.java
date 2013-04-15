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

import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.justifications.GeneralisationBasedJustificationSimilarity;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasonerFactory;

public class SemanticPopularityClusteringJustificationSimilarityExperiment {

	private final static String root = "experiment-results/semantic/bioportal/justification-results/";
	private static File outputFile;

	/**
	 * Experiment which takes a number of ontologies from a text file and
	 * computes their semantic regularities
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws OPPLException
	 * @throws OWLOntologyCreationException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			OWLOntologyCreationException, OPPLException,
			ParserConfigurationException {
		String ontologyList = "40BioPortalList.txt";
		new File(root).mkdirs();
		BufferedReader d = new BufferedReader(new FileReader(new File(
				ontologyList)));
		ArrayList<String> inputList = new ArrayList<String>();
		try {
			String s = "";
			while ((s = d.readLine()) != null) {
				inputList.add(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		outputFile = new File(root
				+ "bioportal-popularity-justification-allstats.csv");
		for (int i = 0; i < inputList.size(); i++) {
			runPopularitySemanticClustering(inputList.get(i));
		}
	}

	private static void runPopularitySemanticClustering(String input)
			throws OPPLException, ParserConfigurationException,
			OWLOntologyCreationException, FileNotFoundException {
		ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
		System.out
				.println("\n PopularityClusteringWithADEvaluationExperiment.main() \t "
						+ input);
		String substring = input.substring(input.lastIndexOf("/") + 1);
		String filename = root + substring.replaceAll(".owl", "")
				+ "-popularity.csv";
		System.out
				.println("SemanticPopularityClusteringJustificationSimilarityExperiment.setupPopularityClustering() filename "
						+ filename);
		File f = new File(filename);
		if (!f.exists()) {
			// load ontology and get general ontology metrics
			OWLOntologyManager m = OWLManager.createOWLOntologyManager();
			OWLOntology o = m.loadOntologyFromOntologyDocument(new File(input));
			ExperimentHelper.stripOntologyFromAnnotationAssertions(o);
			metrics.add(new SimpleMetric<String>("Ontology", input));

			// get KE metrics
			KnowledgeExplorer ke = ExperimentUtils
					.runFactplusplusKnowledgeExplorerReasoner(o);
			Set<OWLAxiom> entailments = ke.getAxioms();
			System.out.println("Entailments " + entailments.size());
			metrics.add(new SimpleMetric<Integer>("#Entailments", entailments
					.size()));
			// popularity distance
			Distance<OWLEntity> distance = DistanceCreator
					.createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(o,
							ke);
			metrics.add(new SimpleMetric<String>("Clustering-type",
					"popularity"));
			ClusterDecompositionModel<OWLEntity> model = ExperimentHelper
					.startSemanticClustering(o, entailments, distance,
							ke.getEntities());
			MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = Utils
					.extractGeneralisationMap(model);
			PrintStream singleOut = new PrintStream(f);
			System.out
					.println("SemanticPopularityClusteringJustificationSimilarityExperiment.runPopularitySemanticClustering()  Computing justification similarity....");
			GeneralisationBasedJustificationSimilarity similarity = new GeneralisationBasedJustificationSimilarity(
					new FaCTPlusPlusReasonerFactory(), o, generalisationMap);
			metrics.add(new SimpleMetric<Double>(
					"TotalMeanJustificationSimilarity", similarity
							.getTotalMeanJustificationSimilarity()));
			System.out
					.println("SemanticPopularityClusteringJustificationSimilarityExperiment.runPopularitySemanticClustering() Printing metrics...");
			printJustificationExtendedResults(similarity, singleOut);
			singleOut.close();
			ClusteringWithADEvaluationExperimentBase.printMetrics(metrics,
					outputFile);
			ClusteringWithADEvaluationExperimentBase.firstTime = false;
		} else {
			System.out
					.println("SemanticPopularityClusteringJustificationSimilarityExperiment.setupPopularityClustering() already done.");
		}
	}

	private static void printJustificationExtendedResults(
			GeneralisationBasedJustificationSimilarity similarity, PrintStream p) {

		MultiMap<Double, OWLAxiom> map = similarity
				.getJustificationSimilarityValueMap();
		p.println("#JustificationSimilarity,Generalisations");
		for (Double d : map.keySet()) {
			p.println(d + "," + map.get(d).size() + " "
					+ map.get(d).toString().replaceAll("\t", " "));
		}

	}

}
