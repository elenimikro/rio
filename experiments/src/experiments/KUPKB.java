package experiments;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;

import org.coode.distance.Distance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.utils.SimpleMetric;
import org.coode.utils.owl.DistanceCreator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class KUPKB extends ClusteringWithADEvaluationExperimentBase {
	private final static String root = "similarity/experiment-results/kupkbsemantic/";
	private final static String input = "http://www.e-lico.eu/public/kupkb/kupkb.owl";

	public static void main(String[] args) throws Exception {
		new File(root).mkdirs();
		String suffix = "semantic-allstats.csv";
		String pop = "_popularity_";
		String struc = "_structural_";
		String prop = "_objectproperty_";
		setupClusteringExperimentPopularity(pop, "kupkb", new File(root
				+ "kupkb" + pop + suffix));
		setupClusteringExperimentStructural(struc, "kupkb", new File(root
				+ "kupkb" + struc + suffix));
		setupClusteringExperimentProperty(prop, "kupkb", new File(root
				+ "kupkb" + prop + suffix));
	}

	public static void setupClusteringExperimentPopularity(String type,
			String current, File allResultsFile) throws Exception {
		ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
		String filename = root + current.replaceAll(".owl", "") + type + ".csv";
		File f = new File(filename);
		if (!f.exists()) {
			// load ontology and get general ontology metrics
			OWLOntologyManager m = OWLManager.createOWLOntologyManager();
			OWLOntology o = ResolveImports.resolveImports("kupkb", input);
			ExperimentHelper.stripOntologyFromAnnotationAssertions(o);
			System.out.println("KUPKB.setupClusteringExperimentPopularity() "
					+ o.getAxiomCount());
			metrics.add(new SimpleMetric<String>("Ontology", current));
			metrics.addAll(getBasicOntologyMetrics(m));
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
			PrintStream singleOut = new PrintStream(f);
			ClusterDecompositionModel<OWLEntity> model = SemanticClusteringWithADEvaluationExperiment
					.run(type, metrics, singleOut, o, distance,
							ke.getEntities(), entailments);
			singleOut.close();
			SemanticClusteringWithADEvaluationExperiment.saveResults(current
					+ type, model);
			printMetrics(metrics, allResultsFile);
			firstTime = false;
		} else {
			System.out
					.println("SnomedSemanticClusteringWithADEvaluationExperiment.setupClusteringExperimentPopularity() already done");
		}
	}

	public static void setupClusteringExperimentStructural(String type,
			String current, File allResultsFile) throws Exception {
		ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
		String filename = root + current.replaceAll(".owl", "") + type + ".csv";
		File f = new File(filename);
		if (!f.exists()) {
			// load ontology and get general ontology metrics
			OWLOntologyManager m = OWLManager.createOWLOntologyManager();
			OWLOntology o = ResolveImports.resolveImports("kupkb", input);
			ExperimentHelper.stripOntologyFromAnnotationAssertions(o);
			System.out.println("KUPKB.setupClusteringExperimentStructural() "
					+ o.getAxiomCount());
			metrics.add(new SimpleMetric<String>("Ontology", current));
			metrics.addAll(getBasicOntologyMetrics(m));
			// get KE metrics
			KnowledgeExplorer ke = ExperimentUtils
					.runFactplusplusKnowledgeExplorerReasoner(o);
			Set<OWLAxiom> entailments = ke.getAxioms();
			System.out
					.println("SemanticClusteringOfBigOntologiesWithADEvaluationExperiment.setupClusteringExperiment() Entailments "
							+ entailments.size());
			metrics.add(new SimpleMetric<Integer>("#Entailments", entailments
					.size()));
			// structural
			Distance<OWLEntity> distance = DistanceCreator
					.createStructuralKnowledgeExplorerAxiomRelevanceBasedDistance(
							o, ke);
			PrintStream singleOut = new PrintStream(f);
			ClusterDecompositionModel<OWLEntity> model = SemanticClusteringWithADEvaluationExperiment
					.run(type, metrics, singleOut, o, distance,
							ke.getEntities(), entailments);
			singleOut.close();
			SemanticClusteringWithADEvaluationExperiment.saveResults(current
					+ type, model);
			printMetrics(metrics, allResultsFile);
			firstTime = false;
		} else {
			System.out
					.println("SnomedSemanticClusteringWithADEvaluationExperiment.setupClusteringExperimentStructural() already done");
		}
	}

	public static void setupClusteringExperimentProperty(String type,
			String current, File allResultsFile) throws Exception {
		ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
		String filename = root + current.replaceAll(".owl", "") + type + ".csv";
		File f = new File(filename);
		if (!f.exists()) {
			// load ontology and get general ontology metrics
			OWLOntologyManager m = OWLManager.createOWLOntologyManager();
			OWLOntology o = ResolveImports.resolveImports("kupkb", input);
			ExperimentHelper.stripOntologyFromAnnotationAssertions(o);
			System.out.println("KUPKB.setupClusteringExperimentProperty() "
					+ o.getAxiomCount());
			metrics.add(new SimpleMetric<String>("Ontology", current));
			metrics.addAll(getBasicOntologyMetrics(m));
			// get KE metrics
			KnowledgeExplorer ke = ExperimentUtils
					.runFactplusplusKnowledgeExplorerReasoner(o);
			Set<OWLAxiom> entailments = ke.getAxioms();
			System.out
					.println("SemanticClusteringOfBigOntologiesWithADEvaluationExperiment.setupClusteringExperiment() Entailments "
							+ entailments.size());
			metrics.add(new SimpleMetric<Integer>("#Entailments", entailments
					.size()));
			// property relevance
			Set<OWLEntity> filteredSignature = SemanticClusteringWithADEvaluationExperiment
					.getSignatureWithoutProperties(ke);
			Distance<OWLEntity> distance = DistanceCreator
					.createKnowledgeExplorerOWLEntityRelevanceBasedDistance(o,
							ke);
			PrintStream singleOut = new PrintStream(f);
			ClusterDecompositionModel<OWLEntity> model = SemanticClusteringWithADEvaluationExperiment
					.run(type, metrics, singleOut, o, distance,
							filteredSignature, entailments);
			singleOut.close();
			SemanticClusteringWithADEvaluationExperiment.saveResults(current
					+ type, model);
			printMetrics(metrics, allResultsFile);
			firstTime = false;
		} else {
			System.out
					.println("SnomedSemanticClusteringWithADEvaluationExperiment.setupClusteringExperimentProperty() already done");
		}
	}
}
