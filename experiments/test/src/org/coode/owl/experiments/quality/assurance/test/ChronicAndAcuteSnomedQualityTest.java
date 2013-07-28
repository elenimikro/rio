package org.coode.owl.experiments.quality.assurance.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.basetest.ClusteringHelper;
import org.coode.owl.experiments.quality.assurance.LexicalAndAxiomaticPatternBasedQualityAssurance;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.junit.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import experiments.ExperimentHelper;
import experiments.ExperimentUtils;
import experiments.SimpleMetric;

public class ChronicAndAcuteSnomedQualityTest {

	private final String chronic_snomed_2013 = "snomed/chronic_inferred_usage_2013.owl";
	private final String acute_snomed_2013 = "snomed/acute_inferred_usage_2013.owl";

	// private String acute_snomed_2013 =
	// "snomed/acute_inferred_usage_2013.owl";

	// /**
	// * @param args
	// */
	// public static void main(String[] args) {
	// try {
	//
	// keyword = "acute";
	// System.out.println("Working with " + keyword);
	// o = ExperimentUtils.loadOntology(new File(acute_snomed_2013));
	// System.out.println("Computing clusters...");
	// model = ClusteringHelper.getSyntacticPopularityClusterModel(o);
	// xml = new File("snomed/acute_inferred_usage_2013_reg.xml");
	// Utils.saveToXML(model, xml);
	// System.out.println("Computing quality assurance stats...");
	// stats.addAll(getQualityAssuranceStats(model, keyword, o));
	// print(stats, new File("quality_assurance_stats.csv"));
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// }

	@Test
	public void testChronicTargetEntities() {
		String keyword = "chronic";
		OWLOntology o = ExperimentUtils.loadOntology(new File(
				chronic_snomed_2013));
		LexicalAndAxiomaticPatternBasedQualityAssurance qa = new LexicalAndAxiomaticPatternBasedQualityAssurance(
				keyword, o);
		ManchesterSyntaxRenderer renderer = ExperimentHelper
				.setManchesterSyntaxWithLabelRendering(o
						.getOWLOntologyManager());
		Set<OWLEntity> targetEntities = qa.getTargetEntities();
		for (OWLEntity e : targetEntities) {
			System.out.println(renderer.render(e));
		}
		System.out
				.println("Number of target entities " + targetEntities.size());
	}

	@Test
	public void testChronicModuleQuality() throws ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {
		String keyword = "chronic";
		System.out.println("Working with " + keyword);
		OWLOntology o = ExperimentUtils.loadOntology(new File(
				chronic_snomed_2013));
		LexicalAndAxiomaticPatternBasedQualityAssurance qa = new LexicalAndAxiomaticPatternBasedQualityAssurance(
				keyword, o);
		assertTrue(!o.getAxioms(AxiomType.ANNOTATION_ASSERTION).isEmpty());
		Set<OWLEntity> targetEntities = qa.getTargetEntities();
		assertFalse(targetEntities.isEmpty());
		Set<OWLAnnotationAssertionAxiom> annotations = ExperimentHelper
				.stripOntologyFromAnnotationAssertions(o);
		System.out.println("Computing clusters...");
		ClusterDecompositionModel<OWLEntity> model = ClusteringHelper
				.getSyntacticPopularityClusterModel(o);
		File xml = new File("snomed/chronic_inferred_usage_2013_reg.xml");
		Utils.saveToXML(model, xml);
		System.out.println("Regularity results were saved in " + xml);
		System.out.println("Computing quality assurance stats...");

		ArrayList<SimpleMetric<?>> stats = qa.getQualityAssuranceStats(model);
		for (SimpleMetric<?> m : stats) {
			System.out.println(m);
		}
	}

	@Test
	public void testAcuteModuleQuality() throws ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {
		String keyword = "acute";
		System.out.println("Working with " + keyword);
		OWLOntology o = ExperimentUtils
				.loadOntology(new File(acute_snomed_2013));
		LexicalAndAxiomaticPatternBasedQualityAssurance qa = new LexicalAndAxiomaticPatternBasedQualityAssurance(
				keyword, o);
		assertTrue(!o.getAxioms(AxiomType.ANNOTATION_ASSERTION).isEmpty());
		Set<OWLEntity> targetEntities = qa.getTargetEntities();
		assertFalse(targetEntities.isEmpty());
		ExperimentHelper.stripOntologyFromAnnotationAssertions(o);
		System.out.println("Computing clusters...");
		ClusterDecompositionModel<OWLEntity> model = ClusteringHelper
				.getSyntacticPopularityClusterModel(o);
		File xml = new File("snomed/acute_inferred_usage_2013_reg.xml");
		Utils.saveToXML(model, xml);
		System.out.println("Regularity results were saved in " + xml);
		System.out.println("Computing quality assurance stats...");

		ArrayList<SimpleMetric<?>> stats = qa.getQualityAssuranceStats(model);
		for (SimpleMetric<?> m : stats) {
			System.out.println(m);
		}
	}

}
