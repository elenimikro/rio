package org.coode.proximitymatrix.cluster;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

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
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.atomicdecomposition.Atom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposition;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecompositionImpl;

/**
 * @author eleni
 */
public class GeneralisedAtomicDecomposition {
    private final ClusterDecompositionModel<OWLEntity> model;
    private final AtomicDecomposition ad;
    private final OWLOntology o;
    private final Map<OWLAxiom, Atom> genAtomMap = new HashMap<>();
    private final Map<Collection<OWLAxiom>, Atom> delegate = new HashMap<>();
    private final MultiMap<Atom, OWLAxiom> atomMap = new MultiMap<>();
    private final MultiMap<Atom, OWLEntity> atomEntityMap = new MultiMap<>();
    private final MultiMap<OWLEntity, Atom> entityAtomMap = new MultiMap<>();
    private final Map<OWLAxiom, OWLAxiom> instToGenMap = new HashMap<>();
    private final MultiMap<OWLAxiom, OWLAxiomInstantiation> regularitiesMap = new MultiMap<>();
    private final MultiMap<Collection<OWLAxiom>, Atom> duplicateAtomMap = new MultiMap<>();
    MultiMap<Atom, OWLAxiom> dirtyMap = new MultiMap<>();

    /**
     * @param model model
     * @param o o
     */
    public GeneralisedAtomicDecomposition(ClusterDecompositionModel<OWLEntity> model,
        OWLOntology o) {
        this.model = model;
        Set<OWLEntity> clusterSig = new HashSet<>();
        List<Cluster<OWLEntity>> clusterList = this.model.getClusterList();
        for (int i = 0; i < clusterList.size(); i++) {
            regularitiesMap.putAll(model.get(clusterList.get(i)));
            clusterSig.addAll(clusterList.get(i));
        }
        ad = new AtomicDecompositionImpl(o);
        this.o = o;
        buildGeneralisationAtomMap();
    }

