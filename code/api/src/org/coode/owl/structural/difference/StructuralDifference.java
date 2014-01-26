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
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

/** @author eleni */
public class StructuralDifference {
    private final List<Integer> position = new ArrayList<Integer>();

    /** @param position
     *            position */
    public StructuralDifference(final List<? extends Integer> position) {
        if (position == null) {
            throw new NullPointerException("The position cannot be null");
        }
        this.position.addAll(position);
    }

    /**
     * 
     */
    public StructuralDifference() {
        this(Collections.<Integer> emptyList());
    }

    /** Retrieves the position of the top-most difference between the two input
     * object, relative to the first one.
     * 
     * @param anOWLObject
     *            input OWL Object.
     * @param anotherOWLObject
     *            input OWL Object.
     * @return An instance of StructuralDifferenceReport describing the result
     *         of the comparison. */
    public StructuralDifferenceReport getTopDifference(final OWLObject anOWLObject,
            final OWLObject anotherOWLObject) {
        StructuralDifferenceReport toReturn = areComparable(anOWLObject, anotherOWLObject) ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
            StructuralComparison comparison = new StructuralComparison(anOWLObject,
                    position);
            toReturn = anotherOWLObject.accept(comparison);
        }
        return toReturn;
    }

    /** @param anOWLObject
     *            anOWLObject
     * @param anotherOWLObject
     *            anotherOWLObject
     * @return top differences */
    public List<StructuralDifferenceReport> getTopDifferences(
            final OWLObject anOWLObject, final OWLObject anotherOWLObject) {
        boolean areComparable = areComparable(anOWLObject, anotherOWLObject);
        List<StructuralDifferenceReport> toReturn = areComparable ? Collections
                .<StructuralDifferenceReport> emptyList()
                : new ArrayList<StructuralDifferenceReport>(
                        Collections.singleton(StructuralDifferenceReport.INCOMPARABLE));
        if (areComparable) {
            CompleteStructuralComparison comparison = new CompleteStructuralComparison(
                    anOWLObject, position);
            toReturn = anotherOWLObject.accept(comparison);
        }
        toReturn.remove(StructuralDifferenceReport.NO_DIFFERENCE);
        return toReturn;
    }

    /** @param c
     *            c
     * @return top differences */
    public Set<List<StructuralDifferenceReport>> getTopDifferences(
            final Collection<? extends OWLObject> c) {
        if (c == null) {
            throw new NullPointerException("The collection cannot be null");
        }
        Set<List<StructuralDifferenceReport>> toReturn = new HashSet<List<StructuralDifferenceReport>>();
        for (OWLObject owlObject : c) {
            for (OWLObject anotherOWLObject : c) {
                if (owlObject != anotherOWLObject) {
                    toReturn.add(this.getTopDifferences(owlObject, anotherOWLObject));
                }
            }
        }
        return toReturn;
    }

    /** Determines if the structural difference makes sense for the pair of input
     * OWLObjects. It returns <code>true</code> if the input objects are fo the
     * same kind.
     * 
     * @param anOWLObject
     *            An input object
     * @param anotherOWLObject
     *            Another input object
     * @return <code>true</code> if the input objects are of the same kind. */
    @SuppressWarnings("boxing")
    public boolean areComparable(final OWLObject anOWLObject,
            final OWLObject anotherOWLObject) {
        boolean toReturn = false;
        if (anOWLObject == null) {
            toReturn = anotherOWLObject == null;
        } else if (anotherOWLObject != null) {
            toReturn = anOWLObject.accept(new OWLObjectVisitorEx<Boolean>() {
                @Override
                public Boolean visit(final OWLSubClassOfAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLSubClassOfAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLNegativeObjectPropertyAssertionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLNegativeDataPropertyAssertionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLAsymmetricObjectPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLAsymmetricObjectPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLReflexiveObjectPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLReflexiveObjectPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDisjointClassesAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDisjointClassesAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDataPropertyDomainAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDataPropertyDomainAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectPropertyDomainAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean
                                        visit(final OWLObjectPropertyDomainAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLEquivalentObjectPropertiesAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLEquivalentObjectPropertiesAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLNegativeDataPropertyAssertionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLNegativeDataPropertyAssertionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDifferentIndividualsAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean
                                        visit(final OWLDifferentIndividualsAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDisjointDataPropertiesAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLDisjointDataPropertiesAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDisjointObjectPropertiesAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLDisjointObjectPropertiesAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectPropertyRangeAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLObjectPropertyRangeAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectPropertyAssertionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLObjectPropertyAssertionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLFunctionalObjectPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLFunctionalObjectPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLSubObjectPropertyOfAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLSubObjectPropertyOfAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDisjointUnionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDisjointUnionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDeclarationAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDeclarationAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLAnnotationAssertionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLAnnotationAssertionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLSymmetricObjectPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLSymmetricObjectPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDataPropertyRangeAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDataPropertyRangeAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLFunctionalDataPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLFunctionalDataPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLEquivalentDataPropertiesAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLEquivalentDataPropertiesAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLClassAssertionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLClassAssertionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLEquivalentClassesAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLEquivalentClassesAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDataPropertyAssertionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean
                                        visit(final OWLDataPropertyAssertionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLTransitiveObjectPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLTransitiveObjectPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLIrreflexiveObjectPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLIrreflexiveObjectPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLSubDataPropertyOfAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLSubDataPropertyOfAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLInverseFunctionalObjectPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLInverseFunctionalObjectPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLSameIndividualAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLSameIndividualAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLSubPropertyChainOfAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLSubPropertyChainOfAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLInverseObjectPropertiesAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLInverseObjectPropertiesAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLHasKeyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLHasKeyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDatatypeDefinitionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDatatypeDefinitionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final SWRLRule rule) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final SWRLRule r) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLSubAnnotationPropertyOfAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLSubAnnotationPropertyOfAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLAnnotationPropertyDomainAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLAnnotationPropertyDomainAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLAnnotationPropertyRangeAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        final OWLAnnotationPropertyRangeAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLClass ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLClass c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectIntersectionOf ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLObjectIntersectionOf c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectUnionOf ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLObjectUnionOf c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectComplementOf ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLObjectComplementOf c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectSomeValuesFrom ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLObjectSomeValuesFrom c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectAllValuesFrom ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLObjectAllValuesFrom c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectHasValue ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLObjectHasValue c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectMinCardinality ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLObjectMinCardinality c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectExactCardinality ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLObjectExactCardinality c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectMaxCardinality ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLObjectMaxCardinality c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectHasSelf ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLObjectHasSelf c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectOneOf ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLObjectOneOf c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDataSomeValuesFrom ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDataSomeValuesFrom c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDataAllValuesFrom ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDataAllValuesFrom c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDataHasValue ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDataHasValue c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDataMinCardinality ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDataMinCardinality c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDataExactCardinality ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDataExactCardinality c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDataMaxCardinality ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDataMaxCardinality c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDatatype node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDatatype n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDataComplementOf node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDataComplementOf n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDataOneOf node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDataOneOf n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDataIntersectionOf node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDataIntersectionOf n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDataUnionOf node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDataUnionOf n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDatatypeRestriction node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDatatypeRestriction n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLLiteral node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLLiteral n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLFacetRestriction node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLFacetRestriction n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectProperty property) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLObjectProperty p) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLObjectInverseOf property) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLObjectInverseOf p) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLDataProperty property) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLDataProperty p) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLNamedIndividual individual) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLNamedIndividual i) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLAnnotationProperty property) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLAnnotationProperty p) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLAnnotation node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLAnnotation n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final IRI iri) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final IRI i) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLAnonymousIndividual individual) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLAnonymousIndividual i) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final SWRLClassAtom node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final SWRLClassAtom n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final SWRLDataRangeAtom node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final SWRLDataRangeAtom n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final SWRLObjectPropertyAtom node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final SWRLObjectPropertyAtom n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final SWRLDataPropertyAtom node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final SWRLDataPropertyAtom n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final SWRLBuiltInAtom node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final SWRLBuiltInAtom n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final SWRLVariable node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final SWRLVariable n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final SWRLIndividualArgument node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final SWRLIndividualArgument n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final SWRLLiteralArgument node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final SWRLLiteralArgument n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final SWRLSameIndividualAtom node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final SWRLSameIndividualAtom n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final SWRLDifferentIndividualsAtom node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean
                                        visit(final SWRLDifferentIndividualsAtom n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(final OWLOntology ontology) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(final OWLOntology o) {
                                    return true;
                                }
                            });
                }
            });
        }
        return toReturn;
    }

    /** @return the position */
    public List<Integer> getPosition() {
        return new ArrayList<Integer>(position);
    }
}
