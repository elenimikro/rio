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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.coode.basetest.TestHelper;
import org.coode.owl.structural.difference.IncomparableObjectsStructuralDifferenceReport;
import org.coode.owl.structural.difference.NoDifferenceStructuralDifferenceReport;
import org.coode.owl.structural.difference.SomeDifferenceStructuralDifferenceReport;
import org.coode.owl.structural.difference.StructuralDifference;
import org.coode.owl.structural.difference.StructuralDifferenceReport;
import org.coode.owl.structural.difference.StructuralDifferenceReportVisitorAdapter;
import org.coode.owl.structural.difference.StructuralDifferenceReportVisitorExAdapter;
import org.coode.utils.OntologyManagerUtils;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/** @author eleni */
@SuppressWarnings("javadoc")
public class TestStructuralDifference {
    private static class DiffAdapter extends StructuralDifferenceReportVisitorAdapter {
        public DiffAdapter() {}

        @Override
        public void visitNoDifferenceStructuralDifferenceReport(
            NoDifferenceStructuralDifferenceReport report) {
            fail("Wrong kind of report");
        }

        @Override
        public void visitIncomparableObjectsStructuralDifferenceReport(
            IncomparableObjectsStructuralDifferenceReport report) {
            fail("Wrong kind of report");
        }
    }

    private final class Personalized3
        extends StructuralDifferenceReportVisitorExAdapter<List<Integer>> {
        public Personalized3(List<Integer> defaultValue) {
            super(defaultValue);
        }

        @Override
        public List<Integer> visitSomeDifferenceStructuralDifferenceReport(
            SomeDifferenceStructuralDifferenceReport report) {
            return report.getPosition();
        }
    }

    private final class Personalized1 extends DiffAdapter {
        public Personalized1() {}

        @Override
        public void visitSomeDifferenceStructuralDifferenceReport(
            SomeDifferenceStructuralDifferenceReport report) {
            List<Integer> position = report.getPosition();
            assertTrue(position.size() == 1);
            assertTrue(position.get(0).intValue() == 2);
        }
    }

    private static final class PersonalizedReportVisitor extends DiffAdapter {
        public PersonalizedReportVisitor() {}

        @Override
        public void visitSomeDifferenceStructuralDifferenceReport(
            SomeDifferenceStructuralDifferenceReport report) {
            List<Integer> position = report.getPosition();
            assertTrue(position.size() == 2);
            assertTrue(position.get(0).intValue() == 2);
            assertTrue(position.get(1).intValue() == 2);
        }
    }

    @Test
    public void testGetTopDifference() {
        OWLOntology ontology = TestHelper.getPizza();
        // OWLOntologyManager ontologyManager =
        // ontology.getOWLOntologyManager();
        StructuralDifference difference = new StructuralDifference();
        ontology.axioms().forEach(axiom -> {
            StructuralDifferenceReport topDifference = difference.getTopDifference(axiom, axiom);
            assertTrue(topDifference == StructuralDifferenceReport.NO_DIFFERENCE);
            topDifference = difference.getTopDifference(ontology, axiom);
            assertTrue(topDifference == StructuralDifferenceReport.INCOMPARABLE);
        });
    }

    @Test
    public void testAreComparable() {
        OWLOntology ontology = TestHelper.getPizza();
        // OWLOntologyManager ontologyManager =
        // ontology.getOWLOntologyManager();
        StructuralDifference difference = new StructuralDifference();
        ontology.axioms().forEach(ax -> assertTrue(difference.areComparable(ax, ax)));
    }

    @Test
    public void testSomeDifference() {
        // ToStringRenderer.getInstance().setRenderer(
        // new ManchesterOWLSyntaxOWLObjectRendererImpl());
        OWLOntologyManager ontologyManager = OntologyManagerUtils.ontologyManager();
        OWLDataFactory dataFactory = ontologyManager.getOWLDataFactory();
        OWLClass a = dataFactory.getOWLClass(IRI.create("A"));
        OWLClass b = dataFactory.getOWLClass(IRI.create("B"));
        OWLClass c = dataFactory.getOWLClass(IRI.create("C"));
        OWLObjectProperty p = dataFactory.getOWLObjectProperty(IRI.create("p"));
        OWLObjectIntersectionOf aANDb = dataFactory.getOWLObjectIntersectionOf(a, b);
        OWLObjectIntersectionOf aANDc = dataFactory.getOWLObjectIntersectionOf(a, c);
        OWLObjectUnionOf aORc = dataFactory.getOWLObjectUnionOf(a, c);
        OWLObjectSomeValuesFrom somePAANDB = dataFactory.getOWLObjectSomeValuesFrom(p, aANDb);
        OWLObjectSomeValuesFrom somePAANDC = dataFactory.getOWLObjectSomeValuesFrom(p, aANDc);
        OWLObjectSomeValuesFrom somePAORC = dataFactory.getOWLObjectSomeValuesFrom(p, aORc);
        StructuralDifference difference = new StructuralDifference();
        StructuralDifferenceReport topDifference = difference.getTopDifference(aANDb, aANDc);
        topDifference.accept(new Personalized1());
        topDifference = difference.getTopDifference(somePAANDB, somePAANDC);
        topDifference.accept(new PersonalizedReportVisitor());
        topDifference = difference.getTopDifference(somePAANDB, somePAORC);
        topDifference.accept(new Personalized1());
    }

