package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterStatistics;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.atomicdecomposition.Atom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposer;

public class ClusteringWithADEvaluationExperimentBase {

	protected static boolean firstTime = true;

	@SuppressWarnings("resource")
	protected static void printMetrics(ArrayList<SimpleMetric<?>> metrics,
			File file) throws FileNotFoundException {
		FileOutputStream fout = new FileOutputStream(file, true);
		PrintStream out = new PrintStream(fout);
		// if(!file.exists()){
		if (firstTime) {
			for (int i = 0; i < metrics.size(); i++) {
				out.print(metrics.get(i).getName() + ",");
			}
			out.println();
		}
		for (int i = 0; i < metrics.size(); i++) {
			out.print(metrics.get(i).getValue() + ",");
		}
		out.println();
	}

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
		toReturn.add(new SimpleMetric<Integer>("#LogicalAxioms",
				logicalAxiomCount));
		toReturn.add(new SimpleMetric<Integer>("#Entities", entitiesNo));
		return toReturn;
	}

	public static Collection<? extends SimpleMetric<?>> getADMetrics(
			AtomicDecomposer ad) {
		ArrayList<SimpleMetric<?>> toReturn = new ArrayList<SimpleMetric<?>>();
		Set<Atom> atoms = ad.getAtoms();
		toReturn.add(new SimpleMetric<Integer>("#OriginalAtoms", atoms.size()));
		double atomSize = 0;
		for (Atom a : atoms) {
			atomSize += a.getAxioms().size();
		}
		toReturn.add(new SimpleMetric<Double>("#MeanAtomSize", atomSize
				/ atoms.size()));
		return toReturn;
	}

	// FIXME: These are wrong. They need refinement
	public static <P> Collection<? extends SimpleMetric<?>> getClusteringStats(
			PrintStream out, Collection<Cluster<P>> sortedClusters) {
		ArrayList<SimpleMetric<?>> toReturn = new ArrayList<SimpleMetric<?>>();
		// MultiArrayMap<String, Number> indiFileMetrics = new
		// MultiArrayMap<String, Number>();
		double totalAverageInternalDistance = 0;
		int totalAverageExternalDistance = 0;
		int totalAverageMaxInternalDistance = 0;
		int totalAverageMinInternalDistance = 0;
		int totalAverageMaxExternalDistance = 0;
		int totalAverageMinExternalDistance = 0;
		double totalAverageHomogeneity = 0;

		int clNo = sortedClusters.size();
		int index = 0;
		out.println("cluster index, cluster size, "
				+ "average internal distance, average external distance, "
				+ "max internal distance, min internal distance, "
				+ "max external distance, min external distance, homogeneity");
		for (Cluster<P> cluster : sortedClusters) {
			ClusterStatistics<P> stats = ClusterStatistics
					.buildStatistics(cluster);
			out.print("cluster " + index);
			index++;
			out.println(cluster.size() + ","
					+ stats.getAverageInternalDistance() + ","
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
			totalAverageHomogeneity += (1 - stats.getAverageInternalDistance());
		}
		toReturn.add(new SimpleMetric<Double>("MeanInternalDistance",
				totalAverageInternalDistance / clNo));
		toReturn.add(new SimpleMetric<Double>("MeanExternalDistance",
				(double) totalAverageExternalDistance / clNo));
		toReturn.add(new SimpleMetric<Double>("MaxInternalDistance",
				(double) totalAverageMaxInternalDistance / clNo));
		toReturn.add(new SimpleMetric<Double>("MinInternalDistance",
				(double) totalAverageMinInternalDistance / clNo));
		toReturn.add(new SimpleMetric<Double>("MaxExternalDistance",
				(double) totalAverageMaxExternalDistance / clNo));
		toReturn.add(new SimpleMetric<Double>("minExternalDistance",
				(double) totalAverageMinExternalDistance / clNo));
		toReturn.add(new SimpleMetric<Double>("homogeneity",
				totalAverageHomogeneity / clNo));

		return toReturn;
	}

	public ClusteringWithADEvaluationExperimentBase() {
		super();
	}

}