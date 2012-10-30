package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;

import javax.imageio.stream.FileImageInputStream;
import javax.xml.parsers.ParserConfigurationException;

import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.oppl.exceptions.OPPLException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class BioPortalSyntacticClusteringWIthADEvaluationExperiment extends SyntacticClusteringWithADEvaluationExperiment{

	private final static String RESULTS_BASE = "/Users/elenimikroyannidi/eclipse-workspace/similarity/similarity/experiment-results/syntactic/bioportal"; 


	public static void main(String[] args) throws OWLOntologyCreationException,
			OPPLException, ParserConfigurationException, FileNotFoundException {

		String bioportalList = "/Users/elenimikroyannidi/eclipse-workspace/similarity/similarity/BioPortal_LocalRepositoryIRIs.txt";
		BufferedReader d = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(bioportalList))));
		ArrayList<String> inputList = new ArrayList<String>();
		try {
			while(d.read()!=-1){
				inputList.add(d.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void setupClusteringExperiment(ArrayList<String> input,
			File allResultsFile) throws FileNotFoundException,
			OWLOntologyCreationException, OPPLException,
			ParserConfigurationException {
		for(String s : input){
			ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
			System.out
					.println("\n PopularityClusteringWithADEvaluationExperiment.main() \t " + s);
			String substring = s.substring(s.lastIndexOf("/")+1);
			String filename = RESULTS_BASE + substring.replaceAll(".owl", ".csv");
			String xml = RESULTS_BASE + substring.replaceAll(".owl", ".xml");
			File f = new File(filename);
			if (!f.exists()) {
				PrintStream singleOut = new PrintStream(f);
				
				//load ontology and get general ontology metrics
				OWLOntologyManager m = OWLManager.createOWLOntologyManager();
				OWLOntology o = m.loadOntologyFromOntologyDocument(new File(
						s));
				metrics.add(new SimpleMetric<String>("Ontology", s));
				metrics.addAll(SyntacticClusteringWithADEvaluationExperiment.getBasicOntologyMetrics(m));
				
				//popularity distance
				Distance<OWLEntity> distance = DistanceCreator
						.createAxiomRelevanceAxiomBasedDistance(m);
				SyntacticClusteringWithADEvaluationExperiment.run("popularity", metrics, singleOut, o, distance, null);
				
				//structural
				distance = DistanceCreator
						.createStructuralAxiomRelevanceAxiomBasedDistance(m);
				SyntacticClusteringWithADEvaluationExperiment.run("structural", metrics, singleOut, o, distance, null);
				
				//property relevance
				Set<OWLEntity> set = SyntacticClusteringWithADEvaluationExperiment.getSignatureWithoutProperties(o);
				distance = DistanceCreator
						.createOWLEntityRelevanceAxiomBasedDistance(m);
				SyntacticClusteringWithADEvaluationExperiment.run("object-property-relevance", metrics, singleOut, o, distance, set);
				
				printMetrics(metrics, allResultsFile);
				firstTime = false;
			}
		}
	}

}
