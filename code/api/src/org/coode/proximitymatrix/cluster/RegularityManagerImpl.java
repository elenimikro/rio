package org.coode.proximitymatrix.cluster;

import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.basetest.ClusterCreator;
import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

public class RegularityManagerImpl {

	private RegularitiesDecompositionModel<?, OWLEntity> model = null;
	private final OWLOntology ontology;
	private final Set<OWLAxiom> seedAxioms = new HashSet<OWLAxiom>();

	public RegularityManagerImpl(OWLOntology ontology) {
		if (ontology == null) {
			throw new NullPointerException("The ontology cannot be null");
		}
		this.ontology = ontology;
	}

	public RegularityManagerImpl(OWLOntology ontology, Set<OWLAxiom> axioms) {
		if (ontology == null) {
			throw new NullPointerException("The ontology cannot be null");
		}
		this.ontology = ontology;
	}

	public void getSeedAxioms() {

	}

	public void getDistanceAxioms() {

	}

	public void getClusters() {

	}

	public MultiMap<OWLAxiom, OWLAxiomInstantiation> getGeneralisations() {
		return this.model.getGeneralisationMap();
	}

	public void getGeneralisationMap() {

	}

	public void saveRegularities() {

	}

	public RegularitiesDecompositionModel<?, OWLEntity> getRegularityModel() {
		return model;

	}

	public void loadClustersFromFile() {

	}

	public void loadRegularityModel() {

	}

	public void createRegularityModel() {
		Distance<OWLEntity> distance = DistanceCreator
				.createAxiomRelevanceAxiomBasedDistance(ontology
						.getOWLOntologyManager());
		ClusterCreator clusterer = new ClusterCreator();
		try {
			Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(
					ontology, distance, null);
			this.model = clusterer.buildClusterDecompositionModel(ontology,
					clusters);
		} catch (OPPLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}