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
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
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
import org.coode.distance.TableDistance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
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
import org.coode.proximitymatrix.SimpleHistoryItemFactory;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterStatistics;
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
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.w3c.dom.Document;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

/**
 * @author Luigi Iannone
 * 
 */
public class ClusteringGUI extends JFrame {
	private final class Reducer
			extends
			SwingWorker<ClusteringProximityMatrix<OWLEntity>, ClusteringProximityMatrix<OWLEntity>> {
		@Override
		protected ClusteringProximityMatrix<OWLEntity> doInBackground() throws Exception {
			return ClusteringGUI.this.clusteringMatrix.reduce(ClusteringGUI.this.filter);
		}

		@Override
		protected void done() {
			try {
				ClusteringGUI.this.clusteringMatrix = this.get();
				ClusteringGUI.this.updateGUI();
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
				e.printStackTrace();
			} catch (ExecutionException e) {
				JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private abstract class Agglomerator
			extends
			SwingWorker<ClusteringProximityMatrix<OWLEntity>, ClusteringProximityMatrix<OWLEntity>> {
		private final ClusteringProximityMatrix<OWLEntity> start;
		private final PairFilter<Collection<? extends OWLEntity>> filter;
		private int count = 0;

		/**
		 * @param start
		 * @param filter
		 */
		public Agglomerator(ClusteringProximityMatrix<OWLEntity> start,
				PairFilter<Collection<? extends OWLEntity>> filter) {
			if (start == null) {
				throw new NullPointerException("The start cannot be null");
			}
			if (filter == null) {
				throw new NullPointerException("The filter cannot be null");
			}
			this.start = start;
			this.filter = filter;
		}

		protected abstract boolean stop(ClusteringProximityMatrix<OWLEntity> matrix);

		@Override
		protected ClusteringProximityMatrix<OWLEntity> doInBackground() throws Exception {
			ClusteringProximityMatrix<OWLEntity> toReturn = this.getStart();
			do {
				toReturn = toReturn.agglomerate(this.getFilter());
				this.count++;
				this.process(Arrays.asList(toReturn));
			} while (!this.stop(toReturn));
			return toReturn;
		}

		@Override
		protected void process(List<ClusteringProximityMatrix<OWLEntity>> chunks) {
			if (!chunks.isEmpty()) {
				int clusterCount = 0;
				for (ClusteringProximityMatrix<OWLEntity> clusteringProximityMatrix : chunks) {
					clusterCount = clusteringProximityMatrix.getColumnDimension();
				}
				ClusteringGUI.this.glassPane
						.setMessage(String.format("%d agglomerations so far %d clusters",
								this.count, clusterCount));
			}
		}

		@Override
		protected void done() {
			try {
				ClusteringProximityMatrix<OWLEntity> clusteringProximityMatrix = this
						.get();
				ClusteringGUI.this.clusteringMatrix = clusteringProximityMatrix;
				ClusteringGUI.this.updateGUI();
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
				e.printStackTrace();
			} catch (ExecutionException e) {
				JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
				e.printStackTrace();
			}
		}

		/**
		 * @return the start
		 */
		public ClusteringProximityMatrix<OWLEntity> getStart() {
			return this.start;
		}

		/**
		 * @return the filter
		 */
		public PairFilter<Collection<? extends OWLEntity>> getFilter() {
			return this.filter;
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

		public void actionPerformed(ActionEvent arg0) {
			FileDialog dialog = new FileDialog(ClusteringGUI.this, "Save",
					FileDialog.SAVE);
			dialog.setVisible(true);
			String fileName = dialog.getFile();
			if (fileName != null) {
				try {
					OWLOntology ontology = ClusteringGUI.this.manager.getOntologies()
							.iterator().next();
					OPPLFactory factory = new OPPLFactory(ClusteringGUI.this.manager,
							ontology, null);
					ConstraintSystem constraintSystem = factory.createConstraintSystem();
					SortedSet<Cluster<OWLEntity>> sortedClusters = new TreeSet<Cluster<OWLEntity>>(
							ClusterStatisticsTableModel.SIZE_COMPARATOR);
					sortedClusters.addAll(ClusteringGUI.this.buildClusters());
					OWLObjectGeneralisation generalisation = Utils
							.getOWLObjectGeneralisation(sortedClusters,
									ClusteringGUI.this.manager.getOntologies(),
									constraintSystem);
					Document xml = Utils.toXML(sortedClusters, ClusteringGUI.this.manager
							.getOntologies(),
							new ManchesterOWLSyntaxOWLObjectRendererImpl(),
							generalisation, new ShowMessageRuntimeExceptionHandler(
									ClusteringGUI.this));
					Transformer t = TransformerFactory.newInstance().newTransformer();
					StreamResult result = new StreamResult(new File(new File(
							dialog.getDirectory()), fileName));
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

	private final class SaveAction extends AbstractAction {
		public SaveAction() {
			super("Save");
		}

		/**
		 *
		 */
		private static final long serialVersionUID = -6759993806728785589L;

		public void actionPerformed(ActionEvent arg0) {
			FileDialog dialog = new FileDialog(ClusteringGUI.this, "Save",
					FileDialog.SAVE);
			dialog.setVisible(true);
			String fileName = dialog.getFile();
			if (fileName != null) {
				MathContext mathContext = new MathContext(2);
				Set<Cluster<OWLEntity>> clusters = ClusteringGUI.this.buildClusters();
				try {
					File file = new File(new File(dialog.getDirectory()), fileName);
					PrintWriter out = new PrintWriter(file);
					out.println("Clusters:");
					for (Cluster<OWLEntity> cluster : clusters) {
						ClusterStatistics<OWLEntity> stats = ClusterStatistics
								.buildStatistics(cluster);
						out.println(String.format("%s\t%s\t%s\t%s", this
								.renderCluster(stats.getCluster()),
								new BigDecimal(stats.getAverageInternalDistance(),
										mathContext),
								new BigDecimal(stats.getMinInternalDistance(),
										mathContext),
								new BigDecimal(stats.getMaxInternalDistance(),
										mathContext)));
					}
					out.println("Proximity Matrix");
					double[][] data = ClusteringGUI.this.clusteringMatrix.getData();
					Iterator<Cluster<OWLEntity>> iterator = clusters.iterator();
					while (iterator.hasNext()) {
						Cluster<OWLEntity> cluster = iterator.next();
						out.print(this.renderCluster(cluster));
						if (iterator.hasNext()) {
							out.print("\t");
						}
					}
					out.println();
					List<Cluster<OWLEntity>> list = new ArrayList<Cluster<OWLEntity>>(
							clusters);
					int i = 0;
					for (double[] ds : data) {
						out.print(this.renderCluster(list.get(i)));
						for (double d : ds) {
							out.print(String.format("\t%s",
									new BigDecimal(d, mathContext)));
						}
						out.println();
						i++;
					}
					out.println("Distance matrix");
					List<OWLEntity> objects = new ArrayList<OWLEntity>(
							ClusteringGUI.this.distanceMatrix.getObjects());
					Iterator<OWLEntity> it = objects.iterator();
					while (it.hasNext()) {
						OWLEntity owlEntity = it.next();
						out.print(this.renderOWLEntity(owlEntity));
						if (it.hasNext()) {
							out.print("\t");
						}
					}
					data = ClusteringGUI.this.distanceMatrix.getData();
					i = 0;
					for (double[] ds : data) {
						out.print(this.renderOWLEntity(objects.get(i)));
						for (double d : ds) {
							out.print(String.format("\t%s",
									new BigDecimal(d, mathContext)));
						}
						out.println();
						i++;
					}
					out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(ClusteringGUI.this, e.getMessage());
				}
			}
		}

		/**
		 * @param owlEntity
		 * @return
		 */
		private String renderOWLEntity(OWLEntity owlEntity) {
			return ClusteringGUI.this.shortFormProvider.getShortForm(owlEntity);
		}

		/**
		 * @param cluster
		 * @return
		 */
		private String renderCluster(Cluster<OWLEntity> cluster) {
			Set<String> toReturn = new HashSet<String>(cluster.size());
			for (OWLEntity owlEntity : cluster) {
				toReturn.add(ClusteringGUI.this.shortFormProvider.getShortForm(owlEntity));
			}
			return toReturn.toString();
		}
	}

	private final class ClusteringTableCellRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
			Pair<Collection<? extends OWLEntity>> minimumDistancePair = ClusteringGUI.this.clusteringMatrix
					.getMinimumDistancePair();
			if (minimumDistancePair != null && table == ClusteringGUI.this.proximityTable) {
				List<Integer> rows = ClusteringGUI.this.clusteringMatrix
						.getRows(minimumDistancePair);
				if (rows.contains(row)) {
					defaultTableCellRenderer.setForeground(Color.RED);
				}
			}
			String rendering = value == null ? "" : value.toString();
			if (value instanceof Set<?>) {
				Set<String> members = new HashSet<String>(((Set<?>) value).size());
				for (Object object : (Set<?>) value) {
					members.add(this.render(object));
				}
				rendering = String.format(" Cluster %d) %s, size %d", row + 1,
						members.toString(), members.size());
			}
			Component toReturn = defaultTableCellRenderer.getTableCellRendererComponent(
					table, rendering, isSelected, hasFocus, row, column);
			return toReturn;
		}

		protected String render(Object object) {
			String toReturn = object.toString();
			if (object instanceof OWLEntity) {
				toReturn = ClusteringGUI.this.shortFormProvider
						.getShortForm((OWLEntity) object);
			}
			return toReturn;
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 3154241412745213737L;
	private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private final ClusterSummaryPanel<OWLEntity> clusterSummaryPanel = ClusterSummaryPanel
			.buildOWLEntityClusterSummaryPanel(new SimpleShortFormProvider());
	private ClusterAxiomPanel<OWLEntity> clusterAxiomPanel;
	private final JTable proximityTable = new JTable();
	private final JTable clusterStatisticsTable = new JTable();
	private ClusteringProximityMatrix<OWLEntity> clusteringMatrix = null;
	private final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
	private final JButton reduceButton = new JButton("Reduce");
	private final JButton agglomerateButton = new JButton("Agglomerate");
	private final JButton agglomerateAllZerosButton = new JButton("Agglomerate All Zeros");
	private final JButton agglomerateAllButton = new JButton("Agglomerate All");
	private final GlassPane glassPane = new GlassPane();
	private final Action saveAction = new SaveAction();
	private final Action saveClusteringAction = new SaveClustering();
	private final ClusteringTableCellRenderer clusteringTableCellRenderer = new ClusteringTableCellRenderer();
	private ProximityMatrix<OWLEntity> distanceMatrix;
	private PairFilter<Collection<? extends OWLEntity>> filter;

	public ClusteringGUI(Collection<? extends IRI> iris) {
		if (iris == null) {
			throw new NullPointerException("The IRI collection cannot be null");
		}
		for (IRI iri : iris) {
			try {
				URI uri = iri.toURI();
				if (uri.getScheme().startsWith("file") && uri.isAbsolute()) {
					File file = new File(uri);
					File parentFile = file.getParentFile();
					if (parentFile.isDirectory()) {
						this.manager.addIRIMapper(new AutoIRIMapper(parentFile, true));
					}
				}
				this.manager.loadOntology(iri);
			} catch (OWLOntologyCreationException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(),
						"Error in loading ontology", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
		System.out.println(String.format("Loaded %d ontologies ", this.manager
				.getOntologies().size()));
		this.reset();
		this.initGUI();
	}

	private void reset() {
		Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
			public int compare(OWLEntity o1, OWLEntity o2) {
				return ClusteringGUI.this.shortFormProvider.getShortForm(o1).compareTo(
						ClusteringGUI.this.shortFormProvider.getShortForm(o2));
			}
		});
		for (OWLOntology ontology : this.manager.getOntologies()) {
			entities.addAll(ontology.getSignature());
		}
		System.out.println(String.format("Computing distance between %d entities ...",
				entities.size()));
		// ByKindOWLEntityPopularityBasedRelevantPolicy policy = new
		// ByKindOWLEntityPopularityBasedRelevantPolicy(
		// entities, this.manager.getOntologies());
		// final Distance<OWLEntity> distance = new AxiomBasedDistance(
		// this.manager.getOntologies(), this.manager.getOWLDataFactory(),
		// policy, this.manager);
		final Distance<OWLEntity> distance = new AxiomRelevanceAxiomBasedDistance(
				this.manager.getOntologies(), this.manager.getOWLDataFactory(),
				this.manager);
		// final Distance<OWLEntity> distance = new EditDistance(
		// this.manager.getOntologies(), this.manager.getOWLDataFactory(),
		// this.manager);
		this.distanceMatrix = new SimpleProximityMatrix<OWLEntity>(entities, distance);
		System.out.println(String.format(
				"Computing distance between %d entities finished", entities.size()));
		Set<Collection<? extends OWLEntity>> newObjects = new LinkedHashSet<Collection<? extends OWLEntity>>();
		for (OWLEntity object : this.distanceMatrix.getObjects()) {
			newObjects.add(Collections.singleton(object));
		}
		Distance<Collection<? extends OWLEntity>> singletonDistance = new Distance<Collection<? extends OWLEntity>>() {
			public double getDistance(Collection<? extends OWLEntity> a,
					Collection<? extends OWLEntity> b) {
				return distance.getDistance(a.iterator().next(), b.iterator().next());
			}
		};
		this.filter = DistanceThresholdBasedFilter.build(new TableDistance<OWLEntity>(
				entities, this.distanceMatrix.getData()), 1);
		this.clusteringMatrix = ClusteringProximityMatrix.build(this.distanceMatrix,
				new CentroidProximityMeasureFactory(), this.filter,
				PairFilterBasedComparator.build(this.filter, newObjects,
						singletonDistance),
				new SimpleHistoryItemFactory<Collection<? extends OWLEntity>>());
		this.updateGUI();
		this.reduceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Reducer reducer = new Reducer();
				ClusteringGUI.this.glassPane.setMessage("Reducing...");
				ClusteringGUI.this.glassPane.setVisible(true);
				reducer.execute();
			}
		});
		this.agglomerateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Agglomerator agglomerator = new Agglomerator(
						ClusteringGUI.this.clusteringMatrix, ClusteringGUI.this.filter) {
					@Override
					protected boolean stop(ClusteringProximityMatrix<OWLEntity> matrix) {
						return true;
					}
				};
				ClusteringGUI.this.glassPane.setMessage("Agglomerating...");
				ClusteringGUI.this.glassPane.setVisible(true);
				agglomerator.execute();
			}
		});
		this.agglomerateAllZerosButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Agglomerator agglomerator = new Agglomerator(
						ClusteringGUI.this.clusteringMatrix, ClusteringGUI.this.filter) {
					@Override
					protected boolean stop(ClusteringProximityMatrix<OWLEntity> matrix) {
						return matrix.getMinimumDistancePair() == null
								|| matrix.getMinimumDistance() > 0;
					}
				};
				ClusteringGUI.this.glassPane.setMessage("Agglomerating...");
				ClusteringGUI.this.glassPane.setVisible(true);
				agglomerator.execute();
			}
		});
		this.agglomerateAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Agglomerator agglomerator = new Agglomerator(
						ClusteringGUI.this.clusteringMatrix, ClusteringGUI.this.filter) {
					@Override
					protected boolean stop(ClusteringProximityMatrix<OWLEntity> matrix) {
						return matrix.getMinimumDistancePair() == null
								|| !this.getFilter().accept(
										matrix.getMinimumDistancePair().getFirst(),
										matrix.getMinimumDistancePair().getSecond());
					}
				};
				ClusteringGUI.this.glassPane.setMessage("Agglomerating...");
				ClusteringGUI.this.glassPane.setVisible(true);
				agglomerator.execute();
			}
		});
	}

	private void updateGUI() {
		List<String> columnNames = this
				.getColumnNames(this.clusteringMatrix.getObjects());
		this.proximityTable.setModel(new ProximityMatrixTableModel(this.clusteringMatrix,
				columnNames.toArray(new String[columnNames.size()])));
		this.clusterStatisticsTable.setModel(new ClusterStatisticsTableModel(this
				.buildClusters()));
		this.proximityTable.getColumn("*").setCellRenderer(
				ClusteringGUI.this.clusteringTableCellRenderer);
		this.clusterStatisticsTable.getColumn("Cluster").setCellRenderer(
				this.clusteringTableCellRenderer);
		this.agglomerateButton.setEnabled(ClusteringGUI.this.clusteringMatrix
				.getMinimumDistancePair() != null);
		this.agglomerateAllZerosButton.setEnabled(this.clusteringMatrix
				.getMinimumDistance() == 0);
		this.agglomerateAllButton.setEnabled(ClusteringGUI.this.clusteringMatrix
				.getMinimumDistancePair() != null);
		this.glassPane.setVisible(false);
	}

	private Set<Cluster<OWLEntity>> buildClusters() {
		Set<Collection<? extends OWLEntity>> objects = this.clusteringMatrix.getObjects();
		Set<Cluster<OWLEntity>> toReturn = new HashSet<Cluster<OWLEntity>>(objects.size());
		for (Collection<? extends OWLEntity> collection : objects) {
			toReturn.add(new SimpleCluster<OWLEntity>(collection, this.distanceMatrix));
		}
		return toReturn;
	}

	private void initGUI() {
		this.setLayout(new BorderLayout());
		JSplitPane mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainPanel.setResizeWeight(.5);
		mainPanel.setDividerLocation(.5);
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		JMenu menu = new JMenu();
		menuBar.add(menu);
		menu.setText("File");
		JMenuItem saveMenuItem = new JMenuItem(this.saveAction);
		JMenuItem saveClustersMenuItem = new JMenuItem(this.saveClusteringAction);
		menu.add(saveMenuItem);
		menu.add(saveClustersMenuItem);
		this.setGlassPane(this.glassPane);
		JSplitPane clusteringPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.proximityTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.proximityTable.getColumn("*").setCellRenderer(
				this.clusteringTableCellRenderer);
		this.clusterStatisticsTable.getColumn("Cluster").setCellRenderer(
				this.clusteringTableCellRenderer);
		JPanel rightPane = new JPanel(new BorderLayout());
		rightPane.add(new JScrollPane(this.proximityTable), BorderLayout.CENTER);
		clusteringPane.setLeftComponent(new JScrollPane(this.clusterStatisticsTable));
		clusteringPane.setRightComponent(rightPane);
		this.clusterAxiomPanel = ClusterAxiomPanel
				.build(new ManchesterOWLSyntaxOWLObjectRendererImpl());
		JSplitPane clusterPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		clusterPanel.setResizeWeight(.5);
		clusterPanel.setDividerLocation(.5);
		clusterPanel.setLeftComponent(this.clusterSummaryPanel);
		clusterPanel.setRightComponent(this.clusterAxiomPanel);
		mainPanel.setTopComponent(clusterPanel);
		mainPanel.setBottomComponent(clusteringPane);
		this.add(mainPanel, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(this.reduceButton);
		buttonPanel.add(this.agglomerateButton);
		buttonPanel.add(this.agglomerateAllZerosButton);
		buttonPanel.add(this.agglomerateAllButton);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.clusterStatisticsTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@SuppressWarnings("unchecked")
					public void valueChanged(ListSelectionEvent e) {
						int selectedRow = ClusteringGUI.this.clusterStatisticsTable
								.getSelectedRow();
						Object valueAt = selectedRow == -1 ? null
								: ClusteringGUI.this.clusterStatisticsTable.getModel()
										.getValueAt(selectedRow, 0);
						if (valueAt instanceof Cluster<?>) {
							ClusteringGUI.this.clusterSummaryPanel
									.setCluster((Cluster<OWLEntity>) valueAt);
							OWLObjectGeneralisation generalisation;
							try {
								OWLOntology ontology = ClusteringGUI.this.manager
										.getOntologies().iterator().next();
								OPPLFactory factory = new OPPLFactory(
										ClusteringGUI.this.manager, ontology, null);
								ConstraintSystem constraintSystem = factory
										.createConstraintSystem();
								SortedSet<Cluster<OWLEntity>> sortedClusters = new TreeSet<Cluster<OWLEntity>>(
										ClusterStatisticsTableModel.SIZE_COMPARATOR);
								sortedClusters.addAll(ClusteringGUI.this.buildClusters());
								generalisation = Utils.getOWLObjectGeneralisation(
										sortedClusters,
										ClusteringGUI.this.manager.getOntologies(),
										constraintSystem);
								ClusteringGUI.this.clusterAxiomPanel.setCluster(
										(Cluster<OWLEntity>) valueAt,
										ClusteringGUI.this.manager.getOntologies(),
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
	 * @param objects
	 * @return
	 */
	public List<String> getColumnNames(
			Collection<? extends Collection<? extends OWLEntity>> objects) {
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

	public static void main(String[] args) {
		List<IRI> iris = new ArrayList<IRI>(args.length);
		for (String string : args) {
			IRI iri = IRI.create(string);
			iris.add(iri);
		}
		ClusteringGUI frame = new ClusteringGUI(iris);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
