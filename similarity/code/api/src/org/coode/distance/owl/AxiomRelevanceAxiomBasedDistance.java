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
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

/** @author Luigi Iannone */
public class AxiomRelevanceAxiomBasedDistance implements AbstractAxiomBasedDistance {
    private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
    private final MultiMap<OWLEntity, OWLAxiom> cache = new MultiMap<OWLEntity, OWLAxiom>();
    private final OWLOntologyManager ontologyManger;
    private final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<OWLEntity, OWLAxiom>();
    private final AxiomMap axiomMap;
    private final Set<OWLEntity> ontologySignature = new HashSet<OWLEntity>();
    private final OWLEntityProvider entityProvider;
    private final OPPLFactory factory;
    private final OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
        public void ontologiesChanged(final List<? extends OWLOntologyChange> changes)
                throws OWLException {
            AxiomRelevanceAxiomBasedDistance.this.buildOntologySignature();
            AxiomRelevanceAxiomBasedDistance.this
                    .buildAxiomEntityMap(AxiomRelevanceAxiomBasedDistance.this
                            .getOntologies());
        }
    };
    private final OWLEntityReplacer replacer;

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

    public AxiomRelevanceAxiomBasedDistance(
            final Collection<? extends OWLOntology> ontologies,
            final OWLOntologyManager manager, final OWLEntityReplacer replacer) {
        if (ontologies == null) {
            throw new NullPointerException("The ontolgies canont be null");
        }
        if (manager == null) {
            throw new NullPointerException("The ontolgy manager cannot be null");
        }
        this.replacer = replacer;
        axiomMap = new AxiomMap(ontologies, manager, replacer);
        this.ontologies.addAll(ontologies);
        ontologyManger = manager;
        buildOntologySignature();
        buildAxiomEntityMap(ontologies);
        entityProvider = new OntologyManagerBasedOWLEntityProvider(getOntologyManger());
        factory = new OPPLFactory(getOntologyManger(), this.ontologies.iterator().next(),
                null);
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
        Set<AxiomType<?>> types = new HashSet<AxiomType<?>>(AxiomType.AXIOM_TYPES);
        types.remove(AxiomType.DECLARATION);
        for (OWLAxiom axiom : candidates.get(owlEntity)) {
            AxiomRelevancePolicy policy = new AxiomRelevancePolicy(
                    (OWLAxiom) axiom.accept(replacer), axiomMap);
            RelevancePolicyOWLObjectGeneralisation genreplacer = new RelevancePolicyOWLObjectGeneralisation(
                    Utils.toOWLObjectRelevancePolicy(policy), getEntityProvider());
            ConstraintSystem cs = factory.createConstraintSystem();
            ((SingleOWLEntityReplacementVariableProvider) genreplacer
                    .getVariableProvider()).setOWLObject(owlEntity);
            genreplacer.getVariableProvider().setConstraintSystem(cs);
            OWLAxiom replaced = (OWLAxiom) axiom.accept(genreplacer);
            if (isRelevant(replaced)) {
                cache.put(owlEntity, replaced);
            }
        }
        return new HashSet<OWLAxiom>(cache.get(owlEntity));
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
