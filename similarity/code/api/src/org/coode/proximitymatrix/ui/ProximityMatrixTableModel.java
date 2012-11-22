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

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.coode.proximitymatrix.ProximityMatrix;

/** @author Luigi Iannone */
public class ProximityMatrixTableModel implements TableModel {
    private final ProximityMatrix<?> proximityMatrix;
    private final String[] columnNames;
    private final List<TableModelListener> listeners = new ArrayList<TableModelListener>();

    /** @param proximityMatrix */
    public ProximityMatrixTableModel(final ProximityMatrix<?> proximityMatrix,
            final String[] columnNames) {
        if (proximityMatrix == null) {
            throw new NullPointerException("Proximity matrix cannot be null");
        }
        this.columnNames = new String[columnNames.length];
        System.arraycopy(columnNames, 0, this.columnNames, 0, columnNames.length);
        this.proximityMatrix = proximityMatrix;
    }

    @Override
    public void addTableModelListener(final TableModelListener l) {
        if (l != null) {
            listeners.add(l);
        }
    }

    @Override
    public Class<?> getColumnClass(final int i) {
        return Object.class;
    }

    @Override
    public int getColumnCount() {
        return getProximityMatrix().getData().length() + 1;
    }

    @Override
    public String getColumnName(final int i) {
        return columnNames[i];
    }

    @Override
    public int getRowCount() {
        return getProximityMatrix().getData().length() + 1;
    }

    @Override
    public Object getValueAt(final int row, final int column) {
        Object toReturn = null;
        if (column == 0) {
            toReturn = columnNames[row + 1];
        } else {
            toReturn = getProximityMatrix().getData().get(row, column - 1);
        }
        return toReturn;
    }

    @Override
    public boolean isCellEditable(final int arg0, final int arg1) {
        return false;
    }

    @Override
    public void removeTableModelListener(final TableModelListener l) {
        listeners.remove(l);
    }

    @Override
    public void setValueAt(final Object arg0, final int arg1, final int arg2) {
        // TODO Auto-generated method stub
    }

    /** @return the proximityMatrix */
    public ProximityMatrix<?> getProximityMatrix() {
        return proximityMatrix;
    }
}
