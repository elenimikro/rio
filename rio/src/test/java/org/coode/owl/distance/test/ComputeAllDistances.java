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
package org.coode.owl.distance.test;

import java.util.ArrayList;
import java.util.List;

import org.coode.distance.entityrelevance.DefaultOWLEntityRelevancePolicy;
import org.coode.distance.owl.AxiomBasedDistance;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.OntologyManagerUtils;
import org.coode.utils.owl.IOUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @author elenimikroyannidi Class which takes as argument an ontology and computes all distances.
 */
public class ComputeAllDistances {
    /**
     * @param args args
     * @throws OWLOntologyCreationException OWLOntologyCreationException
     */
    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OntologyManagerUtils.ontologyManager();
        List<IRI> iris = new ArrayList<>(args.length);
        for (String string : args) {
            iris.add(IRI.create(string));
        }
        IOUtils.loadIRIMappers(iris, manager);
        List<OWLEntity> entities = Utils.getSortedSignature(manager);
        AxiomBasedDistance distance = new AxiomBasedDistance(manager.ontologies(),
            DefaultOWLEntityRelevancePolicy.getAlwaysIrrelevantPolicy(), manager);
        SimpleProximityMatrix<OWLEntity> distanceMatrix =
            new SimpleProximityMatrix<>(entities, distance);
        System.out.println(String.format("Finished computing distance between %d entities",
            Integer.valueOf(distanceMatrix.getObjects().size())));
    }
}
