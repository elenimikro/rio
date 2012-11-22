package experiments.scripts;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillersImpl;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecomposition;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasoner;
import uk.ac.manchester.cs.factplusplus.owlapiv3.OWLKnowledgeExplorationReasonerWrapper;
import experiments.ClusteringWithADEvaluationExperimentBase;
import experiments.ExperimentHelper;
import experiments.SimpleMetric;

public class SemanticClustering extends
		ClusteringWithADEvaluationExperimentBase {

	private final ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();

	public void runPopularitySemanticClustering(OWLOntology o, String filename)
			throws FileNotFoundException, OWLOntologyCreationException,
			OPPLException, ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {

		// get KE metrics
		KnowledgeExplorer ke = runFactplusplusKnowledgeExplorerReasoner(o);
		Set<OWLAxiom> entailments = ke.getAxioms();
		System.out
				.println("SemanticClustering.runPopularitySemanticClustering() Entailment size: "
						+ entailments.size());
		metrics.add(new SimpleMetric<Integer>("#Entailments", entailments
				.size()));

		String clustering_type = "popularity";
		Distance<OWLEntity> distance = DistanceCreator
				.createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(
						o.getOWLOntologyManager(), ke);
		PrintStream out = new PrintStream(filename);
		ClusterDecompositionModel<OWLEntity> model = run(clustering_type, out,
				o, distance, ke.getEntities(), entailments);
        IOExperimentUtils.saveResults(filename, clustering_type, model);
        out.close();
	}

	public ClusterDecompositionModel<OWLEntity> run(String distanceType,
			PrintStream singleOut, OWLOntology o, Distance<OWLEntity> distance,
			Set<OWLEntity> clusteringSignature, Set<OWLAxiom> entailments)
			throws OPPLException, ParserConfigurationException,
			OWLOntologyCreationException {
		metrics.add(new SimpleMetric<String>("Clustering-type", distanceType));
		ClusterDecompositionModel<OWLEntity> model = ExperimentHelper
				.startSemanticClustering(o, entailments, distance,
						clusteringSignature);
		OWLOntology inferedOnto = OWLManager.createOWLOntologyManager()
				.createOntology(entailments);
		metrics.addAll(getMetrics(singleOut, inferedOnto, model));
		return model;
	}

	private Collection<? extends SimpleMetric<?>> getMetrics(
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

	public KnowledgeExplorer runFactplusplusKnowledgeExplorerReasoner(
			OWLOntology o) {
		OWLReasoner reasoner = new FaCTPlusPlusReasoner(o,
				new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		KnowledgeExplorer ke = new KnowledgeExplorerMaxFillersImpl(reasoner,
				new OWLKnowledgeExplorationReasonerWrapper(
						new FaCTPlusPlusReasoner(o, new SimpleConfiguration(),
								BufferingMode.NON_BUFFERING)));
		return ke;
	}

}
