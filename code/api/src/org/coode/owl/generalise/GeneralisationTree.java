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

import org.coode.utils.DefaultTree;
import org.coode.utils.DefaultTreeNode;
import org.coode.utils.Tree;
import org.coode.utils.TreeNode;
import org.semanticweb.owlapi.model.OWLAxiom;

/** @author eleni */
public class GeneralisationTree extends DefaultTree implements Tree {
    /** @param rootAxiom
     *            rootAxiom */
    public GeneralisationTree(OWLAxiom rootAxiom) {
        super(buildNode(rootAxiom));
    }

    /** @param axiom
     *            axiom
     * @return node */
    public static TreeNode<OWLAxiom> buildNode(OWLAxiom axiom) {
        TreeNode<OWLAxiom> toReturn = new DefaultTreeNode<OWLAxiom>(axiom);
        return toReturn;
    }
}
