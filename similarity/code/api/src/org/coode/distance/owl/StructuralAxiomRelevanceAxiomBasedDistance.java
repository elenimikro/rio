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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

/** @author Luigi Iannone */
public class StructuralAxiomRelevanceAxiomBasedDistance implements
        AbstractAxiomBasedDistance {
    private final class AxiomRelevanceMap extends AxiomRelevanceMapBase {
        public AxiomRelevanceMap(final Collection<? extends OWLAxiom> axioms,
                final OWLEntityProvider entityProvider,
                final ConstraintSystem constraintSystem) {
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

    private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
    private final OWLDataFactory dataFactory;
    private final MultiMap<OWLEntity, OWLAxiom> cache = new MultiMap<OWLEntity, OWLAxiom>();
    private final OWLOntologyManager ontologyManger;
    private final OWLEntityProvider entityProvider;
    private final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<OWLEntity, OWLAxiom>();
    private final Set<OWLEntity> ontologySignature = new HashSet<OWLEntity>();
    private final AxiomRelevanceMap axiomRelevanceMap;
    private final OPPLFactory opplfactory;
    private final OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
        public void ontologiesChanged(final List<? extends OWLOntologyChange> changes)
                throws OWLException {
            StructuralAxiomRelevanceAxiomBasedDistance.this.buildOntologySignature();
            StructuralAxiomRelevanceAxiomBasedDistance.this
                    .buildAxiomEntityMap(StructuralAxiomRelevanceAxiomBasedDistance.this
                            .getOntologies());
        }
    };

    private void buildAxiomEntityMap(final Collection<? extends OWLOntology> ontos) {
        Set<AxiomType<?>> types = new HashSet<AxiomType<?>>(AxiomType.AXIOM_TYPES);
        types.remove(AxiomType.DECLARATION);
        for (OWLOntology ontology : ontos) {
            for (AxiomType<?> t : types) {
                for (OWLAxiom ax : ontology.getAxioms(t)) {
                    for (OWLEntity e : ax.getSignature()) {
                        candidates.put(e, ax);
                    }
                }
            }
        }
    }

    private void buildOntologySignature() {
        ontologySignature.clear();
        for (OWLOntology ontology : getOntologies()) {
            ontologySignature.addAll(ontology.getSignature());
        }
    }

    public StructuralAxiomRelevanceAxiomBasedDistance(
            final Collection<? extends OWLOntology> ontologies,
            final OWLDataFactory dataFactory, final OWLOntologyManager manager) {
        if (ontologies == null) {
            throw new NullPointerException("The ontolgies canont be null");
        }
        if (dataFactory == null) {
            throw new NullPointerException("The data factory cannot be null");
        }
        if (manager == null) {
            throw new NullPointerException("The ontolgy manager cannot be null");
        }
        this.ontologies.addAll(ontologies);
        ontologyManger = manager;
        buildOntologySignature();
        buildAxiomEntityMap(ontologies);
        axiomRelevanceMap = buildAxiomRelevanceMap();
        this.dataFactory = dataFactory;
        entityProvider = new OntologyManagerBasedOWLEntityProvider(getOntologyManger());
        opplfactory = new OPPLFactory(getOntologyManger(), this.ontologies.iterator()
                .next(), null);
    }

    private AxiomRelevanceMap buildAxiomRelevanceMap() {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLOntology ontology : ontologies) {
            for (OWLAxiom axiom : ontology.getAxioms()) {
                if (!axiom.getAxiomType().equals(AxiomType.DECLARATION)) {
                    axioms.add(axiom);
                }
            }
        }
        OPPLFactory factory = new OPPLFactory(getOntologyManger(), getOntologyManger()
                .getOntologies().iterator().next(), null);
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        return new AxiomRelevanceMap(axioms, new OntologyManagerBasedOWLEntityProvider(
                getOntologyManger()), constraintSystem);
    }

    /** @see org.coode.distance.Distance#getDistance(java.lang.Object,
     *      java.lang.Object) */
    public double getDistance(final OWLEntity a, final OWLEntity b) {
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

    public Set<OWLAxiom> getAxioms(final OWLEntity owlEntity) {
        Collection<OWLAxiom> cached = cache.get(owlEntity);
        return cached.isEmpty() ? computeAxiomsForEntity(owlEntity)
                : new HashSet<OWLAxiom>(cached);
    }

    /** @param owlEntity
     * @return */
    protected Set<OWLAxiom> computeAxiomsForEntity(final OWLEntity owlEntity) {
        for (OWLAxiom axiom : candidates.get(owlEntity)) {
            RelevancePolicy<OWLObject> policy = CollectionBasedRelevantPolicy
                    .allOf(new HashSet<OWLObject>(getRelevantEntities(axiom)));
            RelevancePolicyOWLObjectGeneralisation replacer = new RelevancePolicyOWLObjectGeneralisation(
                    policy, getEntityProvider());
            ((SingleOWLEntityReplacementVariableProvider) replacer.getVariableProvider())
                    .setOWLObject(owlEntity);
            final ConstraintSystem cs = opplfactory.createConstraintSystem();
            replacer.setConstraintSystem(cs);
            replacer.getVariableProvider().setConstraintSystem(cs);
            OWLAxiom replaced = (OWLAxiom) axiom.accept(replacer);
            if (isRelevant(replaced)) {
                cache.put(owlEntity, replaced);
            }
        }
        return new HashSet<OWLAxiom>(cache.get(owlEntity));
    }

    private Set<OWLEntity> getRelevantEntities(final OWLAxiom axiom) {
        return axiomRelevanceMap.getRelevantEntities(axiom);
    }

    protected boolean isRelevant(final OWLAxiom replaced) {
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

    /** @return the ontologies */
    public Set<OWLOntology> getOntologies() {
        return new HashSet<OWLOntology>(ontologies);
    }

    /** @return the dataFactory */
    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }

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
