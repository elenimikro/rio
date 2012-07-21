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
package org.coode.metrics.owl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coode.metrics.AbstractRanking;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Luigi Iannone
 *
 */
public class OWLEntityPopularityRanking extends AbstractRanking<OWLEntity, Double> {
	/**
	 * @param metric
	 * @param objects
	 */
	public OWLEntityPopularityRanking(Set<? extends OWLEntity> objects,
			Collection<? extends OWLOntology> ontologies) {
		super(new OWLEntityPopularity(ontologies), objects);
	}

	/**
	 * @see org.coode.metrics.Ranking#isAverageable()
	 */
	public boolean isAverageable() {
		return true;
	}

	/**
	 * @see org.coode.metrics.AbstractRanking#computeAverage()
	 */
	@Override
	protected Double computeAverage() {
		List<Double> values = this.getValuesList();
		double sum = 0;
		for (Double value : values) {
			sum += value;
		}
		return sum / values.size();
	}

	public static OWLEntityPopularityRanking buildRanking(
			Collection<? extends OWLOntology> ontologies) {
		Set<OWLEntity> entities = new HashSet<OWLEntity>();
		for (OWLOntology owlOntology : ontologies) {
			entities.addAll(owlOntology.getSignature());
		}
		return new OWLEntityPopularityRanking(entities, ontologies);
	}
}
