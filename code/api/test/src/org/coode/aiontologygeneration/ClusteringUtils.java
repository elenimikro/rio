package org.coode.aiontologygeneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
import org.coode.oppl.exceptions.RuntimeExceptionHandler;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;
import org.xml.sax.SAXException;

public class ClusteringUtils {

    public static MultiMap<OWLAxiom, OWLAxiomInstantiation> getGeneralisationMap(
            final OWLOntology onto, final Set<Set<OWLEntity>> clusters) {
        OWLOntologyManager ontologyManager = onto.getOWLOntologyManager();
        MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
        try {
            OPPLFactory opplFactory = new OPPLFactory(ontologyManager, onto, null);
            ConstraintSystem constraintSystem = opplFactory.createConstraintSystem();
            OWLObjectGeneralisation generalisation = Utils.getOWLObjectGeneralisation(
                    clusters, ontologyManager.getOntologies(), constraintSystem);
            RuntimeExceptionHandler runtimeExceptionHandler = new QuickFailRuntimeExceptionHandler();
            for (Set<OWLEntity> cluster : clusters) {
                MultiMap<OWLAxiom, OWLAxiomInstantiation> map = Utils
                        .buildGeneralisationMap(cluster, onto.getImportsClosure(), onto.getAxioms(),
                                generalisation, runtimeExceptionHandler);
                generalisationMap.putAll(map);
            }
        } catch (OPPLException e) {
            e.printStackTrace();
        }
        return generalisationMap;
    }

    public static Set<Set<OWLEntity>> loadClustersFromFile(final String filename,
            final OWLOntologyManager ontologyManager) throws FileNotFoundException,
            ParserConfigurationException, SAXException, IOException {
        Set<Set<OWLEntity>> clusters = Utils.readFromXML(new FileInputStream(filename),
                ontologyManager);
        return clusters;
    }

    public static void main(final String[] args) throws Exception {
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(
                new File("comparison.txt"))));
        String line = r.readLine();
        while (line != null) {
            String[] strings = line.split(" ");
            String ontologyFile = strings[0];
            String newXMLFile = null;
            String oldXMLFile = null;
            for (int i = 1; i < strings.length; i++) {
                if (strings[i].length() > 5) {
                    if (newXMLFile == null) {
                        newXMLFile = strings[i];
                    } else {
                        oldXMLFile = strings[i];
                    }
                }
            }
            if (newXMLFile != null && oldXMLFile != null) {
                check(ontologyFile, newXMLFile, oldXMLFile);
            }
            line = r.readLine();
        }
    }

    public static boolean check(final String onto, final String input1,
            final String input2) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntology(IRI.create(new File(onto)));
        return check(ontology, input1, input2);
    }

    public static boolean check(final OWLOntology onto, final String input1,
            final String input2) throws Exception {
        // the clusters are sorted
        boolean toReturn = true;
        Set<Set<OWLEntity>> loadClustersFromFile1 = ClusteringUtils.loadClustersFromFile(
                input1, onto.getOWLOntologyManager());
        // MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap1 =
        // ClusteringUtils
        // .getGeneralisationMap(onto, loadClustersFromFile1);
        Set<Set<OWLEntity>> loadClustersFromFile2 = ClusteringUtils.loadClustersFromFile(
                input2, onto.getOWLOntologyManager());
        // MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap2 =
        // ClusteringUtils
        // .getGeneralisationMap(onto, loadClustersFromFile2);
        Set<Set<OWLEntity>> common = new HashSet<Set<OWLEntity>>();
        if (!loadClustersFromFile1.equals(loadClustersFromFile2)) {
            int diffs = 0;
            int size = 0;
            for (Set<OWLEntity> s : loadClustersFromFile1) {
                boolean found = false;
                for (Set<OWLEntity> s2 : loadClustersFromFile2) {
                    if (s2.equals(s)) {
                        found = true;
                    }
                }
                if (found) {
                    common.add(s);
                } else {
                    diffs++;
                    size += s.size();
                }
            }
            for (Set<OWLEntity> s : loadClustersFromFile2) {
                boolean found = false;
                for (Set<OWLEntity> s2 : loadClustersFromFile1) {
                    if (s2.equals(s)) {
                        found = true;
                    }
                }
                if (found) {
                    common.add(s);
                } else {
                    diffs++;
                    size += s.size();
                }
            }
            toReturn = diffs == 0;
            if (diffs != 0) {
                System.out.println("ClusteringUtils.check() " + onto + " commons: "
                        + common.size() + "\t" + diffs + "\t" + size + "\t"
                        + (double) size / diffs);
            }
        }
        return toReturn;
    }
}
