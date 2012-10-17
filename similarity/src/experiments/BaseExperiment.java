package experiments;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.basetest.ClusterCreator;
import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.ClusterStatistics;
import org.coode.proximitymatrix.cluster.GeneralisationStatistics;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecomposition;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecompositionMetrics;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class BaseExperiment {
	
	Properties properties;
	
	
	public BaseExperiment() {
		properties = new Properties();
	}

	public static void setUpOutputHeader(PrintStream out){
		out.println("Ontology, #Axioms, #LogicalAxioms, SignatureSize, " +
				"#Clusters,#Generalisations,#Instantiations,MeanEntitiesPerCluster,GeneralisedAxioms%," +
				"MeanInstantiationsPerGeneralisation, MeanClusterCoverage," +
				"ADCompression,MeanMergedAxiomsPerGeneralisation,ratioOfMergedGeneralisations," +
				"MeanInternalDistance,MeanExternalDistance,MaxInternalDistance,MinInternalDistance,MaxExternalDistance," +
				"minExternalDistance,homogeneity");
	}
	
	public static ClusterDecompositionModel<OWLEntity> startClustering(
			OWLOntologyManager m, OWLOntology o) throws OPPLException,
			ParserConfigurationException {
		Distance<OWLEntity> distance = DistanceCreator
				.createAxiomRelevanceAxiomBasedDistance(m);
		ClusterCreator clusterer = new ClusterCreator();

		final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		Set<OWLEntity> entities = new TreeSet<OWLEntity>(
				new Comparator<OWLEntity>() {
					public int compare(final OWLEntity o1, final OWLEntity o2) {
						return shortFormProvider.getShortForm(o1).compareTo(
								shortFormProvider.getShortForm(o2));
					}
				});
		for (OWLOntology ontology : m.getOntologies()) {
			entities.addAll(ontology.getSignature());
		}
		ClusterDecompositionModel<OWLEntity> model = clusterer.agglomerateAll(o, distance, entities);
		return model;
	}
	
	public static void printStats(PrintStream out, String s, int axiomCount,
			int logicalAxiomCount, int entitiesNo,
			ClusterDecompositionModel<OWLEntity> model,
			GeneralisedAtomicDecomposition<OWLEntity> gad) {
		//generalisation quality metrics
		GeneralisationStatistics<OWLEntity> genStats = GeneralisationStatistics.buildStatistics(model);
		int clusterNo = model.getClusterList().size();
		int numberOfGeneralisations = genStats.getNumberOfGeneralisations();
		int numberOfInstantiations = genStats.getNumberOfInstantiations();
		double meanEntitiesPerCluster = genStats.getMeanEntitiesPerCluster();
		double ratioOfGeneralisedAxioms = genStats.getRatioOfGeneralisedAxiomsToTotalAxioms();
		double meanInstantiationsPerGeneralisation = genStats.getMeanOWLAxiomInstantiationsPerGeneralisation();
		double meanClusterCoverage = genStats.getMeanClusterCoveragePerGeneralisation();
		
		//ad compression metrics
		GeneralisedAtomicDecompositionMetrics adstats = GeneralisedAtomicDecompositionMetrics.buildMetrics(gad);
		double adCompression = adstats.getAtomicDecompositionCompression();
		double meanMergedAxiomsPerGeneralisation = adstats.getMeanMergedAxiomsPerGeneralisation();
		double ratioOfMergedGeneralisations = adstats.getRatioOfMergedGeneralisations();
		
		out.print(s + "," + axiomCount + "," + logicalAxiomCount + "," + entitiesNo + "," + 
				clusterNo + "," + numberOfGeneralisations + "," + numberOfInstantiations + "," + meanEntitiesPerCluster + "," + ratioOfGeneralisedAxioms + "," +
				meanInstantiationsPerGeneralisation + "," + meanClusterCoverage + "," +
				adCompression + "," + meanMergedAxiomsPerGeneralisation + "," + ratioOfMergedGeneralisations + ",");
	}
	
	public static <P> void printClusteringStats(PrintStream out, File f,
			Collection<Cluster<P>> sortedClusters) {
		try {	
			PrintStream indiout = new PrintStream(f);
			double totalAverageInternalDistance = 0;
			int totalAverageExternalDistance = 0;
			int totalAverageMaxInternalDistance = 0;
			int totalAverageMinInternalDistance = 0;
			int totalAverageMaxExternalDistance = 0; 
			int totalAverageMinExternalDistance = 0;
			double totalAverageHomogeneity = 0;
			
			int clNo = sortedClusters.size();
			int index = 0;
			indiout.println("cluster index, cluster size, "
					+ "average internal distance, average external distance, "
					+ "max internal distance, min internal distance, "
					+ "max external distance, min external distance, homogeneity");
			for (Cluster<P> cluster : sortedClusters) {
				ClusterStatistics<P> stats = ClusterStatistics.buildStatistics(cluster);
				indiout.print("cluster " + index);
				index++;
				indiout.println(cluster.size() + "," + stats.getAverageInternalDistance()
						+ "," + stats.getAverageExternalDistance() + ","
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
			out.println((double) totalAverageInternalDistance / clNo + ","
					+ (double) totalAverageExternalDistance/clNo + ","
					+ (double) totalAverageMaxInternalDistance/clNo + ","
					+ (double) totalAverageMinInternalDistance/clNo + ","
					+ (double) totalAverageMaxExternalDistance/clNo + ","
					+ (double) totalAverageMinExternalDistance/clNo + ","
					+ (double) totalAverageHomogeneity/clNo);
			indiout.close();
		} catch (IOException e) {
			System.out
					.println("StructuralDifferenceWrappingEquivalenceClassesAgglomerateAll.printHomogeneityStats() Cannot save extra metrics");
			e.printStackTrace(System.out);
		}
	}
	
	public static void print(PrintStream output, String[][] table) {
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				output.print(table[i][j].replaceAll("\n", " "));
				output.print('\t');
			}
			output.println();
		}
		// empty line to separate clusters
		output.println();
	}
}
