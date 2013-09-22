package org.coode.owl.experiments.quality.assurance;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisationStatistics;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.SimpleMetric;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import experiments.ExperimentUtils;

public class SingleOntologyLexicalPatternRegularityAnalysis {

	public static final String VARIABLE_NAME_INVALID_CHARACTERS_REGEXP = "[[^\\?]&&[^\\p{Alnum}]&&[^-_]]";
	public static final String RESULTS_BASE = "/Volumes/Passport-mac/Expeiments/";
	public static final String ONTOLOGY_BASE = "/Volumes/Passport-mac/Expeiments/snomed/2013/";

	/**
	 * @param <C>
	 * @param args
	 *            [0]:Ontology path
	 */
	public static void main(String[] args) {
		String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar
				.getInstance().getTime());
		String resultsDir = RESULTS_BASE + "Results_" + timeStamp + "/";
		new File(resultsDir).mkdirs();
		// File xmlPatternsFile = new File(args[1]);
		File toSaveMetrics = new File("allLexicalAxiomRegularityResults.csv");

		ArrayList<String> keywords = ExperimentUtils
				.extractListFromFile("processedKeywords.txt");
		System.out.println(String.format("Extracted %d keywords",
				keywords.size()));
		for (String keyword : keywords) {
			String ontoname = ONTOLOGY_BASE + keyword.replaceAll(" ", "_")
					+ "_usage.owl";

			runAnalysis(resultsDir, toSaveMetrics, ontoname, keyword);
		}
		// runAnalysis(resultsDir, toSaveMetrics, processedKeywords, onto,
		// lexpat)

	}

	private static void runAnalysis(String resultsDir, File toSaveMetrics,
			String ontoname, String lexpat) {
		try {

			System.out.println(String.format("Working with %s", lexpat));
			File toXML = new File(resultsDir
					+ lexpat.replaceAll(
							VARIABLE_NAME_INVALID_CHARACTERS_REGEXP, "_")
					+ "_regularities.xml");
			if (!toXML.exists()) {
				File ontoFile = new File(ontoname);
				OWLOntology onto = ExperimentUtils.loadOntology(ontoFile);
				System.out.println("Loaded ontology..." + ontoFile.getPath());

				if (onto.getLogicalAxiomCount() < 20000) {
					LexicalAndAxiomaticPatternBasedQualityAssurance<Cluster<OWLEntity>> qa = new LexicalAndAxiomaticPatternBasedQualityAssurance<Cluster<OWLEntity>>(
							lexpat, onto);
					System.out.println(String.format(
							"Number of target entities: %s", qa
									.getTargetEntities().size()));
					int usageSize = qa.getTargetEntitiesUsage().size();
					System.out.println(String.format(
							"Usage of target entities: %s axioms", usageSize));
					System.out.println("Computing regularities...");

					ClusterDecompositionModel<OWLEntity> model = qa
							.getRegularitiesBasedOnUsage();
					Utils.saveToXML(model, toXML);
					System.out
							.println(String.format(
									"Regularirities were saved in %s",
									toXML.getPath()));
					System.out.println("Printing metrics....");
					List<SimpleMetric<?>> stats = GeneralisationStatistics
							.buildStatistics(model).getStats();
					stats.addAll(qa.getQualityAssuranceStats(model));
					ExperimentUtils.printMetrics(stats, toSaveMetrics);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
