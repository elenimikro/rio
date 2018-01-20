package org.coode.aiontologygeneration;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

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

/** @author eleni */
public class ClusteringUtils {
    /**
     * @param onto onto
     * @param clusters clusters
     * @return generalisation map
     */
    public static MultiMap<OWLAxiom, OWLAxiomInstantiation> getGeneralisationMap(OWLOntology onto,
        Set<Set<OWLEntity>> clusters) {
        OWLOntologyManager ontologyManager = onto.getOWLOntologyManager();
        MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<>();
        try {
            OPPLFactory opplFactory = new OPPLFactory(ontologyManager, onto, null);
            ConstraintSystem constraintSystem = opplFactory.createConstraintSystem();
            OWLObjectGeneralisation generalisation = Utils.getOWLObjectGeneralisation(clusters,
                asList(ontologyManager.ontologies()), constraintSystem);
            for (Set<OWLEntity> cluster : clusters) {
                MultiMap<OWLAxiom, OWLAxiomInstantiation> map = Utils.buildGeneralisationMap(
                    cluster, asList(onto.importsClosure()), onto.axioms(), generalisation);
                generalisationMap.putAll(map);
            }
        } catch (OPPLException e) {
            e.printStackTrace();
        }
        return generalisationMap;
    }

    /**
     * @param filename filename
     * @param ontologyManager ontologyManager
     * @return cluster entities
     * @throws FileNotFoundException FileNotFoundException
     * @throws ParserConfigurationException ParserConfigurationException
     * @throws SAXException SAXException
     * @throws IOException IOException
     */
    public static Set<Set<OWLEntity>> loadClustersFromFile(String filename,
        OWLOntologyManager ontologyManager)
        throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        Set<Set<OWLEntity>> clusters =
            Utils.readFromXML(new FileInputStream(filename), ontologyManager);
        return clusters;
    }

    /**
     * @param args args
     * @throws Exception Exception
     */
    public static void main(String[] args) throws Exception {
        BufferedReader r = new BufferedReader(
            new InputStreamReader(new FileInputStream(new File("comparison.txt"))));
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

    /**
     * @param onto onto
     * @param input1 input1
     * @param input2 input2
     * @return true if checked
     * @throws Exception Exception
     */
    public static boolean check(String onto, String input1, String input2) throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntology(IRI.create(new File(onto)));
        return check(ontology, input1, input2);
    }

    /**
     * @param onto onto
     * @param input1 input1
     * @param input2 input2
     * @return true if checked
     * @throws Exception Exception
     */
    public static boolean check(OWLOntology onto, String input1, String input2) throws Exception {
        // the clusters are sorted
        boolean toReturn = true;
        Set<Set<OWLEntity>> loadClustersFromFile1 =
            ClusteringUtils.loadClustersFromFile(input1, onto.getOWLOntologyManager());
        Set<Set<OWLEntity>> loadClustersFromFile2 =
            ClusteringUtils.loadClustersFromFile(input2, onto.getOWLOntologyManager());
        Set<Set<OWLEntity>> common = new HashSet<>();
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
                System.out.println("ClusteringUtils.check() " + onto + " commons: " + common.size()
                    + "\t" + diffs + "\t" + size + "\t" + (double) size / diffs);
            }
        }
        return toReturn;
    }
}
