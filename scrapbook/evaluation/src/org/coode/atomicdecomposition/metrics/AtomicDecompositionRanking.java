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
package org.coode.atomicdecomposition.metrics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.coode.atomicdecomposition.wrappers.OWLAtomicDecompositionMap;
import org.coode.metrics.AbstractRanking;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/** @author Eleni Mikroyannidi */
public class AtomicDecompositionRanking extends AbstractRanking<OWLEntity> {
	/**
	 * @param metric
	 * @param objects
	 */
	public AtomicDecompositionRanking(final Set<OWLEntity> objects,
			final Collection<OWLOntology> ontologies,
			final OWLAtomicDecompositionMap map) {
		super(new AtomicDecompositionMetric(ontologies, map), objects,
				OWLEntity.class);
	}

	/** @see org.coode.metrics.Ranking#isAverageable() */
	@Override
	public boolean isAverageable() {
		return true;
	}

	public static AtomicDecompositionRanking buildRanking(
			final Collection<OWLOntology> ontologies,
			final OWLAtomicDecompositionMap map) {
		Set<OWLEntity> entities = new HashSet<OWLEntity>();
		for (OWLOntology owlOntology : ontologies) {
			entities.addAll(owlOntology.getSignature());
		}
		return new AtomicDecompositionRanking(entities, ontologies, map);
	}
}
