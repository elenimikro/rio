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

import java.util.HashSet;
import java.util.Set;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.PartialOWLObjectInstantiator;
import org.coode.oppl.Variable;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
import org.coode.oppl.function.SimpleValueComputationParameters;
import org.coode.utils.DefaultTreeNode;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
public class VariableGeneralisationTreeNode extends DefaultTreeNode<Variable<?>>
    implements GeneralisationTreeNode<Variable<?>> {
    private final OWLAxiom generalisation;
    private final ConstraintSystem constraintSystem;
    private final MultiMap<BindingNode, OWLAxiomInstantiation> bindingNodes = new MultiMap<>();

    /**
     * @param userObject userObject
     * @param generalisation generalisation
     * @param bindingNodes bindingNodes
     * @param constraintSystem constraintSystem
     */
    public VariableGeneralisationTreeNode(Variable<?> userObject, OWLAxiom generalisation,
        MultiMap<BindingNode, OWLAxiomInstantiation> bindingNodes,
        ConstraintSystem constraintSystem) {
        super(userObject);
        if (bindingNodes == null) {
            throw new NullPointerException("The binding nodes collection cannot be null");
        }
        if (generalisation == null) {
            throw new NullPointerException("The generalisation cannot be null");
        }
        if (constraintSystem == null) {
            throw new NullPointerException("The constraint system cannot be null");
        }
        this.constraintSystem = constraintSystem;
        this.generalisation = generalisation;
        this.bindingNodes.putAll(bindingNodes);
        Set<BindingNode> keySet = bindingNodes.keySet();
        for (BindingNode bindingNode : keySet) {
            SimpleValueComputationParameters parameters = new SimpleValueComputationParameters(
                constraintSystem, bindingNode, new QuickFailRuntimeExceptionHandler());
            PartialOWLObjectInstantiator instantiator =
                new PartialOWLObjectInstantiator(parameters);
            OWLAxiom instantiation = (OWLAxiom) generalisation.accept(instantiator);
            this.addChild(new BindingNodeGeneralisationTreeNode(bindingNode, instantiation,
                bindingNodes.get(bindingNode), constraintSystem));
        }
    }

    @Override
    public void accept(GeneralisationTreeNodeVisitor visitor) {
        visitor.visitVariableGeneralisationTreeNode(this);
    }

    @Override
    public <P> P accept(GeneralisationTreeNodeVisitorEx<P> visitor) {
        return visitor.visitVariableGeneralisationTreeNode(this);
    }

    /** @return the generalisation */
    public OWLAxiom getGeneralisation() {
        return generalisation;
    }

    /** @return the bindingNodes */
    public Set<BindingNode> getBindingNodes() {
        return new HashSet<>(bindingNodes.keySet());
    }

    /** @return the constraintSystem */
    public ConstraintSystem getConstraintSystem() {
        return constraintSystem;
    }

    @Override
    public String render() {
        return getUserObject().getName();
    }
}
