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

class CompleteStructuralComparison implements OWLObjectVisitorEx<List<StructuralDifferenceReport>> {
    private final OWLObject objectToCompare;
    protected final TIntList position = new TIntArrayList();
    private final StructuralDifference difference;

    /**
     * @param owlObject owlObject
     * @param position position
     */
    public CompleteStructuralComparison(OWLObject owlObject, TIntList position) {
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

    private class Diff implements OWLObjectVisitorEx<List<StructuralDifferenceReport>> {
        private OWLObject arg;

        public Diff(OWLObject o) {
            arg = o;
        }

        protected <O extends Collection<P>, P extends OWLObject> List<StructuralDifferenceReport> compareCollection(
            O aCollection, O anotherCollection) {
            return this.compareCollection(aCollection, anotherCollection, 0);
        }

        protected <O extends Collection<P>, P extends OWLObject> List<StructuralDifferenceReport> compareCollection(
            O aCollection, O anotherCollection, int startIndex) {
            boolean sizeMatch = aCollection.size() == anotherCollection.size();
            List<StructuralDifferenceReport> toReturn =
                sizeMatch ? Collections.<StructuralDifferenceReport>emptyList()
                    : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
            if (sizeMatch) {
                Iterator<P> iterator = aCollection.iterator();
                Iterator<P> anotherIterator = anotherCollection.iterator();
                List<SimplePair<OWLObject>> pairs = new ArrayList<>(aCollection.size());
                while (iterator.hasNext()) {
                    pairs.add(p(iterator.next(), anotherIterator.next()));
                }
                toReturn = this.compare(pairs, startIndex);
            }
            return toReturn;
        }

        protected <P extends OWLObject> SimplePair<OWLObject> p(P first, P second) {
            return new SimplePair<>(first, second);
        }

        protected List<StructuralDifferenceReport> compare(List<SimplePair<OWLObject>> pairs) {
            return this.compare(pairs, 0);
        }

        protected List<StructuralDifferenceReport> compare(List<SimplePair<OWLObject>> pairs,
            int startIndex) {
            Iterator<SimplePair<OWLObject>> iterator = pairs.iterator();
            StructuralDifference structDifference = new StructuralDifference(position);
            List<StructuralDifferenceReport> toReturn = new ArrayList<>();
            while (iterator.hasNext()) {
                SimplePair<OWLObject> pair = iterator.next();
                startIndex = compare(startIndex, structDifference, toReturn, pair.getFirst(),
                    pair.getSecond());
            }
            return toReturn;
        }

        protected List<StructuralDifferenceReport> compare(OWLObject a, OWLObject b) {
            StructuralDifference structDifference = new StructuralDifference(position);
            List<StructuralDifferenceReport> toReturn = new ArrayList<>();
            compare(0, structDifference, toReturn, a, b);
            return toReturn;
        }

        protected int compare(int startIndex, StructuralDifference structDifference,
            List<StructuralDifferenceReport> toReturn, OWLObject first, OWLObject second) {
            List<StructuralDifferenceReport> differenceReports =
                structDifference.getTopDifferences(first, second);
            if (!differenceReports.isEmpty()) {
                for (StructuralDifferenceReport differenceReport : differenceReports) {
                    TIntList newPositions = new TIntArrayList(position);
                    newPositions.add(startIndex + 1);
                    differenceReport.accept(new StructuralDifferenceReportVisitorAdapter() {
                        @Override
                        public void visitSomeDifferenceStructuralDifferenceReport(
                            SomeDifferenceStructuralDifferenceReport someDifferenceStructuralDifferenceReport) {
                            newPositions.addAll(someDifferenceStructuralDifferenceReport.position);
                        }
                    });
                    StructuralDifferenceReport newDifferenceReport =
                        SomeDifferenceStructuralDifferenceReport.build(newPositions);
                    toReturn.add(newDifferenceReport);
                }
            }
            return startIndex + 1;
        }


        @Override
        public <T> List<StructuralDifferenceReport> doDefault(T object) {
            return Collections.<StructuralDifferenceReport>emptyList();
        }

        private <T extends OWLObject> T arg(T o) {
            return (T) o.getClass().cast(arg);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSubClassOfAxiom owlObject) {
            OWLSubClassOfAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getSubClass(), arg2.getSubClass()));
            pairs.add(p(owlObject.getSuperClass(), arg2.getSuperClass()));
            return compare(pairs);
        }


