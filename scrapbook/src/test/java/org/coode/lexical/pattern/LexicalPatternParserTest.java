package org.coode.lexical.pattern;

import java.io.File;

import org.coode.lexical.pattern.clustering.LexicalPatternParser;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
@SuppressWarnings("javadoc")
public class LexicalPatternParserTest {
    @Test
    public void testXmlParser() {
        try {
            String inputFile = "/Users/elenimikroyannidi/LexicalAnalysis/SO/SequenceOnt_CaseSensitivefalse_PerctCov_Partition_5.0_ExtraRandom.xml";
            File ontoFile = new File(
                    "/Users/elenimikroyannidi/LexicalAnalysis/so_2_5_1.obo");
            OWLOntology o = OWLManager.createOWLOntologyManager()
                    .loadOntologyFromOntologyDocument(ontoFile);
            LexicalPatternParser parser = new LexicalPatternParser(inputFile, o);
            MultiMap<String, OWLEntity> patMap = parser.getLexicalPatternMap();
            for (String pat : patMap.keySet()) {
                System.out.println("Pattern: " + pat);
                for (OWLEntity e : patMap.get(pat)) {
                    System.out.println("\t" + e);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
