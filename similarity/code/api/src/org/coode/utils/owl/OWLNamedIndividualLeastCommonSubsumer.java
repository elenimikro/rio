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
package org.coode.utils.owl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.coode.owl.wrappers.OWLAxiomProvider;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

public class OWLNamedIndividualLeastCommonSubsumer extends
		LeastCommonSubsumer<OWLNamedIndividual, OWLClass> {
	private final OWLClassLeastCommonSubsumer delegate;

	public OWLNamedIndividualLeastCommonSubsumer(OWLAxiomProvider axiomProvider,
			OWLDataFactory dataFactory) {
		super(axiomProvider, dataFactory.getOWLThing());
		this.delegate = new OWLClassLeastCommonSubsumer(axiomProvider, dataFactory);
	}

	@Override
	protected void rebuild() {
		for (OWLAxiom axiom : this.getAxiomProvider()) {
			axiom.accept(new OWLAxiomVisitorAdapter() {
				@Override
				public void visit(OWLClassAssertionAxiom axiom) {
					if (!axiom.getIndividual().isAnonymous()
							&& !axiom.getClassExpression().isAnonymous()) {
						OWLNamedIndividualLeastCommonSubsumer.this.addParent(axiom
								.getIndividual().asOWLNamedIndividual(), axiom
								.getClassExpression().asOWLClass());
					}
				}
			});
		}
	}

	@Override
	public OWLClass get(Collection<? extends OWLNamedIndividual> c) {
		List<OWLClass> results = new ArrayList<OWLClass>();
		for (OWLNamedIndividual owlNamedIndividual : c) {
			results.addAll(this.getParents(owlNamedIndividual));
		}
		results = new ArrayList<OWLClass>(new HashSet<OWLClass>(results));
		
		if(results.size() == 0)
			return null;
		
		return 	results.size() > 1 ? results.size() == 2 ? this.delegate.get(
				results.get(0), results.get(1)) : this.delegate.get(
				results.get(0),
				results.get(1),
				results.subList(2, results.size()).toArray(
						new OWLClass[results.size() - 2])) : results.get(0);
	}
}
