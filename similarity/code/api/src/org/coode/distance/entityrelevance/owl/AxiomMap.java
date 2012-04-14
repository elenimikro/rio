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
package org.coode.distance.entityrelevance.owl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class AxiomMap {
	private final Map<OWLAxiom, Map<OWLEntity, Integer>> delegate = new HashMap<OWLAxiom, Map<OWLEntity, Integer>>();
	private final Map<OWLAxiom, Double> axiomCountMap = new HashMap<OWLAxiom, Double>();
	private final OWLEntityReplacer replacer;
	private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
	private final OWLOntologyManager ontologyManager;
	private final OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
		public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
				throws OWLException {
			AxiomMap.this.buildMaps(AxiomMap.this.getOntologies());
		}
	};

	public AxiomMap(Collection<? extends OWLOntology> ontologies,
			OWLOntologyManager ontologyManager) {
		if (ontologies == null) {
			throw new NullPointerException("The ontology colleciton cannot be null");
		}
		if (ontologyManager == null) {
			throw new NullPointerException("The ontology manager cannot be null");
		}
		this.ontologyManager = ontologyManager;
		this.replacer = new OWLEntityReplacer(ontologyManager.getOWLDataFactory(),
				new ReplacementByKindStrategy(ontologyManager.getOWLDataFactory()));
		ontologyManager.addOntologyChangeListener(this.listener);
		this.buildMaps(ontologies);
	}

	private void buildMaps(Collection<? extends OWLOntology> ontologies) {
		this.delegate.clear();
		this.axiomCountMap.clear();
		for (OWLOntology owlOntology : ontologies) {
			for (OWLAxiom axiom : owlOntology.getAxioms()) {
				if (axiom.getAxiomType() != AxiomType.DECLARATION) {
					OWLAxiom replaced = (OWLAxiom) axiom.accept(this.replacer);
					Map<OWLEntity, Integer> entityMap = this.delegate.get(replaced);
					Double d = this.axiomCountMap.get(replaced);
					if (d == null) {
						d = 0d;
					}
					this.axiomCountMap.put(replaced, d + 1);
					if (entityMap == null) {
						entityMap = new HashMap<OWLEntity, Integer>();
						this.delegate.put(replaced, entityMap);
					}
					Set<OWLEntity> signature = axiom.getSignature();
					for (OWLEntity owlEntity : signature) {
						Integer integer = entityMap.get(owlEntity);
						if (integer == null) {
							integer = 0;
						}
						entityMap.put(owlEntity, integer + 1);
					}
				}
			}
		}
	}

	public Map<OWLEntity, Integer> get(OWLAxiom object) {
		Map<OWLEntity, Integer> map = this.delegate.get(object);
		if (map == null) {
			map = new HashMap<OWLEntity, Integer>();
		}
		return new HashMap<OWLEntity, Integer>(map);
	}

	public double getAxiomCount(Object object) {
		Double toReturn = this.axiomCountMap.get(object);
		if (toReturn == null) {
			toReturn = 0d;
		}
		return toReturn;
	}

	public Set<OWLAxiom> getAxioms() {
		return new HashSet<OWLAxiom>(this.delegate.keySet());
	}

	public void dispose() {
		this.ontologyManager.removeOntologyChangeListener(this.listener);
	}

	/**
	 * @return the ontologies
	 */
	public Set<OWLOntology> getOntologies() {
		return new HashSet<OWLOntology>(this.ontologies);
	}
}
