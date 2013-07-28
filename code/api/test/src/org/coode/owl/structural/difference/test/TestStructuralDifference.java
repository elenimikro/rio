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
package org.coode.owl.structural.difference.test;

import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.coode.basetest.TestHelper;
import org.coode.owl.structural.difference.IncomparableObjectsStructuralDifferenceReport;
import org.coode.owl.structural.difference.NoDifferenceStructuralDifferenceReport;
import org.coode.owl.structural.difference.SomeDifferenceStructuralDifferenceReport;
import org.coode.owl.structural.difference.StructuralDifference;
import org.coode.owl.structural.difference.StructuralDifferenceReport;
import org.coode.owl.structural.difference.StructuralDifferenceReportVisitorAdapter;
import org.coode.owl.structural.difference.StructuralDifferenceReportVisitorExAdapter;
import org.coode.owl.structural.position.Position;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

public class TestStructuralDifference extends TestCase {
    private static class DiffAdapter extends StructuralDifferenceReportVisitorAdapter {
        @Override
        public
                void
                visitNoDifferenceStructuralDifferenceReport(
                        final NoDifferenceStructuralDifferenceReport noDifferenceStructuralDifferenceReport) {
            fail("Wrong kind of report");
        }

        @Override
        public
                void
                visitIncomparableObjectsStructuralDifferenceReport(
                        final IncomparableObjectsStructuralDifferenceReport incomparableObjectsStructuralDifferenceReport) {
            fail("Wrong kind of report");
        }
    }

    private final class Personalized3 extends
            StructuralDifferenceReportVisitorExAdapter<List<Integer>> {
        private Personalized3(final List<Integer> defaultValue) {
            super(defaultValue);
        }

        @Override
        public
                List<Integer>
                visitSomeDifferenceStructuralDifferenceReport(
                        final SomeDifferenceStructuralDifferenceReport someDifferenceStructuralDifferenceReport) {
            return someDifferenceStructuralDifferenceReport.getPosition();
        }
    }

    private final class Personalized1 extends DiffAdapter {
        @Override
        public
                void
                visitSomeDifferenceStructuralDifferenceReport(
                        final SomeDifferenceStructuralDifferenceReport someDifferenceStructuralDifferenceReport) {
            List<Integer> position = someDifferenceStructuralDifferenceReport
                    .getPosition();
            assertTrue(position.size() == 1);
            assertTrue(position.get(0).intValue() == 2);
        }
    }

    private static final class PersonalizedReportVisitor extends DiffAdapter {
        @Override
        public
                void
                visitSomeDifferenceStructuralDifferenceReport(
                        final SomeDifferenceStructuralDifferenceReport someDifferenceStructuralDifferenceReport) {
            List<Integer> position = someDifferenceStructuralDifferenceReport
                    .getPosition();
            assertTrue(position.size() == 2);
            assertTrue(position.get(0).intValue() == 2);
            assertTrue(position.get(1).intValue() == 2);
        }
    }

    public void testGetTopDifference() {
        OWLOntology ontology = TestHelper.getPizza();
        // OWLOntologyManager ontologyManager =
        // ontology.getOWLOntologyManager();
        StructuralDifference difference = new StructuralDifference();
        for (OWLAxiom axiom : ontology.getAxioms()) {
            StructuralDifferenceReport topDifference = difference.getTopDifference(axiom,
                    axiom);
            assertTrue(topDifference == StructuralDifferenceReport.NO_DIFFERENCE);
            topDifference = difference.getTopDifference(ontology, axiom);
            assertTrue(topDifference == StructuralDifferenceReport.INCOMPARABLE);
        }
    }

    public void testAreComparable() {
        OWLOntology ontology = TestHelper.getPizza();
        // OWLOntologyManager ontologyManager =
        // ontology.getOWLOntologyManager();
        StructuralDifference difference = new StructuralDifference();
        for (OWLAxiom axiom : ontology.getAxioms()) {
            boolean areComparable = difference.areComparable(axiom, axiom);
            assertTrue(areComparable);
        }
    }

    public void testSomeDifference() {
        ToStringRenderer.getInstance().setRenderer(
                new ManchesterOWLSyntaxOWLObjectRendererImpl());
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLDataFactory dataFactory = ontologyManager.getOWLDataFactory();
        OWLClass a = dataFactory.getOWLClass(IRI.create("A"));
        OWLClass b = dataFactory.getOWLClass(IRI.create("B"));
        OWLClass c = dataFactory.getOWLClass(IRI.create("C"));
        OWLObjectProperty p = dataFactory.getOWLObjectProperty(IRI.create("p"));
        OWLObjectIntersectionOf aANDb = dataFactory.getOWLObjectIntersectionOf(a, b);
        OWLObjectIntersectionOf aANDc = dataFactory.getOWLObjectIntersectionOf(a, c);
        OWLObjectUnionOf aORc = dataFactory.getOWLObjectUnionOf(a, c);
        OWLObjectSomeValuesFrom somePAANDB = dataFactory.getOWLObjectSomeValuesFrom(p,
                aANDb);
        OWLObjectSomeValuesFrom somePAANDC = dataFactory.getOWLObjectSomeValuesFrom(p,
                aANDc);
        OWLObjectSomeValuesFrom somePAORC = dataFactory.getOWLObjectSomeValuesFrom(p,
                aORc);
        StructuralDifference difference = new StructuralDifference();
        StructuralDifferenceReport topDifference = difference.getTopDifference(aANDb,
                aANDc);
        System.out.printf("Between {%s, %s} is %s \n", aANDb, aANDc, topDifference);
        topDifference.accept(new Personalized1());
        topDifference = difference.getTopDifference(somePAANDB, somePAANDC);
        System.out.printf("Between {%s, %s} is %s \n", somePAANDB, somePAANDC,
                topDifference);
        topDifference.accept(new PersonalizedReportVisitor());
        topDifference = difference.getTopDifference(somePAANDB, somePAORC);
        System.out.printf("Between {%s, %s} is at %s  which is %s \n", somePAANDB,
                somePAORC, topDifference, Position.get(somePAANDB, topDifference
                        .accept(new Personalized3(Collections.<Integer> emptyList()))));
        topDifference.accept(new Personalized1());
    }

