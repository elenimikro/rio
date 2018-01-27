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

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.coode.metrics.Metric;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

/** @author Luigi Iannone */
public class OWLEntityPopularity implements Metric<OWLEntity> {
    private final Set<OWLOntology> ontologies = new HashSet<>();
    private final Map<OWLEntity, Double> cache = new HashMap<>();
    MultiMap<OWLEntity, OWLAxiom> multimapFromOntologies = new MultiMap<>();

    /**
     * @param ontologies ontologies
     */
    public OWLEntityPopularity(Collection<? extends OWLOntology> ontologies) {
        if (ontologies == null) {
            throw new NullPointerException("The ontology collection cannot be null");
        }
        this.ontologies.addAll(ontologies);
        ontologies.stream().flatMap(OWLOntology::axioms)
            .forEach(ax -> ax.signature().forEach(e -> multimapFromOntologies.put(e, ax)));
        // compute the number of axioms, duplicates removed
        // XXX needs to be done incrementally if changes to the ontologies are
        // made
        int size = getAxiomSet(ontologies).size();
        MultiMap<OWLEntity, OWLAxiom> axioms = getAxiomMap();
        ontologies.stream().flatMap(OWLOntology::signature).distinct()
            .forEach(e -> cache.put(e, Double.valueOf(computeValue(e, axioms, size))));

    }

    /** @return reverse indexing entity to set of mentioning axioms */
    public MultiMap<OWLEntity, OWLAxiom> getAxiomMap() {
        return multimapFromOntologies;
    }

    /**
     * @param ontos ontos
     * @return axioms
     */
    public Set<OWLAxiom> getAxiomSet(Collection<? extends OWLOntology> ontos) {
        return asSet(ontos.stream().flatMap(OWLOntology::axioms));
    }

    @Override
    public double getValue(OWLEntity object) {
        Double toReturn = cache.get(object);
        return toReturn == null
            ? computeValue(object, getAxiomMap(), getAxiomSet(ontologies).size())
            : toReturn.doubleValue();
    }

    private double computeValue(OWLEntity object, MultiMap<OWLEntity, OWLAxiom> axioms, int size) {
        double toReturn = axioms.get(object).size();
        // Eliminated the duplicates by putting everything in the same set
        double value = toReturn / size;
        cache.put(object, Double.valueOf(value));
        return value;
    }

    /** @return the ontologies */
    public Set<OWLOntology> getOntologies() {
        return new HashSet<>(ontologies);
    }
}
