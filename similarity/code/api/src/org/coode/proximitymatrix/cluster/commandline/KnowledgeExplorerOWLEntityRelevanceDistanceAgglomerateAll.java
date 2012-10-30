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

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.basetest.ClusterCreator;
import org.coode.basetest.DistanceCreator;
import org.coode.basetest.TestHelper;
import org.coode.distance.Distance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.distance.wrapping.DistanceTableObject;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillerJFactImpl;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillersFactplusplusImpl;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
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
public class KnowledgeExplorerOWLEntityRelevanceDistanceAgglomerateAll{
    /** @param args
     * @throws OWLOntologyCreationException 
     * @throws ParserConfigurationException 
     * @throws OPPLException 
     * @throws TransformerException 
     * @throws TransformerFactoryConfigurationError */
    public static void main(final String[] args) throws OWLOntologyCreationException, OPPLException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
        KnowledgeExplorerOWLEntityRelevanceDistanceAgglomerateAll agglomerator = new KnowledgeExplorerOWLEntityRelevanceDistanceAgglomerateAll();
        agglomerator.checkArgumentsAndRun(args);
    }
  
	public void checkArgumentsAndRun(final String[] args)
			throws OWLOntologyCreationException, OPPLException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {
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

	public ClusterDecompositionModel<OWLEntity> run(File outfile, IRI iri)
			throws OWLOntologyCreationException, OPPLException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = TestHelper.loadIRIMappers(iri, manager);

	 JFactReasoner reasoner = new JFactReasoner(ontology, new SimpleConfiguration(),
	                BufferingMode.NON_BUFFERING);
		reasoner.precomputeInferences();
		KnowledgeExplorer ke = new KnowledgeExplorerMaxFillersFactplusplusImpl(reasoner);
		//KnowledgeExplorer ke = new KnowledgeExplorerMaxFillerJFactImpl(reasoner, manager);
		final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		Set<OWLEntity> entities = new TreeSet<OWLEntity>(
				new Comparator<OWLEntity>() {
					public int compare(final OWLEntity o1, final OWLEntity o2) {
						return shortFormProvider.getShortForm(o1).compareTo(
								shortFormProvider.getShortForm(o2));
					}
				});
		
		Set<OWLEntity> set = ke.getEntities();
		assertNotNull(set);
		for(OWLEntity e : set){
			if(!e.isType(EntityType.OBJECT_PROPERTY)){
				entities.add(e);
			}
		}
		Distance<OWLEntity> distance = DistanceCreator.createKnowledgeExplorerOWLEntityRelevanceBasedDistance(manager, ke);
		ClusterCreator clusterer = new ClusterCreator();
		Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(ontology, distance, entities);
		ClusterDecompositionModel<OWLEntity> model = clusterer
				.buildKnowledgeExplorerClusterDecompositionModel(ontology,
						ke.getAxioms(), manager, clusters);
		Utils.saveToXML(model, manager, outfile);
		return model;
	}
}
