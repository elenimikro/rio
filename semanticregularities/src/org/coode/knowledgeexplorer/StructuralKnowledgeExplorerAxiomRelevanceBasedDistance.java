/*******************************************************************************
 * Copyright (c) 2012 Eleni Mikroyannidi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     Eleni Mikroyannidi, Luigi Iannone - initial API and implementation
 ******************************************************************************/
/**
 *
 */
package org.coode.knowledgeexplorer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.coode.distance.entityrelevance.CollectionBasedRelevantPolicy;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.entityrelevance.owl.AxiomGeneralityDetector;
import org.coode.distance.owl.AbstractAxiomBasedDistanceImpl;
import org.coode.distance.owl.AxiomRelevanceMapBase;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.AxiomGeneralisationTreeNode;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.structural.RelevancePolicyOWLObjectGeneralisation;
import org.coode.owl.generalise.structural.SingleOWLEntityReplacementVariableProvider;
import org.coode.owl.generalise.structural.StructuralOWLObjectGeneralisation;
import org.coode.owl.wrappers.OWLEntityProvider;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.MultiMap;

/** @author Luigi Iannone */
public class StructuralKnowledgeExplorerAxiomRelevanceBasedDistance extends
        AbstractAxiomBasedDistanceImpl {
    protected class AxiomRelevanceMap extends AxiomRelevanceMapBase {
        public AxiomRelevanceMap(Collection<? extends OWLAxiom> axioms,
                OWLEntityProvider entityProvider, ConstraintSystem constraintSystem) {
            if (axioms == null) {
                throw new NullPointerException("The axiom collection cannot be null");
            }
            for (OWLAxiom axiom : axioms) {
                StructuralOWLObjectGeneralisation generalisation = new StructuralOWLObjectGeneralisation(
                        entityProvider, constraintSystem);
                OWLAxiom generalisedAxiom = (OWLAxiom) axiom.accept(generalisation);
                generalisationMap.put(axiom, generalisedAxiom);
                instantionMap.put(generalisedAxiom, new OWLAxiomInstantiation(axiom,
                        generalisation.getSubstitutions()));
            }
            Set<OWLAxiom> generalisedAxioms = instantionMap.keySet();
            for (OWLAxiom generalisedAxiom : generalisedAxioms) {
                AxiomGeneralisationTreeNode generalisationTreeNode = new AxiomGeneralisationTreeNode(
                        generalisedAxiom, instantionMap.get(generalisedAxiom),
                        constraintSystem);
                relevanceMap.setEntry(generalisedAxiom,
                        extractValues(generalisationTreeNode));
            }
        }
    }

    private final KnowledgeExplorer ke;
    private final MultiMap<OWLEntity, OWLAxiom> cache = new MultiMap<OWLEntity, OWLAxiom>();
    private final OWLOntologyManager ontologyManger;
    private final OWLEntityProvider entityProvider;
    private final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<OWLEntity, OWLAxiom>();
    private final Set<OWLEntity> ontologySignature = new HashSet<OWLEntity>();
    private final AxiomRelevanceMap axiomRelevanceMap;
    private final OPPLFactory opplfactory;

    private void buildAxiomEntityMap(Set<OWLAxiom> set) {
        for (OWLAxiom ax : set) {
            if (!ax.isOfType(AxiomType.DECLARATION)) {
                for (OWLEntity e : ax.getSignature()) {
                    candidates.put(e, ax);
                }
            }
        }
    }

    private void buildSignature() {
        ontologySignature.clear();
        ontologySignature.addAll(ke.getEntities());
    }

    private final Map<OWLAxiom, RelevancePolicyOWLObjectGeneralisation> replacers = new HashMap<OWLAxiom, RelevancePolicyOWLObjectGeneralisation>();

    /** @param ontology
     *            ontology
     * @param knowledgeExplorer
     *            knowledgeExplorer */
    public StructuralKnowledgeExplorerAxiomRelevanceBasedDistance(OWLOntology ontology,
            KnowledgeExplorer knowledgeExplorer) {
        if (ontology == null) {
            throw new NullPointerException("The ontolgy canont be null");
        }
        if (knowledgeExplorer == null) {
            throw new NullPointerException("The knowledge explorer cannot be null");
        }
        ke = knowledgeExplorer;
        ontologyManger = ontology.getOWLOntologyManager();
        buildSignature();
        buildAxiomEntityMap(ke.getAxioms());
        axiomRelevanceMap = buildAxiomRelevanceMap();
        entityProvider = new OntologyManagerBasedOWLEntityProvider(getOntologyManger());
        opplfactory = new OPPLFactory(getOntologyManger(), ontology, null);
    }

    protected AxiomRelevanceMap buildAxiomRelevanceMap() {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLAxiom axiom : ke.getAxioms()) {
            if (!axiom.getAxiomType().equals(AxiomType.DECLARATION)) {
                axioms.add(axiom);
            }
        }
        OPPLFactory factory = new OPPLFactory(getOntologyManger(), getOntologyManger()
                .getOntologies().iterator().next(), null);
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        return new AxiomRelevanceMap(axioms, new OntologyManagerBasedOWLEntityProvider(
                getOntologyManger()), constraintSystem);
    }

    @Override
    public Set<OWLAxiom> getAxioms(OWLEntity owlEntity) {
        Collection<OWLAxiom> cached = cache.get(owlEntity);
        return cached.isEmpty() ? computeAxiomsForEntity(owlEntity) : CollectionFactory
                .getCopyOnRequestSetFromImmutableCollection(cached);
    }

    /** @param owlEntity
     *            owlEntity
     * @return axioms */
    protected Set<OWLAxiom> computeAxiomsForEntity(OWLEntity owlEntity) {
        for (OWLAxiom axiom : candidates.get(owlEntity)) {
            RelevancePolicy<OWLEntity> policy = CollectionBasedRelevantPolicy
                    .allOf(getRelevantEntities(axiom));
            RelevancePolicyOWLObjectGeneralisation generalReplacer = replacers.get(axiom);
            if (generalReplacer == null) {
                generalReplacer = new RelevancePolicyOWLObjectGeneralisation(policy,
                        getEntityProvider());
                replacers.put(axiom, generalReplacer);
            }
            ((SingleOWLEntityReplacementVariableProvider) generalReplacer
                    .getVariableProvider()).setOWLObject(owlEntity);
            ConstraintSystem cs = opplfactory.createConstraintSystem();
            generalReplacer.getVariableProvider().setConstraintSystem(cs);
            generalReplacer.setConstraintSystem(cs);
            OWLAxiom replaced = (OWLAxiom) axiom.accept(generalReplacer);
            if (isRelevant(replaced)) {
                cache.put(owlEntity, replaced);
            }
        }
        return CollectionFactory.getCopyOnRequestSetFromImmutableCollection(cache
                .get(owlEntity));
    }

    private Collection<OWLEntity> getRelevantEntities(OWLAxiom axiom) {
        return axiomRelevanceMap.getRelevantEntities(axiom);
    }

    protected boolean isRelevant(OWLAxiom replaced) {
        Set<OWLEntity> signature = replaced.getSignature();
        boolean found = false;
        Iterator<OWLEntity> iterator = signature.iterator();
        while (!found && iterator.hasNext()) {
            OWLEntity owlEntity = iterator.next();
            found = ontologySignature.contains(owlEntity);
        }
        if (!found) {
            found = replaced.accept(AxiomGeneralityDetector.getInstance());
        }
        return found;
    }

    /** @return the ontologyManger */
    public OWLOntologyManager getOntologyManger() {
        return ontologyManger;
    }

    /** @return the entityProvider */
    public OWLEntityProvider getEntityProvider() {
        return entityProvider;
    }
}