    public void testSomeDifferenceComplete() {
        ToStringRenderer.getInstance().setRenderer(
                new ManchesterOWLSyntaxOWLObjectRendererImpl());
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLDataFactory dataFactory = ontologyManager.getOWLDataFactory();
        OWLClass a = dataFactory.getOWLClass(IRI.create("A"));
        OWLClass b = dataFactory.getOWLClass(IRI.create("B"));
        OWLClass c = dataFactory.getOWLClass(IRI.create("C"));
        OWLObjectProperty p = dataFactory.getOWLObjectProperty(IRI.create("p"));
        OWLObjectProperty q = dataFactory.getOWLObjectProperty(IRI.create("q"));
        OWLObjectIntersectionOf aANDb = dataFactory.getOWLObjectIntersectionOf(a, b);
        OWLObjectIntersectionOf aANDc = dataFactory.getOWLObjectIntersectionOf(a, c);
        OWLObjectSomeValuesFrom somePAANDB = dataFactory.getOWLObjectSomeValuesFrom(p,
                aANDb);
        OWLObjectSomeValuesFrom somePAANDC = dataFactory.getOWLObjectSomeValuesFrom(p,
                aANDc);
        OWLObjectSomeValuesFrom someQAANDC = dataFactory.getOWLObjectSomeValuesFrom(q,
                aANDc);
        StructuralDifference difference = new StructuralDifference();
        List<StructuralDifferenceReport> topDifferences = difference.getTopDifferences(
                somePAANDB, somePAANDC);
        System.out.printf("Between {%s, %s} is %s \n", somePAANDB, somePAANDC,
                topDifferences);
        assertTrue(topDifferences.size() == 1);
        for (StructuralDifferenceReport topDifference : topDifferences) {
            topDifference.accept(new PersonalizedReportVisitor());
        }
        topDifferences = difference.getTopDifferences(somePAANDB, someQAANDC);
        System.out.printf("Between {%s, %s} is %s \n", somePAANDB, someQAANDC,
                topDifferences);
        assertTrue(topDifferences.size() == 2);
    }

    public void testSomeDifferenceCompleteASubClassAxiom() {
        ToStringRenderer.getInstance().setRenderer(
                new ManchesterOWLSyntaxOWLObjectRendererImpl());
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLDataFactory dataFactory = ontologyManager.getOWLDataFactory();
        OWLClass a = dataFactory.getOWLClass(IRI.create("blah#A"));
        OWLClass b = dataFactory.getOWLClass(IRI.create("blah#B"));
        OWLClass c = dataFactory.getOWLClass(IRI.create("blah#C"));
        OWLClass d = dataFactory.getOWLClass(IRI.create("blah#D"));
        OWLSubClassOfAxiom aSubClassOfb = dataFactory.getOWLSubClassOfAxiom(a, b);
        OWLSubClassOfAxiom cSubClassOfd = dataFactory.getOWLSubClassOfAxiom(c, d);
        OWLObjectIntersectionOf aANDb = dataFactory.getOWLObjectIntersectionOf(a, b);
        OWLObjectIntersectionOf aANDc = dataFactory.getOWLObjectIntersectionOf(a, c);
        OWLSubClassOfAxiom aANDbSubClassOfaANDc = dataFactory.getOWLSubClassOfAxiom(
                aANDb, aANDc);
        OWLSubClassOfAxiom aANDcSubClassOfaANDb = dataFactory.getOWLSubClassOfAxiom(
                aANDc, aANDb);
        StructuralDifference difference = new StructuralDifference();
        List<StructuralDifferenceReport> topDifferences = difference.getTopDifferences(
                aSubClassOfb, cSubClassOfd);
        System.out.printf("Between {%s, %s} is %s \n", aSubClassOfb, cSubClassOfd,
                topDifferences);
        assertTrue(topDifferences.size() == 2);
        topDifferences = difference.getTopDifferences(aANDcSubClassOfaANDb,
                aANDbSubClassOfaANDc);
        System.out.printf("Between {%s, %s} is %s \n", aANDcSubClassOfaANDb,
                aANDbSubClassOfaANDc, topDifferences);
        assertTrue(topDifferences.size() == 2);
    }
}
