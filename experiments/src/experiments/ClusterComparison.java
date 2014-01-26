package experiments;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;

/** @author eleni
 * @param <O>
 *            type */
public class ClusterComparison<O> {
    Comparator<Set<OWLEntity>> sizeComparator = new Comparator<Set<OWLEntity>>() {
        @Override
        public int compare(Set<OWLEntity> o1, Set<OWLEntity> o2) {
            return o1.size() - o2.size();
        }
    };

    /** @param cluster
     *            cluster
     * @param anotherCluster
     *            anotherCluster
     * @return intersection */
    public Set<O> getIntersection(Set<O> cluster, Set<O> anotherCluster) {
        Set<O> intersection = new HashSet<O>();
        intersection.addAll(cluster);
        intersection.retainAll(anotherCluster);
        return intersection;
    }

    /** @param cluster
     *            cluster
     * @param anotherCluster
     *            anotherCluster
     * @return similarity */
    public double getClusterSimilarity(Set<O> cluster, Set<O> anotherCluster) {
        if (cluster.size() == 0 && anotherCluster.size() == 0) {
            return 0;
        } else {
            Set<O> union = new HashSet<O>();
            Set<O> intersection = new HashSet<O>();
            union.addAll(cluster);
            union.addAll(anotherCluster);
            intersection.addAll(cluster);
            intersection.retainAll(anotherCluster);
            double toReturn = (double) intersection.size() / union.size();
            return toReturn;
        }
    }
}
