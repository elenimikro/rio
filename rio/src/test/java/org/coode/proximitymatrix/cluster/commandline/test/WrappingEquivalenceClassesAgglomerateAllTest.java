package org.coode.proximitymatrix.cluster.commandline.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.coode.basetest.TestHelper;
import org.coode.proximitymatrix.cluster.commandline.AgglomeratorBase;
import org.coode.proximitymatrix.cluster.commandline.WrappingAgglomerateAll;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;


public class WrappingEquivalenceClassesAgglomerateAllTest {

	@Test
	public void test() {
		List<IRI> iris = new ArrayList<IRI>(1);
		WrappingAgglomerateAll agglomerator = new WrappingAgglomerateAll();
		File file = new File(getClass().getResource("/pizza.owl").getFile());
		iris.add(IRI.create(file));
		System.out.println(iris);
		File outfile = new File("pizza.xml");
        try {
			agglomerator.run(outfile, iris);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
