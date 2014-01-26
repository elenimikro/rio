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

final class StructuralComparison implements
        OWLObjectVisitorEx<StructuralDifferenceReport> {
    private final OWLObject objectToCompare;
    private final List<Integer> position = new ArrayList<Integer>();
    private final StructuralDifference difference;

    /** @param owlObject
     *            owlObject
     * @param position
     *            position */
    public StructuralComparison(final OWLObject owlObject, final List<Integer> position) {
        if (owlObject == null) {
            throw new NullPointerException("The OWL Object cannot be null");
        }
        if (position == null) {
            throw new NullPointerException("The position cannot be null");
        }
        objectToCompare = owlObject;
        this.position.addAll(position);
        difference = new StructuralDifference(position);
    }

    /** @return the objectToCompare */
    public OWLObject getOWLObject() {
        return objectToCompare;
    }

    @Override
    public StructuralDifferenceReport visit(final OWLSubClassOfAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLSubClassOfAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getSubClass(),
                                axiom.getSubClass()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getSuperClass(),
                                axiom.getSuperClass()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    protected <O extends Collection<P>, P extends OWLObject> StructuralDifferenceReport
            compareCollection(final O aCollection, final O anotherCollection) {
        return this.compareCollection(aCollection, anotherCollection, 0);
    }

    protected <O extends Collection<P>, P extends OWLObject> StructuralDifferenceReport
            compareCollection(final O aCollection, final O anotherCollection,
                    final int startIndex) {
        StructuralDifferenceReport toReturn = aCollection.size() == anotherCollection
                .size() ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
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

    protected StructuralDifferenceReport compare(final List<SimplePair<OWLObject>> pairs) {
        return this.compare(pairs, 0);
    }

    protected StructuralDifferenceReport compare(final List<SimplePair<OWLObject>> pairs,
            int startIndex) {
        StructuralDifferenceReport toReturn = StructuralDifferenceReport.NO_DIFFERENCE;
        Iterator<SimplePair<OWLObject>> iterator = pairs.iterator();
        StructuralDifference structDifference = new StructuralDifference(getPosition());
        while (toReturn == StructuralDifferenceReport.NO_DIFFERENCE && iterator.hasNext()) {
            SimplePair<OWLObject> pair = iterator.next();
            OWLObject first = pair.getFirst();
            OWLObject second = pair.getSecond();
            toReturn = structDifference.getTopDifference(first, second);
            startIndex++;
        }
        final List<Integer> newPositions = getPosition();
        if (toReturn != StructuralDifferenceReport.NO_DIFFERENCE) {
            newPositions.add(startIndex);
            toReturn.accept(new StructuralDifferenceReportVisitorAdapter() {
                @Override
                public
                        void
                        visitSomeDifferenceStructuralDifferenceReport(
                                final SomeDifferenceStructuralDifferenceReport someDifferenceStructuralDifferenceReport) {
                    newPositions.addAll(someDifferenceStructuralDifferenceReport
                            .getPosition());
                }
            });
            toReturn = SomeDifferenceStructuralDifferenceReport.build(newPositions);
        }
        return toReturn;
    }

    @Override
    public StructuralDifferenceReport visit(
            final OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLNegativeDataPropertyAssertionAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getSubject(), axiom
                                .getSubject()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getObject(), axiom
                                .getObject()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLAsymmetricObjectPropertyAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLAsymmetricObjectPropertyAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLReflexiveObjectPropertyAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLAsymmetricObjectPropertyAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDisjointClassesAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDisjointClassesAxiom owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getClassExpressions(),
                                axiom.getClassExpressions());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataPropertyDomainAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDataPropertyDomainAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getDomain(), axiom
                                .getDomain()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectPropertyDomainAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectPropertyDomainAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getDomain(), axiom
                                .getDomain()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport
            visit(final OWLEquivalentObjectPropertiesAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLEquivalentObjectPropertiesAxiom owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getProperties(), axiom.getProperties());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(
            final OWLNegativeDataPropertyAssertionAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLNegativeDataPropertyAssertionAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getSubject(), axiom
                                .getSubject()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getObject(), axiom
                                .getObject()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDifferentIndividualsAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDifferentIndividualsAxiom owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getIndividuals(), axiom.getIndividuals());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDisjointDataPropertiesAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDisjointDataPropertiesAxiom owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getProperties(), axiom.getProperties());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDisjointObjectPropertiesAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDisjointObjectPropertiesAxiom owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getProperties(), axiom.getProperties());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectPropertyRangeAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectPropertyRangeAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getRange(), axiom
                                .getRange()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectPropertyAssertionAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectPropertyAssertionAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getSubject(), axiom
                                .getSubject()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getObject(), axiom
                                .getObject()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLFunctionalObjectPropertyAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLFunctionalObjectPropertyAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLSubObjectPropertyOfAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLSubObjectPropertyOfAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getSubProperty(),
                                axiom.getSubProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getSuperProperty(),
                                axiom.getSuperProperty()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDisjointUnionAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDisjointUnionAxiom owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getClassExpressions(),
                                axiom.getClassExpressions());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDeclarationAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDeclarationAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getEntity(), axiom
                                .getEntity()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLAnnotationAssertionAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLAnnotationAssertionAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getSubject(), axiom
                                .getSubject()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getAnnotation(),
                                axiom.getAnnotation()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLSymmetricObjectPropertyAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLSymmetricObjectPropertyAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataPropertyRangeAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDataPropertyRangeAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getRange(), axiom
                                .getRange()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLFunctionalDataPropertyAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLFunctionalDataPropertyAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLEquivalentDataPropertiesAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLEquivalentDataPropertiesAxiom owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getProperties(), axiom.getProperties());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLClassAssertionAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLClassAssertionAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject
                                .getClassExpression(), axiom.getClassExpression()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getIndividual(),
                                axiom.getIndividual()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLEquivalentClassesAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLEquivalentClassesAxiom owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getClassExpressions(),
                                axiom.getClassExpressions());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataPropertyAssertionAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDataPropertyAssertionAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getSubject(), axiom
                                .getSubject()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getObject(), axiom
                                .getObject()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLTransitiveObjectPropertyAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLTransitiveObjectPropertyAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport
            visit(final OWLIrreflexiveObjectPropertyAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLIrreflexiveObjectPropertyAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLSubDataPropertyOfAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLSubDataPropertyOfAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getSubProperty(),
                                axiom.getSubProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getSuperProperty(),
                                axiom.getSuperProperty()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(
            final OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLInverseFunctionalObjectPropertyAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLSameIndividualAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLSameIndividualAxiom owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getIndividuals(), axiom.getIndividuals());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLSubPropertyChainOfAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLSubPropertyChainOfAxiom owlObject) {
                        StructuralDifferenceReport toReturn = StructuralComparison.this
                                .compareCollection(owlObject.getPropertyChain(),
                                        axiom.getPropertyChain());
                        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                            List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                            pairs.add(new SimplePair<OWLObject>(owlObject
                                    .getSuperProperty(), axiom.getSuperProperty()));
                            toReturn = StructuralComparison.this.compare(pairs, owlObject
                                    .getPropertyChain().size());
                        }
                        return toReturn;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLInverseObjectPropertiesAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLInverseObjectPropertiesAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getFirstProperty(),
                                axiom.getFirstProperty()));
                        pairs.add(new SimplePair<OWLObject>(
                                owlObject.getSecondProperty(), axiom.getSecondProperty()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLHasKeyAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport
                            visit(final OWLHasKeyAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject
                                .getClassExpression(), axiom.getClassExpression()));
                        StructuralDifferenceReport toReturn = StructuralComparison.this
                                .compare(pairs);
                        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                            toReturn = StructuralComparison.this.compareCollection(
                                    owlObject.getPropertyExpressions(),
                                    axiom.getPropertyExpressions(), 1);
                        }
                        return toReturn;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDatatypeDefinitionAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDatatypeDefinitionAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getDatatype(),
                                axiom.getDatatype()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getDataRange(),
                                axiom.getDataRange()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLRule rule) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(final SWRLRule owlObject) {
                        StructuralDifferenceReport toReturn = StructuralComparison.this
                                .compareCollection(owlObject.getHead(), rule.getHead());
                        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                            toReturn = StructuralComparison.this.compareCollection(
                                    owlObject.getBody(), rule.getBody(), owlObject
                                            .getHead().size());
                        }
                        return toReturn;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLSubAnnotationPropertyOfAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLSubAnnotationPropertyOfAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getSubProperty(),
                                axiom.getSubProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getSuperProperty(),
                                axiom.getSuperProperty()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLAnnotationPropertyDomainAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLAnnotationPropertyDomainAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getDomain(), axiom
                                .getDomain()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLAnnotationPropertyRangeAxiom axiom) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLAnnotationPropertyRangeAxiom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(),
                                axiom.getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getRange(), axiom
                                .getRange()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLClass ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(final OWLClass owlObject) {
                        return owlObject.equals(ce) ? StructuralDifferenceReport.NO_DIFFERENCE
                                : StructuralDifferenceReport.INCOMPARABLE;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectIntersectionOf ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectIntersectionOf owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getOperands(), ce.getOperands());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectUnionOf ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectUnionOf owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getOperands(), ce.getOperands());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectComplementOf ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectComplementOf owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getOperand(), ce
                                .getOperand()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectSomeValuesFrom ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectSomeValuesFrom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
                                .getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), ce
                                .getFiller()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectAllValuesFrom ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectAllValuesFrom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
                                .getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), ce
                                .getFiller()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectHasValue ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectHasValue owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
                                .getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getValue(), ce
                                .getValue()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectMinCardinality ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectMinCardinality owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
                                .getProperty()));
                        StructuralDifferenceReport toReturn = StructuralComparison.this
                                .compare(pairs);
                        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                            if (owlObject.getCardinality() == ce.getCardinality()) {
                                pairs.clear();
                                pairs.add(new SimplePair<OWLObject>(
                                        owlObject.getFiller(), ce.getFiller()));
                                toReturn = StructuralComparison.this.compare(pairs, 2);
                            } else {
                                List<Integer> newPositions = StructuralComparison.this
                                        .getPosition();
                                newPositions.add(2);
                                toReturn = SomeDifferenceStructuralDifferenceReport
                                        .build(newPositions);
                            }
                        }
                        return toReturn;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectExactCardinality ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectExactCardinality owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
                                .getProperty()));
                        StructuralDifferenceReport toReturn = StructuralComparison.this
                                .compare(pairs);
                        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                            if (owlObject.getCardinality() == ce.getCardinality()) {
                                pairs.clear();
                                pairs.add(new SimplePair<OWLObject>(
                                        owlObject.getFiller(), ce.getFiller()));
                                toReturn = StructuralComparison.this.compare(pairs, 2);
                            } else {
                                List<Integer> newPositions = StructuralComparison.this
                                        .getPosition();
                                newPositions.add(2);
                                toReturn = SomeDifferenceStructuralDifferenceReport
                                        .build(newPositions);
                            }
                        }
                        return toReturn;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectMaxCardinality ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectMaxCardinality owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
                                .getProperty()));
                        StructuralDifferenceReport toReturn = StructuralComparison.this
                                .compare(pairs);
                        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                            if (owlObject.getCardinality() == ce.getCardinality()) {
                                pairs.clear();
                                pairs.add(new SimplePair<OWLObject>(
                                        owlObject.getFiller(), ce.getFiller()));
                                toReturn = StructuralComparison.this.compare(pairs, 2);
                            } else {
                                List<Integer> newPositions = StructuralComparison.this
                                        .getPosition();
                                newPositions.add(2);
                                toReturn = SomeDifferenceStructuralDifferenceReport
                                        .build(newPositions);
                            }
                        }
                        return toReturn;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectHasSelf ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectHasSelf owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
                                .getProperty()));
                        StructuralDifferenceReport toReturn = StructuralComparison.this
                                .compare(pairs);
                        return toReturn;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectOneOf ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport
                            visit(final OWLObjectOneOf owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getIndividuals(), ce.getIndividuals());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataSomeValuesFrom ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDataSomeValuesFrom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
                                .getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), ce
                                .getFiller()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataAllValuesFrom ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDataAllValuesFrom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
                                .getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), ce
                                .getFiller()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataHasValue ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDataHasValue owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
                                .getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getValue(), ce
                                .getValue()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataMinCardinality ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDataMinCardinality owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
                                .getProperty()));
                        StructuralDifferenceReport toReturn = StructuralComparison.this
                                .compare(pairs);
                        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                            if (owlObject.getCardinality() == ce.getCardinality()) {
                                pairs.clear();
                                pairs.add(new SimplePair<OWLObject>(
                                        owlObject.getFiller(), ce.getFiller()));
                                toReturn = StructuralComparison.this.compare(pairs, 2);
                            } else {
                                List<Integer> newPositions = StructuralComparison.this
                                        .getPosition();
                                newPositions.add(2);
                                toReturn = SomeDifferenceStructuralDifferenceReport
                                        .build(newPositions);
                            }
                        }
                        return toReturn;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataExactCardinality ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDataExactCardinality owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
                                .getProperty()));
                        StructuralDifferenceReport toReturn = StructuralComparison.this
                                .compare(pairs);
                        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                            if (owlObject.getCardinality() == ce.getCardinality()) {
                                pairs.clear();
                                pairs.add(new SimplePair<OWLObject>(
                                        owlObject.getFiller(), ce.getFiller()));
                                toReturn = StructuralComparison.this.compare(pairs, 2);
                            } else {
                                List<Integer> newPositions = StructuralComparison.this
                                        .getPosition();
                                newPositions.add(2);
                                toReturn = SomeDifferenceStructuralDifferenceReport
                                        .build(newPositions);
                            }
                        }
                        return toReturn;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataMaxCardinality ce) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDataMaxCardinality owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), ce
                                .getProperty()));
                        StructuralDifferenceReport toReturn = StructuralComparison.this
                                .compare(pairs);
                        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                            if (owlObject.getCardinality() == ce.getCardinality()) {
                                pairs.clear();
                                pairs.add(new SimplePair<OWLObject>(
                                        owlObject.getFiller(), ce.getFiller()));
                                toReturn = StructuralComparison.this.compare(pairs, 2);
                            } else {
                                List<Integer> newPositions = StructuralComparison.this
                                        .getPosition();
                                newPositions.add(2);
                                toReturn = SomeDifferenceStructuralDifferenceReport
                                        .build(newPositions);
                            }
                        }
                        return toReturn;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDatatype node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(final OWLDatatype owlObject) {
                        return node.equals(owlObject) ? StructuralDifferenceReport.NO_DIFFERENCE
                                : StructuralDifferenceReport.INCOMPARABLE;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataComplementOf node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDataComplementOf owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getDataRange(),
                                node.getDataRange()));
                        StructuralDifferenceReport toReturn = StructuralComparison.this
                                .compare(pairs);
                        return toReturn;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataOneOf node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(final OWLDataOneOf owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getValues(), node.getValues());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataIntersectionOf node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDataIntersectionOf owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getOperands(), node.getOperands());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataUnionOf node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport
                            visit(final OWLDataUnionOf owlObject) {
                        return StructuralComparison.this.compareCollection(
                                owlObject.getOperands(), node.getOperands());
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDatatypeRestriction node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDatatypeRestriction owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getDatatype(), node
                                .getDatatype()));
                        StructuralDifferenceReport toReturn = StructuralComparison.this
                                .compare(pairs);
                        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                            toReturn = StructuralComparison.this.compareCollection(
                                    owlObject.getFacetRestrictions(),
                                    node.getFacetRestrictions(), 1);
                        }
                        return toReturn;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLLiteral node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(final OWLLiteral literal) {
                        return literal.equals(node) ? StructuralDifferenceReport.NO_DIFFERENCE
                                : StructuralDifferenceReport.INCOMPARABLE;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLFacetRestriction node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLFacetRestriction owlObject) {
                        StructuralDifferenceReport toReturn = owlObject.getFacet()
                                .equals(node.getFacet()) ? StructuralDifferenceReport.NO_DIFFERENCE
                                : StructuralDifferenceReport.INCOMPARABLE;
                        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                            List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                            pairs.add(new SimplePair<OWLObject>(
                                    owlObject.getFacetValue(), node.getFacetValue()));
                            toReturn = StructuralComparison.this.compare(pairs, 1);
                        }
                        return toReturn;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectProperty property) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectProperty owlObject) {
                        return owlObject.equals(property) ? StructuralDifferenceReport.NO_DIFFERENCE
                                : StructuralDifferenceReport.INCOMPARABLE;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectInverseOf property) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLObjectInverseOf owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject
                                .getInverseProperty(), property.getInverseProperty()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataProperty property) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLDataProperty owlObject) {
                        return owlObject.equals(property) ? StructuralDifferenceReport.NO_DIFFERENCE
                                : StructuralDifferenceReport.INCOMPARABLE;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLNamedIndividual individual) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLNamedIndividual owlObject) {
                        return owlObject.equals(individual) ? StructuralDifferenceReport.NO_DIFFERENCE
                                : StructuralDifferenceReport.INCOMPARABLE;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLAnnotationProperty property) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLAnnotationProperty owlObject) {
                        return owlObject.equals(property) ? StructuralDifferenceReport.NO_DIFFERENCE
                                : StructuralDifferenceReport.INCOMPARABLE;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLAnnotation node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport
                            visit(final OWLAnnotation owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getProperty(), node
                                .getProperty()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getValue(), node
                                .getValue()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final IRI iri) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(final IRI owlObject) {
                        return owlObject.equals(iri) ? StructuralDifferenceReport.NO_DIFFERENCE
                                : StructuralDifferenceReport.INCOMPARABLE;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLAnonymousIndividual individual) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final OWLAnonymousIndividual owlObject) {
                        return owlObject.equals(individual) ? StructuralDifferenceReport.NO_DIFFERENCE
                                : StructuralDifferenceReport.INCOMPARABLE;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLClassAtom node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport
                            visit(final SWRLClassAtom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getPredicate(),
                                node.getPredicate()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getArgument(), node
                                .getArgument()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLDataRangeAtom node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final SWRLDataRangeAtom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getPredicate(),
                                node.getPredicate()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getArgument(), node
                                .getArgument()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLObjectPropertyAtom node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final SWRLObjectPropertyAtom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getPredicate(),
                                node.getPredicate()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getFirstArgument(),
                                node.getFirstArgument()));
                        pairs.add(new SimplePair<OWLObject>(
                                owlObject.getSecondArgument(), node.getSecondArgument()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLDataPropertyAtom node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final SWRLDataPropertyAtom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getPredicate(),
                                node.getPredicate()));
                        pairs.add(new SimplePair<OWLObject>(owlObject.getFirstArgument(),
                                node.getFirstArgument()));
                        pairs.add(new SimplePair<OWLObject>(
                                owlObject.getSecondArgument(), node.getSecondArgument()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLBuiltInAtom node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final SWRLBuiltInAtom owlObject) {
                        StructuralDifferenceReport toReturn = StructuralDifferenceReport.NO_DIFFERENCE;
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getPredicate(),
                                node.getPredicate()));
                        toReturn = StructuralComparison.this.compare(pairs);
                        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                            toReturn = StructuralComparison.this.compareCollection(
                                    owlObject.getArguments(), node.getArguments(), 1);
                        }
                        return toReturn;
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLVariable node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(final SWRLVariable owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getIRI(), node
                                .getIRI()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLIndividualArgument node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final SWRLIndividualArgument owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getIndividual(),
                                node.getIndividual()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLLiteralArgument node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final SWRLLiteralArgument owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getLiteral(), node
                                .getLiteral()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLSameIndividualAtom node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final SWRLSameIndividualAtom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getFirstArgument(),
                                node.getFirstArgument()));
                        pairs.add(new SimplePair<OWLObject>(
                                owlObject.getSecondArgument(), node.getSecondArgument()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLDifferentIndividualsAtom node) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(
                            final SWRLDifferentIndividualsAtom owlObject) {
                        List<SimplePair<OWLObject>> pairs = new ArrayList<SimplePair<OWLObject>>();
                        pairs.add(new SimplePair<OWLObject>(owlObject.getFirstArgument(),
                                node.getFirstArgument()));
                        pairs.add(new SimplePair<OWLObject>(
                                owlObject.getSecondArgument(), node.getSecondArgument()));
                        return StructuralComparison.this.compare(pairs);
                    }
                });
    }

    @Override
    public StructuralDifferenceReport visit(final OWLOntology ontology) {
        return getOWLObject().accept(
                new OWLObjectVisitorExAdapter<StructuralDifferenceReport>(
                        StructuralDifferenceReport.INCOMPARABLE) {
                    @Override
                    public StructuralDifferenceReport visit(final OWLOntology owlObject) {
                        return owlObject.equals(ontology) ? StructuralDifferenceReport.NO_DIFFERENCE
                                : StructuralDifferenceReport.INCOMPARABLE;
                    }
                });
    }

    /** @return the position */
    public List<Integer> getPosition() {
        return new ArrayList<Integer>(position);
    }

    /** @return the difference */
    public StructuralDifference getDifference() {
        return difference;
    }
}
