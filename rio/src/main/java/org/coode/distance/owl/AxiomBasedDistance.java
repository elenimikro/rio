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
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.structural.RelevancePolicyOWLObjectGeneralisation;
import org.coode.owl.generalise.structural.SingleOWLEntityReplacementVariableProvider;
import org.coode.owl.wrappers.OWLEntityProvider;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.MultiMap;

/** @author Luigi Iannone */
// XXX
public class AxiomBasedDistance implements AbstractAxiomBasedDistance {
    protected final List<OWLOntology> ontologies = new ArrayList<>();
    private final OWLDataFactory dataFactory;
    private final RelevancePolicy<OWLEntity> relevancePolicy;
    private final MultiMap<OWLEntity, OWLAxiom> cache = new MultiMap<>();
    private final OWLOntologyManager ontologyManger;
    private final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<>();
    private final MultiMap<OWLAxiom, OWLAxiomInstantiation> instantiationMap = new MultiMap<>();
    private final OWLOntologyChangeListener listener = changes -> buildAxiomMap();
    private final OWLEntityProvider entityProvider;
    private final OPPLFactory factory;
    private final RelevancePolicyOWLObjectGeneralisation replacer;

    void buildAxiomMap() {
        Utils.axiomsSkipDeclarations(ontologies)
            .forEach(ax -> ax.signature().forEach(e -> candidates.put(e, ax)));
    }

    /**
     * @param ontologies ontologies
     * @param dataFactory dataFactory
     * @param relevancePolicy relevancePolicy
     * @param manager manager
     */
    public AxiomBasedDistance(Stream<OWLOntology> ontologies, OWLDataFactory dataFactory,
        RelevancePolicy<OWLEntity> relevancePolicy, OWLOntologyManager manager) {
        if (ontologies == null) {
            throw new NullPointerException("The ontolgies canont be null");
        }
        if (dataFactory == null) {
            throw new NullPointerException("The data factory cannot be null");
        }
        if (relevancePolicy == null) {
            throw new NullPointerException("The relevance policy cannot be null");
        }
        if (manager == null) {
            throw new NullPointerException("The ontolgy manager cannot be null");
        }
        add(this.ontologies, ontologies);
        ontologyManger = manager;
        buildAxiomMap();
        this.relevancePolicy = relevancePolicy;
        this.dataFactory = dataFactory;
        entityProvider = new OntologyManagerBasedOWLEntityProvider(getOntologyManger());
        factory = new OPPLFactory(getOntologyManger(), this.ontologies.iterator().next(), null);
        replacer =
            new RelevancePolicyOWLObjectGeneralisation(org.coode.distance.entityrelevance.owl.Utils
                .toOWLObjectRelevancePolicy(getRelevancePolicy()), getEntityProvider());
    }

    @Override
    public double getDistance(OWLEntity a, OWLEntity b) {
        double toReturn = a.equals(b) ? 0 : 1;
        if (toReturn == 1) {
            Set<OWLAxiom> axiomsForA = getAxioms(a);
            Set<OWLAxiom> axiomsForB = getAxioms(b);
            if (!axiomsForA.isEmpty() || !axiomsForB.isEmpty()) {
                int AorB = axiomsForA.size();
                int AandB = 0;
                for (OWLAxiom e1 : axiomsForB) {
                    if (!axiomsForA.contains(e1)) {
                        // union increases for every non duplicate
                        AorB++;
                    } else {
                        // intersection increases for each common element
                        AandB++;
                    }
                }
                toReturn = (double) (AorB - AandB) / AorB;
            }
        }
        return toReturn;
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
            ConstraintSystem createConstraintSystem = factory.createConstraintSystem();
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

    /** @return the dataFactory */
    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }

    /** @return the relevancePolicy */
    public RelevancePolicy<OWLEntity> getRelevancePolicy() {
        return relevancePolicy;
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
