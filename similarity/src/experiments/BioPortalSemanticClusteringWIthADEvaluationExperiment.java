package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.stream.FileImageInputStream;
import javax.xml.parsers.ParserConfigurationException;

import org.coode.basetest.DistanceCreator;
import org.coode.distance.Distance;
import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillersFactplusplusImpl;
import org.coode.oppl.exceptions.OPPLException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

import uk.ac.manchester.cs.jfact.JFactReasoner;

public class BioPortalSemanticClusteringWIthADEvaluationExperiment extends SyntacticClusteringWithADEvaluationExperiment{

	private final static String RESULTS_BASE = "similarity/experiment-results/syntactic/bioportal/"; 

	public static void main(String[] args) throws OWLOntologyCreationException,
			OPPLException, ParserConfigurationException, FileNotFoundException {

		String bioportalList = "similarity/BioPortal_LocalRepositoryIRIs.txt";
//		BufferedReader d = new BufferedReader(new InputStreamReader(
//				new FileInputStream(new File(bioportalList))));
		BufferedReader d = new BufferedReader(new FileReader(new File(bioportalList)));
		ArrayList<String> inputList = new ArrayList<String>();
		try {
			String s = "";
			while((s = d.readLine())!=null){
				inputList.add(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		File output = new File(RESULTS_BASE + "bioportal-syntactic.csv");
		for(int i=0; i<inputList.size(); i++){
			System.out.println(inputList.get(i));
		}
		setupClusteringExperiment(inputList, output);
	}
	
	
	public static void setupClusteringExperiment(ArrayList<String> input,
			File allResultsFile) throws FileNotFoundException,
			OWLOntologyCreationException, OPPLException,
			ParserConfigurationException {
		for(final String s : input){
			final ArrayList<SimpleMetric<?>> metrics = new ArrayList<SimpleMetric<?>>();
			System.out
					.println("\n PopularityClusteringWithADEvaluationExperiment.main() \t " + s);
			String substring = s.substring(s.lastIndexOf("/")+1);
			String filename = RESULTS_BASE + substring.replaceAll(".owl", ".csv");
			System.out
					.println("BioPortalSyntacticClusteringWIthADEvaluationExperiment.setupClusteringExperiment() " + substring);
			String xml = RESULTS_BASE + substring.replaceAll(".owl", ".xml");
			File f = new File(filename);
			if (!f.exists()) {
				final PrintStream singleOut = new PrintStream(f);

				final OWLOntologyManager m = OWLManager
						.createOWLOntologyManager();
				final OWLOntology o = m
						.loadOntologyFromOntologyDocument(IRI.create(s));
				final AtomicReference<JFactReasoner>   reasoner = new AtomicReference<JFactReasoner>(); 
				final AtomicReference<KnowledgeExplorer> ke = new AtomicReference<KnowledgeExplorer>();
				Callable<Object> task1 = new Callable<Object>() {
					public Object call() throws OWLOntologyCreationException {
						//load ontology and get general ontology metrics
						metrics.add(new SimpleMetric<String>("Ontology", s));
						metrics.addAll(SemanticClusteringWithADEvaluationExperiment.getBasicOntologyMetrics(m));
						reasoner.set(new JFactReasoner(o,
								new SimpleConfiguration(), BufferingMode.NON_BUFFERING));
						reasoner.get().precomputeInferences();
						ke.set(new KnowledgeExplorerMaxFillersFactplusplusImpl(
								reasoner.get()));
						
						return null;
					}
				};
				runTaskWithTimeout(task1, 5, TimeUnit.MINUTES);
				
				Set<OWLAxiom> entailments = ke.get().getAxioms();
				Callable<Object> task2 = new Callable<Object>() {
					public Object call() throws OPPLException, ParserConfigurationException {
						//popularity distance
						Distance<OWLEntity> distance = DistanceCreator
								.createAxiomRelevanceAxiomBasedDistance(m);
						
						SyntacticClusteringWithADEvaluationExperiment.run("popularity", metrics, singleOut, o, distance, null);
						return null;
					}
				};
				runTaskWithTimeout(task2, 30, TimeUnit.MINUTES);
				
				Callable<Object> task3 = new Callable<Object>() {
					public Object call() throws OPPLException, ParserConfigurationException {
						//property relevance
						Set<OWLEntity> set = SyntacticClusteringWithADEvaluationExperiment.getSignatureWithoutProperties(o);
						Distance<OWLEntity> distance = DistanceCreator
								.createOWLEntityRelevanceAxiomBasedDistance(m);
						SyntacticClusteringWithADEvaluationExperiment.run("object-property-relevance", metrics, singleOut, o, distance, set);
						return null;
					}
				};
				runTaskWithTimeout(task3, 30, TimeUnit.MINUTES);
				
				//structural
				Callable<Object> task4 = new Callable<Object>() {
					public Object call() throws OPPLException, ParserConfigurationException {
						Distance<OWLEntity> distance = DistanceCreator
								.createStructuralAxiomRelevanceAxiomBasedDistance(m);
						SyntacticClusteringWithADEvaluationExperiment.run("structural", metrics, singleOut, o, distance, null);
						return null;
					}
				};
				runTaskWithTimeout(task4, 30, TimeUnit.MINUTES);
								
				printMetrics(metrics, allResultsFile);
				firstTime = false;
			}
		}
	}
	
	
	public static void runTaskWithTimeout(Callable<Object> task, long timeout, TimeUnit timeUnit){
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<Object> future = executor.submit(task);
		try {
			Object result = future.get(timeout, timeUnit);
		} catch (TimeoutException ex) {
			System.out.println("Took too long!");
		} catch (InterruptedException e) {
			System.out.println("Interrupted!");
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			future.cancel(false); // may or may not desire this
		}
	}

}
