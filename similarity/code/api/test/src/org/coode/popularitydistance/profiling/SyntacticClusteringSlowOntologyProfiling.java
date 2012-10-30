package org.coode.popularitydistance.profiling;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import experiments.ExperimentHelper;

public class SyntacticClusteringSlowOntologyProfiling {

	private static String ontoName = "similarity/documents/ChronicALLModule.owl";
	private static String xml = "similarity/documents/ChronicALLModule-syntactic-popularity.xml";

	public static void testSyntacticClustering()
			throws OWLOntologyCreationException, OPPLException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o = m.loadOntologyFromOntologyDocument(new File(ontoName));
		System.out.println("SyntacticClusteringTest.testSyntacticClustering() " + ontoName);
		System.out.println("SyntacticClusteringTest.testSyntacticClustering() Ontology was loaded");

		Distance<OWLEntity> distance = DistanceCreator
				.createAxiomRelevanceAxiomBasedDistance(m);
		System.out.println("SyntacticClusteringTest.testSyntacticClustering() Distance was created");
		ClusterDecompositionModel<OWLEntity> model = ExperimentHelper
				.startSyntacticClustering(o, distance, null);
		Utils.saveToXML(model, m, new File(xml));
	}

	public static void main(String[] args) throws OWLOntologyCreationException,
			OPPLException, ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {
		testSyntacticClustering();
	}

}
