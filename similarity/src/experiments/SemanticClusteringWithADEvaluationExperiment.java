package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerChainsawJFactImpl;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecomposition;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

import uk.ac.manchester.cs.jfact.JFactReasoner;

public class SemanticClusteringWithADEvaluationExperiment extends
		ClusteringWithADEvaluationExperimentBase {

	private final static String RESULTS_BASE = "similarity/experiment-results/semantic/";

	// private static MultiArrayMap<String, SimpleMetric<?>> metricMap = new
	// MultiArrayMap<String, SimpleMetric<?>>();
	// private static Map<String, MultiArrayMap<String, Number>>
	// detailedMetricMap = new HashMap<String, MultiArrayMap<String,Number>>();

	public static void main(String[] args) throws OWLOntologyCreationException,
			OPPLException, ParserConfigurationException, FileNotFoundException {
		String base = "similarity/experiment-ontologies/";
		String[] input = new String[] { "amino-acid-original.owl",
				"flowers7.owl", "wine.owl",
				"sct-20100731-stated_Hypertension-subs_module.owl",
				"kupkb/kupkb.owl", "obi.owl", "ChronicALLModule.owl",
				"tambis-full.owl", "galen.owl" };
		// long currentTimeMillis = System.currentTimeMillis();
		File file = new File(RESULTS_BASE + "semantic-allstats.csv");

		setupClusteringExperiment(base, input, file);
	}

	public static void setupClusteringExperiment(String baseDir,
			String[] input, File allResultsFile) throws FileNotFoundException,
			OWLOntologyCreationException, OPPLException,
			ParserConfigurationException {
		String current;
		for (String s : input) {
			ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
			System.out
					.println("\n PopularityClusteringWithADEvaluationExperiment.main() \t "
							+ s);
			String substring = s.substring(s.indexOf("/") + 1);
			String filename = RESULTS_BASE
					+ substring.replaceAll(".owl", ".csv");
			String xml = RESULTS_BASE + substring.replaceAll(".owl", ".xml");
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

				JFactReasoner reasoner = new JFactReasoner(o,
						new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
				reasoner.precomputeInferences();

				KnowledgeExplorer ke = new KnowledgeExplorerChainsawJFactImpl(
						reasoner);
				// KnowledgeExplorer ke = new
				// KnowledgeExplorerMaxFillersFactplusplusImpl(
				// reasoner);
				Set<OWLAxiom> entailments = ke.getAxioms();

				// popularity distance
				Distance<OWLEntity> distance = DistanceCreator
						.createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(
								m, ke);
				run("popularity", metrics, singleOut, o, distance,
						ke.getEntities(), entailments);

				// property relevance
				Set<OWLEntity> filteredSignature = getSignatureWithoutProperties(ke);
				distance = DistanceCreator
						.createKnowledgeExplorerOWLEntityRelevanceBasedDistance(
								m, ke);
				run("object-property-relevance", metrics, singleOut, o,
						distance, filteredSignature, entailments);

				// structural
				distance = DistanceCreator
						.createStructuralKnowledgeExplorerAxiomRelevanceBasedDistance(
								o, ke);
				run("structural-relevance", metrics, singleOut, o, distance,
						ke.getEntities(), entailments);

				printMetrics(metrics, allResultsFile);

				firstTime = false;
			}
		}
	}

	public static void run(String distanceType,
			ArrayList<SimpleMetric<?>> metrics, PrintStream singleOut,
			OWLOntology o, Distance<OWLEntity> distance,
			Set<OWLEntity> clusteringSignature, Set<OWLAxiom> entailments)
			throws OPPLException, ParserConfigurationException,
			OWLOntologyCreationException {
		System.out.println("ClusteringWithADEvaluationExperiment.main() \t "
				+ distanceType);
		metrics.add(new SimpleMetric<String>("Clustering-type", distanceType));
		ClusterDecompositionModel<OWLEntity> model = ExperimentHelper
				.startSemanticClustering(o, entailments, distance,
						clusteringSignature);
		OWLOntology inferedOnto = OWLManager.createOWLOntologyManager()
				.createOntology(entailments);
		metrics.addAll(getMetrics(singleOut, inferedOnto, model));
	}

	private static Collection<? extends SimpleMetric<?>> getMetrics(
			PrintStream singleOut, OWLOntology o,
			ClusterDecompositionModel<OWLEntity> model) {
		ArrayList<SimpleMetric<?>> toReturn = new ArrayList<SimpleMetric<?>>();
		GeneralisedAtomicDecomposition<OWLEntity> gad = new GeneralisedAtomicDecomposition<OWLEntity>(
				model, o);

		toReturn.addAll(getADMetrics(gad.getAtomicDecomposer()));
		toReturn.addAll(ExperimentHelper.getClusteringMetrics(model));
		toReturn.addAll(ExperimentHelper
				.getAtomicDecompositionGeneralisedMetrics(gad));
		toReturn.addAll(getClusteringStats(singleOut, model.getClusterList()));
		return toReturn;
	}

	public static Set<OWLEntity> getSignatureWithoutProperties(
			KnowledgeExplorer ke) {
		// final SimpleShortFormProvider shortFormProvider = new
		// SimpleShortFormProvider();
		Set<OWLEntity> entities = new HashSet<OWLEntity>();
		// exclude all properties
		for (OWLEntity e : ke.getEntities()) {
			if (!e.isType(EntityType.OBJECT_PROPERTY)
					&& !e.isType(EntityType.DATA_PROPERTY)
					&& !e.isType(EntityType.ANNOTATION_PROPERTY)
					&& !e.isType(EntityType.DATATYPE)) {
				entities.add(e);
			}
		}
		return entities;
	}
}
