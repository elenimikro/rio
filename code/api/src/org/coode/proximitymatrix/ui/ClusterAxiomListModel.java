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
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

/** @author Luigi Iannone */
public class ClusterAxiomListModel implements ListModel {
    private final ListModel delegate;
    private final int axiomCount;

    public ClusterAxiomListModel(Cluster<? extends OWLEntity> cluster,
            Collection<? extends OWLOntology> ontologies,
            OWLObjectGeneralisation generalisation) {
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
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLOntology ont : ontologies) {
            axioms.addAll(ont.getAxioms());
        }
        final MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = Utils
                .buildGeneralisationMap(cluster, ontologies, axioms, generalisation);
        Comparator<OWLAxiom> comparator = new Comparator<OWLAxiom>() {
            @Override
            public int compare(OWLAxiom axiom, OWLAxiom anotherAxiom) {
                int toReturn = axiom.hashCode() - anotherAxiom.hashCode();
                Collection<OWLAxiomInstantiation> genAxioms = generalisationMap
                        .get(axiom);
                Collection<OWLAxiomInstantiation> otherAxioms = generalisationMap
                        .get(anotherAxiom);
                if (genAxioms != otherAxioms) {
                    toReturn = genAxioms.size() - otherAxioms.size();
                    if (toReturn == 0) {
                        toReturn = genAxioms.hashCode() - otherAxioms.hashCode();
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
        axiomCount = defaultListModel.getSize();
        delegate = defaultListModel;
    }

    /** @return the delegate */
    public ListModel getDelegate() {
        return delegate;
    }

    /** @return
     * @see javax.swing.ListModel#getSize() */
    @Override
    public int getSize() {
        return getDelegate().getSize();
    }

    /** @param index
     * @return
     * @see javax.swing.ListModel#getElementAt(int) */
    @Override
    public Object getElementAt(int index) {
        return getDelegate().getElementAt(index);
    }

    /** @param l
     * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener) */
    @Override
    public void addListDataListener(ListDataListener l) {
        getDelegate().addListDataListener(l);
    }

    /** @param l
     * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener) */
    @Override
    public void removeListDataListener(ListDataListener l) {
        getDelegate().removeListDataListener(l);
    }

    /** @return the axiomCount */
    public int getAxiomCount() {
        return axiomCount;
    }
}
