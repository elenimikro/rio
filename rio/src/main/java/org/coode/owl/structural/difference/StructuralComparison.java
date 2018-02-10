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

final class StructuralComparison implements OWLObjectVisitorEx<StructuralDifferenceReport> {
    private final OWLObject objectToCompare;
    private final List<Integer> position = new ArrayList<>();
    private final StructuralDifference difference;

    /**
     * @param owlObject owlObject
     * @param position position
     */
    public StructuralComparison(OWLObject owlObject, List<Integer> position) {
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

        @Override
        public StructuralDifferenceReport visit(OWLSubClassOfAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getSubClass(), arg(owlObject).getSubClass()));
            pairs.add(new SimplePair<>(owlObject.getSuperClass(), arg(owlObject).getSuperClass()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLNegativeDataPropertyAssertionAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getSubject(), arg(owlObject).getSubject()));
            pairs.add(new SimplePair<>(owlObject.getObject(), arg(owlObject).getObject()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLAsymmetricObjectPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLReflexiveObjectPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLDisjointClassesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataPropertyDomainAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getDomain(), arg(owlObject).getDomain()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectPropertyDomainAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getDomain(), arg(owlObject).getDomain()));
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
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getRange(), arg(owlObject).getRange()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectPropertyAssertionAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getSubject(), arg(owlObject).getSubject()));
            pairs.add(new SimplePair<>(owlObject.getObject(), arg(owlObject).getObject()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLFunctionalObjectPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLSubObjectPropertyOfAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs
                .add(new SimplePair<>(owlObject.getSubProperty(), arg(owlObject).getSubProperty()));
            pairs.add(
                new SimplePair<>(owlObject.getSuperProperty(), arg(owlObject).getSuperProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLDisjointUnionAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDeclarationAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getEntity(), arg(owlObject).getEntity()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLAnnotationAssertionAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getSubject(), arg(owlObject).getSubject()));
            pairs.add(new SimplePair<>(owlObject.getAnnotation(), arg(owlObject).getAnnotation()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLSymmetricObjectPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataPropertyRangeAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getRange(), arg(owlObject).getRange()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLFunctionalDataPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLEquivalentDataPropertiesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLClassAssertionAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getClassExpression(),
                arg(owlObject).getClassExpression()));
            pairs.add(new SimplePair<>(owlObject.getIndividual(), arg(owlObject).getIndividual()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLEquivalentClassesAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataPropertyAssertionAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getSubject(), arg(owlObject).getSubject()));
            pairs.add(new SimplePair<>(owlObject.getObject(), arg(owlObject).getObject()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLTransitiveObjectPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLIrreflexiveObjectPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLSubDataPropertyOfAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs
                .add(new SimplePair<>(owlObject.getSubProperty(), arg(owlObject).getSubProperty()));
            pairs.add(
                new SimplePair<>(owlObject.getSuperProperty(), arg(owlObject).getSuperProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLInverseFunctionalObjectPropertyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLSameIndividualAxiom owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLSubPropertyChainOfAxiom owlObject) {
            StructuralDifferenceReport toReturn =
                compareCollection(owlObject.getPropertyChain(), arg(owlObject).getPropertyChain());
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                List<SimplePair<OWLObject>> pairs = new ArrayList<>();
                pairs.add(new SimplePair<>(owlObject.getSuperProperty(),
                    arg(owlObject).getSuperProperty()));
                toReturn = compare(pairs, owlObject.getPropertyChain().size());
            }
            return toReturn;
        }

        @Override
        public StructuralDifferenceReport visit(OWLInverseObjectPropertiesAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<>(owlObject.getFirstProperty(), arg(owlObject).getFirstProperty()));
            pairs.add(new SimplePair<>(owlObject.getSecondProperty(),
                arg(owlObject).getSecondProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLHasKeyAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getClassExpression(),
                arg(owlObject).getClassExpression()));
            StructuralDifferenceReport toReturn = compare(pairs);
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                toReturn = compareCollection(owlObject.getOperandsAsList(),
                    arg(owlObject).getOperandsAsList(), 1);
            }
            return toReturn;
        }

        @Override
        public StructuralDifferenceReport visit(OWLDatatypeDefinitionAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getDatatype(), arg(owlObject).getDatatype()));
            pairs.add(new SimplePair<>(owlObject.getDataRange(), arg(owlObject).getDataRange()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLRule owlObject) {
            StructuralDifferenceReport toReturn =
                compareCollection(owlObject.headList(), arg(owlObject).headList());
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                toReturn = compareCollection(owlObject.bodyList(), arg(owlObject).bodyList(),
                    owlObject.headList().size());
            }
            return toReturn;
        }

        @Override
        public StructuralDifferenceReport visit(OWLSubAnnotationPropertyOfAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs
                .add(new SimplePair<>(owlObject.getSubProperty(), arg(owlObject).getSubProperty()));
            pairs.add(
                new SimplePair<>(owlObject.getSuperProperty(), arg(owlObject).getSuperProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLAnnotationPropertyDomainAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getDomain(), arg(owlObject).getDomain()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLAnnotationPropertyRangeAxiom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getRange(), arg(owlObject).getRange()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLClass owlObject) {
            return owlObject.equals(arg) ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
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
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getOperand(), arg(owlObject).getOperand()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectSomeValuesFrom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getFiller(), arg(owlObject).getFiller()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectAllValuesFrom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getFiller(), arg(owlObject).getFiller()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectHasValue owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getFiller(), arg(owlObject).getFiller()));
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
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg2.getProperty()));
            StructuralDifferenceReport toReturn = compare(pairs);
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                if (owlObject.getCardinality() == arg2.getCardinality()) {
                    pairs.clear();
                    pairs.add(new SimplePair<>(owlObject.getFiller(), arg2.getFiller()));
                    toReturn = compare(pairs, 2);
                } else {
                    List<Integer> newPositions = getPosition();
                    newPositions.add(Integer.valueOf(2));
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
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectOneOf owlObject) {
            return compareCollection(owlObject.getOperandsAsList(),
                arg(owlObject).getOperandsAsList());
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataSomeValuesFrom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getFiller(), arg(owlObject).getFiller()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataAllValuesFrom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getFiller(), arg(owlObject).getFiller()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataHasValue owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getFiller(), arg(owlObject).getFiller()));
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
            return arg(owlObject).equals(owlObject) ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataComplementOf owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getDataRange(), arg(owlObject).getDataRange()));
            return compare(pairs);
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
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getDatatype(), arg(owlObject).getDatatype()));
            StructuralDifferenceReport toReturn = compare(pairs);
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                toReturn = compareCollection(owlObject.facetRestrictionsAsList(),
                    arg(owlObject).facetRestrictionsAsList(), 1);
            }
            return toReturn;
        }

        @Override
        public StructuralDifferenceReport visit(OWLLiteral literal) {
            return literal.equals(arg) ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
        }

        @Override
        public StructuralDifferenceReport visit(OWLFacetRestriction owlObject) {
            StructuralDifferenceReport toReturn =
                owlObject.getFacet().equals(arg(owlObject).getFacet())
                    ? StructuralDifferenceReport.NO_DIFFERENCE
                    : StructuralDifferenceReport.INCOMPARABLE;
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                List<SimplePair<OWLObject>> pairs = new ArrayList<>();
                pairs.add(
                    new SimplePair<>(owlObject.getFacetValue(), arg(owlObject).getFacetValue()));
                toReturn = compare(pairs, 1);
            }
            return toReturn;
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectProperty owlObject) {
            return owlObject.equals(arg) ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
        }

        @Override
        public StructuralDifferenceReport visit(OWLObjectInverseOf owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getInverseProperty(),
                arg(owlObject).getInverseProperty()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLDataProperty owlObject) {
            return owlObject.equals(arg) ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
        }

        @Override
        public StructuralDifferenceReport visit(OWLNamedIndividual owlObject) {
            return owlObject.equals(arg) ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
        }

        @Override
        public StructuralDifferenceReport visit(OWLAnnotationProperty owlObject) {
            return owlObject.equals(arg) ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
        }

        @Override
        public StructuralDifferenceReport visit(OWLAnnotation owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getProperty(), arg(owlObject).getProperty()));
            pairs.add(new SimplePair<>(owlObject.getValue(), arg(owlObject).getValue()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(IRI owlObject) {
            return owlObject.equals(arg) ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
        }

        @Override
        public StructuralDifferenceReport visit(OWLAnonymousIndividual owlObject) {
            return owlObject.equals(arg) ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
        }

        @Override
        public StructuralDifferenceReport visit(SWRLClassAtom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getPredicate(), arg(owlObject).getPredicate()));
            pairs.add(new SimplePair<>(owlObject.getArgument(), arg(owlObject).getArgument()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLDataRangeAtom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getPredicate(), arg(owlObject).getPredicate()));
            pairs.add(new SimplePair<>(owlObject.getArgument(), arg(owlObject).getArgument()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLObjectPropertyAtom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getPredicate(), arg(owlObject).getPredicate()));
            pairs.add(
                new SimplePair<>(owlObject.getFirstArgument(), arg(owlObject).getFirstArgument()));
            pairs.add(new SimplePair<>(owlObject.getSecondArgument(),
                arg(owlObject).getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLDataPropertyAtom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getPredicate(), arg(owlObject).getPredicate()));
            pairs.add(
                new SimplePair<>(owlObject.getFirstArgument(), arg(owlObject).getFirstArgument()));
            pairs.add(new SimplePair<>(owlObject.getSecondArgument(),
                arg(owlObject).getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLBuiltInAtom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getPredicate(), arg(owlObject).getPredicate()));
            StructuralDifferenceReport toReturn = compare(pairs);
            if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
                toReturn =
                    compareCollection(owlObject.getArguments(), arg(owlObject).getArguments(), 1);
            }
            return toReturn;
        }

        @Override
        public StructuralDifferenceReport visit(SWRLVariable owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getIRI(), arg(owlObject).getIRI()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLIndividualArgument owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getIndividual(), arg(owlObject).getIndividual()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLLiteralArgument owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(new SimplePair<>(owlObject.getLiteral(), arg(owlObject).getLiteral()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLSameIndividualAtom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<>(owlObject.getFirstArgument(), arg(owlObject).getFirstArgument()));
            pairs.add(new SimplePair<>(owlObject.getSecondArgument(),
                arg(owlObject).getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(SWRLDifferentIndividualsAtom owlObject) {
            List<SimplePair<OWLObject>> pairs = new ArrayList<>();
            pairs.add(
                new SimplePair<>(owlObject.getFirstArgument(), arg(owlObject).getFirstArgument()));
            pairs.add(new SimplePair<>(owlObject.getSecondArgument(),
                arg(owlObject).getSecondArgument()));
            return compare(pairs);
        }

        @Override
        public StructuralDifferenceReport visit(OWLOntology owlObject) {
            return owlObject.equals(arg) ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
        }

        protected StructuralDifferenceReport compare(List<SimplePair<OWLObject>> pairs) {
            return this.compare(pairs, 0);
        }

        protected StructuralDifferenceReport compare(List<SimplePair<OWLObject>> pairs,
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
                newPositions.add(Integer.valueOf(startIndex));
                toReturn.accept(new StructuralDifferenceReportVisitorAdapter() {
                    @Override
                    public void visitSomeDifferenceStructuralDifferenceReport(
                        SomeDifferenceStructuralDifferenceReport someDifferenceStructuralDifferenceReport) {
                        newPositions.addAll(someDifferenceStructuralDifferenceReport.getPosition());
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
                    pairs.add(new SimplePair<>(first, second));
                }
                toReturn = this.compare(pairs, startIndex);
            }
            return toReturn;
        }
    }

    @Override
    public StructuralDifferenceReport visit(final OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLAsymmetricObjectPropertyAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLReflexiveObjectPropertyAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDisjointClassesAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataPropertyDomainAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectPropertyDomainAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLEquivalentObjectPropertiesAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLNegativeDataPropertyAssertionAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDifferentIndividualsAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDisjointDataPropertiesAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDisjointObjectPropertiesAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectPropertyRangeAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectPropertyAssertionAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLFunctionalObjectPropertyAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLSubObjectPropertyOfAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDisjointUnionAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDeclarationAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLAnnotationAssertionAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLSymmetricObjectPropertyAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataPropertyRangeAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLFunctionalDataPropertyAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLEquivalentDataPropertiesAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLClassAssertionAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLEquivalentClassesAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataPropertyAssertionAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLTransitiveObjectPropertyAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLIrreflexiveObjectPropertyAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLSubDataPropertyOfAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLSameIndividualAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLSubPropertyChainOfAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLInverseObjectPropertiesAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLHasKeyAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDatatypeDefinitionAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLRule rule) {
        return getOWLObject().accept(new StructuralDiffVisitor(rule));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLSubAnnotationPropertyOfAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLAnnotationPropertyDomainAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLAnnotationPropertyRangeAxiom axiom) {
        return getOWLObject().accept(new StructuralDiffVisitor(axiom));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLClass ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectIntersectionOf ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectUnionOf ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectComplementOf ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectSomeValuesFrom ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectAllValuesFrom ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectHasValue ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectMinCardinality ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectExactCardinality ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectMaxCardinality ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectHasSelf ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectOneOf ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataSomeValuesFrom ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataAllValuesFrom ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataHasValue ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataMinCardinality ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataExactCardinality ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataMaxCardinality ce) {
        return getOWLObject().accept(new StructuralDiffVisitor(ce));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDatatype node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataComplementOf node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataOneOf node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataIntersectionOf node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataUnionOf node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDatatypeRestriction node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLLiteral node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLFacetRestriction node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectProperty property) {
        return getOWLObject().accept(new StructuralDiffVisitor(property));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLObjectInverseOf property) {
        return getOWLObject().accept(new StructuralDiffVisitor(property));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLDataProperty property) {
        return getOWLObject().accept(new StructuralDiffVisitor(property));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLNamedIndividual individual) {
        return getOWLObject().accept(new StructuralDiffVisitor(individual));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLAnnotationProperty property) {
        return getOWLObject().accept(new StructuralDiffVisitor(property));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLAnnotation node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final IRI iri) {
        return getOWLObject().accept(new StructuralDiffVisitor(iri));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLAnonymousIndividual individual) {
        return getOWLObject().accept(new StructuralDiffVisitor(individual));
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLClassAtom node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLDataRangeAtom node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLObjectPropertyAtom node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLDataPropertyAtom node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLBuiltInAtom node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLVariable node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLIndividualArgument node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLLiteralArgument node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLSameIndividualAtom node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final SWRLDifferentIndividualsAtom node) {
        return getOWLObject().accept(new StructuralDiffVisitor(node));
    }

    @Override
    public StructuralDifferenceReport visit(final OWLOntology ontology) {
        return getOWLObject().accept(new StructuralDiffVisitor(ontology));
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
