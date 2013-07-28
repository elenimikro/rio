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
import java.util.Arrays;
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
import org.semanticweb.owlapi.model.OWLEntity;

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
    private final RelevancePolicy policy;
	private final JLabel summaryLabel = new JLabel();

    public RelevancePolicyPanel(RelevancePolicy policy) {
		if (policy == null) {
			throw new NullPointerException("The policy cannot be null");
		}
		this.policy = policy;
		this.initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		this.add(this.summaryLabel, BorderLayout.NORTH);
		this.add(new JScrollPane(this.rankingList), BorderLayout.CENTER);
		this.rankingList.setCellRenderer(new ListCellRenderer() {
			@Override
            public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				DefaultListCellRenderer renderer = new DefaultListCellRenderer();
                final HashSet<OWLEntity> members = new HashSet<OWLEntity>(Arrays
                        .asList(((RankingSlot<OWLEntity>) value).getMembers()));
                Object toRender = value instanceof RankingSlot<?> ? String.format(
						"%s [%s] relevant? %b",
                        RelevancePolicyPanel.this.render(members),
                        ((RankingSlot<?>) value).getValue(),
						RelevancePolicyPanel.this.getPolicy().isRelevant(
                                ((RankingSlot<OWLEntity>) value).getMembers()[0]))
                        : value;
				Component toReturn = renderer.getListCellRendererComponent(list,
						String.format("%d) %s ", index + 1, toRender), index, isSelected,
						cellHasFocus);
				return toReturn;
			}
		});
		this.summaryLabel.setText(this.getPolicy().toString());
	}

	protected abstract Object render(Set<?> members);

    public <P> void reset(Ranking ranking) {
        List<? extends RankingSlot<OWLEntity>> list = ranking.getSortedRanking();
		DefaultListModel model = new DefaultListModel();
        for (RankingSlot<OWLEntity> rankingSlot : list) {
			model.addElement(rankingSlot);
		}
		this.rankingList.setModel(model);
	}

	/**
	 * @return the policy
	 */
    public RelevancePolicy getPolicy() {
		return this.policy;
	}
}
