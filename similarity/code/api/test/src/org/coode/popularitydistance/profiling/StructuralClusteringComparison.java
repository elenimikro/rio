package org.coode.popularitydistance.profiling;

import java.io.File;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.aiontologygeneration.ClusteringUtils;
import org.coode.basetest.ClusterCreator;
import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class StructuralClusteringComparison {

	public StructuralClusteringComparison() {
		// TODO Auto-generated constructor stub
	}

	private final static String snomed_iri = "similarity/experiment-ontologies/ChronicALLModule.owl";

	/**
	 * @param args
	 * @throws TransformerFactoryConfigurationError
	 * @throws Exception
	 */
	public static void main(String[] args)
			throws TransformerFactoryConfigurationError, Exception {
		boolean correct = true;
		File ontology = new File(snomed_iri);
		Calendar c = Calendar.getInstance();
		String saveTo = "results/" + ontology.getName() + "_"
				+ c.get(Calendar.DAY_OF_MONTH) + "_" + c.get(Calendar.HOUR)
				+ ".xml";
		String compareTo = "similarity/profiling_data/results/chronic_module_structural_agglAll_results.xml";

		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o = m.loadOntologyFromOntologyDocument(ontology);
		Distance<OWLEntity> distance = DistanceCreator
				.createStructuralAxiomRelevanceAxiomBasedDistance(m);
		final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		Set<OWLEntity> entities = new TreeSet<OWLEntity>(
				new Comparator<OWLEntity>() {
					public int compare(final OWLEntity o1, final OWLEntity o2) {
						return shortFormProvider.getShortForm(o1).compareTo(
								shortFormProvider.getShortForm(o2));
					}
				});
		for (OWLOntology onto : m.getOntologies()) {
			entities.addAll(onto.getSignature());
		}
		ClusterCreator clusterer = new ClusterCreator();
		System.out
				.println("StructuralClusteringComparison.main() Starting clustering....");
		Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(o,
				distance, o.getSignature());
		ClusterDecompositionModel<OWLEntity> model = clusterer
				.buildClusterDecompositionModel(o, m, clusters);
		Utils.saveToXML(model, m, new File(saveTo));
		correct &= ClusteringUtils.check(o, saveTo, compareTo);
		System.out.println("correct? " + correct);
	}

}
