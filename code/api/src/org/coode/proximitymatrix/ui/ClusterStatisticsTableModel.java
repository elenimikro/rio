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

/** @author Luigi Iannone */
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
                @Override
                public int compare(final Cluster<?> o1, final Cluster<?> o2) {
                    int sizeDifference = o1.size() - o2.size();
                    return sizeDifference == 0 ? o1.hashCode() - o2.hashCode()
                            : sizeDifference;
                }
            });
    private static final String[] COLUMN_NAMES = new String[] { "Cluster",
            "Avg Distance", "Min Distance", "Max Distance" };

    public ClusterStatisticsTableModel(final Collection<? extends Cluster<?>> clusters) {
        this(clusters, SIZE_COMPARATOR);
    }

    public ClusterStatisticsTableModel(final Collection<? extends Cluster<?>> clusters,
            final Comparator<Cluster<?>> comparator) {
        if (clusters == null) {
            throw new NullPointerException("The cluster set cannot be null");
        }
        if (comparator == null) {
            throw new NullPointerException("The comparator cannot be null");
        }
        SortedSet<Cluster<?>> sortedSet = new TreeSet<Cluster<?>>(comparator);
        sortedSet.addAll(clusters);
        this.clusters = sortedSet.toArray(new Cluster[clusters.size()]);
        statistics = new ClusterStatistics<?>[this.clusters.length];
        for (int i = 0; i < this.clusters.length; i++) {
            Cluster<?> cluster = this.clusters[i];
            statistics[i] = ClusterStatistics.buildStatistics(cluster);
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
    @Override
    public void addTableModelListener(final TableModelListener l) {
        if (l != null) {
            listeners.add(l);
        }
    }


    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        return Object.class;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        Object toReturn = null;
        if (columnIndex == 0) {
            double avgSize = avg(new Selector() {
                @Override
                public double select(final ClusterStatistics<?> s) {
                    return s.getCluster().size();
                }
            });
            String totalString = String.format("TOTAL %d Average size: %f ",
                    clusters.length, avgSize);
            toReturn = rowIndex >= clusters.length ? totalString : clusters[rowIndex];
        } else {
            ClusterStatistics<?> clusterStatistics = rowIndex >= statistics.length ? null
                    : statistics[rowIndex];
            if (clusterStatistics == null) {
                return null;
            }
            switch (columnIndex) {
                case 1:
                    toReturn = rowIndex >= statistics.length ? avg(new Selector() {
                        @Override
                        public double select(final ClusterStatistics<?> s) {
                            return s.getAverageInternalDistance();
                        }
                    }) : clusterStatistics.getAverageInternalDistance();
                    break;
                case 2:
                    toReturn = rowIndex >= statistics.length ? avg(new Selector() {
                        @Override
                        public double select(final ClusterStatistics<?> s) {
                            return s.getMinInternalDistance();
                        }
                    }) : clusterStatistics.getMinInternalDistance();
                    break;
                case 3:
                    toReturn = rowIndex >= statistics.length ? avg(new Selector() {
                        @Override
                        public double select(final ClusterStatistics<?> s) {
                            return s.getMaxInternalDistance();
                        }
                    }) : clusterStatistics.getMaxInternalDistance();
                    break;
                default:
                    break;
            }
        }
        return toReturn;
    }

    @Override
    public String getColumnName(final int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

    @Override
    public int getRowCount() {
        return clusters.length + 1;
    }


    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return false;
    }

    @Override
    public void removeTableModelListener(final TableModelListener l) {
        listeners.remove(l);
    }


    @Override
    public void
            setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {}

    /** @return the clusters */
    public Cluster<?>[] getClusters() {
        Cluster<?>[] toReturn = new Cluster[clusters.length];
        System.arraycopy(clusters, 0, toReturn, 0, clusters.length);
        return toReturn;
    }

    private double avg(final Selector selector) {
        double toReturn = 0;
        if (statistics.length > 0) {
            for (ClusterStatistics<?> s : statistics) {
                toReturn += selector.select(s);
            }
            toReturn = toReturn / statistics.length;
        }
        return toReturn;
    }
}