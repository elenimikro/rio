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
package org.coode.atomicdecomposition.commandline;

import org.coode.atomicdecomposition.distance.AxiomRelevanceAtomicDecompositionBasedDistance;
import org.coode.distance.Distance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.cluster.commandline.AgglomeratorBase;
import org.coode.proximitymatrix.cluster.commandline.Utility;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class AtomicDecompositionWrappingEquivalenceClassesAgglomerateAll extends
		AgglomeratorBase {
	/**
	 * @param args
	 * @throws OWLOntologyCreationException
	 */
	public static void main(final String[] args)
			throws OWLOntologyCreationException {
		AtomicDecompositionWrappingEquivalenceClassesAgglomerateAll agglomerator = new AtomicDecompositionWrappingEquivalenceClassesAgglomerateAll();
		agglomerator.checkArgumentsAndRun(args);
	}

	@Override
	public Distance<OWLEntity> getDistance(final OWLOntologyManager manager) {
		final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
				manager.getOWLDataFactory(), new ReplacementByKindStrategy(
						manager.getOWLDataFactory()));
		final Distance<OWLEntity> distance = new AxiomRelevanceAtomicDecompositionBasedDistance(
				manager.getOntologies().iterator().next(),
				manager.getOWLDataFactory(), manager, owlEntityReplacer);
		return distance;
	}

	@Override
	public void print(final ClusteringProximityMatrix<?> clusteringMatrix) {
		Utility.print1(clusteringMatrix);
	}

}
