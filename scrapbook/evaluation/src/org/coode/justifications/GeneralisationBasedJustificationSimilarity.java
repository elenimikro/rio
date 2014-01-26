package org.coode.justifications;

import java.util.Set;

import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
public class GeneralisationBasedJustificationSimilarity {
    private final MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
    private final MultiMap<Double, OWLAxiom> valueMap = new MultiMap<Double, OWLAxiom>();
    private final OWLReasonerFactory rfactory;
    private final OWLOntology ontology;

    /** @param rfactory
     *            rfactory
     * @param ontology
     *            ontology
     * @param generalisationMap
     *            generalisationMap */
    public GeneralisationBasedJustificationSimilarity(OWLReasonerFactory rfactory,
            OWLOntology ontology,
            MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap) {
        this.rfactory = rfactory;
        this.ontology = ontology;
        this.generalisationMap.putAll(generalisationMap);
    }

    /** @return mean justification similarity */
    public double getTotalMeanJustificationSimilarity() {
        double sumMean = 0;
        for (OWLAxiom gen : generalisationMap.keySet()) {
            Set<OWLAxiom> entailments = Utils.extractAxioms(generalisationMap.get(gen));
            JustificationSimilarity just = new JustificationSimilarity(entailments,
                    rfactory, ontology);
            double similarity = just.getJustificationSimilarity();
            valueMap.put(similarity, gen);
            sumMean += similarity;
        }
        return sumMean / generalisationMap.keySet().size();
    }

    /** @return justification map */
    public MultiMap<Double, OWLAxiom> getJustificationSimilarityValueMap() {
        return valueMap;
    }

    /** @return generalisation map */
    public MultiMap<OWLAxiom, OWLAxiomInstantiation> getGeneralisationMap() {
        return generalisationMap;
    }
}
