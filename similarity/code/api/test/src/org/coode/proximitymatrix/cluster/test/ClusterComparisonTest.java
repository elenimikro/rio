package org.coode.proximitymatrix.cluster.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import experiments.ClusterComparison;

public class ClusterComparisonTest {

	private final Set<String> cluster = new HashSet<String>();
	private final Set<String> anotherCluster = new HashSet<String>();
	private final Set<String> thirdCluster = new HashSet<String>();

	@Before
	public void setUp() {
		cluster.add("A");
		cluster.add("B");
		cluster.add("C");

		anotherCluster.add("A");
		anotherCluster.add("B");
		anotherCluster.add("D");

		thirdCluster.add("A");
		thirdCluster.add("C");
		thirdCluster.add("D");
	}

	@Test
	public void getIntersectionTest() {
		ClusterComparison<String> comparator = new ClusterComparison<String>();
		Set<String> intersection = comparator.getIntersection(cluster,
				anotherCluster);
		assertEquals(2, intersection.size());
		assertTrue(intersection.contains("A"));
		assertTrue(intersection.contains("B"));
	}

	@Test
	public void getClusterSimilarityTest() {
		ClusterComparison<String> comparator = new ClusterComparison<String>();
		double similarity = comparator.getClusterSimilarity(cluster,
				anotherCluster);
		assertEquals(0.5, similarity, 0.01);
	}

	@Test
	public void getMultipleClusterSimilarityTest() {
		// three methods
		List<Set<String>> methodA = new ArrayList<Set<String>>();
		List<Set<String>> methodB = new ArrayList<Set<String>>();
		List<Set<String>> methodC = new ArrayList<Set<String>>();

		methodA.add(new HashSet<String>(Arrays.asList("A", "B", "C", "D")));
		methodA.add(new HashSet<String>(Arrays.asList("E", "F", "G", "I")));
		methodA.add(new HashSet<String>(Arrays.asList("J", "K", "L")));

		methodB.add(new HashSet<String>(Arrays.asList("A", "B", "C", "D", "J",
				"K", "L")));
		methodB.add(new HashSet<String>(Arrays.asList("E", "F", "G", "I")));

		methodC.add(new HashSet<String>(Arrays.asList("A", "B", "C")));
		methodC.add(new HashSet<String>(Arrays.asList("D", "J")));
		methodC.add(new HashSet<String>(Arrays.asList("E", "F", "G")));
		methodC.add(new HashSet<String>(Arrays.asList("K", "L")));

		ArrayList<String> elements = new ArrayList<String>(Arrays.asList("A",
				"B", "C", "D", "E", "F", "G", "I", "J", "K", "L"));
		Map<String, Integer> mapA = new HashMap<String, Integer>();
		Map<String, Integer> mapB = new HashMap<String, Integer>();
		Map<String, Integer> mapC = new HashMap<String, Integer>();

		// build element maps
		for (String s : elements) {
			for (int i = 0; i < methodA.size(); i++) {
				if (methodA.get(i).contains(s)) {
					mapA.put(s, i);
				}
			}
			for (int i = 0; i < methodB.size(); i++) {
				if (methodB.get(i).contains(s)) {
					mapB.put(s, i);
				}
			}
			for (int i = 0; i < methodC.size(); i++) {
				if (methodC.get(i).contains(s)) {
					mapC.put(s, i);
				}
			}
		}

		double similarityAB = computeSimilarities(methodA, methodB, elements,
				mapA, mapB, "A", "B");
		assertEquals(0.67, similarityAB, 0.01);
		double similarityAC = computeSimilarities(methodA, methodC, elements,
				mapA, mapC, "A", "C");
		assertEquals(0.5, similarityAC, 0.1);
		double similarityBC = computeSimilarities(methodB, methodC, elements,
				mapB, mapC, "B", "C");
		assertEquals(0.4375, similarityBC, 0.0001);
	}

	public double computeSimilarities(List<Set<String>> methodA,
			List<Set<String>> methodB, ArrayList<String> elements,
			Map<String, Integer> mapA, Map<String, Integer> mapB,
			String method, String anotherMethod) {
		Set<String> visitedEntities = new HashSet<String>();
		ClusterComparison<String> comparator = new ClusterComparison<String>();
		double totalSimilarity = 0;
		double counter = 0;
		// compute similarity
		for (String s : elements) {
			System.out.println("Checking entity " + s);
			if (!visitedEntities.contains(s)) {
				// A to B
				Integer indexA = mapA.get(s);
				Integer indexB = mapB.get(s);
				if (indexA != null && indexB != null) {
					double clusterSimilarity = comparator.getClusterSimilarity(
							methodA.get(indexA), methodB.get(indexB));
					System.out.println("Cluster similarity between cluster "
							+ indexA + " of method " + method + " and cluster "
							+ indexB + " of method " + anotherMethod + " is "
							+ clusterSimilarity);
					Set<String> intersection = comparator.getIntersection(
							methodA.get(indexA), methodB.get(indexB));
					visitedEntities.addAll(intersection);
					System.out.println("Intersection " + intersection);
					totalSimilarity += clusterSimilarity;
					counter++;
				}
			}
		}
		double toReturn = totalSimilarity / counter;
		return toReturn;
	}

}
