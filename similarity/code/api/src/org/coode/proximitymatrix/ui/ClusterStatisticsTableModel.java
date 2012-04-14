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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterStatistics;

/**
 * @author Luigi Iannone
 * 
 */
public class ClusterStatisticsTableModel implements TableModel {
	private interface Selector {
		double select(ClusterStatistics<?> statistics);
	}

	// private static final MathContext MATH_CONTEXT = new MathContext(2);
	private final List<TableModelListener> listeners = new ArrayList<TableModelListener>();
	private final Cluster<?>[] clusters;
	private final ClusterStatistics<?>[] statistics;
	public final static Comparator<Cluster<?>> SIZE_COMPARATOR = Collections
			.reverseOrder(new Comparator<Cluster<?>>() {
				public int compare(Cluster<?> o1, Cluster<?> o2) {
					int sizeDifference = o1.size() - o2.size();
					return sizeDifference == 0 ? o1.hashCode() - o2.hashCode()
							: sizeDifference;
				}
			});
	private static final String[] COLUMN_NAMES = new String[] { "Cluster",
			"Avg Distance", "Min Distance", "Max Distance" };

	public ClusterStatisticsTableModel(Collection<? extends Cluster<?>> clusters) {
		this(clusters, SIZE_COMPARATOR);
	}

	public ClusterStatisticsTableModel(Collection<? extends Cluster<?>> clusters,
			Comparator<Cluster<?>> comparator) {
		if (clusters == null) {
			throw new NullPointerException("The cluster set cannot be null");
		}
		if (comparator == null) {
			throw new NullPointerException("The comparator cannot be null");
		}
		SortedSet<Cluster<?>> sortedSet = new TreeSet<Cluster<?>>(comparator);
		sortedSet.addAll(clusters);
		this.clusters = sortedSet.toArray(new Cluster[clusters.size()]);
		this.statistics = new ClusterStatistics<?>[this.clusters.length];
		for (int i = 0; i < this.clusters.length; i++) {
			Cluster<?> cluster = this.clusters[i];
			this.statistics[i] = ClusterStatistics.buildStatistics(cluster);
		}
		// this.delegate = new DefaultTableModel(clusters.size() + 1, 4);
		// this.delegate.setColumnIdentifiers(new String[] { "Cluster",
		// "Avg Distance",
		// "Min Distance", "Max Distance" });
		// int i = 0;
		// double count = 0;
		// for (Cluster<?> cluster : sortedSet) {
		// this.fillRow(i, cluster);
		// i++;
		// count += cluster.size();
		// }
		// this.delegate.setValueAt("TOTAL", i, 0);
		// double sumInternal = 0;
		// double minInternal = Double.MAX_VALUE;
		// double maxInternal = 0;
		// for (int j = 0; j < i; j++) {
		// minInternal = minInternal <=
		// Double.parseDouble(this.delegate.getValueAt(j, 2).toString()) ?
		// minInternal
		// : Double.parseDouble(this.delegate.getValueAt(j, 2).toString());
		// maxInternal = maxInternal >=
		// Double.parseDouble(this.delegate.getValueAt(j, 3).toString()) ?
		// maxInternal
		// : Double.parseDouble(this.delegate.getValueAt(j, 3).toString());
		// sumInternal += Double.parseDouble(this.delegate.getValueAt(j,
		// 1).toString());
		// }
		// this.delegate.setValueAt(
		// String.format("TOTAL %d  Avg size %s", clusters.size(), count /
		// clusters.size()),
		// i,
		// 0);
		// this.delegate.setValueAt(new BigDecimal(sumInternal / i,
		// MATH_CONTEXT).doubleValue(), i, 1);
		// this.delegate.setValueAt(new BigDecimal(minInternal,
		// MATH_CONTEXT).doubleValue(), i, 2);
		// this.delegate.setValueAt(new BigDecimal(maxInternal,
		// MATH_CONTEXT).doubleValue(), i, 3);
	}

	// private void fillRow(int i, Cluster<?> cluster) {
	// ClusterStatistics<?> statistics =
	// ClusterStatistics.buildStatistics(cluster);
	// this.delegate.setValueAt(cluster, i, 0);
	// this.delegate.setValueAt(new
	// BigDecimal(statistics.getAverageInternalDistance(),
	// MATH_CONTEXT).doubleValue(), i, 1);
	// this.delegate.setValueAt(
	// new BigDecimal(statistics.getMinInternalDistance(),
	// MATH_CONTEXT).doubleValue(),
	// i,
	// 2);
	// this.delegate.setValueAt(
	// new BigDecimal(statistics.getMaxInternalDistance(),
	// MATH_CONTEXT).doubleValue(),
	// i,
	// 3);
	// }
	public void addTableModelListener(TableModelListener l) {
		if (l != null) {
			this.listeners.add(l);
		}
	}

	public Class<?> getColumnClass(int columnIndex) {
		return Object.class;
	}

	public int getColumnCount() {
		return 4;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Object toReturn = null;
		if (columnIndex == 0) {
			double avgSize = this.avg(new Selector() {
				public double select(ClusterStatistics<?> statistics) {
					return statistics.getCluster().size();
				}
			});
			String totalString = String.format("TOTAL %d Average size: %f ",
					this.clusters.length, avgSize);
			toReturn = rowIndex >= this.clusters.length ? totalString
					: this.clusters[rowIndex];
		} else {
			ClusterStatistics<?> clusterStatistics = rowIndex >= this.statistics.length ? null
					: this.statistics[rowIndex];
			switch (columnIndex) {
				case 1:
					toReturn = rowIndex >= this.statistics.length ? this
							.avg(new Selector() {
								public double select(ClusterStatistics<?> statistics) {
									return statistics.getAverageInternalDistance();
								}
							}) : clusterStatistics.getAverageInternalDistance();
					break;
				case 2:
					toReturn = rowIndex >= this.statistics.length ? this
							.avg(new Selector() {
								public double select(ClusterStatistics<?> statistics) {
									return statistics.getMinInternalDistance();
								}
							}) : clusterStatistics.getMinInternalDistance();
					break;
				case 3:
					toReturn = rowIndex >= this.statistics.length ? this
							.avg(new Selector() {
								public double select(ClusterStatistics<?> statistics) {
									return statistics.getMaxInternalDistance();
								}
							}) : clusterStatistics.getMaxInternalDistance();
					break;
				default:
					break;
			}
		}
		return toReturn;
	}

	public String getColumnName(int columnIndex) {
		return COLUMN_NAMES[columnIndex];
	}

	public int getRowCount() {
		return this.clusters.length + 1;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void removeTableModelListener(TableModelListener l) {
		this.listeners.remove(l);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

	/**
	 * @return the clusters
	 */
	public Cluster<?>[] getClusters() {
		Cluster<?>[] toReturn = new Cluster[this.clusters.length];
		System.arraycopy(this.clusters, 0, toReturn, 0, this.clusters.length);
		return toReturn;
	}

	private double avg(Selector selector) {
		double toReturn = 0;
		if (this.statistics.length > 0) {
			for (ClusterStatistics<?> s : this.statistics) {
				toReturn += selector.select(s);
			}
			toReturn = toReturn / this.statistics.length;
		}
		return toReturn;
	}
}
