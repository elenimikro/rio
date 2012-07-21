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
package org.coode.owl.generalise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.Variable;
import org.coode.oppl.bindingtree.AssignmentMap;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.oppl.datafactory.OPPLOWLDataFactory;
import org.coode.oppl.function.Aggregandum;
import org.coode.oppl.function.OPPLFunction;
import org.coode.oppl.function.OPPLFunctionVisitor;
import org.coode.oppl.function.OPPLFunctionVisitorEx;
import org.coode.oppl.function.ValueComputationParameters;
import org.coode.oppl.function.ValuesVariableAtttribute;
import org.coode.oppl.function.inline.InlineSet;
import org.coode.oppl.generated.GeneratedVariable;
import org.coode.oppl.variabletypes.ANNOTATIONPROPERTYVariableType;
import org.coode.oppl.variabletypes.CLASSVariableType;
import org.coode.oppl.variabletypes.CONSTANTVariableType;
import org.coode.oppl.variabletypes.DATAPROPERTYVariableType;
import org.coode.oppl.variabletypes.INDIVIDUALVariableType;
import org.coode.oppl.variabletypes.OBJECTPROPERTYVariableType;
import org.coode.oppl.variabletypes.VariableType;
import org.coode.oppl.variabletypes.VariableTypeFactory;
import org.coode.oppl.variabletypes.VariableTypeVisitorEx;
import org.coode.parsers.oppl.VariableIRI;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
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
import org.semanticweb.owlapi.model.OWLIndividual;
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
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;

