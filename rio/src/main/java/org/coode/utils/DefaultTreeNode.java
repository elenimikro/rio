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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** @author eleni
 * @param <O>
 *            type */
public class DefaultTreeNode<O> implements TreeNode<O> {
    private final O userObject;
    private final List<TreeNode<?>> children = new ArrayList<TreeNode<?>>();

    /** @param userObject
     *            userObject */
    public DefaultTreeNode(O userObject) {
        this(userObject, Collections.<TreeNode<?>> emptyList());
    }

    /** @param userObject
     *            userObject
     * @param children
     *            children */
    public DefaultTreeNode(O userObject, Collection<? extends TreeNode<?>> children) {
        if (children == null) {
            throw new NullPointerException("The children colleciton cannot be null");
        }
        this.userObject = userObject;
        this.children.addAll(children);
    }

    /** @return the userObject */
    @Override
    public O getUserObject() {
        return this.userObject;
    }

    /** @return the children */
    @Override
    public List<TreeNode<?>> getChildren() {
        return new ArrayList<TreeNode<?>>(this.children);
    }

    @Override
    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    @Override
    public int getChildIndex(Object object) {
        int i = -1;
        boolean found = false;
        for (int index = 0; !found && i < children.size(); index++) {
            found = children.get(index).getUserObject().equals(object);
            if (found) {
                i = index;
            }
        }
        return i;
    }

    /** Adds the input TreeNode as the last of this TreeNode children.
     * 
     * @param child
     *            The child TreeNode to add. cannot be {@code null}.
     * @throws NullPointerException
     *             if the input child is {@code null}. */
    public void addChild(TreeNode<?> child) {
        if (child == null) {
            throw new NullPointerException("The child node cannot be null");
        }
        this.addChild(this.children.size(), child);
    }

    /** Adds the input TreeNode as child of this TreeNode at the input position i
     * to this TreeNode.
     * 
     * @param i
     *            The desired position of the list of children. It must be
     *            greater than 0 and less than the current children size.
     * @param child
     *            The TreeNode to add as child. Cannot be {@code null}.
     * @throws NullPointerException
     *             if the input child is {@code null}.
     * @throws IllegalArgumentException
     *             if {@literal i<0 || i>getChildren().size()}.
     * @see TreeNode#getChildren() */
    public void addChild(int i, TreeNode<?> child) {
        if (child == null) {
            throw new NullPointerException("The child node cannot be null");
        }
        if (i < 0 || i > this.children.size()) {
            throw new IllegalArgumentException(String.format(
                    "%d not in the allowed range [0,%d] ", i, this.children.size()));
        }
        this.children.add(i, child);
    }

    /** Removes the input TreeNode from this TreeNode children. Returns
     * {@code true} if the removed TreeNode was actually amongst the children of
     * this TreeNode.
     * 
     * @param child
     *            The TreeNode to remove.
     * @return {@code true} if the removed node was in fact a child of this
     *         TreeNode. {@code false} otherwise. */
    public boolean removeChild(TreeNode<?> child) {
        return this.children.remove(child);
    }

    @Override
    public String render() {
        return toString();
    }
}
