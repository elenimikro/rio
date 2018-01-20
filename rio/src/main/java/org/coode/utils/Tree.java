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

/** @author eleni */
public interface Tree {
    /** @author eleni */
    public interface Search {
        /**
         * @param node node
         * @return true if goal reached
         */
        boolean goalReached(TreeNode<?> node);
    }

    /**
     * The root of this Tree.
     * 
     * @return a TreeNode
     */
    public TreeNode<?> getRoot();

    /**
     * Finds a path to the TreeNode instance containing the input object as its user object if it is
     * present in the Tree. An empty List otherwise. The path is the path from this object to the
     * root of the Tree.
     * 
     * @param o The target user object.
     * @return A List whose first element is the TreeNode containing the target object as its user
     *         object, or an empty List if the search could not find the target object.
     */
    public List<TreeNode<?>> findDepthFirst(Object o);

    /**
     * Finds a path to the TreeNode instance matching the input Search criteria, if it is present in
     * the Tree. An empty List otherwise. The path is the path from this object to the root of the
     * Tree.
     * 
     * @param search The search criteria to identify the desired TreeNode. Cannot be {@code null}.
     * @return A List whose first element is the TreeNode matching the input search criteria, or an
     *         empty List if the search could not find the target object.
     * @throws NullPointerException if the input search is {@code null}.
     */
    public List<TreeNode<?>> findDepthFirst(Search search);

    /**
     * Finds a path to the TreeNode instance matching the input Search criteria, if it is present in
     * the portion of the Tree rooted at the input start node. An empty List otherwise. The path is
     * the path from this object to the start input TreeNode.
     * 
     * @param start The root of the search. Cannot be {@code null}.
     * @param search The search criteria to identify the desired TreeNode. Cannot be {@code null}.
     * @return A List whose first element is the TreeNode matching the input search criteria, or an
     *         empty List if the search could not find the target object.
     * @throws NullPointerException if either input is {@code null}.
     */
    public List<TreeNode<?>> findDepthFirst(TreeNode<?> start, Search search);

    /**
     * Finds all paths in the Tree to TreeNode instances whose user objects are equal to the input
     * object and stores them in the solutions input List. It returns {@code true} if it found at
     * least one path, {@code false} otherwise.
     * 
     * @param object The target user object.
     * @param solutions Will contain all the paths from the TreeNode instances in the Tree whose
     *        user object is equal to the target input object. Cannot be {@code null}. If the list
     *        has content when passed to the method it should be replaced with the solutions in all
     *        the implementations.
     * @return {@code true} if there is at least one node in the Tree whose user object is equal to
     *         the input object.
     * @throws NullPointerException if the input solutions is {@code null}.
     */
    public boolean findAllDepthFirst(Object object, List<List<TreeNode<?>>> solutions);
}
