package org.coode.owl.experiments.quality.assurance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.GeneralisationDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisationStatistics;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.util.MultiMap;
import org.xml.sax.SAXException;

import experiments.ExperimentUtils;
import experiments.SimpleMetric;

public class ISWCSyntacticMetricsRecomputed {

	private static final String ASSERTED_ONTOLOGY = "/Volumes/Passport-mac/Expeiments/snomed/2013/";
	private static final String SEMANTIC_BASE = "/Volumes/Passport-mac/Expeiments/inferred_Results_20130506/";
	public static final String VARIABLE_NAME_INVALID_CHARACTERS_REGEXP = "[[^\\?]&&[^\\p{Alnum}]&&[^-_]]";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			ArrayList<String> keywords = ExperimentUtils
					.extractListFromFile("processedSemanticKeywords.txt");
			File toSaveMetrics = new File("semantic_general_metrics.csv");
			List<SimpleMetric<?>> stats = new ArrayList<SimpleMetric<?>>();

			OWLOntology o = ExperimentUtils.loadOntology(new File(
					ASSERTED_ONTOLOGY));

			for (String keyword : keywords) {
				System.out.println("Keyword " + keyword);
				LexicalAndAxiomaticPatternBasedQualityAssurance qa = new LexicalAndAxiomaticPatternBasedQualityAssurance(
						keyword, o);
				File readXML = new File(SEMANTIC_BASE
						+ keyword.replaceAll(
								VARIABLE_NAME_INVALID_CHARACTERS_REGEXP, "_")
						+ "_regularities.xml");
				System.out.println("Loading ontology...");

				System.out.println("Loading regularities...");
				Set<Set<OWLEntity>> clusters = Utils.readFromXML(
						new FileInputStream(readXML),
						OWLManager.createOWLOntologyManager());
				System.out.println("Computing stats...");
				GeneralisationDecompositionModel<OWLEntity> model = new GeneralisationDecompositionModel<OWLEntity>(
						clusters, o);
				stats.addAll(GeneralisationStatistics.buildStatistics(model)
						.getStats());

			}
			ExperimentUtils.printMetrics(stats, toSaveMetrics);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownOWLOntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OPPLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void flatten(Set<Set<OWLEntity>> clusters) {
		for (Set<OWLEntity> cluster : clusters) {
		}

	}

	public static MultiMap<OWLEntity, Integer> mapEntitiesToCluster(
			List<Set<OWLEntity>> clusterList, Set<OWLEntity> signature) {
		MultiMap<OWLEntity, Integer> map = new MultiMap<OWLEntity, Integer>();
		for (OWLEntity e : signature) {
			for (int i = 0; i < clusterList.size(); i++) {
				if (clusterList.get(i).contains(e)) {
					map.put(e, i);
				}
			}
		}
		return map;
	}
}
