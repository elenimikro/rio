package org.coode.proximitymatrix.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.atomicdecomposition.Atom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposerOWLAPITOOLS;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposition;

public class GeneralisedAtomicDecomposition<P extends OWLEntity> {

	private final ClusterDecompositionModel<OWLEntity> model;
	private final AtomicDecomposition ad;
	private final OWLOntology o;

	private final Map<OWLAxiom, Atom> genAtomMap = new HashMap<OWLAxiom, Atom>();
	private final Map<Collection<OWLAxiom>, Atom> delegate = new HashMap<Collection<OWLAxiom>, Atom>();
	private final MultiMap<Atom, OWLAxiom> atomMap = new MultiMap<Atom, OWLAxiom>();
	private final MultiMap<Atom, Atom> wrappedMap = new MultiMap<Atom, Atom>();

	private final MultiMap<Atom, OWLEntity> atomEntityMap = new MultiMap<Atom, OWLEntity>();
	private final MultiMap<OWLEntity, Atom> entityAtomMap = new MultiMap<OWLEntity, Atom>();
	private final Map<OWLAxiom, OWLAxiom> instToGenMap = new HashMap<OWLAxiom, OWLAxiom>();
	private final MultiMap<OWLAxiom, OWLAxiomInstantiation> regularitiesMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();

	private final MultiMap<Collection<OWLAxiom>, Atom> duplicateAtomMap = new MultiMap<Collection<OWLAxiom>, Atom>();
	MultiMap<Atom, OWLAxiom> dirtyMap = new MultiMap<Atom, OWLAxiom>();

	public GeneralisedAtomicDecomposition(
			ClusterDecompositionModel<OWLEntity> model, OWLOntology o) {
		this.model = model;
		Set<OWLEntity> clusterSig = new HashSet<OWLEntity>();
		List<Cluster<OWLEntity>> clusterList = this.model.getClusterList();
		for (int i = 0; i < clusterList.size(); i++) {
			regularitiesMap.putAll(model.get(clusterList.get(i)));
			clusterSig.addAll(clusterList.get(i));
		}
		this.ad = new AtomicDecomposerOWLAPITOOLS(o);
		this.o = o;
		buildGeneralisationAtomMap();
	}

	private void buildGeneralisationAtomMap() {
		Set<OWLAxiom> keySet = regularitiesMap.keySet();
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for (OWLOntology onto : o.getImportsClosure()) {
			axioms.addAll(onto.getAxioms());
		}
		MultiMap<Atom, OWLAxiom> map = new MultiMap<Atom, OWLAxiom>();
		for (OWLAxiom ax : keySet) {
			Collection<OWLAxiomInstantiation> insts = regularitiesMap.get(ax);
			for (OWLAxiomInstantiation inst : insts) {
				Atom atomForAxiom = ad.getAtomForAxiom(inst.getAxiom());
				if (atomForAxiom != null) {
					map.put(atomForAxiom, ax);
				}
				axioms.remove(inst.getAxiom());
			}
		}
		for (OWLAxiom ax : axioms) {
			Atom atom = this.ad.getAtomForAxiom(ax);
			if (atom != null) {
				map.put(atom, ax);
			}
		}

		this.dirtyMap.putAll(map);

		for (Atom a : map.keySet()) {
			delegate.put(map.get(a), a);
			duplicateAtomMap.put(map.get(a), a);
			atomMap.putAll(a, map.get(a));
		}

	}

	public Set<Atom> getTopAtoms() {
		Set<Atom> toReturn = new HashSet<Atom>();
		Set<Atom> bottomAtoms = ad.getBottomAtoms();
		toReturn.addAll(this.getReducedAtoms(bottomAtoms));
		return toReturn;
	}

	public Set<Atom> getBottomAtoms() {
		Set<Atom> toReturn = new HashSet<Atom>();
		Set<Atom> bottomAtoms = ad.getBottomAtoms();
		toReturn.addAll(this.getReducedAtoms(bottomAtoms));
		return toReturn;
	}

	public Map<Collection<OWLAxiom>, Atom> getGeneralisationAtomMap() {
		return this.delegate;

	}

	public AtomicDecomposition getAtomicDecomposer() {
		return ad;
	}

	public ClusterDecompositionModel<OWLEntity> getClusterDecompositionModel() {
		return model;
	}

	public Collection<? extends Atom> getDependents(Atom a, boolean b) {
		Set<Atom> dependencies = ad.getDependents(a, b);
		return getReducedAtoms(dependencies);
	}

