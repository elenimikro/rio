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

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.coode.distance.Distance;
import org.coode.distance.SparseMatrix;
import org.coode.distance.TableDistance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.pair.Pair;
import org.coode.pair.filter.PairFilter;
import org.coode.pair.filter.commons.DistanceThresholdBasedFilter;
import org.coode.proximitymatrix.CentroidProximityMeasureFactory;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.ProximityMatrix;
import org.coode.proximitymatrix.ProximityMatrixUtils;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;
import org.coode.proximitymatrix.cluster.SimpleCluster;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.ui.GlassPane;
import org.coode.utils.owl.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLAPIStreamUtils;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.w3c.dom.Document;

/** @author Luigi Iannone */
public class ClusteringGUI extends JFrame {
    private final class Reducer extends
        SwingWorker<ClusteringProximityMatrix<OWLEntity>, ClusteringProximityMatrix<OWLEntity>> {
        public Reducer() {}

        @Override
        protected ClusteringProximityMatrix<OWLEntity> doInBackground() {
            return clusteringMatrix.reduce(filter);
        }

        @Override
        protected void done() {
            try {
                clusteringMatrix = this.get();
                updateGUI();
            } catch (InterruptedException e) {
                JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
                e.printStackTrace();
            } catch (ExecutionException e) {
                JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private abstract class Agglomerator extends
        SwingWorker<ClusteringProximityMatrix<OWLEntity>, ClusteringProximityMatrix<OWLEntity>> {
        private final ClusteringProximityMatrix<OWLEntity> start;
        private final PairFilter<Collection<? extends OWLEntity>> pairFilter;
        private int count = 0;

        /**
         * @param start start
         * @param filter filter
         */
        public Agglomerator(ClusteringProximityMatrix<OWLEntity> start,
            PairFilter<Collection<? extends OWLEntity>> filter) {
            if (start == null) {
                throw new NullPointerException("The start cannot be null");
            }
            if (filter == null) {
                throw new NullPointerException("The pairFilter cannot be null");
            }
            this.start = start;
            pairFilter = filter;
        }

        protected abstract boolean stop(ClusteringProximityMatrix<OWLEntity> matrix);

        @Override
        protected ClusteringProximityMatrix<OWLEntity> doInBackground() {
            ClusteringProximityMatrix<OWLEntity> toReturn = getStart();
            do {
                toReturn = toReturn.agglomerate(getFilter());
                count++;
                process(Collections.singletonList(toReturn));
            } while (!stop(toReturn));
            return toReturn;
        }

        @Override
        protected void process(List<ClusteringProximityMatrix<OWLEntity>> chunks) {
            if (!chunks.isEmpty()) {
                int clusterCount = 0;
                for (ClusteringProximityMatrix<OWLEntity> clusteringProximityMatrix : chunks) {
                    clusterCount = clusteringProximityMatrix.getData().length();
                }
                glassPane.setMessage(String.format("%d agglomerations so far %d clusters",
                    Integer.valueOf(count), Integer.valueOf(clusterCount)));
            }
        }

        @Override
        protected void done() {
            try {
                ClusteringProximityMatrix<OWLEntity> clusteringProximityMatrix = this.get();
                clusteringMatrix = clusteringProximityMatrix;
                updateGUI();
            } catch (InterruptedException e) {
                JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
                e.printStackTrace();
            } catch (ExecutionException e) {
                JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
                e.printStackTrace();
            }
        }

        /** @return the start */
        public ClusteringProximityMatrix<OWLEntity> getStart() {
            return start;
        }

        /** @return the pairFilter */
        public PairFilter<Collection<? extends OWLEntity>> getFilter() {
            return pairFilter;
        }
    }

    private class SaveClustering extends AbstractAction {
        private static final long serialVersionUID = 7419620562572349981L;

        /** default constructor */
        public SaveClustering() {
            super("Save Clusters");
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            FileDialog dialog = new FileDialog(ClusteringGUI.this, "Save", FileDialog.SAVE);
            dialog.setVisible(true);
            String fileName = dialog.getFile();
            if (fileName != null) {
                try {
                    List<OWLOntology> ontologeis = asList(manager.ontologies());
                    OWLOntology ontology = ontologeis.get(0);
                    OPPLFactory factory = new OPPLFactory(manager, ontology, null);
                    ConstraintSystem constraintSystem = factory.createConstraintSystem();
                    SortedSet<Cluster<OWLEntity>> sortedClusters =
                        new TreeSet<>(ClusterStatisticsTableModel.SIZE_COMPARATOR);
                    sortedClusters.addAll(buildClusters());
                    OWLObjectGeneralisation generalisation = Utils
                        .getOWLObjectGeneralisation(sortedClusters, ontologeis, constraintSystem);
                    Document xml = Utils.toXML(sortedClusters, ontologeis,
                        new ManchesterOWLSyntaxOWLObjectRendererImpl(), generalisation);
                    Transformer t = TransformerFactory.newInstance().newTransformer();
                    StreamResult result =
                        new StreamResult(new File(new File(dialog.getDirectory()), fileName));
                    t.setOutputProperty(OutputKeys.INDENT, "yes");
                    t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
                    t.transform(new DOMSource(xml), result);
                } catch (ParserConfigurationException e) {
                    JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
                    e.printStackTrace();
                } catch (TransformerConfigurationException e) {
                    JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
                    e.printStackTrace();
                } catch (TransformerFactoryConfigurationError e) {
                    JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
                    e.printStackTrace();
                } catch (TransformerException e) {
                    JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
                    e.printStackTrace();
                } catch (OPPLException e) {
                    JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private class SaveAction extends AbstractAction {
        public SaveAction() {
            super("Save");
        }

        private static final long serialVersionUID = -6759993806728785589L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            FileDialog dialog = new FileDialog(ClusteringGUI.this, "Save", FileDialog.SAVE);
            dialog.setVisible(true);
            String fileName = dialog.getFile();
            if (fileName != null) {
                Set<Cluster<OWLEntity>> clusters = buildClusters();
                File file = new File(new File(dialog.getDirectory()), fileName);
                try (PrintWriter out = new PrintWriter(file);) {
                    ProximityMatrixUtils.printClusters(clusters, out);
                    SparseMatrix data = clusteringMatrix.getData();
                    ProximityMatrixUtils.printProximityMatrix(data, clusters, out);
                    data = distanceMatrix.getData();
                    ProximityMatrixUtils.printDistanceMatrix(out, data, distanceMatrix);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
                }
            }
        }
    }

    private class ClusteringTableCellRenderer implements TableCellRenderer {
        public ClusteringTableCellRenderer() {}

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
            Pair<Collection<? extends OWLEntity>> minimumDistancePair =
                clusteringMatrix.getMinimumDistancePair();
            if (minimumDistancePair != null && table == proximityTable) {
                int[] rows = clusteringMatrix.getRows(minimumDistancePair);
                for (int i = 0; i < rows.length; i++) {
                    if (rows[i] == row) {
                        defaultTableCellRenderer.setForeground(Color.RED);
                    }
                }
            }
            String rendering = value == null ? "" : value.toString();
            if (value instanceof Set<?>) {
                Set<String> members = new HashSet<>(((Set<?>) value).size());
                for (Object object : (Set<?>) value) {
                    members.add(render(object));
                }
                rendering = String.format(" Cluster %d) %s, size %d", Integer.valueOf(row + 1),
                    members.toString(), Integer.valueOf(members.size()));
            }
            Component toReturn = defaultTableCellRenderer.getTableCellRendererComponent(table,
                rendering, isSelected, hasFocus, row, column);
            return toReturn;
        }

        protected String render(Object object) {
            String toReturn = object.toString();
            if (object instanceof OWLEntity) {
                toReturn = shortFormProvider.getShortForm((OWLEntity) object);
            }
            return toReturn;
        }
    }

    private static final long serialVersionUID = 3154241412745213737L;
    protected final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    protected final ClusterSummaryPanel<OWLEntity> clusterSummaryPanel =
        ClusterSummaryPanel.buildOWLEntityClusterSummaryPanel(new SimpleShortFormProvider());
    protected ClusterAxiomPanel<OWLEntity> clusterAxiomPanel;
    protected final JTable proximityTable = new JTable();
    protected final JTable clusterStatisticsTable = new JTable();
    protected ClusteringProximityMatrix<OWLEntity> clusteringMatrix = null;
    protected final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
    protected final JButton reduceButton = new JButton("Reduce");
    protected final JButton agglomerateButton = new JButton("Agglomerate");
    protected final JButton agglomerateAllZerosButton = new JButton("Agglomerate All Zeros");
    protected final JButton agglomerateAllButton = new JButton("Agglomerate All");
    protected final GlassPane glassPane = new GlassPane();
    protected final Action saveAction = new SaveAction();
    protected final Action saveClusteringAction = new SaveClustering();
    protected final ClusteringTableCellRenderer clusteringTableCellRenderer =
        new ClusteringTableCellRenderer();
    protected ProximityMatrix<OWLEntity> distanceMatrix;
    protected PairFilter<Collection<? extends OWLEntity>> filter;

    /**
     * @param iris iris
     */
    public ClusteringGUI(Collection<? extends IRI> iris) {
        if (iris == null) {
            throw new NullPointerException("The IRI collection cannot be null");
        }
        try {
            Collection<IRI> collection = new ArrayList<>(iris);
            IOUtils.loadIRIMappers(collection, manager);
        } catch (OWLOntologyCreationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error in loading ontology",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        System.out.println(
            String.format("Loaded %d ontologies ", Long.valueOf(manager.ontologies().count())));
        reset();
        initGUI();
    }

    private void reset() {
        Set<OWLEntity> entities = new TreeSet<>((o1, o2) -> shortFormProvider.getShortForm(o1)
            .compareTo(shortFormProvider.getShortForm(o2)));
        OWLAPIStreamUtils.add(entities, manager.ontologies().flatMap(OWLOntology::signature));
        System.out.println(String.format("Computing distance between %d entities ...",
            Integer.valueOf(entities.size())));
        final OWLEntityReplacer owlEntityReplacer =
            new OWLEntityReplacer(manager.getOWLDataFactory(),
                new ReplacementByKindStrategy(manager.getOWLDataFactory()));
        final Distance<OWLEntity> distance =
            new AxiomRelevanceAxiomBasedDistance(manager.ontologies(), owlEntityReplacer, manager);
        distanceMatrix = new SimpleProximityMatrix<>(entities, distance);
        System.out.println(String.format("Computing distance between %d entities finished",
            Integer.valueOf(entities.size())));
        Set<Collection<? extends OWLEntity>> newObjects = new LinkedHashSet<>();
        for (OWLEntity object : distanceMatrix.getObjects()) {
            newObjects.add(Collections.singleton(object));
        }
        Distance<Collection<? extends OWLEntity>> singletonDistance =
            (a, b) -> distance.getDistance(a.iterator().next(), b.iterator().next());
        filter = DistanceThresholdBasedFilter
            .build(new TableDistance<>(entities, distanceMatrix.getData()), 1);
        clusteringMatrix =
            ClusteringProximityMatrix.build(distanceMatrix, new CentroidProximityMeasureFactory(),
                filter, PairFilterBasedComparator.build(filter, newObjects, singletonDistance));
        updateGUI();
        reduceButton.addActionListener(e -> {
            Reducer reducer = new Reducer();
            glassPane.setMessage("Reducing...");
            glassPane.setVisible(true);
            reducer.execute();
        });
        agglomerateButton.addActionListener(e -> {
            Agglomerator agglomerator = new Agglomerator(clusteringMatrix, filter) {
                @Override
                protected boolean stop(ClusteringProximityMatrix<OWLEntity> matrix) {
                    return true;
                }
            };
            glassPane.setMessage("Agglomerating...");
            glassPane.setVisible(true);
            agglomerator.execute();
        });
        agglomerateAllZerosButton.addActionListener(e -> {
            Agglomerator agglomerator = new Agglomerator(clusteringMatrix, filter) {
                @Override
                protected boolean stop(ClusteringProximityMatrix<OWLEntity> matrix) {
                    return matrix.getMinimumDistancePair() == null
                        || matrix.getMinimumDistance() > 0;
                }
            };
            glassPane.setMessage("Agglomerating...");
            glassPane.setVisible(true);
            agglomerator.execute();
        });
        agglomerateAllButton.addActionListener(e -> {
            Agglomerator agglomerator = new Agglomerator(clusteringMatrix, filter) {
                @Override
                protected boolean stop(ClusteringProximityMatrix<OWLEntity> matrix) {
                    return matrix.getMinimumDistancePair() == null
                        || !getFilter().accept(matrix.getMinimumDistancePair().getFirst(),
                            matrix.getMinimumDistancePair().getSecond());
                }
            };
            glassPane.setMessage("Agglomerating...");
            glassPane.setVisible(true);
            agglomerator.execute();
        });
    }

    protected void updateGUI() {
        List<String> columnNames = getColumnNames(clusteringMatrix.getObjects());
        proximityTable.setModel(new ProximityMatrixTableModel(clusteringMatrix,
            columnNames.toArray(new String[columnNames.size()])));
        clusterStatisticsTable.setModel(new ClusterStatisticsTableModel(buildClusters()));
        proximityTable.getColumn("*")
            .setCellRenderer(ClusteringGUI.this.clusteringTableCellRenderer);
        clusterStatisticsTable.getColumn("Cluster").setCellRenderer(clusteringTableCellRenderer);
        agglomerateButton
            .setEnabled(ClusteringGUI.this.clusteringMatrix.getMinimumDistancePair() != null);
        agglomerateAllZerosButton.setEnabled(clusteringMatrix.getMinimumDistance() == 0);
        agglomerateAllButton
            .setEnabled(ClusteringGUI.this.clusteringMatrix.getMinimumDistancePair() != null);
        glassPane.setVisible(false);
    }

    protected Set<Cluster<OWLEntity>> buildClusters() {
        Collection<Collection<? extends OWLEntity>> objects = clusteringMatrix.getObjects();
        Set<Cluster<OWLEntity>> toReturn = new HashSet<>(objects.size());
        for (Collection<? extends OWLEntity> collection : objects) {
            toReturn.add(new SimpleCluster<>(collection, distanceMatrix));
        }
        return toReturn;
    }

    private void initGUI() {
        setLayout(new BorderLayout());
        JSplitPane mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainPanel.setResizeWeight(.5);
        mainPanel.setDividerLocation(.5);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu menu = new JMenu();
        menuBar.add(menu);
        menu.setText("File");
        JMenuItem saveMenuItem = new JMenuItem(saveAction);
        JMenuItem saveClustersMenuItem = new JMenuItem(saveClusteringAction);
        menu.add(saveMenuItem);
        menu.add(saveClustersMenuItem);
        setGlassPane(glassPane);
        JSplitPane clusteringPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        proximityTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        proximityTable.getColumn("*").setCellRenderer(clusteringTableCellRenderer);
        clusterStatisticsTable.getColumn("Cluster").setCellRenderer(clusteringTableCellRenderer);
        JPanel rightPane = new JPanel(new BorderLayout());
        rightPane.add(new JScrollPane(proximityTable), BorderLayout.CENTER);
        clusteringPane.setLeftComponent(new JScrollPane(clusterStatisticsTable));
        clusteringPane.setRightComponent(rightPane);
        clusterAxiomPanel = ClusterAxiomPanel.build(new ManchesterOWLSyntaxOWLObjectRendererImpl());
        JSplitPane clusterPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        clusterPanel.setResizeWeight(.5);
        clusterPanel.setDividerLocation(.5);
        clusterPanel.setLeftComponent(clusterSummaryPanel);
        clusterPanel.setRightComponent(clusterAxiomPanel);
        mainPanel.setTopComponent(clusterPanel);
        mainPanel.setBottomComponent(clusteringPane);
        this.add(mainPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(reduceButton);
        buttonPanel.add(agglomerateButton);
        buttonPanel.add(agglomerateAllZerosButton);
        buttonPanel.add(agglomerateAllButton);
        this.add(buttonPanel, BorderLayout.SOUTH);
        clusterStatisticsTable.getSelectionModel()
            .addListSelectionListener(new ListSelectionListener() {
                @Override
                @SuppressWarnings("unchecked")
                public void valueChanged(ListSelectionEvent e) {
                    int selectedRow = clusterStatisticsTable.getSelectedRow();
                    Object valueAt = selectedRow == -1 ? null
                        : clusterStatisticsTable.getModel().getValueAt(selectedRow, 0);
                    if (valueAt instanceof Cluster<?>) {
                        clusterSummaryPanel.setCluster((Cluster<OWLEntity>) valueAt);
                        OWLObjectGeneralisation generalisation;
                        try {
                            List<OWLOntology> ontologies = asList(manager.ontologies());
                            OWLOntology ontology = ontologies.get(0);
                            OPPLFactory factory = new OPPLFactory(manager, ontology, null);
                            ConstraintSystem constraintSystem = factory.createConstraintSystem();
                            SortedSet<Cluster<OWLEntity>> sortedClusters =
                                new TreeSet<>(ClusterStatisticsTableModel.SIZE_COMPARATOR);
                            sortedClusters.addAll(ClusteringGUI.this.buildClusters());
                            generalisation = Utils.getOWLObjectGeneralisation(sortedClusters,
                                ontologies, constraintSystem);
                            clusterAxiomPanel.setCluster((Cluster<OWLEntity>) valueAt, ontologies,
                                generalisation);
                        } catch (Exception exception) {
                            JOptionPane.showMessageDialog(ClusteringGUI.this,
                                exception.getMessage());
                            exception.printStackTrace();
                        }
                    }
                }
            });
    }

    /**
     * @param objects objects
     * @return column names
     */
    public List<String> getColumnNames(
        Collection<? extends Collection<? extends OWLEntity>> objects) {
        List<String> columnNames = new ArrayList<>(objects.size());
        columnNames.add("*");
        for (Collection<? extends OWLEntity> entities : objects) {
            Set<String> objectNames = new HashSet<>(entities.size());
            for (OWLEntity owlEntity : entities) {
                objectNames.add(ClusteringGUI.this.shortFormProvider.getShortForm(owlEntity));
            }
            columnNames.add(objectNames.toString());
        }
        return columnNames;
    }

    /**
     * @param args args
     */
    public static void main(String[] args) {
        List<IRI> iris = new ArrayList<>(args.length);
        for (String string : args) {
            if (string.startsWith("http")) {
                IRI iri = IRI.create(string);
                iris.add(iri);
            } else {
                IRI iri = IRI.create(new File(string));
                iris.add(iri);
            }
        }
        ClusteringGUI frame = new ClusteringGUI(iris);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
