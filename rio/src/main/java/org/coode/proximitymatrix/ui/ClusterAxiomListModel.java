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

import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

/** @author Luigi Iannone */
public class ClusterAxiomListModel implements ListModel<OWLAxiomListItem> {
    private final ListModel<OWLAxiomListItem> delegate;
    private final int axiomCount;

    /**
     * @param cluster cluster
     * @param ontologies ontologies
     * @param generalisation generalisation
     */
    public ClusterAxiomListModel(Cluster<OWLEntity> cluster, Collection<OWLOntology> ontologies,
        OWLObjectGeneralisation generalisation) {
        if (cluster == null) {
            throw new NullPointerException("The cluster cannot be null");
        }
        if (ontologies == null) {
            throw new NullPointerException("The ontologies collection cannot be null");
        }
        if (ontologies.isEmpty()) {
            throw new IllegalArgumentException("The colleciton of ontologies cannot be null");
        }
        DefaultListModel<OWLAxiomListItem> defaultListModel = new DefaultListModel<>();
        final MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap =
            Utils.buildGeneralisationMap(cluster, ontologies,
                Utils.axiomsSkipDeclarationsAndAnnotations(ontologies), generalisation);
        Comparator<OWLAxiom> comparator = (axiom, anotherAxiom) -> {
            int toReturn = axiom.hashCode() - anotherAxiom.hashCode();
            Collection<OWLAxiomInstantiation> genAxioms = generalisationMap.get(axiom);
            Collection<OWLAxiomInstantiation> otherAxioms = generalisationMap.get(anotherAxiom);
            if (genAxioms != otherAxioms) {
                toReturn = genAxioms.size() - otherAxioms.size();
                if (toReturn == 0) {
                    toReturn = genAxioms.hashCode() - otherAxioms.hashCode();
                }
            }
            return toReturn;
        };
        SortedSet<OWLAxiom> sortedAxioms = new TreeSet<>(Collections.reverseOrder(comparator));
        sortedAxioms.addAll(generalisationMap.keySet());
        for (OWLAxiom axiom : sortedAxioms) {
            Collection<OWLAxiomInstantiation> axiomInstantiations = generalisationMap.get(axiom);
            defaultListModel.addElement(new OWLAxiomListItem(axiom, axiomInstantiations));
        }
        axiomCount = defaultListModel.getSize();
        delegate = defaultListModel;
    }

    /** @return the delegate */
    public ListModel<OWLAxiomListItem> getDelegate() {
        return delegate;
    }

    @Override
    public int getSize() {
        return getDelegate().getSize();
    }

    @Override
    public OWLAxiomListItem getElementAt(int index) {
        return getDelegate().getElementAt(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        getDelegate().addListDataListener(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        getDelegate().removeListDataListener(l);
    }

    /** @return the axiomCount */
    public int getAxiomCount() {
        return axiomCount;
    }
}