    @Test
    public void testSomeDifferenceComplete() {
        // ToStringRenderer.getInstance().setRenderer(
        // new ManchesterOWLSyntaxOWLObjectRendererImpl());
        OWLOntologyManager ontologyManager = OntologyManagerUtils.ontologyManager();
        OWLDataFactory dataFactory = ontologyManager.getOWLDataFactory();
        OWLClass a = dataFactory.getOWLClass(IRI.create("A"));
        OWLClass b = dataFactory.getOWLClass(IRI.create("B"));
        OWLClass c = dataFactory.getOWLClass(IRI.create("C"));
        OWLObjectProperty p = dataFactory.getOWLObjectProperty(IRI.create("p"));
        OWLObjectProperty q = dataFactory.getOWLObjectProperty(IRI.create("q"));
        OWLObjectIntersectionOf aANDb = dataFactory.getOWLObjectIntersectionOf(a, b);
        OWLObjectIntersectionOf aANDc = dataFactory.getOWLObjectIntersectionOf(a, c);
        OWLObjectSomeValuesFrom somePAANDB = dataFactory.getOWLObjectSomeValuesFrom(p, aANDb);
        OWLObjectSomeValuesFrom somePAANDC = dataFactory.getOWLObjectSomeValuesFrom(p, aANDc);
        OWLObjectSomeValuesFrom someQAANDC = dataFactory.getOWLObjectSomeValuesFrom(q, aANDc);
        StructuralDifference difference = new StructuralDifference();
        List<StructuralDifferenceReport> topDifferences =
            difference.getTopDifferences(somePAANDB, somePAANDC);
        assertTrue(topDifferences.size() == 1);
        for (StructuralDifferenceReport topDifference : topDifferences) {
            topDifference.accept(new PersonalizedReportVisitor());
        }
        topDifferences = difference.getTopDifferences(somePAANDB, someQAANDC);
        assertTrue(topDifferences.size() == 2);
    }

    public void testSomeDifferenceCompleteASubClassAxiom() {
        // ToStringRenderer.getInstance().setRenderer(
        // new ManchesterOWLSyntaxOWLObjectRendererImpl());
        OWLOntologyManager ontologyManager = OntologyManagerUtils.ontologyManager();
        OWLDataFactory dataFactory = ontologyManager.getOWLDataFactory();
        OWLClass a = dataFactory.getOWLClass(IRI.create("blah#A"));
        OWLClass b = dataFactory.getOWLClass(IRI.create("blah#B"));
        OWLClass c = dataFactory.getOWLClass(IRI.create("blah#C"));
        OWLClass d = dataFactory.getOWLClass(IRI.create("blah#D"));
        OWLSubClassOfAxiom aSubClassOfb = dataFactory.getOWLSubClassOfAxiom(a, b);
        OWLSubClassOfAxiom cSubClassOfd = dataFactory.getOWLSubClassOfAxiom(c, d);
        OWLObjectIntersectionOf aANDb = dataFactory.getOWLObjectIntersectionOf(a, b);
        OWLObjectIntersectionOf aANDc = dataFactory.getOWLObjectIntersectionOf(a, c);
        OWLSubClassOfAxiom aANDbSubClassOfaANDc = dataFactory.getOWLSubClassOfAxiom(aANDb, aANDc);
        OWLSubClassOfAxiom aANDcSubClassOfaANDb = dataFactory.getOWLSubClassOfAxiom(aANDc, aANDb);
        StructuralDifference difference = new StructuralDifference();
        List<StructuralDifferenceReport> topDifferences =
            difference.getTopDifferences(aSubClassOfb, cSubClassOfd);
        assertTrue(topDifferences.size() == 2);
        topDifferences = difference.getTopDifferences(aANDcSubClassOfaANDb, aANDbSubClassOfaANDc);
        assertTrue(topDifferences.size() == 2);
    }
}
