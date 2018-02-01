package org.coode.proximitymatrix.cluster.commandline.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.coode.proximitymatrix.cluster.commandline.WrappingAgglomerateAll;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

@SuppressWarnings("javadoc")
public class WrappingEquivalenceClassesAgglomerateAllTest {

    @Test
    public void test() {
        List<IRI> iris = new ArrayList<>(1);
        WrappingAgglomerateAll agglomerator = new WrappingAgglomerateAll();
        File file = new File(getClass().getResource("/pizza.owl").getFile());
        iris.add(IRI.create(file));
        File outfile = new File("pizza.xml");
        try {
            agglomerator.run(outfile, iris);
        } catch (OWLOntologyCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
