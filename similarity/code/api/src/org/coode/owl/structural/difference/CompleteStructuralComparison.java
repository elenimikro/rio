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
package org.coode.owl.structural.difference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.coode.pair.SimplePair;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

final class CompleteStructuralComparison implements
		OWLObjectVisitorEx<List<StructuralDifferenceReport>> {
	private final OWLObject owlObject;
	private final List<Integer> position = new ArrayList<Integer>();
	private final StructuralDifference difference;

	/**
	 * @param owlObject
	 */
	public CompleteStructuralComparison(OWLObject owlObject, List<Integer> position) {
		if (owlObject == null) {
			throw new NullPointerException("The OWL Object cannot be null");
		}
		if (position == null) {
			throw new NullPointerException("The position cannot be null");
		}
		this.owlObject = owlObject;
		this.position.addAll(position);
		this.difference = new StructuralDifference(position);
	}

	/**
	 * @return the owlObject
	 */
	public OWLObject getOWLObject() {
		return this.owlObject;
	}

	public List<StructuralDifferenceReport> visit(final OWLSubClassOfAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLSubClassOfAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getSubClass(),
								axiom.getSubClass()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getSuperClass(),
								axiom.getSuperClass()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	protected <O extends Collection<P>, P extends OWLObject> List<StructuralDifferenceReport> compareCollection(
			O aCollection, O anotherCollection) {
		return this.compareCollection(aCollection, anotherCollection, 0);
	}

	protected <O extends Collection<P>, P extends OWLObject> List<StructuralDifferenceReport> compareCollection(
			O aCollection, O anotherCollection, int startIndex) {
		boolean sizeMatch = aCollection.size() == anotherCollection.size();
		List<StructuralDifferenceReport> toReturn = sizeMatch ? Collections
				.<StructuralDifferenceReport> emptyList()
				: new ArrayList<StructuralDifferenceReport>(
						Collections.singleton(StructuralDifferenceReport.INCOMPARABLE));
		if (sizeMatch) {
			Iterator<P> iterator = aCollection.iterator();
			Iterator<P> anotherIterator = anotherCollection.iterator();
			List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>(
					aCollection.size());
			while (iterator.hasNext()) {
				P first = iterator.next();
				P second = anotherIterator.next();
				pairs.add(new SimplePair<OWLObject>(first, second));
			}
			toReturn = this.compare(pairs, startIndex);
		}
		return toReturn;
	}

	protected List<StructuralDifferenceReport> compare(List<SimplePair<OWLObject>> pairs) {
		return this.compare(pairs, 0);
	}

	protected List<StructuralDifferenceReport> compare(List<SimplePair<OWLObject>> pairs,
			int startIndex) {
		Iterator<SimplePair<OWLObject>> iterator = pairs.iterator();
		StructuralDifference difference = new StructuralDifference(this.getPosition());
		List<StructuralDifferenceReport> toReturn = new ArrayList<StructuralDifferenceReport>();
		while (iterator.hasNext()) {
			SimplePair<OWLObject> pair = iterator.next();
			OWLObject first = pair.getFirst();
			OWLObject second = pair.getSecond();
			List<StructuralDifferenceReport> differenceReports = difference
					.getTopDifferences(first, second);
			startIndex++;
			if (!differenceReports.isEmpty()) {
				for (StructuralDifferenceReport differenceReport : differenceReports) {
					final List<Integer> position = this.getPosition();
					position.add(startIndex);
					differenceReport
							.accept(new StructuralDifferenceReportVisitorAdapter() {
								@Override
								public void visitSomeDifferenceStructuralDifferenceReport(
										SomeDifferenceStructuralDifferenceReport someDifferenceStructuralDifferenceReport) {
									position.addAll(someDifferenceStructuralDifferenceReport
											.getPosition());
								}
							});
					StructuralDifferenceReport newDifferenceReport = SomeDifferenceStructuralDifferenceReport
							.build(position);
					toReturn.add(newDifferenceReport);
				}
			}
		}
		return toReturn;
	}

	public List<StructuralDifferenceReport> visit(
			final OWLNegativeObjectPropertyAssertionAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLNegativeDataPropertyAssertionAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getSubject(), axiom
								.getSubject()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getObject(), axiom
								.getObject()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLAsymmetricObjectPropertyAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLAsymmetricObjectPropertyAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLReflexiveObjectPropertyAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLAsymmetricObjectPropertyAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDisjointClassesAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDisjointClassesAxiom owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getClassExpressions(),
								axiom.getClassExpressions());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDataPropertyDomainAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDataPropertyDomainAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getDomain(), axiom
								.getDomain()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectPropertyDomainAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLObjectPropertyDomainAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getDomain(), axiom
								.getDomain()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLEquivalentObjectPropertiesAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLEquivalentObjectPropertiesAxiom owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getProperties(), axiom.getProperties());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLNegativeDataPropertyAssertionAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLNegativeDataPropertyAssertionAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getSubject(), axiom
								.getSubject()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getObject(), axiom
								.getObject()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDifferentIndividualsAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDifferentIndividualsAxiom owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getIndividuals(), axiom.getIndividuals());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLDisjointDataPropertiesAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDisjointDataPropertiesAxiom owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getProperties(), axiom.getProperties());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLDisjointObjectPropertiesAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDisjointObjectPropertiesAxiom owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getProperties(), axiom.getProperties());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectPropertyRangeAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLObjectPropertyRangeAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getRange(), axiom
								.getRange()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLObjectPropertyAssertionAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLObjectPropertyAssertionAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getSubject(), axiom
								.getSubject()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getObject(), axiom
								.getObject()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLFunctionalObjectPropertyAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLFunctionalObjectPropertyAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLSubObjectPropertyOfAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLSubObjectPropertyOfAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getSubProperty(),
								axiom.getSubProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getSuperProperty(),
								axiom.getSuperProperty()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDisjointUnionAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDisjointUnionAxiom owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getClassExpressions(),
								axiom.getClassExpressions());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDeclarationAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDeclarationAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getEntity(), axiom
								.getEntity()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLAnnotationAssertionAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLAnnotationAssertionAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getSubject(), axiom
								.getSubject()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getAnnotation(),
								axiom.getAnnotation()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLSymmetricObjectPropertyAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLSymmetricObjectPropertyAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDataPropertyRangeAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDataPropertyRangeAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getRange(), axiom
								.getRange()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLFunctionalDataPropertyAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLFunctionalDataPropertyAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLEquivalentDataPropertiesAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLEquivalentDataPropertiesAxiom owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getProperties(), axiom.getProperties());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLClassAssertionAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLClassAssertionAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject
								.getClassExpression(), axiom.getClassExpression()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getIndividual(),
								axiom.getIndividual()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLEquivalentClassesAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLEquivalentClassesAxiom owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getClassExpressions(),
								axiom.getClassExpressions());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLDataPropertyAssertionAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDataPropertyAssertionAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getSubject(), axiom
								.getSubject()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getObject(), axiom
								.getObject()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLTransitiveObjectPropertyAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLTransitiveObjectPropertyAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLIrreflexiveObjectPropertyAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLIrreflexiveObjectPropertyAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLSubDataPropertyOfAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLSubDataPropertyOfAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getSubProperty(),
								axiom.getSubProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getSuperProperty(),
								axiom.getSuperProperty()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLInverseFunctionalObjectPropertyAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLInverseFunctionalObjectPropertyAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLSameIndividualAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLSameIndividualAxiom owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getIndividuals(), axiom.getIndividuals());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLSubPropertyChainOfAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLSubPropertyChainOfAxiom owlObject) {
						List<StructuralDifferenceReport> toReturn = CompleteStructuralComparison.this
								.compareCollection(owlObject.getPropertyChain(),
										axiom.getPropertyChain());
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getSuperProperty(),
								axiom.getSuperProperty()));
						toReturn.addAll(CompleteStructuralComparison.this.compare(pairs,
								owlObject.getPropertyChain().size()));
						return toReturn;
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLInverseObjectPropertiesAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLInverseObjectPropertiesAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getFirstProperty(),
								axiom.getFirstProperty()));
						pairs.add(new SimplePair<OWLObject>(
								owlObject.getSecondProperty(), axiom.getSecondProperty()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLHasKeyAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(OWLHasKeyAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject
								.getClassExpression(), axiom.getClassExpression()));
						List<StructuralDifferenceReport> toReturn = CompleteStructuralComparison.this
								.compare(pairs);
						toReturn.addAll(CompleteStructuralComparison.this
								.compareCollection(owlObject.getPropertyExpressions(),
										axiom.getPropertyExpressions(), 1));
						return toReturn;
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDatatypeDefinitionAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDatatypeDefinitionAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getDatatype(),
								axiom.getDatatype()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getDataRange(),
								axiom.getDataRange()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final SWRLRule rule) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(SWRLRule owlObject) {
						List<StructuralDifferenceReport> toReturn = CompleteStructuralComparison.this
								.compareCollection(owlObject.getHead(), rule.getHead());
						toReturn.addAll(CompleteStructuralComparison.this
								.compareCollection(owlObject.getBody(), rule.getBody(),
										owlObject.getHead().size()));
						return toReturn;
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLSubAnnotationPropertyOfAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLSubAnnotationPropertyOfAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getSubProperty(),
								axiom.getSubProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getSuperProperty(),
								axiom.getSuperProperty()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLAnnotationPropertyDomainAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLAnnotationPropertyDomainAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getDomain(), axiom
								.getDomain()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(
			final OWLAnnotationPropertyRangeAxiom axiom) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLAnnotationPropertyRangeAxiom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
								axiom.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getRange(), axiom
								.getRange()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLClass ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(OWLClass owlObject) {
						return owlObject.equals(ce) ? Collections
								.<StructuralDifferenceReport> emptyList()
								: new ArrayList<StructuralDifferenceReport>(
										Collections
												.singleton(StructuralDifferenceReport.INCOMPARABLE));
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectIntersectionOf ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLObjectIntersectionOf owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getOperands(), ce.getOperands());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectUnionOf ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							final OWLObjectUnionOf owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getOperands(), ce.getOperands());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectComplementOf ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLObjectComplementOf owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getOperand(), ce
								.getOperand()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectSomeValuesFrom ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLObjectSomeValuesFrom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
								.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), ce
								.getFiller()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectAllValuesFrom ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLObjectAllValuesFrom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
								.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), ce
								.getFiller()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectHasValue ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLObjectHasValue owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
								.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getValue(), ce
								.getValue()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectMinCardinality ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLObjectMinCardinality owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
								.getProperty()));
						List<StructuralDifferenceReport> toReturn = CompleteStructuralComparison.this
								.compare(pairs);
						if (owlObject.getCardinality() != ce.getCardinality()) {
							List<Integer> position = CompleteStructuralComparison.this
									.getPosition();
							position.add(2);
							toReturn.add(SomeDifferenceStructuralDifferenceReport
									.build(position));
						}
						pairs.clear();
						pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), ce
								.getFiller()));
						toReturn.addAll(CompleteStructuralComparison.this.compare(pairs,
								2));
						return toReturn;
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectExactCardinality ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLObjectExactCardinality owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
								.getProperty()));
						List<StructuralDifferenceReport> toReturn = CompleteStructuralComparison.this
								.compare(pairs);
						if (owlObject.getCardinality() != ce.getCardinality()) {
							List<Integer> position = CompleteStructuralComparison.this
									.getPosition();
							position.add(2);
							toReturn.add(SomeDifferenceStructuralDifferenceReport
									.build(position));
						}
						pairs.clear();
						pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), ce
								.getFiller()));
						toReturn.addAll(CompleteStructuralComparison.this.compare(pairs,
								2));
						return toReturn;
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectMaxCardinality ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLObjectMaxCardinality owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
								.getProperty()));
						List<StructuralDifferenceReport> toReturn = CompleteStructuralComparison.this
								.compare(pairs);
						if (owlObject.getCardinality() != ce.getCardinality()) {
							List<Integer> position = CompleteStructuralComparison.this
									.getPosition();
							position.add(2);
							toReturn.add(SomeDifferenceStructuralDifferenceReport
									.build(position));
						}
						pairs.clear();
						pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), ce
								.getFiller()));
						toReturn.addAll(CompleteStructuralComparison.this.compare(pairs,
								2));
						return toReturn;
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectHasSelf ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLObjectHasSelf owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
								.getProperty()));
						List<StructuralDifferenceReport> toReturn = CompleteStructuralComparison.this
								.compare(pairs);
						return toReturn;
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectOneOf ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(OWLObjectOneOf owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getIndividuals(), ce.getIndividuals());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDataSomeValuesFrom ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDataSomeValuesFrom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
								.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), ce
								.getFiller()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDataAllValuesFrom ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDataAllValuesFrom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
								.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), ce
								.getFiller()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDataHasValue ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDataHasValue owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
								.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getValue(), ce
								.getValue()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDataMinCardinality ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDataMinCardinality owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
								.getProperty()));
						List<StructuralDifferenceReport> toReturn = CompleteStructuralComparison.this
								.compare(pairs);
						if (owlObject.getCardinality() != ce.getCardinality()) {
							List<Integer> position = CompleteStructuralComparison.this
									.getPosition();
							position.add(2);
							toReturn.add(SomeDifferenceStructuralDifferenceReport
									.build(position));
						}
						pairs.clear();
						pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), ce
								.getFiller()));
						toReturn.addAll(CompleteStructuralComparison.this.compare(pairs,
								2));
						return toReturn;
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDataExactCardinality ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDataExactCardinality owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
								.getProperty()));
						List<StructuralDifferenceReport> toReturn = CompleteStructuralComparison.this
								.compare(pairs);
						if (owlObject.getCardinality() != ce.getCardinality()) {
							List<Integer> position = CompleteStructuralComparison.this
									.getPosition();
							position.add(2);
							toReturn.add(SomeDifferenceStructuralDifferenceReport
									.build(position));
						}
						pairs.clear();
						pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), ce
								.getFiller()));
						toReturn.addAll(CompleteStructuralComparison.this.compare(pairs,
								2));
						return toReturn;
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDataMaxCardinality ce) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDataMaxCardinality owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
								.getProperty()));
						List<StructuralDifferenceReport> toReturn = CompleteStructuralComparison.this
								.compare(pairs);
						if (owlObject.getCardinality() != ce.getCardinality()) {
							List<Integer> position = CompleteStructuralComparison.this
									.getPosition();
							position.add(2);
							toReturn.add(SomeDifferenceStructuralDifferenceReport
									.build(position));
						}
						pairs.clear();
						pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), ce
								.getFiller()));
						toReturn.addAll(CompleteStructuralComparison.this.compare(pairs,
								2));
						return toReturn;
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDatatype node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(OWLDatatype owlObject) {
						return node.equals(owlObject) ? Collections
								.<StructuralDifferenceReport> emptyList()
								: new ArrayList<StructuralDifferenceReport>(
										Collections
												.singleton(StructuralDifferenceReport.INCOMPARABLE));
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDataComplementOf node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDataComplementOf owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getDataRange(),
								node.getDataRange()));
						List<StructuralDifferenceReport> toReturn = CompleteStructuralComparison.this
								.compare(pairs);
						return toReturn;
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDataOneOf node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(OWLDataOneOf owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getValues(), node.getValues());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDataIntersectionOf node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDataIntersectionOf owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getOperands(), node.getOperands());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDataUnionOf node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(OWLDataUnionOf owlObject) {
						return CompleteStructuralComparison.this.compareCollection(
								owlObject.getOperands(), node.getOperands());
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDatatypeRestriction node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDatatypeRestriction owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getDatatype(), node
								.getDatatype()));
						List<StructuralDifferenceReport> toReturn = CompleteStructuralComparison.this
								.compare(pairs);
						toReturn.addAll(CompleteStructuralComparison.this
								.compareCollection(owlObject.getFacetRestrictions(),
										node.getFacetRestrictions(), 1));
						return toReturn;
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLLiteral node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(OWLLiteral literal) {
						return literal.equals(node) ? Collections
								.<StructuralDifferenceReport> emptyList()
								: new ArrayList<StructuralDifferenceReport>(
										Collections
												.singleton(StructuralDifferenceReport.INCOMPARABLE));
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLFacetRestriction node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLFacetRestriction owlObject) {
						List<StructuralDifferenceReport> toReturn = owlObject.getFacet()
								.equals(node.getFacet()) ? Collections
								.<StructuralDifferenceReport> emptyList()
								: new ArrayList<StructuralDifferenceReport>(
										Collections
												.singleton(StructuralDifferenceReport.INCOMPARABLE));
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getFacetValue(),
								node.getFacetValue()));
						toReturn.addAll(CompleteStructuralComparison.this.compare(pairs,
								1));
						return toReturn;
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectProperty property) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLObjectProperty owlObject) {
						return owlObject.equals(property) ? Collections
								.<StructuralDifferenceReport> emptyList()
								: new ArrayList<StructuralDifferenceReport>(
										Collections
												.singleton(StructuralDifferenceReport.INCOMPARABLE));
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLObjectInverseOf property) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLObjectInverseOf owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject
								.getInverseProperty(), property.getInverseProperty()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLDataProperty property) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLDataProperty owlObject) {
						return owlObject.equals(property) ? Collections
								.<StructuralDifferenceReport> emptyList()
								: new ArrayList<StructuralDifferenceReport>(
										Collections
												.singleton(StructuralDifferenceReport.INCOMPARABLE));
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLNamedIndividual individual) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLNamedIndividual owlObject) {
						return owlObject.equals(individual) ? Collections
								.<StructuralDifferenceReport> emptyList()
								: new ArrayList<StructuralDifferenceReport>(
										Collections
												.singleton(StructuralDifferenceReport.INCOMPARABLE));
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLAnnotationProperty property) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLAnnotationProperty owlObject) {
						return owlObject.equals(property) ? Collections
								.<StructuralDifferenceReport> emptyList()
								: new ArrayList<StructuralDifferenceReport>(
										Collections
												.singleton(StructuralDifferenceReport.INCOMPARABLE));
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLAnnotation node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(OWLAnnotation owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), node
								.getProperty()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getValue(), node
								.getValue()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final IRI iri) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(IRI owlObject) {
						return owlObject.equals(iri) ? Collections
								.<StructuralDifferenceReport> emptyList()
								: new ArrayList<StructuralDifferenceReport>(
										Collections
												.singleton(StructuralDifferenceReport.INCOMPARABLE));
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLAnonymousIndividual individual) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							OWLAnonymousIndividual owlObject) {
						return owlObject.equals(individual) ? Collections
								.<StructuralDifferenceReport> emptyList()
								: new ArrayList<StructuralDifferenceReport>(
										Collections
												.singleton(StructuralDifferenceReport.INCOMPARABLE));
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final SWRLClassAtom node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(SWRLClassAtom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getPredicate(),
								node.getPredicate()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getArgument(), node
								.getArgument()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final SWRLDataRangeAtom node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							SWRLDataRangeAtom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getPredicate(),
								node.getPredicate()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getArgument(), node
								.getArgument()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final SWRLObjectPropertyAtom node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							SWRLObjectPropertyAtom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getPredicate(),
								node.getPredicate()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getFirstArgument(),
								node.getFirstArgument()));
						pairs.add(new SimplePair<OWLObject>(
								owlObject.getSecondArgument(), node.getSecondArgument()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final SWRLDataPropertyAtom node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							SWRLDataPropertyAtom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getPredicate(),
								node.getPredicate()));
						pairs.add(new SimplePair<OWLObject>(owlObject.getFirstArgument(),
								node.getFirstArgument()));
						pairs.add(new SimplePair<OWLObject>(
								owlObject.getSecondArgument(), node.getSecondArgument()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final SWRLBuiltInAtom node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							SWRLBuiltInAtom owlObject) {
						List<StructuralDifferenceReport> toReturn = Collections
								.<StructuralDifferenceReport> emptyList();
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getPredicate(),
								node.getPredicate()));
						toReturn.addAll(CompleteStructuralComparison.this.compare(pairs));
						toReturn.addAll(CompleteStructuralComparison.this
								.compareCollection(owlObject.getArguments(),
										node.getArguments(), 1));
						return toReturn;
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final SWRLVariable node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(SWRLVariable owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getIRI(), node
								.getIRI()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final SWRLIndividualArgument node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							SWRLIndividualArgument owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getIndividual(),
								node.getIndividual()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final SWRLLiteralArgument node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							SWRLLiteralArgument owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getLiteral(), node
								.getLiteral()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final SWRLSameIndividualAtom node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							SWRLSameIndividualAtom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getFirstArgument(),
								node.getFirstArgument()));
						pairs.add(new SimplePair<OWLObject>(
								owlObject.getSecondArgument(), node.getSecondArgument()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final SWRLDifferentIndividualsAtom node) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(
							SWRLDifferentIndividualsAtom owlObject) {
						List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
						pairs.add(new SimplePair<OWLObject>(owlObject.getFirstArgument(),
								node.getFirstArgument()));
						pairs.add(new SimplePair<OWLObject>(
								owlObject.getSecondArgument(), node.getSecondArgument()));
						return CompleteStructuralComparison.this.compare(pairs);
					}
				});
	}

	public List<StructuralDifferenceReport> visit(final OWLOntology ontology) {
		return this.getOWLObject().accept(
				new OWLObjectVisitorExAdapter<List<StructuralDifferenceReport>>(
						Collections.<StructuralDifferenceReport> emptyList()) {
					@Override
					public List<StructuralDifferenceReport> visit(OWLOntology owlObject) {
						return owlObject.equals(ontology) ? Collections
								.<StructuralDifferenceReport> emptyList()
								: new ArrayList<StructuralDifferenceReport>(
										Collections
												.singleton(StructuralDifferenceReport.INCOMPARABLE));
					}
				});
	}

	/**
	 * @return the position
	 */
	public List<Integer> getPosition() {
		return new ArrayList<Integer>(this.position);
	}

	/**
	 * @return the difference
	 */
	public StructuralDifference getDifference() {
		return this.difference;
	}
}
