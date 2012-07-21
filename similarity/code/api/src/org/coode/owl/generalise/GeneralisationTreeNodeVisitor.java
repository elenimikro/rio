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

public interface GeneralisationTreeNodeVisitor {
    void visitAxiomGeneralisationTreeNode(
            AxiomGeneralisationTreeNode axiomGeneralisationTreeNode);

            void
            visitAxiomAtomicDecompositionGeneralisationTreeNode(
                    AxiomAtomicDecompositionGeneralisationTreeNode axiomAtomicDecompositionGeneralisationTreeNode);

    void visitVariableGeneralisationTreeNode(
            VariableGeneralisationTreeNode variableGeneralisationTreeNode);

    void visitBindingNodeGeneralisationTreeNode(
            BindingNodeGeneralisationTreeNode bindingNodeGeneralisationTreeNode);
}
