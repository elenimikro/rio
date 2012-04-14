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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.distance.entityrelevance.CollectionBasedRelevantPolicy;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.entityrelevance.owl.AxiomGeneralityDetector;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.oppl.bindingtree.Assignment;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.owl.generalise.AxiomGeneralisationTreeNode;
import org.coode.owl.generalise.BindingNodeGeneralisationTreeNode;
import org.coode.owl.generalise.GeneralisationTreeNode;
import org.coode.owl.generalise.GeneralisationTreeNodeVisitorAdapter;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.structural.RelevancePolicyOWLObjectGeneralisation;
import org.coode.owl.generalise.structural.StructuralOWLObjectGeneralisation;
import org.coode.owl.wrappers.OWLEntityProvider;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.coode.utils.TreeNode;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.OWLObjectVisitorAdapter;

/**
 * @author Luigi Iannone
 * 
 */
public class StructuralAxiomRelevanceAxiomBasedDistance implements
		AbstractAxiomBasedDistance {
	private final class AxiomRelevanceMap {
		private final Map<OWLAxiom, OWLAxiom> generalisationMap = new HashMap<OWLAxiom, OWLAxiom>();
		private final MultiMap<OWLAxiom, OWLAxiomInstantiation> instantionMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
		private final MultiMap<OWLAxiom, OWLEntity> relevanceMap = new MultiMap<OWLAxiom, OWLEntity>();

		public AxiomRelevanceMap(Collection<? extends OWLAxiom> axioms,
				OWLEntityProvider entityProvider, ConstraintSystem constraintSystem) {
			if (axioms == null) {
				throw new NullPointerException("The axiom collection cannot be null");
			}
			for (OWLAxiom axiom : axioms) {
				StructuralOWLObjectGeneralisation generalisation = new StructuralOWLObjectGeneralisation(
						entityProvider, constraintSystem);
				OWLAxiom generalisedAxiom = (OWLAxiom) axiom.accept(generalisation);
				this.generalisationMap.put(axiom, generalisedAxiom);
				this.instantionMap.put(generalisedAxiom, new OWLAxiomInstantiation(axiom,
						generalisation.getSubstitutions()));
			}
			Set<OWLAxiom> generalisedAxioms = this.instantionMap.keySet();
			for (OWLAxiom generalisedAxiom : generalisedAxioms) {
				AxiomGeneralisationTreeNode generalisationTreeNode = new AxiomGeneralisationTreeNode(
						generalisedAxiom, this.instantionMap.get(generalisedAxiom),
						constraintSystem);
				this.relevanceMap.setEntry(generalisedAxiom,
						this.extractValues(generalisationTreeNode));
			}
		}

		public Set<OWLEntity> getRelevantEntities(OWLAxiom axiom) {
			OWLAxiom generalisedOWLAxiom = this.generalisationMap.get(axiom);
			if (generalisedOWLAxiom != null) {
				return new HashSet<OWLEntity>(this.relevanceMap.get(generalisedOWLAxiom));
			} else {
				return Collections.emptySet();
			}
		}

		private Set<OWLEntity> extractValues(
				GeneralisationTreeNode<?> generalisationTreeNode) {
			final Set<OWLEntity> toReturn = new HashSet<OWLEntity>();
			generalisationTreeNode.accept(new GeneralisationTreeNodeVisitorAdapter() {
				@Override
				public void visitBindingNodeGeneralisationTreeNode(
						BindingNodeGeneralisationTreeNode bindingNodeGeneralisationTreeNode) {
					BindingNode bindingNode = bindingNodeGeneralisationTreeNode
							.getUserObject();
					Set<Assignment> assignments = bindingNode.getAssignments();
					for (Assignment assignment : assignments) {
						OWLObject assignmentValue = assignment.getAssignment();
						assignmentValue.accept(new OWLObjectVisitorAdapter() {
							@Override
							public void visit(OWLClass desc) {
								toReturn.add(desc);
							}

							@Override
							public void visit(OWLAnnotationProperty property) {
								toReturn.add(property);
							}

							@Override
							public void visit(OWLDataProperty property) {
								toReturn.add(property);
							}

							@Override
							public void visit(OWLObjectProperty property) {
								toReturn.add(property);
							}

							@Override
							public void visit(OWLNamedIndividual individual) {
								toReturn.add(individual);
							}
						});
					}
				}
			});
			List<TreeNode<?>> children = generalisationTreeNode.getChildren();
			for (TreeNode<?> child : children) {
				toReturn.addAll(this.extractValues((GeneralisationTreeNode<?>) child));
			}
			return toReturn;
		}
	}

	private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
	private final OWLDataFactory dataFactory;
	private final MultiMap<OWLEntity, OWLAxiom> cache = new MultiMap<OWLEntity, OWLAxiom>();
	private final OWLOntologyManager ontologyManger;
	private final OWLEntityProvider entityProvider;
	private final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<OWLEntity, OWLAxiom>();
	private final Set<OWLEntity> ontologySignature = new HashSet<OWLEntity>();
	private final AxiomRelevanceMap axiomRelevanceMap;
	private final OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
		public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
				throws OWLException {
			StructuralAxiomRelevanceAxiomBasedDistance.this.buildOntologySignature();
			StructuralAxiomRelevanceAxiomBasedDistance.this
					.buildAxiomEntityMap(StructuralAxiomRelevanceAxiomBasedDistance.this
							.getOntologies());
		}
	};

	private void buildAxiomEntityMap(Collection<? extends OWLOntology> ontos) {
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

	private void buildOntologySignature() {
		this.ontologySignature.clear();
		for (OWLOntology ontology : this.getOntologies()) {
			this.ontologySignature.addAll(ontology.getSignature());
		}
	}

	public StructuralAxiomRelevanceAxiomBasedDistance(
			Collection<? extends OWLOntology> ontologies, OWLDataFactory dataFactory,
			OWLOntologyManager manager) {
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
		this.buildOntologySignature();
		this.buildAxiomEntityMap(ontologies);
		this.axiomRelevanceMap = this.buildAxiomRelevanceMap();
		this.dataFactory = dataFactory;
		this.entityProvider = new OntologyManagerBasedOWLEntityProvider(
				this.getOntologyManger());
	}

	private AxiomRelevanceMap buildAxiomRelevanceMap() {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for (OWLOntology ontology : this.ontologies) {
			for (OWLAxiom axiom : ontology.getAxioms()) {
				if (!axiom.getAxiomType().equals(AxiomType.DECLARATION)) {
					axioms.add(axiom);
				}
			}
		}
		OPPLFactory factory = new OPPLFactory(this.getOntologyManger(), this
				.getOntologyManger().getOntologies().iterator().next(), null);
		ConstraintSystem constraintSystem = factory.createConstraintSystem();
		return new AxiomRelevanceMap(axioms, new OntologyManagerBasedOWLEntityProvider(
				this.getOntologyManger()), constraintSystem);
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
		Collection<OWLAxiom> cached = cache.get(owlEntity);
		return cached.isEmpty() ? this.computeAxiomsForEntity(owlEntity)
				: new HashSet<OWLAxiom>(cached);
	}

	/**
	 * @param owlEntity
	 * @return
	 */
	protected Set<OWLAxiom> computeAxiomsForEntity(OWLEntity owlEntity) {
		Set<AxiomType<?>> types = new HashSet<AxiomType<?>>(AxiomType.AXIOM_TYPES);
		types.remove(AxiomType.DECLARATION);
		OPPLFactory factory = new OPPLFactory(this.getOntologyManger(), this.ontologies
				.iterator().next(), null);
		for (OWLAxiom axiom : this.candidates.get(owlEntity)) {
			RelevancePolicy<OWLObject> policy = CollectionBasedRelevantPolicy
					.allOf(new HashSet<OWLObject>(this.getRelevantEntities(axiom)));
			RelevancePolicyOWLObjectGeneralisation replacer = new RelevancePolicyOWLObjectGeneralisation(
					policy, owlEntity, this.getEntityProvider(),
					factory.createConstraintSystem());
			OWLAxiom replaced = (OWLAxiom) axiom.accept(replacer);
			if (this.isRelevant(replaced)) {
				this.cache.put(owlEntity, replaced);
			}
		}
		return new HashSet<OWLAxiom>(cache.get(owlEntity));
	}

	private Set<OWLEntity> getRelevantEntities(OWLAxiom axiom) {
		return this.axiomRelevanceMap.getRelevantEntities(axiom);
	}

	protected boolean isRelevant(OWLAxiom replaced) {
		Set<OWLEntity> signature = replaced.getSignature();
		boolean found = false;
		Iterator<OWLEntity> iterator = signature.iterator();
		while (!found && iterator.hasNext()) {
			OWLEntity owlEntity = iterator.next();
			found = this.ontologySignature.contains(owlEntity);
		}
		if (!found) {
			found = replaced.accept(AxiomGeneralityDetector.getInstance());
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
