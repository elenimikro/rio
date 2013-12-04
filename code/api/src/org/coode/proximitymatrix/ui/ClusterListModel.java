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

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/** @author Luigi Iannone */
public class ClusterListModel<O> implements ListModel<Collection<? extends O>> {
    private final ListModel<Collection<? extends O>> delegate;

    /** @param clusters */
    public ClusterListModel(Collection<? extends Collection<? extends O>> clusters) {
        if (clusters == null) {
            throw new NullPointerException("The clusters collection cannot be null");
        }
        DefaultListModel<Collection<? extends O>> defaultListModel = new DefaultListModel<Collection<? extends O>>();
        for (Collection<? extends O> collection : clusters) {
            defaultListModel.addElement(collection);
        }
        this.delegate = defaultListModel;
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        this.delegate.addListDataListener(l);
    }

    @Override
    public Collection<? extends O> getElementAt(int index) {
        return this.delegate.getElementAt(index);
    }

    @Override
    public int getSize() {
        return this.delegate.getSize();
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        this.delegate.removeListDataListener(l);
    }

    public static <P> ClusterListModel<P> build(
            Collection<? extends Collection<? extends P>> clusters) {
        return new ClusterListModel<P>(clusters);
    }
}
