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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.distance.entityrelevance.CollectionBasedRelevantPolicy;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.entityrelevance.owl.AxiomGeneralityDetector;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.AxiomGeneralisationTreeNode;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.structural.RelevancePolicyOWLObjectGeneralisation;
import org.coode.owl.generalise.structural.SingleOWLEntityReplacementVariableProvider;
import org.coode.owl.generalise.structural.StructuralOWLObjectGeneralisation;
import org.coode.owl.wrappers.OWLEntityProvider;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.MultiMap;

/** @author Luigi Iannone */
public class StructuralKnowledgeExplorerAxiomRelevanceBasedDistance extends AbstractAxiomBasedDistanceImpl {
	protected final class AxiomRelevanceMap extends AxiomRelevanceMapBase {
		public AxiomRelevanceMap(final Collection<? extends OWLAxiom> axioms,
				final OWLEntityProvider entityProvider,
				final ConstraintSystem constraintSystem) {
			if (axioms == null) {
				throw new NullPointerException(
						"The axiom collection cannot be null");
			}
			for (OWLAxiom axiom : axioms) {
				StructuralOWLObjectGeneralisation generalisation = new StructuralOWLObjectGeneralisation(
						entityProvider, constraintSystem);
				OWLAxiom generalisedAxiom = (OWLAxiom) axiom
						.accept(generalisation);
				generalisationMap.put(axiom, generalisedAxiom);
				instantionMap.put(generalisedAxiom, new OWLAxiomInstantiation(
						axiom, generalisation.getSubstitutions()));
			}
			Set<OWLAxiom> generalisedAxioms = instantionMap.keySet();
			for (OWLAxiom generalisedAxiom : generalisedAxioms) {
				AxiomGeneralisationTreeNode generalisationTreeNode = new AxiomGeneralisationTreeNode(
						generalisedAxiom, instantionMap.get(generalisedAxiom),
						constraintSystem);
				relevanceMap.setEntry(generalisedAxiom,
						extractValues(generalisationTreeNode));
			}
		}
	}
	
	private final KnowledgeExplorer ke;
	private final MultiMap<OWLEntity, OWLAxiom> cache = new MultiMap<OWLEntity, OWLAxiom>();
	private final OWLOntologyManager ontologyManger;
	private final OWLEntityProvider entityProvider;
	private final MultiMap<OWLEntity, OWLAxiom> candidates = new MultiMap<OWLEntity, OWLAxiom>();
	private final Set<OWLEntity> ontologySignature = new HashSet<OWLEntity>();
	private final AxiomRelevanceMap axiomRelevanceMap;
	private final OPPLFactory opplfactory;

	private void buildAxiomEntityMap(
			final Set<OWLAxiom> set) {
		for(OWLAxiom ax : set){
			if(!ax.isOfType(AxiomType.DECLARATION)){
				for (OWLEntity e : ax.getSignature()) {
					candidates.put(e, ax);
				}
			}
		}
	}

	private void buildSignature() {
		ontologySignature.clear();
		ontologySignature.addAll(ke.getEntities());
	}

	private final Map<OWLAxiom, RelevancePolicyOWLObjectGeneralisation> replacers = new HashMap<OWLAxiom, RelevancePolicyOWLObjectGeneralisation>();

	public StructuralKnowledgeExplorerAxiomRelevanceBasedDistance(
			final OWLOntology ontology,
			final KnowledgeExplorer knowledgeExplorer) {
		if (ontology == null) {
			throw new NullPointerException("The ontolgy canont be null");
		}
		if (knowledgeExplorer == null) {
			throw new NullPointerException("The knowledge explorer cannot be null");
		}
		this.ke = knowledgeExplorer;
		this.ontologyManger = ontology.getOWLOntologyManager();
		buildSignature();
		buildAxiomEntityMap(ke.getAxioms());
		axiomRelevanceMap = buildAxiomRelevanceMap();
		entityProvider = new OntologyManagerBasedOWLEntityProvider(
				getOntologyManger());
		opplfactory = new OPPLFactory(getOntologyManger(), ontology, null);
	}

	protected AxiomRelevanceMap buildAxiomRelevanceMap() {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for (OWLAxiom axiom : ke.getAxioms()) {
			if (!axiom.getAxiomType().equals(AxiomType.DECLARATION)) {
				axioms.add(axiom);
			}
		}
		OPPLFactory factory = new OPPLFactory(getOntologyManger(),
				getOntologyManger().getOntologies().iterator().next(), null);
		ConstraintSystem constraintSystem = factory.createConstraintSystem();
		return new AxiomRelevanceMap(axioms,
				new OntologyManagerBasedOWLEntityProvider(getOntologyManger()),
				constraintSystem);
	}

	public Set<OWLAxiom> getAxioms(final OWLEntity owlEntity) {
		Collection<OWLAxiom> cached = cache.get(owlEntity);
		return cached.isEmpty() ? computeAxiomsForEntity(owlEntity)
				: new HashSet<OWLAxiom>(cached);
	}

	/**
	 * @param owlEntity
	 * @return
	 */
	protected Set<OWLAxiom> computeAxiomsForEntity(final OWLEntity owlEntity) {
		for (OWLAxiom axiom : candidates.get(owlEntity)) {
			RelevancePolicy<OWLObject> policy = CollectionBasedRelevantPolicy
					.allOf(new HashSet<OWLObject>(getRelevantEntities(axiom)));
			RelevancePolicyOWLObjectGeneralisation generalReplacer = replacers
					.get(axiom);
			if (generalReplacer == null) {
				generalReplacer = new RelevancePolicyOWLObjectGeneralisation(
						policy, getEntityProvider());
				replacers.put(axiom, generalReplacer);
			}

			((SingleOWLEntityReplacementVariableProvider) generalReplacer
					.getVariableProvider()).setOWLObject(owlEntity);
			final ConstraintSystem cs = opplfactory.createConstraintSystem();
			generalReplacer.getVariableProvider().setConstraintSystem(cs);
			generalReplacer.setConstraintSystem(cs);
			OWLAxiom replaced = (OWLAxiom) axiom.accept(generalReplacer);
			if (isRelevant(replaced)) {
				cache.put(owlEntity, replaced);
			}
		}
		return new CollectionFactory.ConditionalCopySet<OWLAxiom>(
				cache.get(owlEntity), false);
	}

	private Set<OWLEntity> getRelevantEntities(final OWLAxiom axiom) {
		return axiomRelevanceMap.getRelevantEntities(axiom);
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


	/** @return the ontologyManger */
	public OWLOntologyManager getOntologyManger() {
		return ontologyManger;
	}

	/** @return the entityProvider */
	public OWLEntityProvider getEntityProvider() {
		return entityProvider;
	}
}
