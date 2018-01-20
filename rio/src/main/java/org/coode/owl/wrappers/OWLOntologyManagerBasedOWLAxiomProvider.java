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
package org.coode.owl.wrappers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyChangeVisitor;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.RemoveOntologyAnnotation;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
public class OWLOntologyManagerBasedOWLAxiomProvider extends AbstractOWLAxiomProvider {
    final OWLOntologyManager ontologyManager;
    final MultiMap<OWLEntity, OWLAxiom> entityAxiomMap = new MultiMap<>();
    final Set<OWLAxiom> axiomsDelegate = new HashSet<>();
    private final OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
        @Override
        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
            for (OWLOntologyChange change : changes) {
                change.accept(new OWLOntologyChangeVisitor() {
                    @Override
                    public void visit(RemoveOntologyAnnotation c) {
                        // Do Nothing
                    }

                    @Override
                    public void visit(AddOntologyAnnotation c) {
                        // Do Nothing
                    }

                    @Override
                    public void visit(RemoveImport c) {
                        OWLOntology ontology = c.getOntology();
                        Set<OWLAxiom> axioms = ontology.getAxioms();
                        for (OWLAxiom axiom : axioms) {
                            RemoveAxiom remove = new RemoveAxiom(ontology, axiom);
                            remove.accept(this);
                        }
                    }

                    @Override
                    public void visit(AddImport c) {
                        OWLOntology ontology = c.getOntology();
                        Set<OWLAxiom> axioms = ontology.getAxioms();
                        for (OWLAxiom axiom : axioms) {
                            AddAxiom add = new AddAxiom(ontology, axiom);
                            add.accept(this);
                        }
                    }

                    @Override
                    public void visit(SetOntologyID c) {
                        // Do Nothing
                    }

                    @Override
                    public void visit(RemoveAxiom c) {
                        OWLAxiom axiom = c.getAxiom();
                        boolean found = false;
                        // search for the removed axiom in ontologies other than
                        // the one where the change is happening
                        for (OWLOntology o : ontologyManager.getOntologies()) {
                            if (!found && o != c.getOntology() && o.containsAxiom(axiom)) {
                                found = true;
                            }
                        }
                        // if the axiom is not found anywhere else, drop it from
                        // the delegate set
                        if (!found) {
                            axiomsDelegate.remove(axiom);
                        }
                        Set<OWLEntity> signature = axiom.getSignature();
                        for (OWLEntity entity : signature) {
                            entityAxiomMap.remove(entity, axiom);
                        }
                    }

                    @Override
                    public void visit(AddAxiom c) {
                        OWLAxiom axiom = c.getAxiom();
                        // no need to check references, just add to the delegate
                        // set
                        axiomsDelegate.add(axiom);
                        Set<OWLEntity> signature = axiom.getSignature();
                        for (OWLEntity entity : signature) {
                            entityAxiomMap.put(entity, axiom);
                        }
                    }
                });
            }
            OWLOntologyManagerBasedOWLAxiomProvider.this.notifyListeners();
        }
    };

    /**
     * @param ontologyManager ontologyManager
     */
    public OWLOntologyManagerBasedOWLAxiomProvider(OWLOntologyManager ontologyManager) {
        if (ontologyManager == null) {
            throw new NullPointerException("The manager cannot be null");
        }
        this.ontologyManager = ontologyManager;
        getOntologyManager().addOntologyChangeListener(listener);
        rebuild();
    }

    private void rebuild() {
        Set<OWLOntology> ontologies = getOntologyManager().getOntologies();
        for (OWLOntology ontology : ontologies) {
            Set<OWLAxiom> axioms = ontology.getAxioms();
            for (OWLAxiom axiom : axioms) {
                axiomsDelegate.add(axiom);
                Set<OWLEntity> signature = axiom.getSignature();
                for (OWLEntity entity : signature) {
                    entityAxiomMap.put(entity, axiom);
                }
            }
        }
    }

    /** @return mnager */
    public OWLOntologyManager getOntologyManager() {
        return ontologyManager;
    }

    @Override
    public Set<OWLEntity> getSignature() {
        return new HashSet<>(entityAxiomMap.keySet());
    }

    /**
     * 
     */
    public void dispose() {
        getOntologyManager().removeOntologyChangeListener(listener);
    }

    // Delegate methods
    @Override
    public boolean add(OWLAxiom e) {
        return axiomsDelegate.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends OWLAxiom> c) {
        return axiomsDelegate.addAll(c);
    }

    @Override
    public void clear() {
        axiomsDelegate.clear();
    }

    @Override
    public boolean contains(Object o) {
        return axiomsDelegate.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return axiomsDelegate.containsAll(c);
    }

    @Override
    public boolean equals(Object o) {
        return axiomsDelegate.equals(o);
    }

    @Override
    public int hashCode() {
        return axiomsDelegate.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return axiomsDelegate.isEmpty();
    }

    @Override
    public Iterator<OWLAxiom> iterator() {
        return axiomsDelegate.iterator();
    }

    @Override
    public boolean remove(Object o) {
        return axiomsDelegate.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return axiomsDelegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return axiomsDelegate.retainAll(c);
    }

    @Override
    public int size() {
        return axiomsDelegate.size();
    }

    @Override
    public Object[] toArray() {
        return axiomsDelegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return axiomsDelegate.toArray(a);
    }
}
