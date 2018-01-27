package org.coode.proximitymatrix.cluster;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.oppl.Variable;
import org.coode.oppl.bindingtree.AssignmentMap;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.util.MultiMap;

/**
 * Convenience class for analysing regularities that were already saved in xml file. The difference
 * from the ClusterDecompositionModel is that instead of having Cluster is using Set.
 * 
 * @author elenimikroyannidi
 * @param <P> type
 */
public class GeneralisationDecompositionModel<P extends OWLEntity>
    implements RegularitiesDecompositionModel<Set<P>, P> {
    private final Map<Set<P>, MultiMap<OWLAxiom, OWLAxiomInstantiation>> fullGeneralisationMap =
        new LinkedHashMap<>();
    protected static MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<>();
    private final MultiMap<OWLAxiom, OWLAxiomInstantiation> sortedGeneralisationMap =
        new MultiMap<>();
    Map<Set<P>, Variable<?>> variableMap = new HashMap<>();
    List<Set<P>> sortedClusters;
    private final OWLOntology ontology;
    private static final Comparator<Set<?>> CLUSTER_SIZE_COMPARATOR =
        Collections.reverseOrder((cluster, anothercluster) -> {
            // return cluster.size() - anothercluster.size();
            int sizeDifference = cluster.size() - anothercluster.size();
            return sizeDifference == 0 ? cluster.hashCode() - anothercluster.hashCode()
                : sizeDifference;
        });
    private static final Comparator<OWLAxiom> GENERALISATIONS_SIZE_COMPARATOR =
        (axiom, otherAxiom) -> {
            Collection<OWLAxiomInstantiation> axiomInstatiations = generalisationMap.get(axiom);
            Collection<OWLAxiomInstantiation> otherAxiomInstantiations =
                generalisationMap.get(otherAxiom);
            return axiomInstatiations.size() - otherAxiomInstantiations.size();
        };
    private ConstraintSystem constraintSystem;

    /**
     * @param _clusters _clusters
     * @param ontology ontology
     * @throws UnknownOWLOntologyException UnknownOWLOntologyException
     */
    public GeneralisationDecompositionModel(Collection<? extends Set<P>> _clusters,
        OWLOntology ontology) throws UnknownOWLOntologyException {
        sortedClusters = new ArrayList<>(_clusters.size());
        for (Set<P> c : _clusters) {
            if (c.size() > 1) {
                sortedClusters.add(c);
            }
        }
        Collections.sort(sortedClusters, CLUSTER_SIZE_COMPARATOR);
        this.ontology = ontology;
    }

    private void buildGeneralisationMap(OWLOntology ont) {
        try {
            OPPLFactory opplFactory = new OPPLFactory(ont.getOWLOntologyManager(), ont, null);
            constraintSystem = opplFactory.createConstraintSystem();
            List<OWLOntology> closure = asList(ont.importsClosure());
            OWLObjectGeneralisation generalisation =
                Utils.getOWLObjectGeneralisation(sortedClusters, closure, constraintSystem);
            for (Set<P> cluster : sortedClusters) {
                MultiMap<OWLAxiom, OWLAxiomInstantiation> map = Utils.buildGeneralisationMap(
                    (Collection<OWLEntity>) cluster, closure, generalisation);
                fullGeneralisationMap.put(cluster, map);
                generalisationMap.putAll(map);
            }
            sortGeneralisations();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sortGeneralisations() {
        Set<OWLAxiom> orderedGenSet =
            new TreeSet<>(Collections.reverseOrder(GENERALISATIONS_SIZE_COMPARATOR));
        orderedGenSet.addAll(generalisationMap.keySet());
        for (OWLAxiom ax : orderedGenSet) {
            sortedGeneralisationMap.putAll(ax, generalisationMap.get(ax));
        }
    }

    @Override
    public void put(Set<P> cluster, MultiMap<OWLAxiom, OWLAxiomInstantiation> map) {
        fullGeneralisationMap.put(cluster, map);
    }

    /**
     * @param map map
     */
    public void putAll(MultiMap<OWLAxiom, OWLAxiomInstantiation> map) {
        generalisationMap.putAll(map);
    }

    @Override
    public List<Set<P>> getClusterList() {
        return new ArrayList<>(sortedClusters);
    }

    @Override
    public MultiMap<OWLAxiom, OWLAxiomInstantiation> get(Set<P> c) {
        if (fullGeneralisationMap.size() < 2) {
            buildGeneralisationMap(ontology);
        }
        return fullGeneralisationMap.get(c);
    }

    /** @return ontology */
    public OWLOntology getOntology() {
        return ontology;
    }

    @Override
    public Variable<?> getVariableRepresentative(Set<P> c) {
        if (variableMap == null || variableMap.isEmpty()) {
            buildVariableMap();
        }
        return variableMap.get(c);
    }

    private void buildVariableMap() {
        for (Set<P> cluster : sortedClusters) {
            MultiMap<OWLAxiom, OWLAxiomInstantiation> multiMap = fullGeneralisationMap.get(cluster);
            OWLAxiom exampleLogicGeneralisation = getExampleLogicGeneralisation(multiMap);
            if (exampleLogicGeneralisation != null) {
                Collection<OWLAxiomInstantiation> collection =
                    multiMap.get(exampleLogicGeneralisation);
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

    private static OWLAxiom getExampleLogicGeneralisation(
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
        // if (sortedGeneralisationMap.size() < 2) {
        buildGeneralisationMap(ontology);
        // }
        return sortedGeneralisationMap;
    }

    /** @return constraint system */
    public ConstraintSystem getConstraintSystem() {
        return constraintSystem;
    }

    /**
     * @param constraintSystem constraintSystem
     */
    public void setConstraintSystem(ConstraintSystem constraintSystem) {
        this.constraintSystem = constraintSystem;
    }

    @Override
    public List<OWLOntology> getOntologies() {
        return asList(ontology.importsClosure());
    }

    /**
     * @param map map
     */
    public void setGeneralisationMap(MultiMap<OWLAxiom, OWLAxiomInstantiation> map) {
        GeneralisationDecompositionModel.generalisationMap.clear();
        this.sortedGeneralisationMap.clear();
        generalisationMap.putAll(map);
        sortGeneralisations();
    }
}