public abstract class AbstractOWLObjectGeneralisation extends
        OWLObjectVisitorExAdapter<OWLObject> implements OWLObjectVisitorEx<OWLObject> {
    private abstract class FunctionGeneralisation implements
            OWLClassExpressionVisitorEx<OWLClassExpression> {
        public OWLClassExpression visit(final OWLClass ce) {
            OWLClassExpression toReturn = ce;
            final Variable<?> variable = getConstraintSystem().getVariable(ce.getIRI());
            if (variable != null) {
                toReturn = variable.getType().accept(
                        new VariableTypeVisitorEx<OWLClassExpression>() {
                            public OWLClassExpression visitCLASSVariableType(
                                    final CLASSVariableType classVariableType) {
                                @SuppressWarnings("unchecked")
                                Variable<OWLClassExpression> v = (Variable<OWLClassExpression>) variable;
                                final ValuesVariableAtttribute<OWLClassExpression> values = ValuesVariableAtttribute
                                        .getValuesVariableAtttribute(v);
                                Set<Aggregandum<Collection<? extends OWLClassExpression>>> aggregandums = new HashSet<Aggregandum<Collection<? extends OWLClassExpression>>>();
                                aggregandums
                                        .add(new Aggregandum<Collection<? extends OWLClassExpression>>() {
                                            public
                                                    Set<OPPLFunction<Collection<? extends OWLClassExpression>>>
                                                    getOPPLFunctions() {
                                                return Collections
                                                        .<OPPLFunction<Collection<? extends OWLClassExpression>>> singleton(values);
                                            }

                                            public boolean isCompatible(
                                                    final VariableType<?> variableType) {
                                                return values.getVariable().getType() == variableType;
                                            }

                                            public
                                                    String
                                                    render(final ConstraintSystem constraintSystem) {
                                                return values
                                                        .render(getConstraintSystem());
                                            }

                                            public
                                                    String
                                                    render(final ShortFormProvider shortFormProvider) {
                                                return values.render(shortFormProvider);
                                            }
                                        });
                                GeneratedVariable<OWLClassExpression> generatedVariable = FunctionGeneralisation.this
                                        .aggregate(variable, aggregandums);
                                getConstraintSystem().importVariable(generatedVariable);
                                return factory.getOWLClass(generatedVariable.getIRI());
                            }

                            public
                                    OWLClassExpression
                                    visitOBJECTPROPERTYVariableType(
                                            final OBJECTPROPERTYVariableType objectpropertyVariableType) {
                                return ce;
                            }

                            public
                                    OWLClassExpression
                                    visitDATAPROPERTYVariableType(
                                            final DATAPROPERTYVariableType datapropertyVariableType) {
                                return ce;
                            }

                            public OWLClassExpression visitINDIVIDUALVariableType(
                                    final INDIVIDUALVariableType individualVariableType) {
                                return ce;
                            }

                            public OWLClassExpression visitCONSTANTVariableType(
                                    final CONSTANTVariableType constantVariableType) {
                                return ce;
                            }

                            public
                                    OWLClassExpression
                                    visitANNOTATIONPROPERTYVariableType(
                                            final ANNOTATIONPROPERTYVariableType annotationpropertyVariableType) {
                                return ce;
                            }
                        });
            }
            return toReturn;
        }

        public OWLClassExpression visit(final OWLObjectIntersectionOf ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLObjectUnionOf ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLObjectComplementOf ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLObjectSomeValuesFrom ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLObjectAllValuesFrom ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLObjectHasValue ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLObjectMinCardinality ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLObjectExactCardinality ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLObjectMaxCardinality ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLObjectHasSelf ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLObjectOneOf ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLDataSomeValuesFrom ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLDataAllValuesFrom ce) {
            return generaliseClassExpression(ce);
        }

        /** @param ce
         * @return */
        private OWLClassExpression generaliseClassExpression(final OWLClassExpression ce) {
            String name = "?expression";
            Variable<?> variable = getConstraintSystem().getVariable(name);
            int i = 1;
            String newName = name;
            while (variable != null) {
                newName = String.format("%s_%d", name, i++);
                variable = getConstraintSystem().getVariable(newName);
            }
            GeneratedVariable<? extends OWLClassExpression> expressionGeneratedVariable = generalisedClassExpressionsVariables
                    .get(ce);
            if (expressionGeneratedVariable == null) {
                expressionGeneratedVariable = getConstraintSystem()
                        .createExpressionGeneratedVariable(newName, ce);
                getConstraintSystem().importVariable(expressionGeneratedVariable);
                generalisedClassExpressionsVariables.put(ce, expressionGeneratedVariable);
            }
            final ValuesVariableAtttribute<? extends OWLClassExpression> values = ValuesVariableAtttribute
                    .getValuesVariableAtttribute(expressionGeneratedVariable);
            Aggregandum<Collection<? extends OWLClassExpression>> a = new Aggregandum<Collection<? extends OWLClassExpression>>() {
                public Set<OPPLFunction<Collection<? extends OWLClassExpression>>>
                        getOPPLFunctions() {
                    OPPLFunction<Collection<? extends OWLClassExpression>> opplFunction = new OPPLFunction<Collection<? extends OWLClassExpression>>() {
                        public Collection<? extends OWLClassExpression> compute(
                                final ValueComputationParameters params) {
                            return values.compute(params);
                        }

                        public <P> P accept(final OPPLFunctionVisitorEx<P> visitor) {
                            return visitor.visitValuesVariableAtttribute(values);
                        }

                        public void accept(final OPPLFunctionVisitor visitor) {
                            visitor.visitValuesVariableAtttribute(values);
                        }

                        public String render(final ConstraintSystem constraintSystem) {
                            return values.render(constraintSystem);
                        }

                        public String render(final ShortFormProvider shortFormProvider) {
                            return values.render(shortFormProvider);
                        }
                    };
                    return Collections.singleton(opplFunction);
                }

                public boolean isCompatible(final VariableType<?> variableType) {
                    return values.getVariable().getType() == variableType;
                }

                public String render(final ConstraintSystem constraintSystem) {
                    return values.render(constraintSystem);
                }

                public String render(final ShortFormProvider shortFormProvider) {
                    return values.render(shortFormProvider);
                }
            };
            GeneratedVariable<OWLClassExpression> aggreationGeneratedVariable = aggregate(
                    expressionGeneratedVariable, Collections.singleton(a));
            return factory.getOWLClass(aggreationGeneratedVariable.getIRI());
        }

        /** @param expressionGeneratedVariable
         * @param a
         * @return */
        protected abstract
                GeneratedVariable<OWLClassExpression>
                aggregate(
                        Variable<?> expressionGeneratedVariable,
                        Collection<? extends Aggregandum<Collection<? extends OWLClassExpression>>> a);

        public OWLClassExpression visit(final OWLDataHasValue ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLDataMinCardinality ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLDataExactCardinality ce) {
            return generaliseClassExpression(ce);
        }

        public OWLClassExpression visit(final OWLDataMaxCardinality ce) {
            return generaliseClassExpression(ce);
        }
    }

    private ConstraintSystem constraintSystem;
    private final VariableProvider variableProvider;
    private final Map<OWLClassExpression, GeneratedVariable<? extends OWLClassExpression>> generalisedClassExpressionsVariables = new HashMap<OWLClassExpression, GeneratedVariable<? extends OWLClassExpression>>();
    private final Map<Collection<? extends Aggregandum<?>>, GeneratedVariable<?>> generatedVariableAggregations = new HashMap<Collection<? extends Aggregandum<?>>, GeneratedVariable<?>>();
    private final AssignmentMap substitutions = new AssignmentMap(
            Collections.<BindingNode> emptySet());
    private final OWLDataFactory factory;

    /** @param constraintSystem */
    public AbstractOWLObjectGeneralisation(final VariableProvider variableProvider) {
        factory = OWLManager.getOWLDataFactory();
        if (variableProvider == null) {
            throw new NullPointerException("The variable provider cannot be null");
        }
        this.variableProvider = variableProvider;
    }

    @Override
    protected OWLObject getDefaultReturnValue(final OWLObject object) {
        return object;
    }

    @Override
    public OWLObject visit(final IRI iri) {
        IRI toReturn = iri;
        Variable<?> v = getVariableProvider().getAbstractingVariable(iri);
        if (v != null) {
            storeSubstitution(v, getVariableProvider().getOWLEntity(iri));
            toReturn = new VariableIRI(v);
        }
        return toReturn;
    }

    @Override
    public OWLObject visit(final OWLAnnotation annotation) {
        OWLAnnotationProperty property = (OWLAnnotationProperty) annotation.getProperty()
                .accept(this);
        OWLAnnotationValue value = (OWLAnnotationValue) annotation.getValue()
                .accept(this);
        return factory.getOWLAnnotation(property, value);
    }

    @Override
    public OWLObject visit(final OWLAnnotationAssertionAxiom axiom) {
        OWLAnnotationSubject generalisedSubject = (OWLAnnotationSubject) axiom
                .getSubject().accept(this);
        OWLAnnotation generalisedAnnotation = (OWLAnnotation) axiom.getAnnotation()
                .accept(this);
        return factory.getOWLAnnotationAssertionAxiom(generalisedSubject,
                generalisedAnnotation);
    }

    @Override
    public OWLObject visit(final OWLAnnotationProperty property) {
        OWLAnnotationProperty toReturn = property;
        Variable<?> v = getVariableProvider().getAbstractingVariable(property);
        if (v != null) {
            toReturn = factory.getOWLAnnotationProperty(v.getIRI());
            storeSubstitution(v, property);
        }
        return toReturn;
    }

    @Override
    public OWLObject visit(final OWLAsymmetricObjectPropertyAxiom axiom) {
        OWLObjectPropertyExpression property = axiom.getProperty();
        return factory
                .getOWLAsymmetricObjectPropertyAxiom((OWLObjectPropertyExpression) property
                        .accept(this));
    }

    @Override
    public OWLClassExpression visit(final OWLClass desc) {
        OWLClassExpression toReturn = null;
        Variable<?> v = getVariableProvider().getAbstractingVariable(desc);
        if (v != null) {
            toReturn = factory.getOWLClass(v.getIRI());
            storeSubstitution(v, desc);
        } else {
            toReturn = desc;
        }
        return toReturn;
    }

    @Override
    public OWLObject visit(final OWLClassAssertionAxiom axiom) {
        OWLClassExpression description = axiom.getClassExpression();
        OWLIndividual individual = axiom.getIndividual();
        return factory.getOWLClassAssertionAxiom(
                (OWLClassExpression) description.accept(this),
                (OWLIndividual) individual.accept(this));
    }

    @Override
    public OWLObject visit(final OWLDataAllValuesFrom desc) {
        OWLDataRange filler = desc.getFiller();
        OWLDataPropertyExpression property = desc.getProperty();
        return factory.getOWLDataAllValuesFrom(
                (OWLDataPropertyExpression) property.accept(this),
                (OWLDataRange) filler.accept(this));
    }

    @Override
    public OWLObject visit(final OWLDataComplementOf node) {
        OWLDataRange dataRange = node.getDataRange();
        return factory.getOWLDataComplementOf((OWLDataRange) dataRange.accept(this));
    }

    @Override
    public OWLObject visit(final OWLDataExactCardinality desc) {
        int cardinality = desc.getCardinality();
        OWLDataRange filler = desc.getFiller();
        OWLDataPropertyExpression property = desc.getProperty();
        return factory.getOWLDataExactCardinality(cardinality,
                (OWLDataPropertyExpression) property.accept(this),
                (OWLDataRange) filler.accept(this));
    }

    @Override
    public OWLObject visit(final OWLDataHasValue desc) {
        OWLDataPropertyExpression property = desc.getProperty();
        OWLLiteral value = desc.getValue();
        return factory.getOWLDataHasValue(
                (OWLDataPropertyExpression) property.accept(this),
                (OWLLiteral) value.accept(this));
    }

    @Override
    public OWLObject visit(final OWLDataMaxCardinality desc) {
        int cardinality = desc.getCardinality();
        OWLDataRange filler = desc.getFiller();
        OWLDataPropertyExpression property = desc.getProperty();
        return factory.getOWLDataMaxCardinality(cardinality,
                (OWLDataPropertyExpression) property.accept(this),
                (OWLDataRange) filler.accept(this));
    }

    @Override
    public OWLObject visit(final OWLDataMinCardinality desc) {
        int cardinality = desc.getCardinality();
        OWLDataRange filler = desc.getFiller();
        OWLDataPropertyExpression property = desc.getProperty();
        return factory.getOWLDataMinCardinality(cardinality,
                (OWLDataPropertyExpression) property.accept(this),
                (OWLDataRange) filler.accept(this));
    }

    @Override
    public OWLObject visit(final OWLDataOneOf node) {
        Set<OWLLiteral> values = node.getValues();
        Set<OWLLiteral> instantiatedValues = new HashSet<OWLLiteral>();
        for (OWLLiteral constant : values) {
            instantiatedValues.add((OWLLiteral) constant.accept(this));
        }
        return factory.getOWLDataOneOf(instantiatedValues);
    }

    @Override
    public OWLObject visit(final OWLDataProperty property) {
        OWLDataProperty toReturn = property;
        Variable<?> v = getVariableProvider().getAbstractingVariable(property);
        if (v != null) {
            toReturn = factory.getOWLDataProperty(v.getIRI());
            storeSubstitution(v, property);
        }
        return toReturn;
    }

    @Override
    public OWLObject visit(final OWLDataPropertyAssertionAxiom axiom) {
        OWLIndividual subject = axiom.getSubject();
        OWLDataPropertyExpression property = axiom.getProperty();
        OWLLiteral object = axiom.getObject();
        return factory.getOWLDataPropertyAssertionAxiom(
                (OWLDataPropertyExpression) property.accept(this),
                (OWLIndividual) subject.accept(this), (OWLLiteral) object.accept(this));
    }

    @Override
    public OWLObject visit(final OWLDataPropertyDomainAxiom axiom) {
        OWLClassExpression domain = axiom.getDomain();
        OWLDataPropertyExpression property = axiom.getProperty();
        return factory.getOWLDataPropertyDomainAxiom(
                (OWLDataPropertyExpression) property.accept(this),
                (OWLClassExpression) domain.accept(this));
    }

    @Override
    public OWLObject visit(final OWLDataPropertyRangeAxiom axiom) {
        OWLDataPropertyExpression property = axiom.getProperty();
        OWLDataRange range = axiom.getRange();
        return factory.getOWLDataPropertyRangeAxiom(
                (OWLDataPropertyExpression) property.accept(this),
                (OWLDataRange) range.accept(this));
    }

    @Override
    public OWLObject visit(final OWLDataSomeValuesFrom desc) {
        OWLDataRange filler = desc.getFiller();
        OWLDataPropertyExpression property = desc.getProperty();
        return factory.getOWLDataSomeValuesFrom(
                (OWLDataPropertyExpression) property.accept(this),
                (OWLDataRange) filler.accept(this));
    }

    @Override
    public OWLObject visit(final OWLDatatypeRestriction node) {
        OWLDataRange dataRange = node.getDatatype();
        Set<OWLFacetRestriction> facetRestrictions = node.getFacetRestrictions();
        return factory.getOWLDatatypeRestriction((OWLDatatype) dataRange.accept(this),
                facetRestrictions);
    }

    @Override
    public OWLObject visit(final OWLDifferentIndividualsAxiom axiom) {
        Set<OWLIndividual> individuals = axiom.getIndividuals();
        Set<OWLIndividual> instatiatedDescriptions = new HashSet<OWLIndividual>();
        MultiMap<OWLIndividual, OWLIndividual> generalisationMap = new MultiMap<OWLIndividual, OWLIndividual>();
        for (OWLIndividual individual : individuals) {
            OWLIndividual generalised = (OWLIndividual) individual.accept(this);
            generalisationMap.put(generalised, individual);
        }
        Set<InlineSet<OWLIndividual>> inlineSets = new HashSet<InlineSet<OWLIndividual>>(
                generalisationMap.keySet().size());
        for (OWLIndividual generalisation : generalisationMap.keySet()) {
            Collection<OWLIndividual> set = generalisationMap.get(generalisation);
            if (set.size() > 1) {
                InlineSetBuilder<OWLIndividual> builder = InlineSetBuilder
                        .getOWLIndividualBuilder(getConstraintSystem(), factory);
                InlineSet<OWLIndividual> inlineSet = generalisation.accept(builder);
                inlineSets.add(inlineSet);
            } else {
                instatiatedDescriptions.add(generalisation);
            }
        }
        OWLDifferentIndividualsAxiom toReturn = factory
                .getOWLDifferentIndividualsAxiom(instatiatedDescriptions);
        if (!inlineSets.isEmpty()) {
            OPPLOWLDataFactory dataFactory = new OPPLOWLDataFactory(factory);
            InlineSet<OWLIndividual> set = InlineSet.buildInlineSet(dataFactory,
                    getConstraintSystem(), VariableTypeFactory
                            .getINDIVIDUALVariableType(), inlineSets,
                    instatiatedDescriptions
                            .toArray(new OWLIndividual[instatiatedDescriptions.size()]));
            toReturn = dataFactory.getOWLDifferentIndividualsAxiom(set, axiom
                    .getIndividuals().size() == 2);
        }
        return toReturn;
    }

    @Override
    public OWLObject visit(final OWLDisjointClassesAxiom axiom) {
        Set<OWLClassExpression> descriptions = axiom.getClassExpressions();
        Set<OWLClassExpression> instatiatedDescriptions = new HashSet<OWLClassExpression>();
        MultiMap<OWLClassExpression, OWLClassExpression> generalisationMap = new MultiMap<OWLClassExpression, OWLClassExpression>();
        for (OWLClassExpression classExpression : descriptions) {
            OWLClassExpression generalised = (OWLClassExpression) classExpression
                    .accept(this);
            generalisationMap.put(generalised, classExpression);
        }
        Set<InlineSet<OWLClassExpression>> inlineSets = new HashSet<InlineSet<OWLClassExpression>>(
                generalisationMap.keySet().size());
        for (OWLClassExpression generalisation : generalisationMap.keySet()) {
            Collection<OWLClassExpression> set = generalisationMap.get(generalisation);
            if (set.size() > 1) {
                InlineSetBuilder<OWLClassExpression> builder = InlineSetBuilder
                        .getOWLClassExpressionBuilder(getConstraintSystem(), factory);
                InlineSet<OWLClassExpression> inlineSet = generalisation.accept(builder);
                inlineSets.add(inlineSet);
            } else {
                instatiatedDescriptions.add(generalisation);
            }
        }
        OWLDisjointClassesAxiom toReturn = factory
                .getOWLDisjointClassesAxiom(instatiatedDescriptions);
        if (!inlineSets.isEmpty()) {
            OPPLOWLDataFactory dataFactory = new OPPLOWLDataFactory(factory);
            InlineSet<OWLClassExpression> set = InlineSet.buildInlineSet(dataFactory,
                    getConstraintSystem(), VariableTypeFactory.getCLASSVariableType(),
                    inlineSets, instatiatedDescriptions
                            .toArray(new OWLClassExpression[instatiatedDescriptions
                                    .size()]));
            toReturn = dataFactory.getOWLDisjointClassesAxiom(set, axiom
                    .getClassExpressions().size() == 2);
        }
        return toReturn;
    }

    @Override
    public OWLObject visit(final OWLDisjointDataPropertiesAxiom axiom) {
        Set<OWLDataPropertyExpression> properties = axiom.getProperties();
        Set<OWLDataPropertyExpression> instatiatedProperties = new HashSet<OWLDataPropertyExpression>();
        MultiMap<OWLDataPropertyExpression, OWLDataProperty> generalisationMap = new MultiMap<OWLDataPropertyExpression, OWLDataProperty>();
        for (OWLDataPropertyExpression property : properties) {
            OWLDataPropertyExpression generalised = (OWLDataPropertyExpression) property
                    .accept(this);
            generalisationMap.put(generalised, property.asOWLDataProperty());
        }
        Set<InlineSet<OWLDataPropertyExpression>> inlineSets = new HashSet<InlineSet<OWLDataPropertyExpression>>(
                generalisationMap.keySet().size());
        for (OWLDataPropertyExpression generalisation : generalisationMap.keySet()) {
            Collection<OWLDataProperty> set = generalisationMap.get(generalisation);
            if (set.size() > 1) {
                InlineSetBuilder<OWLDataPropertyExpression> builder = InlineSetBuilder
                        .getOWLDataPropertyExpressionBuilder(getConstraintSystem(),
                                factory);
                InlineSet<OWLDataPropertyExpression> inlineSet = generalisation
                        .accept(builder);
                inlineSets.add(inlineSet);
            } else {
                instatiatedProperties.add(generalisation);
            }
        }
        OWLDisjointDataPropertiesAxiom toReturn = factory
                .getOWLDisjointDataPropertiesAxiom(instatiatedProperties);
        if (!inlineSets.isEmpty()) {
            OPPLOWLDataFactory dataFactory = new OPPLOWLDataFactory(factory);
            InlineSet<OWLDataPropertyExpression> set = InlineSet.buildInlineSet(
                    dataFactory, getConstraintSystem(), VariableTypeFactory
                            .getDATAPROPERTYVariableType(), inlineSets,
                    instatiatedProperties
                            .toArray(new OWLDataProperty[instatiatedProperties.size()]));
            toReturn = dataFactory.getOWLDisjointDataPropertiesAxiom(set, axiom
                    .getProperties().size() == 2);
        }
        return toReturn;
    }

    @Override
    public OWLObject visit(final OWLDisjointObjectPropertiesAxiom axiom) {
        Set<OWLObjectPropertyExpression> properties = axiom.getProperties();
        Set<OWLObjectPropertyExpression> instatiatedProperties = new HashSet<OWLObjectPropertyExpression>();
        MultiMap<OWLObjectPropertyExpression, OWLObjectPropertyExpression> generalisationMap = new MultiMap<OWLObjectPropertyExpression, OWLObjectPropertyExpression>();
        for (OWLObjectPropertyExpression property : properties) {
            OWLObjectPropertyExpression generalised = (OWLObjectPropertyExpression) property
                    .accept(this);
            generalisationMap.put(generalised, property);
        }
        Set<InlineSet<OWLObjectPropertyExpression>> inlineSets = new HashSet<InlineSet<OWLObjectPropertyExpression>>(
                generalisationMap.keySet().size());
        for (OWLObjectPropertyExpression generalisation : generalisationMap.keySet()) {
            Collection<OWLObjectPropertyExpression> set = generalisationMap
                    .get(generalisation);
            if (set.size() > 1) {
                InlineSetBuilder<OWLObjectPropertyExpression> builder = InlineSetBuilder
                        .getOWLObjectPropertyExpressionBuilder(getConstraintSystem(),
                                factory);
                InlineSet<OWLObjectPropertyExpression> inlineSet = generalisation
                        .accept(builder);
                inlineSets.add(inlineSet);
            } else {
                instatiatedProperties.add(generalisation);
            }
        }
        OWLDisjointObjectPropertiesAxiom toReturn = factory
                .getOWLDisjointObjectPropertiesAxiom(instatiatedProperties);
        if (!inlineSets.isEmpty()) {
            OPPLOWLDataFactory dataFactory = new OPPLOWLDataFactory(factory);
            InlineSet<OWLObjectPropertyExpression> set = InlineSet
                    .buildInlineSet(
                            dataFactory,
                            getConstraintSystem(),
                            VariableTypeFactory.getOBJECTPROPERTYTypeVariableType(),
                            inlineSets,
                            instatiatedProperties
                                    .toArray(new OWLObjectPropertyExpression[instatiatedProperties
                                            .size()]));
            toReturn = dataFactory.getOWLDisjointObjectPropertiesAxiom(set, axiom
                    .getProperties().size() == 2);
        }
        return toReturn;
    }

    @Override
    public OWLObject visit(final OWLDisjointUnionAxiom axiom) {
        Set<OWLClassExpression> descriptions = axiom.getClassExpressions();
        OWLClass owlClass = axiom.getOWLClass();
        Set<OWLClassExpression> instantiatedDescriptions = axiom.getClassExpressions();
        for (OWLClassExpression description : descriptions) {
            instantiatedDescriptions.add((OWLClassExpression) description.accept(this));
        }
        return factory.getOWLDisjointUnionAxiom((OWLClass) owlClass.accept(this),
                instantiatedDescriptions);
    }

    @Override
    public OWLObject visit(final OWLEquivalentClassesAxiom axiom) {
        Set<OWLClassExpression> descriptions = axiom.getClassExpressions();
        Set<OWLClassExpression> instantiatedDescriptions = new HashSet<OWLClassExpression>();
        for (OWLClassExpression description : descriptions) {
            instantiatedDescriptions.add((OWLClassExpression) description.accept(this));
        }
        return factory.getOWLEquivalentClassesAxiom(instantiatedDescriptions);
    }

    @Override
    public OWLObject visit(final OWLEquivalentDataPropertiesAxiom axiom) {
        Set<OWLDataPropertyExpression> properties = axiom.getProperties();
        Set<OWLDataPropertyExpression> instantiatedProperties = new HashSet<OWLDataPropertyExpression>();
        for (OWLDataPropertyExpression dataPropertyExpression : properties) {
            instantiatedProperties.add((OWLDataPropertyExpression) dataPropertyExpression
                    .accept(this));
        }
        return factory.getOWLEquivalentDataPropertiesAxiom(instantiatedProperties);
    }

    @Override
    public OWLObject visit(final OWLEquivalentObjectPropertiesAxiom axiom) {
        Set<OWLObjectPropertyExpression> properties = axiom.getProperties();
        Set<OWLObjectPropertyExpression> instantiatedProperties = new HashSet<OWLObjectPropertyExpression>();
        for (OWLObjectPropertyExpression objectPropertyExpression : properties) {
            instantiatedProperties
                    .add((OWLObjectPropertyExpression) objectPropertyExpression
                            .accept(this));
        }
        return factory.getOWLEquivalentObjectPropertiesAxiom(instantiatedProperties);
    }

    @Override
    public OWLObject visit(final OWLFunctionalDataPropertyAxiom axiom) {
        OWLDataPropertyExpression property = axiom.getProperty();
        return factory
                .getOWLFunctionalDataPropertyAxiom((OWLDataPropertyExpression) property
                        .accept(this));
    }

    @Override
    public OWLObject visit(final OWLFunctionalObjectPropertyAxiom axiom) {
        OWLObjectPropertyExpression property = axiom.getProperty();
        return factory
                .getOWLFunctionalObjectPropertyAxiom((OWLObjectPropertyExpression) property
                        .accept(this));
    }

    @Override
    public OWLObject visit(final OWLInverseFunctionalObjectPropertyAxiom axiom) {
        OWLObjectPropertyExpression property = axiom.getProperty();
        return factory
                .getOWLInverseFunctionalObjectPropertyAxiom((OWLObjectPropertyExpression) property
                        .accept(this));
    }

    @Override
    public OWLObject visit(final OWLInverseObjectPropertiesAxiom axiom) {
        OWLObjectPropertyExpression firstProperty = axiom.getFirstProperty();
        OWLObjectPropertyExpression secondProperty = axiom.getSecondProperty();
        return factory.getOWLInverseObjectPropertiesAxiom(
                (OWLObjectPropertyExpression) firstProperty.accept(this),
                (OWLObjectPropertyExpression) secondProperty.accept(this));
    }

    @Override
    public OWLObject visit(final OWLIrreflexiveObjectPropertyAxiom axiom) {
        OWLObjectPropertyExpression property = axiom.getProperty();
        return factory
                .getOWLIrreflexiveObjectPropertyAxiom((OWLObjectPropertyExpression) property
                        .accept(this));
    }

    @Override
    public OWLObject visit(final OWLLiteral node) {
        OWLLiteral toReturn = null;
        Variable<?> v = getVariableProvider().getAbstractingVariable(node);
        if (v != null) {
            toReturn = factory.getOWLLiteral(v.getName());
            storeSubstitution(v, node);
        } else {
            toReturn = node;
        }
        return toReturn;
    }

    @Override
    public OWLObject visit(final OWLNamedIndividual individual) {
        OWLIndividual toReturn = individual;
        Variable<?> v = getVariableProvider().getAbstractingVariable(individual);
        if (v != null) {
            toReturn = factory.getOWLNamedIndividual(v.getIRI());
            storeSubstitution(v, individual);
        }
        return toReturn;
    }

    @Override
    public OWLObject visit(final OWLNegativeDataPropertyAssertionAxiom axiom) {
        OWLDataPropertyExpression property = axiom.getProperty();
        OWLIndividual subject = axiom.getSubject();
        OWLLiteral object = axiom.getObject();
        return factory.getOWLNegativeDataPropertyAssertionAxiom(
                (OWLDataPropertyExpression) property.accept(this),
                (OWLIndividual) subject.accept(this), (OWLLiteral) object.accept(this));
    }

    @Override
    public OWLObject visit(final OWLNegativeObjectPropertyAssertionAxiom axiom) {
        OWLObjectPropertyExpression property = axiom.getProperty();
        OWLIndividual subject = axiom.getSubject();
        OWLIndividual object = axiom.getObject();
        OWLIndividual instantiatedSubject = (OWLIndividual) subject.accept(this);
        OWLObjectPropertyExpression instantiatedProperty = (OWLObjectPropertyExpression) property
                .accept(this);
        OWLIndividual instantiatedObject = (OWLIndividual) object.accept(this);
        return factory.getOWLNegativeObjectPropertyAssertionAxiom(instantiatedProperty,
                instantiatedSubject, instantiatedObject);
    }

    @Override
    public OWLClassExpression visit(final OWLObjectAllValuesFrom desc) {
        OWLClassExpression filler = desc.getFiller();
        OWLObjectPropertyExpression property = desc.getProperty();
        return factory.getOWLObjectAllValuesFrom(
                (OWLObjectPropertyExpression) property.accept(this),
                (OWLClassExpression) filler.accept(this));
    }

    @Override
    public OWLClassExpression visit(final OWLObjectComplementOf desc) {
        OWLClassExpression operand = desc.getOperand();
        return factory
                .getOWLObjectComplementOf((OWLClassExpression) operand.accept(this));
    }

    @Override
    public OWLObject visit(final OWLObjectExactCardinality desc) {
        int cardinality = desc.getCardinality();
        OWLClassExpression filler = desc.getFiller();
        OWLObjectPropertyExpression property = desc.getProperty();
        return factory.getOWLObjectExactCardinality(cardinality,
                (OWLObjectPropertyExpression) property.accept(this),
                (OWLClassExpression) filler.accept(this));
    }

    @Override
    public OWLObject visit(final OWLObjectHasSelf desc) {
        OWLObjectPropertyExpression property = desc.getProperty();
        return factory.getOWLObjectHasSelf((OWLObjectPropertyExpression) property
                .accept(this));
    }

    @Override
    public OWLClassExpression visit(final OWLObjectHasValue desc) {
        OWLObjectPropertyExpression property = desc.getProperty();
        OWLIndividual value = desc.getValue();
        return factory.getOWLObjectHasValue(
                (OWLObjectPropertyExpression) property.accept(this),
                (OWLIndividual) value.accept(this));
    }

    @Override
    public OWLClassExpression visit(final OWLObjectIntersectionOf desc) {
        Set<OWLClassExpression> operands = desc.getOperands();
        Set<OWLClassExpression> newOperands = new HashSet<OWLClassExpression>(
                operands.size());
        MultiMap<OWLClassExpression, OWLClassExpression> generalisationMap = new MultiMap<OWLClassExpression, OWLClassExpression>();
        for (OWLClassExpression classExpression : operands) {
            OWLClassExpression generalised = (OWLClassExpression) classExpression
                    .accept(this);
            generalisationMap.put(generalised, classExpression);
        }
        for (OWLClassExpression generalisation : generalisationMap.keySet()) {
            if (generalisationMap.get(generalisation).size() > 1) {
                newOperands.add(generalisation.accept(new FunctionGeneralisation() {
                    @Override
                    protected
                            GeneratedVariable<OWLClassExpression>
                            aggregate(
                                    final Variable<?> expressionGeneratedVariable,
                                    final Collection<? extends Aggregandum<Collection<? extends OWLClassExpression>>> a) {
                        GeneratedVariable<OWLClassExpression> toReturn = (GeneratedVariable<OWLClassExpression>) generatedVariableAggregations
                                .get(a);
                        if (toReturn == null) {
                            toReturn = AbstractOWLObjectGeneralisation.this
                                    .getConstraintSystem()
                                    .createIntersectionGeneratedVariable(
                                            String.format("%s_conjunction",
                                                    expressionGeneratedVariable.getName()),
                                            expressionGeneratedVariable.getType(), a);
                            generatedVariableAggregations.put(a, toReturn);
                        }
                        return toReturn;
                    }
                }));
            } else {
                newOperands.add(generalisation);
            }
        }
        return factory.getOWLObjectIntersectionOf(new HashSet<OWLClassExpression>(
                newOperands));
    }

    @Override
    public OWLObject visit(final OWLObjectInverseOf property) {
        OWLObjectPropertyExpression inverse = property.getInverse();
        return factory.getOWLObjectInverseOf((OWLObjectPropertyExpression) inverse
                .accept(this));
    }

    @Override
    public OWLObject visit(final OWLObjectMaxCardinality desc) {
        int cardinality = desc.getCardinality();
        OWLClassExpression filler = desc.getFiller();
        OWLObjectPropertyExpression property = desc.getProperty();
        return factory.getOWLObjectMaxCardinality(cardinality,
                (OWLObjectPropertyExpression) property.accept(this),
                (OWLClassExpression) filler.accept(this));
    }

    @Override
    public OWLObject visit(final OWLObjectMinCardinality desc) {
        int cardinality = desc.getCardinality();
        OWLClassExpression filler = desc.getFiller();
        OWLObjectPropertyExpression property = desc.getProperty();
        return factory.getOWLObjectMinCardinality(cardinality,
                (OWLObjectPropertyExpression) property.accept(this),
                (OWLClassExpression) filler.accept(this));
    }

    @Override
    public OWLObject visit(final OWLObjectOneOf desc) {
        Set<OWLIndividual> instantiatedIndividuals = new HashSet<OWLIndividual>(
                this.generaliseCollection(desc.getIndividuals()));
        return factory.getOWLObjectOneOf(instantiatedIndividuals);
    }

    @Override
    public OWLObject visit(final OWLObjectProperty property) {
        OWLObjectProperty toReturn = property;
        Variable<?> v = getVariableProvider().getAbstractingVariable(property);
        if (v != null) {
            toReturn = factory.getOWLObjectProperty(v.getIRI());
            storeSubstitution(v, property);
        }
        return toReturn;
    }

    @Override
    public OWLObject visit(final OWLObjectPropertyAssertionAxiom axiom) {
        OWLIndividual subject = axiom.getSubject();
        OWLObjectPropertyExpression property = axiom.getProperty();
        OWLIndividual object = axiom.getObject();
        return factory
                .getOWLObjectPropertyAssertionAxiom(
                        (OWLObjectPropertyExpression) property.accept(this),
                        (OWLIndividual) subject.accept(this),
                        (OWLIndividual) object.accept(this));
    }

    @Override
    public OWLObject visit(final OWLObjectPropertyDomainAxiom axiom) {
        OWLClassExpression domain = axiom.getDomain();
        OWLObjectPropertyExpression property = axiom.getProperty();
        return factory.getOWLObjectPropertyDomainAxiom(
                (OWLObjectPropertyExpression) property.accept(this),
                (OWLClassExpression) domain.accept(this));
    }

    @Override
    public OWLObject visit(final OWLObjectPropertyRangeAxiom axiom) {
        OWLObjectPropertyExpression property = axiom.getProperty();
        OWLClassExpression range = axiom.getRange();
        return factory.getOWLObjectPropertyRangeAxiom(
                (OWLObjectPropertyExpression) property.accept(this),
                (OWLClassExpression) range.accept(this));
    }

    @Override
    public OWLClassExpression visit(final OWLObjectSomeValuesFrom desc) {
        OWLClassExpression filler = desc.getFiller();
        OWLObjectPropertyExpression property = desc.getProperty();
        return factory.getOWLObjectSomeValuesFrom(
                (OWLObjectPropertyExpression) property.accept(this),
                (OWLClassExpression) filler.accept(this));
    }

    @Override
    public OWLObject visit(final OWLReflexiveObjectPropertyAxiom axiom) {
        OWLObjectPropertyExpression property = axiom.getProperty();
        return factory
                .getOWLReflexiveObjectPropertyAxiom((OWLObjectPropertyExpression) property
                        .accept(this));
    }

    @Override
    public OWLObject visit(final OWLSameIndividualAxiom axiom) {
        Set<OWLIndividual> individuals = axiom.getIndividuals();
        Set<OWLIndividual> instatiatedDescriptions = new HashSet<OWLIndividual>();
        MultiMap<OWLIndividual, OWLIndividual> generalisationMap = new MultiMap<OWLIndividual, OWLIndividual>();
        for (OWLIndividual individual : individuals) {
            OWLIndividual generalised = (OWLIndividual) individual.accept(this);
            generalisationMap.put(generalised, individual);
        }
        Set<InlineSet<OWLIndividual>> inlineSets = new HashSet<InlineSet<OWLIndividual>>(
                generalisationMap.keySet().size());
        for (OWLIndividual generalisation : generalisationMap.keySet()) {
            Collection<OWLIndividual> set = generalisationMap.get(generalisation);
            if (set.size() > 1) {
                InlineSetBuilder<OWLIndividual> builder = InlineSetBuilder
                        .getOWLIndividualBuilder(getConstraintSystem(), factory);
                InlineSet<OWLIndividual> inlineSet = generalisation.accept(builder);
                inlineSets.add(inlineSet);
            } else {
                instatiatedDescriptions.add(generalisation);
            }
        }
        OWLSameIndividualAxiom toReturn = factory
                .getOWLSameIndividualAxiom(instatiatedDescriptions);
        if (!inlineSets.isEmpty()) {
            OPPLOWLDataFactory dataFactory = new OPPLOWLDataFactory(factory);
            InlineSet<OWLIndividual> set = InlineSet.buildInlineSet(dataFactory,
                    getConstraintSystem(), VariableTypeFactory
                            .getINDIVIDUALVariableType(), inlineSets,
                    instatiatedDescriptions
                            .toArray(new OWLIndividual[instatiatedDescriptions.size()]));
            toReturn = dataFactory.getOWLSameIndividualAxiom(set, axiom.getIndividuals()
                    .size() == 2);
        }
        return toReturn;
    }

    @Override
    public OWLObject visit(final OWLSubClassOfAxiom axiom) {
        OWLClassExpression superClass = (OWLClassExpression) axiom.getSuperClass()
                .accept(this);
        OWLClassExpression subClass = (OWLClassExpression) axiom.getSubClass().accept(
                this);
        return factory.getOWLSubClassOfAxiom(subClass, superClass);
    }

    @Override
    public OWLObject visit(final OWLSubDataPropertyOfAxiom axiom) {
        OWLDataPropertyExpression subProperty = axiom.getSubProperty();
        OWLDataPropertyExpression superProperty = axiom.getSuperProperty();
        OWLDataPropertyExpression generalisedSubProperty = (OWLDataPropertyExpression) subProperty
                .accept(this);
        OWLDataPropertyExpression generalisedSuperProperty = (OWLDataPropertyExpression) superProperty
                .accept(this);
        return factory.getOWLSubDataPropertyOfAxiom(generalisedSubProperty,
                generalisedSuperProperty);
    }

    @Override
    public OWLObject visit(final OWLSubObjectPropertyOfAxiom axiom) {
        OWLObjectPropertyExpression subProperty = axiom.getSubProperty();
        OWLObjectPropertyExpression superProperty = axiom.getSuperProperty();
        OWLObjectPropertyExpression generalisedSubProperty = (OWLObjectPropertyExpression) subProperty
                .accept(this);
        return factory.getOWLSubObjectPropertyOfAxiom(generalisedSubProperty,
                (OWLObjectPropertyExpression) superProperty.accept(this));
    }

    @Override
    public OWLObject visit(final OWLSubPropertyChainOfAxiom axiom) {
        OWLObjectPropertyExpression superProperty = axiom.getSuperProperty();
        List<? extends OWLObjectPropertyExpression> instantiatedPropertyChain = new ArrayList<OWLObjectPropertyExpression>(
                this.generaliseCollection(axiom.getPropertyChain()));
        return factory.getOWLSubPropertyChainOfAxiom(instantiatedPropertyChain,
                (OWLObjectPropertyExpression) superProperty.accept(this));
    }

    @Override
    public OWLObject visit(final OWLSymmetricObjectPropertyAxiom axiom) {
        OWLObjectPropertyExpression property = axiom.getProperty();
        return factory
                .getOWLSymmetricObjectPropertyAxiom((OWLObjectPropertyExpression) property
                        .accept(this));
    }

    @Override
    public OWLObject visit(final OWLTransitiveObjectPropertyAxiom axiom) {
        OWLObjectPropertyExpression property = axiom.getProperty();
        return factory
                .getOWLTransitiveObjectPropertyAxiom((OWLObjectPropertyExpression) property
                        .accept(this));
    }

    public void clearSubstitutions() {
        substitutions.clear();
    }

    @SuppressWarnings("unchecked")
    protected final <O extends OWLObject> Collection<? extends O> generaliseCollection(
            final Collection<? extends O> collection) {
        Set<O> toReturn = new HashSet<O>();
        for (O o : collection) {
            toReturn.add((O) o.accept(this));
        }
        return toReturn;
    }

    /** @return the constraintSystem */
    public ConstraintSystem getConstraintSystem() {
        if (constraintSystem == null) {
            throw new NullPointerException("The constraint system cannot be null");
        }
        return constraintSystem;
    }

    public void setConstraintSystem(final ConstraintSystem cs) {
        constraintSystem = cs;
    }

    /** @return the substitutions */
    public AssignmentMap getSubstitutions() {
        return new AssignmentMap(substitutions);
    }

    @Override
    public OWLClassExpression visit(final OWLObjectUnionOf desc) {
        Set<OWLClassExpression> operands = desc.getOperands();
        Set<OWLClassExpression> newOperands = new HashSet<OWLClassExpression>(
                operands.size());
        MultiMap<OWLClassExpression, OWLClassExpression> generalisationMap = new MultiMap<OWLClassExpression, OWLClassExpression>();
        for (OWLClassExpression classExpression : operands) {
            OWLClassExpression generalised = (OWLClassExpression) classExpression
                    .accept(this);
            generalisationMap.put(generalised, classExpression);
        }
        for (OWLClassExpression generalisation : generalisationMap.keySet()) {
            if (generalisationMap.get(generalisation).size() > 1) {
                newOperands.add(generalisation.accept(new FunctionGeneralisation() {
                    @Override
                    protected
                            GeneratedVariable<OWLClassExpression>
                            aggregate(
                                    final Variable<?> expressionGeneratedVariable,
                                    final Collection<? extends Aggregandum<Collection<? extends OWLClassExpression>>> a) {
                        GeneratedVariable<OWLClassExpression> toReturn = (GeneratedVariable<OWLClassExpression>) generatedVariableAggregations
                                .get(a);
                        if (toReturn == null) {
                            toReturn = AbstractOWLObjectGeneralisation.this
                                    .getConstraintSystem()
                                    .createUnionGeneratedVariable(
                                            String.format("%s_disjunction",
                                                    expressionGeneratedVariable.getName()),
                                            expressionGeneratedVariable.getType(), a);
                            generatedVariableAggregations.put(a, toReturn);
                        }
                        return toReturn;
                    }
                }));
            } else {
                newOperands.add(generalisation);
            }
        }
        return factory.getOWLObjectUnionOf(new HashSet<OWLClassExpression>(newOperands));
    }

    private void storeSubstitution(final Variable<?> variable, final OWLObject owlObject) {
        Set<OWLObject> assignments = substitutions.get(variable);
        if (assignments == null) {
            assignments = new HashSet<OWLObject>();
            substitutions.put(variable, assignments);
        }
        assignments.add(owlObject);
    }

    /** @return the variableProvider */
    public VariableProvider getVariableProvider() {
        return variableProvider;
    }
}
