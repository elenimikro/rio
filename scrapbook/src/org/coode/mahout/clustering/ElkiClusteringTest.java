//package org.coode.mahout.clustering;
//
//import de.lmu.ifi.dbs.elki.algorithm.Algorithm;
//import de.lmu.ifi.dbs.elki.algorithm.clustering.hierarchical.NaiveAgglomerativeHierarchicalClustering;
//import de.lmu.ifi.dbs.elki.algorithm.outlier.lof.LOF;
//import de.lmu.ifi.dbs.elki.data.LabelList;
//import de.lmu.ifi.dbs.elki.data.NumberVector;
//import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
//import de.lmu.ifi.dbs.elki.database.Database;
//import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
//import de.lmu.ifi.dbs.elki.database.relation.Relation;
//import de.lmu.ifi.dbs.elki.datasource.FileBasedDatabaseConnection;
//import de.lmu.ifi.dbs.elki.distance.distancevalue.DoubleDistance;
//import de.lmu.ifi.dbs.elki.result.Result;
//import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
//import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
//import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
//
//public class ElkiClusteringTest {
//
//	public static void main(String[] args) {
//		// Setup parameters:
//		ListParameterization params = new ListParameterization();
//		Object filename = null;
//		params.addParameter(FileBasedDatabaseConnection.INPUT_ID, filename);
//		// Add other parameters for the database here!
//
//		// Instantiate the database:
//		Database db = ClassGenericsUtil.parameterizeOrAbort(
//				StaticArrayDatabase.class, params);
//		// Don't forget this, it will load the actual data...
//		db.initialize();
//
//		Relation<NumberVector<?>> vectors = db
//				.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
//		Relation<LabelList> labels = db.getRelation(TypeUtil.LABELLIST);
//
//		// params.addParameter(LOF<O, NumberDistance<D,?>>.K_ID, 20);
//
//		Algorithm alg = ClassGenericsUtil.parameterizeOrAbort(
//				NaiveAgglomerativeHierarchicalClustering.class, params);
//		Result result = alg.run(db); // will choose the relation automatically!
//
//		LOF<NumberVector<?>, DoubleDistance> lof = ClassGenericsUtil
//				.parameterizeOrAbort(LOF.class, params);
//		OutlierResult outliers = (OutlierResult) alg.run(db);
//
//	}
// }
