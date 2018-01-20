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
import org.coode.oppl.variabletypes.VariableType;
import org.coode.oppl.variabletypes.VariableTypeFactory;
import org.coode.owl.wrappers.OWLEntityProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

/** @author eleni */
public class OWLEntityReplacementVariableProvider
    extends SingleOWLEntityReplacementVariableProvider {
    /**
     * @param relevancePolicy relevancePolicy
     * @param entityProvider entityProvider
     * @param constraintSystem constraintSystem
     */
    public OWLEntityReplacementVariableProvider(RelevancePolicy<OWLEntity> relevancePolicy,
        OWLEntityProvider entityProvider, ConstraintSystem constraintSystem) {
        super(relevancePolicy, entityProvider);
        setConstraintSystem(constraintSystem);
    }

    @Override
    protected Variable<?> getAbstractingVariable(OWLObject owlObject) {
        if (owlObject == null) {
            throw new NullPointerException("The owlObject cannot be null");
        }
        return owlObject.accept(new OWLObjectVisitorEx<Variable<?>>() {
            @Override
            public <T> Variable<?> doDefault(T _object) {
                OWLEntity object = (OWLEntity) _object;
                return object.equals(OWLEntityReplacementVariableProvider.this.getOWLObject())
                    ? null
                    : OWLEntityReplacementVariableProvider.this.getRelevancePolicy()
                        .isRelevant(object) ? null
                            : OWLEntityReplacementVariableProvider.this.getVariable(object);
            }

            @Override
            public Variable<?> visit(IRI iri) {
                OWLObject owlEntity = OWLEntityReplacementVariableProvider.this.getOWLEntity(iri);
                return owlEntity != null ? owlEntity.accept(this) : null;
            }
        });
    }

    @Override
    public Variable<?> getVariable(OWLObject owlObject) {
        VariableType<?> type = owlObject.accept(new OWLObjectVisitorEx<VariableType<?>>() {
            @Override
            public <T> VariableType<?> doDefault(T object) {
                return VariableTypeFactory.getVariableType(owlObject);
            }

            @Override
            public VariableType<?> visit(IRI iri) {
                OWLObject owlEntity = OWLEntityReplacementVariableProvider.this.getOWLEntity(iri);
                return owlEntity != null ? VariableTypeFactory.getVariableType(owlEntity) : null;
            }
        });
        Variable<?> toReturn = null;
        if (type != null) {
            newVariable(type);
            toReturn = get(owlObject);
        }
        return toReturn;
    }
}
