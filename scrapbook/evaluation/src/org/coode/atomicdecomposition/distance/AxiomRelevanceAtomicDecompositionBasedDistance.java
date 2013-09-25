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
package org.coode.atomicdecomposition.distance;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.atomicdecomposition.distance.entityrelevance.AtomicDecompositionRelevancePolicy;
import org.coode.distance.entityrelevance.owl.AxiomGeneralityDetector;
import org.coode.distance.entityrelevance.owl.AxiomMap;
import org.coode.distance.entityrelevance.owl.Utils;
import org.coode.distance.owl.AbstractAxiomBasedDistanceImpl;
import org.coode.distance.owl.OWLEntityReplacer;
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
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.atomicdecomposition.Atom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposerOWLAPITOOLS;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposition;

/** @author Eleni Mikroyanndi */
public class AxiomRelevanceAtomicDecompositionBasedDistance extends
		AbstractAxiomBasedDistanceImpl {
	private final OWLOntology ontology;
	private final OWLDataFactory dataFactory;
	private final MultiMap<OWLEntity, OWLAxiom> cache = new MultiMap<OWLEntity, OWLAxiom>();
	private final OWLOntologyManager ontologyManger;
	private final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<OWLEntity, OWLAxiom>();
	private final AxiomMap axiomMap;
	private final Set<OWLEntity> ontologySignature = new HashSet<OWLEntity>();
	private final AtomicDecomposition atomicDecomposition;
	private final OWLEntityProvider entityProvider;
	MultiMap<OWLEntity, Atom> entityAtomDependencies = new MultiMap<OWLEntity, Atom>();
	private final OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {

		@Override
		public void ontologiesChanged(
				final List<? extends OWLOntologyChange> changes)
				throws OWLException {
			AxiomRelevanceAtomicDecompositionBasedDistance.this
					.buildOntologySignature();
			AxiomRelevanceAtomicDecompositionBasedDistance.this
					.buildAxiomEntityMap();
		}
	};

	private void buildAxiomEntityMap() {
		Set<AxiomType<?>> types = new HashSet<AxiomType<?>>(
				AxiomType.AXIOM_TYPES);
		types.remove(AxiomType.DECLARATION);
		for (OWLOntology ontology : this.ontology.getImportsClosure()) {
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
		for (OWLOntology ontology : this.ontology.getImportsClosure()) {
			ontologySignature.addAll(ontology.getSignature());
		}
	}

	private void buildAtomDependenciesMap() {
		for (OWLEntity entity : ontologySignature) {
			Map<OWLEntity, Set<Atom>> termBasedIndex = atomicDecomposition
					.getTermBasedIndex();
			Set<Atom> atoms = termBasedIndex.get(entity);
			if (atoms != null) {
				Set<Atom> dependencies = new HashSet<Atom>();
				for (Atom atom : atoms) {
					dependencies.addAll(atomicDecomposition
							.getDependencies(atom));
				}
				dependencies.removeAll(atoms);
				entityAtomDependencies.setEntry(entity, dependencies);
			}
		}
	}

	public AxiomRelevanceAtomicDecompositionBasedDistance(
			final OWLOntology ontology, final OWLDataFactory dataFactory,
			final OWLOntologyManager manager, final OWLEntityReplacer replacer) {
		if (ontology == null) {
			throw new NullPointerException("The ontolgies canont be null");
		}
		if (dataFactory == null) {
			throw new NullPointerException("The data factory cannot be null");
		}
		if (manager == null) {
			throw new NullPointerException("The ontolgy manager cannot be null");
		}
		axiomMap = new AxiomMap(ontology.getImportsClosure(), manager, replacer);
		this.ontology = ontology;
		ontologyManger = manager;
		buildOntologySignature();
		atomicDecomposition = new AtomicDecomposerOWLAPITOOLS(this.ontology);
		buildAtomDependenciesMap();
		buildAxiomEntityMap();
		this.dataFactory = dataFactory;
		entityProvider = new OntologyManagerBasedOWLEntityProvider(
				getOntologyManger());
	}

	@Override
	public Set<OWLAxiom> getAxioms(final OWLEntity owlEntity) {
		Collection<OWLAxiom> cached = cache.get(owlEntity);
		return cached.isEmpty() ? computeAxiomsForEntity(owlEntity)
				: CollectionFactory
						.getCopyOnRequestSetFromImmutableCollection(cached);
	}

	/**
	 * @param owlEntity
	 * @return
	 */
	protected Set<OWLAxiom> computeAxiomsForEntity(final OWLEntity owlEntity) {
		// Set<AxiomType<?>> types = new
		// HashSet<AxiomType<?>>(AxiomType.AXIOM_TYPES);
		// types.remove(AxiomType.DECLARATION);
		OPPLFactory factory = new OPPLFactory(getOntologyManger(), ontology,
				null);
		for (OWLAxiom axiom : candidates.get(owlEntity)) {
			AtomicDecompositionRelevancePolicy policy = new AtomicDecompositionRelevancePolicy(
					axiom, getOntologyManger().getOWLDataFactory(),
					ontology.getImportsClosure(), axiomMap,
					entityAtomDependencies);
			RelevancePolicyOWLObjectGeneralisation genreplacer = new RelevancePolicyOWLObjectGeneralisation(
					Utils.toOWLObjectRelevancePolicy(policy),
					getEntityProvider());
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
		return CollectionFactory
				.getCopyOnRequestSetFromImmutableCollection(cache
						.get(owlEntity));
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

	/** @return the ontology */
	public OWLOntology getOntologies() {
		return ontology;
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
