package org.coode.proximitymatrix.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.oppl.Variable;
import org.coode.oppl.bindingtree.AssignmentMap;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.proximitymatrix.ui.ClusterStatisticsTableModel;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni
 * @param <P>
 *            type */
public class ClusterDecompositionModel<P> implements
        RegularitiesDecompositionModel<Cluster<P>, P> {
    Map<Cluster<P>, MultiMap<OWLAxiom, OWLAxiomInstantiation>> fullGeneralisationMap = new LinkedHashMap<Cluster<P>, MultiMap<OWLAxiom, OWLAxiomInstantiation>>();
    Map<Cluster<P>, Variable<?>> variableMap = new HashMap<Cluster<P>, Variable<?>>();
    List<Cluster<P>> sortedClusters;
    private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();

    /** @param _clusters
     *            _clusters
     * @param ontologies
     *            ontologies */
    public ClusterDecompositionModel(Collection<? extends Cluster<P>> _clusters,
            Collection<? extends OWLOntology> ontologies) {
        sortedClusters = new ArrayList<Cluster<P>>(_clusters.size());
        for (Cluster<P> c : _clusters) {
            if (c.size() > 1) {
                sortedClusters.add(c);
            }
        }
        Collections.sort(sortedClusters, ClusterStatisticsTableModel.SIZE_COMPARATOR);
        this.ontologies.addAll(ontologies);
    }

    /** @param _clusters
     *            _clusters */
    public ClusterDecompositionModel(Collection<? extends Cluster<P>> _clusters) {
        sortedClusters = new ArrayList<Cluster<P>>(_clusters.size());
        for (Cluster<P> c : _clusters) {
            if (c.size() > 1) {
                sortedClusters.add(c);
            }
        }
        Collections.sort(sortedClusters, ClusterStatisticsTableModel.SIZE_COMPARATOR);
    }

    @Override
    public void put(Cluster<P> cluster, MultiMap<OWLAxiom, OWLAxiomInstantiation> map) {
        fullGeneralisationMap.put(cluster, map);
    }

    @Override
    public List<Cluster<P>> getClusterList() {
        return new ArrayList<Cluster<P>>(sortedClusters);
    }

    @Override
    public MultiMap<OWLAxiom, OWLAxiomInstantiation> get(Cluster<P> c) {
        return fullGeneralisationMap.get(c);
    }

    @Override
    public Set<OWLOntology> getOntologies() {
        return ontologies;
    }

    @Override
    public Variable<?> getVariableRepresentative(Cluster<P> c) {
        if (variableMap == null || variableMap.isEmpty()) {
            buildVariableMap();
        }
        return variableMap.get(c);
    }

    private void buildVariableMap() {
        for (Cluster<P> cluster : sortedClusters) {
            MultiMap<OWLAxiom, OWLAxiomInstantiation> multiMap = fullGeneralisationMap
                    .get(cluster);
            OWLAxiom exampleLogicGeneralisation = getExampleLogicGeneralisation(multiMap);
            if (exampleLogicGeneralisation != null) {
                Collection<OWLAxiomInstantiation> collection = multiMap
                        .get(exampleLogicGeneralisation);
                OWLAxiomInstantiation exampleInst = collection.iterator().next();
                AssignmentMap substitutions = exampleInst.getSubstitutions();
                for (Variable<?> var : substitutions.getVariables()) {
                    if (cluster.containsAll(substitutions.get(var))) {
                        variableMap.put(cluster, var);
                    }
                }
            }
        }
    }

    private OWLAxiom getExampleLogicGeneralisation(
            MultiMap<OWLAxiom, OWLAxiomInstantiation> multiMap) {
        if (multiMap != null) {
            for (OWLAxiom ax : multiMap.keySet()) {
                if (ax.isLogicalAxiom()) {
                    return ax;
                }
            }
        }
        return null;
    }

    @Override
    public MultiMap<OWLAxiom, OWLAxiomInstantiation> getGeneralisationMap() {
        MultiMap<OWLAxiom, OWLAxiomInstantiation> map = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
        for (Cluster<P> c : fullGeneralisationMap.keySet()) {
            map.putAll(fullGeneralisationMap.get(c));
        }
        return map;
    }
}