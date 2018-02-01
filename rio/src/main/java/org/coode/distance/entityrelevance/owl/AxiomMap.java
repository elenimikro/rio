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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/** @author eleni */
public class AxiomMap {
    private final Map<OWLAxiom, TObjectIntMap<OWLEntity>> delegate = new HashMap<>();
    private final TObjectIntMap<OWLAxiom> axiomCountMap = new TObjectIntHashMap<>();
    private final OWLEntityReplacer replacer;

    /**
     * @param ontologies ontologies
     * @param replacer replacer
     */
    public AxiomMap(Stream<? extends OWLOntology> ontologies, OWLEntityReplacer replacer) {
        if (ontologies == null) {
            throw new NullPointerException("The ontology colleciton cannot be null");
        }
        this.replacer = replacer;
        buildMaps(ontologies.flatMap(OWLOntology::axioms));
    }

    void buildMaps(Stream<OWLAxiom> axs) {
        delegate.clear();
        axiomCountMap.clear();
        axs.filter(Utils::NOT_DECLARATION).forEach(this::replaceAndCount);
    }

    protected void replaceAndCount(OWLAxiom axiom) {
        OWLAxiom replaced = (OWLAxiom) axiom.accept(replacer);
        add(replaced, axiomCountMap);
        countAxioms(axiom, delegate.computeIfAbsent(replaced, x -> new TObjectIntHashMap<>()));
    }

    protected void countAxioms(OWLAxiom ax, TObjectIntMap<OWLEntity> map) {
        ax.signature().forEach(e -> add(e, map));
    }

    private static <T> void add(T t, TObjectIntMap<T> map) {
        map.adjustOrPutValue(t, 1, 1);
    }

    private static final TObjectIntMap<OWLEntity> EMPTY = new TObjectIntHashMap<>();

    /**
     * @param object object
     * @return map
     */
    public TObjectIntMap<OWLEntity> get(OWLAxiom object) {
        return delegate.getOrDefault(object, EMPTY);
    }

    int lastElement = -1;
    OWLAxiom lastRequest;

    /**
     * @param object object
     * @return count
     */
    public int getAxiomCount(OWLAxiom object) {
        if (object == lastRequest) {
            return lastElement;
        }
        int toReturn = axiomCountMap.get(object);
        if (toReturn == 0) {
            lastRequest = object;
            lastElement = 0;
            return 0;
        }
        lastRequest = object;
        lastElement = toReturn;
        return lastElement;
    }
}
