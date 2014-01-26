package org.coode.proximitymatrix.cluster;

import java.util.HashSet;
import java.util.Set;

import org.coode.distance.Distance;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.utils.owl.ClusterCreator;
import org.coode.utils.owl.DistanceCreator;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
public class RegularityManagerImpl {
    private RegularitiesDecompositionModel<?, OWLEntity> model = null;
    private final OWLOntology ontology;
    private final Set<OWLAxiom> seedAxioms = new HashSet<OWLAxiom>();

    /** @param ontology
     *            ontology */
    public RegularityManagerImpl(OWLOntology ontology) {
        if (ontology == null) {
            throw new NullPointerException("The ontology cannot be null");
        }
        this.ontology = ontology;
    }

    /** @param ontology
     *            ontology
     * @param axioms
     *            axioms */
    public RegularityManagerImpl(OWLOntology ontology, Set<OWLAxiom> axioms) {
        if (ontology == null) {
            throw new NullPointerException("The ontology cannot be null");
        }
        this.ontology = ontology;
        seedAxioms.addAll(axioms);
    }

    /** seed axioms */
    public void getSeedAxioms() {}

    /** distance axioms */
    public void getDistanceAxioms() {}

    /** clusters */
    public void getClusters() {}

    /** @return generalisations */
    public MultiMap<OWLAxiom, OWLAxiomInstantiation> getGeneralisations() {
        return model.getGeneralisationMap();
    }

    /** generalisation map */
    public void getGeneralisationMap() {}

    /** save regularities */
    public void saveRegularities() {}

    /** @return regularities */
    public RegularitiesDecompositionModel<?, OWLEntity> getRegularityModel() {
        return model;
    }

    /** load clusters */
    public void loadClustersFromFile() {}

    /** load model */
    public void loadRegularityModel() {}

    /** create regularity model */
    public void createRegularityModel() {
        Distance<OWLEntity> distance = DistanceCreator
                .createAxiomRelevanceAxiomBasedDistance(ontology.getOWLOntologyManager());
        ClusterCreator clusterer = new ClusterCreator();
        try {
            Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(distance, null);
            model = clusterer.buildClusterDecompositionModel(ontology, clusters);
        } catch (OPPLException e) {
            e.printStackTrace();
        }
    }
}
