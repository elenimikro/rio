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

import java.util.HashMap;
import java.util.Map;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.Variable;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.variabletypes.VariableType;
import org.coode.oppl.variabletypes.VariableTypeFactory;
import org.coode.owl.wrappers.OWLEntityProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLObject;

/** @author eleni */
public abstract class VariableProvider {
    private ConstraintSystem constraintSystem;
    private final OWLEntityProvider entityProvider;
    private final Map<OWLObject, Variable<?>> cache = new HashMap<>();
    private final Map<VariableType<?>, Variable<?>> variables = new HashMap<>();
    private final Map<VariableType<?>, Integer> variableIndex = new HashMap<>();

    /**
     * @param entityProvider entityProvider
     */
    public VariableProvider(OWLEntityProvider entityProvider) {
        if (entityProvider == null) {
            throw new NullPointerException("The entity provider cannot be null");
        }
        this.entityProvider = entityProvider;
    }

    /**
     * @param owlObject owlObject
     * @return variable
     */
    public Variable<?> get(OWLObject owlObject) {
        Variable<?> toReturn = cache.get(owlObject);
        if (toReturn == null) {
            VariableType<?> variableType = VariableTypeFactory.getVariableType(owlObject);
            toReturn = variables.get(variableType);
            if (toReturn != null) {
                cache.put(owlObject, toReturn);
            }
        }
        return toReturn;
    }

    /**
     * @param variableType variableType
     */
    public void newVariable(VariableType<?> variableType) {
        Integer integer = variableIndex.get(variableType);
        if (integer == null) {
            integer = 0;
        }
        variableIndex.put(variableType, integer + 1);
        Variable<?> v;
        try {
            v = getConstraintSystem().createVariable(createName(variableType), variableType, null);
            variables.put(variableType, v);
        } catch (OPPLException e) {
            e.printStackTrace();
        }
    }

    private String createName(VariableType<?> variableType) {
        return String.format("?%s_%d", variableType.toString().toLowerCase(),
            variableIndex.get(variableType).intValue());
    }

    protected abstract Variable<?> getAbstractingVariable(OWLObject owlObject);

    /** @return the constraintSystem */
    public ConstraintSystem getConstraintSystem() {
        if (constraintSystem == null) {
            throw new NullPointerException("The constraint system cannot be null");
        }
        return constraintSystem;
    }

    /**
     * @param cs cs
     */
    public void setConstraintSystem(ConstraintSystem cs) {
        constraintSystem = cs;
    }

    /**
     * @param iri iri
     * @return entity
     */
    public OWLObject getOWLEntity(final IRI iri) {
        return getEntityProvider().stream().filter(e -> e.getIRI().equals(iri)).findFirst()
            .orElse(null);
    }

    /** @return the entityProvider */
    public OWLEntityProvider getEntityProvider() {
        return entityProvider;
    }
}
