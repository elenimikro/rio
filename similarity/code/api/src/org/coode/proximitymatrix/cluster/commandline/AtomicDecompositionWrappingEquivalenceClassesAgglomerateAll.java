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
package org.coode.proximitymatrix.cluster.commandline;

import java.util.Collection;

import org.coode.distance.Distance;
import org.coode.distance.owl.AxiomRelevanceAtomicDecompositionBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.distance.wrapping.DistanceTableObject;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class AtomicDecompositionWrappingEquivalenceClassesAgglomerateAll extends
        AgglomeratorBase {
    /** @param args */
    public static void main(final String[] args) {
        AtomicDecompositionWrappingEquivalenceClassesAgglomerateAll agglomerator = new AtomicDecompositionWrappingEquivalenceClassesAgglomerateAll();
        agglomerator.checkArgumentsAndRun(args);
    }

    @Override
    public Distance<OWLEntity> getDistance(final OWLOntologyManager manager) {
        final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
                manager.getOWLDataFactory(), new ReplacementByKindStrategy(
                        manager.getOWLDataFactory()));
        final Distance<OWLEntity> distance = new AxiomRelevanceAtomicDecompositionBasedDistance(
                manager.getOntologies(), manager.getOWLDataFactory(), manager,
                owlEntityReplacer);
        return distance;
    }

    public void print(final ClusteringProximityMatrix<?> clusteringMatrix) {
        System.out
                .println(String.format(
                        "Next Pair %s %s %f",
                        Utils.renderManchester((Collection<DistanceTableObject<OWLEntity>>) clusteringMatrix
                                .getMinimumDistancePair().getFirst()),
                        Utils.renderManchester((Collection<DistanceTableObject<OWLEntity>>) clusteringMatrix
                                .getMinimumDistancePair().getSecond()), clusteringMatrix
                                .getMinimumDistance()));
    }
}
