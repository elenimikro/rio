package org.coode.lexical.pattern.clustering;

import java.io.File;

import org.coode.proximitymatrix.cluster.LexicalClusterModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
public class MultipleLexicalPatternRegularitiesTest {
    /** @param args
     *            args
     * @throws Exception
     *             exception */
    public static void main(String[] args) throws Exception {
        // load ontology
        OWLOntology o = OWLManager
                .createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(
                        new File("/Users/elenimikroyannidi/LexicalAnalysis/so_2_5_1.obo"));
        // load lexical patterns
        LexicalPatternParser parser = new LexicalPatternParser(
                "/Users/elenimikroyannidi/LexicalAnalysis/SO/SequenceOnt_CaseSensitivefalse_PerctCov_Partition_5.0_ExtraRandom.xml",
                o);
        MultiMap<String, OWLEntity> lexicalPatternMap = parser.getLexicalPatternMap();
        // get axiomatic regularities
        AbstractLexicalPatternRegularities reg = new AbstractLexicalPatternRegularities(
                o, lexicalPatternMap);
        LexicalClusterModel model = reg.getAxiomRegularitiesFromLexicalPatterns();
        // save the model
        File file = new File("SO_test.xml");
        Utils.saveToXML(model, file);
        System.out.println("The file was saved in " + file.getPath());
    }
}
