package org.coode.basetest;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.distance.Distance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillersImpl;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasoner;
import uk.ac.manchester.cs.factplusplus.owlapiv3.OWLKnowledgeExplorationReasonerWrapper;

public class ClusteringHelper {

	public static ClusterDecompositionModel<OWLEntity> getPopularityClusterModel(
			OWLOntology o) {
		try {
			OWLOntologyManager m = o.getOWLOntologyManager();
			AxiomRelevanceAxiomBasedDistance distance = (AxiomRelevanceAxiomBasedDistance) DistanceCreator
					.createAxiomRelevanceAxiomBasedDistance(m);
			ClusterCreator clusterer = new ClusterCreator();

			final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
			Set<OWLEntity> entities = new TreeSet<OWLEntity>(
					new Comparator<OWLEntity>() {
						@Override
						public int compare(final OWLEntity o1,
								final OWLEntity o2) {
							return shortFormProvider.getShortForm(o1)
									.compareTo(
											shortFormProvider.getShortForm(o2));
						}
					});
			for (OWLOntology ontology : m.getOntologies()) {
				entities.addAll(ontology.getSignature());
			}
			Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(o,
					distance, entities);
			ClusterDecompositionModel<OWLEntity> model = clusterer
					.buildClusterDecompositionModel(o, m, clusters);

			List<Cluster<OWLEntity>> clusterList = model.getClusterList();
			MultiMap<OWLAxiom, OWLAxiomInstantiation> multiMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
			for (int counter = 0; counter < clusterList.size(); counter++) {
				multiMap.putAll(model.get(clusterList.get(counter)));
			}
			for (OWLAxiom ax : multiMap.keySet()) {
				System.out.println("Generalisation: " + ax);
				System.out.println("Instantiations:");
				for (OWLAxiomInstantiation inst : multiMap.get(ax)) {
					System.out.println("\t" + inst);
				}
			}
			return model;
		} catch (OPPLException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static ClusterDecompositionModel<OWLEntity> getSemanticPopularityClusterModel(
			OWLOntology o) throws Exception {

		OWLOntologyManager m = o.getOWLOntologyManager();
		KnowledgeExplorer ke = runFactplusplusKnowledgeExplorerReasoner(o);
		Distance<OWLEntity> distance = DistanceCreator
				.createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(o, ke);
		ClusterCreator clusterer = new ClusterCreator();

		final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		Set<OWLEntity> entities = new TreeSet<OWLEntity>(
				new Comparator<OWLEntity>() {
					@Override
					public int compare(final OWLEntity o1, final OWLEntity o2) {
						return shortFormProvider.getShortForm(o1).compareTo(
								shortFormProvider.getShortForm(o2));
					}
				});

		entities.addAll(ke.getEntities());
		Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(o,
				distance, entities);
		ClusterDecompositionModel<OWLEntity> model = clusterer
				.buildKnowledgeExplorerClusterDecompositionModel(o,
						ke.getAxioms(), m, clusters);

		List<Cluster<OWLEntity>> clusterList = model.getClusterList();
		MultiMap<OWLAxiom, OWLAxiomInstantiation> multiMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
		for (int counter = 0; counter < clusterList.size(); counter++) {
			multiMap.putAll(model.get(clusterList.get(counter)));
		}
		for (OWLAxiom ax : multiMap.keySet()) {
			System.out.println("Generalisation: " + ax);
			System.out.println("Instantiations:");
			for (OWLAxiomInstantiation inst : multiMap.get(ax)) {
				System.out.println("\t" + inst);
			}
		}
		return model;
	}

	private static KnowledgeExplorer runFactplusplusKnowledgeExplorerReasoner(
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
