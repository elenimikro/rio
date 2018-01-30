package org.coode.proximitymatrix.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.oppl.Variable;
import org.coode.oppl.bindingtree.AssignmentMap;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.utils.SimpleMetric;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

/**
 * @author eleni
 * @param <C> set
 * @param <P> type
 */
public class GeneralisationStatistics<C extends Set<P>, P> {
    private final RegularitiesDecompositionModel<C, P> model;
    private final Set<OWLOntology> ontologies = new HashSet<>();
    private final MultiMap<OWLAxiom, OWLAxiomInstantiation> genMap = new MultiMap<>();

    private GeneralisationStatistics(RegularitiesDecompositionModel<C, P> model) {
        this.model = model;
        this.ontologies.addAll(this.model.getOntologies());
        extractGeneralisationMap();
    }

    private void extractGeneralisationMap() {
        List<C> clusterList = this.model.getClusterList();
        for (int i = 0; i < clusterList.size(); i++) {
            this.genMap.putAll(model.get(clusterList.get(i)));
        }
    }

    /** @return number of generalisations */
    public int getNumberOfGeneralisations() {
        return genMap.keySet().size();
    }

    /** @return number of instantiations */
    public int getNumberOfInstantiations() {
        return genMap.getAllValues().size();
    }

    /** @return mean cluster coverage */
    public double getMeanClusterCoveragePerGeneralisation() {
        List<C> clusterList = this.model.getClusterList();
        Map<Variable<?>, C> variableMap = new HashMap<>();
        for (int i = 0; i < clusterList.size(); i++) {
            C cluster = clusterList.get(i);
            Set<OWLEntity> clusterSet = new HashSet<>();
            for (P e : cluster) {
                clusterSet.add((OWLEntity) e);
            }
            Variable<?> variable = Utils.getVariable(clusterSet, this.model.get(cluster));
            if (variable != null) {
                variableMap.put(variable, cluster);
            }
        }
        double genCoverage = 0;
        for (OWLAxiom ax : genMap.keySet()) {
            Collection<OWLAxiomInstantiation> instantiations = genMap.get(ax);
            MultiMap<Variable<?>, OWLObject> map = new MultiMap<>();
            for (OWLAxiomInstantiation inst : instantiations) {
                AssignmentMap substitutions = inst.getSubstitutions();
                substitutions.variables().forEach(v -> map.putAll(v, substitutions.get(v)));
            }
            for (Variable<?> v : map.keySet()) {
                double count = 0;
                if (v != null) {
                    C cluster = variableMap.get(v);
                    if (cluster != null) {
                        count = (double) map.get(v).size() / cluster.size();
                    }
                }
                genCoverage += count / map.keySet().size();
            }
        }
        if (genMap.keySet().isEmpty()) {
            throw new IllegalStateException("The map cannot be empty at this point");
        }
        return genCoverage / genMap.keySet().size();
    }

    /** @return ratio of generalised to total axioms */
    public double getRatioOfGeneralisedAxiomsToTotalAxioms() {
        int osize = 0;
        for (OWLOntology o : ontologies) {
            osize += o.getAxiomCount();
        }
        return (double) genMap.getAllValues().size() / osize;
    }

    /** @return mean instantiations */
    public double getMeanOWLAxiomInstantiationsPerGeneralisation() {
        return (double) this.genMap.getAllValues().size() / this.genMap.keySet().size();
    }

    /** @return mean entities per cluster */
    public double getMeanEntitiesPerCluster() {
        List<C> clusterList = this.model.getClusterList();
        int size = 0;
        for (int i = 0; i < clusterList.size(); i++) {
            size += clusterList.get(i).size();
        }
        return (double) size / clusterList.size();
    }

    /** build descriptive statistics */
    public void buildDescriptiveStatistics() {}

    /** @return stats */
    public List<SimpleMetric<?>> getStats() {
        List<SimpleMetric<?>> stats = new ArrayList<>();
        stats.add(new SimpleMetric<>("# Generalisations",
            Integer.valueOf(this.getNumberOfGeneralisations())));
        stats.add(new SimpleMetric<>("# Instantiations",
            Integer.valueOf(this.getNumberOfInstantiations())));
        stats.add(new SimpleMetric<>("# Generalised Axioms",
            Double.valueOf(this.getRatioOfGeneralisedAxiomsToTotalAxioms())));
        stats.add(new SimpleMetric<>("Mean instantiations per generalisation",
            Double.valueOf(this.getMeanOWLAxiomInstantiationsPerGeneralisation())));
        stats.add(new SimpleMetric<>("Mean entities per cluster",
            Double.valueOf(this.getMeanEntitiesPerCluster())));
        stats.add(new SimpleMetric<>("Mean cluster coverage",
            Double.valueOf(this.getMeanClusterCoveragePerGeneralisation())));
        return stats;
    }

    /**
     * @param model model
     * @param <P> type
     * @param <C> set of type
     * @return generalisation stats
     */
    public static <C extends Set<P>, P extends OWLEntity> GeneralisationStatistics<C, P> buildStatistics(
        RegularitiesDecompositionModel<C, P> model) {
        return new GeneralisationStatistics<>(model);
    }
}
