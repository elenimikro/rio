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
package org.coode.owl.distance.test;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import java.io.File;
import java.util.List;

import org.coode.basetest.TestHelper;
import org.coode.distance.entityrelevance.AbstractRankingRelevancePolicy;
import org.coode.distance.entityrelevance.DefaultOWLEntityRelevancePolicy;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.distance.owl.AbstractAxiomBasedDistance;
import org.coode.distance.owl.AxiomBasedDistance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.metrics.owl.OWLEntityPopularityRanking;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.parameters.Imports;

/** @author Luigi Iannone */
@SuppressWarnings("javadoc")
public class TestAxiomBasedDistance extends DistanceTestCase {
    @Override
    protected DistanceBuilder getDistanceBuilder() {
        return new DistanceBuilder() {
            @Override
            public AbstractAxiomBasedDistance getDistance(OWLOntology o) {
                OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
                    o.getOWLOntologyManager().getOWLDataFactory(),
                    new ReplacementByKindStrategy(o.getOWLOntologyManager().getOWLDataFactory()));
                return new AxiomRelevanceAxiomBasedDistance(o.importsClosure(), owlEntityReplacer,
                    o.getOWLOntologyManager());
            }

            @Override
            public AbstractAxiomBasedDistance getDistance(OWLOntology o,
                RelevancePolicy<OWLEntity> rp) {
                return new AxiomBasedDistance(o.importsClosure(), rp, o.getOWLOntologyManager());
            }
        };
    }

    @Test
    public void testPizza() {
        OWLOntology o = TestHelper.getPizza();
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o,
            DefaultOWLEntityRelevancePolicy.getAlwaysIrrelevantPolicy());
        final List<OWLEntity> signature = asList(o.signature(Imports.INCLUDED));
        properTest(distance, signature);
    }

    public void testMargheritaSundriedTomatoTopping() {
        OWLOntology o = TestHelper.getPizza();
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o,
            DefaultOWLEntityRelevancePolicy.getAlwaysRelevantPolicy());
        List<OWLClass> classes =
            getClasses(pizza_ns + "Margherita", pizza_ns + "SundriedTomatoTopping");
        properTest(distance, classes);
    }

    public void testMargheritaSiciliana() {
        OWLOntology o = TestHelper.getPizza();
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o,
            DefaultOWLEntityRelevancePolicy.getAlwaysRelevantPolicy());
        List<OWLClass> classes = getClasses(pizza_ns + "Margherita", pizza_ns + "Siciliana");
        properTest(distance, classes);
    }

    public void testNapoletanaParmaHamTopping() {
        OWLOntology o = TestHelper.getPizza();
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o,
            DefaultOWLEntityRelevancePolicy.getAlwaysRelevantPolicy());
        List<OWLClass> classes = getClasses(pizza_ns + "Napoletana", pizza_ns + "ParmaHamTopping");
        properTest(distance, classes);
    }

    public void testUnclosedPizzaIceCream() {
        OWLOntology o = TestHelper.getPizza();
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o,
            AbstractRankingRelevancePolicy.getAbstractRankingRelevancePolicy(
                new OWLEntityPopularityRanking(asSet(o.signature()), o.importsClosure())));
        List<OWLClass> classes = getClasses(pizza_ns + "UnclosedPizza", pizza_ns + "IceCream");
        properTest(distance, classes);
    }

    public void testMargheritaSicilianaPopularityRelevance() {
        OWLOntology o = TestHelper.getPizza();
        OWLEntityPopularityRanking ranking =
            OWLEntityPopularityRanking.buildRanking(asList(o.importsClosure()));
        RelevancePolicy<OWLEntity> policy =
            AbstractRankingRelevancePolicy.getAbstractRankingRelevancePolicy(ranking);
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o, policy);
        List<OWLClass> classes = getClasses(pizza_ns + "Margherita", pizza_ns + "Siciliana");
        properTest(distance, classes);
    }

    public void testSpicinessSauceToppingPopularityRelevance() {
        OWLOntology o = TestHelper.getPizza();
        OWLEntityPopularityRanking ranking =
            OWLEntityPopularityRanking.buildRanking(asList(o.importsClosure()));
        RelevancePolicy<OWLEntity> policy =
            AbstractRankingRelevancePolicy.getAbstractRankingRelevancePolicy(ranking);
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o, policy);
        List<OWLClass> classes = getClasses(pizza_ns + "Spiciness", pizza_ns + "SauceTopping");
        properTest(distance, classes);
    }

    public void testMolePercentPopularityRelevance() throws OWLOntologyCreationException {
        OWLOntology o = getOntology(
            "http://owl.cs.manchester.ac.uk/repository/download?ontology=http://sweet.jpl.nasa.gov/ontology/units.owl&format=RDF/XML");
        OWLEntityPopularityRanking ranking =
            OWLEntityPopularityRanking.buildRanking(asList(o.importsClosure()));
        RelevancePolicy<OWLEntity> policy =
            AbstractRankingRelevancePolicy.getAbstractRankingRelevancePolicy(ranking);
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o, policy);
        List<OWLClass> classes = getClasses("http://sweet.jpl.nasa.gov/ontology/units.owl#mole",
            "http://sweet.jpl.nasa.gov/ontology/units.owl#percent");
        properTest(distance, classes);
    }

    public void testToyOntology() throws OWLOntologyCreationException {
        OWLOntology o = getOntology(new File("code/api/test/resources/RegularToyOntology.owl"));
        OWLEntityPopularityRanking ranking =
            OWLEntityPopularityRanking.buildRanking(asList(o.importsClosure()));
        AbstractRankingRelevancePolicy<OWLEntity> policy =
            AbstractRankingRelevancePolicy.getAbstractRankingRelevancePolicy(ranking);
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o, policy);
        List<OWLNamedIndividual> classes = getNamedIndividuals(
            "http://www.semanticweb.org/ontologies/2010/11/RegularToyOntology.owl#L_indi_1",
            "http://www.semanticweb.org/ontologies/2010/11/RegularToyOntology.owl#L_indi_2");
        properTest(distance, classes);
    }

    public void testSNOMEDINtraAbdominalArteryVsHemolosys() throws OWLOntologyCreationException {
        // http://www.ihtsdo.org/SCT_122860000 (intra abdominal artery)
        // http://www.ihtsdo.org/SCT_95605009 (Hemolosys)
        OWLOntology o = getOntology(
            new File("code/api/test/resources/sct-20100731-stated_Hypertension-subs_module.owl"));
        AbstractAxiomBasedDistance distance = getDistanceBuilder().getDistance(o);
        List<OWLClass> classes =
            getClasses("http://www.ihtsdo.org/SCT_122860000", "http://www.ihtsdo.org/SCT_95605009");
        properTest(distance, classes);
    }
}
