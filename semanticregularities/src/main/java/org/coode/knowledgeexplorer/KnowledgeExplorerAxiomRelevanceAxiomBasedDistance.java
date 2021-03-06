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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.coode.distance.entityrelevance.owl.AxiomGeneralityDetector;
import org.coode.distance.entityrelevance.owl.AxiomMap;
import org.coode.distance.entityrelevance.owl.AxiomRelevancePolicy;
import org.coode.distance.entityrelevance.owl.Utils;
import org.coode.distance.owl.AbstractAxiomBasedDistanceImpl;
import org.coode.distance.owl.OWLEntityReplacer;
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
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.WeakIndexCache;

/** @author Eleni Mikroyannidi */
public class KnowledgeExplorerAxiomRelevanceAxiomBasedDistance extends
        AbstractAxiomBasedDistanceImpl {
    static class EntityMap extends MultiMap<OWLEntity, OWLAxiom> {
        public EntityMap() {
            super(false);
        }
    }

    private final KnowledgeExplorer ke;
    private final EntityMap cache = new EntityMap();
    private final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<OWLEntity, OWLAxiom>(
            false, false);
    private final AxiomMap axiomMap;
    private final Set<OWLEntity> keSignature = new HashSet<OWLEntity>();
    private final OWLEntityProvider entityProvider;
    private final OWLEntityReplacer replacer;
    private final OPPLFactory factory;
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

    void buildSignature() {
        keSignature.clear();
        keSignature.addAll(ke.getEntities());
    }

    private final WeakIndexCache<OWLAxiom, RelevancePolicyOWLObjectGeneralisation> replacers = new WeakIndexCache<OWLAxiom, RelevancePolicyOWLObjectGeneralisation>();

    /** @param ontology
     *            ontology
     * @param replacer
     *            replacer
     * @param explorer
     *            explorer */
    public KnowledgeExplorerAxiomRelevanceAxiomBasedDistance(OWLOntology ontology,
            OWLEntityReplacer replacer, KnowledgeExplorer explorer) {
        if (replacer == null) {
            throw new NullPointerException("The replacer cannot be null");
        }
        ke = explorer;
        this.replacer = replacer;
        axiomMap = new AxiomMap(explorer.getAxioms(), replacer);
        buildSignature();
        buildAxiomEntityMap();
        entityProvider = new OntologyManagerBasedOWLEntityProvider(
                ontology.getOWLOntologyManager());
        factory = new OPPLFactory(ontology.getOWLOntologyManager(), ontology, null);
    }

    @Override
    public Set<OWLAxiom> getAxioms(OWLEntity owlEntity) {
        Set<OWLAxiom> cached = (Set<OWLAxiom>) cache.get(owlEntity);
        if (cached.isEmpty()) {
            cached = computeAxiomsForEntity(owlEntity);
        }
        return cached;
    }

    /** @param owlEntity
     *            owlEntity
     * @return axioms */
    protected Set<OWLAxiom> computeAxiomsForEntity(OWLEntity owlEntity) {
        for (OWLAxiom axiom : candidates.get(owlEntity)) {
            // RelevancePolicyOWLObjectGeneralisation generalReplacer = new
            // RelevancePolicyOWLObjectGeneralisation(
            // Utils.toOWLObjectRelevancePolicy(new AxiomRelevancePolicy(
            // (OWLAxiom) axiom.accept(replacer), axiomMap)),
            // getEntityProvider(), factory.createConstraintSystem());
            RelevancePolicyOWLObjectGeneralisation generalReplacer = replacers.get(axiom);
            if (generalReplacer == null) {
                generalReplacer = replacers.cache(
                        axiom,
                        new RelevancePolicyOWLObjectGeneralisation(Utils
                                .toOWLObjectRelevancePolicy(new AxiomRelevancePolicy(
                                        (OWLAxiom) axiom.accept(replacer), axiomMap)),
                                getEntityProvider()));
            }
            ((SingleOWLEntityReplacementVariableProvider) generalReplacer
                    .getVariableProvider()).setOWLObject(owlEntity);
            ConstraintSystem cs = factory.createConstraintSystem();
            generalReplacer.getVariableProvider().setConstraintSystem(cs);
            generalReplacer.setConstraintSystem(cs);
            OWLAxiom replaced = (OWLAxiom) axiom.accept(generalReplacer);
            if (isRelevant(replaced)) {
                cache.put(owlEntity, replaced);
            }
        }
        Set<OWLAxiom> collection = new HashSet<OWLAxiom>(cache.get(owlEntity));
        cache.putAll(owlEntity, collection);
        return collection;
    }

    protected boolean isRelevant(OWLAxiom replaced) {
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

    /** dispose */
    public void dispose() {
        // ontologyManger.removeOntologyChangeListener(listener);
    }

    /** @return the entityProvider */
    public OWLEntityProvider getEntityProvider() {
        return entityProvider;
    }
}
