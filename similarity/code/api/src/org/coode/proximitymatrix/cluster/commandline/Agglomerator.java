package org.coode.proximitymatrix.cluster.commandline;

import java.io.File;
import java.util.List;

import org.coode.distance.Distance;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public interface Agglomerator {
    void run(File outFile, List<IRI> iris);

    Distance<OWLEntity> getDistance(OWLOntologyManager manager);

    void print(ClusteringProximityMatrix<?> clusteringMatrix);

    void checkArgumentsAndRun(String[] args);
}
