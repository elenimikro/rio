package org.coode.owl.experiments.quality.assurance;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import experiments.ExperimentHelper;
import experiments.ExperimentUtils;
/** @author eleni */
public class LexicalUsageExtractor {

	private static String RESULTS_BASE = "";// "/Volumes/Passport-mac/Expeiments/";

	// private static final String snomed_iri =
	// "snomed/2010/acute_moduleinferred_ke.owl";

	// private static final String[] keywords = { "absent", "present", "left",
	// "right", "chronic", "acute" };

	// private static final String keyword = "acute";

	// "snomed/full_snomed_inferred_16_04_2013.owl";

	/**
	 * @param args
	 *            args[0]:ontology, args[1]: keyword
	 */
	public static void main(String[] args) {
		if (args.length >= 1) {
			String ontologyName = args[0];
			System.out.println("Loading the ontology...");
			OWLOntology onto = ExperimentUtils.loadOntology(new File(
					ontologyName));
			List<String> keywords = ExperimentUtils
					.extractListFromFile("fma_processedKeywords.txt");
			for (String keyword : keywords) {
				System.out.println(String.format(
						"Extracting usage for %s keyword", keyword));
				String usage_onto_name = ontologyName.substring(0,
						ontologyName.lastIndexOf('/') + 1)
						+ keyword.replaceAll(" ", "_") + "_usage.owl";
				File ontoFile = new File(RESULTS_BASE + usage_onto_name);
				if (!ontoFile.exists()) {
					Set<OWLAxiom> axioms = extractKeywordUsage(keyword, onto);

					ExperimentUtils.saveOntology(ontoFile, axioms);
					System.out.println("Ontology was saved in "
							+ ontoFile.getPath());
				}
			}
		} else {
			System.out.println(String.format(
					"Usage java -cp ... %s <ontology.owl>",
					LexicalUsageExtractor.class.getCanonicalName()));
		}
	}

	private static Set<OWLAxiom> extractKeywordUsage(String keyword,
			OWLOntology onto) {
		ManchesterSyntaxRenderer renderer = ExperimentHelper
				.setManchesterSyntaxWithLabelRendering(onto
						.getOWLOntologyManager());
		Set<OWLClass> target = new HashSet<OWLClass>();
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		System.out.println("Searching for " + keyword + " entities...");
		Set<OWLClass> classesInSignature = onto.getClassesInSignature();
		for (OWLClass c : classesInSignature) {
			if (renderer.render(c).toLowerCase().indexOf(keyword) != -1) {
				target.add(c);
				axioms.addAll(onto.getReferencingAxioms(c));
				axioms.addAll(onto.getAxioms(AxiomType.ANNOTATION_ASSERTION));
			}
		}
		System.out.println("Found " + target.size() + " " + keyword
				+ " entities " + "and " + " " + axioms.size() + " axioms.");
		return axioms;
	}
}
