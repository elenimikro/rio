package experiments;

import org.coode.proximitymatrix.cluster.commandline.WrappingEquivalenceClassesAgglomerateAll;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class InputRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String arguments[] = new String[2];
		arguments[0] = "release/fma_results_18-08-2013";
		arguments[1] = "ontologies/fma_skeleton/fma_skeleton_module20130620.owl";
		try {
			WrappingEquivalenceClassesAgglomerateAll.main(arguments);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
