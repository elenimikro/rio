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
package org.coode.ui;

import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.coode.utils.Tree;
import org.coode.utils.TreeNode;

/** @author eleni */
public class TreeModelAdapter implements TreeModel {
    private final TreeModel delegate;

    /** @param tree
     *            tree */
    public TreeModelAdapter(Tree tree) {
        TreeNode<?> currentTreeNode = tree.getRoot();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(currentTreeNode);
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(root);
        buildNode(root, currentTreeNode);
        delegate = defaultTreeModel;
    }

    private void buildNode(DefaultMutableTreeNode node, TreeNode<?> currentTreeNode) {
        List<TreeNode<?>> children = currentTreeNode.getChildren();
        for (TreeNode<?> childTreeNode : children) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childTreeNode);
            node.add(childNode);
            buildNode(childNode, childTreeNode);
        }
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        delegate.addTreeModelListener(l);
    }

    @Override
    public Object getChild(Object parent, int index) {
        return delegate.getChild(parent, index);
    }

    @Override
    public int getChildCount(Object parent) {
        return delegate.getChildCount(parent);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return delegate.getIndexOfChild(parent, child);
    }

    @Override
    public Object getRoot() {
        return delegate.getRoot();
    }

    @Override
    public boolean isLeaf(Object node) {
        return delegate.isLeaf(node);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        delegate.removeTreeModelListener(l);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        delegate.valueForPathChanged(path, newValue);
    }
}
