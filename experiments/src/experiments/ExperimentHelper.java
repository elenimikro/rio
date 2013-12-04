package experiments;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.coode.distance.Distance;
import org.coode.distance.wrapping.DistanceTableObject;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.ClusterStatistics;
import org.coode.proximitymatrix.cluster.GeneralisationStatistics;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecomposition;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecompositionMetrics;
import org.coode.utils.SimpleMetric;
import org.coode.utils.owl.ClusterCreator;
import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class ExperimentHelper {
    private static ClusterCreator clusterer;

    public static ClusterDecompositionModel<OWLEntity> startSyntacticClustering(
            OWLOntology o, Distance<OWLEntity> distance,
            Set<OWLEntity> clusteringSignature) throws OPPLException {
        OWLOntologyManager m = o.getOWLOntologyManager();
        // remove annotations
        clusterer = new ClusterCreator();
        Set<Cluster<OWLEntity>> clusters = runClustering(distance, clusteringSignature,
                m, clusterer);
        // put annotations back in
        // m.addAxioms(o, annotationAssertions);
        ClusterDecompositionModel<OWLEntity> model = clusterer
                .buildClusterDecompositionModel(o, clusters);
        return model;
    }

    public static ClusterDecompositionModel<OWLEntity> startSemanticClustering(
            OWLOntology o, Set<OWLAxiom> entailments, Distance<OWLEntity> distance,
            Set<OWLEntity> clusteringSignature) throws OPPLException {
        OWLOntologyManager m = o.getOWLOntologyManager();
        clusterer = new ClusterCreator();
        Set<Cluster<OWLEntity>> clusters = runClustering(distance, clusteringSignature,
                m, clusterer);
        ClusterDecompositionModel<OWLEntity> model = clusterer
                .buildKnowledgeExplorerClusterDecompositionModel(o, entailments, m,
                        clusters);
        return model;
    }

    private static Set<Cluster<OWLEntity>> runClustering(Distance<OWLEntity> distance,
            Set<OWLEntity> clusteringSignature, OWLOntologyManager m,
            ClusterCreator clusterCreator) {
        final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
            @Override
            public int compare(final OWLEntity o1, final OWLEntity o2) {
                return shortFormProvider.getShortForm(o1).compareTo(
                        shortFormProvider.getShortForm(o2));
            }
        });
        if (clusteringSignature == null || clusteringSignature.isEmpty()) {
            for (OWLOntology ontology : m.getOntologies()) {
                entities.addAll(ontology.getSignature());
            }
        } else {
            entities.addAll(clusteringSignature);
        }
        Set<Cluster<OWLEntity>> clusters = clusterCreator.agglomerateAll(distance,
                entities);
        return clusters;
    }

    // public static ClusterDecompositionModel<OWLEntity>
    // startSyntacticClustering(
    // OWLOntology o, Distance<OWLEntity> distance, Set<OWLEntity> entities)
    // throws OPPLException, ParserConfigurationException {
    //
    // ClusterCreator clusterer = new ClusterCreator();
    // Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(o,
    // distance, entities);
    // ClusterDecompositionModel<OWLEntity> model = clusterer
    // .buildClusterDecompositionModel(o, o.getOWLOntologyManager(),
    // clusters);
    // return model;
    // }
    public static Collection<? extends SimpleMetric<?>> getClusteringMetrics(
            ClusterDecompositionModel<OWLEntity> model) {
        ArrayList<SimpleMetric<?>> toReturn = new ArrayList<SimpleMetric<?>>();
        GeneralisationStatistics<Cluster<OWLEntity>, OWLEntity> genStats = GeneralisationStatistics
                .buildStatistics(model);
        int clusterNo = model.getClusterList().size();
        int numberOfGeneralisations = genStats.getNumberOfGeneralisations();
        int numberOfInstantiations = genStats.getNumberOfInstantiations();
        double meanEntitiesPerCluster = genStats.getMeanEntitiesPerCluster();
        double ratioOfGeneralisedAxioms = genStats
                .getRatioOfGeneralisedAxiomsToTotalAxioms();
        double meanInstantiationsPerGeneralisation = genStats
                .getMeanOWLAxiomInstantiationsPerGeneralisation();
        double meanClusterCoverage = genStats.getMeanClusterCoveragePerGeneralisation();
        toReturn.add(new SimpleMetric<Integer>("#Clusters", clusterNo));
        toReturn.add(new SimpleMetric<Integer>("#Generalisations",
                numberOfGeneralisations));
        toReturn.add(new SimpleMetric<Integer>("#Instantiations", numberOfInstantiations));
        toReturn.add(new SimpleMetric<Double>("MeanEntitiesPerCluster",
                meanEntitiesPerCluster));
        toReturn.add(new SimpleMetric<Double>("GeneralisedAxioms%",
                ratioOfGeneralisedAxioms));
        toReturn.add(new SimpleMetric<Double>("MeanInstantiationsPerGeneralisation",
                meanInstantiationsPerGeneralisation));
        toReturn.add(new SimpleMetric<Double>("MeanClusterCoverage", meanClusterCoverage));
        return toReturn;
    }

    public static Collection<? extends SimpleMetric<?>>
            getAtomicDecompositionGeneralisedMetrics(
                    GeneralisedAtomicDecomposition<OWLEntity> gad) {
        ArrayList<SimpleMetric<?>> toReturn = new ArrayList<SimpleMetric<?>>();
        // ad compression metrics
        GeneralisedAtomicDecompositionMetrics adstats = GeneralisedAtomicDecompositionMetrics
                .buildMetrics(gad);
        double adCompression = adstats.getAtomicDecompositionCompression();
        double meanMergedAxiomsPerGeneralisation = adstats
                .getMeanMergedAxiomsPerGeneralisation();
        double ratioOfMergedGeneralisations = adstats.getRatioOfMergedGeneralisations();
        toReturn.add(new SimpleMetric<Double>("ADCompression", adCompression));
        toReturn.add(new SimpleMetric<Double>("MeanMergedAxiomsPerGeneralisation",
                meanMergedAxiomsPerGeneralisation));
        toReturn.add(new SimpleMetric<Double>("ratioOfMergedGeneralisations",
                ratioOfMergedGeneralisations));
        return toReturn;
    }

    public static void printStats(PrintStream out, String s, int axiomCount,
            int logicalAxiomCount, int entitiesNo,
            ClusterDecompositionModel<OWLEntity> model,
            GeneralisedAtomicDecomposition<OWLEntity> gad) {
        // generalisation quality metrics
        GeneralisationStatistics<Cluster<OWLEntity>, OWLEntity> genStats = GeneralisationStatistics
                .buildStatistics(model);
        int clusterNo = model.getClusterList().size();
        int numberOfGeneralisations = genStats.getNumberOfGeneralisations();
        int numberOfInstantiations = genStats.getNumberOfInstantiations();
        double meanEntitiesPerCluster = genStats.getMeanEntitiesPerCluster();
        double ratioOfGeneralisedAxioms = genStats
                .getRatioOfGeneralisedAxiomsToTotalAxioms();
        double meanInstantiationsPerGeneralisation = genStats
                .getMeanOWLAxiomInstantiationsPerGeneralisation();
        double meanClusterCoverage = genStats.getMeanClusterCoveragePerGeneralisation();
        // ad compression metrics
        GeneralisedAtomicDecompositionMetrics adstats = GeneralisedAtomicDecompositionMetrics
                .buildMetrics(gad);
        double adCompression = adstats.getAtomicDecompositionCompression();
        double meanMergedAxiomsPerGeneralisation = adstats
                .getMeanMergedAxiomsPerGeneralisation();
        double ratioOfMergedGeneralisations = adstats.getRatioOfMergedGeneralisations();
        out.print(s + "," + axiomCount + "," + logicalAxiomCount + "," + entitiesNo + ","
                + clusterNo + "," + numberOfGeneralisations + ","
                + numberOfInstantiations + "," + meanEntitiesPerCluster + ","
                + ratioOfGeneralisedAxioms + "," + meanInstantiationsPerGeneralisation
                + "," + meanClusterCoverage + "," + adCompression + ","
                + meanMergedAxiomsPerGeneralisation + "," + ratioOfMergedGeneralisations
                + ",");
    }

    public static <P> void printClusteringStats(PrintStream out, File f,
            Collection<Cluster<P>> sortedClusters) {
        try {
            PrintStream indiout = new PrintStream(f);
            double totalAverageInternalDistance = 0;
            double totalAverageHomogeneity = 0;
            int totalAverageExternalDistance = 0;
            int totalAverageMaxInternalDistance = 0;
            int totalAverageMinInternalDistance = 0;
            int totalAverageMaxExternalDistance = 0;
            int totalAverageMinExternalDistance = 0;
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
            }
            double avgInternalDistanceFinal = totalAverageInternalDistance / clNo;
            totalAverageHomogeneity = 1 - avgInternalDistanceFinal;
            out.println(avgInternalDistanceFinal + ","
                    + (double) totalAverageExternalDistance / clNo + ","
                    + (double) totalAverageMaxInternalDistance / clNo + ","
                    + (double) totalAverageMinInternalDistance / clNo + ","
                    + (double) totalAverageMaxExternalDistance / clNo + ","
                    + (double) totalAverageMinExternalDistance / clNo + ","
                    + totalAverageHomogeneity);
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

    public static Set<OWLAnnotationAssertionAxiom> stripOntologyFromAnnotationAssertions(
            OWLOntology o) {
        Set<OWLAnnotationAssertionAxiom> set = new HashSet<OWLAnnotationAssertionAxiom>();
        OWLOntologyManager m = o.getOWLOntologyManager();
        for (OWLOntology onto : o.getImportsClosure()) {
            set.addAll(onto.getAxioms(AxiomType.ANNOTATION_ASSERTION));
            m.removeAxioms(onto, set);
        }
        return set;
    }

    public static ClusteringProximityMatrix<DistanceTableObject<OWLEntity>>
            getClusteringMatrix() {
        return clusterer.getClusteringMatrix();
    }

    public static ManchesterSyntaxRenderer setManchesterSyntaxWithLabelRendering(
            OWLOntologyManager manager) {
        OWLDataFactory dataFactory = manager.getOWLDataFactory();
        ManchesterSyntaxRenderer renderer = new ManchesterSyntaxRenderer();
        ShortFormProvider shortFormProvider = new AnnotationValueShortFormProvider(
                Arrays.asList(dataFactory.getRDFSLabel()),
                Collections.<OWLAnnotationProperty, List<String>> emptyMap(), manager);
        renderer.setShortFormProvider(shortFormProvider);
        return renderer;
    }
}
