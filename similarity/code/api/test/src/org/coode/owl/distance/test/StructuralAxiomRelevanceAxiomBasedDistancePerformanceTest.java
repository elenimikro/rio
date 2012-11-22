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

import java.util.HashSet;
import java.util.Set;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.structural.StructuralOWLObjectGeneralisation;
import org.coode.owl.wrappers.OWLEntityProvider;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class StructuralAxiomRelevanceAxiomBasedDistancePerformanceTest {
	public static void main(String[] args) {
		OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		try {
			OWLOntology ontology = ontologyManager.loadOntology(IRI
					.create("http://purl.obolibrary.org/obo/obi.owl"));
			Set<OWLDisjointClassesAxiom> axioms = ontology
					.getAxioms(AxiomType.DISJOINT_CLASSES);
			OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
			ConstraintSystem constraintSystem = factory.createConstraintSystem();
			OWLEntityProvider entityProvider = new OntologyManagerBasedOWLEntityProvider(
					ontologyManager);
			OWLAxiom generalisedAxiom = null;
			Set<OWLAxiomInstantiation> instantiations = new HashSet<OWLAxiomInstantiation>(
					axioms.size());
			System.out.println(String.format("Axiom size: %d", axioms.size()));
			for (OWLDisjointClassesAxiom owlDisjointClassesAxiom : axioms) {
				StructuralOWLObjectGeneralisation generalisation = new StructuralOWLObjectGeneralisation(
						entityProvider, constraintSystem);
				generalisedAxiom = (OWLAxiom) owlDisjointClassesAxiom
						.accept(generalisation);
				instantiations.add(new OWLAxiomInstantiation(owlDisjointClassesAxiom,
						generalisation.getSubstitutions()));
			}
            // AxiomGeneralisationTreeNode generalisationTreeNode = new
            // AxiomGeneralisationTreeNode(
            // generalisedAxiom, instantiations, constraintSystem);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}
}
