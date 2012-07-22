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

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import org.coode.distance.entityrelevance.RelevancePolicy;
import org.coode.metrics.Ranking;
import org.coode.metrics.RankingSlot;

/**
 * @author Luigi Iannone
 *
 */
public abstract class RelevancePolicyPanel<O> extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 8159023598180517334L;
	private final JList rankingList = new JList();
	private final RelevancePolicy<O> policy;
	private final JLabel summaryLabel = new JLabel();

	public RelevancePolicyPanel(RelevancePolicy<O> policy) {
		if (policy == null) {
			throw new NullPointerException("The policy cannot be null");
		}
		this.policy = policy;
		this.initGUI();
	}

	private void initGUI() {
		this.setLayout(new BorderLayout());
		this.add(this.summaryLabel, BorderLayout.NORTH);
		this.add(new JScrollPane(this.rankingList), BorderLayout.CENTER);
		this.rankingList.setCellRenderer(new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				DefaultListCellRenderer renderer = new DefaultListCellRenderer();
				Object toRender = value instanceof RankingSlot<?, ?> ? String.format(
						"%s [%s] relevant? %b",
						RelevancePolicyPanel.this.render(new HashSet<O>(((RankingSlot<O, ?>) value)
								.getMembers())),
						((RankingSlot<?, ?>) value).getValue(),
						RelevancePolicyPanel.this.getPolicy().isRelevant(
								((RankingSlot<O, ?>) value).getMembers().iterator()
										.next())) : value;
				Component toReturn = renderer.getListCellRendererComponent(list,
						String.format("%d) %s ", index + 1, toRender), index, isSelected,
						cellHasFocus);
				return toReturn;
			}
		});
		this.summaryLabel.setText(this.getPolicy().toString());
	}

	protected abstract Object render(Set<?> members);

	public <P> void reset(Ranking<O, ? extends Comparable<P>> ranking) {
		List<? extends RankingSlot<O, ?>> list = ranking.getSortedRanking();
		DefaultListModel model = new DefaultListModel();
		for (RankingSlot<O, ?> rankingSlot : list) {
			model.addElement(rankingSlot);
		}
		this.rankingList.setModel(model);
	}

	/**
	 * @return the policy
	 */
	public RelevancePolicy<O> getPolicy() {
		return this.policy;
	}
}
