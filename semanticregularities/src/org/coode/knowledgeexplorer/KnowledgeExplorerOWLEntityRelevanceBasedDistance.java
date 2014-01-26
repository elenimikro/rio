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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.distance.entityrelevance.DefaultOWLEntityTypeRelevancePolicy;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.entityrelevance.owl.AxiomGeneralityDetector;
import org.coode.distance.entityrelevance.owl.Utils;
import org.coode.distance.owl.AbstractAxiomBasedDistanceImpl;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.structural.RelevancePolicyOWLObjectGeneralisation;
import org.coode.owl.generalise.structural.SingleOWLEntityReplacementVariableProvider;
import org.coode.owl.wrappers.OWLEntityProvider;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.MultiMap;

/** @author Eleni Mikroyannidi */
public class KnowledgeExplorerOWLEntityRelevanceBasedDistance extends
        AbstractAxiomBasedDistanceImpl {
    private final KnowledgeExplorer ke;
    private final MultiMap<OWLEntity, OWLAxiom> cache = new MultiMap<OWLEntity, OWLAxiom>();
    private final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<OWLEntity, OWLAxiom>();
    private final Set<OWLEntity> keSignature = new HashSet<OWLEntity>();
    private final OWLEntityProvider entityProvider;
    private final OPPLFactory factory;
    private final Map<OWLAxiom, RelevancePolicyOWLObjectGeneralisation> replacers = new HashMap<OWLAxiom, RelevancePolicyOWLObjectGeneralisation>();
    private final RelevancePolicy<OWLEntity> policy;
    private final static List<AxiomType<?>> types = new ArrayList<AxiomType<?>>(
            AxiomType.AXIOM_TYPES);
    static {
        types.remove(AxiomType.DECLARATION);
    }

    private void buildAxiomEntityMap() {
        Set<OWLAxiom> axioms = ke.getAxioms();
        for (OWLAxiom ax : axioms) {
            if (!ax.isOfType(AxiomType.DECLARATION)) {
                for (OWLEntity e : ax.getSignature()) {
                    candidates.put(e, ax);
                }
            }
        }
    }

    private void buildSignature() {
        keSignature.clear();
        keSignature.addAll(ke.getEntities());
    }

    /** @param ontology
     *            ontology
     * @param explorer
     *            explorer */
    public KnowledgeExplorerOWLEntityRelevanceBasedDistance(OWLOntology ontology,
            KnowledgeExplorer explorer) {
        ke = explorer;
        buildSignature();
        buildAxiomEntityMap();
        entityProvider = new OntologyManagerBasedOWLEntityProvider(
                ontology.getOWLOntologyManager());
        factory = new OPPLFactory(ontology.getOWLOntologyManager(), ontology, null);
        policy = DefaultOWLEntityTypeRelevancePolicy
                .getObjectPropertyAlwaysRelevantPolicy();
    }

    @Override
    public Set<OWLAxiom> getAxioms(final OWLEntity owlEntity) {
        Collection<OWLAxiom> cached = cache.get(owlEntity);
        return cached.isEmpty() ? computeAxiomsForEntity(owlEntity) : CollectionFactory
                .getCopyOnRequestSetFromImmutableCollection(cached);
    }

    /** @param owlEntity
     *            owlEntity
     * @return axioms */
    protected Set<OWLAxiom> computeAxiomsForEntity(final OWLEntity owlEntity) {
        for (OWLAxiom axiom : candidates.get(owlEntity)) {
            RelevancePolicyOWLObjectGeneralisation generalReplacer = replacers.get(axiom);
            if (generalReplacer == null) {
                generalReplacer = new RelevancePolicyOWLObjectGeneralisation(
                        Utils.toOWLObjectRelevancePolicy(policy), getEntityProvider());
                replacers.put(axiom, generalReplacer);
            }
            ((SingleOWLEntityReplacementVariableProvider) generalReplacer
                    .getVariableProvider()).setOWLObject(owlEntity);
            final ConstraintSystem cs = factory.createConstraintSystem();
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

    protected boolean isRelevant(final OWLAxiom replaced) {
        Set<OWLEntity> signature = replaced.getSignature();
        boolean found = false;
        Iterator<OWLEntity> iterator = signature.iterator();
        while (!found && iterator.hasNext()) {
            OWLEntity owlEntity = iterator.next();
            found = keSignature.contains(owlEntity);
        }
        if (!found) {
            found = replaced.accept(AxiomGeneralityDetector.getInstance());
        }
        return found;
    }

    /** @return the entityProvider */
    public OWLEntityProvider getEntityProvider() {
        return entityProvider;
    }
}
