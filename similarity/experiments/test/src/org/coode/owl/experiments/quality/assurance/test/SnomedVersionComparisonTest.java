package org.coode.owl.experiments.quality.assurance.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.coode.basetest.ClusteringHelper;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import experiments.ExperimentUtils;

public class SnomedVersionComparisonTest {

	public SnomedVersionComparisonTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String snomed_2010 = "snomed/chronic_inferred_usage_2010.owl";
		String snomed_2013 = "snomed/chronic_inferred_usage_2013.owl";

		OWLOntology older_o = ExperimentUtils
				.loadOntology(new File(snomed_2010));
		OWLOntology new_o = ExperimentUtils.loadOntology(new File(snomed_2013));

		ClusterDecompositionModel<OWLEntity> older_model = ClusteringHelper
				.getSyntacticPopularityClusterModel(older_o);
		ClusterDecompositionModel<OWLEntity> new_model = ClusteringHelper
				.getSyntacticPopularityClusterModel(new_o);
	}

	@Test
	public void testChronicInferredModules() {
		String snomed_2010 = "snomed/chronic_inferred_usage_2010.owl";
		String snomed_2013 = "snomed/chronic_inferred_usage_2013.owl";

		OWLOntology older_o = ExperimentUtils
				.loadOntology(new File(snomed_2010));
		OWLOntology new_o = ExperimentUtils.loadOntology(new File(snomed_2013));

		assertEquals(older_o.getLogicalAxiomCount(),
				new_o.getLogicalAxiomCount());
	}
}
