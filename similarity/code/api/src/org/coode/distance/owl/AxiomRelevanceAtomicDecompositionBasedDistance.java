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
package org.coode.distance.owl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.coode.distance.entityrelevance.owl.AtomicDecompositionRelevancePolicy;
import org.coode.distance.entityrelevance.owl.AxiomGeneralityDetector;
import org.coode.distance.entityrelevance.owl.AxiomMap;
import org.coode.distance.entityrelevance.owl.Utils;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.structural.RelevancePolicyOWLObjectGeneralisation;
import org.coode.owl.generalise.structural.SingleOWLEntityReplacementVariableProvider;
import org.coode.owl.wrappers.OWLEntityProvider;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.demost.ui.adextension.ChiaraAtomicDecomposition;
import uk.ac.manchester.cs.demost.ui.adextension.ChiaraDecompositionAlgorithm;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import edu.arizona.bio5.onto.decomposition.Atom;

/** @author Luigi Iannone */
public class AxiomRelevanceAtomicDecompositionBasedDistance implements
        AbstractAxiomBasedDistance {
    private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
    private final OWLDataFactory dataFactory;
    private final MultiMap<OWLEntity, OWLAxiom> cache = new MultiMap<OWLEntity, OWLAxiom>();
    private final OWLOntologyManager ontologyManger;
    private final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<OWLEntity, OWLAxiom>();
    private final AxiomMap axiomMap;
    private final Set<OWLEntity> ontologySignature = new HashSet<OWLEntity>();
    private ChiaraAtomicDecomposition atomicDecomposition;
    private final OWLEntityProvider entityProvider;
    MultiMap<OWLEntity, Atom> entityAtomDependencies = new MultiMap<OWLEntity, Atom>();
    private final OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
        public void ontologiesChanged(final List<? extends OWLOntologyChange> changes)
                throws OWLException {
            AxiomRelevanceAtomicDecompositionBasedDistance.this.buildOntologySignature();
            AxiomRelevanceAtomicDecompositionBasedDistance.this
                    .buildAxiomEntityMap(AxiomRelevanceAtomicDecompositionBasedDistance.this
                            .getOntologies());
        }
    };

    private void buildAxiomEntityMap(final Collection<? extends OWLOntology> ontos) {
        Set<AxiomType<?>> types = new HashSet<AxiomType<?>>(AxiomType.AXIOM_TYPES);
        types.remove(AxiomType.DECLARATION);
        for (OWLOntology ontology : ontos) {
            for (AxiomType<?> t : types) {
                for (OWLAxiom ax : ontology.getAxioms(t)) {
                    for (OWLEntity e : ax.getSignature()) {
                        candidates.put(e, ax);
                    }
                }
            }
        }
    }

    private void buildOntologySignature() {
        ontologySignature.clear();
        for (OWLOntology ontology : getOntologies()) {
            ontologySignature.addAll(ontology.getSignature());
        }
    }

    private void buildOntologyAtomicDecomposition() {
        ChiaraDecompositionAlgorithm chiaraDecompositionAlgorithm = new ChiaraDecompositionAlgorithm(
                ModuleType.BOT);
        atomicDecomposition = (ChiaraAtomicDecomposition) chiaraDecompositionAlgorithm
                .decompose(ontologyManger, null, getOntologies());
    }

    private void buildAtomDependenciesMap() {
        for (OWLEntity entity : ontologySignature) {
            Collection<Atom> atoms = atomicDecomposition.getEntitiesToAtom().get(entity);
            if (atoms != null) {
                Set<Atom> dependencies = new HashSet<Atom>();
                for (Atom atom : atoms) {
                    dependencies.addAll(atomicDecomposition.getDependencies(atom));
                }
                dependencies.removeAll(atoms);
                entityAtomDependencies.setEntry(entity, dependencies);
            }
        }
    }

    public AxiomRelevanceAtomicDecompositionBasedDistance(
            final Collection<? extends OWLOntology> ontologies,
            final OWLDataFactory dataFactory, final OWLOntologyManager manager,
            final OWLEntityReplacer replacer) {
        if (ontologies == null) {
            throw new NullPointerException("The ontolgies canont be null");
        }
        if (dataFactory == null) {
            throw new NullPointerException("The data factory cannot be null");
        }
        if (manager == null) {
            throw new NullPointerException("The ontolgy manager cannot be null");
        }
        axiomMap = new AxiomMap(ontologies, manager, replacer);
        this.ontologies.addAll(ontologies);
        ontologyManger = manager;
        buildOntologySignature();
        buildOntologyAtomicDecomposition();
        buildAtomDependenciesMap();
        buildAxiomEntityMap(ontologies);
        this.dataFactory = dataFactory;
        entityProvider = new OntologyManagerBasedOWLEntityProvider(getOntologyManger());
    }

    /** @see org.coode.distance.Distance#getDistance(java.lang.Object,
     *      java.lang.Object) */
    public double getDistance(final OWLEntity a, final OWLEntity b) {
        double toReturn = a.equals(b) ? 0 : 1;
        if (toReturn == 1) {
            Set<OWLAxiom> axiomsForA = getAxioms(a);
            Set<OWLAxiom> axiomsForB = getAxioms(b);
            if (!axiomsForA.isEmpty() || !axiomsForB.isEmpty()) {
                int AorB = axiomsForA.size();
                int AandB = 0;
                for (OWLAxiom e1 : axiomsForB) {
                    if (!axiomsForA.contains(e1)) {
                        // union increases for every non duplicate
                        AorB++;
                    } else {
                        // intersection increases for each common element
                        AandB++;
                    }
                }
                toReturn = (double) (AorB - AandB) / AorB;
            }
        }
        return toReturn;
    }

    public Set<OWLAxiom> getAxioms(final OWLEntity owlEntity) {
        Collection<OWLAxiom> cached = cache.get(owlEntity);
        return cached.isEmpty() ? computeAxiomsForEntity(owlEntity)
                : new HashSet<OWLAxiom>(cached);
    }

    /** @param owlEntity
     * @return */
    protected Set<OWLAxiom> computeAxiomsForEntity(final OWLEntity owlEntity) {
        // Set<AxiomType<?>> types = new
        // HashSet<AxiomType<?>>(AxiomType.AXIOM_TYPES);
        // types.remove(AxiomType.DECLARATION);
        OPPLFactory factory = new OPPLFactory(getOntologyManger(), ontologies.iterator()
                .next(), null);
        for (OWLAxiom axiom : candidates.get(owlEntity)) {
            AtomicDecompositionRelevancePolicy policy = new AtomicDecompositionRelevancePolicy(
                    axiom, getOntologyManger().getOWLDataFactory(), getOntologies(),
                    axiomMap, entityAtomDependencies);
            RelevancePolicyOWLObjectGeneralisation genreplacer = new RelevancePolicyOWLObjectGeneralisation(
                    Utils.toOWLObjectRelevancePolicy(policy), getEntityProvider());
            final ConstraintSystem cs = factory.createConstraintSystem();
            genreplacer.getVariableProvider().setConstraintSystem(cs);
            genreplacer.getVariableProvider().setConstraintSystem(cs);
            ((SingleOWLEntityReplacementVariableProvider) genreplacer
                    .getVariableProvider()).setOWLObject(owlEntity);
            OWLAxiom replaced = (OWLAxiom) axiom.accept(genreplacer);
            if (isRelevant(replaced)) {
                cache.put(owlEntity, replaced);
            }
        }
        return new HashSet<OWLAxiom>(cache.get(owlEntity));
    }

    protected boolean isRelevant(final OWLAxiom replaced) {
        Set<OWLEntity> signature = replaced.getSignature();
        boolean found = false;
        Iterator<OWLEntity> iterator = signature.iterator();
        while (!found && iterator.hasNext()) {
            OWLEntity owlEntity = iterator.next();
            found = ontologySignature.contains(owlEntity);
        }
        if (!found) {
            found = replaced.accept(AxiomGeneralityDetector.getInstance());
        }
        return found;
    }

    /** @return the ontologies */
    public Set<OWLOntology> getOntologies() {
        return new HashSet<OWLOntology>(ontologies);
    }

    /** @return the dataFactory */
    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }

    public void dispose() {
        ontologyManger.removeOntologyChangeListener(listener);
    }

    /** @return the ontologyManger */
    public OWLOntologyManager getOntologyManger() {
        return ontologyManger;
    }

    /** @return the entityProvider */
    public OWLEntityProvider getEntityProvider() {
        return entityProvider;
    }
}
