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
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.coode.distance.entityrelevance.CollectionBasedRelevantPolicy;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.entityrelevance.owl.AxiomGeneralityDetector;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.AxiomGeneralisationTreeNode;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.structural.RelevancePolicyOWLObjectGeneralisation;
import org.coode.owl.generalise.structural.SingleOWLEntityReplacementVariableProvider;
import org.coode.owl.generalise.structural.StructuralOWLObjectGeneralisation;
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
public class StructuralAxiomRelevanceAxiomBasedDistance extends AbstractAxiomBasedDistanceImpl {
    protected final class AxiomRelevanceMap extends AxiomRelevanceMapBase {
        public AxiomRelevanceMap(Collection<? extends OWLAxiom> axioms,
            OWLEntityProvider entityProvider, ConstraintSystem constraintSystem) {
            if (axioms == null) {
                throw new NullPointerException("The axiom collection cannot be null");
            }
            for (OWLAxiom axiom : axioms) {
                StructuralOWLObjectGeneralisation generalisation =
                    new StructuralOWLObjectGeneralisation(entityProvider, constraintSystem);
                OWLAxiom generalisedAxiom = (OWLAxiom) axiom.accept(generalisation);
                generalisationMap.put(axiom, generalisedAxiom);
                instantionMap.put(generalisedAxiom,
                    new OWLAxiomInstantiation(axiom, generalisation.getSubstitutions()));
            }
            Set<OWLAxiom> generalisedAxioms = instantionMap.keySet();
            for (OWLAxiom generalisedAxiom : generalisedAxioms) {
                AxiomGeneralisationTreeNode generalisationTreeNode =
                    new AxiomGeneralisationTreeNode(generalisedAxiom,
                        instantionMap.get(generalisedAxiom).stream(), constraintSystem);
                relevanceMap.setEntry(generalisedAxiom, extractValues(generalisationTreeNode));
            }
        }
    }

    protected final List<OWLOntology> ontologies = new ArrayList<>();
    private final OWLDataFactory dataFactory;
    private final MultiMap<OWLEntity, OWLAxiom> cache = new MultiMap<>();
    private final OWLOntologyManager ontologyManger;
    private final OWLEntityProvider entityProvider;
    private final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<>();
    private final Set<OWLEntity> ontologySignature = new HashSet<>();
    private final AxiomRelevanceMap axiomRelevanceMap;
    private final OPPLFactory opplfactory;
    private final OWLOntologyChangeListener listener = changes -> {
        buildOntologySignature();
        buildAxiomEntityMap();
    };

    protected void buildAxiomEntityMap() {
        ontologies.stream().flatMap(OWLOntology::axioms).filter(Utils::NOT_DECLARATION)
            .forEach(ax -> ax.signature().forEach(e -> candidates.put(e, ax)));
    }

    protected void buildOntologySignature() {
        ontologySignature.clear();
        add(ontologySignature, ontologies.stream().flatMap(OWLOntology::signature));
    }

    private final Map<OWLAxiom, RelevancePolicyOWLObjectGeneralisation> replacers = new HashMap<>();

    /**
     * @param ontologies ontologies
     * @param dataFactory dataFactory
     * @param manager manager
     */
    public StructuralAxiomRelevanceAxiomBasedDistance(Stream<? extends OWLOntology> ontologies,
        OWLDataFactory dataFactory, OWLOntologyManager manager) {
        if (ontologies == null) {
            throw new NullPointerException("The ontolgies canont be null");
        }
        if (dataFactory == null) {
            throw new NullPointerException("The data factory cannot be null");
        }
        if (manager == null) {
            throw new NullPointerException("The ontolgy manager cannot be null");
        }
        add(this.ontologies, ontologies);
        ontologyManger = manager;
        opplfactory = new OPPLFactory(ontologyManger, this.ontologies.iterator().next(), null);
        buildOntologySignature();
        buildAxiomEntityMap();
        axiomRelevanceMap = buildAxiomRelevanceMap();
        this.dataFactory = dataFactory;
        entityProvider = new OntologyManagerBasedOWLEntityProvider(ontologyManger);
    }

    protected AxiomRelevanceMap buildAxiomRelevanceMap() {
        Set<OWLAxiom> axioms =
            asSet(ontologies.stream().flatMap(OWLOntology::axioms).filter(Utils::NOT_DECLARATION));
        ConstraintSystem constraintSystem = opplfactory.createConstraintSystem();
        return new AxiomRelevanceMap(axioms,
            new OntologyManagerBasedOWLEntityProvider(ontologyManger), constraintSystem);
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
            RelevancePolicy<OWLEntity> policy =
                CollectionBasedRelevantPolicy.allOf(getRelevantEntities(axiom));
            RelevancePolicyOWLObjectGeneralisation generalReplacer = replacers.get(axiom);
            if (generalReplacer == null) {
                generalReplacer =
                    new RelevancePolicyOWLObjectGeneralisation(policy, getEntityProvider());
                replacers.put(axiom, generalReplacer);
            }
            ((SingleOWLEntityReplacementVariableProvider) generalReplacer.getVariableProvider())
                .setOWLObject(owlEntity);
            ConstraintSystem cs = opplfactory.createConstraintSystem();
            generalReplacer.getVariableProvider().setConstraintSystem(cs);
            generalReplacer.setConstraintSystem(cs);
            OWLAxiom replaced = (OWLAxiom) axiom.accept(generalReplacer);
            if (isRelevant(replaced)) {
                cache.put(owlEntity, replaced);
            }
        }
        return CollectionFactory.getCopyOnRequestSetFromImmutableCollection(cache.get(owlEntity));
    }

    private Collection<OWLEntity> getRelevantEntities(OWLAxiom axiom) {
        return axiomRelevanceMap.getRelevantEntities(axiom);
    }

    protected boolean isRelevant(OWLAxiom replaced) {
        return replaced.signature().anyMatch(ontologySignature::contains)
            || replaced.accept(AxiomGeneralityDetector.getInstance()).booleanValue();
    }

    /** @return the dataFactory */
    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }

    /**
     * 
     */
    public void dispose() {
        ontologyManger.removeOntologyChangeListener(listener);
    }

    /** @return the entityProvider */
    public OWLEntityProvider getEntityProvider() {
        return entityProvider;
    }
}
