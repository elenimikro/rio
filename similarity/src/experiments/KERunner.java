package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class KERunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			File ontoFile = new File(args[0]);
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = manager
					.loadOntologyFromOntologyDocument(ontoFile);
			ExperimentHelper.stripOntologyFromAnnotationAssertions(ontology);

			KnowledgeExplorer ke = ExperimentUtils
					.runFactplusplusKnowledgeExplorerReasoner(ontology);

			Set<OWLAxiom> entailments = new HashSet<OWLAxiom>();
			entailments.addAll(ke.getAxioms());
			// add atomic subsumsions
			// System.out.println("Adding subclass axioms...");

			// JFactChainsawReasoner reasoner = new JFactChainsawReasoner(
			// new JFactFactory(), ontology, new SimpleConfiguration());
			// reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

			ElkReasonerFactory rf = new ElkReasonerFactory();
			OWLReasoner reasoner = rf.createNonBufferingReasoner(ontology);
			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

			entailments.addAll(getAtomicSubsumptions(ontology, reasoner));
			// InferredSubClassAxiomGenerator generator = new
			// InferredSubClassAxiomGenerator();
			// entailments.addAll(generator.createAxioms(manager, reasoner));

			OWLOntology inferedOnto = OWLManager.createOWLOntologyManager()
					.createOntology(entailments);
			File outfile = new File("inferredOntology.owl");
			FileOutputStream os = new FileOutputStream(outfile);
			inferedOnto.getOWLOntologyManager().saveOntology(inferedOnto, os);
			os.close();
			System.out.println("The Resulted ontology was saved in "
					+ outfile.getAbsolutePath());
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static Set<OWLAxiom> getAtomicSubsumptions(OWLOntology ontology,
			OWLReasoner reasoner) {
		Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();
		Set<OWLClass> signature = ontology.getClassesInSignature();
		OWLDataFactory df = ontology.getOWLOntologyManager()
				.getOWLDataFactory();
		for (OWLClass c : signature) {
			NodeSet<OWLClass> subClasses = reasoner.getSubClasses(c, true);
			for (OWLClass sub : subClasses.getFlattened()) {
				OWLAxiom subsumption = df.getOWLSubClassOfAxiom(sub, c);
				if (!ontology.containsAxiom(subsumption)) {
					toReturn.add(subsumption);
				}
			}
		}
		return toReturn;
	}
}
