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
package org.coode.distance.entityrelevance.owl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.coode.distance.entityrelevance.AtomicDecompositionRankingRelevancePolicy;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.metrics.AbstractRanking;
import org.coode.metrics.owl.AtomicDecompositionRanking;
import org.coode.owl.wrappers.OWLAtomicDecompositionMap;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

import edu.arizona.bio5.onto.decomposition.Atom;

public class AtomicDecompositionRelevancePolicyNEW implements
		RelevancePolicy<OWLEntity> {
	private final OWLAxiom axiom;
	private final OWLDataFactory dataFactory;
	private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
	private final OWLEntityReplacer replacer;
	private final AbstractRanking<OWLEntity, Double> ranking;
	private final RelevancePolicy<OWLEntity> relevance;
	MultiMap<OWLEntity, Atom> entityAtomDependencies = new MultiMap<OWLEntity, Atom>();
	
	

	/**
	 * @param axiom
	 */
	public AtomicDecompositionRelevancePolicyNEW(OWLAxiom axiom,
			OWLDataFactory dataFactory,
			Collection<? extends OWLOntology> ontologies,
			OWLAtomicDecompositionMap map) {
		if (axiom == null) {
			throw new NullPointerException("The axiom cannot be null");
		}
		if (dataFactory == null) {
			throw new NullPointerException("The dataFactory cannot be null");
		}
		if (ontologies == null) {
			throw new NullPointerException(
					"The ontolgy collection cannot be null");
		}
		if (entityAtomDependencies == null) {
			throw new NullPointerException("The axiom map cannot be null");
		}
		this.dataFactory = dataFactory;
		this.ontologies.addAll(ontologies);
		this.replacer = new OWLEntityReplacer(dataFactory,
				new ReplacementByKindStrategy(this.getDataFactory()));
		this.axiom = axiom;
		this.entityAtomDependencies.putAll(map.getEntityAtomDependencies());
		this.ranking = AtomicDecompositionRanking.buildRanking(getOntologies(), map);
		// change relevance
		this.relevance = AtomicDecompositionRankingRelevancePolicy
				.getAbstractRankingRelevancePolicy(this.ranking);

	}


	/**
	 * @see org.coode.distance.entityrelevance.RelevancePolicy#isRelevant(java.lang
	 *      .Object)
	 */
	public boolean isRelevant(OWLEntity object) {
		return this.relevance.isRelevant(object);
	}

	/**
	 * @return the axiom
	 */
	public OWLAxiom getAxiom() {
		return this.axiom;
	}

	/**
	 * @return the ontologies
	 */
	public Set<OWLOntology> getOntologies() {
		return new HashSet<OWLOntology>(this.ontologies);
	}

	/**
	 * @return the dataFactory
	 */
	public OWLDataFactory getDataFactory() {
		return this.dataFactory;
	}
}
