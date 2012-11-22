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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.coode.distance.owl.OWLEntityReplacer;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class AxiomMap {
    private final Map<OWLAxiom, Map<OWLEntity, AtomicInteger>> delegate = new HashMap<OWLAxiom, Map<OWLEntity, AtomicInteger>>();
    private final Map<OWLAxiom, AtomicInteger> axiomCountMap = new HashMap<OWLAxiom, AtomicInteger>();
    private final OWLEntityReplacer replacer;
    private final Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
    private final OWLOntologyManager ontologyManager;
    private final OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
        @Override
        public void ontologiesChanged(final List<? extends OWLOntologyChange> changes)
                throws OWLException {
            AxiomMap.this.buildMaps(axioms);
        }
    };

    public AxiomMap(final Collection<? extends OWLOntology> ontologies,
            final OWLOntologyManager ontologyManager, final OWLEntityReplacer replacer) {
        if (ontologies == null) {
            throw new NullPointerException("The ontology colleciton cannot be null");
        }
        if (ontologyManager == null) {
            throw new NullPointerException("The ontology manager cannot be null");
        }
        this.ontologyManager = ontologyManager;
        this.replacer = replacer;
        ontologyManager.addOntologyChangeListener(listener);
        for(OWLOntology o : ontologies){
        	axioms.addAll(o.getAxioms());
        }
        buildMaps(axioms);
    }
    
    
    public AxiomMap(Set<OWLAxiom> axioms,
            final OWLOntologyManager ontologyManager, final OWLEntityReplacer replacer) {
        if (axioms == null) {
            throw new NullPointerException("The set of axioms cannot be null");
        }
        if (ontologyManager == null) {
            throw new NullPointerException("The ontology manager cannot be null");
        }
        this.ontologyManager = ontologyManager;
        this.replacer = replacer;
        ontologyManager.addOntologyChangeListener(listener);
        this.axioms.addAll(axioms);
        buildMaps(axioms);
    }

    void buildMaps(final Set<OWLAxiom> axioms) {
        delegate.clear();
        axiomCountMap.clear();
		for (OWLAxiom axiom : axioms) {
			if (axiom.getAxiomType() != AxiomType.DECLARATION) {
				OWLAxiom replaced = (OWLAxiom) axiom.accept(replacer);
				Map<OWLEntity, AtomicInteger> entityMap = delegate
						.get(replaced);
				AtomicInteger d = axiomCountMap.get(replaced);
				if (d == null) {
					d = new AtomicInteger();
					axiomCountMap.put(replaced, d);
				}
				d.incrementAndGet();
				if (entityMap == null) {
					entityMap = new HashMap<OWLEntity, AtomicInteger>();
					delegate.put(replaced, entityMap);
				}
				Set<OWLEntity> signature = axiom.getSignature();
				for (OWLEntity owlEntity : signature) {
					AtomicInteger integer = entityMap.get(owlEntity);
					if (integer == null) {
						integer = new AtomicInteger();
						entityMap.put(owlEntity, integer);
					}
					integer.incrementAndGet();
				}
			}
		}
    }

    public Map<OWLEntity, AtomicInteger> get(final OWLAxiom object) {
        Map<OWLEntity, AtomicInteger> map = delegate.get(object);
        if (map == null) {
            return Collections.emptyMap();
            // map = new HashMap<OWLEntity, Integer>();
        }
        return map; // new HashMap<OWLEntity, Integer>(map);
    }

    int lastElement = -1;
    OWLAxiom lastRequest;

    public int getAxiomCount(final OWLAxiom object) {
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

    public void dispose() {
        ontologyManager.removeOntologyChangeListener(listener);
    }
}
