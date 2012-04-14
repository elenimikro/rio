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
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class DefaultTree implements Tree {
	private final TreeNode<?> root;

	/**
	 * @param root
	 */
	public DefaultTree(TreeNode<?> root) {
		if (root == null) {
			throw new NullPointerException("The root cannot be null");
		}
		this.root = root;
	}

	/**
	 * @return the root
	 */
	public TreeNode<?> getRoot() {
		return this.root;
	}

	public final List<TreeNode<?>> findDepthFirst(final Object o) {
		return this.findDepthFirst(this.getRoot(), Utils.getUserObjectSearch(o));
	}

	public final List<TreeNode<?>> findDepthFirst(Tree.Search search) {
		if (search == null) {
			throw new NullPointerException("The search criteria cannot be null");
		}
		return this.findDepthFirst(this.getRoot(), search);
	}

	public final List<TreeNode<?>> findDepthFirst(TreeNode<?> start, Tree.Search search) {
		if (start == null) {
			throw new NullPointerException("The search root node cannot be null");
		}
		if (search == null) {
			throw new NullPointerException("The search criteria cannot be null");
		}
		Stack<TreeNode<?>> result = new Stack<TreeNode<?>>();
		boolean found = this.depthFirstSearch(start, result, search);
		return found ? new ArrayList<TreeNode<?>>(result) : Collections
				.<TreeNode<?>> emptyList();
	}

	private boolean depthFirstSearch(TreeNode<?> start, Stack<TreeNode<?>> result,
			Tree.Search search) {
		if (result.contains(start)) {
			return false;
		}
		result.push(start);
		boolean goalReached = search.goalReached(start);
		if (goalReached) {
			return true;
		}
		List<TreeNode<?>> children = start.getChildren();
		for (int i = 0; i < children.size(); i++) {
			TreeNode<?> child = children.get(i);
			if (this.depthFirstSearch(child, result, search)) {
				return true;
			}
		}
		// No path was found
		result.pop();
		return false;
	}

	public boolean findAllDepthFirst(final Object object,
			List<List<TreeNode<?>>> solutions) {
		if (solutions == null) {
			throw new NullPointerException("The solution list cannot be null");
		}
		solutions.clear();
		return this.findAllDepthFirst(this.getRoot(), new Stack<TreeNode<?>>(),
				solutions, Utils.getUserObjectSearch(object));
	}

	private boolean findAllDepthFirst(TreeNode<?> start, Stack<TreeNode<?>> currentPath,
			List<List<TreeNode<?>>> solutions, Search search) {
		if (currentPath.contains(start)) {
			return false;
		}
		currentPath.push(start);
		boolean goalReached = search.goalReached(start);
		if (goalReached) {
			solutions.add(new ArrayList<TreeNode<?>>(currentPath));
			currentPath.pop();
			return true;
		}
		List<TreeNode<?>> children = start.getChildren();
		boolean found = false;
		for (int i = 0; i < children.size(); i++) {
			TreeNode<?> child = children.get(i);
			boolean searchSubTree = this.findAllDepthFirst(child, currentPath, solutions,
					search);
			found = found || searchSubTree;
		}
		currentPath.pop();
		return found;
	}
}
