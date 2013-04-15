package org.coode.proximitymatrix.cluster.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.basetest.ClusteringHelper;
import org.coode.basetest.DistanceCreator;
import org.coode.basetest.OntologyTestHelper;
import org.coode.distance.owl.AbstractAxiomBasedDistance;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.GeneralisationDecompositionModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.util.MultiMap;
import org.xml.sax.SAXException;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import experiments.ClusterComparison;
import experiments.ExperimentHelper;
import experiments.GeneralisationComparison;

public class GeneralisationComparisonTest {

	private final MultiMap<OWLAxiom, OWLAxiomInstantiation> popularityMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
	private final MultiMap<OWLAxiom, OWLAxiomInstantiation> structuralMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
	private GeneralisationComparison comparator;

	// @Before
	public void setUp() {
		OWLOntology ontology = OntologyTestHelper
				.getSmallMeaningfullTestOntology();
		ClusterDecompositionModel<OWLEntity> popularityModel = ClusteringHelper
				.getSyntacticPopularityClusterModel(ontology);
		ClusterDecompositionModel<OWLEntity> structuralModel = ClusteringHelper
				.getSyntacticStructuralClusterModel(ontology);
		popularityMap.putAll(ClusteringHelper
				.extractGeneralisationMap(popularityModel));
		structuralMap.putAll(ClusteringHelper
				.extractGeneralisationMap(structuralModel));
		comparator = new GeneralisationComparison();
	}

	@Test
	public void getGeneralisationIntersectionTest() {
		MultiMap<OWLAxiom, OWLAxiomInstantiation> map = comparator
				.getGeneralisationIntersection(popularityMap, structuralMap);
		System.out.println("\n" + map);
		System.out.println("\n" + popularityMap);
		assertTrue(map.size() == popularityMap.size());
		for (OWLAxiom ax : map.keySet()) {
			for (OWLAxiomInstantiation inst : map.get(ax)) {
				assertTrue(popularityMap.contains(ax, inst));
			}
		}
	}

	@Test
	public void getOWLInstantiationIntersectionTest() {
		Set<OWLAxiom> owlInstantiationIntersection = comparator
				.getOWLInstantiationIntersection(popularityMap, structuralMap);
		assertTrue(popularityMap.getAllValues().size() == owlInstantiationIntersection
				.size());
		System.out.println(owlInstantiationIntersection);
		System.out.println(popularityMap.getAllValues());
		assertTrue(Utils.extractAxioms(popularityMap.getAllValues())
				.containsAll(owlInstantiationIntersection));
		assertTrue(Utils.extractAxioms(structuralMap.getAllValues())
				.containsAll(owlInstantiationIntersection));
	}

	@Test
	public void getGeneralisationSimilarityTest() {
		double similarity = comparator.getGeneralisationSimilarity(
				popularityMap, structuralMap);
		assertEquals(1, similarity, 0.1);
	}

	@Test
	public void getOWLInstantiationSimilarityTest() {
		double similarity = comparator.getOWLInstantiationSimilarity(
				popularityMap, structuralMap);
		assertEquals(1, similarity, 0.1);
	}

