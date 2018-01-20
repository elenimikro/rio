package org.coode.proximitymatrix.cluster.commandline;

import java.io.File;
import java.util.List;

import org.coode.distance.Distance;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
public interface Agglomerator {
    /**
     * @param outFile outFile
     * @param iris iris
     * @throws OWLOntologyCreationException OWLOntologyCreationException
     */
    void run(File outFile, List<IRI> iris) throws OWLOntologyCreationException;

    /**
     * @param manager manager
     * @return distance
     */
    Distance<OWLEntity> getDistance(OWLOntologyManager manager);

    /**
     * @param clusteringMatrix clusteringMatrix
     */
    void print(ClusteringProximityMatrix<?> clusteringMatrix);

    /**
     * @param args args
     * @throws OWLOntologyCreationException OWLOntologyCreationException
     */
    void checkArgumentsAndRun(String[] args) throws OWLOntologyCreationException;
}
