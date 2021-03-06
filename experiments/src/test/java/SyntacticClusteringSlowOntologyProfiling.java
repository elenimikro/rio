package org.coode.popularitydistance.profiling;

import java.io.File;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.distance.Distance;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.owl.DistanceCreator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import experiments.ExperimentHelper;

/** @author eleni */
public class SyntacticClusteringSlowOntologyProfiling {
    private static String ontoName = "documents/ChronicALLModule.owl";
    private static String xml = "documents/ChronicALLModule-syntactic-popularity.xml";

    /** @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     * @throws OPPLException
     *             OPPLException
     * @throws ParserConfigurationException
     *             ParserConfigurationException
     * @throws TransformerFactoryConfigurationError
     *             TransformerFactoryConfigurationError
     * @throws TransformerException
     *             TransformerException */
    public static void testSyntacticClustering() throws OWLOntologyCreationException,
            OPPLException, ParserConfigurationException,
            TransformerFactoryConfigurationError, TransformerException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.loadOntologyFromOntologyDocument(new File(ontoName));
        System.out.println("SyntacticClusteringTest.testSyntacticClustering() "
                + ontoName);
        System.out
                .println("SyntacticClusteringTest.testSyntacticClustering() Ontology was loaded");
        o.getOWLOntologyManager().removeAxioms(
                o,
                new HashSet<OWLAnnotationAssertionAxiom>(o
                        .getAxioms(AxiomType.ANNOTATION_ASSERTION)));
        Distance<OWLEntity> distance = DistanceCreator
                .createAxiomRelevanceAxiomBasedDistance(m);
        System.out
                .println("SyntacticClusteringTest.testSyntacticClustering() Distance was created");
        ClusterDecompositionModel<OWLEntity> model = ExperimentHelper
                .startSyntacticClustering(o, distance, null);
        Utils.saveToXML(model, new File(xml));
    }

    /** @param args
     *            args
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     * @throws OPPLException
     *             OPPLException
     * @throws ParserConfigurationException
     *             ParserConfigurationException
     * @throws TransformerFactoryConfigurationError
     *             TransformerFactoryConfigurationError
     * @throws TransformerException
     *             TransformerException */
    public static void main(String[] args) throws OWLOntologyCreationException,
            OPPLException, ParserConfigurationException,
            TransformerFactoryConfigurationError, TransformerException {
        testSyntacticClustering();
    }
}
