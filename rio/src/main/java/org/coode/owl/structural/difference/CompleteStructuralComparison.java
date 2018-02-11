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

import org.semanticweb.owlapi.model.HasOperands;
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

    class DiffComplete implements OWLObjectVisitorEx<List<StructuralDifferenceReport>> {
        private OWLObject arg;

        public DiffComplete(OWLObject o) {
            arg = o;
        }

        private <T extends OWLObject> T arg(T o) {
            return (T) o.getClass().cast(arg);
        }

        protected List<StructuralDifferenceReport> c(OWLObject o1) {
            return this.compare(o1, arg(o1), 0);
        }

        protected List<StructuralDifferenceReport> compareCollection(
            HasOperands<? extends OWLObject> aCollection) {
            return this.compareCollection(aCollection.getOperandsAsList(),
                ((HasOperands<OWLObject>) arg((OWLObject) aCollection)).getOperandsAsList(), 0);
        }

        protected List<StructuralDifferenceReport> compareCollection(
            Collection<? extends OWLObject> aCollection,
            Collection<? extends OWLObject> anotherCollection, int startIndex) {
            boolean sizeMatch = aCollection.size() == anotherCollection.size();
            List<StructuralDifferenceReport> toReturn =
                sizeMatch ? Collections.<StructuralDifferenceReport>emptyList()
                    : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
            if (sizeMatch) {
                Iterator<? extends OWLObject> iterator = aCollection.iterator();
                Iterator<? extends OWLObject> anotherIterator = anotherCollection.iterator();
                while (iterator.hasNext()) {
                    toReturn = this.compare(iterator.next(), anotherIterator.next(), startIndex);
                    startIndex++;
                }
            }
            return toReturn;
        }

        protected List<StructuralDifferenceReport> compare(OWLObject o1, OWLObject o2,
            int startIndex) {
            StructuralDifference structDifference = new StructuralDifference(position);
            List<StructuralDifferenceReport> toReturn = new ArrayList<>();
            Iterator<?> first = o1.componentsWithoutAnnotations().iterator();
            Iterator<?> second = o2.componentsWithoutAnnotations().iterator();
            while (first.hasNext()) {
                Object obj1 = first.next();
                Object obj2 = second.next();
                if (obj1 instanceof OWLObject) {
                    startIndex = compare(startIndex, structDifference, toReturn, (OWLObject) obj1,
                        (OWLObject) obj2);
                }
            }
            return toReturn;
        }

        protected int compare(int startIndex, StructuralDifference structDifference,
            List<StructuralDifferenceReport> toReturn, OWLObject first, OWLObject second) {
            List<StructuralDifferenceReport> differenceReports =
                structDifference.getTopDifferences(first, second);
            for (StructuralDifferenceReport differenceReport : differenceReports) {
                TIntList newPositions = new TIntArrayList(position);
                newPositions.add(startIndex + 1);
                differenceReport.accept(new StructuralDifferenceReportVisitorAdapter() {
                    @Override
                    public void visitSomeDifferenceStructuralDifferenceReport(
                        SomeDifferenceStructuralDifferenceReport report) {
                        newPositions.addAll(report.position);
                    }
                });
                toReturn.add(SomeDifferenceStructuralDifferenceReport.build(newPositions));
            }
            return startIndex + 1;
        }


        protected List<StructuralDifferenceReport> plainEqual(OWLObject o) {
            return o.equals(arg) ? Collections.<StructuralDifferenceReport>emptyList()
                : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
        }

        protected List<StructuralDifferenceReport> visitCardinalty(
            OWLCardinalityRestriction<? extends OWLObject> o) {
            OWLCardinalityRestriction<? extends OWLObject> arg2 = arg(o);
            List<StructuralDifferenceReport> toReturn =
                compare(o.getProperty(), arg2.getProperty(), 0);
            if (o.getCardinality() != arg2.getCardinality()) {
                TIntList newPositions = new TIntArrayList(position);
                newPositions.add(2);
                toReturn.add(SomeDifferenceStructuralDifferenceReport.build(newPositions));
            }
            toReturn.addAll(compare(o.getFiller(), arg2.getFiller(), 2));
            return toReturn;
        }

        @Override
        public <T> List<StructuralDifferenceReport> doDefault(T object) {
            return Collections.<StructuralDifferenceReport>emptyList();
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSubClassOfAxiom o) {
            return c(o);
        }


        @Override
        public List<StructuralDifferenceReport> visit(OWLAsymmetricObjectPropertyAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLReflexiveObjectPropertyAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDisjointClassesAxiom o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataPropertyDomainAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectPropertyDomainAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLEquivalentObjectPropertiesAxiom o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLNegativeDataPropertyAssertionAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDifferentIndividualsAxiom o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDisjointDataPropertiesAxiom o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDisjointObjectPropertiesAxiom o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectPropertyRangeAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectPropertyAssertionAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLFunctionalObjectPropertyAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSubObjectPropertyOfAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDisjointUnionAxiom o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDeclarationAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotationAssertionAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSymmetricObjectPropertyAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataPropertyRangeAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLFunctionalDataPropertyAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLEquivalentDataPropertiesAxiom o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLClassAssertionAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLEquivalentClassesAxiom o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataPropertyAssertionAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLTransitiveObjectPropertyAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLIrreflexiveObjectPropertyAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSubDataPropertyOfAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLInverseFunctionalObjectPropertyAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSameIndividualAxiom o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSubPropertyChainOfAxiom o) {
            OWLSubPropertyChainOfAxiom arg2 = arg(o);
            List<StructuralDifferenceReport> toReturn =
                compareCollection(o.getPropertyChain(), arg2.getPropertyChain(), 0);
            toReturn.addAll(compare(o.getSuperProperty(), arg2.getSuperProperty(),
                o.getPropertyChain().size()));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLInverseObjectPropertiesAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLHasKeyAxiom o) {
            OWLHasKeyAxiom arg2 = arg(o);
            List<StructuralDifferenceReport> toReturn =
                compare(o.getClassExpression(), arg2.getClassExpression(), 0);
            toReturn.addAll(compareCollection(o.getOperandsAsList(), arg2.getOperandsAsList(), 1));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDatatypeDefinitionAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLRule o) {
            SWRLRule arg2 = arg(o);
            List<StructuralDifferenceReport> toReturn =
                compareCollection(o.headList(), arg2.headList(), 0);
            toReturn.addAll(compareCollection(o.bodyList(), arg2.bodyList(), o.headList().size()));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSubAnnotationPropertyOfAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotationPropertyDomainAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotationPropertyRangeAxiom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectIntersectionOf o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectUnionOf o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectComplementOf o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectSomeValuesFrom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectAllValuesFrom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectHasValue o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectMinCardinality o) {
            return visitCardinalty(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataMinCardinality o) {
            return visitCardinalty(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataExactCardinality o) {
            return visitCardinalty(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataMaxCardinality o) {
            return visitCardinalty(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectExactCardinality o) {
            return visitCardinalty(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectMaxCardinality o) {
            return visitCardinalty(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectHasSelf o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectOneOf o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataSomeValuesFrom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataAllValuesFrom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataHasValue o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataComplementOf o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataOneOf o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataIntersectionOf o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataUnionOf o) {
            return compareCollection(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDatatypeRestriction o) {
            OWLDatatypeRestriction arg2 = arg(o);
            List<StructuralDifferenceReport> toReturn =
                compare(o.getDatatype(), arg2.getDatatype(), 0);
            toReturn.addAll(
                compareCollection(o.facetRestrictionsAsList(), arg2.facetRestrictionsAsList(), 1));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLFacetRestriction o) {
            OWLFacetRestriction arg2 = arg(o);
            List<StructuralDifferenceReport> toReturn = o.getFacet().equals(arg2.getFacet())
                ? Collections.<StructuralDifferenceReport>emptyList()
                : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
            toReturn.addAll(compare(o.getFacetValue(), arg2.getFacetValue(), 1));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectInverseOf o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotation o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLClassAtom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLDataRangeAtom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLObjectPropertyAtom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLDataPropertyAtom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLBuiltInAtom o) {
            SWRLBuiltInAtom arg2 = arg(o);
            List<StructuralDifferenceReport> toReturn =
                compare(o.getPredicate(), arg2.getPredicate(), 0);
            toReturn.addAll(compareCollection(o.getArguments(), arg2.getArguments(), 1));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLVariable o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLIndividualArgument o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLLiteralArgument o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLSameIndividualAtom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLDifferentIndividualsAtom o) {
            return c(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDatatype o) {
            return plainEqual(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLLiteral literal) {
            return plainEqual(literal);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectProperty o) {
            return plainEqual(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataProperty o) {
            return plainEqual(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLNamedIndividual o) {
            return plainEqual(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotationProperty o) {
            return plainEqual(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnonymousIndividual o) {
            return plainEqual(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLOntology o) {
            return plainEqual(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(IRI o) {
            return plainEqual(o);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLClass o) {
            return plainEqual(o);
        }
    }

    @Override
    public <T> List<StructuralDifferenceReport> doDefault(T object) {
        return getOWLObject().accept(new DiffComplete((OWLObject) object));
    }

    /** @return the difference */
    public StructuralDifference getDifference() {
        return difference;
    }
}
