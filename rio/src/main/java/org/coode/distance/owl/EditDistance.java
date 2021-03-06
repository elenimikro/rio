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

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.add;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.coode.distance.entityrelevance.DefaultOWLEntityRelevancePolicy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
public class EditDistance extends AbstractAxiomBasedDistance {
    /**
     * @param ontologies ontologies
     * @param manager manager
     */
    public EditDistance(Stream<OWLOntology> ontologies, OWLOntologyManager manager) {
        super(manager);
        if (ontologies == null) {
            throw new NullPointerException("The ontolgies canont be null");
        }
        add(this.ontologies, ontologies);
        buildAxiomMap();
    }

    @Override
    public double getDistance(OWLEntity a, OWLEntity b) {
        Set<OWLAxiom> axiomsForA = getAxioms(a);
        MultiMap<OWLAxiom, OWLAxiom> partitionForA = buildMap(a, axiomsForA);
        Set<OWLAxiom> axiomsForB = getAxioms(b);
        MultiMap<OWLAxiom, OWLAxiom> partitionForB = buildMap(b, axiomsForB);
        double total = partitionForA.keySet().size() + partitionForB.keySet().size();
        Set<OWLAxiom> intersection = new HashSet<>(partitionForA.keySet());
        Set<OWLAxiom> leftOut = new HashSet<>(partitionForA.keySet());
        leftOut.addAll(partitionForB.keySet());
        intersection.retainAll(partitionForB.keySet());
        leftOut.removeAll(intersection);
        double editDistance = 0;
        for (OWLAxiom owlAxiom : intersection) {
            Collection<OWLAxiom> instantiationsForA = partitionForA.get(owlAxiom);
            Collection<OWLAxiom> instantiationsForB = partitionForA.get(owlAxiom);
            Set<OWLEntity> entitiesForA = extractOWLEntities(instantiationsForA);
            Set<OWLEntity> entitiesForB = extractOWLEntities(instantiationsForB);
            if (!entitiesForA.isEmpty() || !entitiesForB.isEmpty()) {
                int AorB = entitiesForA.size();
                int AandB = 0;
                for (OWLEntity e1 : entitiesForB) {
                    if (!entitiesForA.contains(e1)) {
                        // union increases for every non duplicate
                        AorB++;
                    } else {
                        // intersection increases for each common element
                        AandB++;
                    }
                }
                editDistance += (double) (AorB - AandB) / AorB;
            }
        }
        editDistance = (editDistance + leftOut.size()) / total;
        return editDistance;
    }

    private static Set<OWLEntity> extractOWLEntities(Collection<? extends OWLAxiom> axioms) {
        return asSet(axioms.stream().flatMap(OWLAxiom::signature));
    }

    private MultiMap<OWLAxiom, OWLAxiom> buildMap(OWLEntity owlEntity,
        Collection<? extends OWLAxiom> axioms) {
        MultiMap<OWLAxiom, OWLAxiom> toReturn = new MultiMap<>();
        OWLEntityReplacer replacer = new OWLEntityReplacer(manager.getOWLDataFactory(),
            new SingleOWLObjectReplacementByKindStrategy(owlEntity, manager.getOWLDataFactory(),
                DefaultOWLEntityRelevancePolicy.getAlwaysIrrelevantPolicy()));
        for (OWLAxiom axiom : axioms) {
            OWLAxiom replaced = (OWLAxiom) axiom.accept(replacer);
            toReturn.put(replaced, axiom);
        }
        return toReturn;
    }

    /**
     * @param owlEntity owlEntity
     * @return axioms
     */
    @Override
    protected Set<OWLAxiom> computeAxiomsForEntity(OWLEntity owlEntity) {
        if (!cache.get(owlEntity).isEmpty()) {
            return CollectionFactory
                .getCopyOnRequestSetFromImmutableCollection(cache.get(owlEntity));
        }
        candidates.get(owlEntity).forEach(ax -> cache.put(owlEntity, ax));
        return CollectionFactory.getCopyOnRequestSetFromImmutableCollection(cache.get(owlEntity));
    }
}
