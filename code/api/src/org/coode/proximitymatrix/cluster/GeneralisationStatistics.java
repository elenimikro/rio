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
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

import experiments.SimpleMetric;

public class GeneralisationStatistics<C extends Set<P>, P> {

	private final RegularitiesDecompositionModel<C, P> model;
	private final Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
	private final MultiMap<OWLAxiom, OWLAxiomInstantiation> genMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();

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

	public int getNumberOfGeneralisations() {
		return genMap.keySet().size();
	}

	public int getNumberOfInstantiations() {
		return genMap.getAllValues().size();
	}

	public double getMeanClusterCoveragePerGeneralisation() {
		List<C> clusterList = this.model.getClusterList();
		Map<Variable<?>, C> variableMap = new HashMap<Variable<?>, C>();
		for (int i = 0; i < clusterList.size(); i++) {
			C cluster = clusterList.get(i);
			Set<OWLEntity> clusterSet = new HashSet<OWLEntity>();
			for (P e : cluster) {
				clusterSet.add((OWLEntity) e);
			}
			Variable<?> variable = Utils.getVariable(clusterSet,
					this.model.get(cluster));
			// Variable<?> variable = this.model
			// .getVariableRepresentative(cluster);
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
					C cluster = variableMap.get(v);
					if (cluster != null) {
						count = (double) map.get(v).size() / cluster.size();
					}
				}
				genCoverage += count / map.keySet().size();
			}
			// sumGenCoverage += genCoverage
		}
		return genCoverage / genMap.keySet().size();
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
		List<C> clusterList = this.model.getClusterList();
		int size = 0;
		for (int i = 0; i < clusterList.size(); i++) {
			size += clusterList.get(i).size();
		}
		return (double) size / clusterList.size();
	}

	public void buildDescriptiveStatistics() {
		// DescriptiveStatistics stats = new DescriptiveStatistics();
		// stats.a
	}

	public List<SimpleMetric<?>> getStats() {
		List<SimpleMetric<?>> stats = new ArrayList<SimpleMetric<?>>();
		stats.add(new SimpleMetric<Integer>("# Generalisations", this
				.getNumberOfGeneralisations()));
		stats.add(new SimpleMetric<Integer>("# Instantiations", this
				.getNumberOfInstantiations()));
		stats.add(new SimpleMetric<Double>("# Generalised Axioms", this
				.getRatioOfGeneralisedAxiomsToTotalAxioms()));
		stats.add(new SimpleMetric<Double>(
				"Mean instantiations per generalisation", this
						.getMeanOWLAxiomInstantiationsPerGeneralisation()));
		stats.add(new SimpleMetric<Double>("Mean entities per cluster", this
				.getMeanEntitiesPerCluster()));
		stats.add(new SimpleMetric<Double>("Mean cluster coverage", this
				.getMeanClusterCoveragePerGeneralisation()));

		return stats;
	}

	public static <C extends Set<P>, P extends OWLEntity> GeneralisationStatistics<C, P> buildStatistics(
			RegularitiesDecompositionModel<C, P> model) {
		return new GeneralisationStatistics<C, P>(model);
	}
}