package experiments;

import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillersImpl;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasoner;
import uk.ac.manchester.cs.factplusplus.owlapiv3.OWLKnowledgeExplorationReasonerWrapper;

public class ExperimentUtils {

	public static KnowledgeExplorer runFactplusplusKnowledgeExplorerReasoner(
			OWLOntology ontology) {
		OWLReasoner reasoner = new FaCTPlusPlusReasoner(ontology,
				new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		KnowledgeExplorer ke = new KnowledgeExplorerMaxFillersImpl(reasoner,
				new OWLKnowledgeExplorationReasonerWrapper(
						new FaCTPlusPlusReasoner(ontology,
								new SimpleConfiguration(),
								BufferingMode.NON_BUFFERING)));
		return ke;
	}

}
