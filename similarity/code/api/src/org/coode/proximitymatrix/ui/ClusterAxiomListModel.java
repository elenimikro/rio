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
/**
 *
 */
package org.coode.proximitymatrix.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.coode.oppl.exceptions.RuntimeExceptionHandler;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

/**
 * @author Luigi Iannone
 * 
 */
public class ClusterAxiomListModel implements ListModel {
	private final ListModel delegate;
	private final int axiomCount;

	public ClusterAxiomListModel(Cluster<? extends OWLEntity> cluster,
			Collection<? extends OWLOntology> ontologies,
			OWLObjectGeneralisation generalisation,
			RuntimeExceptionHandler runtimeExceptionHandler) {
		if (cluster == null) {
			throw new NullPointerException("The cluster cannot be null");
		}
		if (ontologies == null) {
			throw new NullPointerException("The ontologies collection cannot be null");
		}
		if (ontologies.isEmpty()) {
			throw new IllegalArgumentException(
					"The colleciton of ontologies cannot be null");
		}
		DefaultListModel defaultListModel = new DefaultListModel();
		final MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = Utils
				.buildGeneralisationMap(cluster, ontologies, generalisation,
						runtimeExceptionHandler);
		Comparator<OWLAxiom> comparator = new Comparator<OWLAxiom>() {
			public int compare(OWLAxiom axiom, OWLAxiom anotherAxiom) {
				int toReturn = axiom.hashCode() - anotherAxiom.hashCode();
				Collection<OWLAxiomInstantiation> axioms = generalisationMap.get(axiom);
				Collection<OWLAxiomInstantiation> otherAxioms = generalisationMap
						.get(anotherAxiom);
				if (axioms != otherAxioms) {
					toReturn = axioms.size() - otherAxioms.size();
					if (toReturn == 0) {
						toReturn = axioms.hashCode() - otherAxioms.hashCode();
					}
				}
				return toReturn;
			}
		};
		SortedSet<OWLAxiom> sortedAxioms = new TreeSet<OWLAxiom>(
				Collections.reverseOrder(comparator));
		sortedAxioms.addAll(generalisationMap.keySet());
		for (OWLAxiom axiom : sortedAxioms) {
			Collection<OWLAxiomInstantiation> axiomInstantiations = generalisationMap
					.get(axiom);
			defaultListModel.addElement(new OWLAxiomListItem(axiom, axiomInstantiations));
		}
		this.axiomCount = defaultListModel.getSize();
		this.delegate = defaultListModel;
	}

	/**
	 * @return the delegate
	 */
	public ListModel getDelegate() {
		return this.delegate;
	}

	/**
	 * @return
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		return this.getDelegate().getSize();
	}

	/**
	 * @param index
	 * @return
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		return this.getDelegate().getElementAt(index);
	}

	/**
	 * @param l
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	public void addListDataListener(ListDataListener l) {
		this.getDelegate().addListDataListener(l);
	}

	/**
	 * @param l
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	public void removeListDataListener(ListDataListener l) {
		this.getDelegate().removeListDataListener(l);
	}

	/**
	 * @return the axiomCount
	 */
	public int getAxiomCount() {
		return this.axiomCount;
	}
}
