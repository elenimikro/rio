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
package org.coode.owl.generalise.structural;

import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.Variable;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.variabletypes.ANNOTATIONPROPERTYVariableType;
import org.coode.oppl.variabletypes.CLASSVariableType;
import org.coode.oppl.variabletypes.CONSTANTVariableType;
import org.coode.oppl.variabletypes.DATAPROPERTYVariableType;
import org.coode.oppl.variabletypes.INDIVIDUALVariableType;
import org.coode.oppl.variabletypes.OBJECTPROPERTYVariableType;
import org.coode.oppl.variabletypes.VariableType;
import org.coode.oppl.variabletypes.VariableTypeFactory;
import org.coode.oppl.variabletypes.VariableTypeVisitorEx;
import org.coode.owl.generalise.VariableProvider;
import org.coode.owl.wrappers.OWLEntityProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

public class SingleOWLEntityReplacementVariableProvider extends VariableProvider {
	private final OWLObject owlObject;
	private final RelevancePolicy<OWLObject> relevancePolicy;

	/**
	 * @param entityProvider
	 * @param constraintSystem
	 * @param owlObject
	 */
	public SingleOWLEntityReplacementVariableProvider(
			RelevancePolicy<OWLObject> relevancePolicy, OWLObject owlObject,
			OWLEntityProvider entityProvider, ConstraintSystem constraintSystem) {
		super(entityProvider, constraintSystem);
		if (owlObject == null) {
			throw new NullPointerException("The owlObject cannot be null");
		}
		if (relevancePolicy == null) {
			throw new NullPointerException("The relevance policy cannot be null");
		}
		this.relevancePolicy = relevancePolicy;
		this.owlObject = owlObject;
	}

	@Override
	protected Variable<?> getAbstractingVariable(OWLObject owlObject) {
		if (owlObject == null) {
			throw new NullPointerException("The owlObject cannot be null");
		}
		return owlObject.accept(new OWLObjectVisitorExAdapter<Variable<?>>() {
			@Override
			protected Variable<?> getDefaultReturnValue(OWLObject object) {
				return object.equals(SingleOWLEntityReplacementVariableProvider.this
						.getOWLObject()) ? SingleOWLEntityReplacementVariableProvider.this
						.getStar(object)
						: SingleOWLEntityReplacementVariableProvider.this
								.getRelevancePolicy().isRelevant(object) ? null
								: SingleOWLEntityReplacementVariableProvider.this
										.getVariable(object);
			}

			@Override
			public Variable<?> visit(IRI iri) {
				OWLObject owlEntity = SingleOWLEntityReplacementVariableProvider.this
						.getOWLEntity(iri);
				return owlEntity != null ? owlEntity.accept(this) : null;
			}
		});
	}

	private Variable<?> getVariable(OWLObject owlObject) {
		Variable<?> toReturn = null;
		VariableType<?> variableType = VariableTypeFactory.getVariableType(owlObject);
		if (variableType != null) {
			toReturn = variableType.accept(new VariableTypeVisitorEx<Variable<?>>() {
				@Override
				public Variable<?> visitCLASSVariableType(
						CLASSVariableType classVariableType) {
					return this.createVariable("?owlClass", classVariableType);
				}

				/**
				 * @param type
				 * @return
				 * @throws OPPLException
				 */
				protected Variable<?> createVariable(String name, VariableType<?> type) {
					try {
						return SingleOWLEntityReplacementVariableProvider.this
								.getConstraintSystem().createVariable(name, type, null);
					} catch (OPPLException e) {
						e.printStackTrace();
						return null;
					}
				}

				@Override
				public Variable<?> visitOBJECTPROPERTYVariableType(
						OBJECTPROPERTYVariableType objectpropertyVariableType) {
					return this.createVariable("?owlObjectproperty",
							objectpropertyVariableType);
				}

				@Override
				public Variable<?> visitDATAPROPERTYVariableType(
						DATAPROPERTYVariableType datapropertyVariableType) {
					return this.createVariable("?owlDatatypeProperty",
							datapropertyVariableType);
				}

				@Override
				public Variable<?> visitINDIVIDUALVariableType(
						INDIVIDUALVariableType individualVariableType) {
					return this.createVariable("?owlIndividual", individualVariableType);
				}

				@Override
				public Variable<?> visitCONSTANTVariableType(
						CONSTANTVariableType constantVariableType) {
					return this.createVariable("?owlLiteral", constantVariableType);
				}

				@Override
				public Variable<?> visitANNOTATIONPROPERTYVariableType(
						ANNOTATIONPROPERTYVariableType annotationpropertyVariableType) {
					return this.createVariable("?owlAnnotationProperty",
							annotationpropertyVariableType);
				}
			});
		}
		return toReturn;
	}

	private Variable<?> getStar(OWLObject owlObject) {
		try {
			return this.getConstraintSystem().createVariable("?star",
					VariableTypeFactory.getVariableType(owlObject), null);
		} catch (OPPLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the owlObject
	 */
	public OWLObject getOWLObject() {
		return this.owlObject;
	}

	/**
	 * @return the relevancePolicy
	 */
	public RelevancePolicy<OWLObject> getRelevancePolicy() {
		return this.relevancePolicy;
	}
}
