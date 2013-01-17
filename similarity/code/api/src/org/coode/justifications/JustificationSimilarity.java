/**
 * 
 */
package org.coode.justifications;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.structural.StructuralOWLObjectGeneralisation;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.MultiMap;

/**
 * @author eleni mikroyannidi
 * 
 *         Class for checking isomorphic justifications
 * 
 */
public class JustificationSimilarity {

	private final Set<ArrayList<OWLAxiom>> isomorphicJusts = new HashSet<ArrayList<OWLAxiom>>();
	private final OWLOntology ontology;
	private final OWLReasonerFactory rfactory;
	private final MultiMap<OWLAxiom, Explanation<OWLAxiom>> justificationMap = new MultiMap<OWLAxiom, Explanation<OWLAxiom>>();

	/**
	 * 
	 */
	public JustificationSimilarity(Set<OWLAxiom> entailments,
			OWLReasonerFactory rfactory, OWLOntology ontology) {
		this.ontology = ontology;
		this.rfactory = rfactory;
		computeJustifications(entailments, 1);
		computeJustificationSimilarity();
	}

	private void computeJustificationSimilarity() {
		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		OPPLFactory factory = new OPPLFactory(manager, ontology, null);
		ConstraintSystem constraintSystem = factory.createConstraintSystem();
		ArrayList<OWLAxiom> generalisedAxioms = new ArrayList<OWLAxiom>();

		for (OWLAxiom entailment : justificationMap.keySet()) {
			Collection<Explanation<OWLAxiom>> collection = justificationMap
					.get(entailment);
			for (Explanation<OWLAxiom> expl : collection) {
				Set<OWLAxiom> axioms = expl.getAxioms();
				for (OWLAxiom ax : axioms) {
					StructuralOWLObjectGeneralisation generalisation = new StructuralOWLObjectGeneralisation(
							new OntologyManagerBasedOWLEntityProvider(manager),
							constraintSystem);
					OWLAxiom generalised = (OWLAxiom) ax.accept(generalisation);
					generalisedAxioms.add(generalised);
				}
				isomorphicJusts.add(generalisedAxioms);
			}
		}
	}

	public double getJustificationSimilarity() {
		return (double) justificationMap.size() / isomorphicJusts.size();
	}

	private void computeJustifications(Set<OWLAxiom> entailments, int upperLimit) {
		// create the explanation factory
		ExplanationGeneratorFactory<OWLAxiom> expfactory = ExplanationManager
				.createExplanationGeneratorFactory(rfactory);
		ExplanationGenerator<OWLAxiom> gen = expfactory
				.createExplanationGenerator(ontology);
		for (OWLAxiom ax : entailments) {
			justificationMap.putAll(ax, gen.getExplanations(ax, upperLimit));
		}
	}

	public Collection<Explanation<OWLAxiom>> getJustification(
			OWLAxiom entailment) {
		return justificationMap.get(entailment);
	}

	public Set<ArrayList<OWLAxiom>> getIsomorphicJustifications() {
		return isomorphicJusts;
	}

	public MultiMap<OWLAxiom, Explanation<OWLAxiom>> getJustificationMap() {
		return justificationMap;
	}

}
