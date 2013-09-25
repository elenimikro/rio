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
package org.coode.owl.atomicdecomposition.distance.test;

import org.coode.atomicdecomposition.distance.AtomicDecompositionGeneralisationTreeBasedDistance;
import org.coode.basetest.TestHelper;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.owl.AbstractAxiomBasedDistance;
import org.coode.owl.distance.test.DistanceBuilder;
import org.coode.owl.distance.test.DistanceTestCase;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

public class AtomicDecompositionAxiomBasedDistanceTest extends DistanceTestCase {
	@Override
	protected DistanceBuilder getDistanceBuilder() {
		return new DistanceBuilder() {
			@Override
			public AbstractAxiomBasedDistance getDistance(final OWLOntology o) {
				return new AtomicDecompositionGeneralisationTreeBasedDistance(
						o, o.getOWLOntologyManager().getOWLDataFactory(),
						o.getOWLOntologyManager());
			}

			@Override
			public AbstractAxiomBasedDistance getDistance(final OWLOntology o,
					final RelevancePolicy rp) {
				return null;
			}
		};
	}

	public void testGetAxiomsMozzarellaTopping() {
		OWLOntology o = TestHelper.getPizza();
		AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(
				o);
		OWLClass[] classes = getClasses(pizza_ns + "SpicyTopping");
		properTest(distance, classes);
	}

	public void testGetAxiomsPepperTopping() {
		OWLOntology o = TestHelper.getPizza();
		AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(
				o);
		OWLClass[] classes = getClasses(pizza_ns + "PepperTopping");
		properTest(distance, classes);
	}

	public void testDistanceGarlicToppingPizza() {
		OWLOntology o = TestHelper.getPizza();
		AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(
				o);
		OWLClass[] classes = getClasses(pizza_ns + "GarlicTopping", pizza_ns
				+ "Pizza");
		properTest(distance, classes);
	}

	public void testDistancehasSpicinesshasTopping() {
		OWLOntology o = TestHelper.getPizza();
		AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(
				o);
		OWLObjectProperty[] objectProperties = getObjectProperties(pizza_ns
				+ "hasSpiciness", pizza_ns + "hasTopping");
		properTest(distance, objectProperties);
	}

	public void testDistanceSpicyPizza() {
		OWLOntology o = TestHelper.getPizza();
		AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(
				o);
		OWLClass[] classes = getClasses(pizza_ns + "SpicyPizza", pizza_ns
				+ "CheeseyPizza");
		properTest(distance, classes);
	}

	public void testDistanceCheesyMeatyPizza() {
		OWLOntology o = TestHelper.getPizza();
		AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(
				o);
		OWLClass[] classes = getClasses(pizza_ns + "CheeseyPizza", pizza_ns
				+ "MeatyPizza");
		properTest(distance, classes);
	}

	public void testDistancePeperoniSausageTopping() {
		OWLOntology o = TestHelper.getPizza();
		AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(
				o);
		OWLClass[] classes = getClasses(pizza_ns + "PeperoniSausageTopping",
				pizza_ns + "HotSpicedBeefTopping");
		properTest(distance, classes);
	}
}
