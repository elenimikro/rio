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
/**
 * 
 */
package org.coode.owl.generalise;

import java.util.Collection;
import java.util.Collections;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.Variable;
import org.coode.oppl.function.Adapter;
import org.coode.oppl.function.Aggregandum;
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
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
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
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

/**
 * @author Luigi Iannone
 * 
 */
public abstract class InlineSetBuilder<O extends OWLObject> extends
		OWLObjectVisitorExAdapter<InlineSet<O>> implements
		OWLObjectVisitorEx<InlineSet<O>> {
	private final ConstraintSystem constraintSystem;
	private final OWLDataFactory dataFactory;

	/**
	 * @param constraintSystem
	 */
	public InlineSetBuilder(ConstraintSystem constraintSystem, OWLDataFactory dataFactory) {
		if (constraintSystem == null) {
			throw new NullPointerException("The constraint system cannot be null");
		}
		if (dataFactory == null) {
			throw new NullPointerException("The data factory cannot be null");
		}
		this.constraintSystem = constraintSystem;
		this.dataFactory = dataFactory;
	}

	public static InlineSetBuilder<OWLObjectPropertyExpression> getOWLObjectPropertyExpressionBuilder(
			ConstraintSystem constraintSystem, OWLDataFactory dataFactory) {
		return new InlineSetBuilder<OWLObjectPropertyExpression>(constraintSystem,
				dataFactory) {
			@Override
			public InlineSet<OWLObjectPropertyExpression> visit(
					final OWLObjectProperty property) {
				InlineSet<OWLObjectPropertyExpression> toReturn = buildSingletonInlineSet(
						property,
						VariableTypeFactory.getOBJECTPROPERTYTypeVariableType(),
						this.getDataFactory(), this.getConstraintSystem());
				final Variable<?> variable = this.getConstraintSystem().getVariable(
						property.getIRI());
				if (variable != null) {
					toReturn = variable
							.getType()
							.accept(new VariableTypeVisitorEx<InlineSet<OWLObjectPropertyExpression>>() {
								public InlineSet<OWLObjectPropertyExpression> visitCLASSVariableType(
										CLASSVariableType classVariableType) {
									return buildSingletonInlineSet(property,
											VariableTypeFactory
													.getOBJECTPROPERTYTypeVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								@SuppressWarnings("unchecked")
								public InlineSet<OWLObjectPropertyExpression> visitOBJECTPROPERTYVariableType(
										OBJECTPROPERTYVariableType objectpropertyVariableType) {
									// I am sure the type is
									// InlineSet<OWLObjectPropertyExpression>
									return (InlineSet<OWLObjectPropertyExpression>) buildVariableValuesInlineSet(
											variable, getDataFactory(),
											getConstraintSystem());
								}

								public InlineSet<OWLObjectPropertyExpression> visitDATAPROPERTYVariableType(
										DATAPROPERTYVariableType datapropertyVariableType) {
									return buildSingletonInlineSet(property,
											VariableTypeFactory
													.getOBJECTPROPERTYTypeVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								public InlineSet<OWLObjectPropertyExpression> visitINDIVIDUALVariableType(
										INDIVIDUALVariableType individualVariableType) {
									return buildSingletonInlineSet(property,
											VariableTypeFactory
													.getOBJECTPROPERTYTypeVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								public InlineSet<OWLObjectPropertyExpression> visitCONSTANTVariableType(
										CONSTANTVariableType constantVariableType) {
									return buildSingletonInlineSet(property,
											VariableTypeFactory
													.getOBJECTPROPERTYTypeVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								public InlineSet<OWLObjectPropertyExpression> visitANNOTATIONPROPERTYVariableType(
										ANNOTATIONPROPERTYVariableType annotationpropertyVariableType) {
									return buildSingletonInlineSet(property,
											VariableTypeFactory
													.getOBJECTPROPERTYTypeVariableType(),
											getDataFactory(), getConstraintSystem());
								}
							});
				}
				return toReturn;
			}

			@Override
			public InlineSet<OWLObjectPropertyExpression> visit(
					OWLObjectInverseOf property) {
				return this.generaliseObjectPropertyExpression(property);
			}

			private InlineSet<OWLObjectPropertyExpression> generaliseObjectPropertyExpression(
					OWLObjectPropertyExpression property) {
				String name = "?expression";
				Variable<?> variable = this.getConstraintSystem().getVariable(name);
				int i = 1;
				String newName = name;
				while (variable != null) {
					newName = String.format("%s_%d", name, i++);
					variable = this.getConstraintSystem().getVariable(newName);
				}
				GeneratedVariable<OWLObjectPropertyExpression> expressionGeneratedVariable = this
						.getConstraintSystem().createExpressionGeneratedVariable(newName,
								property);
				this.getConstraintSystem().importVariable(expressionGeneratedVariable);
				return buildVariableValuesInlineSet(expressionGeneratedVariable,
						this.getDataFactory(), this.getConstraintSystem());
			}
		};
	}

	public static InlineSetBuilder<OWLIndividual> getOWLIndividualBuilder(
			ConstraintSystem constraintSystem, OWLDataFactory dataFactory) {
		return new InlineSetBuilder<OWLIndividual>(constraintSystem, dataFactory) {
			@Override
			public InlineSet<OWLIndividual> visit(final OWLNamedIndividual individual) {
				InlineSet<OWLIndividual> toReturn = buildSingletonInlineSet(individual,
						VariableTypeFactory.getINDIVIDUALVariableType(),
						this.getDataFactory(), this.getConstraintSystem());
				final Variable<?> variable = this.getConstraintSystem().getVariable(
						individual.getIRI());
				if (variable != null) {
					toReturn = variable.getType().accept(
							new VariableTypeVisitorEx<InlineSet<OWLIndividual>>() {
								public InlineSet<OWLIndividual> visitCLASSVariableType(
										CLASSVariableType classVariableType) {
									return buildSingletonInlineSet(individual,
											VariableTypeFactory
													.getINDIVIDUALVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								public InlineSet<OWLIndividual> visitOBJECTPROPERTYVariableType(
										OBJECTPROPERTYVariableType objectpropertyVariableType) {
									return buildSingletonInlineSet(individual,
											VariableTypeFactory
													.getINDIVIDUALVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								public InlineSet<OWLIndividual> visitDATAPROPERTYVariableType(
										DATAPROPERTYVariableType datapropertyVariableType) {
									return buildSingletonInlineSet(individual,
											VariableTypeFactory
													.getINDIVIDUALVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								@SuppressWarnings("unchecked")
								public InlineSet<OWLIndividual> visitINDIVIDUALVariableType(
										INDIVIDUALVariableType individualVariableType) {
									// I am sure the type is
									// InlineSet<OWLIndividual>
									return (InlineSet<OWLIndividual>) buildVariableValuesInlineSet(
											variable, getDataFactory(),
											getConstraintSystem());
								}

								public InlineSet<OWLIndividual> visitCONSTANTVariableType(
										CONSTANTVariableType constantVariableType) {
									return buildSingletonInlineSet(individual,
											VariableTypeFactory
													.getINDIVIDUALVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								public InlineSet<OWLIndividual> visitANNOTATIONPROPERTYVariableType(
										ANNOTATIONPROPERTYVariableType annotationpropertyVariableType) {
									return buildSingletonInlineSet(individual,
											VariableTypeFactory
													.getINDIVIDUALVariableType(),
											getDataFactory(), getConstraintSystem());
								}
							});
				}
				return toReturn;
			}
		};
	}

	public static InlineSetBuilder<OWLDataPropertyExpression> getOWLDataPropertyExpressionBuilder(
			ConstraintSystem constraintSystem, OWLDataFactory dataFactory) {
		return new InlineSetBuilder<OWLDataPropertyExpression>(constraintSystem,
				dataFactory) {
			@Override
			public InlineSet<OWLDataPropertyExpression> visit(
					final OWLDataProperty property) {
				InlineSet<OWLDataPropertyExpression> toReturn = buildSingletonInlineSet(
						property, VariableTypeFactory.getDATAPROPERTYVariableType(),
						this.getDataFactory(), this.getConstraintSystem());
				final Variable<?> variable = this.getConstraintSystem().getVariable(
						property.getIRI());
				if (variable != null) {
					toReturn = variable
							.getType()
							.accept(new VariableTypeVisitorEx<InlineSet<OWLDataPropertyExpression>>() {
								public InlineSet<OWLDataPropertyExpression> visitCLASSVariableType(
										CLASSVariableType classVariableType) {
									return buildSingletonInlineSet(property,
											VariableTypeFactory
													.getDATAPROPERTYVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								public InlineSet<OWLDataPropertyExpression> visitOBJECTPROPERTYVariableType(
										OBJECTPROPERTYVariableType objectpropertyVariableType) {
									return buildSingletonInlineSet(property,
											VariableTypeFactory
													.getDATAPROPERTYVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								@SuppressWarnings("unchecked")
								public InlineSet<OWLDataPropertyExpression> visitDATAPROPERTYVariableType(
										DATAPROPERTYVariableType datapropertyVariableType) {
									// I am sure the type is
									// InlineSet<OWLDataProperty>
									return (InlineSet<OWLDataPropertyExpression>) buildVariableValuesInlineSet(
											variable, getDataFactory(),
											getConstraintSystem());
								}

								public InlineSet<OWLDataPropertyExpression> visitINDIVIDUALVariableType(
										INDIVIDUALVariableType individualVariableType) {
									return buildSingletonInlineSet(property,
											VariableTypeFactory
													.getDATAPROPERTYVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								public InlineSet<OWLDataPropertyExpression> visitCONSTANTVariableType(
										CONSTANTVariableType constantVariableType) {
									return buildSingletonInlineSet(property,
											VariableTypeFactory
													.getDATAPROPERTYVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								public InlineSet<OWLDataPropertyExpression> visitANNOTATIONPROPERTYVariableType(
										ANNOTATIONPROPERTYVariableType annotationpropertyVariableType) {
									return buildSingletonInlineSet(property,
											VariableTypeFactory
													.getDATAPROPERTYVariableType(),
											getDataFactory(), getConstraintSystem());
								}
							});
				}
				return toReturn;
			}
		};
	}

	public static InlineSetBuilder<OWLClassExpression> getOWLClassExpressionBuilder(
			ConstraintSystem constraintSystem, OWLDataFactory dataFactory) {
		return new InlineSetBuilder<OWLClassExpression>(constraintSystem, dataFactory) {
			@Override
			public InlineSet<OWLClassExpression> visit(final OWLClass desc) {
				InlineSet<OWLClassExpression> toReturn = buildSingletonInlineSet(desc,
						VariableTypeFactory.getCLASSVariableType(),
						this.getDataFactory(), this.getConstraintSystem());
				final Variable<?> variable = this.getConstraintSystem().getVariable(
						desc.getIRI());
				if (variable != null) {
					toReturn = variable.getType().accept(
							new VariableTypeVisitorEx<InlineSet<OWLClassExpression>>() {
								@SuppressWarnings("unchecked")
								public InlineSet<OWLClassExpression> visitCLASSVariableType(
										CLASSVariableType classVariableType) {
									// I am sure the type is
									// InlineSet<OWLClassExpression>
									return (InlineSet<OWLClassExpression>) buildVariableValuesInlineSet(
											variable, getDataFactory(),
											getConstraintSystem());
								}

								public InlineSet<OWLClassExpression> visitOBJECTPROPERTYVariableType(
										OBJECTPROPERTYVariableType objectpropertyVariableType) {
									return buildSingletonInlineSet(desc,
											VariableTypeFactory.getCLASSVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								public InlineSet<OWLClassExpression> visitDATAPROPERTYVariableType(
										DATAPROPERTYVariableType datapropertyVariableType) {
									return buildSingletonInlineSet(desc,
											VariableTypeFactory.getCLASSVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								public InlineSet<OWLClassExpression> visitINDIVIDUALVariableType(
										INDIVIDUALVariableType individualVariableType) {
									return buildSingletonInlineSet(desc,
											VariableTypeFactory.getCLASSVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								public InlineSet<OWLClassExpression> visitCONSTANTVariableType(
										CONSTANTVariableType constantVariableType) {
									return buildSingletonInlineSet(desc,
											VariableTypeFactory.getCLASSVariableType(),
											getDataFactory(), getConstraintSystem());
								}

								public InlineSet<OWLClassExpression> visitANNOTATIONPROPERTYVariableType(
										ANNOTATIONPROPERTYVariableType annotationpropertyVariableType) {
									return buildSingletonInlineSet(desc,
											VariableTypeFactory.getCLASSVariableType(),
											getDataFactory(), getConstraintSystem());
								}
							});
				}
				return toReturn;
			}

			@Override
			public InlineSet<OWLClassExpression> visit(OWLObjectIntersectionOf ce) {
				return this.generaliseClassExpression(ce);
			}

			@Override
			public InlineSet<OWLClassExpression> visit(OWLObjectUnionOf ce) {
				return this.generaliseClassExpression(ce);
			}

			@Override
			public InlineSet<OWLClassExpression> visit(OWLObjectComplementOf ce) {
				return this.generaliseClassExpression(ce);
			}

			@Override
			public InlineSet<OWLClassExpression> visit(OWLObjectSomeValuesFrom ce) {
				return this.generaliseClassExpression(ce);
			}

			@Override
			public InlineSet<OWLClassExpression> visit(OWLObjectAllValuesFrom ce) {
				return this.generaliseClassExpression(ce);
			}

			@Override
			public InlineSet<OWLClassExpression> visit(OWLObjectHasValue ce) {
				return this.generaliseClassExpression(ce);
			}

			@Override
			public InlineSet<OWLClassExpression> visit(OWLObjectMinCardinality ce) {
				return this.generaliseClassExpression(ce);
			}

			@Override
			public InlineSet<OWLClassExpression> visit(OWLObjectExactCardinality ce) {
				return this.generaliseClassExpression(ce);
			}

			@Override
			public InlineSet<OWLClassExpression> visit(OWLObjectMaxCardinality ce) {
				return this.generaliseClassExpression(ce);
			}

			@Override
			public InlineSet<OWLClassExpression> visit(OWLObjectHasSelf ce) {
				return this.generaliseClassExpression(ce);
			}

			@Override
			public InlineSet<OWLClassExpression> visit(OWLObjectOneOf ce) {
				return this.generaliseClassExpression(ce);
			}

			@Override
			public InlineSet<OWLClassExpression> visit(OWLDataSomeValuesFrom ce) {
				return this.generaliseClassExpression(ce);
			}

			@Override
			public InlineSet<OWLClassExpression> visit(OWLDataAllValuesFrom ce) {
				return this.generaliseClassExpression(ce);
			}

			/**
			 * @param ce
			 * @return
			 */
			private InlineSet<OWLClassExpression> generaliseClassExpression(
					OWLClassExpression ce) {
				String name = "?expression";
				Variable<?> variable = this.getConstraintSystem().getVariable(name);
				int i = 1;
				String newName = name;
				while (variable != null) {
					newName = String.format("%s_%d", name, i++);
					variable = this.getConstraintSystem().getVariable(newName);
				}
				GeneratedVariable<OWLClassExpression> expressionGeneratedVariable = this
						.getConstraintSystem().createExpressionGeneratedVariable(newName,
								ce);
				this.getConstraintSystem().importVariable(expressionGeneratedVariable);
				return buildVariableValuesInlineSet(expressionGeneratedVariable,
						this.getDataFactory(), this.getConstraintSystem());
			}
		};
	}

	/**
	 * @return the constraintSystem
	 */
	public ConstraintSystem getConstraintSystem() {
		return this.constraintSystem;
	}

	private static <O extends OWLObject> InlineSet<O> buildSingletonInlineSet(O object,
			VariableType<O> variableType, OWLDataFactory dataFactory,
			ConstraintSystem constraintSystem) {
		Aggregandum<Collection<? extends O>> aggregandums = Adapter
				.buildAggregandumOfCollection(object);
		return new InlineSet<O>(variableType, Collections.singleton(aggregandums),
				dataFactory, constraintSystem);
	}

	private static <O extends OWLObject> InlineSet<O> buildVariableValuesInlineSet(
			Variable<O> variable, OWLDataFactory dataFactory,
			ConstraintSystem constraintSystem) {
		Aggregandum<Collection<? extends O>> aggregandums = Adapter
				.buildAggregandumCollection(Collections
						.singleton(ValuesVariableAtttribute
								.getValuesVariableAtttribute(variable)));
		return new InlineSet<O>(variable.getType(), Collections.singleton(aggregandums),
				dataFactory, constraintSystem);
	}

	/**
	 * @return the dataFactory
	 */
	public OWLDataFactory getDataFactory() {
		return this.dataFactory;
	}
}
