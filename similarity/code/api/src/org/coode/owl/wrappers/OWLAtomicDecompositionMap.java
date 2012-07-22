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
package org.coode.owl.wrappers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.demost.ui.adextension.ChiaraAtomicDecomposition;
import uk.ac.manchester.cs.demost.ui.adextension.ChiaraDecompositionAlgorithm;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import edu.arizona.bio5.onto.decomposition.Atom;

public class OWLAtomicDecompositionMap {

	private ChiaraAtomicDecomposition atomicDecomposition;
	private final OWLOntologyManager ontologyManger;
	private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
	private final Set<OWLEntity> ontologySignature = new HashSet<OWLEntity>();
	MultiMap<OWLEntity, Atom> entityAtomDependencies = new MultiMap<OWLEntity, Atom>();
	MultiMap<OWLEntity, Atom> entityAtomInfluences = new MultiMap<OWLEntity, Atom>();
	
	

	public OWLAtomicDecompositionMap(Collection<? extends OWLOntology> ontologies,OWLOntologyManager manager) {
		if (ontologies == null) {
			throw new NullPointerException("The ontolgies canont be null");
		}
		if (manager == null) {
			throw new NullPointerException("The ontolgy manager cannot be null");
		}
		this.ontologies.addAll(ontologies);
		this.ontologyManger = manager;
		this.buildOntologySignature();
		this.buildOntologyAtomicDecomposition();
		this.buildAtomDependenciesMap();
		this.buildInfluencesMap();
	}

	private void buildInfluencesMap() {
		for(OWLEntity entity : this.ontologySignature){
			Collection<Atom> atoms = this.atomicDecomposition.getEntitiesToAtom().get(
					entity);
			if (atoms != null) {
				Set<Atom> influencies = new HashSet<Atom>();
				for (Atom atom : atoms) {
					influencies.addAll(this.atomicDecomposition.getDependents(atom));
				}
				influencies.removeAll(atoms);
				entityAtomInfluences.setEntry(entity, influencies);
			}
		}
		
	}

	private void buildOntologyAtomicDecomposition() {
		ChiaraDecompositionAlgorithm chiaraDecompositionAlgorithm = new ChiaraDecompositionAlgorithm(
				ModuleType.BOT);

		this.atomicDecomposition = (ChiaraAtomicDecomposition) chiaraDecompositionAlgorithm
				.decompose(ontologyManger, null, this.getOntologies());
	}
	
	private void buildOntologySignature() {
		this.ontologySignature.clear();
		for (OWLOntology ontology : this.getOntologies()) {
			this.ontologySignature.addAll(ontology.getSignature());
		}
	}
	
	private void buildAtomDependenciesMap() {
		for(OWLEntity entity : this.ontologySignature){
			Collection<Atom> atoms = this.atomicDecomposition.getEntitiesToAtom().get(
					entity);
			if (atoms != null) {
				Set<Atom> dependencies = new HashSet<Atom>();
				for (Atom atom : atoms) {
					dependencies.addAll(this.atomicDecomposition.getDependencies(atom));
				}
				dependencies.removeAll(atoms);
				entityAtomDependencies.setEntry(entity, dependencies);
			}
		}
		
	}
	
	public ChiaraAtomicDecomposition getAtomicDecomposition() {
		return atomicDecomposition;
	}

	public MultiMap<OWLEntity, Atom> getEntityAtomDependencies() {
		return entityAtomDependencies;
	}
	
	public OWLOntologyManager getOntologyManger() {
		return ontologyManger;
	}

	public Set<OWLOntology> getOntologies() {
		return ontologies;
	}
}
