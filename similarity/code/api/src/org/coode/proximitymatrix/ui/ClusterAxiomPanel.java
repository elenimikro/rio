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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import org.coode.oppl.Variable;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Luigi Iannone
 * 
 */
public class ClusterAxiomPanel<O extends OWLEntity> extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8706696433509939648L;
	private final JList axiomList = new JList();
	private final JList variableList = new JList();
	private final JLabel summaryLabel = new JLabel();
	private OWLObjectGeneralisation generalisation;

	public ClusterAxiomPanel() {
		this.initGUI();
		this.reset(null, Collections.<OWLOntology> emptySet());
	}

	private void reset(Cluster<O> cluster, Collection<? extends OWLOntology> ontologies) {
		if (cluster != null) {
			ClusterAxiomListModel model = new ClusterAxiomListModel(cluster, ontologies,
					this.getGeneralisation(),
					new ShowMessageRuntimeExceptionHandler(this));
			this.axiomList.setModel(model);
			String string = String.format("Axiom count: %d", model.getAxiomCount());
			if (this.getGeneralisation() != null) {
				Comparator<Variable<?>> comparator = new Comparator<Variable<?>>() {
					public int compare(Variable<?> o1, Variable<?> o2) {
						return o1.getName().compareTo(o2.getName());
					}
				};
				Set<Variable<?>> variables = new TreeSet<Variable<?>>(comparator);
				variables.addAll(this.getGeneralisation().getConstraintSystem()
						.getInputVariables());
				DefaultListModel defaultListModel = new DefaultListModel();
				for (Variable<?> variable : variables) {
					defaultListModel.addElement(variable);
				}
				variables.clear();
				variables.addAll(this.getGeneralisation().getConstraintSystem()
						.getGeneratedVariables());
				for (Variable<?> variable : variables) {
					defaultListModel.addElement(variable);
				}
				this.variableList.setModel(defaultListModel);
			}
			this.summaryLabel.setText(string);
		} else {
			this.axiomList.setModel(new DefaultListModel());
			this.summaryLabel.setText("Axiom count:");
		}
	}

	private void initGUI() {
		this.setLayout(new BorderLayout());
		this.add(this.summaryLabel, BorderLayout.NORTH);
		this.add(new JScrollPane(this.variableList), BorderLayout.WEST);
		this.add(new JScrollPane(this.axiomList), BorderLayout.CENTER);
		this.axiomList.setCellRenderer(new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
				OWLAxiomListItem owlAxiomListItem = (OWLAxiomListItem) value;
				Object toRender = value instanceof OWLAxiomListItem ? String.format(
						"%s [%d] %s", ClusterAxiomPanel.this.render(owlAxiomListItem
								.getAxiom()), owlAxiomListItem.getCount(), Utils
								.renderInstantiationsStats(Utils
										.buildAssignmentMap(owlAxiomListItem
												.getInstantiations()))) : value;
				return defaultListCellRenderer.getListCellRendererComponent(list,
						toRender, index, isSelected, cellHasFocus);
			}
		});
		this.variableList.setCellRenderer(new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
				Object toRender = value instanceof Variable<?>
						&& ClusterAxiomPanel.this.getGeneralisation() != null ? ((Variable<?>) value)
						.render(ClusterAxiomPanel.this.getGeneralisation()
								.getConstraintSystem()) : value;
				return defaultListCellRenderer.getListCellRendererComponent(list,
						toRender, index, isSelected, cellHasFocus);
			}
		});
	}

	protected String render(OWLAxiom axiom) {
		String toReturn = axiom.toString();
		return toReturn;
	}

	/**
	 * @param cluster
	 *            the cluster to set
	 */
	public void setCluster(Cluster<O> cluster,
			Collection<? extends OWLOntology> ontologies,
			OWLObjectGeneralisation generalisation) {
		this.setGeneralisation(generalisation);
		this.reset(cluster, ontologies);
	}

	public static <P extends OWLEntity> ClusterAxiomPanel<P> build(
			final OWLObjectRenderer renderer) {
		return new ClusterAxiomPanel<P>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 5936336750441629116L;

			@Override
			protected String render(OWLAxiom axiom) {
				String toReturn = renderer.render(axiom);
				return toReturn;
			}
		};
	}

	/**
	 * @return the generalisation
	 */
	public OWLObjectGeneralisation getGeneralisation() {
		return this.generalisation;
	}

	/**
	 * @param generalisation
	 *            the generalisation to set
	 */
	public void setGeneralisation(OWLObjectGeneralisation generalisation) {
		this.generalisation = generalisation;
	}
}
