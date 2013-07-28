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

import org.coode.atomicdecomposition.distance.AtomicDecompositionGeneralisationTreeBasedDistance;
import org.coode.distance.Distance;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.cluster.commandline.AgglomeratorBase;
import org.coode.proximitymatrix.cluster.commandline.Utility;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class AtomicDecompositionDifferenceWrappingEquivalenceClassesAgglomerateAll extends
        AgglomeratorBase {
    public static void main(final String[] args) throws OWLOntologyCreationException {
        AtomicDecompositionDifferenceWrappingEquivalenceClassesAgglomerateAll runner = new AtomicDecompositionDifferenceWrappingEquivalenceClassesAgglomerateAll();
        runner.checkArgumentsAndRun(args);
    }

    @Override
    public void print(final ClusteringProximityMatrix<?> clusteringMatrix) {
        Utility.print2(clusteringMatrix);
    }


    @Override
    public Distance<OWLEntity> getDistance(final OWLOntologyManager manager) {
        final Distance<OWLEntity> distance = new AtomicDecompositionGeneralisationTreeBasedDistance(
                manager.getOntologies(), manager.getOWLDataFactory(), manager);
        return distance;
    }
}
