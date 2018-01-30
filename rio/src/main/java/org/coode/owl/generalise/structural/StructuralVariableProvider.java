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

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.Variable;
import org.coode.oppl.variabletypes.VariableType;
import org.coode.oppl.variabletypes.VariableTypeFactory;
import org.coode.owl.generalise.VariableProvider;
import org.coode.owl.wrappers.OWLEntityProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

/** @author eleni */
public class StructuralVariableProvider extends VariableProvider {
    class VarVisitor implements OWLObjectVisitorEx<VariableType<?>> {

        @Override
        public <T> VariableType<?> doDefault(T object) {
            return VariableTypeFactory.getVariableType((OWLObject) object);
        }

        @Override
        public VariableType<?> visit(IRI iri) {
            OWLObject owlEntity = getOWLEntity(iri);
            return owlEntity != null ? VariableTypeFactory.getVariableType(owlEntity) : null;
        }
    }

    private VarVisitor visitor = new VarVisitor();

    /**
     * @param entityProvider entityProvider
     * @param constraintSystem constraintSystem
     */
    public StructuralVariableProvider(OWLEntityProvider entityProvider,
        ConstraintSystem constraintSystem) {
        super(entityProvider);
        setConstraintSystem(constraintSystem);
    }

    @Override
    protected Variable<?> getAbstractingVariable(OWLObject owlObject) {
        VariableType<?> type = owlObject.accept(visitor);
        Variable<?> toReturn = null;
        if (type != null) {
            newVariable(type);
            toReturn = get(owlObject);
        }
        return toReturn;
    }
}
