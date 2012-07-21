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
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

public class StructuralVariableProvider extends VariableProvider {
    public StructuralVariableProvider(final OWLEntityProvider entityProvider,
            final ConstraintSystem constraintSystem) {
        super(entityProvider);
        setConstraintSystem(constraintSystem);
    }

    @Override
    protected Variable<?> getAbstractingVariable(final OWLObject owlObject) {
        VariableType<?> type = owlObject
                .accept(new OWLObjectVisitorExAdapter<VariableType<?>>(
                        VariableTypeFactory.getVariableType(owlObject)) {
                    @Override
                    public VariableType<?> visit(final IRI iri) {
                        OWLObject owlEntity = StructuralVariableProvider.this
                                .getOWLEntity(iri);
                        return owlEntity != null ? VariableTypeFactory
                                .getVariableType(owlEntity) : null;
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
