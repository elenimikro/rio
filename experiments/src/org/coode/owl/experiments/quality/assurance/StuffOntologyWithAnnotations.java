package org.coode.owl.experiments.quality.assurance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import experiments.ExperimentUtils;

public class StuffOntologyWithAnnotations {
    /** @param args
     *            args[0]: Asserted ontology, args[1]: Ontology to stuff. */
    public static void main(String[] args) {
        System.out.println("Loading asserted ontology...");
        OWLOntology onto = ExperimentUtils.loadOntology(new File(args[0]));
        System.out.println("Loading inferred ontology....");
        OWLOntology inferredOnto = ExperimentUtils.loadOntology(new File(args[1]));
        Set<OWLAnnotationAssertionAxiom> annotations = onto
                .getAxioms(AxiomType.ANNOTATION_ASSERTION);
        System.out.println(annotations.size() + " are going to be added...");
        inferredOnto.getOWLOntologyManager().addAxioms(inferredOnto, annotations);
        try {
            inferredOnto.getOWLOntologyManager().saveOntology(inferredOnto,
                    new FileOutputStream(new File(args[1])));
        } catch (OWLOntologyStorageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Ontology was saved...");
    }
}
