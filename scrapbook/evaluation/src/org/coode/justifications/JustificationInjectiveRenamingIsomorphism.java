/**
 * 
 */
package org.coode.justifications;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
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
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni mikroyannidi Class for checking isomorphic justifications
 *         between entailments that are abstracted by the same generalisation. */
public class JustificationInjectiveRenamingIsomorphism {
    private final OWLOntology ontology;
    private final OWLReasonerFactory rfactory;
    private final MultiMap<OWLAxiom, Explanation<OWLAxiom>> justificationMap = new MultiMap<OWLAxiom, Explanation<OWLAxiom>>();
    private final MultiMap<Set<OWLAxiom>, OWLAxiom> isomorphicJustifications = new MultiMap<Set<OWLAxiom>, OWLAxiom>();
    private final Set<OWLAxiom> entailments = new HashSet<OWLAxiom>();

    /**
	 * 
	 */
    public JustificationInjectiveRenamingIsomorphism(Set<OWLAxiom> entailments,
            OWLReasonerFactory rfactory, OWLOntology ontology) {
        this.ontology = ontology;
        this.rfactory = rfactory;
        this.entailments.addAll(entailments);
    }

    private void computeIsomorphicJustifications() {
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OPPLFactory factory = new OPPLFactory(manager, ontology, null);
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        for (OWLAxiom entailment : justificationMap.keySet()) {
            Collection<Explanation<OWLAxiom>> collection = justificationMap
                    .get(entailment);
            for (Explanation<OWLAxiom> expl : collection) {
                Set<OWLAxiom> generalisedAxioms = new LinkedHashSet<OWLAxiom>();
                Set<OWLAxiom> axioms = expl.getAxioms();
                StructuralOWLObjectGeneralisation generalisation = new StructuralOWLObjectGeneralisation(
                        new OntologyManagerBasedOWLEntityProvider(manager),
                        constraintSystem);
                for (OWLAxiom ax : axioms) {
                    OWLAxiom generalised = (OWLAxiom) ax.accept(generalisation);
                    generalisedAxioms.add(generalised);
                }
                isomorphicJustifications.put(generalisedAxioms, entailment);
            }
        }
    }

    private void _computeIsomorphicJustifications() {
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OPPLFactory factory = new OPPLFactory(manager, ontology, null);
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        for (OWLAxiom entailment : justificationMap.keySet()) {
            Map<OWLObject, OWLObject> cache = new HashMap<OWLObject, OWLObject>();
            Collection<Explanation<OWLAxiom>> collection = justificationMap
                    .get(entailment);
            for (Explanation<OWLAxiom> expl : collection) {
                Set<OWLAxiom> generalisedAxioms = new LinkedHashSet<OWLAxiom>();
                Set<OWLAxiom> axioms = expl.getAxioms();
                Set<OWLEntity> signature = new HashSet<OWLEntity>();
                for (OWLAxiom ax : axioms) {
                    signature.addAll(ax.getSignature());
                }
                // Assign new variable to the explanation signature
                for (OWLEntity e : signature) {
                    StructuralOWLObjectGeneralisation generalisation = new StructuralOWLObjectGeneralisation(
                            new OntologyManagerBasedOWLEntityProvider(manager),
                            constraintSystem);
                    OWLEntity generalised = (OWLEntity) e.accept(generalisation);
                    cache.put(e, generalised);
                }
                isomorphicJustifications.put(generalisedAxioms, entailment);
            }
        }
    }

    /** Implements Jaccard's index and returns the justification similarity
     * 
     * @return */
    public double getJustificationSimilarity() {
        computeJustifications(entailments, 1);
        computeIsomorphicJustifications();
        if (isomorphicJustifications.keySet().equals(justificationMap.size())) {
            return 0;
        } else {
            double intersection = 0;
            double union = isomorphicJustifications.size();
            for (Set<OWLAxiom> just : isomorphicJustifications.keySet()) {
                // if a generalised justification covers more than one
                // entailment then I should
                // increment the intersection
                if (isomorphicJustifications.get(just).size() > 1) {
                    intersection += isomorphicJustifications.get(just).size();
                }
            }
            return intersection / union;
        }
    }

    private void computeJustifications(Set<OWLAxiom> entailedAxioms, int upperLimit) {
        // create the explanation factory
        ExplanationGeneratorFactory<OWLAxiom> expfactory = ExplanationManager
                .createExplanationGeneratorFactory(rfactory);
        ExplanationGenerator<OWLAxiom> gen = expfactory
                .createExplanationGenerator(ontology);
        for (OWLAxiom ax : entailedAxioms) {
            justificationMap.putAll(ax, gen.getExplanations(ax, upperLimit));
        }
    }

    public Collection<Explanation<OWLAxiom>> getJustification(OWLAxiom entailment) {
        if (justificationMap.size() == 0) {
            computeJustifications(entailments, 1);
        }
        return justificationMap.get(entailment);
    }

    public Set<Set<OWLAxiom>> getIsomorphicJustifications() {
        if (isomorphicJustifications.size() == 0) {
            computeIsomorphicJustifications();
        }
        return isomorphicJustifications.keySet();
    }

    public MultiMap<OWLAxiom, Explanation<OWLAxiom>> getJustificationMap() {
        if (justificationMap.size() == 0) {
            computeJustifications(entailments, 1);
        }
        return justificationMap;
    }
}