    private void buildGeneralisationAtomMap() {
        Set<OWLAxiom> keySet = regularitiesMap.keySet();
        Set<OWLAxiom> axioms = asSet(o.axioms(Imports.INCLUDED));
        MultiMap<Atom, OWLAxiom> map = new MultiMap<>();
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
            Atom atom = ad.getAtomForAxiom(ax);
            if (atom != null) {
                map.put(atom, ax);
            }
        }
        dirtyMap.putAll(map);
        for (Atom a : map.keySet()) {
            delegate.put(map.get(a), a);
            duplicateAtomMap.put(map.get(a), a);
            atomMap.putAll(a, map.get(a));
        }
    }

    /** @return top atoms */
    public Set<Atom> getTopAtoms() {
        Set<Atom> toReturn = new HashSet<>();
        Set<Atom> bottomAtoms = ad.getBottomAtoms();
        toReturn.addAll(getReducedAtoms(bottomAtoms));
        return toReturn;
    }

    /** @return bottom atoms */
    public Set<Atom> getBottomAtoms() {
        Set<Atom> toReturn = new HashSet<>();
        Set<Atom> bottomAtoms = ad.getBottomAtoms();
        toReturn.addAll(getReducedAtoms(bottomAtoms));
        return toReturn;
    }

    /** @return generalisation axiom map */
    public Map<Collection<OWLAxiom>, Atom> getGeneralisationAtomMap() {
        return delegate;
    }

    /** @return atomic decomposition */
    public AtomicDecomposition getAtomicDecomposer() {
        return ad;
    }

    /** @return Cluster decomposition model */
    public ClusterDecompositionModel<OWLEntity> getClusterDecompositionModel() {
        return model;
    }

    /**
     * @param a a
     * @param b b
     * @return dependents
     */
    public Collection<? extends Atom> getDependents(Atom a, boolean b) {
        Set<Atom> dependencies = ad.getDependents(a, b);
        return getReducedAtoms(dependencies);
    }

    /**
     * @param atom atom
     * @param b b
     * @return dependencies
     */
    public Collection<? extends Atom> getDependencies(Atom atom, boolean b) {
        Set<Atom> dependencies = ad.getDependencies(atom, b);
        if (dependencies != null && !dependencies.isEmpty()) {
            return getReducedAtoms(dependencies);
        } else {
            return dependencies;
        }
    }

    /**
     * @param atom atom
     * @return dependencies
     */
    public Collection<? extends Atom> getDependencies(Atom atom) {
        Set<Atom> dependencies = ad.getDependencies(atom);
        if (dependencies != null && !dependencies.isEmpty()) {
            return getReducedAtoms(dependencies);
        } else {
            return dependencies;
        }
    }

    private Collection<? extends Atom> getReducedAtoms(Set<Atom> atoms) {
        Set<Atom> toReturn = new HashSet<>();
        if (instToGenMap.isEmpty() || genAtomMap.isEmpty()) {
            buildDepedencyMap();
        }
        for (Atom a : atoms) {
            Collection<OWLAxiom> axioms = a.getAxioms();
            for (OWLAxiom ax : axioms) {
                if (instToGenMap.get(ax) != null) {
                    toReturn.add(genAtomMap.get(instToGenMap.get(ax)));
                } else {
                    toReturn.add(genAtomMap.get(ax));
                }
            }
        }
        return toReturn;
    }

    private void buildDepedencyMap() {
        Set<OWLAxiom> keySet = regularitiesMap.keySet();
        for (OWLAxiom ax : keySet) {
            Collection<OWLAxiomInstantiation> collection = regularitiesMap.get(ax);
            for (OWLAxiomInstantiation inst : collection) {
                instToGenMap.put(inst.getAxiom(), ax);
            }
        }
        Set<Collection<OWLAxiom>> keySet2 = delegate.keySet();
        for (Collection<OWLAxiom> set : keySet2) {
            for (OWLAxiom axiom : set) {
                genAtomMap.put(axiom, delegate.get(set));
            }
        }
    }

    /** @return term based index */
    public MultiMap<OWLEntity, Atom> getTermBasedIndex() {
        if (entityAtomMap == null || entityAtomMap.size() == 0) {
            Set<Collection<OWLAxiom>> keySet = delegate.keySet();
            for (Collection<OWLAxiom> collection : keySet) {
                Set<OWLEntity> signature = asSet(collection.stream().flatMap(OWLAxiom::signature));
                Atom atom = delegate.get(collection);
                atomEntityMap.putAll(atom, signature);
                for (OWLEntity e : signature) {
                    entityAtomMap.put(e, atom);
                }
            }
        }
        return entityAtomMap;
    }

    /** @return dirty map */
    public MultiMap<Atom, OWLAxiom> getDirtyMap() {
        return dirtyMap;
    }

    /** @return merged atoms */
    public MultiMap<Collection<OWLAxiom>, Atom> getMergedAtoms() {
        MultiMap<Collection<OWLAxiom>, Atom> toReturn = new MultiMap<>();
        Set<Collection<OWLAxiom>> collections = duplicateAtomMap.keySet();
        for (Collection<OWLAxiom> col : collections) {
            Collection<Atom> atoms = duplicateAtomMap.get(col);
            if (atoms.size() > 1) {
                toReturn.putAll(col, atoms);
            }
        }
        return toReturn;
    }

    /**
     * @param atom atom
     * @return axioms
     */
    public Collection<OWLAxiom> getAxioms(Atom atom) {
        return atomMap.get(atom);
    }

    /** @return atoms */
    public Collection<? extends Atom> getAtoms() {
        Set<Atom> toReturn = new HashSet<>();
        toReturn.addAll(delegate.values());
        return toReturn;
    }

    /** @return entities to atoms */
    public MultiMap<OWLEntity, Atom> getEntitiesToAtom() {
        return entityAtomMap;
    }

    /** @return syntactic regularities */
    public MultiMap<OWLAxiom, OWLAxiomInstantiation> getSyntacticRegularities() {
        return regularitiesMap;
    }

    /** @return patterns */
    public Set<List<Atom>> getPatterns() {
        Set<List<Atom>> toReturn = new HashSet<>();
        Set<Atom> topAtoms = getTopAtoms();
        for (Atom atom : topAtoms) {
            List<Atom> patterns = new ArrayList<>();
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
