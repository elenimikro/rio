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
import org.coode.owl.wrappers.OWLAtomicDecompositionMap;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

import edu.arizona.bio5.onto.decomposition.Atom;

/** @author Eleni Mikroyannidi */
public class AtomicDecompositionMetric implements Metric<OWLEntity, Double> {
    private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
    private final Map<OWLEntity, Double> cache = new HashMap<OWLEntity, Double>();
    private final OWLAtomicDecompositionMap atomsMap;

    public AtomicDecompositionMetric(final Collection<? extends OWLOntology> ontologies,
            final OWLAtomicDecompositionMap map) {
        if (ontologies == null) {
            throw new NullPointerException("The ontology collection cannot be null");
        }
        this.ontologies.addAll(ontologies);
        atomsMap = map;
        MultiMap<OWLEntity, Atom> entityAtomDependencies = atomsMap
                .getEntityAtomDependencies();
        if (entityAtomDependencies == null) {
            throw new NullPointerException("The entity atom dependencies cannot be null");
        }
        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        for (OWLOntology ontology : ontologies) {
            entities.addAll(ontology.getSignature());
        }
        for (OWLEntity owlEntity : entities) {
            cache.put(owlEntity, computeValue(owlEntity, entityAtomDependencies));
        }
    }

    /** reverse indexing entity to set of mentioning axioms */
    public MultiMap<OWLEntity, OWLAxiom> getAxiomMap(
            final Collection<? extends OWLOntology> ontos) {
        MultiMap<OWLEntity, OWLAxiom> toReturn = new MultiMap<OWLEntity, OWLAxiom>();
        for (OWLOntology ontology : ontos) {
            for (AxiomType<?> t : AxiomType.AXIOM_TYPES) {
                for (OWLAxiom ax : ontology.getAxioms(t)) {
                    for (OWLEntity e : ax.getSignature()) {
                        toReturn.put(e, ax);
                    }
                }
            }
        }
        return toReturn;
    }

    public Set<OWLAxiom> getAxiomSet(final Collection<? extends OWLOntology> ontos) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLOntology ontology : ontos) {
            for (AxiomType<?> t : AxiomType.AXIOM_TYPES) {
                axioms.addAll(ontology.getAxioms(t));
            }
        }
        return axioms;
    }

    /** @see org.coode.owl.metrics.DoubleMetric#getValue(java.lang.Object) */
    public Double getValue(final OWLEntity object) {
        Double toReturn = cache.get(object);
        return toReturn == null ? computeValue(object,
                atomsMap.getEntityAtomDependencies()) : toReturn;
    }

    private Double computeValue(final OWLEntity object,
            final MultiMap<OWLEntity, Atom> entityAtomDependencies) {
        double toReturn = entityAtomDependencies.get(object).size();
        double value = toReturn;
        cache.put(object, value);
        return value;
    }

    /** @return the ontologies */
    public Set<OWLOntology> getOntologies() {
        return new HashSet<OWLOntology>(ontologies);
    }
}