	@Test
	public void computeBioportalOntologyGeneralisationSimilarity() {
		File ontologyDocument = new File(
				"bioportal/adverse-event-reporting-ontology/adverse-event-reporting-ontology_main.owl");
		try {
			OWLOntology ontology = OWLManager.createOWLOntologyManager()
					.loadOntologyFromOntologyDocument(ontologyDocument);
			ExperimentHelper.stripOntologyFromAnnotationAssertions(ontology);
			ClusterDecompositionModel<OWLEntity> popularityModel = ClusteringHelper
					.getSyntacticPopularityClusterModel(ontology);
			ClusterDecompositionModel<OWLEntity> structuralModel = ClusteringHelper
					.getSyntacticStructuralClusterModel(ontology);

			Set<Set<OWLEntity>> xml_popularity_clusters = Utils
					.readFromXML(
							new FileInputStream(
									new File(
											"previva-experiment/_popularity_-adverse-event-reporting-ontology_main.xml")),
							ontology.getOWLOntologyManager());
			GeneralisationDecompositionModel<OWLEntity> xml_popularity_model = new GeneralisationDecompositionModel<OWLEntity>(
					xml_popularity_clusters, ontology);
			Set<Set<OWLEntity>> xml_structural_clusters = Utils
					.readFromXML(
							new FileInputStream(
									new File(
											"previva-experiment/_structural_-adverse-event-reporting-ontology_main.xml")),
							ontology.getOWLOntologyManager());
			GeneralisationDecompositionModel<OWLEntity> xml_structural_model = new GeneralisationDecompositionModel<OWLEntity>(
					xml_structural_clusters, ontology);
			MultiMap<OWLAxiom, OWLAxiomInstantiation> xml_popularityMap = xml_popularity_model
					.getGeneralisationMap();
			MultiMap<OWLAxiom, OWLAxiomInstantiation> xml_structuralMap = xml_structural_model
					.getGeneralisationMap();

			System.out.println("popularity xml size "
					+ xml_popularity_clusters.size());
			System.out.println("popularity size "
					+ popularityModel.getClusterList().size());
			System.out.println("popularity xml "
					+ xml_popularity_model.getClusterList());
			System.out.println("clusterlist "
					+ popularityModel.getClusterList());
			// for (Set<OWLEntity> clusterList : xml_popularity_clusters) {
			// if (popularityModel.getClusterList().contains(clusterList)) {
			// System.out.println("Not contained \t" + clusterList);
			// }
			// }

			ClusterComparison<OWLEntity> clusterComparator = new ClusterComparison<OWLEntity>();
			// assertEquals(clusterComparator.getClusterSimilarity(cluster,
			// anotherCluster))

			MultiMap<OWLAxiom, OWLAxiomInstantiation> popularityMap = ClusteringHelper
					.extractGeneralisationMap(popularityModel);
			MultiMap<OWLAxiom, OWLAxiomInstantiation> structuralMap = ClusteringHelper
					.extractGeneralisationMap(structuralModel);
			ConstraintSystem constraintSystem = new OPPLFactory(
					ontology.getOWLOntologyManager(), ontology, null)
					.createConstraintSystem();
			QuickFailRuntimeExceptionHandler runtimeExceptionHandler = new QuickFailRuntimeExceptionHandler();
			comparator = new GeneralisationComparison();
			System.out.println("generalisation similarity "
					+ comparator.getGeneralisationSimilarity(popularityMap,
							structuralMap));
			assertEquals(comparator.getGeneralisationSimilarity(popularityMap,
					structuralMap), comparator.getGeneralisationSimilarity(
					xml_popularityMap, xml_structuralMap), 0.01);
			// assertTrue(comparator.getGeneralisationIntersection(popularityMap,
			// structuralMap).equals(
			// comparator.getGeneralisationIntersection(xml_popularityMap,
			// xml_structuralMap)));

		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownOWLOntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OPPLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void compareGeneralisationsOfMyPeople() {
		OWLOntology ontology = OntologyTestHelper.getSmallPaperOntology();
		ClusterDecompositionModel<OWLEntity> popularityClusterModel = ClusteringHelper
				.getSyntacticPopularityClusterModel(ontology);
		ClusterDecompositionModel<OWLEntity> structuralClusterModel = ClusteringHelper
				.getSyntacticStructuralClusterModel(ontology);
		ClusterDecompositionModel<OWLEntity> propertyClusterModel = ClusteringHelper
				.getSyntacticPropertyClusterModel(ontology);

		MultiMap<OWLAxiom, OWLAxiomInstantiation> popularityMap = ClusteringHelper
				.extractGeneralisationMap(popularityClusterModel);
		MultiMap<OWLAxiom, OWLAxiomInstantiation> structuralMap = ClusteringHelper
				.extractGeneralisationMap(structuralClusterModel);
		MultiMap<OWLAxiom, OWLAxiomInstantiation> propertyMap = ClusteringHelper
				.extractGeneralisationMap(propertyClusterModel);

		ToStringRenderer.getInstance().setRenderer(
				new ManchesterOWLSyntaxOWLObjectRendererImpl());
		System.out.println("Popularity clusters");
		printClusters(popularityClusterModel);
		System.out.println("Structural clusters");
		printClusters(structuralClusterModel);
		System.out.println("Property clusters");
		printClusters(propertyClusterModel);
		// assertTrue(gener)
		// for(OWLEntity e : ontology.getSignature()){
		// System.out.println("Entity " + e);
		//
		// }
		AbstractAxiomBasedDistance popularityDistance = (AbstractAxiomBasedDistance) DistanceCreator
				.createAxiomRelevanceAxiomBasedDistance(ontology
						.getOWLOntologyManager());
		AbstractAxiomBasedDistance structuralDistance = (AbstractAxiomBasedDistance) DistanceCreator
				.createStructuralAxiomRelevanceAxiomBasedDistance(ontology
						.getOWLOntologyManager());
		AbstractAxiomBasedDistance propertyDistance = (AbstractAxiomBasedDistance) DistanceCreator
				.createOWLEntityRelevanceAxiomBasedDistance(ontology
						.getOWLOntologyManager());

		System.out.println("popularity distances");
		printDistanceAxioms(ontology, popularityDistance);
		System.out.println("structural distances");
		printDistanceAxioms(ontology, structuralDistance);
		System.out.println("property distances");
		printDistanceAxioms(ontology, propertyDistance);
		System.out.println("\n Popularity generalisationMap");
		printGeneralisationMap(popularityMap);
		System.out.println("\n Structural generalisationMap");
		printGeneralisationMap(structuralMap);
		System.out.println("\n Property generalisationMap");
		printGeneralisationMap(propertyMap);
	}

	private void printGeneralisationMap(
			MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap) {
		for (OWLAxiom ax : generalisationMap.keySet()) {
			System.out.println(ax);
			System.out.println("Instantiations");
			for (OWLAxiomInstantiation inst : generalisationMap.get(ax)) {
				System.out.println("\t" + inst);
			}
		}

	}

	public void printDistanceAxioms(OWLOntology ontology,
			AbstractAxiomBasedDistance distance) {
		for (OWLEntity e : ontology.getSignature()) {

			System.out.println("Entity " + e + "\t" + distance.getAxioms(e));
		}
	}

	public void printClusters(ClusterDecompositionModel<OWLEntity> clusterModel) {
		int counter = 1;
		for (Cluster<OWLEntity> cluster : clusterModel.getClusterList()) {
			System.out.println("Cluster " + counter);
			System.out.println("  " + cluster);
			counter++;
		}
	}
}
