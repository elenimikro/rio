package org.coode.mahout.clustering;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.classify.WeightedVectorWritable;
import org.apache.mahout.clustering.iterator.DistanceMeasureCluster;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.coode.basetest.OntologyTestHelper;
import org.coode.distance.owl.AbstractAxiomBasedDistance;
import org.coode.utils.owl.DistanceCreator;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

public class MahoutTest {

	public static final double[][] points = { { 1, 1 }, { 2, 1 }, { 1, 2 },
			{ 2, 2 }, { 3, 3 }, { 8, 8 }, { 9, 8 }, { 8, 9 }, { 9, 9 } };

	public static void writePointsToFile(List<Vector> points, String fileName,
			FileSystem fs, Configuration conf) throws IOException {
		Path path = new Path(fileName);
		SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path,
				LongWritable.class, VectorWritable.class);
		long recNum = 0;
		VectorWritable vec = new VectorWritable();
		for (Vector point : points) {
			vec.set(point);
			writer.append(new LongWritable(recNum++), vec);
		}
		writer.close();
	}

	public static List<Vector> getPoints(double[][] raw) {
		List<Vector> points = new ArrayList<Vector>();
		for (int i = 0; i < raw.length; i++) {
			double[] fr = raw[i];
			Vector vec = new RandomAccessSparseVector(fr.length);
			vec.assign(fr);
			points.add(vec);
		}
		return points;
	}

	public static void main(String[] args) {
		try {
			int k = 2;
			List<Vector> vectors = getPoints(points);
			System.out.println(vectors);
			File testData = new File("testdata");
			if (!testData.exists()) {
				testData.mkdir();
			}
			testData = new File("testdata/points");
			if (!testData.exists()) {
				testData.mkdir();
			}
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);
			writePointsToFile(vectors, "testdata/points/file1", fs, conf);
			Path path = new Path("testdata/clusters/part-00000");
			SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf,
					path, Text.class, DistanceMeasureCluster.class);
			for (int i = 0; i < k; i++) {
				Vector vec = vectors.get(i);
				DistanceMeasureCluster cluster = new DistanceMeasureCluster(
						vec, i, new EuclideanDistanceMeasure());
				writer.append(new Text(cluster.getIdentifier()), cluster);
			}
			writer.close();
			KMeansDriver.run(conf, new Path("testdata/points"), new Path(
					"testdata/clusters"), new Path("output"),
					new EuclideanDistanceMeasure(), 0.001, 10, true, k, false);
			SequenceFile.Reader reader = new SequenceFile.Reader(fs,
					new Path("output/" + Cluster.CLUSTERED_POINTS_DIR
							+ "/part-m-00000"), conf);
			IntWritable key = new IntWritable();
			WeightedVectorWritable value = new WeightedVectorWritable();
			while (reader.next(key, value)) {
				System.out.println(value.toString() + " belongs to cluster "
						+ key.toString());
			}
			reader.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Test
	public void tesMahoutVectors() {
		OWLOntology ontology = OntologyTestHelper
				.getSmallMeaningfullTestOntology();

		for (OWLAxiom a : ontology.getAxioms()) {
			System.out.println(a);
		}
		AbstractAxiomBasedDistance distance = (AbstractAxiomBasedDistance) DistanceCreator
				.createAxiomRelevanceAxiomBasedDistance(ontology
						.getOWLOntologyManager());

		Map<OWLEntity, Integer[]> entityAxiomMap = new HashMap<OWLEntity, Integer[]>();
		List<Vector> points = new ArrayList<Vector>();
		Map<OWLAxiom, Integer> axiomIdMap = new HashMap<OWLAxiom, Integer>();
		int index = 0;
		for (OWLEntity e : ontology.getSignature()) {
			Collection<OWLAxiom> distAxioms = distance.getAxioms(e);
			Integer[] axiomIDs = new Integer[distAxioms.size()];
			for (OWLAxiom a : distAxioms) {
				if (axiomIdMap.get(a) == null) {
					axiomIdMap.put(a, index);
					index++;
				}
			}
			// Vector vec = new SequentialAccessSparseVector(ontology
			// .getSignature().size());
			//
			// Text itemWritable = new Text();
			Vector vec = new NamedVector();
			// FeatureVectorEncoder vectorEncoder = new StaticWordValueEncoder(
			// e.toString());
			// vectorEncoder.setProbes(1);
			System.out.println();
		}
	}
}
