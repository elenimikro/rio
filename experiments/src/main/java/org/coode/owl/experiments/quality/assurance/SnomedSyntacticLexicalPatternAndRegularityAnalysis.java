package org.coode.owl.experiments.quality.assurance;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisationStatistics;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.SimpleMetric;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import experiments.ExperimentUtils;

@SuppressWarnings("javadoc")
public class SnomedSyntacticLexicalPatternAndRegularityAnalysis {
    private final Set<String> lexicalPatterns = new HashSet<String>();
    public static final String VARIABLE_NAME_INVALID_CHARACTERS_REGEXP = "[[^\\?]&&[^\\p{Alnum}]&&[^-_]]";
    public static final String RESULTS_BASE = "/Volumes/Passport-mac/Expeiments/";

    /** @param args
     *            [0]:Ontology path, args[1]:xmlPatternsFile */
    public static void main(String[] args) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance()
                .getTime());
        String resultsDir = "Results_" + timeStamp + "/";
        new File(resultsDir).mkdirs();
        String ontoname = "snomed/2013/snomed_StatedRDfXML_20130131.owl";
        File ontoFile = new File(ontoname);
        List<String> keywords = ExperimentUtils
                .extractListFromFile("processedSemanticKeywords.txt");
        File toSaveMetrics = new File(resultsDir + "allLexicalAxiomRegularityResults.csv");
        run(resultsDir, ontoFile, keywords, toSaveMetrics);
    }

    public static void run(String resultsDir, File ontoFile,
            List<String> lexicalPatterns, File toSaveMetrics) {
        try {
            System.out
                    .println(String.format("Loading ontology %s...", ontoFile.getPath()));
            OWLOntology onto = OWLManager.createOWLOntologyManager()
                    .loadOntologyFromOntologyDocument(ontoFile);
            for (String lexpat : lexicalPatterns) {
                runAnalysis(resultsDir, toSaveMetrics, onto, lexpat);
            }
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void runAnalysis(String resultsBase, File toSaveMetrics,
            OWLOntology onto, String lexpat) throws ParserConfigurationException,
            TransformerFactoryConfigurationError, TransformerException,
            FileNotFoundException {
        System.out.println(String.format("Working with %s", lexpat));
        File toXML = new File(resultsBase
                + lexpat.replaceAll(VARIABLE_NAME_INVALID_CHARACTERS_REGEXP, "_")
                + "_regularities.xml");
        if (!toXML.exists()) {
            LexicalAndAxiomaticPatternBasedQualityAssurance<Cluster<OWLEntity>> qa = new LexicalAndAxiomaticPatternBasedQualityAssurance<Cluster<OWLEntity>>(
                    lexpat, onto);
            System.out.println(String.format("Number of target entities: %s", qa
                    .getTargetEntities().size()));
            int usageSize = qa.getTargetEntitiesUsage().size();
            System.out.println(String.format("Usage of target entities: %s axioms",
                    usageSize));
            // if (processedKeywords.contains(lexpat)) {
            System.out.println("Computing regularities...");
            ClusterDecompositionModel<OWLEntity> model = qa.getRegularitiesBasedOnUsage();
            Utils.saveToXML(model, toXML);
            System.out.println(String.format("Regularirities were saved in %s",
                    toXML.getPath()));
            System.out.println("Printing metrics....");
            List<SimpleMetric<?>> stats = GeneralisationStatistics.buildStatistics(model)
                    .getStats();
            stats.addAll(qa.getQualityAssuranceStats(model));
            ExperimentUtils.printMetrics(stats, toSaveMetrics);
            // }
        }
    }

    public SnomedSyntacticLexicalPatternAndRegularityAnalysis(Set<String> lexicalPatterns) {
        this.lexicalPatterns.addAll(lexicalPatterns);
    }
}
