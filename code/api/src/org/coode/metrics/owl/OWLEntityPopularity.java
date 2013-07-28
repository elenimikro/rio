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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.coode.metrics.Metric;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

/**
 * @author Luigi Iannone
 *
 */
public class OWLEntityPopularity implements Metric<OWLEntity> {
	private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
	private final Map<OWLEntity, Double> cache = new HashMap<OWLEntity, Double>();
    MultiMap<OWLEntity, OWLAxiom> multimapFromOntologies = new MultiMap<OWLEntity, OWLAxiom>();



	public OWLEntityPopularity(Collection<? extends OWLOntology> ontologies) {
		if (ontologies == null) {
			throw new NullPointerException("The ontology collection cannot be null");
		}
		this.ontologies.addAll(ontologies);
	    for (OWLOntology ontology : this.ontologies) {
	        for (AxiomType<?> t : AxiomType.AXIOM_TYPES) {
	            for (OWLAxiom ax : ontology.getAxioms(t)) {
	                for (OWLEntity e : ax.getSignature()) {
	                    multimapFromOntologies.put(e, ax);
	                }
	            }
	        }
	    }

		// compute the number of axioms, duplicates removed
		// XXX needs to be done incrementally if changes to the ontologies are made
		int size = getAxiomSet(ontologies).size();
        MultiMap<OWLEntity, OWLAxiom> axioms = getAxiomMap();
		Set<OWLEntity> entities = new HashSet<OWLEntity>();
		for (OWLOntology ontology : ontologies) {
			entities.addAll(ontology.getSignature());
		}
		for (OWLEntity owlEntity : entities) {
			cache.put(owlEntity, computeValue(owlEntity, axioms, size));
		}

	}

	/** reverse indexing entity to set of mentioning axioms */
	public MultiMap<OWLEntity, OWLAxiom> getAxiomMap(
) {
		return multimapFromOntologies;
	}

	public Set<OWLAxiom> getAxiomSet(Collection<? extends OWLOntology> ontos) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for (OWLOntology ontology : ontos) {
			for (AxiomType<?> t : AxiomType.AXIOM_TYPES) {
				axioms.addAll(ontology.getAxioms(t));
			}
		}
		return axioms;
	}

	/**
	 * @see org.coode.owl.metrics.DoubleMetric#getValue(java.lang.Object)
	 */
    @Override
    public double getValue(OWLEntity object) {
		Double toReturn = cache.get(object);
        return toReturn == null ? computeValue(object, getAxiomMap(),
				getAxiomSet(ontologies).size()) : toReturn;
	}

    private double computeValue(OWLEntity object, MultiMap<OWLEntity, OWLAxiom> axioms,
			int size) {
		double toReturn = axioms.get(object).size();
		// Eliminated the duplicates by putting everything in the same set
		double value = toReturn / size;
		cache.put(object, value);
		return value;
	}

	/**
	 * @return the ontologies
	 */
	public Set<OWLOntology> getOntologies() {
		return new HashSet<OWLOntology>(ontologies);
	}
}
