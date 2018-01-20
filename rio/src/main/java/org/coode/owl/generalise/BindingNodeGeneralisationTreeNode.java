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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.bindingtree.Assignment;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.utils.DefaultTreeNode;
import org.semanticweb.owlapi.model.OWLAxiom;

/** @author eleni */
public class BindingNodeGeneralisationTreeNode extends DefaultTreeNode<BindingNode>
    implements GeneralisationTreeNode<BindingNode> {
    private final OWLAxiom generalisation;
    private final Set<OWLAxiomInstantiation> instantiations = new HashSet<>();
    private final ConstraintSystem constraintSystem;

    /**
     * @param userObject userObject
     * @param generalisation generalisation
     * @param instantiations instantiations
     * @param constraintSystem constraintSystem
     */
    public BindingNodeGeneralisationTreeNode(BindingNode userObject, OWLAxiom generalisation,
        Collection<? extends OWLAxiomInstantiation> instantiations,
        ConstraintSystem constraintSystem) {
        super(userObject);
        if (generalisation == null) {
            throw new NullPointerException("The generalisation cannot be null");
        }
        if (instantiations == null) {
            throw new NullPointerException("The instantiation collection cannot be null");
        }
        if (constraintSystem == null) {
            throw new NullPointerException("The constraint system cannot be null");
        }
        this.constraintSystem = constraintSystem;
        this.generalisation = generalisation;
        this.instantiations.addAll(instantiations);
        if (!instantiations.isEmpty()) {
            this.addChild(
                new AxiomGeneralisationTreeNode(generalisation, instantiations, constraintSystem));
        }
    }

    @Override
    public void accept(GeneralisationTreeNodeVisitor visitor) {
        visitor.visitBindingNodeGeneralisationTreeNode(this);
    }

    @Override
    public <P> P accept(GeneralisationTreeNodeVisitorEx<P> visitor) {
        return visitor.visitBindingNodeGeneralisationTreeNode(this);
    }

    /** @return the generalisation */
    public OWLAxiom getGeneralisation() {
        return generalisation;
    }

    /** @return the instantiations */
    public Set<OWLAxiomInstantiation> getInstantiations() {
        return new HashSet<>(instantiations);
    }

    /** @return the constraintSystem */
    public ConstraintSystem getConstraintSystem() {
        return constraintSystem;
    }

    @Override
    public String render() {
        BindingNode bindingNode = getUserObject();
        StringBuilder out = new StringBuilder();
        Iterator<Assignment> iterator = bindingNode.getAssignments().iterator();
        while (iterator.hasNext()) {
            Assignment assignment = iterator.next();
            out.append(assignment.toString());
            if (iterator.hasNext()) {
                out.append(", ");
            }
        }
        return out.toString();
    }
}
