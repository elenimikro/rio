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
package org.coode.atomicdecomposition.wrappers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.atomicdecomposition.Atom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposerOWLAPITOOLS;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposition;

public class OWLAtomicDecompositionMap {

	private final AtomicDecomposition atomicDecomposition;
	private final OWLOntologyManager ontologyManger;
	private final OWLOntology ontology;
	private final Set<OWLEntity> ontologySignature = new HashSet<OWLEntity>();
	MultiMap<OWLEntity, Atom> entityAtomDependencies = new MultiMap<OWLEntity, Atom>();
	MultiMap<OWLEntity, Atom> entityAtomInfluences = new MultiMap<OWLEntity, Atom>();

	public OWLAtomicDecompositionMap(OWLOntology ontology,
			OWLOntologyManager manager) {
		if (ontology == null) {
			throw new NullPointerException("The ontolgies canont be null");
		}
		if (manager == null) {
			throw new NullPointerException("The ontolgy manager cannot be null");
		}
		this.ontology = ontology;
		this.ontologyManger = manager;
		this.buildOntologySignature();
		this.atomicDecomposition = new AtomicDecomposerOWLAPITOOLS(ontology);
		this.buildAtomDependenciesMap();
		this.buildInfluencesMap();
	}

	private void buildInfluencesMap() {
		for (OWLEntity entity : this.ontologySignature) {
			Map<OWLEntity, Set<Atom>> termBasedIndex = this.atomicDecomposition
					.getTermBasedIndex();
			Set<Atom> atoms = termBasedIndex.get(entity);
			if (atoms != null) {
				Set<Atom> influencies = new HashSet<Atom>();
				for (Atom atom : atoms) {
					influencies.addAll(this.atomicDecomposition
							.getDependents(atom));
				}
				influencies.removeAll(atoms);
				entityAtomInfluences.setEntry(entity, influencies);
			}
		}

	}

	private void buildOntologySignature() {
		this.ontologySignature.clear();
		for (OWLOntology ontology : this.ontology.getImportsClosure()) {
			this.ontologySignature.addAll(ontology.getSignature());
		}
	}

	private void buildAtomDependenciesMap() {
		for (OWLEntity entity : this.ontologySignature) {
			Map<OWLEntity, Set<Atom>> termBasedIndex = this.atomicDecomposition
					.getTermBasedIndex();
			Set<Atom> atoms = termBasedIndex.get(entity);
			if (atoms != null) {
				Set<Atom> dependencies = new HashSet<Atom>();
				for (Atom atom : atoms) {
					dependencies.addAll(this.atomicDecomposition
							.getDependencies(atom));
				}
				dependencies.removeAll(atoms);
				entityAtomDependencies.setEntry(entity, dependencies);
			}
		}

	}

	public AtomicDecomposition getAtomicDecomposition() {
		return atomicDecomposition;
	}

	public MultiMap<OWLEntity, Atom> getEntityAtomDependencies() {
		return entityAtomDependencies;
	}

	public OWLOntologyManager getOntologyManger() {
		return ontologyManger;
	}

	public OWLOntology getOntologies() {
		return ontology;
	}
}
