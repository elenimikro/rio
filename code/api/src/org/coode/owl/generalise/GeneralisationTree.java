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

import java.util.Collection;

import org.coode.utils.DefaultTree;
import org.coode.utils.DefaultTreeNode;
import org.coode.utils.Tree;
import org.coode.utils.TreeNode;
import org.semanticweb.owlapi.model.OWLAxiom;

public class GeneralisationTree extends DefaultTree implements Tree {
	public GeneralisationTree(OWLAxiom rootAxiom,
			Collection<? extends OWLAxiomInstantiation> instantiations) {
		super(buildNode(rootAxiom, instantiations));
	}

	public static TreeNode<OWLAxiom> buildNode(OWLAxiom axiom,
			Collection<? extends OWLAxiomInstantiation> instantiations) {
		TreeNode<OWLAxiom> toReturn = new DefaultTreeNode<OWLAxiom>(axiom);
        // for (OWLAxiomInstantiation axiomInstantiation : instantiations) {}
		return toReturn;
	}
}
