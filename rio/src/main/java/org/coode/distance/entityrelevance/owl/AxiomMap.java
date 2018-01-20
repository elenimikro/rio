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

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.coode.distance.owl.OWLEntityReplacer;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
public class AxiomMap {
    private final Map<OWLAxiom, Map<OWLEntity, AtomicInteger>> delegate = new HashMap<>();
    private final Map<OWLAxiom, AtomicInteger> axiomCountMap = new HashMap<>();
    private final OWLEntityReplacer replacer;
    private final Set<OWLAxiom> axioms;

    /**
     * @param ontologies ontologies
     * @param ontologyManager ontologyManager
     * @param replacer replacer
     */
    public AxiomMap(Collection<? extends OWLOntology> ontologies,
        OWLOntologyManager ontologyManager, OWLEntityReplacer replacer) {
        if (ontologies == null) {
            throw new NullPointerException("The ontology colleciton cannot be null");
        }
        if (ontologyManager == null) {
            throw new NullPointerException("The ontology manager cannot be null");
        }
        this.replacer = replacer;
        // ontologyManager.addOntologyChangeListener(listener);
        axioms = asSet(ontologies.stream().flatMap(OWLOntology::axioms));
        buildMaps(axioms);
    }

    /**
     * @param axioms axioms
     * @param replacer replacer
     */
    public AxiomMap(Set<OWLAxiom> axioms, OWLEntityReplacer replacer) {
        if (axioms == null) {
            throw new NullPointerException("The set of axioms cannot be null");
        }
        this.replacer = replacer;
        this.axioms = new HashSet<>(axioms);
        buildMaps(axioms);
    }

    void buildMaps(Set<OWLAxiom> axs) {
        delegate.clear();
        axiomCountMap.clear();
        for (OWLAxiom axiom : axs) {
            if (axiom.getAxiomType() != AxiomType.DECLARATION) {
                OWLAxiom replaced = (OWLAxiom) axiom.accept(replacer);
                Map<OWLEntity, AtomicInteger> entityMap = delegate.get(replaced);
                AtomicInteger d = axiomCountMap.get(replaced);
                if (d == null) {
                    d = new AtomicInteger();
                    axiomCountMap.put(replaced, d);
                }
                d.incrementAndGet();
                if (entityMap == null) {
                    entityMap = new HashMap<>();
                    delegate.put(replaced, entityMap);
                }
                countAxioms(axiom, entityMap);
            }
        }
    }

    protected void countAxioms(OWLAxiom axiom, Map<OWLEntity, AtomicInteger> entityMap) {
        axiom.signature()
            .forEach(e -> entityMap.computeIfAbsent(e, x -> new AtomicInteger()).incrementAndGet());
    }

    /**
     * @param object object
     * @return map
     */
    public Map<OWLEntity, AtomicInteger> get(OWLAxiom object) {
        Map<OWLEntity, AtomicInteger> map = delegate.get(object);
        if (map == null) {
            return Collections.emptyMap();
            // map = new HashMap<OWLEntity, Integer>();
        }
        return map; // new HashMap<OWLEntity, Integer>(map);
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
        AtomicInteger toReturn = axiomCountMap.get(object);
        if (toReturn == null) {
            lastRequest = object;
            lastElement = 0;
            return 0;
        }
        lastRequest = object;
        lastElement = toReturn.intValue();
        return lastElement;
    }

    /** dispose */
    public void dispose() {
        // ontologyManager.removeOntologyChangeListener(listener);
    }

    /** @return axioms */
    public Set<OWLAxiom> getAxioms() {
        return axioms;
    }
}
