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
import java.util.List;
import java.util.Set;

import org.coode.owl.wrappers.OWLAxiomProvider;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

public class OWLClassLeastCommonSubsumer extends
		LeastCommonSubsumer<OWLClass, OWLClass> {
	public OWLClassLeastCommonSubsumer(OWLAxiomProvider axiomProvider,
			OWLDataFactory dataFactory) {
		super(axiomProvider, dataFactory.getOWLThing());
	}

	@Override
	protected void rebuild() {
		for (OWLAxiom axiom : this.getAxiomProvider()) {
			axiom.accept(new OWLAxiomVisitorAdapter() {
				@Override
				public void visit(OWLSubClassOfAxiom axiom) {
					if (!axiom.getSubClass().isAnonymous()
							&& !axiom.getSuperClass().isAnonymous()) {
						OWLClassLeastCommonSubsumer.this.addParent(axiom
								.getSubClass().asOWLClass(), axiom
								.getSuperClass().asOWLClass());
					}
				}
			});
		}
	}

	@Override
	public OWLClass get(Collection<? extends OWLClass> c) {
		List<OWLClass> results = new ArrayList<OWLClass>(c);
		while (results.size() > 1) {
			OWLClass owlClass = results.get(0);
			results.remove(owlClass);
			Set<OWLClass> parents = this.getParents(owlClass);
			// System.out.println(String.format("Child: %s Parents %s",
			// owlClass, parents));
			for (OWLClass parent : parents) {
				this.removeDescendants(parent, results);
				if (!results.contains(parent)) {
					results.add(parent);
				}
			}
			// System.out.println(results);
		}
		return results.get(0);
	}
}