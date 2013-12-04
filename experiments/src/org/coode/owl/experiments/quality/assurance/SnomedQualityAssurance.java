package org.coode.owl.experiments.quality.assurance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;

import org.coode.basetest.ClusteringHelper;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.SimpleMetric;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import experiments.ExperimentHelper;
import experiments.ExperimentUtils;

public class SnomedQualityAssurance {
    /** @param args
     *            args[0]: "ontology usage", args[1]: keyword */
    public static void main(String[] args) {
        try {
            if (args.length >= 2) {
                String keyword = args[1];
                String ontologyName = args[0];
                System.out.println("Working with " + keyword);
                String xmlName = ontologyName.replace(".owl", ".xml");
                File xml = new File(xmlName);
                if (!xml.exists()) {
                    OWLOntology o = ExperimentUtils.loadOntology(new File(ontologyName));
                    LexicalAndAxiomaticPatternBasedQualityAssurance<?> qa = new LexicalAndAxiomaticPatternBasedQualityAssurance(
                            keyword, o);
                    Set<OWLAnnotationAssertionAxiom> annotations = ExperimentHelper
                            .stripOntologyFromAnnotationAssertions(o);
                    System.out.println("Computing clusters...");
                    ClusterDecompositionModel<OWLEntity> model = ClusteringHelper
                            .getSyntacticPopularityClusterModel(o);
                    Utils.saveToXML(model, xml);
                    System.out.println("Regularity results were saved in " + xml);
                    System.out.println("Computing quality assurance stats...");
                    o.getOWLOntologyManager().addAxioms(o, annotations);
                    ArrayList<SimpleMetric<?>> stats = qa.getQualityAssuranceStats(model);
                    print(stats, new File(ontologyName.replace(".owl", ".csv")));
                }
            } else {
                System.out.println(String.format(
                        "Usage java -cp ... %s <ontology-module.owl> <keyword>",
                        SnomedQualityAssurance.class.getCanonicalName()));
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static void print(ArrayList<SimpleMetric<?>> stats, File file) {
        try {
            PrintStream out = new PrintStream(file);
            for (SimpleMetric<?> m : stats) {
                out.println(m.getName() + "," + m.getValue().toString());
            }
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
