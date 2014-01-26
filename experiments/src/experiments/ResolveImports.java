package experiments;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.AutoIRIMapper;

/** @author eleni */
public class ResolveImports {
    /** @param folder
     *            folder
     * @param onto
     *            onto
     * @return ontology
     * @throws Exception
     *             Exception */
    public static OWLOntology resolveImports(String folder, String onto) throws Exception {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        m.addIRIMapper(new AutoIRIMapper(new File(folder), false));
        OWLOntology o = m.loadOntology(IRI.create(onto));
        OWLOntology out = OWLManager.createOWLOntologyManager().createOntology(
                IRI.create(onto));
        for (OWLOntology o1 : o.getImportsClosure()) {
            out.getOWLOntologyManager().addAxioms(out, o1.getAxioms());
        }
        return out;
    }
}
