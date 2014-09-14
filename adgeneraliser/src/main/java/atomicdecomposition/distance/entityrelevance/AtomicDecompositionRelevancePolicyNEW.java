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
package atomicdecomposition.distance.entityrelevance;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import atomicdecomposition.metrics.AtomicDecompositionRanking;
import atomicdecomposition.wrappers.OWLAtomicDecompositionMap;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.metrics.AbstractRanking;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.atomicdecomposition.Atom;

/** @author eleni */
public class AtomicDecompositionRelevancePolicyNEW implements RelevancePolicy<OWLEntity> {
    private final OWLAxiom axiom;
    private final OWLDataFactory dataFactory;
    private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
    private final AbstractRanking<OWLEntity> ranking;
    private final RelevancePolicy<OWLEntity> relevance;
    MultiMap<OWLEntity, Atom> entityAtomDependencies = new MultiMap<OWLEntity, Atom>();

    /** @param axiom
     *            axiom
     * @param dataFactory
     *            dataFactory
     * @param ontologies
     *            ontologies
     * @param map
     *            map */
    public AtomicDecompositionRelevancePolicyNEW(OWLAxiom axiom,
            OWLDataFactory dataFactory, Collection<OWLOntology> ontologies,
            OWLAtomicDecompositionMap map) {
        if (axiom == null) {
            throw new NullPointerException("The axiom cannot be null");
        }
        if (dataFactory == null) {
            throw new NullPointerException("The dataFactory cannot be null");
        }
        if (ontologies == null) {
            throw new NullPointerException("The ontolgy collection cannot be null");
        }
        if (entityAtomDependencies == null) {
            throw new NullPointerException("The axiom map cannot be null");
        }
        this.dataFactory = dataFactory;
        this.ontologies.addAll(ontologies);
        this.axiom = axiom;
        entityAtomDependencies.putAll(map.getEntityAtomDependencies());
        ranking = AtomicDecompositionRanking.buildRanking(ontologies, map);
        // change relevance
        relevance = AtomicDecompositionRankingRelevancePolicy
                .getAbstractRankingRelevancePolicy(ranking);
    }

    @Override
    public boolean isRelevant(OWLEntity object) {
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
