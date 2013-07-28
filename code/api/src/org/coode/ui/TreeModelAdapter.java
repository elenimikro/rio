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

public class TreeModelAdapter implements TreeModel {
	private final TreeModel delegate;

	public TreeModelAdapter(Tree tree) {
		TreeNode<?> currentTreeNode = tree.getRoot();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(currentTreeNode);
		DefaultTreeModel defaultTreeModel = new DefaultTreeModel(root);
		this.buildNode(root, currentTreeNode);
		this.delegate = defaultTreeModel;
	}

	private void buildNode(DefaultMutableTreeNode node, TreeNode<?> currentTreeNode) {
		List<TreeNode<?>> children = currentTreeNode.getChildren();
		for (TreeNode<?> childTreeNode : children) {
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childTreeNode);
			node.add(childNode);
			this.buildNode(childNode, childTreeNode);
		}
	}

	/**
	 * @param l
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
    public void addTreeModelListener(TreeModelListener l) {
		this.delegate.addTreeModelListener(l);
	}

	/**
	 * @param parent
	 * @param index
	 * @return
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	@Override
    public Object getChild(Object parent, int index) {
		return this.delegate.getChild(parent, index);
	}

	/**
	 * @param parent
	 * @return
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	@Override
    public int getChildCount(Object parent) {
		return this.delegate.getChildCount(parent);
	}

	/**
	 * @param parent
	 * @param child
	 * @return
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
    public int getIndexOfChild(Object parent, Object child) {
		return this.delegate.getIndexOfChild(parent, child);
	}

	/**
	 * @return
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	@Override
    public Object getRoot() {
		return this.delegate.getRoot();
	}

	/**
	 * @param node
	 * @return
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	@Override
    public boolean isLeaf(Object node) {
		return this.delegate.isLeaf(node);
	}

	/**
	 * @param l
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
    public void removeTreeModelListener(TreeModelListener l) {
		this.delegate.removeTreeModelListener(l);
	}

	/**
	 * @param path
	 * @param newValue
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,
	 *      java.lang.Object)
	 */
	@Override
    public void valueForPathChanged(TreePath path, Object newValue) {
		this.delegate.valueForPathChanged(path, newValue);
	}
}
