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
    private final List<TableModelListener> listeners = new ArrayList<>();

    /**
     * @param proximityMatrix proximityMatrix
     * @param columnNames columnNames
     */
    public ProximityMatrixTableModel(ProximityMatrix<?> proximityMatrix, String[] columnNames) {
        if (proximityMatrix == null) {
            throw new NullPointerException("Proximity matrix cannot be null");
        }
        this.columnNames = new String[columnNames.length];
        System.arraycopy(columnNames, 0, this.columnNames, 0, columnNames.length);
        this.proximityMatrix = proximityMatrix;
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        if (l != null) {
            listeners.add(l);
        }
    }

    @Override
    public Class<?> getColumnClass(int i) {
        return Object.class;
    }

    @Override
    public int getColumnCount() {
        return getProximityMatrix().getData().length() + 1;
    }

    @Override
    public String getColumnName(int i) {
        return columnNames[i];
    }

    @Override
    public int getRowCount() {
        return getProximityMatrix().getData().length() + 1;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object toReturn = null;
        if (column == 0) {
            toReturn = columnNames[row + 1];
        } else {
            toReturn = Double.valueOf(getProximityMatrix().getData().get(row, column - 1));
        }
        return toReturn;
    }

    @Override
    public boolean isCellEditable(int arg0, int arg1) {
        return false;
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    @Override
    public void setValueAt(Object arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    /** @return the proximityMatrix */
    public ProximityMatrix<?> getProximityMatrix() {
        return proximityMatrix;
    }
}
