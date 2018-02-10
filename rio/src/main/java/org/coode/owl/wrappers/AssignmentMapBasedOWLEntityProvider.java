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
package org.coode.owl.wrappers;

import org.coode.oppl.bindingtree.AssignmentMap;
import org.semanticweb.owlapi.model.OWLEntity;

/** @author eleni */
public class AssignmentMapBasedOWLEntityProvider extends OWLEntityProvider {
    private final AssignmentMap assignmentMap;

    /**
     * @param assignmentMap assignmentMap
     */
    public AssignmentMapBasedOWLEntityProvider(AssignmentMap assignmentMap) {
        if (assignmentMap == null) {
            throw new NullPointerException("The assignment map cannot be null");
        }
        this.assignmentMap = assignmentMap;
        loadDelegate();
    }

    private void loadDelegate() {
        assignmentMap.variables().forEach(v -> assignmentMap.get(v).stream()
            .filter(a -> a instanceof OWLEntity).forEach(a -> add((OWLEntity) a)));
    }

    /** @return the assignmentMap */
    public AssignmentMap getAssignmentMap() {
        return new AssignmentMap(assignmentMap);
    }
}
