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

final class CompleteStructuralComparison
    implements OWLObjectVisitorEx<List<StructuralDifferenceReport>> {
    private final OWLObject objectToCompare;
    private final List<Integer> position = new ArrayList<>();
    private final StructuralDifference difference;

    /**
     * @param owlObject owlObject
     * @param position position
     */
    public CompleteStructuralComparison(OWLObject owlObject, List<Integer> position) {
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
            StructuralDifference structDifference = new StructuralDifference(getPosition());
            List<StructuralDifferenceReport> toReturn = new ArrayList<>();
            while (iterator.hasNext()) {
                SimplePair<OWLObject> pair = iterator.next();
                OWLObject first = pair.getFirst();
                OWLObject second = pair.getSecond();
                List<StructuralDifferenceReport> differenceReports =
                    structDifference.getTopDifferences(first, second);
                startIndex++;
                if (!differenceReports.isEmpty()) {
                    for (StructuralDifferenceReport differenceReport : differenceReports) {
                        final List<Integer> newPositions = getPosition();
                        newPositions.add(Integer.valueOf(startIndex));
                        differenceReport.accept(new StructuralDifferenceReportVisitorAdapter() {
                            @Override
                            public void visitSomeDifferenceStructuralDifferenceReport(
                                SomeDifferenceStructuralDifferenceReport someDifferenceStructuralDifferenceReport) {
                                newPositions
                                    .addAll(someDifferenceStructuralDifferenceReport.getPosition());
                            }
                        });
                        StructuralDifferenceReport newDifferenceReport =
                            SomeDifferenceStructuralDifferenceReport.build(newPositions);
                        toReturn.add(newDifferenceReport);
                    }
                }
            }
            return toReturn;
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
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getSubClass(), arg(owlObject).getSubClass()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getSuperClass(),
                arg(owlObject).getSuperClass()));
            return compare(pairs);
        }


        @Override
        public List<StructuralDifferenceReport> visit(OWLAsymmetricObjectPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(IRI owlObject) {
            return owlObject.equals(arg) ? Collections.<StructuralDifferenceReport>emptyList()
                : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLReflexiveObjectPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDisjointClassesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataPropertyDomainAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getDomain(), arg(owlObject).getDomain()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectPropertyDomainAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getDomain(), arg(owlObject).getDomain()));
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
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getSubject(), arg(owlObject).getSubject()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getObject(), arg(owlObject).getObject()));
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
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getRange(), arg(owlObject).getRange()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectPropertyAssertionAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getSubject(), arg(owlObject).getSubject()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getObject(), arg(owlObject).getObject()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLFunctionalObjectPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSubObjectPropertyOfAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<OWLObject>(owlObject.getSubProperty(),
                arg(owlObject).getSubProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getSuperProperty(),
                arg(owlObject).getSuperProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDisjointUnionAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDeclarationAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<OWLObject>(owlObject.getEntity(), arg(owlObject).getEntity()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotationAssertionAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getSubject(), arg(owlObject).getSubject()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getAnnotation(),
                arg(owlObject).getAnnotation()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSymmetricObjectPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataPropertyRangeAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getRange(), arg(owlObject).getRange()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLFunctionalDataPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLEquivalentDataPropertiesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLClassAssertionAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<OWLObject>(owlObject.getClassExpression(),
                arg(owlObject).getClassExpression()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getIndividual(),
                arg(owlObject).getIndividual()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLEquivalentClassesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataPropertyAssertionAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getSubject(), arg(owlObject).getSubject()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getObject(), arg(owlObject).getObject()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLTransitiveObjectPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLIrreflexiveObjectPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSubDataPropertyOfAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<OWLObject>(owlObject.getSubProperty(),
                arg(owlObject).getSubProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getSuperProperty(),
                arg(owlObject).getSuperProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(
            OWLInverseFunctionalObjectPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSameIndividualAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSubPropertyChainOfAxiom owlObject) {
            List<StructuralDifferenceReport> toReturn =
                compareCollection(owlObject.getPropertyChain(), arg(owlObject).getPropertyChain());
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<OWLObject>(owlObject.getSuperProperty(),
                arg(owlObject).getSuperProperty()));
            toReturn.addAll(compare(pairs, owlObject.getPropertyChain().size()));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLInverseObjectPropertiesAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<OWLObject>(owlObject.getFirstProperty(),
                arg(owlObject).getFirstProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getSecondProperty(),
                arg(owlObject).getSecondProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLHasKeyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<OWLObject>(owlObject.getClassExpression(),
                arg(owlObject).getClassExpression()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            toReturn.addAll(compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList(), 1));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDatatypeDefinitionAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getDatatype(), arg(owlObject).getDatatype()));
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getDataRange(), arg(owlObject).getDataRange()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLRule owlObject) {
            List<StructuralDifferenceReport> toReturn =
                compareCollection(owlObject.headList(), arg(owlObject).headList());
            toReturn.addAll(compareCollection(owlObject.bodyList(), arg(owlObject).bodyList(),
                owlObject.headList().size()));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLSubAnnotationPropertyOfAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<OWLObject>(owlObject.getSubProperty(),
                arg(owlObject).getSubProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getSuperProperty(),
                arg(owlObject).getSuperProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotationPropertyDomainAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getDomain(), arg(owlObject).getDomain()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotationPropertyRangeAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getRange(), arg(owlObject).getRange()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLClass owlObject) {
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
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getOperand(), arg(owlObject).getOperand()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectSomeValuesFrom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), arg(owlObject).getFiller()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectAllValuesFrom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), arg(owlObject).getFiller()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectHasValue owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), arg(owlObject).getFiller()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectMinCardinality owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            if (owlObject.getCardinality() != arg(owlObject).getCardinality()) {
                List<Integer> newPositions = getPosition();
                newPositions.add(Integer.valueOf(2));
                toReturn.add(SomeDifferenceStructuralDifferenceReport.build(newPositions));
            }
            pairs.clear();
            pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), arg(owlObject).getFiller()));
            toReturn.addAll(compare(pairs, 2));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectExactCardinality owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            if (owlObject.getCardinality() != arg(owlObject).getCardinality()) {
                List<Integer> newPositions = getPosition();
                newPositions.add(Integer.valueOf(2));
                toReturn.add(SomeDifferenceStructuralDifferenceReport.build(newPositions));
            }
            pairs.clear();
            pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), arg(owlObject).getFiller()));
            toReturn.addAll(compare(pairs, 2));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectMaxCardinality owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            if (owlObject.getCardinality() != arg(owlObject).getCardinality()) {
                List<Integer> newPositions = getPosition();
                newPositions.add(Integer.valueOf(2));
                toReturn.add(SomeDifferenceStructuralDifferenceReport.build(newPositions));
            }
            pairs.clear();
            pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), arg(owlObject).getFiller()));
            toReturn.addAll(compare(pairs, 2));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectHasSelf owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectOneOf owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataSomeValuesFrom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), arg(owlObject).getFiller()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataAllValuesFrom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), arg(owlObject).getFiller()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataHasValue owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), arg(owlObject).getFiller()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataMinCardinality owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            if (owlObject.getCardinality() != arg(owlObject).getCardinality()) {
                List<Integer> newPositions = getPosition();
                newPositions.add(Integer.valueOf(2));
                toReturn.add(SomeDifferenceStructuralDifferenceReport.build(newPositions));
            }
            pairs.clear();
            pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), arg(owlObject).getFiller()));
            toReturn.addAll(compare(pairs, 2));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataExactCardinality owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            if (owlObject.getCardinality() != arg(owlObject).getCardinality()) {
                List<Integer> newPositions = getPosition();
                newPositions.add(Integer.valueOf(2));
                toReturn.add(SomeDifferenceStructuralDifferenceReport.build(newPositions));
            }
            pairs.clear();
            pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), arg(owlObject).getFiller()));
            toReturn.addAll(compare(pairs, 2));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataMaxCardinality owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            if (owlObject.getCardinality() != arg(owlObject).getCardinality()) {
                List<Integer> newPositions = getPosition();
                newPositions.add(Integer.valueOf(2));
                toReturn.add(SomeDifferenceStructuralDifferenceReport.build(newPositions));
            }
            pairs.clear();
            pairs.add(new SimplePair<OWLObject>(owlObject.getFiller(), arg(owlObject).getFiller()));
            toReturn.addAll(compare(pairs, 2));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDatatype owlObject) {
            return arg(owlObject).equals(owlObject)
                ? Collections.<StructuralDifferenceReport>emptyList()
                : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataComplementOf owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getDataRange(), arg(owlObject).getDataRange()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            return toReturn;
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
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getDatatype(), arg(owlObject).getDatatype()));
            List<StructuralDifferenceReport> toReturn = compare(pairs);
            toReturn.addAll(compareCollection(owlObject.facetRestrictionsAsList(),
                arg(owlObject).facetRestrictionsAsList(), 1));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLLiteral literal) {
            return literal.equals(arg) ? Collections.<StructuralDifferenceReport>emptyList()
                : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLFacetRestriction owlObject) {
            List<StructuralDifferenceReport> toReturn =
                owlObject.getFacet().equals(arg(owlObject).getFacet())
                    ? Collections.<StructuralDifferenceReport>emptyList()
                    : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<OWLObject>(owlObject.getFacetValue(),
                arg(owlObject).getFacetValue()));
            toReturn.addAll(compare(pairs, 1));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectProperty owlObject) {
            return owlObject.equals(arg) ? Collections.<StructuralDifferenceReport>emptyList()
                : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLObjectInverseOf owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<OWLObject>(owlObject.getInverseProperty(),
                arg(owlObject).getInverseProperty()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLDataProperty owlObject) {
            return owlObject.equals(arg) ? Collections.<StructuralDifferenceReport>emptyList()
                : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLNamedIndividual owlObject) {
            return owlObject.equals(arg) ? Collections.<StructuralDifferenceReport>emptyList()
                : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotationProperty owlObject) {
            return owlObject.equals(arg) ? Collections.<StructuralDifferenceReport>emptyList()
                : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnnotation owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getValue(), arg(owlObject).getValue()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLAnonymousIndividual owlObject) {
            return owlObject.equals(arg) ? Collections.<StructuralDifferenceReport>emptyList()
                : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLClassAtom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getPredicate(), arg(owlObject).getPredicate()));
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getArgument(), arg(owlObject).getArgument()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLDataRangeAtom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getPredicate(), arg(owlObject).getPredicate()));
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getArgument(), arg(owlObject).getArgument()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLObjectPropertyAtom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getPredicate(), arg(owlObject).getPredicate()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getFirstArgument(),
                arg(owlObject).getFirstArgument()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getSecondArgument(),
                arg(owlObject).getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLDataPropertyAtom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getPredicate(), arg(owlObject).getPredicate()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getFirstArgument(),
                arg(owlObject).getFirstArgument()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getSecondArgument(),
                arg(owlObject).getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLBuiltInAtom owlObject) {
            List<StructuralDifferenceReport> toReturn =
                Collections.<StructuralDifferenceReport>emptyList();
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getPredicate(), arg(owlObject).getPredicate()));
            toReturn.addAll(compare(pairs));
            toReturn.addAll(
                compareCollection(owlObject.getArguments(), arg(owlObject).getArguments(), 1));
            return toReturn;
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLVariable owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<OWLObject>(owlObject.getIRI(), arg(owlObject).getIRI()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLIndividualArgument owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<OWLObject>(owlObject.getIndividual(),
                arg(owlObject).getIndividual()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLLiteralArgument owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<OWLObject>(owlObject.getLiteral(), arg(owlObject).getLiteral()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLSameIndividualAtom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<OWLObject>(owlObject.getFirstArgument(),
                arg(owlObject).getFirstArgument()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getSecondArgument(),
                arg(owlObject).getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(SWRLDifferentIndividualsAtom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<OWLObject>(owlObject.getFirstArgument(),
                arg(owlObject).getFirstArgument()));
            pairs.add(new SimplePair<OWLObject>(owlObject.getSecondArgument(),
                arg(owlObject).getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public List<StructuralDifferenceReport> visit(OWLOntology owlObject) {
            return owlObject.equals(arg) ? Collections.<StructuralDifferenceReport>emptyList()
                : Collections.singletonList(StructuralDifferenceReport.INCOMPARABLE);
        }
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLSubClassOfAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(
        final OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLAsymmetricObjectPropertyAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLReflexiveObjectPropertyAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDisjointClassesAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDataPropertyDomainAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectPropertyDomainAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLEquivalentObjectPropertiesAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(
        final OWLNegativeDataPropertyAssertionAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDifferentIndividualsAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDisjointDataPropertiesAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDisjointObjectPropertiesAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectPropertyRangeAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectPropertyAssertionAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLFunctionalObjectPropertyAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLSubObjectPropertyOfAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDisjointUnionAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDeclarationAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLAnnotationAssertionAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLSymmetricObjectPropertyAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDataPropertyRangeAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLFunctionalDataPropertyAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLEquivalentDataPropertiesAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLClassAssertionAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLEquivalentClassesAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDataPropertyAssertionAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLTransitiveObjectPropertyAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLIrreflexiveObjectPropertyAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLSubDataPropertyOfAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(
        final OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLSameIndividualAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLSubPropertyChainOfAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLInverseObjectPropertiesAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLHasKeyAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDatatypeDefinitionAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final SWRLRule rule) {
        return getOWLObject().accept(new Diff(rule));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLSubAnnotationPropertyOfAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLAnnotationPropertyDomainAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLAnnotationPropertyRangeAxiom axiom) {
        return getOWLObject().accept(new Diff(axiom));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLClass ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectIntersectionOf ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectUnionOf ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectComplementOf ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectSomeValuesFrom ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectAllValuesFrom ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectHasValue ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectExactCardinality ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectMaxCardinality ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectMinCardinality ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectHasSelf ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectOneOf ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDataSomeValuesFrom ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDataAllValuesFrom ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDataHasValue ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDataExactCardinality ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDataMinCardinality ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDataMaxCardinality ce) {
        return getOWLObject().accept(new Diff(ce));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDatatype node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDataComplementOf node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDataOneOf node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDataIntersectionOf node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDataUnionOf node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDatatypeRestriction node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLLiteral node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLFacetRestriction node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectProperty property) {
        return getOWLObject().accept(new Diff(property));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLObjectInverseOf property) {
        return getOWLObject().accept(new Diff(property));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLDataProperty property) {
        return getOWLObject().accept(new Diff(property));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLNamedIndividual individual) {
        return getOWLObject().accept(new Diff(individual));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLAnnotationProperty property) {
        return getOWLObject().accept(new Diff(property));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLAnnotation node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final IRI iri) {
        return getOWLObject().accept(new Diff(iri));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLAnonymousIndividual individual) {
        return getOWLObject().accept(new Diff(individual));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final SWRLClassAtom node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final SWRLDataRangeAtom node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final SWRLObjectPropertyAtom node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final SWRLDataPropertyAtom node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final SWRLBuiltInAtom node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final SWRLVariable node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final SWRLIndividualArgument node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final SWRLLiteralArgument node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final SWRLSameIndividualAtom node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final SWRLDifferentIndividualsAtom node) {
        return getOWLObject().accept(new Diff(node));
    }

    @Override
    public List<StructuralDifferenceReport> visit(final OWLOntology ontology) {
        return getOWLObject().accept(new Diff(ontology));
    }

    /** @return the position */
    public List<Integer> getPosition() {
        return new ArrayList<>(position);
    }

    /** @return the difference */
    public StructuralDifference getDifference() {
        return difference;
    }
}
