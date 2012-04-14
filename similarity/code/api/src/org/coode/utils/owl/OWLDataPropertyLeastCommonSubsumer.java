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
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

public class OWLDataPropertyLeastCommonSubsumer extends
		LeastCommonSubsumer<OWLDataProperty, OWLDataProperty> {
	public OWLDataPropertyLeastCommonSubsumer(OWLAxiomProvider axiomProvider,
			OWLDataFactory dataFactory) {
		super(axiomProvider, dataFactory.getOWLTopDataProperty());
	}

	@Override
	protected void rebuild() {
		for (OWLAxiom axiom : this.getAxiomProvider()) {
			axiom.accept(new OWLAxiomVisitorAdapter() {
				@Override
				public void visit(OWLSubDataPropertyOfAxiom axiom) {
					if (!axiom.getSubProperty().isAnonymous()
							&& !axiom.getSuperProperty().isAnonymous()) {
						OWLDataPropertyLeastCommonSubsumer.this.addParent(axiom
								.getSubProperty().asOWLDataProperty(), axiom
								.getSuperProperty().asOWLDataProperty());
					}
				}
			});
		}
	}

	@Override
	public OWLDataProperty get(Collection<? extends OWLDataProperty> c) {
		List<OWLDataProperty> results = new ArrayList<OWLDataProperty>(c);
		while (results.size() > 1) {
			OWLDataProperty OWLDataProperty = results.get(0);
			results.remove(OWLDataProperty);
			Set<OWLDataProperty> parents = this.getParents(OWLDataProperty);
			for (OWLDataProperty parent : parents) {
				this.removeDescendants(parent, results);
				if (!results.contains(parent)) {
					results.add(parent);
				}
			}
		}
		return results.get(0);
	}
}
