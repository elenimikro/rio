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

import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.owl.AbstractAxiomBasedDistance;
import org.coode.distance.owl.AtomicDecompositionGeneralisationTreeBasedDistance;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

public class AtomicDecompositionAxiomBasedDistanceTest extends DistanceTestCase {
	@Override
	protected DistanceBuilder getDistanceBuilder() {
		return new DistanceBuilder() {
			@Override
			public AbstractAxiomBasedDistance getDistance(OWLOntology o) {
				return new AtomicDecompositionGeneralisationTreeBasedDistance(
						o.getImportsClosure(), o.getOWLOntologyManager()
								.getOWLDataFactory(), o.getOWLOntologyManager());
			}

			@Override
			public AbstractAxiomBasedDistance getDistance(OWLOntology o,
					RelevancePolicy<OWLEntity> rp) {
				return null;
			}
		};
	}

	public void testGetAxiomsMozzarellaTopping() {
		OWLOntology o = getOntology(pizza_iri);
		AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o);
		OWLClass[] classes = getClasses(pizza_ns + "SpicyTopping");
		properTest(distance, o, classes);
	}

	public void testGetAxiomsPepperTopping() {
		OWLOntology o = getOntology(pizza_iri);
		AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o);
		OWLClass[] classes = getClasses(pizza_ns + "PepperTopping");
		properTest(distance, o, classes);
	}

	public void testDistanceGarlicToppingPizza() {
		OWLOntology o = getOntology(pizza_iri);
		AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o);
		OWLClass[] classes = getClasses(pizza_ns + "GarlicTopping", pizza_ns + "Pizza");
		properTest(distance, o, classes);
	}

	public void testDistancehasSpicinesshasTopping() {
		OWLOntology o = getOntology(pizza_iri);
		AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o);
		OWLObjectProperty[] objectProperties = getObjectProperties(pizza_ns
				+ "hasSpiciness", pizza_ns + "hasTopping");
		properTest(distance, o, objectProperties);
	}

	public void testDistanceSpicyPizza() {
		OWLOntology o = getOntology(pizza_iri);
		AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o);
		OWLClass[] classes = getClasses(pizza_ns + "SpicyPizza", pizza_ns
				+ "CheeseyPizza");
		properTest(distance, o, classes);
	}

	public void testDistanceCheesyMeatyPizza() {
		OWLOntology o = getOntology(pizza_iri);
		AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o);
		OWLClass[] classes = getClasses(pizza_ns + "CheeseyPizza", pizza_ns
				+ "MeatyPizza");
		properTest(distance, o, classes);
	}

	public void testDistancePeperoniSausageTopping() {
		OWLOntology o = getOntology(pizza_iri);
		AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o);
		OWLClass[] classes = getClasses(pizza_ns + "PeperoniSausageTopping", pizza_ns
				+ "HotSpicedBeefTopping");
		properTest(distance, o, classes);
	}
}