	public Collection<? extends Atom> getDependencies(Atom atom, boolean b) {
		Set<Atom> dependencies = ad.getDependencies(atom, b);
		if (dependencies != null && !dependencies.isEmpty()) {
			return getReducedAtoms(dependencies);
		} else {
			return dependencies;
		}
	}

	public Collection<? extends Atom> getDependencies(Atom atom) {
		Set<Atom> dependencies = ad.getDependencies(atom);
		if (dependencies != null && !dependencies.isEmpty()) {
			return getReducedAtoms(dependencies);
		} else {
			return dependencies;
		}
	}

	private Collection<? extends Atom> getReducedAtoms(Set<Atom> atoms) {
		Set<Atom> toReturn = new HashSet<Atom>();
		if (instToGenMap.isEmpty() || genAtomMap.isEmpty()) {
			buildDepedencyMap();
		}
		for (Atom a : atoms) {
			Collection<OWLAxiom> axioms = a.getAxioms();
			for (OWLAxiom ax : axioms) {
				if (instToGenMap.get(ax) != null) {
					toReturn.add(this.genAtomMap.get(instToGenMap.get(ax)));
				} else {
					toReturn.add(this.genAtomMap.get(ax));
				}
			}
		}
		return toReturn;
	}

	private void buildDepedencyMap() {
		Set<OWLAxiom> keySet = regularitiesMap.keySet();
		for (OWLAxiom ax : keySet) {
			Collection<OWLAxiomInstantiation> collection = regularitiesMap
					.get(ax);
			for (OWLAxiomInstantiation inst : collection) {
				this.instToGenMap.put(inst.getAxiom(), ax);
			}
		}
		Set<Collection<OWLAxiom>> keySet2 = this.delegate.keySet();
		for (Collection<OWLAxiom> set : keySet2) {
			for (OWLAxiom axiom : set) {
				this.genAtomMap.put(axiom, this.delegate.get(set));
			}
		}
	}

	public MultiMap<OWLEntity, Atom> getTermBasedIndex() {
		if (entityAtomMap == null || entityAtomMap.size() == 0) {
			Set<Collection<OWLAxiom>> keySet = this.delegate.keySet();
			for (Collection<OWLAxiom> collection : keySet) {
				Set<OWLEntity> signature = new HashSet<OWLEntity>();
				for (OWLAxiom ax : collection) {
					signature.addAll(ax.getSignature());
				}
				Atom atom = this.delegate.get(collection);
				atomEntityMap.putAll(atom, signature);
				for (OWLEntity e : signature) {
					entityAtomMap.put(e, atom);
				}
			}
		}
		return entityAtomMap;
	}

	public MultiMap<Atom, OWLAxiom> getDirtyMap() {
		return this.dirtyMap;
	}

	public MultiMap<Collection<OWLAxiom>, Atom> getMergedAtoms() {
		MultiMap<Collection<OWLAxiom>, Atom> toReturn = new MultiMap<Collection<OWLAxiom>, Atom>();
		Set<Collection<OWLAxiom>> collections = this.duplicateAtomMap.keySet();
		for (Collection<OWLAxiom> col : collections) {
			Collection<Atom> atoms = this.duplicateAtomMap.get(col);
			if (atoms.size() > 1) {
				toReturn.putAll(col, atoms);
			}
		}
		return toReturn;
	}

	public Collection<OWLAxiom> getAxioms(Atom atom) {
		return this.atomMap.get(atom);
	}

	public Collection<? extends Atom> getAtoms() {
		Set<Atom> toReturn = new HashSet<Atom>();
		toReturn.addAll(this.delegate.values());
		return toReturn;
	}

	public MultiMap<OWLEntity, Atom> getEntitiesToAtom() {
		return entityAtomMap;
	}

	public MultiMap<OWLAxiom, OWLAxiomInstantiation> getSyntacticRegularities() {
		return regularitiesMap;
	}

	public Set<List<Atom>> getPatterns() {
		Set<List<Atom>> toReturn = new HashSet<List<Atom>>();
		Set<Atom> topAtoms = this.getTopAtoms();
		for (Atom atom : topAtoms) {
			List<Atom> patterns = new ArrayList<Atom>();
			patterns.add(atom);
			recurseForDepedencies(atom, patterns);
			toReturn.add(patterns);
		}
		return toReturn;
	}

	private void recurseForDepedencies(Atom parent, List<Atom> patterns) {
		for (Atom child : this.getDependencies(parent)) {
			if (!patterns.contains(child)) {
				patterns.add(child);
				recurseForDepedencies(child, patterns);
			}
		}
	}

}
