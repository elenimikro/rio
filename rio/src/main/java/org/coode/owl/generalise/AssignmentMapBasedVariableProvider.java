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

import java.util.Iterator;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.Variable;
import org.coode.oppl.bindingtree.AssignmentMap;
import org.coode.owl.wrappers.AssignmentMapBasedOWLEntityProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

/** @author eleni */
public class AssignmentMapBasedVariableProvider extends VariableProvider {
    protected final AssignmentMap assignmentMap;

    /** @param assignmentMap
     *            assignmentMap
     * @param constraintSystem
     *            constraintSystem */
    public AssignmentMapBasedVariableProvider(AssignmentMap assignmentMap,
            ConstraintSystem constraintSystem) {
        super(new AssignmentMapBasedOWLEntityProvider(assignmentMap));
        setConstraintSystem(constraintSystem);
        this.assignmentMap = new AssignmentMap(assignmentMap);
    }

    @Override
    protected Variable<?> getAbstractingVariable(OWLObject owlObject) {
        return owlObject.accept(new OWLObjectVisitorExAdapter<Variable<?>>() {
            @Override
            protected Variable<?> getDefaultReturnValue(OWLObject object) {
                boolean found = false;
                // AssignmentMap anAssignmentMap =
                // AssignmentMapBasedVariableProvider.this.getAssignmentMap();
                Iterator<Variable<?>> it = assignmentMap.getVariables().iterator();
                Variable<?> toReturn = null;
                Variable<?> aVariable = null;
                while (!found && it.hasNext()) {
                    aVariable = it.next();
                    found = assignmentMap.get(aVariable).contains(object);
                }
                if (found) {
                    toReturn = aVariable;
                }
                return toReturn;
            }

            @Override
            public Variable<?> visit(IRI iri) {
                OWLObject owlEntity = AssignmentMapBasedVariableProvider.this
                        .getOWLEntity(iri);
                return owlEntity != null ? owlEntity.accept(this) : null;
            }
        });
    }

    /** @return the assignmentMap */
    public AssignmentMap getAssignmentMap() {
        return new AssignmentMap(assignmentMap);
    }
}
