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

import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.structural.RelevancePolicyOWLObjectGeneralisation;
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

/**
 * @author Luigi Iannone
 * 
 */
public class AxiomBasedDistance implements AbstractAxiomBasedDistance {
	private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
	private final OWLDataFactory dataFactory;
	private final RelevancePolicy<OWLEntity> relevancePolicy;
	private final MultiMap<OWLEntity, OWLAxiom> cache = new MultiMap<OWLEntity, OWLAxiom>();
	private final OWLOntologyManager ontologyManger;
	private final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<OWLEntity, OWLAxiom>();
	private final MultiMap<OWLAxiom, OWLAxiomInstantiation> instantiationMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
	private final OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
		public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
				throws OWLException {
			AxiomBasedDistance.this
					.buildAxiomMap(AxiomBasedDistance.this.getOntologies());
		}
	};
	private final OWLEntityProvider entityProvider;
	private final OPPLFactory factory;

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

	public AxiomBasedDistance(Collection<? extends OWLOntology> ontologies,
			OWLDataFactory dataFactory, RelevancePolicy<OWLEntity> relevancePolicy,
			OWLOntologyManager manager) {
		if (ontologies == null) {
			throw new NullPointerException("The ontolgies canont be null");
		}
		if (dataFactory == null) {
			throw new NullPointerException("The data factory cannot be null");
		}
		if (relevancePolicy == null) {
			throw new NullPointerException("The relevance policy cannot be null");
		}
		if (manager == null) {
			throw new NullPointerException("The ontolgy manager cannot be null");
		}
		this.ontologies.addAll(ontologies);
		this.ontologyManger = manager;
		this.buildAxiomMap(ontologies);
		this.relevancePolicy = relevancePolicy;
		this.dataFactory = dataFactory;
		this.entityProvider = new OntologyManagerBasedOWLEntityProvider(
				this.getOntologyManger());
		this.factory = new OPPLFactory(this.getOntologyManger(), this.ontologies
				.iterator().next(), null);
	}

	/**
	 * @see org.coode.distance.Distance#getDistance(java.lang.Object,
	 *      java.lang.Object)
	 */
	public double getDistance(OWLEntity a, OWLEntity b) {
		double toReturn = a.equals(b) ? 0 : 1;
		if (toReturn == 1) {
			Set<OWLAxiom> axiomsForA = this.getAxioms(a);
			Set<OWLAxiom> axiomsForB = this.getAxioms(b);
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
		RelevancePolicyOWLObjectGeneralisation replacer = new RelevancePolicyOWLObjectGeneralisation(
				org.coode.distance.entityrelevance.owl.Utils.toOWLObjectRelevancePolicy(this
						.getRelevancePolicy()), owlEntity, this.getEntityProvider(),
				factory.createConstraintSystem());
		Set<AxiomType<?>> types = new HashSet<AxiomType<?>>(AxiomType.AXIOM_TYPES);
		types.remove(AxiomType.DECLARATION);
		for (OWLAxiom axiom : this.candidates.get(owlEntity)) {
			OWLAxiom replaced = (OWLAxiom) axiom.accept(replacer);
			if (this.isRelevant(replaced)) {
				this.cache.put(owlEntity, replaced);
			}
			//XXX: put method for the instantiationMap. 
			instantiationMap.put(replaced, new OWLAxiomInstantiation(axiom, replacer.getSubstitutions()));
		}
		return new HashSet<OWLAxiom>(cache.get(owlEntity));
	}

	public Collection<OWLAxiomInstantiation> getInstantiations(OWLAxiom ax){
		return instantiationMap.get(ax);
	}
	
	protected boolean isRelevant(OWLAxiom replaced) {
		Set<OWLEntity> signature = replaced.getSignature();
		boolean found = false;
		Iterator<OWLEntity> iterator = signature.iterator();
		while (!found && iterator.hasNext()) {
			OWLEntity owlEntity = iterator.next();
			found = this.getRelevancePolicy().isRelevant(owlEntity);
		}
		return found;
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

	/**
	 * @return the relevancePolicy
	 */
	public RelevancePolicy<OWLEntity> getRelevancePolicy() {
		return this.relevancePolicy;
	}

	public void dispose() {
		this.ontologyManger.removeOntologyChangeListener(this.listener);
	}

	/**
	 * @return the ontologyManger
	 */
	public OWLOntologyManager getOntologyManger() {
		return this.ontologyManger;
	}

	/**
	 * @return the entityProvider
	 */
	public OWLEntityProvider getEntityProvider() {
		return this.entityProvider;
	}
}
