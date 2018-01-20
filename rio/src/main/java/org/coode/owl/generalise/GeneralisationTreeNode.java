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

import org.coode.utils.TreeNode;

/**
 * @author eleni
 * @param <O> type
 */
public interface GeneralisationTreeNode<O> extends TreeNode<O> {
    /**
     * @param visitor visitor
     */
    void accept(GeneralisationTreeNodeVisitor visitor);

    /**
     * @param visitor visitor
     * @param <P> type
     * @return visitor value
     */
    <P> P accept(GeneralisationTreeNodeVisitorEx<P> visitor);
}
