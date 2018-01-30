package org.coode.proximitymatrix.cluster;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.oppl.Variable;
import org.coode.oppl.bindingtree.AssignmentMap;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
public class LexicalClusterModel
    implements RegularitiesDecompositionModel<Set<OWLEntity>, OWLEntity> {
    private final OWLOntology onto;
    private final MultiMap<String, OWLEntity> lexicalClustersMap = new MultiMap<>();
    private final Map<String, MultiMap<OWLAxiom, OWLAxiomInstantiation>> lexicalGeneralisationMap =
        new HashMap<>();

    /**
     * @param clusterMap clusterMap
     * @param ontology ontology
     */
    public LexicalClusterModel(MultiMap<String, OWLEntity> clusterMap, OWLOntology ontology) {
        lexicalClustersMap.putAll(clusterMap);
        onto = ontology;
    }

    /**
     * @param keyword keyword
     * @param map map
     */
    public void put(String keyword, MultiMap<OWLAxiom, OWLAxiomInstantiation> map) {
        lexicalGeneralisationMap.put(keyword, map);
    }

    @Override
    public void put(Set<OWLEntity> cluster, MultiMap<OWLAxiom, OWLAxiomInstantiation> map) {
        for (String s : lexicalClustersMap.keySet()) {
            if (lexicalClustersMap.get(s).containsAll(cluster)) {
                lexicalGeneralisationMap.put(s, map);
            }
        }
    }

    @Override
    public List<Set<OWLEntity>> getClusterList() {
        List<Set<OWLEntity>> toReturn = new ArrayList<>();
        for (String s : lexicalClustersMap.keySet()) {
            toReturn.add((Set<OWLEntity>) lexicalClustersMap.get(s));
        }
        return toReturn;
    }

    /**
     * @param keyword keyword
     * @return lexical generalisation map
     */
    public MultiMap<OWLAxiom, OWLAxiomInstantiation> get(String keyword) {
        return lexicalGeneralisationMap.get(keyword);
    }

    @Override
    public MultiMap<OWLAxiom, OWLAxiomInstantiation> get(Set<OWLEntity> c) {
        for (String s : lexicalClustersMap.keySet()) {
            if (lexicalClustersMap.get(s).containsAll(c)) {
                return lexicalGeneralisationMap.get(s);
            }
        }
        return new MultiMap<>();
    }

    @Override
    public List<OWLOntology> getOntologies() {
        return asList(onto.importsClosure());
    }

    @Override
    public Variable<?> getVariableRepresentative(Set<OWLEntity> c) {
        Set<String> keySet = lexicalClustersMap.keySet();
        MultiMap<OWLAxiom, OWLAxiomInstantiation> multiMap = new MultiMap<>();
        for (String keyword : keySet) {
            if (lexicalClustersMap.get(keyword).containsAll(c)) {
                multiMap = lexicalGeneralisationMap.get(keyword);
            }
        }
        OWLAxiomInstantiation inst = multiMap.getAllValues().iterator().next();
        AssignmentMap substitutions = inst.getSubstitutions();
        return substitutions.variables().filter(var -> c.containsAll(substitutions.get(var)))
            .findAny().orElse(null);
    }

    @Override
    public MultiMap<OWLAxiom, OWLAxiomInstantiation> getGeneralisationMap() {
        MultiMap<OWLAxiom, OWLAxiomInstantiation> toReturn = new MultiMap<>();
        return toReturn;
    }

    /**
     * @return the map that has lexical patterns as keys and entities from the ontology as
     */
    public MultiMap<String, OWLEntity> getLexicalPatternBasedClusters() {
        return lexicalClustersMap;
    }

    @Override
    public String toString() {
        org.coode.utils.owl.ManchesterSyntaxRenderer renderer =
            Utils.enableLabelRendering(onto.getOWLOntologyManager());
        StringBuilder sb = new StringBuilder();
        Set<String> keySet = lexicalClustersMap.keySet();
        for (String s : keySet) {
            sb.append("Lexical pattern " + s + "\n");
            sb.append("Cluster size: " + lexicalClustersMap.get(s).size() + "\n" + "[");
            for (OWLEntity o : lexicalClustersMap.get(s)) {
                sb.append(renderer.render(o) + ",");
            }
            sb.append("]" + "\n");
            sb.append("Generalisations: \n");
            MultiMap<OWLAxiom, OWLAxiomInstantiation> multiMap = lexicalGeneralisationMap.get(s);
            for (OWLAxiom ax : multiMap.keySet()) {
                sb.append(renderer.render(ax) + "\n");
                sb.append("\t Instantiations: " + "(" + multiMap.get(ax).size() + ") \n");
                for (OWLAxiomInstantiation inst : multiMap.get(ax)) {
                    sb.append("\t" + inst + " \n");
                }
            }
        }
        return sb.toString();
    }
}
