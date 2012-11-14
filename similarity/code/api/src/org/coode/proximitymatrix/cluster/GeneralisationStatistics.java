package org.coode.proximitymatrix.cluster;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.coode.oppl.Variable;
import org.coode.oppl.bindingtree.AssignmentMap;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

public class GeneralisationStatistics<P> {

	private final ClusterDecompositionModel<P> model;
	private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
	private final MultiMap<OWLAxiom, OWLAxiomInstantiation> genMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
	private final Map<Variable<?>, MultiMap<OWLObject, OWLAxiom>> breakdownsMap = new HashMap<Variable<?>, MultiMap<OWLObject, OWLAxiom>>();
	private final MultiMap<OWLObject, OWLAxiom> clusterAppearanceBreakDown = new MultiMap<OWLObject, OWLAxiom>();

	private final Map<OWLAxiom, Double> generalisationCoverageStats = new HashMap<OWLAxiom, Double>();

	private GeneralisationStatistics(ClusterDecompositionModel<P> model) {
		this.model = model;
		this.ontologies.addAll(this.model.getOntologies());
		extractGeneralisationMap();
	}

	private void extractGeneralisationMap() {
		List<Cluster<P>> clusterList = this.model.getClusterList();
		for (int i = 0; i < clusterList.size(); i++) {
			this.genMap.putAll(model.get(clusterList.get(i)));
		}
	}

	public int getNumberOfGeneralisations() {
		return genMap.keySet().size();
	}

	public int getNumberOfInstantiations() {
		return genMap.getAllValues().size();
	}

	public double getMeanClusterCoveragePerGeneralisation() {
		List<Cluster<P>> clusterList = this.model.getClusterList();
		Map<Variable<?>, Cluster<P>> variableMap = new HashMap<Variable<?>, Cluster<P>>();
		for (int i = 0; i < clusterList.size(); i++) {
			Cluster<P> cluster = clusterList.get(i);
			Set<OWLEntity> clusterSet = new HashSet<OWLEntity>();
			for (P e : cluster) {
				clusterSet.add((OWLEntity) e);
			}
			// Variable<?> variable = Utils.getVariable(clusterSet,
			// this.model.get(cluster));
			Variable<?> variable = this.model
					.getVariableRepresentative(cluster);
			if (variable != null) {
				variableMap.put(variable, cluster);
			}
		}

		double genCoverage = 0;
		for (OWLAxiom ax : genMap.keySet()) {
			Collection<OWLAxiomInstantiation> instantiations = genMap.get(ax);
			MultiMap<Variable<?>, OWLObject> map = new MultiMap<Variable<?>, OWLObject>();
			for (OWLAxiomInstantiation inst : instantiations) {
				AssignmentMap substitutions = inst.getSubstitutions();
				Set<Variable<?>> variables = substitutions.getVariables();
				for (Variable<?> v : variables) {
					map.putAll(v, substitutions.get(v));
				}
			}
			// double genCoverage = 0;
			for (Variable<?> v : map.keySet()) {
				double count = 0;
				if (v != null) {
					SimpleCluster<P> cluster = (SimpleCluster<P>) variableMap
							.get(v);
					if (cluster != null) {
						count = ((double) map.get(v).size()) / cluster.size();
					}
				}
				genCoverage += count / map.keySet().size();
			}
			// sumGenCoverage += genCoverage
		}
		return genCoverage / genMap.keySet().size();
	}

	private void buildBreakdownMap() {
		List<Cluster<P>> clusterList = this.model.getClusterList();
		for (int i = 0; i < clusterList.size(); i++) {
			SimpleCluster<OWLEntity> simplecluster = (SimpleCluster<OWLEntity>) clusterList
					.get(i);
			Iterator<OWLEntity> it = simplecluster.iterator();
			Set<OWLEntity> cluster = new HashSet<OWLEntity>();
			while (it.hasNext()) {
				cluster.add(it.next());
			}
			Variable<?> clusterVariable = Utils.getVariable(cluster, genMap);
			if (clusterVariable != null) {
				MultiMap<OWLObject, OWLAxiom> clusterAppearanceBreakdown = Utils
						.buildClusterAppearanceBreakdown(cluster, genMap);
				this.breakdownsMap.put(clusterVariable,
						clusterAppearanceBreakdown);
			}
		}
	}

	public double getRatioOfGeneralisedAxiomsToTotalAxioms() {
		int osize = 0;
		for (OWLOntology o : ontologies) {
			osize += o.getAxiomCount();
		}
		return (double) genMap.getAllValues().size() / osize;
	}

	public double getMeanOWLAxiomInstantiationsPerGeneralisation() {
		return (double) this.genMap.getAllValues().size()
				/ this.genMap.keySet().size();
	}

	public double getMeanEntitiesPerCluster() {
		List<Cluster<P>> clusterList = this.model.getClusterList();
		int size = 0;
		for (int i = 0; i < clusterList.size(); i++) {
			size += clusterList.get(i).size();
		}
		return (double) size / clusterList.size();
	}

	public void buildDescriptiveStatistics() {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		// stats.a
	}

	public static <P extends OWLEntity> GeneralisationStatistics<P> buildStatistics(
			ClusterDecompositionModel<P> model) {
		return new GeneralisationStatistics<P>(model);
	}
}
