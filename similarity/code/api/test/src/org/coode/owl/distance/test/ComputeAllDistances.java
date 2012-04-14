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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.coode.distance.entityrelevance.DefaultOWLEntityRelevancePolicy;
import org.coode.distance.owl.AxiomBasedDistance;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class ComputeAllDistances {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		List<IRI> iris = new ArrayList<IRI>(args.length);
		for (String string : args) {
			iris.add(IRI.create(string));
		}
		for (IRI iri : iris) {
			try {
				URI uri = iri.toURI();
				if (uri.getScheme().startsWith("file") && uri.isAbsolute()) {
					File file = new File(uri);
					File parentFile = file.getParentFile();
					if (parentFile.isDirectory()) {
						manager.addIRIMapper(new AutoIRIMapper(parentFile, true));
					}
				}
				manager.loadOntology(iri);
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			}
		}
		final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
			public int compare(OWLEntity o1, OWLEntity o2) {
				return shortFormProvider.getShortForm(o1).compareTo(
						shortFormProvider.getShortForm(o2));
			}
		});
		for (OWLOntology ontology : manager.getOntologies()) {
			entities.addAll(ontology.getSignature());
		}
		final AxiomBasedDistance distance = new AxiomBasedDistance(
				manager.getOntologies(), manager.getOWLDataFactory(),
				DefaultOWLEntityRelevancePolicy.getAlwaysIrrelevantPolicy(), manager);
		final SimpleProximityMatrix<OWLEntity> distanceMatrix = new SimpleProximityMatrix<OWLEntity>(
				entities, distance);
		System.out.println(String.format(
				"Finished computing distance between %d entities", distanceMatrix
						.getObjects().size()));
	}
}
