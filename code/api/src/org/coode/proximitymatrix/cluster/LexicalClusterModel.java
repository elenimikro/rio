package org.coode.proximitymatrix.cluster;

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

public class LexicalClusterModel implements
		RegularitiesDecompositionModel<Set<OWLEntity>, OWLEntity> {

	private final OWLOntology onto;
	private final MultiMap<String, OWLEntity> lexicalClustersMap = new MultiMap<String, OWLEntity>();
	private final Map<String, MultiMap<OWLAxiom, OWLAxiomInstantiation>> lexicalGeneralisationMap = new HashMap<String, MultiMap<OWLAxiom, OWLAxiomInstantiation>>();

	public LexicalClusterModel(MultiMap<String, OWLEntity> clusterMap,
			OWLOntology ontology) {
		this.lexicalClustersMap.putAll(clusterMap);
		this.onto = ontology;
	}

	public void put(String keyword,
			MultiMap<OWLAxiom, OWLAxiomInstantiation> map) {
		this.lexicalGeneralisationMap.put(keyword, map);
	}

	@Override
	public void put(Set<OWLEntity> cluster,
			MultiMap<OWLAxiom, OWLAxiomInstantiation> map) {
		for (String s : lexicalClustersMap.keySet()) {
			if (lexicalClustersMap.get(s).containsAll(cluster)) {
				lexicalGeneralisationMap.put(s, map);
			}
		}
	}

	@Override
	public List<Set<OWLEntity>> getClusterList() {
		List<Set<OWLEntity>> toReturn = new ArrayList<Set<OWLEntity>>();
		for (String s : lexicalClustersMap.keySet()) {
			toReturn.add((Set<OWLEntity>) lexicalClustersMap.get(s));
		}
		return toReturn;
	}

	public MultiMap<OWLAxiom, OWLAxiomInstantiation> get(String keyword) {
		return this.lexicalGeneralisationMap.get(keyword);
	}

	@Override
	public MultiMap<OWLAxiom, OWLAxiomInstantiation> get(Set<OWLEntity> c) {
		for (String s : lexicalClustersMap.keySet()) {
			if (lexicalClustersMap.get(s).containsAll(c)) {
				return lexicalGeneralisationMap.get(s);
			}
		}
		return new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
	}

	@Override
	public Set<OWLOntology> getOntologies() {
		return onto.getImportsClosure();
	}

	@Override
	public Variable<?> getVariableRepresentative(Set<OWLEntity> c) {
		Set<String> keySet = this.lexicalClustersMap.keySet();
		MultiMap<OWLAxiom, OWLAxiomInstantiation> multiMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
		for (String keyword : keySet) {
			if (lexicalClustersMap.get(keyword).containsAll(c)) {
				multiMap = this.lexicalGeneralisationMap.get(keyword);
			}
		}
		OWLAxiomInstantiation inst = multiMap.getAllValues().iterator().next();
		AssignmentMap substitutions = inst.getSubstitutions();
		for (Variable<?> v : substitutions.getVariables()) {
			if (c.containsAll(substitutions.get(v))) {
				return v;
			}
		}
		return null;
	}

	@Override
	public MultiMap<OWLAxiom, OWLAxiomInstantiation> getGeneralisationMap() {
		MultiMap<OWLAxiom, OWLAxiomInstantiation> toReturn = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
		return toReturn;
	}

	/**
	 * @return the map that has lexical patterns as keys and entities from the
	 *         ontology as
	 */
	public MultiMap<String, OWLEntity> getLexicalPatternBasedClusters() {
		return lexicalClustersMap;
	}

	@Override
	public String toString() {
		org.coode.utils.owl.ManchesterSyntaxRenderer renderer = Utils
				.enableLabelRendering(onto.getOWLOntologyManager());
		StringBuilder sb = new StringBuilder();
		Set<String> keySet = this.lexicalClustersMap.keySet();
		for (String s : keySet) {
			sb.append("Lexical pattern " + s + "\n");
			sb.append("Cluster size: " + lexicalClustersMap.get(s).size()
					+ "\n" + "[");
			for (OWLEntity o : lexicalClustersMap.get(s)) {
				sb.append(renderer.render(o) + ",");
			}
			sb.append("]" + "\n");
			sb.append("Generalisations: \n");
			MultiMap<OWLAxiom, OWLAxiomInstantiation> multiMap = this.lexicalGeneralisationMap
					.get(s);
			for (OWLAxiom ax : multiMap.keySet()) {
				sb.append(renderer.render(ax) + "\n");
				sb.append("\t Instantiations: " + "(" + multiMap.get(ax).size()
						+ ") \n");
				for (OWLAxiomInstantiation inst : multiMap.get(ax)) {
					sb.append("\t" + inst + " \n");
				}
			}
		}
		return sb.toString();
	}
}
