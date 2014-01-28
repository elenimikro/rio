package org.coode.proximitymatrix.cluster;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.atomicdecomposition.Atom;

/** @author eleni */
public class GeneralisedAtomicDecompositionMetrics {
    private final GeneralisedAtomicDecomposition<OWLEntity> gad;

    private GeneralisedAtomicDecompositionMetrics(
            GeneralisedAtomicDecomposition<OWLEntity> gad) {
        this.gad = gad;
    }

    /** @return atomic decomposition compression */
    public double getAtomicDecompositionCompression() {
        double toReturn = 0.0;
        int initialSize = gad.getAtomicDecomposer().getAtoms().size();
        int compressedSize = gad.getAtoms().size();
        toReturn = 1.0 - (double) compressedSize / initialSize;
        return toReturn;
    }

    /** @return ratio of merged generalisation */
    public double getRatioOfMergedGeneralisations() {
        int mergedGeneralisationsNo = 0;
        MultiMap<OWLAxiom, OWLAxiomInstantiation> logicalRegularities = getLogicalRegularities();
        MultiMap<Collection<OWLAxiom>, Atom> mergedAtoms = gad.getMergedAtoms();
        Set<Collection<OWLAxiom>> patterns = mergedAtoms.keySet();
        for (Collection<OWLAxiom> pattern : patterns) {
            for (OWLAxiom ax : pattern) {
                if (logicalRegularities.get(ax) != null) {
                    mergedGeneralisationsNo++;
                }
            }
        }
        return (double) mergedGeneralisationsNo / logicalRegularities.keySet().size();
    }

    private MultiMap<OWLAxiom, OWLAxiomInstantiation> getLogicalRegularities() {
        MultiMap<OWLAxiom, OWLAxiomInstantiation> logicalRegularities = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
        MultiMap<OWLAxiom, OWLAxiomInstantiation> syntacticRegularitiesMap = gad
                .getSyntacticRegularities();
        for (OWLAxiom ax : syntacticRegularitiesMap.keySet()) {
            if (ax.isLogicalAxiom()) {
                logicalRegularities.putAll(ax, syntacticRegularitiesMap.get(ax));
            }
        }
        return logicalRegularities;
    }

    /** It computes the number of instantiations that belong to the atoms that
     * got merged
     * 
     * @return mean merged axioms per generalization */
    public double getMeanMergedAxiomsPerGeneralisation() {
        MultiMap<OWLAxiom, OWLAxiomInstantiation> logicalRegularities = getLogicalRegularities();
        MultiMap<Collection<OWLAxiom>, Atom> mergedAtoms = gad.getMergedAtoms();
        Set<Atom> atoms = mergedAtoms.getAllValues();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (Atom a : atoms) {
            axioms.addAll(a.getAxioms());
        }
        Set<OWLAxiomInstantiation> instantiations = logicalRegularities.getAllValues();
        // Set<OWLAxiom> instSet = new HashSet<OWLAxiom>();
        int mergedAxiomsNo = 0;
        for (OWLAxiomInstantiation i : instantiations) {
            if (axioms.contains(i.getAxiom())) {
                mergedAxiomsNo++;
            }
        }
        return (double) mergedAxiomsNo / logicalRegularities.size();
    }

    /** @param gad
     *            gad
     * @return AD metric */
    public static GeneralisedAtomicDecompositionMetrics buildMetrics(
            GeneralisedAtomicDecomposition<OWLEntity> gad) {
        return new GeneralisedAtomicDecompositionMetrics(gad);
    }
}
