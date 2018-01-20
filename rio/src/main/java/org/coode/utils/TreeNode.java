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
package org.coode.utils;

import java.util.List;

/**
 * @author eleni
 * @param <O> type
 */
public interface TreeNode<O> {
    /**
     * A List containing all the children of this TreeNode.
     * 
     * @return A List of TreeNode.
     */
    List<TreeNode<?>> getChildren();

    /**
     * The user object. It can be {@code null}.
     * 
     * @return An Object instance or {@code null}.
     */
    O getUserObject();

    /**
     * Determines whether this TreeNode has children.
     * 
     * @return {@code true} if this TreeNode has no children. {@code false} otherwise.
     */
    boolean isLeaf();

    /**
     * The index of the first child TreeNode amongst this TreeNode children that has the input
     * object as its user object. It returns -1 if no chld TreeNode has it.
     * 
     * @param object The target object.
     * @return An integer
     */
    int getChildIndex(Object object);

    /**
     * Provides an appropriate rendering that may be different from the toString method
     * 
     * @return rendering
     */
    public String render();
}
