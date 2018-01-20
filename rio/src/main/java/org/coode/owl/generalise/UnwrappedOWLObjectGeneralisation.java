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
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.coode.owl.generalise;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.bindingtree.BindingNode;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.util.MultiMap;

/**
 * Visitor that abstracts OWLObjects into variables.
 * 
 * @author Eleni Mikroyannidi, Luigi Iannone
 */
public class UnwrappedOWLObjectGeneralisation extends OWLObjectGeneralisation
    implements OWLObjectVisitorEx<OWLObject> {
    /**
     * @param bindingNodes bindingNodes
     * @param constraintSystem constraintSystem
     */
    public UnwrappedOWLObjectGeneralisation(Collection<? extends BindingNode> bindingNodes,
        ConstraintSystem constraintSystem) {
        super(bindingNodes, constraintSystem);
    }

    @Override
    public OWLClassExpression visit(OWLObjectIntersectionOf desc) {
        Set<OWLClassExpression> operands = desc.getOperands();
        Set<OWLClassExpression> newOperands = new HashSet<>(operands.size());
        MultiMap<OWLClassExpression, OWLClassExpression> generalisationMap = new MultiMap<>();
        for (OWLClassExpression classExpression : operands) {
            OWLClassExpression generalised = (OWLClassExpression) classExpression.accept(this);
            generalisationMap.put(generalised, classExpression);
        }
        for (OWLClassExpression generalisation : generalisationMap.keySet()) {
            newOperands.add(generalisation);
        }
        return OWLManager.getOWLDataFactory()
            .getOWLObjectIntersectionOf(new HashSet<>(newOperands));
    }
}
