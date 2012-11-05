package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.basetest.ClusterCreator;
import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.ClusterStatistics;
import org.coode.proximitymatrix.cluster.GeneralisationStatistics;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecomposition;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecompositionMetrics;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.atomicdecomposition.Atom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposer;

public class SyntacticClusteringWithADEvaluationExperiment extends ClusteringWithADEvaluationExperimentBase{
	

	private final static String RESULTS_BASE = "/Users/elenimikroyannidi/eclipse-workspace/similarity/similarity/experiment-results/"; 
	
	public static void main(String[] args) throws OWLOntologyCreationException,
			OPPLException, ParserConfigurationException, FileNotFoundException {

		String base = "/Users/elenimikroyannidi/eclipse-workspace/similarity/similarity/experiment-ontologies/";
		String[] input = new String[] { "amino-acid-original.owl",
				"flowers7.owl", "wine.owl",
				"sct-20100731-stated_Hypertension-subs_module.owl",
				"kupkb/kupkb.owl", "obi.owl", "ChronicALLModule.owl",
				"tambis-full.owl", "galen.owl" };
		// long currentTimeMillis = System.currentTimeMillis();
		File file = new File(RESULTS_BASE + "allstats.csv");

		setupClusteringExperiment(base, input, file);
	}

	public static void setupClusteringExperiment(String baseDir, String[] input,
			File allResultsFile) throws FileNotFoundException,
			OWLOntologyCreationException, OPPLException,
			ParserConfigurationException {
		String current;
		for(String s : input){
			ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
			System.out
					.println("\n PopularityClusteringWithADEvaluationExperiment.main() \t " + s);
			String substring = s.substring(s.indexOf("/")+1);
			String filename = RESULTS_BASE + substring.replaceAll(".owl", ".csv");
			String xml = RESULTS_BASE + substring.replaceAll(".owl", ".xml");
			File f = new File(filename);
			String type;
			if (!f.exists()) {
				PrintStream singleOut = new PrintStream(f);
				current = baseDir + s;
				
				//load ontology and get general ontology metrics
				OWLOntologyManager m = OWLManager.createOWLOntologyManager();
				OWLOntology o = m.loadOntologyFromOntologyDocument(new File(
						current));
				ExperimentHelper.stripOntologyFromAnnotationAssertions(o);
				metrics.add(new SimpleMetric<String>("Ontology", s));
				metrics.addAll(getBasicOntologyMetrics(m));
				
				//popularity distance
				Distance<OWLEntity> distance = DistanceCreator
						.createAxiomRelevanceAxiomBasedDistance(m);
				run("popularity", metrics, singleOut, o, distance, null);
				
				//structural
				distance = DistanceCreator
						.createStructuralAxiomRelevanceAxiomBasedDistance(m);
				run("structural", metrics, singleOut, o, distance, null);
				
				//property relevance
				Set<OWLEntity> set = getSignatureWithoutProperties(o);
				distance = DistanceCreator
						.createOWLEntityRelevanceAxiomBasedDistance(m);
				run("object-property-relevance", metrics, singleOut, o, distance, set);
				
				printMetrics(metrics, allResultsFile);
				firstTime = false;
			}
		}
	}

	protected static void run(String distanceType, ArrayList<SimpleMetric<?>> metrics,
			PrintStream singleOut, OWLOntology o, Distance<OWLEntity> distance, Set<OWLEntity> clusteringSignature)
			throws OPPLException, ParserConfigurationException {
		System.out
				.println("ClusteringWithADEvaluationExperiment.main() \t " + distanceType);
		metrics.add(new SimpleMetric<String>("Clustering-type", distanceType));
		ClusterDecompositionModel<OWLEntity> model = ExperimentHelper
				.startSyntacticClustering(o, distance, clusteringSignature);
		metrics.addAll(getMetrics(singleOut, o, model));
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
		//final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		Set<OWLEntity> entities = new HashSet<OWLEntity>(); 
		//exclude all properties
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
