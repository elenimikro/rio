package org.coode.owl.structural.difference.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.coode.basetest.TestHelper;
import org.coode.distance.owl.AbstractAxiomBasedDistance;
import org.coode.oppl.OPPLShortFormProvider;
import org.coode.utils.owl.DistanceCreator;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;

/** @author eleni */
@SuppressWarnings("javadoc")
public class StructuralDistanceTest {
    @Test
    public void differentStructuralDistanceTest() throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.createOntology();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLClass a = f.getOWLClass(IRI.create("urn:test#A"));
        OWLClass a1 = f.getOWLClass(IRI.create("urn:test#A1"));
        OWLClass b1 = f.getOWLClass(IRI.create("urn:test#B1"));
        OWLClass a2 = f.getOWLClass(IRI.create("urn:test#A2"));
        OWLClass b2 = f.getOWLClass(IRI.create("urn:test#B2"));
        OWLClass a3 = f.getOWLClass(IRI.create("urn:test#A3"));
        OWLClass b3 = f.getOWLClass(IRI.create("urn:test#B3"));
        OWLClass c = f.getOWLClass(IRI.create("urn:test#C"));
        OWLClass d = f.getOWLClass(IRI.create("urn:test#D"));
        OWLClass e = f.getOWLClass(IRI.create("urn:test#E"));
        OWLObjectProperty p1 = f.getOWLObjectProperty(IRI.create("urn:test#p1"));
        OWLObjectProperty p2 = f.getOWLObjectProperty(IRI.create("urn:test#p2"));
        OWLObjectSomeValuesFrom p1someB1 = f.getOWLObjectSomeValuesFrom(p1, b1);
        OWLObjectSomeValuesFrom p1someB2 = f.getOWLObjectSomeValuesFrom(p1, b2);
        OWLObjectSomeValuesFrom p1someB3 = f.getOWLObjectSomeValuesFrom(p1, b3);
        OWLObjectSomeValuesFrom p1someC = f.getOWLObjectSomeValuesFrom(p1, c);
        OWLObjectSomeValuesFrom p1someD = f.getOWLObjectSomeValuesFrom(p1, d);
        OWLObjectSomeValuesFrom p2someE = f.getOWLObjectSomeValuesFrom(p2, e);
        OWLSubClassOfAxiom A1Subp1someB1 = f.getOWLSubClassOfAxiom(a1, p1someB1);
        OWLSubClassOfAxiom A1Subp1someC = f.getOWLSubClassOfAxiom(a1, p1someC);
        OWLSubClassOfAxiom A1Subp1someD = f.getOWLSubClassOfAxiom(a1, p1someD);
        OWLSubClassOfAxiom A2Subp1someB2 = f.getOWLSubClassOfAxiom(a2, p1someB2);
        OWLSubClassOfAxiom A2Subp1someC = f.getOWLSubClassOfAxiom(a2, p1someC);
        OWLSubClassOfAxiom A3Subp1someB3 = f.getOWLSubClassOfAxiom(a3, p1someB3);
        OWLSubClassOfAxiom A3Subp1someC = f.getOWLSubClassOfAxiom(a3, p1someC);
        OWLSubClassOfAxiom ASubp2someE = f.getOWLSubClassOfAxiom(a, p2someE);
        m.addAxiom(o, A1Subp1someB1);
        m.addAxiom(o, A1Subp1someC);
        m.addAxiom(o, A1Subp1someD);
        m.addAxiom(o, A2Subp1someB2);
        m.addAxiom(o, A2Subp1someC);
        m.addAxiom(o, A3Subp1someB3);
        m.addAxiom(o, A3Subp1someC);
        m.addAxiom(o, ASubp2someE);
        // ToStringRenderer.getInstance().setRenderer(
        // new ManchesterOWLSyntaxOWLObjectRendererImpl());
        AbstractAxiomBasedDistance distance = (AbstractAxiomBasedDistance) DistanceCreator
            .createStructuralAxiomRelevanceAxiomBasedDistance(m);
        // Collection<OWLAxiom> a1_axioms = distance.getAxioms(a1);
        o.axioms().forEach(System.out::println);
        AbstractAxiomBasedDistance popularity_distance =
            (AbstractAxiomBasedDistance) DistanceCreator.createAxiomRelevanceAxiomBasedDistance(m);
        o.signature().forEach(en -> {
            System.out.println("Structural Axioms of " + en);
            for (OWLAxiom ax : distance.getAxioms(en)) {
                System.out.println("\t " + ax);
            }
            System.out.println("Popularity Axioms of " + en);
            for (OWLAxiom ax : popularity_distance.getAxioms(en)) {
                System.out.println("\t " + ax);
            }
        });
    }

    @Test
    public void testStructuralPizza() {
        OWLOntology o = TestHelper.getPizza();
        AbstractAxiomBasedDistance distance = (AbstractAxiomBasedDistance) DistanceCreator
            .createStructuralAxiomRelevanceAxiomBasedDistance(o.getOWLOntologyManager());
        // Collection<OWLAxiom> a1_axioms = distance.getAxioms(a1);
        ManchesterOWLSyntaxOWLObjectRendererImpl renderer =
            new ManchesterOWLSyntaxOWLObjectRendererImpl();
        OPPLShortFormProvider shortFormProvider =
            new OPPLShortFormProvider(new AnnotationValueShortFormProvider(
                Arrays.asList(o.getOWLOntologyManager().getOWLDataFactory().getRDFSLabel()),
                Collections.<OWLAnnotationProperty, List<String>>emptyMap(),
                o.getOWLOntologyManager()));
        renderer.setShortFormProvider(shortFormProvider);
        o.signature().peek(e -> System.out.println("Axioms of " + e)).forEach(e -> distance
            .getAxioms(e).forEach(ax -> System.out.println("\t " + renderer.render(ax))));
    }

    @Test
    public void testStructuralPeople() {
        OWLOntology o = TestHelper.getPizza();
        AbstractAxiomBasedDistance distance = (AbstractAxiomBasedDistance) DistanceCreator
            .createStructuralAxiomRelevanceAxiomBasedDistance(o.getOWLOntologyManager());
        // Collection<OWLAxiom> a1_axioms = distance.getAxioms(a1);
        ManchesterOWLSyntaxOWLObjectRendererImpl renderer =
            new ManchesterOWLSyntaxOWLObjectRendererImpl();
        // ToStringRenderer.getInstance().setRenderer(
        // new ManchesterOWLSyntaxOWLObjectRendererImpl());
        o.signature().peek(e -> System.out.println("Axioms of " + e)).forEach(e -> distance
            .getAxioms(e).forEach(ax -> System.out.println("\t " + renderer.render(ax))));
    }

    @Test
    public void testPopularityPeople() {
        OWLOntology o = TestHelper.getPizza();
        AbstractAxiomBasedDistance distance = (AbstractAxiomBasedDistance) DistanceCreator
            .createAxiomRelevanceAxiomBasedDistance(o.getOWLOntologyManager());
        // Collection<OWLAxiom> a1_axioms = distance.getAxioms(a1);
        ManchesterOWLSyntaxOWLObjectRendererImpl renderer =
            new ManchesterOWLSyntaxOWLObjectRendererImpl();
        // ToStringRenderer.getInstance().setRenderer(
        // new ManchesterOWLSyntaxOWLObjectRendererImpl());
        o.signature().peek(e -> System.out.println("Axioms of " + e)).forEach(e -> distance
            .getAxioms(e).forEach(ax -> System.out.println("\t " + renderer.render(ax))));
    }
}
