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
    public StructuralDifferenceReport getTopDifference(OWLObject anOWLObject,
            OWLObject anotherOWLObject) {
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
    public List<StructuralDifferenceReport> getTopDifferences(OWLObject anOWLObject,
            OWLObject anotherOWLObject) {
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
            Collection<? extends OWLObject> c) {
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
     * OWLObjects. It returns {@code true} if the input objects are fo the same
     * kind.
     * 
     * @param anOWLObject
     *            An input object
     * @param anotherOWLObject
     *            Another input object
     * @return {@code true} if the input objects are of the same kind. */
    @SuppressWarnings("boxing")
    public boolean areComparable(OWLObject anOWLObject, final OWLObject anotherOWLObject) {
        boolean toReturn = false;
        if (anOWLObject == null) {
            toReturn = anotherOWLObject == null;
        } else if (anotherOWLObject != null) {
            toReturn = anOWLObject.accept(new OWLObjectVisitorEx<Boolean>() {
                @Override
                public Boolean visit(OWLSubClassOfAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLSubClassOfAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        OWLNegativeDataPropertyAssertionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLAsymmetricObjectPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLAsymmetricObjectPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLReflexiveObjectPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLReflexiveObjectPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDisjointClassesAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDisjointClassesAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDataPropertyDomainAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDataPropertyDomainAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectPropertyDomainAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectPropertyDomainAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLEquivalentObjectPropertiesAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean
                                        visit(OWLEquivalentObjectPropertiesAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        OWLNegativeDataPropertyAssertionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDifferentIndividualsAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDifferentIndividualsAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDisjointDataPropertiesAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDisjointDataPropertiesAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDisjointObjectPropertiesAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDisjointObjectPropertiesAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectPropertyRangeAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectPropertyRangeAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectPropertyAssertionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectPropertyAssertionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLFunctionalObjectPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLFunctionalObjectPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLSubObjectPropertyOfAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLSubObjectPropertyOfAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDisjointUnionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDisjointUnionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDeclarationAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDeclarationAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLAnnotationAssertionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLAnnotationAssertionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLSymmetricObjectPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLSymmetricObjectPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDataPropertyRangeAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDataPropertyRangeAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLFunctionalDataPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLFunctionalDataPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLEquivalentDataPropertiesAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLEquivalentDataPropertiesAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLClassAssertionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLClassAssertionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLEquivalentClassesAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLEquivalentClassesAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDataPropertyAssertionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDataPropertyAssertionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLTransitiveObjectPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLTransitiveObjectPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLIrreflexiveObjectPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLSubDataPropertyOfAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLSubDataPropertyOfAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(
                                        OWLInverseFunctionalObjectPropertyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLSameIndividualAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLSameIndividualAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLSubPropertyChainOfAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLSubPropertyChainOfAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLInverseObjectPropertiesAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLInverseObjectPropertiesAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLHasKeyAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLHasKeyAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDatatypeDefinitionAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDatatypeDefinitionAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(SWRLRule rule) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(SWRLRule r) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLSubAnnotationPropertyOfAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLSubAnnotationPropertyOfAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLAnnotationPropertyDomainAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLAnnotationPropertyDomainAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLAnnotationPropertyRangeAxiom axiom) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLAnnotationPropertyRangeAxiom a) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLClass ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLClass c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectIntersectionOf ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectIntersectionOf c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectUnionOf ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectUnionOf c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectComplementOf ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectComplementOf c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectSomeValuesFrom ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectSomeValuesFrom c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectAllValuesFrom ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectAllValuesFrom c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectHasValue ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectHasValue c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectMinCardinality ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectMinCardinality c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectExactCardinality ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectExactCardinality c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectMaxCardinality ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectMaxCardinality c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectHasSelf ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectHasSelf c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectOneOf ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectOneOf c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDataSomeValuesFrom ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDataSomeValuesFrom c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDataAllValuesFrom ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDataAllValuesFrom c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDataHasValue ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDataHasValue c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDataMinCardinality ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDataMinCardinality c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDataExactCardinality ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDataExactCardinality c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDataMaxCardinality ce) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDataMaxCardinality c) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDatatype node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDatatype n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDataComplementOf node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDataComplementOf n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDataOneOf node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDataOneOf n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDataIntersectionOf node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDataIntersectionOf n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDataUnionOf node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDataUnionOf n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDatatypeRestriction node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDatatypeRestriction n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLLiteral node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLLiteral n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLFacetRestriction node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLFacetRestriction n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectProperty property) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectProperty p) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLObjectInverseOf property) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectInverseOf p) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLDataProperty property) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLDataProperty p) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLNamedIndividual individual) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLNamedIndividual i) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLAnnotationProperty property) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLAnnotationProperty p) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLAnnotation node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLAnnotation n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(IRI iri) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(IRI i) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLAnonymousIndividual individual) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLAnonymousIndividual i) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(SWRLClassAtom node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(SWRLClassAtom n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(SWRLDataRangeAtom node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(SWRLDataRangeAtom n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(SWRLObjectPropertyAtom node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(SWRLObjectPropertyAtom n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(SWRLDataPropertyAtom node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(SWRLDataPropertyAtom n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(SWRLBuiltInAtom node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(SWRLBuiltInAtom n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(SWRLVariable node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(SWRLVariable n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(SWRLIndividualArgument node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(SWRLIndividualArgument n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(SWRLLiteralArgument node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(SWRLLiteralArgument n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(SWRLSameIndividualAtom node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(SWRLSameIndividualAtom n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(SWRLDifferentIndividualsAtom node) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(SWRLDifferentIndividualsAtom n) {
                                    return true;
                                }
                            });
                }

                @Override
                public Boolean visit(OWLOntology ontology) {
                    return anotherOWLObject
                            .accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLOntology o) {
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
