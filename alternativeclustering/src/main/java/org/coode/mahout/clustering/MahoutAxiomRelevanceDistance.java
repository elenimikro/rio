package org.coode.mahout.clustering;

import java.util.Collection;
import java.util.Collections;

import org.apache.hadoop.conf.Configuration;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.parameters.Parameter;
import org.apache.mahout.math.Vector;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.semanticweb.owlapi.model.OWLOntology;
/** @author eleni */
public class MahoutAxiomRelevanceDistance implements DistanceMeasure {

	private final AxiomRelevanceAxiomBasedDistance delegate;
	private final OWLOntology o;

	public MahoutAxiomRelevanceDistance(OWLOntology ontology,
			OWLEntityReplacer replacer) {
		this.o = ontology;
		this.delegate = new AxiomRelevanceAxiomBasedDistance(
				ontology.getImportsClosure(), replacer,
				o.getOWLOntologyManager());
	}

	// ///////////////////////////////
	// MAHOUT INTERFACE //////////////
	// ///////////////////////////////

	@Override
	public Collection<Parameter<?>> getParameters() {
		return Collections.emptyList();
	}

	@Override
	public void createParameters(String prefix, Configuration jobConf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configure(Configuration config) {
		// TODO Auto-generated method stub

	}

	@Override
	public double distance(Vector v1, Vector v2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double distance(double centroidLengthSquare, Vector centroid,
			Vector v) {
		// TODO Auto-generated method stub
		return 0;
	}

}
