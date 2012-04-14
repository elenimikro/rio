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

import java.io.IOException;
import java.util.Properties;

import org.coode.distance.ReplacementStrategy;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

public class ReplacementByKindStrategy implements ReplacementStrategy {
	private final static Properties properties = new Properties();
	private final OWLDataFactory dataFactory;

	/**
	 * @param dataFactory
	 */
	public ReplacementByKindStrategy(OWLDataFactory dataFactory) {
		if (dataFactory == null) {
			throw new NullPointerException("The data factory cannot be null");
		}
		this.dataFactory = dataFactory;
	}

	static {
		try {
			properties.load(ReplacementByKindStrategy.class
					.getResourceAsStream(ReplacementByKindStrategy.class.getName()
							+ ".properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public <O extends OWLObject> O replace(O owlObject) {
		return owlObject.accept(new OWLObjectVisitorExAdapter<O>() {
			@SuppressWarnings("unchecked")
			@Override
			protected O getDefaultReturnValue(OWLObject object) {
				// I am actually sure the cast is safe
				return (O) object;
			}

			@SuppressWarnings("unchecked")
			@Override
			public O visit(OWLClass desc) {
				// I am actually sure the cast is safe
				return (O) ReplacementByKindStrategy.this.getOWLClassReplacement(desc);
			}

			@SuppressWarnings("unchecked")
			@Override
			public O visit(OWLObjectProperty property) {
				// I am actually sure the cast is safe
				return (O) ReplacementByKindStrategy.this
						.getOWLObjectPropertyReplacement(property);
			}

			@SuppressWarnings("unchecked")
			@Override
			public O visit(IRI iri) {
				// I am actually sure the cast is safe
				return (O) ReplacementByKindStrategy.this.getIRIReplacement(iri);
			}

			@SuppressWarnings("unchecked")
			@Override
			public O visit(OWLDataProperty property) {
				// I am actually sure the cast is safe
				return (O) ReplacementByKindStrategy.this
						.getOWLDataPropertyReplacement(property);
			}

			@SuppressWarnings("unchecked")
			@Override
			public O visit(OWLNamedIndividual individual) {
				// I am actually sure the cast is safe
				return (O) ReplacementByKindStrategy.this
						.getOWLNamedIndividualReplacement(individual);
			}

			@SuppressWarnings("unchecked")
			@Override
			public O visit(OWLLiteral literal) {
				// I am actually sure the cast is safe
				return (O) ReplacementByKindStrategy.this
						.getOWLLiteralReplacement(literal);
			}

			@SuppressWarnings("unchecked")
			@Override
			public O visit(OWLAnnotationProperty property) {
				// I am actually sure the cast is safe
				return (O) ReplacementByKindStrategy.this
						.getOWLAnnotationPropertyReplacement(property);
			}
		});
	}

	protected OWLAnnotationProperty getOWLAnnotationPropertyReplacement(
			OWLAnnotationProperty property) {
		return this.getDataFactory().getOWLAnnotationProperty(
				IRI.create(properties.getProperty("owlannotationproperty")));
	}

	protected OWLLiteral getOWLLiteralReplacement(OWLLiteral literal) {
		return this.getDataFactory().getOWLLiteral(properties.getProperty("owlliteral"));
	}

	protected OWLNamedIndividual getOWLNamedIndividualReplacement(
			OWLNamedIndividual individual) {
		return this.getDataFactory().getOWLNamedIndividual(
				IRI.create(properties.getProperty("owlnamedindividual")));
	}

	protected OWLObjectProperty getOWLObjectPropertyReplacement(OWLObjectProperty property) {
		return this.getDataFactory().getOWLObjectProperty(
				IRI.create(properties.getProperty("owlobjectproperty")));
	}

	protected OWLDataProperty getOWLDataPropertyReplacement(OWLDataProperty property) {
		return this.getDataFactory().getOWLDataProperty(
				IRI.create(properties.getProperty("owldataproperty")));
	}

	protected OWLClass getOWLClassReplacement(OWLClass desc) {
		return this.getDataFactory().getOWLClass(
				IRI.create(properties.getProperty("owlclass")));
	}

	protected IRI getIRIReplacement(IRI iri) {
		return IRI.create(properties.getProperty("iri"));
	}

	/**
	 * @return the dataFactory
	 */
	public OWLDataFactory getDataFactory() {
		return this.dataFactory;
	}
}
