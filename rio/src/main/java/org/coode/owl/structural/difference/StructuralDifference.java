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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

/** @author eleni */
public class StructuralDifference {
    private final List<Integer> position = new ArrayList<>();

    /**
     * @param position position
     */
    public StructuralDifference(List<? extends Integer> position) {
        if (position == null) {
            throw new NullPointerException("The position cannot be null");
        }
        this.position.addAll(position);
    }

    /**
     * 
     */
    public StructuralDifference() {
        this(Collections.<Integer>emptyList());
    }

    /**
     * Retrieves the position of the top-most difference between the two input object, relative to
     * the first one.
     * 
     * @param anOWLObject input OWL Object.
     * @param anotherOWLObject input OWL Object.
     * @return An instance of StructuralDifferenceReport describing the result of the comparison.
     */
    public StructuralDifferenceReport getTopDifference(OWLObject anOWLObject,
        OWLObject anotherOWLObject) {
        StructuralDifferenceReport toReturn =
            areComparable(anOWLObject, anotherOWLObject) ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
            StructuralComparison comparison = new StructuralComparison(anOWLObject, position);
            toReturn = anotherOWLObject.accept(comparison);
        }
        return toReturn;
    }

    /**
     * @param anOWLObject anOWLObject
     * @param anotherOWLObject anotherOWLObject
     * @return top differences
     */
    public List<StructuralDifferenceReport> getTopDifferences(OWLObject anOWLObject,
        OWLObject anotherOWLObject) {
        boolean areComparable = areComparable(anOWLObject, anotherOWLObject);
        List<StructuralDifferenceReport> toReturn =
            areComparable ? Collections.<StructuralDifferenceReport>emptyList()
                : new ArrayList<>(Collections.singleton(StructuralDifferenceReport.INCOMPARABLE));
        if (areComparable) {
            CompleteStructuralComparison comparison =
                new CompleteStructuralComparison(anOWLObject, position);
            toReturn = anotherOWLObject.accept(comparison);
        }
        toReturn.remove(StructuralDifferenceReport.NO_DIFFERENCE);
        return toReturn;
    }

    /**
     * @param c c
     * @return top differences
     */
    public Set<List<StructuralDifferenceReport>> getTopDifferences(
        Collection<? extends OWLObject> c) {
        if (c == null) {
            throw new NullPointerException("The collection cannot be null");
        }
        Set<List<StructuralDifferenceReport>> toReturn = new HashSet<>();
        for (OWLObject owlObject : c) {
            for (OWLObject anotherOWLObject : c) {
                if (owlObject != anotherOWLObject) {
                    toReturn.add(this.getTopDifferences(owlObject, anotherOWLObject));
                }
            }
        }
        return toReturn;
    }

    /**
     * Determines if the structural difference makes sense for the pair of input OWLObjects. It
     * returns {@code true} if the input objects are fo the same kind.
     * 
     * @param anOWLObject An input object
     * @param anotherOWLObject Another input object
     * @return {@code true} if the input objects are of the same kind.
     */
    @SuppressWarnings("boxing")
    public boolean areComparable(OWLObject anOWLObject, final OWLObject anotherOWLObject) {
        boolean toReturn = false;
        if (anOWLObject == null) {
            toReturn = anotherOWLObject == null;
        } else if (anotherOWLObject != null) {
            toReturn = anOWLObject.accept(new OWLObjectVisitorEx<Boolean>() {
                // XXX compare OWLObject::type
                @Override
                public Boolean visit(OWLSubClassOfAxiom axiom) {
                    return anotherOWLObject instanceof OWLSubClassOfAxiom;
                }

                @Override
                public Boolean visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
                    return anotherOWLObject instanceof OWLNegativeDataPropertyAssertionAxiom;
                }

                @Override
                public Boolean visit(OWLAsymmetricObjectPropertyAxiom axiom) {
                    return anotherOWLObject instanceof OWLAsymmetricObjectPropertyAxiom;
                }

                @Override
                public Boolean visit(OWLReflexiveObjectPropertyAxiom axiom) {
                    return anotherOWLObject instanceof OWLReflexiveObjectPropertyAxiom;
                }

                @Override
                public Boolean visit(OWLDisjointClassesAxiom axiom) {
                    return anotherOWLObject instanceof OWLDisjointClassesAxiom;
                }

                @Override
                public Boolean visit(OWLDataPropertyDomainAxiom axiom) {
                    return anotherOWLObject instanceof OWLDataPropertyDomainAxiom;
                }

                @Override
                public Boolean visit(OWLObjectPropertyDomainAxiom axiom) {
                    return anotherOWLObject instanceof OWLObjectPropertyDomainAxiom;
                }

                @Override
                public Boolean visit(OWLEquivalentObjectPropertiesAxiom axiom) {
                    return anotherOWLObject instanceof OWLEquivalentObjectPropertiesAxiom;
                }

                @Override
                public Boolean visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
                    return anotherOWLObject instanceof OWLNegativeDataPropertyAssertionAxiom;
                }

                @Override
                public Boolean visit(OWLDifferentIndividualsAxiom axiom) {
                    return anotherOWLObject instanceof OWLDifferentIndividualsAxiom;
                }

                @Override
                public Boolean visit(OWLDisjointDataPropertiesAxiom axiom) {
                    return anotherOWLObject instanceof OWLDisjointDataPropertiesAxiom;
                }

                @Override
                public Boolean visit(OWLDisjointObjectPropertiesAxiom axiom) {
                    return anotherOWLObject instanceof OWLDisjointObjectPropertiesAxiom;
                }

                @Override
                public Boolean visit(OWLObjectPropertyRangeAxiom axiom) {
                    return anotherOWLObject instanceof OWLObjectPropertyRangeAxiom;
                }

                @Override
                public Boolean visit(OWLObjectPropertyAssertionAxiom axiom) {
                    return anotherOWLObject instanceof OWLObjectPropertyAssertionAxiom;
                }

                @Override
                public Boolean visit(OWLFunctionalObjectPropertyAxiom axiom) {
                    return anotherOWLObject instanceof OWLFunctionalObjectPropertyAxiom;
                }

                @Override
                public Boolean visit(OWLSubObjectPropertyOfAxiom axiom) {
                    return anotherOWLObject instanceof OWLSubObjectPropertyOfAxiom;
                }

                @Override
                public Boolean visit(OWLDisjointUnionAxiom axiom) {
                    return anotherOWLObject instanceof OWLDisjointUnionAxiom;
                }

                @Override
                public Boolean visit(OWLDeclarationAxiom axiom) {
                    return anotherOWLObject instanceof OWLDeclarationAxiom;
                }

                @Override
                public Boolean visit(OWLAnnotationAssertionAxiom axiom) {
                    return anotherOWLObject instanceof OWLAnnotationAssertionAxiom;
                }

                @Override
                public Boolean visit(OWLSymmetricObjectPropertyAxiom axiom) {
                    return anotherOWLObject instanceof OWLSymmetricObjectPropertyAxiom;
                }

                @Override
                public Boolean visit(OWLDataPropertyRangeAxiom axiom) {
                    return anotherOWLObject instanceof OWLDataPropertyRangeAxiom;
                }

                @Override
                public Boolean visit(OWLFunctionalDataPropertyAxiom axiom) {
                    return anotherOWLObject instanceof OWLFunctionalDataPropertyAxiom;
                }

                @Override
                public Boolean visit(OWLEquivalentDataPropertiesAxiom axiom) {
                    return anotherOWLObject instanceof OWLEquivalentDataPropertiesAxiom;
                }

                @Override
                public Boolean visit(OWLClassAssertionAxiom axiom) {
                    return anotherOWLObject instanceof OWLClassAssertionAxiom;
                }

                @Override
                public Boolean visit(OWLEquivalentClassesAxiom axiom) {
                    return anotherOWLObject instanceof OWLEquivalentClassesAxiom;
                }

                @Override
                public Boolean visit(OWLDataPropertyAssertionAxiom axiom) {
                    return anotherOWLObject instanceof OWLDataPropertyAssertionAxiom;
                }

                @Override
                public Boolean visit(OWLTransitiveObjectPropertyAxiom axiom) {
                    return anotherOWLObject instanceof OWLTransitiveObjectPropertyAxiom;
                }

                @Override
                public Boolean visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
                    return anotherOWLObject instanceof OWLIrreflexiveObjectPropertyAxiom;
                }

                @Override
                public Boolean visit(OWLSubDataPropertyOfAxiom axiom) {
                    return anotherOWLObject instanceof OWLSubDataPropertyOfAxiom;
                }

                @Override
                public Boolean visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
                    return anotherOWLObject instanceof OWLInverseFunctionalObjectPropertyAxiom;
                }

                @Override
                public Boolean visit(OWLSameIndividualAxiom axiom) {
                    return anotherOWLObject instanceof OWLSameIndividualAxiom;
                }

                @Override
                public Boolean visit(OWLSubPropertyChainOfAxiom axiom) {
                    return anotherOWLObject instanceof OWLSubPropertyChainOfAxiom;
                }

                @Override
                public Boolean visit(OWLInverseObjectPropertiesAxiom axiom) {
                    return anotherOWLObject instanceof OWLInverseObjectPropertiesAxiom;
                }

                @Override
                public Boolean visit(OWLHasKeyAxiom axiom) {
                    return anotherOWLObject instanceof OWLHasKeyAxiom;
                }

                @Override
                public Boolean visit(OWLDatatypeDefinitionAxiom axiom) {
                    return anotherOWLObject instanceof OWLDatatypeDefinitionAxiom;
                }

                @Override
                public Boolean visit(SWRLRule rule) {
                    return anotherOWLObject instanceof SWRLRule;
                }

                @Override
                public Boolean visit(OWLSubAnnotationPropertyOfAxiom axiom) {
                    return anotherOWLObject instanceof OWLSubAnnotationPropertyOfAxiom;
                }

                @Override
                public Boolean visit(OWLAnnotationPropertyDomainAxiom axiom) {
                    return anotherOWLObject instanceof OWLAnnotationPropertyDomainAxiom;
                }

                @Override
                public Boolean visit(OWLAnnotationPropertyRangeAxiom axiom) {
                    return anotherOWLObject instanceof OWLAnnotationPropertyRangeAxiom;
                }

                @Override
                public Boolean visit(OWLClass ce) {
                    return anotherOWLObject instanceof OWLClass;
                }

                @Override
                public Boolean visit(OWLObjectIntersectionOf ce) {
                    return anotherOWLObject instanceof OWLObjectIntersectionOf;
                }

                @Override
                public Boolean visit(OWLObjectUnionOf ce) {
                    return anotherOWLObject instanceof OWLObjectUnionOf;
                }

                @Override
                public Boolean visit(OWLObjectComplementOf ce) {
                    return anotherOWLObject instanceof OWLObjectComplementOf;
                }

                @Override
                public Boolean visit(OWLObjectSomeValuesFrom ce) {
                    return anotherOWLObject instanceof OWLObjectSomeValuesFrom;
                }

                @Override
                public Boolean visit(OWLObjectAllValuesFrom ce) {
                    return anotherOWLObject instanceof OWLObjectAllValuesFrom;
                }

                @Override
                public Boolean visit(OWLObjectHasValue ce) {
                    return anotherOWLObject instanceof OWLObjectHasValue;
                }

                @Override
                public Boolean visit(OWLObjectMinCardinality ce) {
                    return anotherOWLObject instanceof OWLObjectMinCardinality;
                }

                @Override
                public Boolean visit(OWLObjectExactCardinality ce) {
                    return anotherOWLObject instanceof OWLObjectExactCardinality;
                }

                @Override
                public Boolean visit(OWLObjectMaxCardinality ce) {
                    return anotherOWLObject instanceof OWLObjectMaxCardinality;
                }

                @Override
                public Boolean visit(OWLObjectHasSelf ce) {
                    return anotherOWLObject instanceof OWLObjectHasSelf;
                }

                @Override
                public Boolean visit(OWLObjectOneOf ce) {
                    return anotherOWLObject instanceof OWLObjectOneOf;
                }

                @Override
                public Boolean visit(OWLDataSomeValuesFrom ce) {
                    return anotherOWLObject instanceof OWLDataSomeValuesFrom;
                }

                @Override
                public Boolean visit(OWLDataAllValuesFrom ce) {
                    return anotherOWLObject instanceof OWLDataAllValuesFrom;
                }

                @Override
                public Boolean visit(OWLDataHasValue ce) {
                    return anotherOWLObject instanceof OWLDataHasValue;
                }

                @Override
                public Boolean visit(OWLDataMinCardinality ce) {
                    return anotherOWLObject instanceof OWLDataMinCardinality;
                }

                @Override
                public Boolean visit(OWLDataExactCardinality ce) {
                    return anotherOWLObject instanceof OWLDataExactCardinality;
                }

                @Override
                public Boolean visit(OWLDataMaxCardinality ce) {
                    return anotherOWLObject instanceof OWLDataMaxCardinality;
                }

                @Override
                public Boolean visit(OWLDatatype node) {
                    return anotherOWLObject instanceof OWLDatatype;
                }

                @Override
                public Boolean visit(OWLDataComplementOf node) {
                    return anotherOWLObject instanceof OWLDataComplementOf;
                }

                @Override
                public Boolean visit(OWLDataOneOf node) {
                    return anotherOWLObject instanceof OWLDataOneOf;
                }

                @Override
                public Boolean visit(OWLDataIntersectionOf node) {
                    return anotherOWLObject instanceof OWLDataIntersectionOf;
                }

                @Override
                public Boolean visit(OWLDataUnionOf node) {
                    return anotherOWLObject instanceof OWLDataUnionOf;
                }

                @Override
                public Boolean visit(OWLDatatypeRestriction node) {
                    return anotherOWLObject instanceof OWLDatatypeRestriction;
                }

                @Override
                public Boolean visit(OWLLiteral node) {
                    return anotherOWLObject instanceof OWLLiteral;
                }

                @Override
                public Boolean visit(OWLFacetRestriction node) {
                    return anotherOWLObject instanceof OWLFacetRestriction;
                }

                @Override
                public Boolean visit(OWLObjectProperty property) {
                    return anotherOWLObject instanceof OWLObjectProperty;
                }

                @Override
                public Boolean visit(OWLObjectInverseOf property) {
                    return anotherOWLObject instanceof OWLObjectInverseOf;
                }

                @Override
                public Boolean visit(OWLDataProperty property) {
                    return anotherOWLObject instanceof OWLDataProperty;
                }

                @Override
                public Boolean visit(OWLNamedIndividual individual) {
                    return anotherOWLObject instanceof OWLNamedIndividual;
                }

                @Override
                public Boolean visit(OWLAnnotationProperty property) {
                    return anotherOWLObject instanceof OWLAnnotationProperty;
                }

                @Override
                public Boolean visit(OWLAnnotation node) {
                    return anotherOWLObject instanceof OWLAnnotation;
                }

                @Override
                public Boolean visit(IRI iri) {
                    return anotherOWLObject instanceof IRI;
                }

                @Override
                public Boolean visit(OWLAnonymousIndividual individual) {
                    return anotherOWLObject instanceof OWLAnonymousIndividual;
                }

                @Override
                public Boolean visit(SWRLClassAtom node) {
                    return anotherOWLObject instanceof SWRLClassAtom;
                }

                @Override
                public Boolean visit(SWRLDataRangeAtom node) {
                    return anotherOWLObject instanceof SWRLDataRangeAtom;
                }

                @Override
                public Boolean visit(SWRLObjectPropertyAtom node) {
                    return anotherOWLObject instanceof SWRLObjectPropertyAtom;
                }

                @Override
                public Boolean visit(SWRLDataPropertyAtom node) {
                    return anotherOWLObject instanceof SWRLDataPropertyAtom;
                }

                @Override
                public Boolean visit(SWRLBuiltInAtom node) {
                    return anotherOWLObject instanceof SWRLBuiltInAtom;
                }

                @Override
                public Boolean visit(SWRLVariable node) {
                    return anotherOWLObject instanceof SWRLVariable;
                }

                @Override
                public Boolean visit(SWRLIndividualArgument node) {
                    return anotherOWLObject instanceof SWRLIndividualArgument;
                }

                @Override
                public Boolean visit(SWRLLiteralArgument node) {
                    return anotherOWLObject instanceof SWRLLiteralArgument;
                }

                @Override
                public Boolean visit(SWRLSameIndividualAtom node) {
                    return anotherOWLObject instanceof SWRLSameIndividualAtom;
                }

                @Override
                public Boolean visit(SWRLDifferentIndividualsAtom node) {
                    return anotherOWLObject instanceof SWRLDifferentIndividualsAtom;
                }

                @Override
                public Boolean visit(OWLOntology ontology) {
                    return anotherOWLObject instanceof OWLOntology;
                }
            });
        }
        return toReturn;
    }

    /** @return the position */
    public List<Integer> getPosition() {
        return new ArrayList<>(position);
    }
}
