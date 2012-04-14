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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coode.distance.ReplacementStrategy;
import org.semanticweb.owlapi.model.*;

public class OWLEntityReplacer implements OWLObjectVisitorEx<OWLObject> {
	private final OWLDataFactory dataFactory;
	private final ReplacementStrategy replacementStrategy;

	/**
	 * @param structuralHashCode
	 */
	public OWLEntityReplacer(OWLDataFactory dataFactory,
			ReplacementStrategy replacementStrategy) {
		if (dataFactory == null) {
			throw new NullPointerException("The datafactory cannot be null");
		}
		if (replacementStrategy == null) {
			throw new NullPointerException("The replacement strategy cannot be null");
		}
		this.dataFactory = dataFactory;
		this.replacementStrategy = replacementStrategy;
	}

	public OWLObject visit(OWLSubClassOfAxiom axiom) {
		return this.getDataFactory().getOWLSubClassOfAxiom(
				(OWLClassExpression) axiom.getSubClass().accept(this),
				(OWLClassExpression) axiom.getSuperClass().accept(this));
	}

	public OWLObject visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		return this.getDataFactory().getOWLNegativeObjectPropertyAssertionAxiom(
				(OWLObjectPropertyExpression) axiom.getProperty().accept(this),
				(OWLIndividual) axiom.getSubject().accept(this),
				(OWLIndividual) axiom.getObject().accept(this));
	}

	public OWLObject visit(OWLAsymmetricObjectPropertyAxiom axiom) {
		return this.getDataFactory().getOWLAsymmetricObjectPropertyAxiom(
				(OWLObjectPropertyExpression) axiom.getProperty().accept(this));
	}

	public OWLObject visit(OWLReflexiveObjectPropertyAxiom axiom) {
		return this.getDataFactory().getOWLReflexiveObjectPropertyAxiom(
				(OWLObjectPropertyExpression) axiom.getProperty().accept(this));
	}

	public OWLObject visit(OWLDisjointClassesAxiom axiom) {
		Set<OWLClassExpression> operands = new HashSet<OWLClassExpression>(axiom
				.getClassExpressions().size());
		for (OWLClassExpression owlClassExpression : axiom.getClassExpressions()) {
			operands.add((OWLClassExpression) owlClassExpression.accept(this));
		}
		return this.getDataFactory().getOWLDisjointClassesAxiom(operands);
	}

	public OWLObject visit(OWLDataPropertyDomainAxiom axiom) {
		return this.getDataFactory().getOWLDataPropertyDomainAxiom(
				(OWLDataPropertyExpression) axiom.getProperty().accept(this),
				(OWLClassExpression) axiom.getDomain().accept(this));
	}

	public OWLObject visit(OWLObjectPropertyDomainAxiom axiom) {
		return this.getDataFactory().getOWLObjectPropertyDomainAxiom(
				(OWLObjectPropertyExpression) axiom.getProperty().accept(this),
				(OWLClassExpression) axiom.getDomain().accept(this));
	}

	public OWLObject visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		Set<OWLObjectPropertyExpression> properties = new HashSet<OWLObjectPropertyExpression>(
				axiom.getProperties().size());
		for (OWLObjectPropertyExpression owlObjectPropertyExpression : axiom
				.getProperties()) {
			properties.add((OWLObjectPropertyExpression) owlObjectPropertyExpression
					.accept(this));
		}
		return this.getDataFactory().getOWLEquivalentObjectPropertiesAxiom(properties);
	}

	public OWLObject visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		return this.getDataFactory().getOWLNegativeDataPropertyAssertionAxiom(
				(OWLDataPropertyExpression) axiom.getProperty().accept(this),
				(OWLIndividual) axiom.getSubject().accept(this),
				(OWLLiteral) axiom.getObject().accept(this));
	}

	public OWLObject visit(OWLDifferentIndividualsAxiom axiom) {
		Set<OWLIndividual> individuals = new HashSet<OWLIndividual>(axiom
				.getIndividuals().size());
		for (OWLIndividual owlIndividual : axiom.getIndividuals()) {
			individuals.add((OWLIndividual) owlIndividual.accept(this));
		}
		return this.getDataFactory().getOWLDifferentIndividualsAxiom(individuals);
	}

	public OWLObject visit(OWLDisjointDataPropertiesAxiom axiom) {
		Set<OWLDataPropertyExpression> individuals = new HashSet<OWLDataPropertyExpression>(
				axiom.getProperties().size());
		for (OWLDataPropertyExpression owlDataPropertyExpression : axiom.getProperties()) {
			individuals.add((OWLDataPropertyExpression) owlDataPropertyExpression
					.accept(this));
		}
		return this.getDataFactory().getOWLDisjointDataPropertiesAxiom(individuals);
	}

	public OWLObject visit(OWLDisjointObjectPropertiesAxiom axiom) {
		Set<OWLObjectPropertyExpression> owlObjectPropertyExpressions = new HashSet<OWLObjectPropertyExpression>(
				axiom.getProperties().size());
		for (OWLObjectPropertyExpression owlObjectPropertyExpression : axiom
				.getProperties()) {
			owlObjectPropertyExpressions
					.add((OWLObjectPropertyExpression) owlObjectPropertyExpression
							.accept(this));
		}
		return this.getDataFactory().getOWLDisjointObjectPropertiesAxiom(
				owlObjectPropertyExpressions);
	}

	public OWLObject visit(OWLObjectPropertyRangeAxiom axiom) {
		return this.getDataFactory().getOWLObjectPropertyRangeAxiom(
				(OWLObjectPropertyExpression) axiom.getProperty().accept(this),
				(OWLClassExpression) axiom.getRange().accept(this));
	}

	public OWLObject visit(OWLObjectPropertyAssertionAxiom axiom) {
		return this.getDataFactory().getOWLObjectPropertyAssertionAxiom(
				(OWLObjectPropertyExpression) axiom.getProperty().accept(this),
				(OWLIndividual) axiom.getSubject().accept(this),
				(OWLIndividual) axiom.getObject().accept(this));
	}

	public OWLObject visit(OWLFunctionalObjectPropertyAxiom axiom) {
		return this.getDataFactory().getOWLFunctionalObjectPropertyAxiom(
				(OWLObjectPropertyExpression) axiom.getProperty().accept(this));
	}

	public OWLObject visit(OWLSubObjectPropertyOfAxiom axiom) {
		return this.getDataFactory().getOWLSubObjectPropertyOfAxiom(
				(OWLObjectPropertyExpression) axiom.getSubProperty().accept(this),
				(OWLObjectPropertyExpression) axiom.getSuperProperty().accept(this));
	}

	public OWLObject visit(OWLDisjointUnionAxiom axiom) {
		Set<OWLClassExpression> expressions = new HashSet<OWLClassExpression>(axiom
				.getClassExpressions().size());
		for (OWLClassExpression owlClassExpression : axiom.getClassExpressions()) {
			expressions.add((OWLClassExpression) owlClassExpression.accept(this));
		}
		return this.getDataFactory().getOWLDisjointUnionAxiom(
				(OWLClass) axiom.getOWLClass().accept(this), expressions);
	}

	public OWLObject visit(OWLDeclarationAxiom axiom) {
		return this.getDataFactory().getOWLDeclarationAxiom(
				(OWLEntity) axiom.getEntity().accept(this));
	}

	public OWLObject visit(OWLAnnotationAssertionAxiom axiom) {
		return this.getDataFactory().getOWLAnnotationAssertionAxiom(
				(OWLAnnotationSubject) axiom.getSubject().accept(this),
				(OWLAnnotation) axiom.getAnnotation().accept(this));
	}

	public OWLObject visit(OWLSymmetricObjectPropertyAxiom axiom) {
		return this.getDataFactory().getOWLSymmetricObjectPropertyAxiom(
				(OWLObjectPropertyExpression) axiom.getProperty().accept(this));
	}

	public OWLObject visit(OWLDataPropertyRangeAxiom axiom) {
		return this.getDataFactory().getOWLDataPropertyRangeAxiom(
				(OWLDataPropertyExpression) axiom.getProperty().accept(this),
				(OWLDataRange) axiom.getRange().accept(this));
	}

	public OWLObject visit(OWLFunctionalDataPropertyAxiom axiom) {
		return this.getDataFactory().getOWLFunctionalDataPropertyAxiom(
				(OWLDataPropertyExpression) axiom.getProperty().accept(this));
	}

	public OWLObject visit(OWLEquivalentDataPropertiesAxiom axiom) {
		Set<OWLDataPropertyExpression> properties = new HashSet<OWLDataPropertyExpression>(
				axiom.getProperties().size());
		for (OWLDataPropertyExpression owlDataPropertyExpression : axiom.getProperties()) {
			properties.add((OWLDataPropertyExpression) owlDataPropertyExpression
					.accept(this));
		}
		return this.getDataFactory().getOWLEquivalentDataPropertiesAxiom(properties);
	}

	public OWLObject visit(OWLClassAssertionAxiom axiom) {
		return this.getDataFactory().getOWLClassAssertionAxiom(
				(OWLClassExpression) axiom.getClassExpression().accept(this),
				(OWLIndividual) axiom.getIndividual().accept(this));
	}

	public OWLObject visit(OWLEquivalentClassesAxiom axiom) {
		Set<OWLClassExpression> classExpressions = new HashSet<OWLClassExpression>(axiom
				.getClassExpressions().size());
		for (OWLClassExpression owlClassExpression : axiom.getClassExpressions()) {
			classExpressions.add((OWLClassExpression) owlClassExpression.accept(this));
		}
		return this.getDataFactory().getOWLEquivalentClassesAxiom(classExpressions);
	}

	public OWLObject visit(OWLDataPropertyAssertionAxiom axiom) {
		return this.getDataFactory().getOWLDataPropertyAssertionAxiom(
				(OWLDataPropertyExpression) axiom.getProperty().accept(this),
				(OWLIndividual) axiom.getSubject().accept(this),
				(OWLLiteral) axiom.getObject().accept(this));
	}

	public OWLObject visit(OWLTransitiveObjectPropertyAxiom axiom) {
		return this.getDataFactory().getOWLTransitiveObjectPropertyAxiom(
				(OWLObjectPropertyExpression) axiom.getProperty().accept(this));
	}

	public OWLObject visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		return this.getDataFactory().getOWLIrreflexiveObjectPropertyAxiom(
				(OWLObjectPropertyExpression) axiom.getProperty().accept(this));
	}

	public OWLObject visit(OWLSubDataPropertyOfAxiom axiom) {
		return this.getDataFactory().getOWLSubDataPropertyOfAxiom(
				(OWLDataPropertyExpression) axiom.getSubProperty().accept(this),
				(OWLDataPropertyExpression) axiom.getSuperProperty().accept(this));
	}

	public OWLObject visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		return this.getDataFactory().getOWLInverseFunctionalObjectPropertyAxiom(
				(OWLObjectPropertyExpression) axiom.getProperty().accept(this));
	}

	public OWLObject visit(OWLSameIndividualAxiom axiom) {
		Set<OWLIndividual> individuals = new HashSet<OWLIndividual>(axiom
				.getIndividuals().size());
		for (OWLIndividual owlIndividual : axiom.getIndividuals()) {
			individuals.add((OWLIndividual) owlIndividual.accept(this));
		}
		return this.getDataFactory().getOWLSameIndividualAxiom(individuals);
	}

	public OWLObject visit(OWLSubPropertyChainOfAxiom axiom) {
		List<OWLObjectPropertyExpression> properties = new ArrayList<OWLObjectPropertyExpression>(
				axiom.getPropertyChain().size());
		for (OWLObjectPropertyExpression owlObjectPropertyExpression : axiom
				.getPropertyChain()) {
			properties.add((OWLObjectPropertyExpression) owlObjectPropertyExpression
					.accept(this));
		}
		return this.getDataFactory().getOWLSubPropertyChainOfAxiom(properties,
				(OWLObjectPropertyExpression) axiom.getSuperProperty().accept(this));
	}

	public OWLObject visit(OWLInverseObjectPropertiesAxiom axiom) {
		return this.getDataFactory().getOWLInverseObjectPropertiesAxiom(
				(OWLObjectPropertyExpression) axiom.getFirstProperty().accept(this),
				(OWLObjectPropertyExpression) axiom.getSecondProperty().accept(this));
	}

	public OWLObject visit(OWLHasKeyAxiom axiom) {
		Set<OWLPropertyExpression<?, ?>> properties = new HashSet<OWLPropertyExpression<?, ?>>(
				axiom.getPropertyExpressions().size());
		for (OWLPropertyExpression<?, ?> owlPropertyExpression : axiom
				.getPropertyExpressions()) {
			properties.add((OWLPropertyExpression<?, ?>) owlPropertyExpression
					.accept(this));
		}
		return this.getDataFactory().getOWLHasKeyAxiom(
				(OWLClassExpression) axiom.getClassExpression().accept(this), properties);
	}

	public OWLObject visit(OWLDatatypeDefinitionAxiom axiom) {
		return this.getDataFactory().getOWLDatatypeDefinitionAxiom(
				(OWLDatatype) axiom.getDatatype().accept(this),
				(OWLDataRange) axiom.getDataRange().accept(this));
	}

	public OWLObject visit(SWRLRule rule) {
		Set<SWRLAtom> body = new HashSet<SWRLAtom>(rule.getBody().size());
		Set<SWRLAtom> head = new HashSet<SWRLAtom>(rule.getHead().size());
		for (SWRLAtom swrlAtom : rule.getHead()) {
			head.add((SWRLAtom) swrlAtom.accept(this));
		}
		for (SWRLAtom swrlAtom : rule.getBody()) {
			body.add((SWRLAtom) swrlAtom.accept(this));
		}
		return this.getDataFactory().getSWRLRule(head, body);
	}

	public OWLObject visit(OWLSubAnnotationPropertyOfAxiom axiom) {
		return this.getDataFactory().getOWLSubAnnotationPropertyOfAxiom(
				(OWLAnnotationProperty) axiom.getSubProperty().accept(this),
				(OWLAnnotationProperty) axiom.getSuperProperty().accept(this));
	}

	public OWLObject visit(OWLAnnotationPropertyDomainAxiom axiom) {
		return this.getDataFactory().getOWLAnnotationPropertyDomainAxiom(
				(OWLAnnotationProperty) axiom.getProperty().accept(this),
				axiom.getDomain());
	}

	public OWLObject visit(OWLAnnotationPropertyRangeAxiom axiom) {
		return this.getDataFactory().getOWLAnnotationPropertyRangeAxiom(
				(OWLAnnotationProperty) axiom.getProperty().accept(this),
				axiom.getRange());
	}

	public OWLObject visit(OWLClass ce) {
		return this.getReplacementStrategy().replace(ce);
	}

	public OWLObject visit(OWLObjectIntersectionOf ce) {
		Set<OWLClassExpression> operands = new HashSet<OWLClassExpression>(ce
				.getOperands().size());
		for (OWLClassExpression owlClassExpression : ce.getOperands()) {
			operands.add((OWLClassExpression) owlClassExpression.accept(this));
		}
		return this.getDataFactory().getOWLObjectIntersectionOf(operands);
	}

	public OWLObject visit(OWLObjectUnionOf ce) {
		Set<OWLClassExpression> operands = new HashSet<OWLClassExpression>(ce
				.getOperands().size());
		for (OWLClassExpression owlClassExpression : ce.getOperands()) {
			operands.add((OWLClassExpression) owlClassExpression.accept(this));
		}
		return this.getDataFactory().getOWLObjectUnionOf(operands);
	}

	public OWLObject visit(OWLObjectComplementOf ce) {
		return this.getDataFactory().getOWLObjectComplementOf(
				(OWLClassExpression) ce.getOperand().accept(this));
	}

	public OWLObject visit(OWLObjectSomeValuesFrom ce) {
		return this.getDataFactory().getOWLObjectSomeValuesFrom(
				(OWLObjectPropertyExpression) ce.getProperty().accept(this),
				(OWLClassExpression) ce.getFiller().accept(this));
	}

	public OWLObject visit(OWLObjectAllValuesFrom ce) {
		return this.getDataFactory().getOWLObjectAllValuesFrom(
				(OWLObjectPropertyExpression) ce.getProperty().accept(this),
				(OWLClassExpression) ce.getFiller().accept(this));
	}

	public OWLObject visit(OWLObjectHasValue ce) {
		return this.getDataFactory().getOWLObjectHasValue(
				(OWLObjectPropertyExpression) ce.getProperty().accept(this),
				(OWLIndividual) ce.getValue().accept(this));
	}

	public OWLObject visit(OWLObjectMinCardinality ce) {
		return ce.getFiller() == null ? this.getDataFactory().getOWLObjectMinCardinality(
				ce.getCardinality(),
				(OWLObjectPropertyExpression) ce.getProperty().accept(this)) : this
				.getDataFactory().getOWLObjectMinCardinality(ce.getCardinality(),
						(OWLObjectPropertyExpression) ce.getProperty().accept(this),
						(OWLClassExpression) ce.getFiller().accept(this));
	}

	public OWLObject visit(OWLObjectExactCardinality ce) {
		return ce.getFiller() == null ? this.getDataFactory()
				.getOWLObjectExactCardinality(ce.getCardinality(),
						(OWLObjectPropertyExpression) ce.getProperty().accept(this))
				: this.getDataFactory().getOWLObjectExactCardinality(ce.getCardinality(),
						(OWLObjectPropertyExpression) ce.getProperty().accept(this),
						(OWLClassExpression) ce.getFiller().accept(this));
	}

	public OWLObject visit(OWLObjectMaxCardinality ce) {
		return ce.getFiller() == null ? this.getDataFactory().getOWLObjectMaxCardinality(
				ce.getCardinality(),
				(OWLObjectPropertyExpression) ce.getProperty().accept(this)) : this
				.getDataFactory().getOWLObjectMaxCardinality(ce.getCardinality(),
						(OWLObjectPropertyExpression) ce.getProperty().accept(this),
						(OWLClassExpression) ce.getFiller().accept(this));
	}

	public OWLObject visit(OWLObjectHasSelf ce) {
		return this.getDataFactory().getOWLObjectHasSelf(
				(OWLObjectPropertyExpression) ce.getProperty().accept(this));
	}

	public OWLObject visit(OWLObjectOneOf ce) {
		Set<OWLIndividual> individuals = new HashSet<OWLIndividual>(ce.getIndividuals()
				.size());
		for (OWLIndividual owlIndividual : ce.getIndividuals()) {
			individuals.add((OWLIndividual) owlIndividual.accept(this));
		}
		return this.getDataFactory().getOWLObjectOneOf(individuals);
	}

	public OWLObject visit(OWLDataSomeValuesFrom ce) {
		return this.getDataFactory().getOWLDataSomeValuesFrom(
				(OWLDataPropertyExpression) ce.getProperty().accept(this),
				(OWLDataRange) ce.getFiller().accept(this));
	}

	public OWLObject visit(OWLDataAllValuesFrom ce) {
		return this.getDataFactory().getOWLDataAllValuesFrom(
				(OWLDataPropertyExpression) ce.getProperty().accept(this),
				(OWLDataRange) ce.getFiller().accept(this));
	}

	public OWLObject visit(OWLDataHasValue ce) {
		return this.getDataFactory().getOWLDataHasValue(
				(OWLDataPropertyExpression) ce.getProperty().accept(this),
				(OWLLiteral) ce.getValue().accept(this));
	}

	public OWLObject visit(OWLDataMinCardinality ce) {
		return ce.getFiller() == null ? this.getDataFactory().getOWLDataMinCardinality(
				ce.getCardinality(),
				(OWLDataPropertyExpression) ce.getProperty().accept(this)) : this
				.getDataFactory().getOWLDataMinCardinality(ce.getCardinality(),
						(OWLDataPropertyExpression) ce.getProperty().accept(this),
						(OWLDataRange) ce.getFiller().accept(this));
	}

	public OWLObject visit(OWLDataExactCardinality ce) {
		return ce.getFiller() == null ? this.getDataFactory().getOWLDataExactCardinality(
				ce.getCardinality(),
				(OWLDataPropertyExpression) ce.getProperty().accept(this)) : this
				.getDataFactory().getOWLDataExactCardinality(ce.getCardinality(),
						(OWLDataPropertyExpression) ce.getProperty().accept(this),
						(OWLDataRange) ce.getFiller().accept(this));
	}

	public OWLObject visit(OWLDataMaxCardinality ce) {
		return ce.getFiller() == null ? this.getDataFactory().getOWLDataMaxCardinality(
				ce.getCardinality(),
				(OWLDataPropertyExpression) ce.getProperty().accept(this)) : this
				.getDataFactory().getOWLDataMaxCardinality(ce.getCardinality(),
						(OWLDataPropertyExpression) ce.getProperty().accept(this),
						(OWLDataRange) ce.getFiller().accept(this));
	}

	public OWLObject visit(OWLDatatype node) {
		return node;
	}

	public OWLObject visit(OWLDataComplementOf node) {
		return this.getDataFactory().getOWLDataComplementOf(
				(OWLDataRange) node.getDataRange().accept(this));
	}

	public OWLObject visit(OWLDataOneOf node) {
		Set<OWLLiteral> literals = new HashSet<OWLLiteral>(node.getValues().size());
		for (OWLLiteral owlLiteral : node.getValues()) {
			literals.add((OWLLiteral) owlLiteral.accept(this));
		}
		return this.getDataFactory().getOWLDataOneOf(literals);
	}

	public OWLObject visit(OWLDataIntersectionOf node) {
		Set<OWLDataRange> operands = new HashSet<OWLDataRange>(node.getOperands().size());
		for (OWLDataRange owlDataRange : node.getOperands()) {
			operands.add((OWLDataRange) owlDataRange.accept(this));
		}
		return this.getDataFactory().getOWLDataIntersectionOf(operands);
	}

	public OWLObject visit(OWLDataUnionOf node) {
		Set<OWLDataRange> operands = new HashSet<OWLDataRange>(node.getOperands().size());
		for (OWLDataRange owlDataRange : node.getOperands()) {
			operands.add((OWLDataRange) owlDataRange.accept(this));
		}
		return this.getDataFactory().getOWLDataUnionOf(operands);
	}

	public OWLObject visit(OWLDatatypeRestriction node) {
		Set<OWLFacetRestriction> facetRestrictions = new HashSet<OWLFacetRestriction>(
				node.getFacetRestrictions().size());
		for (OWLFacetRestriction owlFacetRestriction : node.getFacetRestrictions()) {
			facetRestrictions.add((OWLFacetRestriction) owlFacetRestriction.accept(this));
		}
		return this.getDataFactory().getOWLDatatypeRestriction(
				(OWLDatatype) node.getDatatype().accept(this), facetRestrictions);
	}

	public OWLObject visit(OWLLiteral node) {
		return this.getReplacementStrategy().replace(node);
	}

	public OWLObject visit(OWLFacetRestriction node) {
		return this.getDataFactory().getOWLFacetRestriction(node.getFacet(),
				(OWLLiteral) node.getFacetValue().accept(this));
	}

	public OWLObject visit(OWLObjectProperty property) {
		return this.getReplacementStrategy().replace(property);
	}

	public OWLObject visit(OWLObjectInverseOf property) {
		return this.getDataFactory().getOWLObjectInverseOf(
				(OWLObjectPropertyExpression) property.getInverse().accept(this));
	}

	public OWLObject visit(OWLDataProperty property) {
		return this.getReplacementStrategy().replace(property);
	}

	public OWLObject visit(OWLNamedIndividual individual) {
		return this.getReplacementStrategy().replace(individual);
	}

	public OWLObject visit(OWLAnnotationProperty property) {
		return this.getReplacementStrategy().replace(property);
	}

	public OWLObject visit(OWLAnnotation node) {
		return this.getDataFactory().getOWLAnnotation(
				(OWLAnnotationProperty) node.getProperty().accept(this),
				(OWLAnnotationValue) node.getValue().accept(this));
	}

	public OWLObject visit(IRI iri) {
		return this.getReplacementStrategy().replace(iri);
	}

	public OWLObject visit(OWLAnonymousIndividual individual) {
		return individual;
	}

	public OWLObject visit(SWRLClassAtom node) {
		return this.getDataFactory().getSWRLClassAtom(
				(OWLClassExpression) node.getPredicate().accept(this),
				(SWRLIArgument) node.getArgument().accept(this));
	}

	public OWLObject visit(SWRLDataRangeAtom node) {
		return this.getDataFactory().getSWRLDataRangeAtom(
				(OWLDataRange) node.getPredicate().accept(this),
				(SWRLDArgument) node.getArgument().accept(this));
	}

	public OWLObject visit(SWRLObjectPropertyAtom node) {
		return this.getDataFactory().getSWRLObjectPropertyAtom(
				(OWLObjectPropertyExpression) node.getPredicate().accept(this),
				(SWRLIArgument) node.getFirstArgument().accept(this),
				(SWRLIArgument) node.getSecondArgument().accept(this));
	}

	public OWLObject visit(SWRLDataPropertyAtom node) {
		return this.getDataFactory().getSWRLDataPropertyAtom(
				(OWLDataPropertyExpression) node.getPredicate().accept(this),
				(SWRLIArgument) node.getFirstArgument().accept(this),
				(SWRLDArgument) node.getSecondArgument().accept(this));
	}

	public OWLObject visit(SWRLBuiltInAtom node) {
		List<SWRLDArgument> arguments = new ArrayList<SWRLDArgument>(node.getArguments()
				.size());
		for (SWRLDArgument swrldArgument : node.getArguments()) {
			arguments.add((SWRLDArgument) swrldArgument.accept(this));
		}
		return this.getDataFactory().getSWRLBuiltInAtom(node.getPredicate(), arguments);
	}

	public OWLObject visit(SWRLVariable node) {
		return node;
	}

	public OWLObject visit(SWRLIndividualArgument node) {
		return this.getDataFactory().getSWRLIndividualArgument(
				(OWLIndividual) node.getIndividual().accept(this));
	}

	public OWLObject visit(SWRLLiteralArgument node) {
		return this.getDataFactory().getSWRLLiteralArgument(
				(OWLLiteral) node.getLiteral().accept(this));
	}

	public OWLObject visit(SWRLSameIndividualAtom node) {
		return this.getDataFactory().getSWRLSameIndividualAtom(
				(SWRLIArgument) node.getFirstArgument().accept(this),
				(SWRLIArgument) node.getSecondArgument().accept(this));
	}

	public OWLObject visit(SWRLDifferentIndividualsAtom node) {
		return this.getDataFactory().getSWRLDifferentIndividualsAtom(
				(SWRLIArgument) node.getFirstArgument().accept(this),
				(SWRLIArgument) node.getSecondArgument().accept(this));
	}

	public OWLObject visit(OWLOntology ontology) {
		return ontology;
	}

	/**
	 * @return the dataFactory
	 */
	public OWLDataFactory getDataFactory() {
		return this.dataFactory;
	}

	/**
	 * @return the replacementStrategy
	 */
	public ReplacementStrategy getReplacementStrategy() {
		return this.replacementStrategy;
	}
}
