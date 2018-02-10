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

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.structural.RelevancePolicyOWLObjectGeneralisation;
import org.coode.owl.generalise.structural.SingleOWLEntityReplacementVariableProvider;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.MultiMap;

/** @author Luigi Iannone */
// XXX
public class AxiomBasedDistance extends AbstractAxiomBasedDistanceImpl {
    private final RelevancePolicy<OWLEntity> relevancePolicy;
    private final MultiMap<OWLAxiom, OWLAxiomInstantiation> instantiationMap = new MultiMap<>();
    private final RelevancePolicyOWLObjectGeneralisation replacer;

    /**
     * @param ontologies ontologies
     * @param relevancePolicy relevancePolicy
     * @param manager manager
     */
    public AxiomBasedDistance(Stream<OWLOntology> ontologies,
        RelevancePolicy<OWLEntity> relevancePolicy, OWLOntologyManager manager) {
        super(manager);
        if (ontologies == null) {
            throw new NullPointerException("The ontolgies canont be null");
        }
        if (relevancePolicy == null) {
            throw new NullPointerException("The relevance policy cannot be null");
        }
        add(this.ontologies, ontologies);
        buildAxiomMap();
        this.relevancePolicy = relevancePolicy;
        opplfactory = new OPPLFactory(manager, this.ontologies.iterator().next(), null);
        replacer =
            new RelevancePolicyOWLObjectGeneralisation(org.coode.distance.entityrelevance.owl.Utils
                .toOWLObjectRelevancePolicy(getRelevancePolicy()), getEntityProvider());
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
            ((SingleOWLEntityReplacementVariableProvider) replacer.getVariableProvider())
                .setOWLObject(owlEntity);
            ConstraintSystem createConstraintSystem = opplfactory.createConstraintSystem();
            replacer.setConstraintSystem(createConstraintSystem);
            replacer.getVariableProvider().setConstraintSystem(createConstraintSystem);
            OWLAxiom replaced = (OWLAxiom) axiom.accept(replacer);
            if (isRelevant(replaced)) {
                cache.put(owlEntity, replaced);
            }
            // XXX: put method for the instantiationMap.
            instantiationMap.put(replaced,
                new OWLAxiomInstantiation(axiom, replacer.getSubstitutions()));
        }
        return CollectionFactory.getCopyOnRequestSetFromImmutableCollection(cache.get(owlEntity));
    }

    /**
     * @param ax ax
     * @return instantiations
     */
    public Collection<OWLAxiomInstantiation> getInstantiations(OWLAxiom ax) {
        return instantiationMap.get(ax);
    }

    protected boolean isRelevant(OWLAxiom replaced) {
        return replaced.signature().anyMatch(e -> getRelevancePolicy().isRelevant(e));
    }

    /** @return the relevancePolicy */
    public RelevancePolicy<OWLEntity> getRelevancePolicy() {
        return relevancePolicy;
    }
}
