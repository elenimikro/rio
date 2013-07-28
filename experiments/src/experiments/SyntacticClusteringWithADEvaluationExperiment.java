package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.basetest.DistanceCreator;
import org.coode.basetest.TestHelper;
import org.coode.distance.Distance;
import org.coode.distance.wrapping.DistanceTableObject;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecomposition;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class SyntacticClusteringWithADEvaluationExperiment extends
		ClusteringWithADEvaluationExperimentBase {

	private final static String RESULTS_BASE = "similarity/experiment-results/syntactic/";

	public static void main(String[] args) throws OWLOntologyCreationException,
			OPPLException, ParserConfigurationException, FileNotFoundException,
			TransformerFactoryConfigurationError, TransformerException {

		String base = "similarity/experiment-ontologies/";
		String[] input = new String[] { "amino-acid-original.owl",
				"flowers7.owl", "wine.owl",
				"sct-20100731-stated_Hypertension-subs_module.owl",
				"kupkb/kupkb.owl", "obi.owl", "ChronicALLModule.owl",
				"tambis-full.owl", "galen.owl" };
		// long currentTimeMillis = System.currentTimeMillis();
		// Calendar c = Calendar.getInstance();
		new File(RESULTS_BASE).mkdirs();
		File file = new File(RESULTS_BASE + "allstats.csv");

		setupClusteringExperiment(base, input, file);
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

			File f = new File(filename);
			if (!f.exists()) {
				PrintStream singleOut = new PrintStream(f);
				current = baseDir + s;

				// load ontology and get general ontology metrics
				OWLOntologyManager m = OWLManager.createOWLOntologyManager();
				OWLOntology o = TestHelper
						.loadFileMappers(new File(current), m);
				metrics.add(new SimpleMetric<String>("Ontology", s));
				metrics.addAll(getBasicOntologyMetrics(m));
				ExperimentHelper.stripOntologyFromAnnotationAssertions(o);

				// popularity distance
				Distance<OWLEntity> distance = DistanceCreator
						.createAxiomRelevanceAxiomBasedDistance(m);
				String clustering_type = "popularity";
				ClusterDecompositionModel<OWLEntity> model = run(
						clustering_type, metrics, singleOut, o, distance, null);
				saveResults(substring, clustering_type, model);

				// structural
				distance = DistanceCreator
						.createStructuralAxiomRelevanceAxiomBasedDistance(m);
				clustering_type = "structural";
				model = run(clustering_type, metrics, singleOut, o, distance,
						null);
				saveResults(substring, clustering_type, model);
				//
				// // property relevance
				Set<OWLEntity> set = getSignatureWithoutProperties(o);
				distance = DistanceCreator
						.createOWLEntityRelevanceAxiomBasedDistance(m);
				clustering_type = "object-property-relevance";
				model = run(clustering_type, metrics, singleOut, o, distance,
						set);
				saveResults(substring, clustering_type, model);

				printMetrics(metrics, allResultsFile);
				firstTime = false;
			}
		}
	}

	protected static File saveResults(String substring, String clustering_type,
			ClusterDecompositionModel<OWLEntity> model)
			throws ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException,
			FileNotFoundException {
		String xmlname = RESULTS_BASE + clustering_type + "-"
				+ substring.replaceAll(".owl", ".xml");
		File xml = new File(xmlname);
		PrintStream out = new PrintStream(new File(xmlname + ".txt"));
		ClusterResultsExploitationUtils.printGeneralisationStats(model, out,
				xmlname);
		ClusteringProximityMatrix<DistanceTableObject<OWLEntity>> clusteringMatrix = ExperimentHelper
				.getClusteringMatrix();
		// ClusterResultsExploitationUtils.filterResults(clusteringMatrix,
		// model,
		// distance, out);
		Utils.saveToXML(model, xml);
		out.close();
		return xml;
	}

	protected static ClusterDecompositionModel<OWLEntity> run(
			String distanceType, ArrayList<SimpleMetric<?>> metrics,
			PrintStream singleOut, OWLOntology o, Distance<OWLEntity> distance,
			Set<OWLEntity> clusteringSignature) throws OPPLException,
			ParserConfigurationException {
		System.out.println("ClusteringWithADEvaluationExperiment.main() \t "
				+ distanceType);
		metrics.add(new SimpleMetric<String>("Clustering-type", distanceType));
		ClusterDecompositionModel<OWLEntity> model = ExperimentHelper
				.startSyntacticClustering(o, distance, clusteringSignature);
		metrics.addAll(getMetrics(singleOut, o, model));
		return model;
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

	protected static Set<OWLEntity> getSignatureWithoutProperties(OWLOntology o) {
		// final SimpleShortFormProvider shortFormProvider = new
		// SimpleShortFormProvider();
		Set<OWLEntity> entities = new HashSet<OWLEntity>();
		// exclude all properties
		for (OWLOntology ontology : o.getImportsClosure()) {
			for (OWLEntity e : ontology.getSignature()) {
				if (!e.isType(EntityType.OBJECT_PROPERTY)
						&& !e.isType(EntityType.DATA_PROPERTY)
						&& !e.isType(EntityType.ANNOTATION_PROPERTY)
						&& !e.isType(EntityType.DATATYPE)) {
					entities.add(e);
				}
			}
		}
		return entities;
	}
}
