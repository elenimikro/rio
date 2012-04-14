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
package org.coode.proximitymatrix.ui;

import java.util.Formatter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.coode.proximitymatrix.cluster.Cluster;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.ShortFormProvider;

public class ClusterSummaryPanel<O> extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3944683574925844955L;
	private Cluster<O> cluster = null;
	private final JTextPane clusterMembers = new JTextPane();
	private final JLabel clusterSize = new JLabel("Size: ");

	public ClusterSummaryPanel() {
		this.initGUI();
	}

	private void initGUI() {
		this.clusterMembers.setOpaque(false);
		this.add(this.clusterSize);
		this.add(this.clusterMembers);
		this.reset();
	}

	private void reset() {
		this.clusterMembers.setText("Members: ");
		this.clusterSize.setText("Size: ");
		if (this.getCluster() != null) {
			Formatter formatter = new Formatter();
			boolean first = true;
			int i = 0;
			for (O member : this.getCluster()) {
				String comma = first ? "" : ", ";
				String newLine = i != 0 && i % 5 == 0 ? "\n" : "";
				formatter.format("%s%s%s", comma, newLine, this.render(member));
				first = false;
				i++;
			}
			this.clusterMembers.setText(String.format("%s %s",
					this.clusterMembers.getText(), formatter.toString()));
			this.clusterSize.setText(String.format("%s %d", this.clusterSize.getText(),
					this.getCluster().size()));
		}
	}

	protected String render(O member) {
		return member.toString();
	}

	/**
	 * @return the cluster
	 */
	public Cluster<O> getCluster() {
		return this.cluster;
	}

	/**
	 * @param cluster
	 *            the cluster to set
	 */
	public void setCluster(Cluster<O> cluster) {
		this.cluster = cluster;
		this.reset();
	}

	public static ClusterSummaryPanel<OWLEntity> buildOWLEntityClusterSummaryPanel(
			final ShortFormProvider shortFormProvider) {
		return new ClusterSummaryPanel<OWLEntity>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7324747611311810954L;

			@Override
			protected String render(OWLEntity member) {
				return shortFormProvider.getShortForm(member);
			}
		};
	}
}
