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
package org.coode.distance.owl;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.add;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.distance.entityrelevance.owl.AxiomGeneralityDetector;
import org.coode.distance.entityrelevance.owl.AxiomMap;
import org.coode.distance.entityrelevance.owl.AxiomRelevancePolicy;
import org.coode.distance.entityrelevance.owl.Utils;
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
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.MultiMap;

/** @author Luigi Iannone */
public class AxiomRelevanceAxiomBasedDistance extends AbstractAxiomBasedDistanceImpl {
    protected final Set<OWLOntology> ontologies = new HashSet<>();
    private final MultiMap<OWLEntity, OWLAxiom> cache = new MultiMap<>();
    private final OWLOntologyManager ontologyManger;
    private final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<>();
    private final AxiomMap axiomMap;
    private final Set<OWLEntity> ontologySignature = new HashSet<>();
    private final OWLEntityProvider entityProvider;
    private final OWLEntityReplacer replacer;
    private final OPPLFactory factory;
    private final OWLOntologyChangeListener listener = changes -> {
        buildOntologySignature();
        buildAxiomEntityMap(ontologies);
    };
    private final static List<AxiomType<?>> types = new ArrayList<>(AxiomType.LOGICAL_AXIOM_TYPES);

    protected void buildAxiomEntityMap(Collection<? extends OWLOntology> ontos) {
        ontos.stream().flatMap(OWLOntology::axioms).filter(ax -> types.contains(ax.getAxiomType()))
            .forEach(ax -> ax.signature().forEach(e -> candidates.put(e, ax)));
    }

    protected void buildOntologySignature() {
        ontologySignature.clear();
        add(ontologySignature, ontologies.stream().flatMap(OWLOntology::signature));
    }

    private final Map<OWLAxiom, RelevancePolicyOWLObjectGeneralisation> replacers = new HashMap<>();

    /**
     * @param ontologies ontologies
     * @param replacer replacer
     * @param manager manager
     */
    public AxiomRelevanceAxiomBasedDistance(Collection<? extends OWLOntology> ontologies,
        OWLEntityReplacer replacer, OWLOntologyManager manager) {
        if (ontologies == null) {
            throw new NullPointerException("The ontolgies canont be null");
        }
        if (manager == null) {
            throw new NullPointerException("The ontolgy manager cannot be null");
        }
        if (replacer == null) {
            throw new NullPointerException("The replacer cannot be null");
        }
        this.replacer = replacer;
        axiomMap = new AxiomMap(ontologies, manager, replacer);
        this.ontologies.addAll(ontologies);
        ontologyManger = manager;
        buildOntologySignature();
        buildAxiomEntityMap(ontologies);
        entityProvider = new OntologyManagerBasedOWLEntityProvider(getOntologyManger());
        factory = new OPPLFactory(getOntologyManger(), this.ontologies.iterator().next(), null);
    }

    @Override
    public Set<OWLAxiom> getAxioms(OWLEntity owlEntity) {
        Collection<OWLAxiom> cached = cache.get(owlEntity);
        return cached.isEmpty() ? computeAxiomsForEntity(owlEntity)
            : CollectionFactory.getCopyOnRequestSetFromImmutableCollection(cached);
    }

    /**
     * @param owlEntity owlEntity
     * @return axioms
     */
    protected Set<OWLAxiom> computeAxiomsForEntity(OWLEntity owlEntity) {
        for (OWLAxiom axiom : candidates.get(owlEntity)) {
            // RelevancePolicyOWLObjectGeneralisation generalReplacer = new
            // RelevancePolicyOWLObjectGeneralisation(
            // Utils.toOWLObjectRelevancePolicy(new AxiomRelevancePolicy(
            // (OWLAxiom) axiom.accept(replacer), axiomMap)),
            // getEntityProvider(), factory.createConstraintSystem());
            RelevancePolicyOWLObjectGeneralisation generalReplacer = replacers.get(axiom);
            if (generalReplacer == null) {
                generalReplacer = new RelevancePolicyOWLObjectGeneralisation(
                    Utils.toOWLObjectRelevancePolicy(
                        new AxiomRelevancePolicy((OWLAxiom) axiom.accept(replacer), axiomMap)),
                    getEntityProvider());
                replacers.put(axiom, generalReplacer);
            }
            ((SingleOWLEntityReplacementVariableProvider) generalReplacer.getVariableProvider())
                .setOWLObject(owlEntity);
            ConstraintSystem cs = factory.createConstraintSystem();
            generalReplacer.getVariableProvider().setConstraintSystem(cs);
            generalReplacer.setConstraintSystem(cs);
            OWLAxiom replaced = (OWLAxiom) axiom.accept(generalReplacer);
            if (isRelevant(replaced)) {
                cache.put(owlEntity, replaced);
            }
        }
        return CollectionFactory.getCopyOnRequestSetFromImmutableCollection(cache.get(owlEntity));
    }

    protected boolean isRelevant(OWLAxiom replaced) {
        return replaced.signature().anyMatch(ontologySignature::contains)
            || replaced.accept(AxiomGeneralityDetector.getInstance()).booleanValue();
    }

    /**
     * 
     */
    public void dispose() {
        ontologyManger.removeOntologyChangeListener(listener);
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
