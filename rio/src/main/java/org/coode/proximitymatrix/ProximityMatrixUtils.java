package org.coode.proximitymatrix;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.coode.distance.SparseMatrix;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterStatistics;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

/** @author eleni */
public class ProximityMatrixUtils {
    /**
     * @param out out
     * @param data data
     * @param distanceMatrix distanceMatrix
     */
    public static void printDistanceMatrix(PrintWriter out, SparseMatrix data,
        ProximityMatrix<OWLEntity> distanceMatrix) {
        out.println("Distance matrix");
        List<OWLEntity> objects = new ArrayList<>(distanceMatrix.getObjects());
        Iterator<OWLEntity> it = objects.iterator();
        while (it.hasNext()) {
            OWLEntity owlEntity = it.next();
            out.print(renderOWLEntity(owlEntity));
            if (it.hasNext()) {
                out.print("\t");
            }
        }
        int i = 0;
        for (int row = 0; row < data.length(); row++) {
            out.print(renderOWLEntity(objects.get(i)));
            data.printLine(row, out);
            out.println();
            i++;
        }
    }

    /**
     * @param clusters clusters
     * @param out out
     */
    public static void printClusters(Set<Cluster<OWLEntity>> clusters, PrintWriter out) {
        MathContext mathContext = new MathContext(2);
        out.println("Clusters:");
        for (Cluster<OWLEntity> cluster : clusters) {
            ClusterStatistics<OWLEntity> stats = ClusterStatistics.buildStatistics(cluster);
            out.println(String.format("%s\t%s\t%s\t%s", renderCluster(stats.getCluster()),
                new BigDecimal(stats.getAverageInternalDistance(), mathContext),
                new BigDecimal(stats.getMinInternalDistance(), mathContext),
                new BigDecimal(stats.getMaxInternalDistance(), mathContext)));
        }
    }

    /**
     * @param data data
     * @param clusters clusters
     * @param out out
     */
    public static void printProximityMatrix(SparseMatrix data, Set<Cluster<OWLEntity>> clusters,
        PrintWriter out) {
        out.println("Proximity Matrix");
        Iterator<Cluster<OWLEntity>> iterator = clusters.iterator();
        while (iterator.hasNext()) {
            Cluster<OWLEntity> cluster = iterator.next();
            out.print(renderCluster(cluster));
            if (iterator.hasNext()) {
                out.print("\t");
            }
        }
        out.println();
        List<Cluster<OWLEntity>> list = new ArrayList<>(clusters);
        int i = 0;
        for (int row = 0; row < data.length(); row++) {
            out.print(renderCluster(list.get(i)));
            data.printLine(row, out);
            out.println();
            i++;
        }
    }

    /**
     * @param owlEntity owlEntity
     * @return rendering
     */
    private static String renderOWLEntity(OWLEntity owlEntity) {
        SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        return shortFormProvider.getShortForm(owlEntity);
    }

    /**
     * @param cluster cluster
     * @return rendering
     */
    private static String renderCluster(Cluster<OWLEntity> cluster) {
        SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        Set<String> toReturn = new HashSet<>(cluster.size());
        for (OWLEntity owlEntity : cluster) {
            toReturn.add(shortFormProvider.getShortForm(owlEntity));
        }
        return toReturn.toString();
    }
}
