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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

public class OWLAnnotationPropertyLeastCommonSubsumer extends
		LeastCommonSubsumer<OWLAnnotationProperty, OWLAnnotationProperty> {
	public static final IRI TOP_ANNOTATION_PROPERTY_IRI = IRI
			.create("http://www.coode.org#topAnnotationProperty");

	public OWLAnnotationPropertyLeastCommonSubsumer(OWLAxiomProvider axiomProvider,
			OWLDataFactory dataFactory) {
		super(axiomProvider, dataFactory
				.getOWLAnnotationProperty(TOP_ANNOTATION_PROPERTY_IRI));
	}

	@Override
	protected void rebuild() {
		for (OWLAxiom axiom : this.getAxiomProvider()) {
			axiom.accept(new OWLAxiomVisitorAdapter() {
				@Override
				public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
					OWLAnnotationPropertyLeastCommonSubsumer.this.addParent(
							axiom.getSubProperty(), axiom.getSuperProperty());
				}
			});
		}
	}

	@Override
	public OWLAnnotationProperty get(Collection<? extends OWLAnnotationProperty> c) {
		List<OWLAnnotationProperty> results = new ArrayList<OWLAnnotationProperty>(c);
		while (results.size() > 1) {
			OWLAnnotationProperty OWLDataProperty = results.get(0);
			results.remove(OWLDataProperty);
			Set<OWLAnnotationProperty> parents = this.getParents(OWLDataProperty);
			for (OWLAnnotationProperty parent : parents) {
				this.removeDescendants(parent, results);
				if (!results.contains(parent)) {
					results.add(parent);
				}
			}
		}
		return results.get(0);
	}
}