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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import org.coode.distance.Distance;
import org.coode.distance.entityrelevance.DefaultOWLEntityRelevancePolicy;
import org.coode.distance.owl.AxiomBasedDistance;
import org.coode.proximitymatrix.ProximityMatrix;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.utils.EntityComparator;
import org.coode.utils.owl.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

/** @author Luigi Iannone */
public class ProximityMatrixGUI extends JFrame {
    private static final long serialVersionUID = 3154241412745213737L;
    private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private final JTable table = new JTable();
    private final JList<Collection<? extends OWLEntity>> clusterList = new JList<Collection<? extends OWLEntity>>();

    /** @param iris
     *            iris */
    public ProximityMatrixGUI(Collection<? extends IRI> iris) {
        if (iris == null) {
            throw new NullPointerException("The IRI collection cannot be null");
        }
        try {
            Collection<IRI> collection = new ArrayList<IRI>(iris);
            IOUtils.loadIRIMappers(collection, manager);
        } catch (OWLOntologyCreationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error in loading ontology", JOptionPane.ERROR_MESSAGE);
        }
        reset();
        initGUI();
    }

    private void reset() {
        SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        Set<OWLEntity> entities = new TreeSet<OWLEntity>(new EntityComparator());
        for (OWLOntology ontology : manager.getOntologies()) {
            entities.addAll(ontology.getSignature());
        }
        Distance<OWLEntity> distance = new AxiomBasedDistance(manager.getOntologies(),
                manager.getOWLDataFactory(),
                DefaultOWLEntityRelevancePolicy.getAlwaysIrrelevantPolicy(), manager);
        ProximityMatrix<OWLEntity> matrix = new SimpleProximityMatrix<OWLEntity>(
                entities, distance);
        Collection<OWLEntity> objects = matrix.getObjects();
        List<String> columnNames = new ArrayList<String>(objects.size());
        columnNames.add("*");
        for (OWLEntity owlEntity : objects) {
            columnNames.add(shortFormProvider.getShortForm(owlEntity));
        }
        Set<Set<OWLEntity>> clusters = new HashSet<Set<OWLEntity>>(objects.size());
        for (OWLEntity owlEntity : objects) {
            clusters.add(Collections.singleton(owlEntity));
        }
        table.setModel(new ProximityMatrixTableModel(matrix, columnNames
                .toArray(new String[columnNames.size()])));
        clusterList.setModel(new ClusterListModel<OWLEntity>(clusters));
    }

    private void initGUI() {
        setLayout(new BorderLayout());
        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        clusterList.setCellRenderer(new ClusterListOWLObjectCellRenderer(
                new SimpleShortFormProvider()));
        mainPane.setLeftComponent(new JScrollPane(clusterList));
        mainPane.setRightComponent(new JScrollPane(table));
        mainPane.setResizeWeight(.5);
        mainPane.setDividerLocation(.5);
        this.add(mainPane, BorderLayout.CENTER);
    }

    /** @param args
     *            args */
    public static void main(String[] args) {
        List<IRI> iris = new ArrayList<IRI>(args.length);
        for (String string : args) {
            iris.add(IRI.create(string));
        }
        ProximityMatrixGUI frame = new ProximityMatrixGUI(iris);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}