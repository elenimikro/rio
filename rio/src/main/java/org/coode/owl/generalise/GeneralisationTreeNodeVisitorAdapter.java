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

import atomicdecomposition.generalise.AxiomAtomicDecompositionGeneralisationTreeNode;

/** @author eleni */
public abstract class GeneralisationTreeNodeVisitorAdapter implements
        GeneralisationTreeNodeVisitor {
    @Override
    public void visitAxiomGeneralisationTreeNode(
            final AxiomGeneralisationTreeNode axiomGeneralisationTreeNode) {}

    @Override
    public void visitVariableGeneralisationTreeNode(
            final VariableGeneralisationTreeNode variableGeneralisationTreeNode) {}

    @Override
    public void visitBindingNodeGeneralisationTreeNode(
            final BindingNodeGeneralisationTreeNode bindingNodeGeneralisationTreeNode) {}

    @Override
    public
            void
            visitAxiomAtomicDecompositionGeneralisationTreeNode(
                    final AxiomAtomicDecompositionGeneralisationTreeNode axiomAtomicDecompositionGeneralisationTreeNode) {}
}
