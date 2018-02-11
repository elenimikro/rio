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
package org.coode.distance.owl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.coode.distance.Distance;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
public abstract class AbstractAxiomBasedDistance implements Distance<OWLEntity> {
    protected final List<OWLOntology> ontologies = new ArrayList<>();
    protected final OWLOntologyManager manager;
    protected final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<>();
    protected final MultiMap<OWLEntity, OWLAxiom> cache = new MultiMap<>();
    protected final List<OWLOntologyChangeListener> listeners =
        new ArrayList<>(Arrays.asList(changes -> buildAxiomMap()));

    protected void buildAxiomMap() {
        Utils.axiomsSkipDeclarations(ontologies)
            .forEach(ax -> ax.signature().forEach(e -> candidates.put(e, ax)));
    }

    protected AbstractAxiomBasedDistance(OWLOntologyManager m) {
        if (m == null) {
            throw new NullPointerException("The ontology mnager cannot be null");
        }
        manager = m;
    }

    /**
     * 
     */
    public void dispose() {
        listeners.forEach(manager::removeOntologyChangeListener);
    }

    /**
     * @param owlEntity owlEntity
     * @return axioms
     */
    public Set<OWLAxiom> getAxioms(OWLEntity owlEntity) {
        Collection<OWLAxiom> cached = cache.get(owlEntity);
        return cached.isEmpty() ? computeAxiomsForEntity(owlEntity)
            : CollectionFactory.getCopyOnRequestSetFromImmutableCollection(cached);
    }

    /**
     * @param owlEntity owlEntity
     * @return axioms
     */
    protected abstract Set<OWLAxiom> computeAxiomsForEntity(OWLEntity owlEntity);
}
