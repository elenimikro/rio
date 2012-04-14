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

/**
 * @author Luigi Iannone
 * 
 */
public class ProximityMatrixTableModel implements TableModel {
	private final ProximityMatrix<?> proximityMatrix;
	private final String[] columnNames;
	private final List<TableModelListener> listeners = new ArrayList<TableModelListener>();

	/**
	 * @param proximityMatrix
	 */
	public ProximityMatrixTableModel(ProximityMatrix<?> proximityMatrix,
			String[] columnNames) {
		if (proximityMatrix == null) {
			throw new NullPointerException("Proximity matrix cannot be null");
		}
		this.columnNames = new String[columnNames.length];
		System.arraycopy(columnNames, 0, this.columnNames, 0, columnNames.length);
		this.proximityMatrix = proximityMatrix;
	}

	public void addTableModelListener(TableModelListener l) {
		if (l != null) {
			this.listeners.add(l);
		}
	}

	public Class<?> getColumnClass(int i) {
		return Object.class;
	}

	public int getColumnCount() {
		return this.getProximityMatrix().getColumnDimension() + 1;
	}

	public String getColumnName(int i) {
		return this.columnNames[i];
	}

	public int getRowCount() {
		return this.getProximityMatrix().getRowDimension();
	}

	public Object getValueAt(int row, int column) {
		Object toReturn = null;
		if (column == 0) {
			toReturn = this.columnNames[row + 1];
		} else {
			toReturn = this.getProximityMatrix().getData()[row][column - 1];
		}
		return toReturn;
	}

	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	public void removeTableModelListener(TableModelListener l) {
		this.listeners.remove(l);
	}

	public void setValueAt(Object arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	/**
	 * @return the proximityMatrix
	 */
	public ProximityMatrix<?> getProximityMatrix() {
		return this.proximityMatrix;
	}
}
