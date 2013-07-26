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
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

import org.coode.basetest.TestHelper;
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
import org.coode.proximitymatrix.SimpleHistoryItemFactory;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;
import org.coode.proximitymatrix.cluster.SimpleCluster;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.ui.GlassPane;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.w3c.dom.Document;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

/** @author Luigi Iannone */

public class ClusteringGUI extends JFrame {
	private final class Reducer
			extends
			SwingWorker<ClusteringProximityMatrix<OWLEntity>, ClusteringProximityMatrix<OWLEntity>> {
		@Override
		protected ClusteringProximityMatrix<OWLEntity> doInBackground()
				throws Exception {
			return clusteringMatrix.reduce(filter);
		}

		@Override
		protected void done() {
			try {
				clusteringMatrix = this.get();
				updateGUI();
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(ClusteringGUI.this,
						e.getMessage());
				e.printStackTrace();
			} catch (ExecutionException e) {
				JOptionPane.showMessageDialog(ClusteringGUI.this,
						e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private abstract class Agglomerator
			extends
			SwingWorker<ClusteringProximityMatrix<OWLEntity>, ClusteringProximityMatrix<OWLEntity>> {
		private final ClusteringProximityMatrix<OWLEntity> start;
		private final PairFilter<Collection<? extends OWLEntity>> pairFilter;
		private int count = 0;

		/**
		 * @param start
		 * @param pairFilter
		 */
		public Agglomerator(final ClusteringProximityMatrix<OWLEntity> start,
				final PairFilter<Collection<? extends OWLEntity>> filter) {
			if (start == null) {
				throw new NullPointerException("The start cannot be null");
			}
			if (filter == null) {
				throw new NullPointerException("The pairFilter cannot be null");
			}
			this.start = start;
			pairFilter = filter;
		}

		protected abstract boolean stop(
				ClusteringProximityMatrix<OWLEntity> matrix);

		@Override
		protected ClusteringProximityMatrix<OWLEntity> doInBackground()
				throws Exception {
			ClusteringProximityMatrix<OWLEntity> toReturn = getStart();
			do {
				toReturn = toReturn.agglomerate(getFilter());
				count++;
				process(Arrays.asList(toReturn));
			} while (!stop(toReturn));
			return toReturn;
		}

		@Override
		protected void process(
				final List<ClusteringProximityMatrix<OWLEntity>> chunks) {
			if (!chunks.isEmpty()) {
				int clusterCount = 0;
				for (ClusteringProximityMatrix<OWLEntity> clusteringProximityMatrix : chunks) {
					clusterCount = clusteringProximityMatrix.getData().length();
				}
				glassPane.setMessage(String.format(
						"%d agglomerations so far %d clusters", count,
						clusterCount));
			}
		}

		@Override
		protected void done() {
			try {
				ClusteringProximityMatrix<OWLEntity> clusteringProximityMatrix = this
						.get();
				clusteringMatrix = clusteringProximityMatrix;
				updateGUI();
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(ClusteringGUI.this,
						e.getMessage());
				e.printStackTrace();
			} catch (ExecutionException e) {
				JOptionPane.showMessageDialog(ClusteringGUI.this,
						e.getMessage());
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

	private final class SaveClustering extends AbstractAction {
		/**
		 *
		 */
		private static final long serialVersionUID = 7419620562572349981L;

		/**
		 *
		 */
		public SaveClustering() {
			super("Save Clusters");
		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			FileDialog dialog = new FileDialog(ClusteringGUI.this, "Save",
					FileDialog.SAVE);
			dialog.setVisible(true);
			String fileName = dialog.getFile();
			if (fileName != null) {
				try {
					OWLOntology ontology = manager.getOntologies().iterator()
							.next();
					OPPLFactory factory = new OPPLFactory(manager, ontology,
							null);
					ConstraintSystem constraintSystem = factory
							.createConstraintSystem();
					SortedSet<Cluster<OWLEntity>> sortedClusters = new TreeSet<Cluster<OWLEntity>>(
							ClusterStatisticsTableModel.SIZE_COMPARATOR);
					sortedClusters.addAll(buildClusters());
					OWLObjectGeneralisation generalisation = Utils
							.getOWLObjectGeneralisation(sortedClusters,
									manager.getOntologies(), constraintSystem);
					Document xml = Utils.toXML(sortedClusters, manager
							.getOntologies(),
							new ManchesterOWLSyntaxOWLObjectRendererImpl(),
							generalisation,
							new ShowMessageRuntimeExceptionHandler(
									ClusteringGUI.this));
					Transformer t = TransformerFactory.newInstance()
							.newTransformer();
					StreamResult result = new StreamResult(new File(new File(
							dialog.getDirectory()), fileName));
					t.setOutputProperty(OutputKeys.INDENT, "yes");
					t.setOutputProperty(
							"{http://xml.apache.org/xslt}indent-amount", "3");
					t.transform(new DOMSource(xml), result);
				} catch (ParserConfigurationException e) {
					JOptionPane.showMessageDialog(ClusteringGUI.this,
							e.getMessage());
					e.printStackTrace();
				} catch (TransformerConfigurationException e) {
					JOptionPane.showMessageDialog(ClusteringGUI.this,
							e.getMessage());
					e.printStackTrace();
				} catch (TransformerFactoryConfigurationError e) {
					JOptionPane.showMessageDialog(ClusteringGUI.this,
							e.getMessage());
					e.printStackTrace();
				} catch (TransformerException e) {
					JOptionPane.showMessageDialog(ClusteringGUI.this,
							e.getMessage());
					e.printStackTrace();
				} catch (OPPLException e) {
					JOptionPane.showMessageDialog(ClusteringGUI.this,
							e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	private final class SaveAction extends AbstractAction {
		public SaveAction() {
			super("Save");
		}

		/**
		 *
		 */
		private static final long serialVersionUID = -6759993806728785589L;

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			FileDialog dialog = new FileDialog(ClusteringGUI.this, "Save",
					FileDialog.SAVE);
			dialog.setVisible(true);
			String fileName = dialog.getFile();
			if (fileName != null) {
				Set<Cluster<OWLEntity>> clusters = buildClusters();
				try {
					File file = new File(new File(dialog.getDirectory()),
							fileName);
					PrintWriter out = new PrintWriter(file);
					ProximityMatrixUtils.printClusters(clusters, out);
					SparseMatrix data = clusteringMatrix.getData();
					ProximityMatrixUtils.printProximityMatrix(data, clusters,
							out);

					data = distanceMatrix.getData();
					ProximityMatrixUtils.printDistanceMatrix(out, data,
							distanceMatrix);
					out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(ClusteringGUI.this,
							e.getMessage());
				}
			}
		}

		/**
		 * @param owlEntity
		 * @return
		 */
		private String renderOWLEntity(final OWLEntity owlEntity) {
			return shortFormProvider.getShortForm(owlEntity);
		}

		/**
		 * @param cluster
		 * @return
		 */
		private String renderCluster(final Cluster<OWLEntity> cluster) {
			Set<String> toReturn = new HashSet<String>(cluster.size());
			for (OWLEntity owlEntity : cluster) {
				toReturn.add(shortFormProvider.getShortForm(owlEntity));
			}
			return toReturn.toString();
		}
	}

	private final class ClusteringTableCellRenderer implements
			TableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(final JTable table,
				final Object value, final boolean isSelected,
				final boolean hasFocus, final int row, final int column) {
			DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
			Pair<Collection<? extends OWLEntity>> minimumDistancePair = clusteringMatrix
					.getMinimumDistancePair();
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
				Set<String> members = new HashSet<String>(
						((Set<?>) value).size());
				for (Object object : (Set<?>) value) {
					members.add(render(object));
				}
				rendering = String.format(" Cluster %d) %s, size %d", row + 1,
						members.toString(), members.size());
			}
			Component toReturn = defaultTableCellRenderer
					.getTableCellRendererComponent(table, rendering,
							isSelected, hasFocus, row, column);
			return toReturn;
		}

		protected String render(final Object object) {
			String toReturn = object.toString();
			if (object instanceof OWLEntity) {
				toReturn = shortFormProvider.getShortForm((OWLEntity) object);
			}
			return toReturn;
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 3154241412745213737L;
	private final OWLOntologyManager manager = OWLManager
			.createOWLOntologyManager();
	private final ClusterSummaryPanel<OWLEntity> clusterSummaryPanel = ClusterSummaryPanel
			.buildOWLEntityClusterSummaryPanel(new SimpleShortFormProvider());
	private ClusterAxiomPanel<OWLEntity> clusterAxiomPanel;
	private final JTable proximityTable = new JTable();
	final JTable clusterStatisticsTable = new JTable();
	ClusteringProximityMatrix<OWLEntity> clusteringMatrix = null;
	private final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
	private final JButton reduceButton = new JButton("Reduce");
	private final JButton agglomerateButton = new JButton("Agglomerate");
	private final JButton agglomerateAllZerosButton = new JButton(
			"Agglomerate All Zeros");
	private final JButton agglomerateAllButton = new JButton("Agglomerate All");
	private final GlassPane glassPane = new GlassPane();
	private final Action saveAction = new SaveAction();
	private final Action saveClusteringAction = new SaveClustering();
	private final ClusteringTableCellRenderer clusteringTableCellRenderer = new ClusteringTableCellRenderer();
	private ProximityMatrix<OWLEntity> distanceMatrix;
	PairFilter<Collection<? extends OWLEntity>> filter;

	public ClusteringGUI(final Collection<? extends IRI> iris) {
		if (iris == null) {
			throw new NullPointerException("The IRI collection cannot be null");
		}
		try {
			Collection<IRI> collection = new ArrayList<IRI>(iris);
			TestHelper.loadIRIMappers(collection, manager);
		} catch (OWLOntologyCreationException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(),
					"Error in loading ontology", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		System.out.println(String.format("Loaded %d ontologies ", manager
				.getOntologies().size()));
		reset();
		initGUI();
	}

	private void reset() {
		Set<OWLEntity> entities = new TreeSet<OWLEntity>(
				new Comparator<OWLEntity>() {
					@Override
					public int compare(final OWLEntity o1, final OWLEntity o2) {
						return shortFormProvider.getShortForm(o1).compareTo(
								shortFormProvider.getShortForm(o2));
					}
				});
		for (OWLOntology ontology : manager.getOntologies()) {
			entities.addAll(ontology.getSignature());
		}
		System.out.println(String.format(
				"Computing distance between %d entities ...", entities.size()));
		// ByKindOWLEntityPopularityBasedRelevantPolicy policy = new
		// ByKindOWLEntityPopularityBasedRelevantPolicy(
		// entities, this.manager.getOntologies());
		// final Distance<OWLEntity> distance = new AxiomBasedDistance(
		// this.manager.getOntologies(), this.manager.getOWLDataFactory(),
		// policy, this.manager);
		final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
				manager.getOWLDataFactory(), new ReplacementByKindStrategy(
						manager.getOWLDataFactory()));
		final Distance<OWLEntity> distance = new AxiomRelevanceAxiomBasedDistance(
				manager.getOntologies(), owlEntityReplacer, manager);
		// final Distance<OWLEntity> distance = new EditDistance(
		// this.manager.getOntologies(), this.manager.getOWLDataFactory(),
		// this.manager);
		distanceMatrix = new SimpleProximityMatrix<OWLEntity>(entities,
				distance);
		System.out.println(String.format(
				"Computing distance between %d entities finished",
				entities.size()));
		Set<Collection<? extends OWLEntity>> newObjects = new LinkedHashSet<Collection<? extends OWLEntity>>();
		for (OWLEntity object : distanceMatrix.getObjects()) {
			newObjects.add(Collections.singleton(object));
		}
		Distance<Collection<? extends OWLEntity>> singletonDistance = new Distance<Collection<? extends OWLEntity>>() {
			@Override
			public double getDistance(final Collection<? extends OWLEntity> a,
					final Collection<? extends OWLEntity> b) {
				return distance.getDistance(a.iterator().next(), b.iterator()
						.next());
			}
		};
		filter = DistanceThresholdBasedFilter
				.build(new TableDistance<OWLEntity>(entities, distanceMatrix
						.getData()), 1);
		clusteringMatrix = ClusteringProximityMatrix
				.build(distanceMatrix,
						new CentroidProximityMeasureFactory(),
						filter,
						PairFilterBasedComparator.build(filter, newObjects,
								singletonDistance),
						new SimpleHistoryItemFactory<Collection<? extends OWLEntity>>());
		updateGUI();
		reduceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				Reducer reducer = new Reducer();
				glassPane.setMessage("Reducing...");
				glassPane.setVisible(true);
				reducer.execute();
			}
		});
		agglomerateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				Agglomerator agglomerator = new Agglomerator(clusteringMatrix,
						filter) {
					@Override
					protected boolean stop(
							final ClusteringProximityMatrix<OWLEntity> matrix) {
						return true;
					}
				};
				glassPane.setMessage("Agglomerating...");
				glassPane.setVisible(true);
				agglomerator.execute();
			}
		});
		agglomerateAllZerosButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				Agglomerator agglomerator = new Agglomerator(clusteringMatrix,
						filter) {
					@Override
					protected boolean stop(
							final ClusteringProximityMatrix<OWLEntity> matrix) {
						return matrix.getMinimumDistancePair() == null
								|| matrix.getMinimumDistance() > 0;
					}
				};
				glassPane.setMessage("Agglomerating...");
				glassPane.setVisible(true);
				agglomerator.execute();
			}
		});
		agglomerateAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				Agglomerator agglomerator = new Agglomerator(clusteringMatrix,
						filter) {
					@Override
					protected boolean stop(
							final ClusteringProximityMatrix<OWLEntity> matrix) {
						return matrix.getMinimumDistancePair() == null
								|| !getFilter().accept(
										matrix.getMinimumDistancePair()
												.getFirst(),
										matrix.getMinimumDistancePair()
												.getSecond());
					}
				};
				glassPane.setMessage("Agglomerating...");
				glassPane.setVisible(true);
				agglomerator.execute();
			}
		});
	}

	private void updateGUI() {
		List<String> columnNames = getColumnNames(clusteringMatrix.getObjects());
		proximityTable.setModel(new ProximityMatrixTableModel(clusteringMatrix,
				columnNames.toArray(new String[columnNames.size()])));
		clusterStatisticsTable.setModel(new ClusterStatisticsTableModel(
				buildClusters()));
		proximityTable.getColumn("*").setCellRenderer(
				ClusteringGUI.this.clusteringTableCellRenderer);
		clusterStatisticsTable.getColumn("Cluster").setCellRenderer(
				clusteringTableCellRenderer);
		agglomerateButton.setEnabled(ClusteringGUI.this.clusteringMatrix
				.getMinimumDistancePair() != null);
		agglomerateAllZerosButton.setEnabled(clusteringMatrix
				.getMinimumDistance() == 0);
		agglomerateAllButton.setEnabled(ClusteringGUI.this.clusteringMatrix
				.getMinimumDistancePair() != null);
		glassPane.setVisible(false);
	}

	private Set<Cluster<OWLEntity>> buildClusters() {
		Collection<Collection<? extends OWLEntity>> objects = clusteringMatrix
				.getObjects();
		Set<Cluster<OWLEntity>> toReturn = new HashSet<Cluster<OWLEntity>>(
				objects.size());
		for (Collection<? extends OWLEntity> collection : objects) {
			toReturn.add(new SimpleCluster<OWLEntity>(collection,
					distanceMatrix));
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
		proximityTable.getColumn("*").setCellRenderer(
				clusteringTableCellRenderer);
		clusterStatisticsTable.getColumn("Cluster").setCellRenderer(
				clusteringTableCellRenderer);
		JPanel rightPane = new JPanel(new BorderLayout());
		rightPane.add(new JScrollPane(proximityTable), BorderLayout.CENTER);
		clusteringPane
				.setLeftComponent(new JScrollPane(clusterStatisticsTable));
		clusteringPane.setRightComponent(rightPane);
		clusterAxiomPanel = ClusterAxiomPanel
				.build(new ManchesterOWLSyntaxOWLObjectRendererImpl());
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
		clusterStatisticsTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					@SuppressWarnings("unchecked")
					public void valueChanged(final ListSelectionEvent e) {
						int selectedRow = clusterStatisticsTable
								.getSelectedRow();
						Object valueAt = selectedRow == -1 ? null
								: clusterStatisticsTable.getModel().getValueAt(
										selectedRow, 0);
						if (valueAt instanceof Cluster<?>) {
							clusterSummaryPanel
									.setCluster((Cluster<OWLEntity>) valueAt);
							OWLObjectGeneralisation generalisation;
							try {
								OWLOntology ontology = manager.getOntologies()
										.iterator().next();
								OPPLFactory factory = new OPPLFactory(manager,
										ontology, null);
								ConstraintSystem constraintSystem = factory
										.createConstraintSystem();
								SortedSet<Cluster<OWLEntity>> sortedClusters = new TreeSet<Cluster<OWLEntity>>(
										ClusterStatisticsTableModel.SIZE_COMPARATOR);
								sortedClusters.addAll(ClusteringGUI.this
										.buildClusters());
								generalisation = Utils
										.getOWLObjectGeneralisation(
												sortedClusters,
												manager.getOntologies(),
												constraintSystem);
								clusterAxiomPanel.setCluster(
										(Cluster<OWLEntity>) valueAt,
										manager.getOntologies(), generalisation);
							} catch (Exception exception) {
								JOptionPane.showMessageDialog(
										ClusteringGUI.this,
										exception.getMessage());
								exception.printStackTrace();
							}
						}
					}
				});
	}

	/**
	 * @param objects
	 * @return
	 */
	public List<String> getColumnNames(
			final Collection<? extends Collection<? extends OWLEntity>> objects) {
		List<String> columnNames = new ArrayList<String>(objects.size());
		columnNames.add("*");
		for (Collection<? extends OWLEntity> entities : objects) {
			Set<String> objectNames = new HashSet<String>(entities.size());
			for (OWLEntity owlEntity : entities) {
				objectNames.add(ClusteringGUI.this.shortFormProvider
						.getShortForm(owlEntity));
			}
			columnNames.add(objectNames.toString());
		}
		return columnNames;
	}

	public static void main(final String[] args) {
		List<IRI> iris = new ArrayList<IRI>(args.length);
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
