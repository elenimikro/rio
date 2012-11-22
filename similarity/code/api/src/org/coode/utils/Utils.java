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

import java.io.PrintStream;
import java.util.List;

import org.coode.utils.Tree.Search;

public class Utils {
	public static Search getUserObjectSearch(final Object object) {
		return new Search() {
			@Override
            public boolean goalReached(TreeNode<?> node) {
				return object == null ? object == node.getUserObject() : object
						.equals(node.getUserObject());
			}
		};
	}

	public static void printTree(Tree tree, PrintStream out) {
		printNode(tree.getRoot(), out);
	}

	public static void printNode(TreeNode<?> node, PrintStream out) {
		printNode(node, 0, out);
	}

	private static void printNode(TreeNode<?> node, int tabs, PrintStream out) {
		for (int i = 0; i < tabs; i++) {
			out.print("\t");
		}
		out.println(node.render());
		List<TreeNode<?>> children = node.getChildren();
		for (TreeNode<?> child : children) {
			printNode(child, tabs + 1, out);
		}
	}
}
