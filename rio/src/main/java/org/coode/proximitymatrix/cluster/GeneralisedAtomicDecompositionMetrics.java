package org.coode.proximitymatrix.cluster;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import java.util.Collection;
import java.util.Set;

import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.atomicdecomposition.Atom;

/** @author eleni */
public class GeneralisedAtomicDecompositionMetrics {
    private final GeneralisedAtomicDecomposition gad;

    private GeneralisedAtomicDecompositionMetrics(GeneralisedAtomicDecomposition gad) {
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
        MultiMap<OWLAxiom, OWLAxiomInstantiation> logicalRegularities = new MultiMap<>();
        MultiMap<OWLAxiom, OWLAxiomInstantiation> syntacticRegularitiesMap =
            gad.getSyntacticRegularities();
        for (OWLAxiom ax : syntacticRegularitiesMap.keySet()) {
            if (ax.isLogicalAxiom()) {
                logicalRegularities.putAll(ax, syntacticRegularitiesMap.get(ax));
            }
        }
        return logicalRegularities;
    }

    /**
     * It computes the number of instantiations that belong to the atoms that got merged
     * 
     * @return mean merged axioms per generalization
     */
    public double getMeanMergedAxiomsPerGeneralisation() {
        MultiMap<OWLAxiom, OWLAxiomInstantiation> logicalRegularities = getLogicalRegularities();
        MultiMap<Collection<OWLAxiom>, Atom> mergedAtoms = gad.getMergedAtoms();
        Set<OWLAxiom> instantiations =
            asSet(logicalRegularities.allValuesTransformed(OWLAxiomInstantiation::getAxiom));
        long mergedAxiomsNo = mergedAtoms.allValuesTransformed(Atom::getAxioms)
            .flatMap(Collection::stream).distinct().filter(instantiations::contains).count();
        return (double) mergedAxiomsNo / logicalRegularities.size();
    }

    /**
     * @param gad gad
     * @return AD metric
     */
    public static GeneralisedAtomicDecompositionMetrics buildMetrics(
        GeneralisedAtomicDecomposition gad) {
        return new GeneralisedAtomicDecompositionMetrics(gad);
    }
}
