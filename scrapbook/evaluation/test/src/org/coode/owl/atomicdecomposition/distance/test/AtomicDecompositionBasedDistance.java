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
/**
 *
 */
package org.coode.owl.atomicdecomposition.distance.test;

import java.io.File;
import java.util.Arrays;

import org.coode.atomicdecomposition.distance.AxiomRelevanceAtomicDecompositionDepedenciesBasedDistance;
import org.coode.atomicdecomposition.distance.entityrelevance.AtomicDecompositionRankingRelevancePolicy;
import org.coode.atomicdecomposition.metrics.AtomicDecompositionRanking;
import org.coode.atomicdecomposition.wrappers.OWLAtomicDecompositionMap;
import org.coode.basetest.TestHelper;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.owl.AbstractAxiomBasedDistance;
import org.coode.distance.owl.AxiomBasedDistance;
import org.coode.metrics.RankingSlot;
import org.coode.owl.distance.test.DistanceBuilder;
import org.coode.owl.distance.test.DistanceTestCase;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/** @author Luigi Iannone */
@SuppressWarnings("javadoc")
public class AtomicDecompositionBasedDistance extends DistanceTestCase {
    @Override
    protected DistanceBuilder getDistanceBuilder() {
        return new DistanceBuilder() {
            @Override
            public AbstractAxiomBasedDistance getDistance(OWLOntology o) {
                return new AxiomRelevanceAtomicDecompositionDepedenciesBasedDistance(
                        o.getImportsClosure(), o.getOWLOntologyManager()
                                .getOWLDataFactory(), o.getOWLOntologyManager());
            }

            @Override
            public AbstractAxiomBasedDistance getDistance(OWLOntology o,
                    RelevancePolicy<OWLEntity> rp) {
                return new AxiomBasedDistance(o.getImportsClosure(), o
                        .getOWLOntologyManager().getOWLDataFactory(), rp,
                        o.getOWLOntologyManager());
            }
        };
    }

    public void testSpicinessSauceToppingAtomicDecompositionRelevance() {
        OWLOntology o = TestHelper.getPizza();
        // here create the atomic decomposition ranking
        OWLAtomicDecompositionMap map = new OWLAtomicDecompositionMap(o,
                getOntologyManager());
        AtomicDecompositionRanking ranking = AtomicDecompositionRanking.buildRanking(
                o.getImportsClosure(), map);
        // add the existing atomic decomposition policy
        RelevancePolicy<OWLEntity> policy = AtomicDecompositionRankingRelevancePolicy
                .getAbstractRankingRelevancePolicy(ranking);
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o, policy);
        OWLClass[] classes = getClasses(pizza_ns + "Spiciness", pizza_ns + "SauceTopping");
        properTest(distance, classes);
        int i = 1;
        System.out.println(String.format("Average atomic decomposition dependencies %s",
                ranking.getAverageValue()));
        for (RankingSlot<OWLEntity> s : ranking.getSortedRanking()) {
            System.out.println(String.format("%d. %s value %s is relevant: %b", i,
                    Arrays.toString(s.getMembers()), s.getValue(),
                    policy.isRelevant(s.getMembers()[0])));
            i++;
        }
    }

    public void testMolePercentPopularityRelevance() {
        OWLOntology o = getOntology("http://owl.cs.manchester.ac.uk/repository/download?ontology=http://sweet.jpl.nasa.gov/ontology/units.owl&format=RDF/XML");
        OWLAtomicDecompositionMap map = new OWLAtomicDecompositionMap(o,
                getOntologyManager());
        AtomicDecompositionRanking ranking = AtomicDecompositionRanking.buildRanking(
                o.getImportsClosure(), map);
        RelevancePolicy<OWLEntity> policy = AtomicDecompositionRankingRelevancePolicy
                .getAbstractRankingRelevancePolicy(ranking);
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o, policy);
        OWLClass[] classes = getClasses(
                "http://sweet.jpl.nasa.gov/ontology/units.owl#mole",
                "http://sweet.jpl.nasa.gov/ontology/units.owl#percent");
        properTest(distance, classes);
        int i = 1;
        System.out.println(String.format("Average popularity %s",
                ranking.getAverageValue()));
        for (RankingSlot<OWLEntity> s : ranking.getSortedRanking()) {
            System.out.println(String.format("%d. %s value %s is relevant: %b", i,
                    Arrays.toString(s.getMembers()), s.getValue(),
                    policy.isRelevant(s.getMembers()[0])));
            i++;
        }
    }

    public void testToyOntology() {
        OWLOntology o = getOntology(new File(
                "code/api/test/resources/RegularToyOntology.owl"));
        OWLAtomicDecompositionMap map = new OWLAtomicDecompositionMap(o,
                getOntologyManager());
        AtomicDecompositionRanking ranking = AtomicDecompositionRanking.buildRanking(
                o.getImportsClosure(), map);
        AtomicDecompositionRankingRelevancePolicy policy = AtomicDecompositionRankingRelevancePolicy
                .getAbstractRankingRelevancePolicy(ranking);
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o, policy);
        OWLNamedIndividual[] classes = getNamedIndividuals(
                "http://www.semanticweb.org/ontologies/2010/11/RegularToyOntology.owl#L_indi_1",
                "http://www.semanticweb.org/ontologies/2010/11/RegularToyOntology.owl#L_indi_2");
        properTest(distance, classes);
        int i = 1;
        System.out.println(String.format(
                "Average atomic decomposition dependencies %s standard deviation %f",
                ranking.getAverageValue(), policy.getStandardDeviation()));
        for (RankingSlot<OWLEntity> s : ranking.getSortedRanking()) {
            System.out.println(String.format("%d. %s value %s is relevant: %b", i,
                    Arrays.toString(s.getMembers()), s.getValue(),
                    policy.isRelevant(s.getMembers()[0])));
            i++;
        }
    }
}
