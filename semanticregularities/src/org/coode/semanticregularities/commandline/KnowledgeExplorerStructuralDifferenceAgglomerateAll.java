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
import org.coode.distance.owl.StructuralAxiomRelevanceAxiomBasedDistance;
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

/**
 * Class for computing syntactic similarities, using the AxiomBased distance
 * with the objproperties always relevant policy.
 * 
 * @author elenimikroyannidi
 */
public class KnowledgeExplorerStructuralDifferenceAgglomerateAll {
	/**
	 * @param args
	 * @throws OWLOntologyCreationException
	 * @throws ParserConfigurationException
	 * @throws OPPLException
	 * @throws TransformerException
	 * @throws TransformerFactoryConfigurationError
	 */
	public static void main(final String[] args)
			throws OWLOntologyCreationException, OPPLException,
			ParserConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {
		KnowledgeExplorerStructuralDifferenceAgglomerateAll agglomerator = new KnowledgeExplorerStructuralDifferenceAgglomerateAll();
		agglomerator.checkArgumentsAndRun(args);
	}

	public Distance<OWLEntity> getDistance(final OWLOntologyManager manager) {
		final Distance<OWLEntity> distance = new StructuralAxiomRelevanceAxiomBasedDistance(
				manager.getOntologies(), manager.getOWLDataFactory(), manager);
		return distance;
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

	public ClusterDecompositionModel<OWLEntity> run(File outfile,
			IRI ontologyIri) throws OWLOntologyCreationException,
			OPPLException, ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = TestHelper.loadIRIMappers(ontologyIri, manager);
		JFactReasoner reasoner = new JFactReasoner(ontology,
				new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
		reasoner.precomputeInferences();
		KnowledgeExplorer ke = new KnowledgeExplorerMaxFillersFactplusplusImpl(
				reasoner);
		// KnowledgeExplorer ke = new
		// KnowledgeExplorerMaxFillerJFactImpl(reasoner, manager);
		Set<OWLEntity> entities = getEntitiesForClustering(ke.getEntities());
		Distance<OWLEntity> distance = DistanceCreator
				.createKnowledgeExplorerAxiomRelevanceAxiomBasedDistance(
						ontology, ke);
		ClusterCreator clusterer = new ClusterCreator();
		Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(ontology,
				distance, entities);
		ClusterDecompositionModel<OWLEntity> model = clusterer
				.buildKnowledgeExplorerClusterDecompositionModel(ontology,
						ke.getAxioms(), manager, clusters);
		Utils.saveToXML(model, outfile);
		return model;
	}

	private Set<OWLEntity> getEntitiesForClustering(Set<OWLEntity> signature) {
		final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		Set<OWLEntity> entities = new TreeSet<OWLEntity>(
				new Comparator<OWLEntity>() {
					@Override
					public int compare(final OWLEntity o1, final OWLEntity o2) {
						return shortFormProvider.getShortForm(o1).compareTo(
								shortFormProvider.getShortForm(o2));
					}
				});
		entities.addAll(signature);
		return entities;
	}
}
