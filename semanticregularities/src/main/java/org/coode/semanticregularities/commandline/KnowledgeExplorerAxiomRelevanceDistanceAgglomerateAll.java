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
package org.coode.semanticregularities.commandline;

import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.basetest.TestHelper;
import org.coode.distance.Distance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillersFactplusplusImpl;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.owl.ClusterCreator;
import org.coode.utils.owl.DistanceCreator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import uk.ac.manchester.cs.jfact.JFactReasoner;

/** Class for computing syntactic similarities, using the AxiomBased distance
 * with the objproperties always relevant policy.
 * 
 * @author elenimikroyannidi */
public class KnowledgeExplorerAxiomRelevanceDistanceAgglomerateAll {
    /** @param args
     *            args
     * @throws OWLOntologyCreationException
     *             OWLOntologyCreationException
     * @throws ParserConfigurationException
     *             ParserConfigurationException
     * @throws OPPLException
     *             OPPLException
     * @throws TransformerException
     *             TransformerException
     * @throws TransformerFactoryConfigurationError
     *             TransformerFactoryConfigurationError */
    public static void main(String[] args) throws OWLOntologyCreationException,
            OPPLException, ParserConfigurationException,
            TransformerFactoryConfigurationError, TransformerException {
        KnowledgeExplorerAxiomRelevanceDistanceAgglomerateAll agglomerator = new KnowledgeExplorerAxiomRelevanceDistanceAgglomerateAll();
        agglomerator.checkArgumentsAndRun(args);
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
    public void checkArgumentsAndRun(String[] args) throws OWLOntologyCreationException,
            OPPLException, ParserConfigurationException,
            TransformerFactoryConfigurationError, TransformerException {
        if (args.length >= 2) {
            File outfile = new File(args[0]);
            run(outfile, IRI.create(args[1]));
        } else {
            System.out
                    .println(String
                            .format("Usage java -cp ... %s <saveResultFilePath> <ontology> ... <ontology>",
                                    this.getClass().getCanonicalName()));
        }
    }

    /** @param outfile
     *            outfile
     * @param ontologyIri
     *            ontologyIri
     * @return cluster decomposition model
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
    public ClusterDecompositionModel<OWLEntity> run(File outfile, IRI ontologyIri)
            throws OWLOntologyCreationException, OPPLException,
            ParserConfigurationException, TransformerFactoryConfigurationError,
            TransformerException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = TestHelper.loadIRIMappers(ontologyIri, manager);
        JFactReasoner reasoner = new JFactReasoner(ontology, new SimpleConfiguration(),
                BufferingMode.NON_BUFFERING);
        reasoner.precomputeInferences();
        KnowledgeExplorer ke = new KnowledgeExplorerMaxFillersFactplusplusImpl(reasoner);
        // KnowledgeExplorer ke = new
        // KnowledgeExplorerMaxFillerJFactImpl(reasoner, manager);
        Set<OWLEntity> entities = getEntitiesForClustering(ke.getEntities());
        Distance<OWLEntity> distance = DistanceCreator
                .createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(ontology, ke);
        ClusterCreator clusterer = new ClusterCreator();
        Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(distance, entities);
        ClusterDecompositionModel<OWLEntity> model = clusterer
                .buildKnowledgeExplorerClusterDecompositionModel(ontology,
                        ke.getAxioms(), manager, clusters);
        Utils.saveToXML(model, outfile);
        return model;
    }

    private Set<OWLEntity> getEntitiesForClustering(Set<OWLEntity> signature) {
        final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
            @Override
            public int compare(OWLEntity o1, OWLEntity o2) {
                return shortFormProvider.getShortForm(o1).compareTo(
                        shortFormProvider.getShortForm(o2));
            }
        });
        entities.addAll(signature);
        return entities;
    }
}
