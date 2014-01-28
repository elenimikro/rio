package org.coode.knowledgeexplorer.distance.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Set;

import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillersFactplusplusImpl;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillersImpl;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasoner;
import uk.ac.manchester.cs.factplusplus.owlapiv3.OWLKnowledgeExplorationReasonerWrapper;
import uk.ac.manchester.cs.jfact.JFactReasoner;

/** @author eleni */
@SuppressWarnings("javadoc")
public class KnowledgeExplorerTest {
    public static void main(String[] args) throws OWLOntologyCreationException {
        knowledgeExplorerTest();
    }

    @Test
    public static void knowledgeExplorerTest() throws OWLOntologyCreationException {
        File f = new File("similarity/ontologies/amino-acid-original.owl");
        OWLOntology o = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(f);
        JFactReasoner reasoner = new JFactReasoner(o, new SimpleConfiguration(),
                BufferingMode.NON_BUFFERING);
        reasoner.precomputeInferences();
        KnowledgeExplorer ke = new KnowledgeExplorerMaxFillersFactplusplusImpl(reasoner);
        assertNotNull(ke.getEntities());
        ke = new KnowledgeExplorerMaxFillersImpl(reasoner,
                new OWLKnowledgeExplorationReasonerWrapper(new FaCTPlusPlusReasoner(o,
                        new SimpleConfiguration(), BufferingMode.NON_BUFFERING)));
        Set<OWLEntity> set = ke.getEntities();
        assertNotNull(set);
    }
}
