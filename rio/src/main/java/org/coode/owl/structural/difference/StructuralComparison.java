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
import org.semanticweb.owlapi.model.OWLCardinalityRestriction;
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

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

final class StructuralComparison implements OWLObjectVisitorEx<StructuralDifferenceReport> {
    private final OWLObject objectToCompare;
    protected final TIntList position = new TIntArrayList();
    private final StructuralDifference difference;

    /**
     * @param owlObject owlObject
     * @param position position
     */
    public StructuralComparison(OWLObject owlObject, TIntList position) {
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
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    // XXX is this the same as iterating through components()?
    class StructuralDiffVisitor implements OWLObjectVisitorEx<StructuralDifferenceReport> {

        private OWLObject arg;

        public StructuralDiffVisitor(OWLObject arg) {
            this.arg = arg;
        }

        @Override
        public <T> StructuralDifferenceReport doDefault(T object) {
            return StructuralDifferenceReport.INCOMPARABLE;
        }

        private <T extends OWLObject> T arg(T o) {
            return (T) o.getClass().cast(arg);
        }

        protected <P extends OWLObject> SimplePair<OWLObject> p(P first, P second) {
            return new SimplePair<>(first, second);
        }

        @Override
        public StructuralDifferenceReport visit(OWLSubClassOfAxiom owlObject) {
            OWLSubClassOfAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getSubClass(), arg2.getSubClass()));
            pairs.add(p(owlObject.getSuperClass(), arg2.getSuperClass()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLNegativeDataPropertyAssertionAxiom owlObject) {
            OWLNegativeDataPropertyAssertionAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getSubject(), arg2.getSubject()));
            pairs.add(p(owlObject.getObject(), arg2.getObject()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLAsymmetricObjectPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public StructuralDifferenceReport visit(OWLReflexiveObjectPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDisjointClassesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataPropertyDomainAxiom owlObject) {
            OWLDataPropertyDomainAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getDomain(), arg2.getDomain()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectPropertyDomainAxiom owlObject) {
            OWLObjectPropertyDomainAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getDomain(), arg2.getDomain()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLEquivalentObjectPropertiesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDifferentIndividualsAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDisjointDataPropertiesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDisjointObjectPropertiesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectPropertyRangeAxiom owlObject) {
            OWLObjectPropertyRangeAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getRange(), arg2.getRange()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectPropertyAssertionAxiom owlObject) {
            OWLObjectPropertyAssertionAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getSubject(), arg2.getSubject()));
            pairs.add(p(owlObject.getObject(), arg2.getObject()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLFunctionalObjectPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public StructuralDifferenceReport visit(OWLSubObjectPropertyOfAxiom owlObject) {
            OWLSubObjectPropertyOfAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getSubProperty(), arg2.getSubProperty()));
            pairs.add(p(owlObject.getSuperProperty(), arg2.getSuperProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLDisjointUnionAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDeclarationAxiom owlObject) {
            return compare(owlObject.getEntity(), arg(owlObject).getEntity());
        }

        @Override
        public StructuralDifferenceReport visit(OWLAnnotationAssertionAxiom owlObject) {
            OWLAnnotationAssertionAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getSubject(), arg2.getSubject()));
            pairs.add(p(owlObject.getAnnotation(), arg2.getAnnotation()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLSymmetricObjectPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataPropertyRangeAxiom owlObject) {
            OWLDataPropertyRangeAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getRange(), arg2.getRange()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLFunctionalDataPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public StructuralDifferenceReport visit(OWLEquivalentDataPropertiesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLClassAssertionAxiom owlObject) {
            OWLClassAssertionAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getClassExpression(), arg2.getClassExpression()));
            pairs.add(p(owlObject.getIndividual(), arg2.getIndividual()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLEquivalentClassesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataPropertyAssertionAxiom owlObject) {
            OWLDataPropertyAssertionAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getSubject(), arg2.getSubject()));
            pairs.add(p(owlObject.getObject(), arg2.getObject()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLTransitiveObjectPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public StructuralDifferenceReport visit(OWLIrreflexiveObjectPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public StructuralDifferenceReport visit(OWLSubDataPropertyOfAxiom owlObject) {
            OWLSubDataPropertyOfAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getSubProperty(), arg2.getSubProperty()));
            pairs.add(p(owlObject.getSuperProperty(), arg2.getSuperProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLInverseFunctionalObjectPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public StructuralDifferenceReport visit(OWLSameIndividualAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLSubPropertyChainOfAxiom owlObject) {
            OWLSubPropertyChainOfAxiom arg2 = arg(owlObject);
            StructuralDifferenceReport toReturn =
                compareCollection(owlObject.getPropertyChain(), arg2.getPropertyChain());
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                List<SimplePair<OWLObject>> pairs = new ArrayList<>();
                pairs.add(new SimplePair<>(owlObject.getSuperProperty(),
                    arg2.getSuperProperty()));
                toReturn = compare(pairs, owlObject.getPropertyChain().size());
            }
            return toReturn;
        }

        @Override
        public StructuralDifferenceReport visit(OWLInverseObjectPropertiesAxiom owlObject) {
            OWLInverseObjectPropertiesAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getFirstProperty(), arg2.getFirstProperty()));
            pairs.add(p(owlObject.getSecondProperty(), arg2.getSecondProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLHasKeyAxiom owlObject) {
            OWLHasKeyAxiom arg2 = arg(owlObject);
            StructuralDifferenceReport toReturn =
                compare(owlObject.getClassExpression(), arg2.getClassExpression());
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                toReturn = compareCollection(owlObject.getOperandsAsList(),
                    arg2.getOperandsAsList(), 1);
            }
            return toReturn;
        }

        @Override
        public StructuralDifferenceReport visit(OWLDatatypeDefinitionAxiom owlObject) {
            OWLDatatypeDefinitionAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getDatatype(), arg2.getDatatype()));
            pairs.add(p(owlObject.getDataRange(), arg2.getDataRange()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLRule owlObject) {
            SWRLRule arg2 = arg(owlObject);
            StructuralDifferenceReport toReturn =
                compareCollection(owlObject.headList(), arg2.headList());
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                toReturn = compareCollection(owlObject.bodyList(), arg2.bodyList(),
                    owlObject.headList().size());
            }
            return toReturn;
        }

        @Override
        public StructuralDifferenceReport visit(OWLSubAnnotationPropertyOfAxiom owlObject) {
            OWLSubAnnotationPropertyOfAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getSubProperty(), arg2.getSubProperty()));
            pairs.add(p(owlObject.getSuperProperty(), arg2.getSuperProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLAnnotationPropertyDomainAxiom owlObject) {
            OWLAnnotationPropertyDomainAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getDomain(), arg2.getDomain()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLAnnotationPropertyRangeAxiom owlObject) {
            OWLAnnotationPropertyRangeAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getRange(), arg2.getRange()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLClass owlObject) {
            return plainEqual(owlObject);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectIntersectionOf owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectUnionOf owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectComplementOf owlObject) {
            return compare(owlObject.getOperand(), arg(owlObject).getOperand());
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectSomeValuesFrom owlObject) {
            OWLObjectSomeValuesFrom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectAllValuesFrom owlObject) {
            OWLObjectAllValuesFrom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectHasValue owlObject) {
            OWLObjectHasValue arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectMinCardinality owlObject) {
            return visitCardinalty(owlObject);
        }

        protected StructuralDifferenceReport visitCardinalty(
            OWLCardinalityRestriction<? extends OWLObject> owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            OWLCardinalityRestriction<? extends OWLObject> arg2 = arg(owlObject);
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            StructuralDifferenceReport toReturn = compare(pairs);
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                if (owlObject.getCardinality() == arg2.getCardinality()) {
                    pairs.clear();
                    pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
                    toReturn = compare(pairs, 2);
                } else {
                    TIntList newPositions = new TIntArrayList(position);
                    newPositions.add(2);
                    toReturn = SomeDifferenceStructuralDifferenceReport.build(newPositions);
                }
            }
            return toReturn;
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectExactCardinality owlObject) {
            return visitCardinalty(owlObject);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectMaxCardinality owlObject) {
            return visitCardinalty(owlObject);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectHasSelf owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectOneOf owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataSomeValuesFrom owlObject) {
            OWLDataSomeValuesFrom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataAllValuesFrom owlObject) {
            OWLDataAllValuesFrom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataHasValue owlObject) {
            OWLDataHasValue arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataMinCardinality owlObject) {
            return visitCardinalty(owlObject);
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataExactCardinality owlObject) {
            return visitCardinalty(owlObject);
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataMaxCardinality owlObject) {
            return visitCardinalty(owlObject);
        }

        @Override
        public StructuralDifferenceReport visit(OWLDatatype owlObject) {
            return plainEqual(owlObject);
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataComplementOf owlObject) {
            return compare(owlObject.getDataRange(), arg(owlObject).getDataRange());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataOneOf owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataIntersectionOf owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataUnionOf owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDatatypeRestriction owlObject) {
            OWLDatatypeRestriction arg2 = arg(owlObject);
            StructuralDifferenceReport toReturn = compare(owlObject.getDatatype(), arg2.getDatatype());
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                toReturn = compareCollection(owlObject.facetRestrictionsAsList(),
                    arg2.facetRestrictionsAsList(), 1);
            }
            return toReturn;
        }

        @Override
        public StructuralDifferenceReport visit(OWLLiteral literal) {
            return plainEqual(literal);
        }

        @Override
        public StructuralDifferenceReport visit(OWLFacetRestriction owlObject) {
            OWLFacetRestriction arg2 = arg(owlObject);
            StructuralDifferenceReport toReturn =
                owlObject.getFacet().equals(arg2.getFacet())
                    ? StructuralDifferenceReport.NO_DIFFERENCE
                    : StructuralDifferenceReport.INCOMPARABLE;
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                List<SimplePair<OWLObject>> pairs = new ArrayList<>();
                pairs.add(
                    new SimplePair<>(owlObject.getFacetValue(), arg2.getFacetValue()));
                toReturn = compare(pairs, 1);
            }
            return toReturn;
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectProperty owlObject) {
            return plainEqual(owlObject);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectInverseOf owlObject) {
            return compare(owlObject.getInverseProperty(), arg(owlObject).getInverseProperty());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataProperty owlObject) {
            return plainEqual(owlObject);
        }

        @Override
        public StructuralDifferenceReport visit(OWLNamedIndividual owlObject) {
            return plainEqual(owlObject);
        }

        protected StructuralDifferenceReport plainEqual(OWLObject owlObject) {
            return owlObject.equals(arg) ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
        }

        @Override
        public StructuralDifferenceReport visit(OWLAnnotationProperty owlObject) {
            return plainEqual(owlObject);
        }

        @Override
        public StructuralDifferenceReport visit(OWLAnnotation owlObject) {
            OWLAnnotation arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getValue(), arg2.getValue()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(IRI owlObject) {
            return plainEqual(owlObject);
        }

        @Override
        public StructuralDifferenceReport visit(OWLAnonymousIndividual owlObject) {
            return plainEqual(owlObject);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLClassAtom owlObject) {
            SWRLClassAtom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getPredicate(), arg2.getPredicate()));
            pairs.add(p(owlObject.getArgument(), arg2.getArgument()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLDataRangeAtom owlObject) {
            SWRLDataRangeAtom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getPredicate(), arg2.getPredicate()));
            pairs.add(p(owlObject.getArgument(), arg2.getArgument()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLObjectPropertyAtom owlObject) {
            SWRLObjectPropertyAtom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getPredicate(), arg2.getPredicate()));
            pairs.add(p(owlObject.getFirstArgument(), arg2.getFirstArgument()));
            pairs.add(p(owlObject.getSecondArgument(), arg2.getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLDataPropertyAtom owlObject) {
            SWRLDataPropertyAtom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getPredicate(), arg2.getPredicate()));
            pairs.add(p(owlObject.getFirstArgument(), arg2.getFirstArgument()));
            pairs.add(p(owlObject.getSecondArgument(), arg2.getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLBuiltInAtom owlObject) {
            SWRLBuiltInAtom arg2 = arg(owlObject);
            StructuralDifferenceReport toReturn =
                compare(owlObject.getPredicate(), arg2.getPredicate());
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                toReturn =
                    compareCollection(owlObject.getArguments(), arg2.getArguments(), 1);
            }
            return toReturn;
        }

        @Override
        public StructuralDifferenceReport visit(SWRLVariable owlObject) {
            return compare(owlObject.getIRI(), arg(owlObject).getIRI());
        }

        @Override
        public StructuralDifferenceReport visit(SWRLIndividualArgument owlObject) {
            return compare(owlObject.getIndividual(), arg(owlObject).getIndividual());
        }

        @Override
        public StructuralDifferenceReport visit(SWRLLiteralArgument owlObject) {
            return compare(owlObject.getLiteral(), arg(owlObject).getLiteral());
        }

        @Override
        public StructuralDifferenceReport visit(SWRLSameIndividualAtom owlObject) {
            SWRLSameIndividualAtom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getFirstArgument(), arg2.getFirstArgument()));
            pairs.add(p(owlObject.getSecondArgument(), arg2.getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLDifferentIndividualsAtom owlObject) {
            SWRLDifferentIndividualsAtom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getFirstArgument(), arg2.getFirstArgument()));
            pairs.add(p(owlObject.getSecondArgument(), arg2.getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLOntology owlObject) {
            return plainEqual(owlObject);
        }

        protected StructuralDifferenceReport compare(List<SimplePair<OWLObject>> pairs) {
            return this.compare(pairs, 0);
        }

        protected StructuralDifferenceReport compare(List<SimplePair<OWLObject>> pairs,
            int startIndex) {
            StructuralDifferenceReport toReturn = StructuralDifferenceReport.NO_DIFFERENCE;
            Iterator<SimplePair<OWLObject>> iterator = pairs.iterator();
            StructuralDifference structDifference = new StructuralDifference(position);
            while (toReturn == StructuralDifferenceReport.NO_DIFFERENCE && iterator.hasNext()) {
                SimplePair<OWLObject> pair = iterator.next();
                OWLObject first = pair.getFirst();
                OWLObject second = pair.getSecond();
                toReturn = structDifference.getTopDifference(first, second);
                startIndex++;
            }
            if (toReturn != StructuralDifferenceReport.NO_DIFFERENCE) {
                final TIntList newPositions = new TIntArrayList(position);
                newPositions.add(startIndex);
                toReturn.accept(new StructuralDifferenceReportVisitorAdapter() {
                    @Override
                    public void visitSomeDifferenceStructuralDifferenceReport(
                        SomeDifferenceStructuralDifferenceReport report) {
                        newPositions.addAll(report.position);
                    }
                });
                toReturn = SomeDifferenceStructuralDifferenceReport.build(newPositions);
            }
            return toReturn;
        }

        protected StructuralDifferenceReport compare(OWLObject first, OWLObject second) {
            StructuralDifference structDifference = new StructuralDifference(position);
            StructuralDifferenceReport toReturn = structDifference.getTopDifference(first, second);
            if (toReturn != StructuralDifferenceReport.NO_DIFFERENCE) {
                final TIntList newPositions = new TIntArrayList(position);
                newPositions.add(1);
                toReturn.accept(new StructuralDifferenceReportVisitorAdapter() {
                    @Override
                    public void visitSomeDifferenceStructuralDifferenceReport(
                        SomeDifferenceStructuralDifferenceReport report) {
                        newPositions.addAll(report.position);
                    }
                });
                toReturn = SomeDifferenceStructuralDifferenceReport.build(newPositions);
            }
            return toReturn;
        }

        protected <O extends Collection<P>, P extends OWLObject> StructuralDifferenceReport compareCollection(
            O aCollection, O anotherCollection) {
            return this.compareCollection(aCollection, anotherCollection, 0);
        }

        protected <O extends Collection<P>, P extends OWLObject> StructuralDifferenceReport compareCollection(
            O aCollection, O anotherCollection, int startIndex) {
            StructuralDifferenceReport toReturn = aCollection.size() == anotherCollection.size()
                ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                Iterator<P> iterator = aCollection.iterator();
                Iterator<P> anotherIterator = anotherCollection.iterator();
                List<SimplePair<OWLObject>> pairs = new ArrayList<>(aCollection.size());
                while (iterator.hasNext()) {
                    P first = iterator.next();
                    P second = anotherIterator.next();
                    pairs.add(p(first, second));
                }
                toReturn = this.compare(pairs, startIndex);
            }
            return toReturn;
        }
    }

    @Override
    public <T> StructuralDifferenceReport doDefault(T object) {
        return getOWLObject().accept(new StructuralDiffVisitor((OWLObject) object));
    }

    /** @return the difference */
    public StructuralDifference getDifference() {
        return difference;
    }
}
