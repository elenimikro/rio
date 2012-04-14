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
	private final OWLOntologyManager ontologyManager;
	//private final Map<OWLAxiom, Set<OWLOntology>> axiomOntologyMap = new HashMap<OWLAxiom, Set<OWLOntology>>();
	private final MultiMap<OWLEntity, OWLAxiom> entityAxiomMap = new MultiMap<OWLEntity, OWLAxiom>();
	private final Set<OWLAxiom> axiomsDelegate = new HashSet<OWLAxiom>();
	private OWLOntologyChangeListener listener = new OWLOntologyChangeListener() {
		@Override
		public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
				throws OWLException {
			for (OWLOntologyChange change : changes) {
				change.accept(new OWLOntologyChangeVisitor() {
					@Override
					public void visit(RemoveOntologyAnnotation change) {
						// Do Nothing
					}

					@Override
					public void visit(AddOntologyAnnotation change) {
						// Do Nothing
					}

					@Override
					public void visit(RemoveImport change) {
						OWLOntology ontology = change.getOntology();
						Set<OWLAxiom> axioms = ontology.getAxioms();
						for (OWLAxiom axiom : axioms) {
							RemoveAxiom remove = new RemoveAxiom(ontology, axiom);
							remove.accept(this);
						}
					}

					@Override
					public void visit(AddImport change) {
						OWLOntology ontology = change.getOntology();
						Set<OWLAxiom> axioms = ontology.getAxioms();
						for (OWLAxiom axiom : axioms) {
							AddAxiom add = new AddAxiom(ontology, axiom);
							add.accept(this);
						}
					}

					@Override
					public void visit(SetOntologyID change) {
						// Do Nothing
					}

					@Override
					public void visit(RemoveAxiom change) {
						OWLAxiom axiom = change.getAxiom();
						boolean found = false;
						// search for the removed axiom in ontologies other than the one where the change is happening
						for (OWLOntology o : ontologyManager.getOntologies()) {
							if (!found && o != change.getOntology()
									&& o.containsAxiom(axiom)) {
								found = true;
							}
						}
						// if the axiom is not found anywhere else, drop it from the delegate set
						if (!found) {
							OWLOntologyManagerBasedOWLAxiomProvider.this.axiomsDelegate
									.remove(axiom);
						}
						//						Set<OWLOntology> set = OWLOntologyManagerBasedOWLAxiomProvider.this.axiomOntologyMap.get(axiom);
						//						if (set != null) {
						//							set.remove(change.getOntology());
						//						}
						//						if (set.isEmpty()) {
						//OWLOntologyManagerBasedOWLAxiomProvider.this.axiomOntologyMap.remove(axiom);
						//							OWLOntologyManagerBasedOWLAxiomProvider.this.axiomsDelegate.remove(axiom);
						//						}
						Set<OWLEntity> signature = axiom.getSignature();
						for (OWLEntity entity : signature) {
							OWLOntologyManagerBasedOWLAxiomProvider.this.entityAxiomMap
									.remove(entity, axiom);
						}
					}

					@Override
					public void visit(AddAxiom change) {
						OWLAxiom axiom = change.getAxiom();
						// no need to check references, just add to the delegate set
						OWLOntologyManagerBasedOWLAxiomProvider.this.axiomsDelegate
								.add(axiom);
						//						Set<OWLOntology> set = OWLOntologyManagerBasedOWLAxiomProvider.this.axiomOntologyMap.get(axiom);
						//						if (set == null) {
						//							set = new HashSet<OWLOntology>();
						//							OWLOntologyManagerBasedOWLAxiomProvider.this.axiomOntologyMap.put(axiom, set);
						//							OWLOntologyManagerBasedOWLAxiomProvider.this.axiomsDelegate.add(axiom);
						//						}
						//						set.add(change.getOntology());
						Set<OWLEntity> signature = axiom.getSignature();
						for (OWLEntity entity : signature) {
							OWLOntologyManagerBasedOWLAxiomProvider.this.entityAxiomMap
									.put(entity, axiom);
						}
					}
				});
			}
			OWLOntologyManagerBasedOWLAxiomProvider.this.notifyListeners();
		}
	};

	public OWLOntologyManagerBasedOWLAxiomProvider(OWLOntologyManager ontologyManager) {
		if (ontologyManager == null) {
			throw new NullPointerException("The manager cannot be null");
		}
		this.ontologyManager = ontologyManager;
		this.getOntologyManager().addOntologyChangeListener(this.listener);
		this.rebuild();
	}

	private void rebuild() {
		Set<OWLOntology> ontologies = this.getOntologyManager().getOntologies();
		for (OWLOntology ontology : ontologies) {
			Set<OWLAxiom> axioms = ontology.getAxioms();
			for (OWLAxiom axiom : axioms) {
				//				Set<OWLOntology> set = this.axiomOntologyMap.get(axiom);
				//				if (set == null) {
				//					set = new HashSet<OWLOntology>();
				//					this.axiomOntologyMap.put(axiom, set);
				this.axiomsDelegate.add(axiom);
				//				}
				//				set.add(ontology);
				Set<OWLEntity> signature = axiom.getSignature();
				for (OWLEntity entity : signature) {
					this.entityAxiomMap.put(entity, axiom);
				}
			}
		}
		//		this.axiomsDelegate.clear();
		//		this.axiomsDelegate.addAll(this.axiomOntologyMap.keySet());
	}

	public OWLOntologyManager getOntologyManager() {
		return this.ontologyManager;
	}

	@Override
	public Set<OWLEntity> getSignature() {
		return new HashSet<OWLEntity>(this.entityAxiomMap.keySet());
	}

	public void dispose() {
		this.getOntologyManager().removeOntologyChangeListener(this.listener);
	}

	// Delegate methods
	/**
	 * @param e
	 * @return
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(OWLAxiom e) {
		return this.axiomsDelegate.add(e);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends OWLAxiom> c) {
		return this.axiomsDelegate.addAll(c);
	}

	/**
	 * 
	 * @see java.util.Set#clear()
	 */
	public void clear() {
		this.axiomsDelegate.clear();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return this.axiomsDelegate.contains(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		return this.axiomsDelegate.containsAll(c);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return this.axiomsDelegate.equals(o);
	}

	/**
	 * @return
	 * @see java.util.Set#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.axiomsDelegate.hashCode();
	}

	/**
	 * @return
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return this.axiomsDelegate.isEmpty();
	}

	/**
	 * @return
	 * @see java.util.Set#iterator()
	 */
	public Iterator<OWLAxiom> iterator() {
		return this.axiomsDelegate.iterator();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return this.axiomsDelegate.remove(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		return this.axiomsDelegate.removeAll(c);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		return this.axiomsDelegate.retainAll(c);
	}

	/**
	 * @return
	 * @see java.util.Set#size()
	 */
	public int size() {
		return this.axiomsDelegate.size();
	}

	/**
	 * @return
	 * @see java.util.Set#toArray()
	 */
	public Object[] toArray() {
		return this.axiomsDelegate.toArray();
	}

	/**
	 * @param <T>
	 * @param a
	 * @return
	 * @see java.util.Set#toArray(T[])
	 */
	public <T> T[] toArray(T[] a) {
		return this.axiomsDelegate.toArray(a);
	}
}
