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

import org.coode.oppl.Variable;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Luigi Iannone
 * @param <O> type
 */
public class ClusterAxiomPanel<O extends OWLEntity> extends JPanel {
    private static final long serialVersionUID = -8706696433509939648L;
    private final JList<OWLAxiomListItem> axiomList = new JList<>();
    private final JList<Variable<OWLObject>> variableList = new JList<>();
    private final JLabel summaryLabel = new JLabel();
    private OWLObjectGeneralisation generalisation;

    @SuppressWarnings("javadoc")
    public ClusterAxiomPanel() {
        this.initGUI();
        this.reset(null, Collections.<OWLOntology>emptySet());
    }

    private void reset(Cluster<O> cluster, Collection<OWLOntology> ontologies) {
        if (cluster != null) {
            ClusterAxiomListModel model = new ClusterAxiomListModel((Cluster<OWLEntity>) cluster,
                ontologies, this.getGeneralisation());
            this.axiomList.setModel(model);
            String string =
                String.format("Axiom count: %d", Integer.valueOf(model.getAxiomCount()));
            if (this.getGeneralisation() != null) {
                Comparator<Variable<?>> comparator =
                    (o1, o2) -> o1.getName().compareTo(o2.getName());
                Set<Variable<? extends OWLObject>> variables = new TreeSet<Variable<?>>(comparator);
                variables
                    .addAll(this.getGeneralisation().getConstraintSystem().getInputVariables());
                DefaultListModel<Variable<OWLObject>> defaultListModel = new DefaultListModel<>();
                for (Variable<? extends OWLObject> variable : variables) {
                    defaultListModel.addElement((Variable<OWLObject>) variable);
                }
                variables.clear();
                variables
                    .addAll(this.getGeneralisation().getConstraintSystem().getGeneratedVariables());
                for (Variable<?> variable : variables) {
                    defaultListModel.addElement((Variable<OWLObject>) variable);
                }
                this.variableList.setModel(defaultListModel);
            }
            this.summaryLabel.setText(string);
        } else {
            this.axiomList.setModel(new DefaultListModel<OWLAxiomListItem>());
            this.summaryLabel.setText("Axiom count:");
        }
    }

    private void initGUI() {
        setLayout(new BorderLayout());
        this.add(this.summaryLabel, BorderLayout.NORTH);
        this.add(new JScrollPane(this.variableList), BorderLayout.WEST);
        this.add(new JScrollPane(this.axiomList), BorderLayout.CENTER);
        this.axiomList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
            Object toRender =
                String.format("%s [%d] %s", ClusterAxiomPanel.this.render(value.getAxiom()),
                    Integer.valueOf(value.getCount()), Utils.renderInstantiationsStats(
                        Utils.buildAssignmentMap(value.getInstantiations())));
            return defaultListCellRenderer.getListCellRendererComponent(list, toRender, index,
                isSelected, cellHasFocus);
        });
        this.variableList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
            Object toRender = ClusterAxiomPanel.this.getGeneralisation() != null
                ? value.render(ClusterAxiomPanel.this.getGeneralisation().getConstraintSystem())
                : value;
            return defaultListCellRenderer.getListCellRendererComponent(list, toRender, index,
                isSelected, cellHasFocus);
        });
    }

    protected String render(OWLAxiom axiom) {
        String toReturn = axiom.toString();
        return toReturn;
    }

    /**
     * @param cluster the cluster to set
     * @param ontologies ontologies
     * @param generalisation generalisation
     */
    public void setCluster(Cluster<O> cluster, Collection<OWLOntology> ontologies,
        OWLObjectGeneralisation generalisation) {
        this.setGeneralisation(generalisation);
        this.reset(cluster, ontologies);
    }

    /**
     * @param renderer renderer
     * @param <P> type
     * @return cluster axiom panel
     */
    public static <P extends OWLEntity> ClusterAxiomPanel<P> build(
        final OWLObjectRenderer renderer) {
        return new ClusterAxiomPanel<P>() {
            private static final long serialVersionUID = 5936336750441629116L;

            @Override
            protected String render(OWLAxiom axiom) {
                String toReturn = renderer.render(axiom);
                return toReturn;
            }
        };
    }

    /** @return the generalisation */
    public OWLObjectGeneralisation getGeneralisation() {
        return this.generalisation;
    }

    /**
     * @param generalisation the generalisation to set
     */
    public void setGeneralisation(OWLObjectGeneralisation generalisation) {
        this.generalisation = generalisation;
    }
}
