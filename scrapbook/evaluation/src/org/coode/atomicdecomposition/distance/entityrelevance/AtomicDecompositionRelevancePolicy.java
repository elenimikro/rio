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
package org.coode.atomicdecomposition.distance.entityrelevance;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.entityrelevance.owl.AxiomMap;
import org.coode.metrics.AbstractRanking;
import org.coode.metrics.Metric;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

import edu.arizona.bio5.onto.decomposition.Atom;

/** @author Luigi Iannone */
public class AtomicDecompositionRelevancePolicy implements RelevancePolicy {
    private final OWLAxiom axiom;
    private final OWLDataFactory dataFactory;
    private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
    // private final OWLEntityReplacer replacer;
    // private final AxiomMap axiomMap;
    private final AbstractRanking ranking;
    private final RelevancePolicy relevance;
    MultiMap<OWLEntity, Atom> entityAtomDependencies = new MultiMap<OWLEntity, Atom>();

    /** @param axiom */
    public AtomicDecompositionRelevancePolicy(final OWLAxiom axiom,
            final OWLDataFactory dataFactory,
            final Collection<? extends OWLOntology> ontologies, final AxiomMap axiomMap,
            final MultiMap<OWLEntity, Atom> entityAtomDependencies) {
        if (axiom == null) {
            throw new NullPointerException("The axiom cannot be null");
        }
        if (dataFactory == null) {
            throw new NullPointerException("The dataFactory cannot be null");
        }
        if (ontologies == null) {
            throw new NullPointerException("The ontolgy collection cannot be null");
        }
        if (axiomMap == null) {
            throw new NullPointerException("The axiom map cannot be null");
        }
        if (entityAtomDependencies == null) {
            throw new NullPointerException("The axiom map cannot be null");
        }
        this.dataFactory = dataFactory;
        this.ontologies.addAll(ontologies);
        // replacer = new OWLEntityReplacer(dataFactory, new
        // ReplacementByKindStrategy(
        // getDataFactory()));
        // this.axiomMap = axiomMap;
        this.axiom = axiom;
        this.entityAtomDependencies.putAll(entityAtomDependencies);
        ranking = buildRanking();
        // change relevance
        relevance = AtomicDecompositionRankingRelevancePolicy
                .getAbstractRankingRelevancePolicy(ranking);
    }

    private AbstractRanking buildRanking() {
        // final OWLAxiom replaced = (OWLAxiom) getAxiom().accept(replacer);
        Metric<OWLEntity> m = new Metric<OWLEntity>() {
            @Override
            public double getValue(final OWLEntity object) {
                double value = entityAtomDependencies.get(object).size();
                // edit this metric and add the one for the atomic decomposition
                // double total = entityAtomDependencies.getAllValues().size();
                return value;
            }
        };
        AbstractRanking toReturn = new AbstractRanking(
                m, entityAtomDependencies.keySet()) {
            @Override
            public boolean isAverageable() {
                return true;
            }


        };
        return toReturn;
    }

    /** @see org.coode.distance.entityrelevance.RelevancePolicy#isRelevant(java.lang
     *      .Object) */
    @Override
    public boolean isRelevant(final OWLEntity object) {
        return relevance.isRelevant(object);
    }

    /** @return the axiom */
    public OWLAxiom getAxiom() {
        return axiom;
    }

    /** @return the ontologies */
    public Set<OWLOntology> getOntologies() {
        return new HashSet<OWLOntology>(ontologies);
    }

    /** @return the dataFactory */
    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }
}
