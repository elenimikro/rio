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
import org.semanticweb.owlapi.model.OWLException;
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

public class OWLOntologyManagerBasedOWLAxiomProvider extends AbstractOWLAxiomProvider {
    final OWLOntologyManager ontologyManager;
    // private final Map<OWLAxiom, Set<OWLOntology>> axiomOntologyMap = new
    // HashMap<OWLAxiom, Set<OWLOntology>>();
    final MultiMap<OWLEntity, OWLAxiom> entityAxiomMap = new MultiMap<OWLEntity, OWLAxiom>();
    final Set<OWLAxiom> axiomsDelegate = new HashSet<OWLAxiom>();
    private final OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
        @Override
        public void ontologiesChanged(final List<? extends OWLOntologyChange> changes)
                throws OWLException {
            for (OWLOntologyChange change : changes) {
                change.accept(new OWLOntologyChangeVisitor() {
                    @Override

                    public void visit(final RemoveOntologyAnnotation c) {
                        // Do Nothing
                    }

                    @Override

                    public void visit(final AddOntologyAnnotation c) {
                        // Do Nothing
                    }

                    @Override
                    public void visit(final RemoveImport c) {
                        OWLOntology ontology = c.getOntology();
                        Set<OWLAxiom> axioms = ontology.getAxioms();
                        for (OWLAxiom axiom : axioms) {
                            RemoveAxiom remove = new RemoveAxiom(ontology, axiom);
                            remove.accept(this);
                        }
                    }

                    @Override
                    public void visit(final AddImport c) {
                        OWLOntology ontology = c.getOntology();
                        Set<OWLAxiom> axioms = ontology.getAxioms();
                        for (OWLAxiom axiom : axioms) {
                            AddAxiom add = new AddAxiom(ontology, axiom);
                            add.accept(this);
                        }
                    }

                    @Override

                    public void visit(final SetOntologyID c) {
                        // Do Nothing
                    }

                    @Override
                    public void visit(final RemoveAxiom c) {
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
                        // Set<OWLOntology> set =
                        // OWLOntologyManagerBasedOWLAxiomProvider.this.axiomOntologyMap.get(axiom);
                        // if (set != null) {
                        // set.remove(change.getOntology());
                        // }
                        // if (set.isEmpty()) {
                        // OWLOntologyManagerBasedOWLAxiomProvider.this.axiomOntologyMap.remove(axiom);
                        // OWLOntologyManagerBasedOWLAxiomProvider.this.axiomsDelegate.remove(axiom);
                        // }
                        Set<OWLEntity> signature = axiom.getSignature();
                        for (OWLEntity entity : signature) {
                            entityAxiomMap.remove(entity, axiom);
                        }
                    }

                    @Override
                    public void visit(final AddAxiom c) {
                        OWLAxiom axiom = c.getAxiom();
                        // no need to check references, just add to the delegate
                        // set
                        axiomsDelegate.add(axiom);
                        // Set<OWLOntology> set =
                        // OWLOntologyManagerBasedOWLAxiomProvider.this.axiomOntologyMap.get(axiom);
                        // if (set == null) {
                        // set = new HashSet<OWLOntology>();
                        // OWLOntologyManagerBasedOWLAxiomProvider.this.axiomOntologyMap.put(axiom,
                        // set);
                        // OWLOntologyManagerBasedOWLAxiomProvider.this.axiomsDelegate.add(axiom);
                        // }
                        // set.add(change.getOntology());
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

    public OWLOntologyManagerBasedOWLAxiomProvider(
            final OWLOntologyManager ontologyManager) {
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
                // Set<OWLOntology> set = this.axiomOntologyMap.get(axiom);
                // if (set == null) {
                // set = new HashSet<OWLOntology>();
                // this.axiomOntologyMap.put(axiom, set);
                axiomsDelegate.add(axiom);
                // }
                // set.add(ontology);
                Set<OWLEntity> signature = axiom.getSignature();
                for (OWLEntity entity : signature) {
                    entityAxiomMap.put(entity, axiom);
                }
            }
        }
        // this.axiomsDelegate.clear();
        // this.axiomsDelegate.addAll(this.axiomOntologyMap.keySet());
    }

    public OWLOntologyManager getOntologyManager() {
        return ontologyManager;
    }

    @Override
    public Set<OWLEntity> getSignature() {
        return new HashSet<OWLEntity>(entityAxiomMap.keySet());
    }

    public void dispose() {
        getOntologyManager().removeOntologyChangeListener(listener);
    }

    // Delegate methods
    /** @param e
     * @return
     * @see java.util.Set#add(java.lang.Object) */
    @Override
    public boolean add(final OWLAxiom e) {
        return axiomsDelegate.add(e);
    }

    /** @param c
     * @return
     * @see java.util.Set#addAll(java.util.Collection) */
    @Override
    public boolean addAll(final Collection<? extends OWLAxiom> c) {
        return axiomsDelegate.addAll(c);
    }

    /** @see java.util.Set#clear() */
    @Override
    public void clear() {
        axiomsDelegate.clear();
    }

    /** @param o
     * @return
     * @see java.util.Set#contains(java.lang.Object) */
    @Override
    public boolean contains(final Object o) {
        return axiomsDelegate.contains(o);
    }

    /** @param c
     * @return
     * @see java.util.Set#containsAll(java.util.Collection) */
    @Override
    public boolean containsAll(final Collection<?> c) {
        return axiomsDelegate.containsAll(c);
    }

    /** @param o
     * @return
     * @see java.util.Set#equals(java.lang.Object) */
    @Override
    public boolean equals(final Object o) {
        return axiomsDelegate.equals(o);
    }

    /** @return
     * @see java.util.Set#hashCode() */
    @Override
    public int hashCode() {
        return axiomsDelegate.hashCode();
    }

    /** @return
     * @see java.util.Set#isEmpty() */
    @Override
    public boolean isEmpty() {
        return axiomsDelegate.isEmpty();
    }

    /** @return
     * @see java.util.Set#iterator() */
    @Override
    public Iterator<OWLAxiom> iterator() {
        return axiomsDelegate.iterator();
    }

    /** @param o
     * @return
     * @see java.util.Set#remove(java.lang.Object) */
    @Override
    public boolean remove(final Object o) {
        return axiomsDelegate.remove(o);
    }

    /** @param c
     * @return
     * @see java.util.Set#removeAll(java.util.Collection) */
    @Override
    public boolean removeAll(final Collection<?> c) {
        return axiomsDelegate.removeAll(c);
    }

    /** @param c
     * @return
     * @see java.util.Set#retainAll(java.util.Collection) */
    @Override
    public boolean retainAll(final Collection<?> c) {
        return axiomsDelegate.retainAll(c);
    }

    /** @return
     * @see java.util.Set#size() */
    @Override
    public int size() {
        return axiomsDelegate.size();
    }

    /** @return
     * @see java.util.Set#toArray() */
    @Override
    public Object[] toArray() {
        return axiomsDelegate.toArray();
    }

    /** @param <T>
     * @param a
     * @return
     * @see java.util.Set#toArray(T[]) */
    @Override
    public <T> T[] toArray(final T[] a) {
        return axiomsDelegate.toArray(a);
    }
}
