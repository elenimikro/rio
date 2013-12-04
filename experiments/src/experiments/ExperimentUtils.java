package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.knowledgeexplorer.KnowledgeExplorer;
import org.coode.knowledgeexplorer.KnowledgeExplorerMaxFillersImpl;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.Variable;
import org.coode.oppl.bindingtree.Assignment;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.utils.OWLObjectExtractor;
import org.coode.oppl.variabletypes.InputVariable;
import org.coode.oppl.variabletypes.VariableType;
import org.coode.oppl.variabletypes.VariableTypeFactory;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.owl.generalise.UnwrappedOWLObjectGeneralisation;
import org.coode.owl.wrappers.OWLAxiomProvider;
import org.coode.owl.wrappers.OWLOntologyManagerBasedOWLAxiomProvider;
import org.coode.proximitymatrix.cluster.RegularitiesDecompositionModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.SimpleMetric;
import org.coode.utils.owl.LeastCommonSubsumer;
import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.factplusplus.owlapiv3.FaCTPlusPlusReasoner;
import uk.ac.manchester.cs.factplusplus.owlapiv3.OWLKnowledgeExplorationReasonerWrapper;

public class ExperimentUtils {
    public static KnowledgeExplorer runFactplusplusKnowledgeExplorerReasoner(
            OWLOntology ontology) {
        OWLReasoner reasoner = new FaCTPlusPlusReasoner(ontology,
                new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        KnowledgeExplorer ke = new KnowledgeExplorerMaxFillersImpl(
                reasoner,
                new OWLKnowledgeExplorationReasonerWrapper(new FaCTPlusPlusReasoner(
                        ontology, new SimpleConfiguration(), BufferingMode.NON_BUFFERING)));
        return ke;
    }

    public static OWLOntology loadOntology(File ontoFile) {
        OWLOntology ontology = null;
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            ontology = manager.loadOntologyFromOntologyDocument(ontoFile);
        } catch (OWLOntologyCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ontology;
    }

    public static OWLOntology saveOntology(File ontoFile, Set<OWLAxiom> axioms) {
        try {
            OWLOntology ontology = OWLManager.createOWLOntologyManager().createOntology(
                    axioms);
            FileOutputStream out = new FileOutputStream(ontoFile);
            ontology.getOWLOntologyManager().saveOntology(ontology, out);
            out.close();
            return ontology;
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
        return null;
    }

    protected static boolean firstTime = true;

    public static void printMetrics(List<SimpleMetric<?>> metrics, File file)
            throws FileNotFoundException {
        FileOutputStream fout = new FileOutputStream(file, true);
        PrintStream out = new PrintStream(fout);
        // if(!file.exists()){
        if (firstTime) {
            for (int i = 0; i < metrics.size(); i++) {
                out.print(metrics.get(i).getName() + ",");
            }
            out.println();
        }
        for (int i = 0; i < metrics.size(); i++) {
            out.print(metrics.get(i).getValue() + ",");
        }
        firstTime = false;
        out.println();
        out.close();
    }

    public static ArrayList<String> extractListFromFile(String bioportalList) {
        ArrayList<String> inputList = new ArrayList<String>();
        try {
            BufferedReader d = new BufferedReader(new FileReader(new File(bioportalList)));
            String s = "";
            while ((s = d.readLine()) != null) {
                inputList.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputList;
    }

    public static <C extends Set<OWLEntity>> void saveToTXT(
            RegularitiesDecompositionModel<C, OWLEntity> model, OWLOntology o,
            File txtFile) {
        try {
            PrintStream out = new PrintStream(txtFile);
            ManchesterSyntaxRenderer renderer = ExperimentHelper
                    .setManchesterSyntaxWithLabelRendering(o.getOWLOntologyManager());
            ToStringRenderer.getInstance().setRenderer(renderer);
            MultiMap<OWLAxiom, OWLAxiomInstantiation> map = model.getGeneralisationMap();
            writeClusters(model.getClusterList(), out, map, renderer);
            writeGeneralisations(out, map, renderer);
            out.close();
            System.out.println("File was saved in " + txtFile.getPath());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static <C extends Set<OWLEntity>> void writeClusters(List<C> list,
            PrintStream out, MultiMap<OWLAxiom, OWLAxiomInstantiation> map,
            ManchesterSyntaxRenderer renderer) {
        for (Set<OWLEntity> cluster : list) {
            Variable<?> variable = Utils.getVariable(cluster, map);
            out.println("Cluster " + variable);
            out.println("Cluster size: " + cluster.size());
            out.print("[");
            for (OWLEntity e : cluster) {
                out.print(renderer.render(e) + ", ");
            }
            out.println("]");
        }
    }

    public static void writeGeneralisations(PrintStream out,
            MultiMap<OWLAxiom, OWLAxiomInstantiation> map,
            ManchesterSyntaxRenderer renderer) {
        out.println("Generalisations:");
        for (OWLAxiom ax : map.keySet()) {
            out.println(renderer.render(ax));
            out.println("\t Instantiations: " + "(" + map.get(ax).size() + ")");
            for (OWLAxiomInstantiation inst : map.get(ax)) {
                out.println("\t" + inst + " ");
            }
        }
    }

    public static <O extends OWLObject> OWLObjectGeneralisation
            getUnwrappedOWLObjectGeneralisation(
                    Collection<? extends Collection<? extends O>> set,
                    Collection<? extends OWLOntology> ontologies,
                    ConstraintSystem constraintSystem, ManchesterSyntaxRenderer renderer)
                    throws OPPLException {
        Set<BindingNode> bindings = new HashSet<BindingNode>(set.size());
        // I need to preload all the constants into a variable before I start
        Set<OWLLiteral> constants = new HashSet<OWLLiteral>();
        for (OWLOntology ontology : ontologies) {
            Set<OWLAxiom> axioms = ontology.getAxioms();
            for (OWLAxiom axiom : axioms) {
                constants.addAll(OWLObjectExtractor.getAllOWLLiterals(axiom));
            }
        }
        if (!constants.isEmpty()) {
            String constantVariableName = "?constant";
            InputVariable<?> constantVariable = constraintSystem.createVariable(
                    constantVariableName, VariableTypeFactory.getCONSTANTVariableType(),
                    null);
            for (OWLLiteral owlLiteral : constants) {
                BindingNode bindingNode = BindingNode.createNewEmptyBindingNode();
                bindingNode.addAssignment(new Assignment(constantVariable, owlLiteral));
                bindings.add(bindingNode);
            }
        }
        Set<String> names = new HashSet<String>();
        Set<String> rootNames = new HashSet<String>(Arrays.asList(null, "Thing",
                "topObjectProperty", "topDataProperty", "topAnnotationProperty"));
        // FIXME!! get rid of the axiomProvider
        OWLAxiomProvider axiomProvider = new OWLOntologyManagerBasedOWLAxiomProvider(
                constraintSystem.getOntologyManager());
        for (Collection<? extends O> cluster : set) {
            if (!cluster.isEmpty()) {
                LeastCommonSubsumer<O, ?> lcs = LeastCommonSubsumer.build(cluster,
                        axiomProvider, constraintSystem.getOntologyManager()
                                .getOWLDataFactory());
                String name;
                // the next condition seems to create problems
                if (lcs != null) {
                    OWLObject x = lcs.get(cluster);
                    name = Utils.createName(renderer.render(x), names, rootNames);
                } else {
                    name = Utils.createName(null, names, rootNames);
                }
                O object = cluster.iterator().next();
                VariableType<?> variableType = VariableTypeFactory
                        .getVariableType(object);
                if (variableType != null) {
                    String variableName = String
                            .format("?%s", name.replaceAll("\\?", "_"))
                            .replaceAll(
                                    ConstraintSystem.VARIABLE_NAME_INVALID_CHARACTERS_REGEXP,
                                    "_");
                    InputVariable<?> variable = constraintSystem.createVariable(
                            variableName, variableType, null);
                    for (O o : cluster) {
                        BindingNode bindingNode = BindingNode.createNewEmptyBindingNode();
                        if (VariableTypeFactory.getVariableType(o) == variable.getType()) {
                            bindingNode.addAssignment(new Assignment(variable, o));
                            bindings.add(bindingNode);
                        }
                    }
                }
            }
        }
        return new UnwrappedOWLObjectGeneralisation(bindings, constraintSystem);
    }

    public static double computeTotalMeanClusterSimilarity(
            List<Set<OWLEntity>> clusterListA, List<Set<OWLEntity>> clusterListB,
            Map<OWLEntity, Integer> mapA, Map<OWLEntity, Integer> mapB,
            Set<OWLEntity> signature) {
        Set<OWLEntity> visitedEntities = new HashSet<OWLEntity>();
        ClusterComparison<OWLEntity> comparator = new ClusterComparison<OWLEntity>();
        double totalSimilarity = 0;
        double counter = 0;
        // compute similarity
        for (OWLEntity s : signature) {
            if (!visitedEntities.contains(s)) {
                // A to B
                Integer indexA = mapA.get(s);
                Integer indexB = mapB.get(s);
                if (indexA != null && indexB != null) {
                    double clusterSimilarity = comparator.getClusterSimilarity(
                            clusterListA.get(indexA), clusterListB.get(indexB));
                    Set<OWLEntity> intersection = comparator.getIntersection(
                            clusterListA.get(indexA), clusterListB.get(indexB));
                    visitedEntities.addAll(intersection);
                    // indi_out.println("Intersection, " + intersection);
                    totalSimilarity += clusterSimilarity;
                    counter++;
                }
            }
        }
        double toReturn = totalSimilarity / counter;
        return toReturn;
    }
}
