package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.basetest.ClusterCreator;
import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.ClusterStatistics;
import org.coode.proximitymatrix.cluster.GeneralisationStatistics;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecomposition;
import org.coode.proximitymatrix.cluster.GeneralisedAtomicDecompositionMetrics;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class PopularityClusteringWithADEvaluationExperiment {

	private final static String RESULTS = "/Users/elenimikroyannidi/eclipse-workspace/similarity/similarity/experiment-results/"; 
	
	public static void main(String[] args) throws OWLOntologyCreationException, OPPLException, ParserConfigurationException, FileNotFoundException {
		String base = "/Users/elenimikroyannidi/eclipse-workspace/similarity/similarity/experiment-ontologies/";
		String[] input = new String[] { "amino-acid-original.owl", 
				"flowers7.owl", "wine.owl", 
				"sct-20100731-stated_Hypertension-subs_module.owl",
				"kupkb/kupkb.owl", "obi.owl" , "ChronicALLModule.owl",
				"tambis-full.owl", "galen.owl" };
		String  current = "";
		File file = new File(RESULTS + "allstats.csv");
		
		FileOutputStream fout = new FileOutputStream(file, true);
		PrintStream out = new PrintStream(fout);
		if(!file.exists()){
			BaseExperiment.setUpOutputHeader(out);
		}
		
		for(String s : input){
			System.out
					.println("\n PopularityClusteringWithADEvaluationExperiment.main() \t " + s);
			String substring = s.substring(s.indexOf("/")+1);
			String filename = RESULTS + substring.replaceAll(".owl", ".csv");
			File f = new File(filename);
			
			if (!f.exists()) {
				current = base + s;
				int axiomCount = 0;
				int logicalAxiomCount = 0;
				int entitiesNo = 0;
				OWLOntologyManager m = OWLManager.createOWLOntologyManager();
				OWLOntology o = m.loadOntologyFromOntologyDocument(new File(
						current));

				for (OWLOntology ontology : m.getOntologies()) {
					axiomCount += ontology.getAxiomCount();
					logicalAxiomCount += ontology.getLogicalAxiomCount();
					entitiesNo += ontology.getSignature().size();
				}

				ClusterDecompositionModel<OWLEntity> model = BaseExperiment
						.startClustering(m, o);
				GeneralisedAtomicDecomposition<OWLEntity> gad = new GeneralisedAtomicDecomposition<OWLEntity>(
						model, o);

				BaseExperiment.printStats(out, s, axiomCount,
						logicalAxiomCount, entitiesNo, model, gad);

				BaseExperiment.printClusteringStats(out, f,
						model.getClusterList());
			}
		}
		
	}	
}
