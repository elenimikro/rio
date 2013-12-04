package experiments;

import java.io.File;
import java.util.Collection;

import org.coode.distance.Distance;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecomposition;
import org.coode.utils.owl.DistanceCreator;
import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.atomicdecomposition.Atom;

public class ADPatternInductionExperiment {
    public ADPatternInductionExperiment() {
        // TODO Auto-generated constructor stub
    }

    /** @param args
     * @throws OPPLException
     * @throws OWLOntologyCreationException */
    public static void main(String[] args) throws OWLOntologyCreationException,
            OPPLException {
        run(args[0]);
    }

    public static ClusterDecompositionModel<OWLEntity> run(String filename)
            throws OWLOntologyCreationException, OPPLException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology o = m.loadOntologyFromOntologyDocument(new File(filename));
        ClusterDecompositionModel<OWLEntity> model = getSyntacticClusteringModel(o);
        GeneralisedAtomicDecomposition<OWLEntity> gad = new GeneralisedAtomicDecomposition<OWLEntity>(
                model, o);
        printMergedAtomPatterns(gad);
        return model;
    }

    private static ClusterDecompositionModel<OWLEntity> getSyntacticClusteringModel(
            OWLOntology o) throws OPPLException {
        OWLOntologyManager m = o.getOWLOntologyManager();
        System.out
                .println("ADPatternInductionExperiment.getSyntacticClusteringModel() ontology "
                        + o.toString());
        Distance<OWLEntity> distance = DistanceCreator
                .createStructuralAxiomRelevanceAxiomBasedDistance(m);
        ClusterDecompositionModel<OWLEntity> model = ExperimentHelper
                .startSyntacticClustering(o, distance, null);
        return model;
    }

    private static void printMergedAtomPatterns(
            GeneralisedAtomicDecomposition<OWLEntity> gad) {
        MultiMap<Collection<OWLAxiom>, Atom> mergedAtoms = gad.getMergedAtoms();
        ToStringRenderer.getInstance().setRenderer(new ManchesterSyntaxRenderer());
        System.out.println("Number of atom patterns: " + mergedAtoms.keySet().size());
        int count = 1;
        for (Collection<OWLAxiom> col : mergedAtoms.keySet()) {
            System.out.println("Atom Pattern " + count + " :");
            for (OWLAxiom ax : col) {
                System.out.println(" \t" + ax);
            }
            System.out.println("Merged Atoms: " + "(" + mergedAtoms.get(col).size() + ")"
                    + mergedAtoms.get(col));
            count++;
        }
    }
}
