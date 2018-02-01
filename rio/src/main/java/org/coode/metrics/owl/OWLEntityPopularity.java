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

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.add;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.coode.metrics.Metric;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;

/** @author Luigi Iannone */
public class OWLEntityPopularity implements Metric<OWLEntity> {
    private final List<OWLOntology> ontologies = new ArrayList<>();
    private final TObjectDoubleMap<OWLEntity> cache = new TObjectDoubleHashMap<>();
    MultiMap<OWLEntity, OWLAxiom> multimapFromOntologies = new MultiMap<>();

    /**
     * @param ontologies ontologies
     */
    public OWLEntityPopularity(Stream<OWLOntology> ontologies) {
        if (ontologies == null) {
            throw new NullPointerException("The ontology collection cannot be null");
        }
        add(this.ontologies, ontologies);
        this.ontologies.stream().flatMap(OWLOntology::axioms)
            .forEach(ax -> ax.signature().forEach(e -> multimapFromOntologies.put(e, ax)));
        // compute the number of axioms, duplicates removed
        // XXX needs to be done incrementally if changes to the ontologies are
        // made
        int size = getAxiomSet().size();
        MultiMap<OWLEntity, OWLAxiom> axioms = getAxiomMap();
        this.ontologies.stream().flatMap(OWLOntology::signature).distinct()
            .forEach(e -> cache.put(e, computeValue(e, axioms, size)));

    }

    /** @return reverse indexing entity to set of mentioning axioms */
    public MultiMap<OWLEntity, OWLAxiom> getAxiomMap() {
        return multimapFromOntologies;
    }

    /**
     * @return axioms
     */
    public Set<OWLAxiom> getAxiomSet() {
        return asSet(ontologies.stream().flatMap(OWLOntology::axioms));
    }

    @Override
    public double getValue(OWLEntity object) {
        return cache.containsKey(object) ? cache.get(object)
            : computeValue(object, getAxiomMap(), getAxiomSet().size());
    }

    private double computeValue(OWLEntity object, MultiMap<OWLEntity, OWLAxiom> axioms, int size) {
        double toReturn = axioms.get(object).size();
        // Eliminated the duplicates by putting everything in the same set
        double value = toReturn / size;
        cache.put(object, value);
        return value;
    }
}
