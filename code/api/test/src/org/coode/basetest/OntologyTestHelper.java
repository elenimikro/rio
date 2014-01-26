package org.coode.basetest;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/** @author eleni */
public class OntologyTestHelper {
    /** @return ontology */
    public static OWLOntology getPeople() {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o;
        try {
            o = m.loadOntologyFromOntologyDocument(new File(
                    "experiment-ontologies/people.owl"));
            return o;
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }

    /** @return ontology */
    public static OWLOntology getSmallPaperOntology() {
        try {
            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = m.loadOntologyFromOntologyDocument(new File(
                    "ontologies/myPeople.owl"));
            int counter = 1;
            for (OWLAxiom ax : ontology.getAxioms()) {
                System.out.println(counter + " " + ax);
                counter++;
            }
            // OWLDataFactory factory = m.getOWLDataFactory();
            // OWLClass vehicle = factory.getOWLClass(IRI
            // .create("urn:test#Vehicle"));
            // OWLClass van = factory.getOWLClass(IRI.create("urn:test#Van"));
            // OWLClass bus = factory.getOWLClass(IRI.create("urn:test#Bus"));
            // OWLClass lorry =
            // factory.getOWLClass(IRI.create("urn:test#Lorry"));
            // OWLClass bicycle = factory.getOWLClass(IRI
            // .create("urn:test#Bicycle"));
            // OWLClass driver = factory
            // .getOWLClass(IRI.create("urn:test#Driver"));
            // OWLClass vanDriver = factory.getOWLClass(IRI
            // .create("urn:test#VanDriver"));
            // OWLClass busDriver = factory.getOWLClass(IRI
            // .create("urn:test#VanDriver"));
            // OWLClass lorryDriver = factory.getOWLClass(IRI
            // .create("urn:test#VanDriver"));
            // OWLClass petOwner = factory.getOWLClass(IRI
            // .create("urn:test#PetOwner"));
            // OWLClass catOwner = factory.getOWLClass(IRI
            // .create("urn:test#CatOwner"));
            // OWLClass dogOwner = factory.getOWLClass(IRI
            // .create("urn:test#DogOwner"));
            // OWLClass cat = factory.getOWLClass(IRI.create("urn:test#cat"));
            // OWLClass dog = factory.getOWLClass(IRI.create("urn:test#dog"));
            //
            // OWLObjectProperty drives = factory.getOWLObjectProperty(IRI
            // .create("urn:test#drives"));
            // OWLObjectProperty hasPet = factory.getOWLObjectProperty(IRI
            // .create("urn:test#drives"));
            //
            // OWLSubClassOfAxiom vanSubVehicle =
            // factory.getOWLSubClassOfAxiom(van, vehicle);
            // OWLSubClassOfAxiom busSubVehicle =
            // factory.getOWLSubClassOfAxiom(bus, vehicle);
            // OWLSubClassOfAxiom vanSubVehicle =
            // factory.getOWLSubClassOfAxiom(van, vehicle);
            return ontology;
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }

    /** @return ontology */
    public static OWLOntology getSmallTestOntology() {
        try {
            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            OWLOntology o = m.createOntology();
            OWLDataFactory factory = m.getOWLDataFactory();
            OWLClass a = factory.getOWLClass(IRI.create("urn:test#A"));
            OWLClass b = factory.getOWLClass(IRI.create("urn:test#B"));
            OWLClass c = factory.getOWLClass(IRI.create("urn:test#C"));
            OWLClass d = factory.getOWLClass(IRI.create("urn:test#D"));
            OWLClass e = factory.getOWLClass(IRI.create("urn:test#E"));
            OWLClass f = factory.getOWLClass(IRI.create("urn:test#F"));
            OWLClass g = factory.getOWLClass(IRI.create("urn:test#G"));
            OWLClass i = factory.getOWLClass(IRI.create("urn:test#I"));
            OWLClass j = factory.getOWLClass(IRI.create("urn:test#J"));
            OWLClass k = factory.getOWLClass(IRI.create("urn:test#K"));
            OWLSubClassOfAxiom ab = factory.getOWLSubClassOfAxiom(a, b);
            OWLSubClassOfAxiom bc = factory.getOWLSubClassOfAxiom(b, c);
            OWLSubClassOfAxiom db = factory.getOWLSubClassOfAxiom(d, b);
            OWLSubClassOfAxiom ec = factory.getOWLSubClassOfAxiom(e, c);
            OWLSubClassOfAxiom fg = factory.getOWLSubClassOfAxiom(f, g);
            OWLSubClassOfAxiom gi = factory.getOWLSubClassOfAxiom(g, i);
            OWLSubClassOfAxiom jg = factory.getOWLSubClassOfAxiom(j, g);
            OWLSubClassOfAxiom ki = factory.getOWLSubClassOfAxiom(k, i);
            m.addAxiom(o, ab);
            m.addAxiom(o, bc);
            m.addAxiom(o, db);
            m.addAxiom(o, ec);
            m.addAxiom(o, fg);
            m.addAxiom(o, gi);
            m.addAxiom(o, jg);
            m.addAxiom(o, ki);
            return o;
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }

    /** @return ontology */
    public static OWLOntology getSmallMeaningfullTestOntology() {
        try {
            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            OWLOntology o = m.createOntology();
            OWLDataFactory factory = m.getOWLDataFactory();
            OWLClass a = factory.getOWLClass(IRI.create("urn:test#Freestyle_swimming"));
            OWLClass b = factory.getOWLClass(IRI.create("urn:test#Swimming"));
            OWLClass c = factory.getOWLClass(IRI.create("urn:test#Sport"));
            OWLClass d = factory
                    .getOWLClass(IRI.create("urn:test#Breaststroke_swimming"));
            OWLClass e = factory.getOWLClass(IRI.create("urn:test#Cycling"));
            OWLClass f = factory.getOWLClass(IRI.create("urn:test#Watercolor_painting"));
            OWLClass g = factory.getOWLClass(IRI.create("urn:test#Painting"));
            OWLClass i = factory.getOWLClass(IRI.create("urn:test#Hobby"));
            OWLClass j = factory.getOWLClass(IRI.create("urn:test#Oil_painting"));
            OWLClass k = factory.getOWLClass(IRI.create("urn:test#Sightseeing"));
            OWLSubClassOfAxiom ab = factory.getOWLSubClassOfAxiom(a, b);
            OWLSubClassOfAxiom bc = factory.getOWLSubClassOfAxiom(b, c);
            OWLSubClassOfAxiom db = factory.getOWLSubClassOfAxiom(d, b);
            OWLSubClassOfAxiom ec = factory.getOWLSubClassOfAxiom(e, c);
            OWLSubClassOfAxiom fg = factory.getOWLSubClassOfAxiom(f, g);
            OWLSubClassOfAxiom gi = factory.getOWLSubClassOfAxiom(g, i);
            OWLSubClassOfAxiom jg = factory.getOWLSubClassOfAxiom(j, g);
            OWLSubClassOfAxiom ki = factory.getOWLSubClassOfAxiom(k, i);
            m.addAxiom(o, ab);
            m.addAxiom(o, bc);
            m.addAxiom(o, db);
            m.addAxiom(o, ec);
            m.addAxiom(o, fg);
            m.addAxiom(o, gi);
            m.addAxiom(o, jg);
            m.addAxiom(o, ki);
            return o;
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }
    }
}
