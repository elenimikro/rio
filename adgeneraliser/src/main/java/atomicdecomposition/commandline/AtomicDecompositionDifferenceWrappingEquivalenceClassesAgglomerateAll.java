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
package atomicdecomposition.commandline;

import atomicdecomposition.distance.AtomicDecompositionGeneralisationTreeBasedDistance;
import org.coode.distance.Distance;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.cluster.commandline.AgglomeratorBase;
import org.coode.proximitymatrix.cluster.commandline.Utility;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
public class AtomicDecompositionDifferenceWrappingEquivalenceClassesAgglomerateAll extends
        AgglomeratorBase {
    /** @param args
     *            args
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException */
    public static void main(String[] args) throws OWLOntologyCreationException {
        AtomicDecompositionDifferenceWrappingEquivalenceClassesAgglomerateAll runner = new AtomicDecompositionDifferenceWrappingEquivalenceClassesAgglomerateAll();
        runner.checkArgumentsAndRun(args);
    }

    @Override
    public void print(ClusteringProximityMatrix<?> clusteringMatrix) {
        Utility.print2(clusteringMatrix);
    }

    @Override
    public Distance<OWLEntity> getDistance(OWLOntologyManager manager) {
        Distance<OWLEntity> distance = new AtomicDecompositionGeneralisationTreeBasedDistance(
                manager.getOntologies().iterator().next(), manager.getOWLDataFactory(),
                manager);
        return distance;
    }
}
