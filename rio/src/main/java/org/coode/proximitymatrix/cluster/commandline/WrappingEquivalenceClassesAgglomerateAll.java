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

import org.coode.distance.Distance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Class for computing syntactic similarities, using the AxiomBased distance with the objproperties
 * always relevant policy.
 * 
 * @author elenimikroyannidi
 */
public class WrappingEquivalenceClassesAgglomerateAll extends AgglomeratorBase {
    /**
     * @param args args
     * @throws OWLOntologyCreationException OWLOntologyCreationException
     */
    public static void main(String[] args) throws OWLOntologyCreationException {
        WrappingEquivalenceClassesAgglomerateAll agglomerator =
            new WrappingEquivalenceClassesAgglomerateAll();
        agglomerator.checkArgumentsAndRun(args);
    }

    @Override
    public Distance<OWLEntity> getDistance(OWLOntologyManager manager) {
        OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(manager.getOWLDataFactory(),
            new ReplacementByKindStrategy(manager.getOWLDataFactory()));
        Distance<OWLEntity> distance =
            new AxiomRelevanceAxiomBasedDistance(manager.ontologies(), owlEntityReplacer, manager);
        return distance;
    }

    @Override
    public void print(ClusteringProximityMatrix<?> clusteringMatrix) {
        Utility.print(clusteringMatrix);
    }
}
