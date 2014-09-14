/*******************************************************************************
 * Copyright (c) 2012 Eleni Mikroyannidi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Eleni Mikroyannidi, Luigi Iannone - initial API and implementation
 ******************************************************************************/
package atomicdecomposition.wrappers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.atomicdecomposition.Atom;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposerOWLAPITOOLS;
import uk.ac.manchester.cs.atomicdecomposition.AtomicDecomposition;

/** @author eleni */
public class OWLAtomicDecompositionMap {
    private final AtomicDecomposition atomicDecomposition;
    private final OWLOntologyManager ontologyManger;
    private final OWLOntology ontology;
    private final Set<OWLEntity> ontologySignature = new HashSet<OWLEntity>();
    MultiMap<OWLEntity, Atom> entityAtomDependencies = new MultiMap<OWLEntity, Atom>();
    MultiMap<OWLEntity, Atom> entityAtomInfluences = new MultiMap<OWLEntity, Atom>();

    /** @param ontology
     *            ontology
     * @param manager
     *            manager */
    public OWLAtomicDecompositionMap(OWLOntology ontology, OWLOntologyManager manager) {
        if (ontology == null) {
            throw new NullPointerException("The ontolgies canont be null");
        }
        if (manager == null) {
            throw new NullPointerException("The ontolgy manager cannot be null");
        }
        this.ontology = ontology;
        ontologyManger = manager;
        buildOntologySignature();
        atomicDecomposition = new AtomicDecomposerOWLAPITOOLS(ontology);
        buildAtomDependenciesMap();
        buildInfluencesMap();
    }

    private void buildInfluencesMap() {
        for (OWLEntity entity : ontologySignature) {
            Map<OWLEntity, Set<Atom>> termBasedIndex = atomicDecomposition
                    .getTermBasedIndex();
            Set<Atom> atoms = termBasedIndex.get(entity);
            if (atoms != null) {
                Set<Atom> influencies = new HashSet<Atom>();
                for (Atom atom : atoms) {
                    influencies.addAll(atomicDecomposition.getDependents(atom));
                }
                influencies.removeAll(atoms);
                entityAtomInfluences.setEntry(entity, influencies);
            }
        }
    }

    private void buildOntologySignature() {
        ontologySignature.clear();
        for (OWLOntology ont : ontology.getImportsClosure()) {
            ontologySignature.addAll(ont.getSignature());
        }
    }

    private void buildAtomDependenciesMap() {
        for (OWLEntity entity : ontologySignature) {
            Map<OWLEntity, Set<Atom>> termBasedIndex = atomicDecomposition
                    .getTermBasedIndex();
            Set<Atom> atoms = termBasedIndex.get(entity);
            if (atoms != null) {
                Set<Atom> dependencies = new HashSet<Atom>();
                for (Atom atom : atoms) {
                    dependencies.addAll(atomicDecomposition.getDependencies(atom));
                }
                dependencies.removeAll(atoms);
                entityAtomDependencies.setEntry(entity, dependencies);
            }
        }
    }

    /** @return AD */
    public AtomicDecomposition getAtomicDecomposition() {
        return atomicDecomposition;
    }

    /** @return dependencies */
    public MultiMap<OWLEntity, Atom> getEntityAtomDependencies() {
        return entityAtomDependencies;
    }

    /** @return manager */
    public OWLOntologyManager getOntologyManger() {
        return ontologyManger;
    }

    /** @return ontology */
    public OWLOntology getOntologies() {
        return ontology;
    }
}