        @Override
        public List<StructuralDifferenceReport> visit(OWLAsymmetricObjectPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public List<StructuralDifferenceReport> visit(IRI owlObject) {
            return owlObject.equals(arg) ? Collections.<StructuralDifferenceReport>emptyList()
                : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLReflexiveObjectPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDisjointClassesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataPropertyDomainAxiom owlObject) {
            OWLDataPropertyDomainAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getDomain(), arg2.getDomain()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectPropertyDomainAxiom owlObject) {
            OWLObjectPropertyDomainAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getDomain(), arg2.getDomain()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(
            OWLEquivalentObjectPropertiesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(
            OWLNegativeDataPropertyAssertionAxiom owlObject) {
            OWLNegativeDataPropertyAssertionAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getSubject(), arg2.getSubject()));
            pairs.add(p(owlObject.getObject(), arg2.getObject()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDifferentIndividualsAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDisjointDataPropertiesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDisjointObjectPropertiesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectPropertyRangeAxiom owlObject) {
            OWLObjectPropertyRangeAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getRange(), arg2.getRange()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectPropertyAssertionAxiom owlObject) {
            OWLObjectPropertyAssertionAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getSubject(), arg2.getSubject()));
            pairs.add(p(owlObject.getObject(), arg2.getObject()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLFunctionalObjectPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSubObjectPropertyOfAxiom owlObject) {
            OWLSubObjectPropertyOfAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getSubProperty(), arg2.getSubProperty()));
            pairs.add(p(owlObject.getSuperProperty(), arg2.getSuperProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDisjointUnionAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDeclarationAxiom owlObject) {
            return compare(owlObject.getEntity(), arg(owlObject).getEntity());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotationAssertionAxiom owlObject) {
            OWLAnnotationAssertionAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getSubject(), arg2.getSubject()));
            pairs.add(p(owlObject.getAnnotation(), arg2.getAnnotation()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSymmetricObjectPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataPropertyRangeAxiom owlObject) {
            OWLDataPropertyRangeAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getRange(), arg2.getRange()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLFunctionalDataPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLEquivalentDataPropertiesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLClassAssertionAxiom owlObject) {
            OWLClassAssertionAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getClassExpression(), arg2.getClassExpression()));
            pairs.add(p(owlObject.getIndividual(), arg2.getIndividual()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLEquivalentClassesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataPropertyAssertionAxiom owlObject) {
            OWLDataPropertyAssertionAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getSubject(), arg2.getSubject()));
            pairs.add(p(owlObject.getObject(), arg2.getObject()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLTransitiveObjectPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLIrreflexiveObjectPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSubDataPropertyOfAxiom owlObject) {
            OWLSubDataPropertyOfAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getSubProperty(), arg2.getSubProperty()));
            pairs.add(p(owlObject.getSuperProperty(), arg2.getSuperProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(
            OWLInverseFunctionalObjectPropertyAxiom owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSameIndividualAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSubPropertyChainOfAxiom owlObject) {
            OWLSubPropertyChainOfAxiom arg2 = arg(owlObject);
            List<StructuralDifferenceReport> toReturn =
                compareCollection(owlObject.getPropertyChain(), arg2.getPropertyChain());
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getSuperProperty(), arg2.getSuperProperty()));
            toReturn.addAll(compare(pairs, owlObject.getPropertyChain().size()));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLInverseObjectPropertiesAxiom owlObject) {
            OWLInverseObjectPropertiesAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getFirstProperty(), arg2.getFirstProperty()));
            pairs.add(p(owlObject.getSecondProperty(), arg2.getSecondProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLHasKeyAxiom owlObject) {
            OWLHasKeyAxiom arg2 = arg(owlObject);
            List<StructuralDifferenceReport> toReturn =
                compare(owlObject.getClassExpression(), arg2.getClassExpression());
            toReturn.addAll(
                compareCollection(owlObject.getOperandsAsList(), arg2.getOperandsAsList(), 1));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDatatypeDefinitionAxiom owlObject) {
            OWLDatatypeDefinitionAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getDatatype(), arg2.getDatatype()));
            pairs.add(p(owlObject.getDataRange(), arg2.getDataRange()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLRule owlObject) {
            SWRLRule arg2 = arg(owlObject);
            List<StructuralDifferenceReport> toReturn =
                compareCollection(owlObject.headList(), arg2.headList());
            toReturn.addAll(compareCollection(owlObject.bodyList(), arg2.bodyList(),
                owlObject.headList().size()));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSubAnnotationPropertyOfAxiom owlObject) {
            OWLSubAnnotationPropertyOfAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getSubProperty(), arg2.getSubProperty()));
            pairs.add(p(owlObject.getSuperProperty(), arg2.getSuperProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotationPropertyDomainAxiom owlObject) {
            OWLAnnotationPropertyDomainAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getDomain(), arg2.getDomain()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotationPropertyRangeAxiom owlObject) {
            OWLAnnotationPropertyRangeAxiom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getRange(), arg2.getRange()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLClass owlObject) {
            return plainEqual(owlObject);
        }

        protected List<StructuralDifferenceReport> plainEqual(OWLObject owlObject) {
            return owlObject.equals(arg) ? Collections.<StructuralDifferenceReport>emptyList()
                : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectIntersectionOf owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectUnionOf owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectComplementOf owlObject) {
            return compare(owlObject.getOperand(), arg(owlObject).getOperand());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectSomeValuesFrom owlObject) {
            OWLObjectSomeValuesFrom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectAllValuesFrom owlObject) {
            OWLObjectAllValuesFrom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectHasValue owlObject) {
            OWLObjectHasValue arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectMinCardinality owlObject) {
            return visitCardinalty(owlObject);
        }

        protected List<StructuralDifferenceReport> visitCardinalty(
            OWLCardinalityRestriction<? extends OWLObject> owlObject) {
            OWLCardinalityRestriction<? extends OWLObject> arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            if (owlObject.getCardinality() != arg2.getCardinality()) {
                TIntList newPositions = new TIntArrayList(position);
                newPositions.add(2);
                toReturn.add(SomeDifferenceStructuralDifferenceReport.build(newPositions));
            }
            pairs.clear();
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            toReturn.addAll(compare(pairs, 2));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectExactCardinality owlObject) {
            OWLObjectExactCardinality arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            if (owlObject.getCardinality() != arg2.getCardinality()) {
                TIntList newPositions = new TIntArrayList(position);
                newPositions.add(2);
                toReturn.add(SomeDifferenceStructuralDifferenceReport.build(newPositions));
            }
            pairs.clear();
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            toReturn.addAll(compare(pairs, 2));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectMaxCardinality owlObject) {
            OWLObjectMaxCardinality arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            if (owlObject.getCardinality() != arg2.getCardinality()) {
                TIntList newPositions = new TIntArrayList(position);
                newPositions.add(2);
                toReturn.add(SomeDifferenceStructuralDifferenceReport.build(newPositions));
            }
            pairs.clear();
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            toReturn.addAll(compare(pairs, 2));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectHasSelf owlObject) {
            return compare(owlObject.getProperty(), arg(owlObject).getProperty());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectOneOf owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataSomeValuesFrom owlObject) {
            OWLDataSomeValuesFrom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataAllValuesFrom owlObject) {
            OWLDataAllValuesFrom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataHasValue owlObject) {
            OWLDataHasValue arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataMinCardinality owlObject) {
            OWLDataMinCardinality arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            if (owlObject.getCardinality() != arg2.getCardinality()) {
                TIntList newPositions = new TIntArrayList(position);
                newPositions.add(2);
                toReturn.add(SomeDifferenceStructuralDifferenceReport.build(newPositions));
            }
            pairs.clear();
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            toReturn.addAll(compare(pairs, 2));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataExactCardinality owlObject) {
            OWLDataExactCardinality arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            if (owlObject.getCardinality() != arg2.getCardinality()) {
                TIntList newPositions = new TIntArrayList(position);
                newPositions.add(2);
                toReturn.add(SomeDifferenceStructuralDifferenceReport.build(newPositions));
            }
            pairs.clear();
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            toReturn.addAll(compare(pairs, 2));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataMaxCardinality owlObject) {
            OWLDataMaxCardinality arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            if (owlObject.getCardinality() != arg2.getCardinality()) {
                TIntList newPositions = new TIntArrayList(position);
                newPositions.add(2);
                toReturn.add(SomeDifferenceStructuralDifferenceReport.build(newPositions));
            }
            pairs.clear();
            pairs.add(p(owlObject.getFiller(), arg2.getFiller()));
            toReturn.addAll(compare(pairs, 2));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDatatype owlObject) {
            return plainEqual(owlObject);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataComplementOf owlObject) {
            return compare(owlObject.getDataRange(), arg(owlObject).getDataRange());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataOneOf owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataIntersectionOf owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataUnionOf owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDatatypeRestriction owlObject) {
            OWLDatatypeRestriction arg2 = arg(owlObject);
            List<StructuralDifferenceReport> toReturn =
                compare(owlObject.getDatatype(), arg2.getDatatype());
            toReturn.addAll(compareCollection(owlObject.facetRestrictionsAsList(),
                arg2.facetRestrictionsAsList(), 1));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLLiteral literal) {
            return plainEqual(literal);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLFacetRestriction owlObject) {
            OWLFacetRestriction arg2 = arg(owlObject);
            List<StructuralDifferenceReport> toReturn = owlObject.getFacet().equals(arg2.getFacet())
                ? Collections.<StructuralDifferenceReport>emptyList()
                : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getFacetValue(), arg2.getFacetValue()));
            toReturn.addAll(compare(pairs, 1));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectProperty owlObject) {
            return plainEqual(owlObject);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectInverseOf owlObject) {
            return compare(owlObject.getInverseProperty(), arg(owlObject).getInverseProperty());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataProperty owlObject) {
            return plainEqual(owlObject);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLNamedIndividual owlObject) {
            return plainEqual(owlObject);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotationProperty owlObject) {
            return plainEqual(owlObject);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotation owlObject) {
            OWLAnnotation arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getProperty(), arg2.getProperty()));
            pairs.add(p(owlObject.getValue(), arg2.getValue()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnonymousIndividual owlObject) {
            return plainEqual(owlObject);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLClassAtom owlObject) {
            SWRLClassAtom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getPredicate(), arg2.getPredicate()));
            pairs.add(p(owlObject.getArgument(), arg2.getArgument()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLDataRangeAtom owlObject) {
            SWRLDataRangeAtom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getPredicate(), arg2.getPredicate()));
            pairs.add(p(owlObject.getArgument(), arg2.getArgument()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLObjectPropertyAtom owlObject) {
            SWRLObjectPropertyAtom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getPredicate(), arg2.getPredicate()));
            pairs.add(p(owlObject.getFirstArgument(), arg2.getFirstArgument()));
            pairs.add(p(owlObject.getSecondArgument(), arg2.getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLDataPropertyAtom owlObject) {
            SWRLDataPropertyAtom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getPredicate(), arg2.getPredicate()));
            pairs.add(p(owlObject.getFirstArgument(), arg2.getFirstArgument()));
            pairs.add(p(owlObject.getSecondArgument(), arg2.getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLBuiltInAtom owlObject) {
            SWRLBuiltInAtom arg2 = arg(owlObject);
            List<StructuralDifferenceReport> toReturn =
                compare(owlObject.getPredicate(), arg2.getPredicate());
            toReturn.addAll(compareCollection(owlObject.getArguments(), arg2.getArguments(), 1));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLVariable owlObject) {
            return compare(owlObject.getIRI(), arg(owlObject).getIRI());
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLIndividualArgument owlObject) {
            return compare(owlObject.getIndividual(), arg(owlObject).getIndividual());
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLLiteralArgument owlObject) {
            return compare(owlObject.getLiteral(), arg(owlObject).getLiteral());
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLSameIndividualAtom owlObject) {
            SWRLSameIndividualAtom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getFirstArgument(), arg2.getFirstArgument()));
            pairs.add(p(owlObject.getSecondArgument(), arg2.getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLDifferentIndividualsAtom owlObject) {
            SWRLDifferentIndividualsAtom arg2 = arg(owlObject);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(p(owlObject.getFirstArgument(), arg2.getFirstArgument()));
            pairs.add(p(owlObject.getSecondArgument(), arg2.getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLOntology owlObject) {
            return plainEqual(owlObject);
        }
    }

    @Override
    public <T> List<StructuralDifferenceReport> doDefault(T object) {
        return getOWLObject().accept(new Diff((OWLObject) object));
    }

    /** @return the difference */
    public StructuralDifference getDifference() {
        return difference;
    }
}
