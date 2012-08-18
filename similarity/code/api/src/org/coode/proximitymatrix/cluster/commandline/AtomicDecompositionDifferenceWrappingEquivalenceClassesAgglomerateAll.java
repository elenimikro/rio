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
import org.coode.distance.owl.AtomicDecompositionGeneralisationTreeBasedDistance;
import org.coode.distance.wrapping.DistanceTableObject;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class AtomicDecompositionDifferenceWrappingEquivalenceClassesAgglomerateAll extends
        AgglomeratorBase {
    public static void main(final String[] args) throws OWLOntologyCreationException {
        AtomicDecompositionDifferenceWrappingEquivalenceClassesAgglomerateAll runner = new AtomicDecompositionDifferenceWrappingEquivalenceClassesAgglomerateAll();
        runner.checkArgumentsAndRun(args);
    }

    public void print(final ClusteringProximityMatrix<?> clusteringMatrix) {
        System.out
                .println(String.format(
                        "Next Pair %s %s %f",
                        Utils.render((Collection<DistanceTableObject<OWLEntity>>) clusteringMatrix
                                .getMinimumDistancePair().getFirst()),
                        Utils.render((Collection<DistanceTableObject<OWLEntity>>) (Collection<? extends OWLEntity>) clusteringMatrix
                                .getMinimumDistancePair().getSecond()), clusteringMatrix
                                .getMinimumDistance()));
    }

    public Distance<OWLEntity> getDistance(final OWLOntologyManager manager) {
        final Distance<OWLEntity> distance = new AtomicDecompositionGeneralisationTreeBasedDistance(
                manager.getOntologies(), manager.getOWLDataFactory(), manager);
        return distance;
    }
}
