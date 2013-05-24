package org.coode.proximitymatrix.cluster;

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
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
import org.coode.oppl.exceptions.RuntimeExceptionHandler;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.util.MultiMap;

/**
 * @author elenimikroyannidi
 * 
 *         Convenience class for analysing regularities that were already saved
 *         in xml file. The difference from the ClusterDecompositionModel is
 *         that instead of having Cluster
 *         <P>
 *         is using Set
 *         <P>
 *         .
 * 
 * @param <P>
 */
public class GeneralisationDecompositionModel<P extends OWLEntity> implements
		RegularitiesDecompositionModel<Set<P>, P> {
	private final Map<Set<P>, MultiMap<OWLAxiom, OWLAxiomInstantiation>> fullGeneralisationMap = new LinkedHashMap<Set<P>, MultiMap<OWLAxiom, OWLAxiomInstantiation>>();

	private static MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
	private final MultiMap<OWLAxiom, OWLAxiomInstantiation> sortedGeneralisationMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();

	Map<Set<P>, Variable<?>> variableMap = new HashMap<Set<P>, Variable<?>>();
	List<Set<P>> sortedClusters;
	private final OWLOntology ontology;

	private static final Comparator<Set<?>> CLUSTER_SIZE_COMPARATOR = Collections
			.reverseOrder(new Comparator<Set<?>>() {
				@Override
				public int compare(Set<?> cluster, Set<?> anothercluster) {
					// return cluster.size() - anothercluster.size();
					int sizeDifference = cluster.size() - anothercluster.size();
					return sizeDifference == 0 ? cluster.hashCode()
							- anothercluster.hashCode() : sizeDifference;
				}
			});

	private static final Comparator<OWLAxiom> GENERALISATIONS_SIZE_COMPARATOR = new Comparator<OWLAxiom>() {

		@Override
		public int compare(OWLAxiom axiom, OWLAxiom otherAxiom) {
			Collection<OWLAxiomInstantiation> axiomInstatiations = generalisationMap
					.get(axiom);
			Collection<OWLAxiomInstantiation> otherAxiomInstantiations = generalisationMap
					.get(otherAxiom);
			return axiomInstatiations.size() - otherAxiomInstantiations.size();
		}
	};
	private ConstraintSystem constraintSystem;

	public GeneralisationDecompositionModel(
			Collection<? extends Set<P>> _clusters, OWLOntology ontology)
			throws UnknownOWLOntologyException, OPPLException {
		sortedClusters = new ArrayList<Set<P>>(_clusters.size());
		for (Set<P> c : _clusters) {
			if (c.size() > 1) {
				sortedClusters.add(c);
			}
		}
		Collections.sort(sortedClusters, CLUSTER_SIZE_COMPARATOR);
		this.ontology = ontology;

	}

	private void buildGeneralisationMap(OWLOntology ontology) {
		try {
			OPPLFactory opplFactory = new OPPLFactory(
					ontology.getOWLOntologyManager(), ontology, null);
			constraintSystem = opplFactory.createConstraintSystem();
			OWLObjectGeneralisation generalisation = Utils
					.getOWLObjectGeneralisation(sortedClusters,
							ontology.getImportsClosure(), constraintSystem);
			RuntimeExceptionHandler runtimeExceptionHandler = new QuickFailRuntimeExceptionHandler();
			// MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new
			// MultiMap<OWLAxiom, OWLAxiomInstantiation>();
			for (Set<P> cluster : sortedClusters) {
				MultiMap<OWLAxiom, OWLAxiomInstantiation> map = Utils
						.buildGeneralisationMap(cluster,
								ontology.getImportsClosure(), generalisation,
								runtimeExceptionHandler);
				// Utils.pruneGeneralisationMap(constraintSystem,
				// runtimeExceptionHandler, map);
				fullGeneralisationMap.put(cluster, map);
				generalisationMap.putAll(map);
			}
			sortGeneralisations();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void sortGeneralisations() {
		Set<OWLAxiom> orderedGenSet = new TreeSet<OWLAxiom>(
				Collections.reverseOrder(GENERALISATIONS_SIZE_COMPARATOR));
		orderedGenSet.addAll(generalisationMap.keySet());
		for (OWLAxiom ax : orderedGenSet) {
			sortedGeneralisationMap.putAll(ax, generalisationMap.get(ax));
		}
	}

	@Override
	public void put(Set<P> cluster,
			MultiMap<OWLAxiom, OWLAxiomInstantiation> map) {
		fullGeneralisationMap.put(cluster, map);
	}

	public void putAll(MultiMap<OWLAxiom, OWLAxiomInstantiation> map) {
		generalisationMap.putAll(map);
	}

	@Override
	public List<Set<P>> getClusterList() {
		return new ArrayList<Set<P>>(sortedClusters);
	}

	@Override
	public MultiMap<OWLAxiom, OWLAxiomInstantiation> get(Set<P> c) {
		if (fullGeneralisationMap.size() < 2) {
			buildGeneralisationMap(ontology);
		}
		return fullGeneralisationMap.get(c);
	}

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
			MultiMap<OWLAxiom, OWLAxiomInstantiation> multiMap = fullGeneralisationMap
					.get(cluster);
			OWLAxiom exampleLogicGeneralisation = getExampleLogicGeneralisation(multiMap);
			if (exampleLogicGeneralisation != null) {
				Collection<OWLAxiomInstantiation> collection = multiMap
						.get(exampleLogicGeneralisation);
				OWLAxiomInstantiation exampleInst = collection.iterator()
						.next();
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
		// if (sortedGeneralisationMap.size() < 2) {
		buildGeneralisationMap(ontology);
		// }
		return sortedGeneralisationMap;
	}

	public ConstraintSystem getConstraintSystem() {
		return constraintSystem;
	}

	public void setConstraintSystem(ConstraintSystem constraintSystem) {
		this.constraintSystem = constraintSystem;
	}

	@Override
	public Set<OWLOntology> getOntologies() {
		return ontology.getImportsClosure();
	}

	public void setGeneralisationMap(
			MultiMap<OWLAxiom, OWLAxiomInstantiation> map) {
		this.generalisationMap.clear();
		this.sortedGeneralisationMap.clear();
		generalisationMap.putAll(map);
		sortGeneralisations();
	}
}
