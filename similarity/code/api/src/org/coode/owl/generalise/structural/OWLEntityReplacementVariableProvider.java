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
import org.coode.owl.wrappers.OWLEntityProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

public class OWLEntityReplacementVariableProvider extends
		SingleOWLEntityReplacementVariableProvider {

	public OWLEntityReplacementVariableProvider(
			RelevancePolicy<OWLObject> relevancePolicy, OWLObject owlObject,
			OWLEntityProvider entityProvider, ConstraintSystem constraintSystem) {
		super(relevancePolicy, owlObject, entityProvider, constraintSystem);
	}
	
	@Override
	protected Variable<?> getAbstractingVariable(OWLObject owlObject) {
		if (owlObject == null) {
			throw new NullPointerException("The owlObject cannot be null");
		}
		return owlObject.accept(new OWLObjectVisitorExAdapter<Variable<?>>() {
			@Override
			protected Variable<?> getDefaultReturnValue(OWLObject object) {
				return object.equals(OWLEntityReplacementVariableProvider.this
						.getOWLObject()) ? null 
//								OWLEntityReplacementVariableProvider.this
//						.getStar(object)
						: OWLEntityReplacementVariableProvider.this
								.getRelevancePolicy().isRelevant(object) ? null
								: OWLEntityReplacementVariableProvider.this
										.getVariable(object);
			}

			@Override
			public Variable<?> visit(IRI iri) {
				OWLObject owlEntity = OWLEntityReplacementVariableProvider.this
						.getOWLEntity(iri);
				return owlEntity != null ? owlEntity.accept(this) : null;
			}
		});
	}
	
	private Variable<?> getVariable(OWLObject owlObject) {
		VariableType<?> type = owlObject
				.accept(new OWLObjectVisitorExAdapter<VariableType<?>>(
						VariableTypeFactory.getVariableType(owlObject)) {
					@Override
					public VariableType<?> visit(IRI iri) {
						OWLObject owlEntity = OWLEntityReplacementVariableProvider.this
								.getOWLEntity(iri);
						return owlEntity != null ? VariableTypeFactory
								.getVariableType(owlEntity) : null;
					}
				});
		Variable<?> toReturn = null;
		if (type != null) {
			this.newVariable(type);
			toReturn = this.get(owlObject);
		}
		return toReturn;
	}
	

}
