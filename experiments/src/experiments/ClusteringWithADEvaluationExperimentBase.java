package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterStatistics;
import org.coode.utils.SimpleMetric;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.atomicdecomposition.Atom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposition;

/** @author eleni */
public class ClusteringWithADEvaluationExperimentBase {
    protected static boolean firstTime = true;

    /** @param indi_metrics
     *            indi_metrics
     * @param file
     *            file
     * @throws FileNotFoundException
     *             FileNotFoundException */
    public static void printMetrics(List<SimpleMetric<?>> indi_metrics, File file)
            throws FileNotFoundException {
        FileOutputStream fout = new FileOutputStream(file, true);
        PrintStream out = new PrintStream(fout);
        // if(!file.exists()){
        if (firstTime) {
            for (int i = 0; i < indi_metrics.size(); i++) {
                out.print(indi_metrics.get(i).getName() + ",");
            }
            out.println();
        }
        for (int i = 0; i < indi_metrics.size(); i++) {
            out.print(indi_metrics.get(i).getValue() + ",");
        }
        firstTime = false;
        out.println();
        out.close();
    }

    /** @param m
     *            m
     * @return simple metrics */
    public static Collection<? extends SimpleMetric<?>> getBasicOntologyMetrics(
            OWLOntologyManager m) {
        ArrayList<SimpleMetric<?>> toReturn = new ArrayList<SimpleMetric<?>>();
        int axiomCount = 0;
        int logicalAxiomCount = 0;
        int entitiesNo = 0;
        for (OWLOntology ontology : m.getOntologies()) {
            axiomCount += ontology.getAxiomCount();
            logicalAxiomCount += ontology.getLogicalAxiomCount();
            entitiesNo += ontology.getSignature().size();
        }
        toReturn.add(new SimpleMetric<Integer>("#Axioms", axiomCount));
        toReturn.add(new SimpleMetric<Integer>("#LogicalAxioms", logicalAxiomCount));
        toReturn.add(new SimpleMetric<Integer>("#Entities", entitiesNo));
        return toReturn;
    }

    /** @param ad
     *            ad
     * @return ad metrics */
    public static Collection<? extends SimpleMetric<?>> getADMetrics(
            AtomicDecomposition ad) {
        ArrayList<SimpleMetric<?>> toReturn = new ArrayList<SimpleMetric<?>>();
        Set<Atom> atoms = ad.getAtoms();
        toReturn.add(new SimpleMetric<Integer>("#OriginalAtoms", atoms.size()));
        double atomSize = 0;
        for (Atom a : atoms) {
            atomSize += a.getAxioms().size();
        }
        toReturn.add(new SimpleMetric<Double>("#MeanAtomSize", atomSize / atoms.size()));
        return toReturn;
    }

    // FIXME: These are wrong. They need refinement
    /** @param out
     *            out
     * @param sortedClusters
     *            sortedClusters
     * @return clustering stats */
    public static <P> Collection<? extends SimpleMetric<?>> getClusteringStats(
            PrintStream out, Collection<Cluster<P>> sortedClusters) {
        ArrayList<SimpleMetric<?>> toReturn = new ArrayList<SimpleMetric<?>>();
        // MultiArrayMap<String, Number> indiFileMetrics = new
        // MultiArrayMap<String, Number>();
        double totalAverageInternalDistance = 0;
        double totalAverageExternalDistance = 0;
        double totalAverageMaxInternalDistance = 0;
        double totalAverageMinInternalDistance = 0;
        double totalAverageMaxExternalDistance = 0;
        double totalAverageMinExternalDistance = 0;
        double totalAverageHomogeneity = 0;
        int clNo = sortedClusters.size();
        int index = 0;
        out.println("cluster index, cluster size, "
                + "average internal distance, average external distance, "
                + "max internal distance, min internal distance, "
                + "max external distance, min external distance, homogeneity");
        for (Cluster<P> cluster : sortedClusters) {
            ClusterStatistics<P> stats = ClusterStatistics.buildStatistics(cluster);
            out.print("cluster " + index);
            index++;
            out.println(cluster.size() + "," + stats.getAverageInternalDistance() + ","
                    + stats.getAverageExternalDistance() + ","
                    + stats.getMaxInternalDistance() + ","
                    + stats.getMinInternalDistance() + ","
                    + stats.getMaxExternalDistance() + ","
                    + stats.getMinExternalDistance() + ","
                    + (1 - stats.getAverageInternalDistance()));
            totalAverageInternalDistance += stats.getAverageInternalDistance();
            totalAverageExternalDistance += stats.getAverageExternalDistance();
            totalAverageMaxInternalDistance += stats.getMaxInternalDistance();
            totalAverageMinInternalDistance += stats.getMinInternalDistance();
            totalAverageMaxExternalDistance += stats.getMaxExternalDistance();
            totalAverageMinExternalDistance += stats.getMinExternalDistance();
            totalAverageHomogeneity += 1 - stats.getAverageInternalDistance();
        }
        toReturn.add(new SimpleMetric<Double>("MeanInternalDistance",
                totalAverageInternalDistance / clNo));
        toReturn.add(new SimpleMetric<Double>("MeanExternalDistance",
                totalAverageExternalDistance / clNo));
        toReturn.add(new SimpleMetric<Double>("MaxInternalDistance",
                totalAverageMaxInternalDistance / clNo));
        toReturn.add(new SimpleMetric<Double>("MinInternalDistance",
                totalAverageMinInternalDistance / clNo));
        toReturn.add(new SimpleMetric<Double>("MaxExternalDistance",
                totalAverageMaxExternalDistance / clNo));
        toReturn.add(new SimpleMetric<Double>("minExternalDistance",
                totalAverageMinExternalDistance / clNo));
        toReturn.add(new SimpleMetric<Double>("homogeneity", totalAverageHomogeneity
                / clNo));
        return toReturn;
    }

    /**
     * 
     */
    public ClusteringWithADEvaluationExperimentBase() {
        super();
    }
}
