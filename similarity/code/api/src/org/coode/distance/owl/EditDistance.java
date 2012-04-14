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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coode.distance.entityrelevance.DefaultOWLEntityRelevancePolicy;
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

public class EditDistance implements AbstractAxiomBasedDistance {
	private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
	private final OWLDataFactory dataFactory;
	private final MultiMap<OWLEntity, OWLAxiom> cache = new MultiMap<OWLEntity, OWLAxiom>();
	private final OWLOntologyManager ontologyManger;
	private final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<OWLEntity, OWLAxiom>();
	private final OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
		public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
				throws OWLException {
			EditDistance.this.buildAxiomMap(EditDistance.this.getOntologies());
		}
	};

	private void buildAxiomMap(Collection<? extends OWLOntology> ontos) {
		Set<AxiomType<?>> types = new HashSet<AxiomType<?>>(AxiomType.AXIOM_TYPES);
		types.remove(AxiomType.DECLARATION);
		for (OWLOntology ontology : ontos) {
			for (AxiomType<?> t : types) {
				for (OWLAxiom ax : ontology.getAxioms(t)) {
					for (OWLEntity e : ax.getSignature()) {
						this.candidates.put(e, ax);
					}
				}
			}
		}
	}

	public EditDistance(Collection<? extends OWLOntology> ontologies,
			OWLDataFactory dataFactory, OWLOntologyManager manager) {
		if (ontologies == null) {
			throw new NullPointerException("The ontolgies canont be null");
		}
		if (dataFactory == null) {
			throw new NullPointerException("The data factory cannot be null");
		}
		if (manager == null) {
			throw new NullPointerException("The ontolgy manager cannot be null");
		}
		this.ontologies.addAll(ontologies);
		this.ontologyManger = manager;
		this.buildAxiomMap(ontologies);
		this.dataFactory = dataFactory;
	}

	/**
	 * @see org.coode.distance.Distance#getDistance(java.lang.Object,
	 *      java.lang.Object)
	 */
	public double getDistance(OWLEntity a, OWLEntity b) {
		Set<OWLAxiom> axiomsForA = this.getAxioms(a);
		MultiMap<OWLAxiom, OWLAxiom> partitionForA = this.buildMap(a, axiomsForA);
		Set<OWLAxiom> axiomsForB = this.getAxioms(b);
		MultiMap<OWLAxiom, OWLAxiom> partitionForB = this.buildMap(b, axiomsForB);
		double total = partitionForA.keySet().size() + partitionForB.keySet().size();
		Set<OWLAxiom> intersection = new HashSet<OWLAxiom>(partitionForA.keySet());
		Set<OWLAxiom> leftOut = new HashSet<OWLAxiom>(partitionForA.keySet());
		leftOut.addAll(partitionForB.keySet());
		intersection.retainAll(partitionForB.keySet());
		leftOut.removeAll(intersection);
		double editDistance = 0;
		for (OWLAxiom owlAxiom : intersection) {
			Collection<OWLAxiom> instantiationsForA = partitionForA.get(owlAxiom);
			Collection<OWLAxiom> instantiationsForB = partitionForA.get(owlAxiom);
			Set<OWLEntity> entitiesForA = this.extractOWLEntities(instantiationsForA);
			Set<OWLEntity> entitiesForB = this.extractOWLEntities(instantiationsForB);
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

	private Set<OWLEntity> extractOWLEntities(Collection<? extends OWLAxiom> axioms) {
		Set<OWLEntity> toReturn = new HashSet<OWLEntity>();
		for (OWLAxiom axiom : axioms) {
			toReturn.addAll(axiom.getSignature());
		}
		return toReturn;
	}

	private MultiMap<OWLAxiom, OWLAxiom> buildMap(OWLEntity owlEntity,
			Collection<? extends OWLAxiom> axioms) {
		MultiMap<OWLAxiom, OWLAxiom> toReturn = new MultiMap<OWLAxiom, OWLAxiom>();
		OWLEntityReplacer replacer = new OWLEntityReplacer(this.getDataFactory(),
				new SingleOWLObjectReplacementByKindStrategy(owlEntity,
						this.getDataFactory(),
						DefaultOWLEntityRelevancePolicy.getAlwaysIrrelevantPolicy()));
		for (OWLAxiom axiom : axioms) {
			OWLAxiom replaced = (OWLAxiom) axiom.accept(replacer);
			toReturn.put(replaced, axiom);
		}
		return toReturn;
	}

	public Set<OWLAxiom> getAxioms(OWLEntity owlEntity) {
		Collection<OWLAxiom> cached = this.cache.get(owlEntity);
		return cached.isEmpty() ? this.computeAxiomsForEntity(owlEntity)
				: new HashSet<OWLAxiom>(cached);
	}

	/**
	 * @param owlEntity
	 * @return
	 */
	protected Set<OWLAxiom> computeAxiomsForEntity(OWLEntity owlEntity) {
		Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();
		if (!this.cache.get(owlEntity).isEmpty()) {
			toReturn.addAll(this.cache.get(owlEntity));
		} else {
			Set<AxiomType<?>> types = new HashSet<AxiomType<?>>(AxiomType.AXIOM_TYPES);
			types.remove(AxiomType.DECLARATION);
			for (OWLAxiom owlAxiom : this.candidates.get(owlEntity)) {
				if (types.contains(owlAxiom.getAxiomType())) {
					this.cache.put(owlEntity, owlAxiom);
				}
			}
		}
		return new HashSet<OWLAxiom>(cache.get(owlEntity));
	}

	/**
	 * @return the ontologies
	 */
	public Set<OWLOntology> getOntologies() {
		return new HashSet<OWLOntology>(this.ontologies);
	}

	/**
	 * @return the dataFactory
	 */
	public OWLDataFactory getDataFactory() {
		return this.dataFactory;
	}

	public void dispose() {
		this.ontologyManger.removeOntologyChangeListener(this.listener);
	}
}
