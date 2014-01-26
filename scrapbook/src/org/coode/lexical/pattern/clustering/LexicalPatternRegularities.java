package org.coode.lexical.pattern.clustering;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
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
import org.coode.proximitymatrix.cluster.LexicalClusterModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.owl.ManchesterSyntaxRenderer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.util.MultiMap;

import experiments.ExperimentHelper;

/** @author eleni */
public class LexicalPatternRegularities {
    private final OWLOntology onto;
    private final MultiMap<String, OWLEntity> keyMap = new MultiMap<String, OWLEntity>();
    private final LexicalClusterModel model;

    /** @param ontology
     *            ontology
     * @param lexicalPatterns
     *            lexicalPatterns */
    public LexicalPatternRegularities(OWLOntology ontology, Set<String> lexicalPatterns) {
        onto = ontology;
        buildKeyMap(lexicalPatterns);
        model = new LexicalClusterModel(keyMap, onto);
    }

    private void buildKeyMap(Set<String> lexicalPatterns) {
        for (String keyword : lexicalPatterns) {
            keyMap.putAll(keyword, extractKeywordEntities(keyword));
        }
    }

    /** @param keyword
     *            keyword
     * @return entities */
    public Collection<OWLEntity> extractKeywordEntities(String keyword) {
        ManchesterSyntaxRenderer renderer = ExperimentHelper
                .setManchesterSyntaxWithLabelRendering(onto.getOWLOntologyManager());
        Set<OWLEntity> target = new HashSet<OWLEntity>();
        Set<OWLEntity> entities = onto.getSignature();
        for (OWLEntity e : entities) {
            if (renderer.render(e).toLowerCase().indexOf(keyword) != -1) {
                target.add(e);
            }
        }
        return target;
    }

    /** @param args
     *            args */
    public static void main(String[] args) {
        try {
            OWLOntology o = OWLManager
                    .createOWLOntologyManager()
                    .loadOntologyFromOntologyDocument(
                            new File(
                                    "/Users/elenimikroyannidi/eclipse-workspace/MyUtils/fma_skeleton_module20130620.owl"));
            Set<String> lexicalPatterns = new HashSet<String>(Arrays.asList("left",
                    "right"));
            LexicalPatternRegularities reg = new LexicalPatternRegularities(o,
                    lexicalPatterns);
            LexicalClusterModel model = reg.getAxiomRegularitiesFromLexicalPatterns();
            // ManchesterSyntaxRenderer renderer = Utils.enableLabelRendering(o
            // .getOWLOntologyManager());
            // ToStringRenderer.getInstance().setRenderer(renderer);
            System.out.println(model);
            File file = new File("FMA_test.xml");
            Utils.saveToXML(model, file);
            System.out.println("The file was saved in " + file.getPath());
        } catch (OWLOntologyCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** @return lexical cluster model */
    public LexicalClusterModel getAxiomRegularitiesFromLexicalPatterns() {
        OPPLFactory opplfactory = new OPPLFactory(onto.getOWLOntologyManager(), onto,
                null);
        ConstraintSystem constraintSystem = opplfactory.createConstraintSystem();
        final Map<String, MultiMap<OWLAxiom, OWLAxiomInstantiation>> toReturn = new HashMap<String, MultiMap<OWLAxiom, OWLAxiomInstantiation>>();
        try {
            // I need to preload all the constants into a variable before I
            // start
            Set<BindingNode> bindings = new HashSet<BindingNode>(keyMap.getAllValues()
                    .size());
            Set<OWLLiteral> constants = new HashSet<OWLLiteral>();
            for (OWLOntology ontology : onto.getImportsClosure()) {
                Set<OWLAxiom> axioms = ontology.getAxioms();
                for (OWLAxiom axiom : axioms) {
                    constants.addAll(OWLObjectExtractor.getAllOWLLiterals(axiom));
                }
            }
            if (!constants.isEmpty()) {
                String constantVariableName = "?constant";
                InputVariable<?> constantVariable = constraintSystem.createVariable(
                        constantVariableName,
                        VariableTypeFactory.getCONSTANTVariableType(), null);
                for (OWLLiteral owlLiteral : constants) {
                    BindingNode bindingNode = BindingNode.createNewEmptyBindingNode();
                    bindingNode
                            .addAssignment(new Assignment(constantVariable, owlLiteral));
                    bindings.add(bindingNode);
                }
            }
            Set<OWLAxiom> axioms = extractLexicalPatternsUsage();
            for (String keyword : keyMap.keySet()) {
                Collection<OWLEntity> cluster = keyMap.get(keyword);
                OWLObjectGeneralisation owlObjectGeneralisation = getOWLObjectGeneralisation(
                        constraintSystem, keyword, bindings);
                MultiMap<OWLAxiom, OWLAxiomInstantiation> map = buildGeneralisationMap(
                        cluster, axioms, owlObjectGeneralisation);
                MultiMap<OWLAxiom, OWLAxiomInstantiation> sortedGeneralisationMap = sortGeneralisations(map);
                toReturn.put(keyword, sortedGeneralisationMap);
                model.put(keyword, sortedGeneralisationMap);
            }
        } catch (OPPLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return model;
    }

    /** @param map
     *            map
     * @return gemeralisation map */
    public MultiMap<OWLAxiom, OWLAxiomInstantiation> sortGeneralisations(
            final MultiMap<OWLAxiom, OWLAxiomInstantiation> map) {
        // order the generalisation map according to size
        MultiMap<OWLAxiom, OWLAxiomInstantiation> toReturn = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
        Comparator<OWLAxiom> size_comparator = new Comparator<OWLAxiom>() {
            @Override
            public int compare(OWLAxiom axiom, OWLAxiom otherAxiom) {
                Collection<OWLAxiomInstantiation> axiomInstatiations = map.get(axiom);
                Collection<OWLAxiomInstantiation> otherAxiomInstantiations = map
                        .get(otherAxiom);
                return axiomInstatiations.size() - otherAxiomInstantiations.size();
            }
        };
        Set<OWLAxiom> orderedGenSet = new TreeSet<OWLAxiom>(
                Collections.reverseOrder(size_comparator));
        orderedGenSet.addAll(map.keySet());
        for (OWLAxiom ax : orderedGenSet) {
            toReturn.putAll(ax, map.get(ax));
        }
        return toReturn;
    }

    /** @param cluster
     *            cluster
     * @param axioms
     *            axioms
     * @param generalisation
     *            generalisation
     * @return generalisation map */
    public MultiMap<OWLAxiom, OWLAxiomInstantiation> buildGeneralisationMap(
            Collection<OWLEntity> cluster, Set<OWLAxiom> axioms,
            OWLObjectGeneralisation generalisation) {
        MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
        // OWLOntologyAnnotationClusterDetector visitor = new
        // OWLOntologyAnnotationClusterDetector(
        // cluster, ontologies);
        // TODO: put a cache here.
        for (OWLAxiom axiom : axioms) {
            if (axiom.getAxiomType() != AxiomType.DECLARATION
                    && axiom.getAxiomType() != AxiomType.ANNOTATION_ASSERTION
                    && axiom.getAxiomType() != AxiomType.ANNOTATION_PROPERTY_DOMAIN
                    && axiom.getAxiomType() != AxiomType.ANNOTATION_PROPERTY_RANGE) {
                Set<OWLEntity> signature = new HashSet<OWLEntity>(axiom.getSignature());
                signature.retainAll(cluster);
                if (!signature.isEmpty()) {
                    generalisation.clearSubstitutions();
                    OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
                    generalisationMap.put(generalised, new OWLAxiomInstantiation(axiom,
                            generalisation.getSubstitutions()));
                }
            }
        }
        return generalisationMap;
    }

    private Set<OWLAxiom> extractLexicalPatternsUsage() {
        Set<OWLAxiom> usage = new HashSet<OWLAxiom>();
        for (OWLObject o : keyMap.getAllValues()) {
            usage.addAll(onto.getReferencingAxioms((OWLEntity) o));
        }
        return usage;
    }

    /** @param constraintSystem
     *            constraintSystem
     * @param keyword
     *            keyword
     * @param bindings
     *            bindings
     * @return generalisation
     * @throws OPPLException
     *             OPPLException */
    public OWLObjectGeneralisation getOWLObjectGeneralisation(
            ConstraintSystem constraintSystem, String keyword, Set<BindingNode> bindings)
            throws OPPLException {
        Collection<OWLEntity> cluster = keyMap.get(keyword);
        if (!cluster.isEmpty()) {
            String name = keyword;
            // String name = getLCSVariableName(constraintSystem, renderer,
            // names, rootNames, axiomProvider, cluster);
            OWLObject object = cluster.iterator().next();
            VariableType<?> variableType = VariableTypeFactory.getVariableType(object);
            if (variableType != null) {
                String variableName = String.format("?%s", name.replaceAll("\\?", "_"))
                        .replaceAll(
                                ConstraintSystem.VARIABLE_NAME_INVALID_CHARACTERS_REGEXP,
                                "_");
                InputVariable<?> variable = constraintSystem.createVariable(variableName,
                        variableType, null);
                for (OWLObject o : cluster) {
                    BindingNode bindingNode = BindingNode.createNewEmptyBindingNode();
                    if (VariableTypeFactory.getVariableType(o) == variable.getType()) {
                        bindingNode.addAssignment(new Assignment(variable, o));
                        bindings.add(bindingNode);
                    }
                }
            }
        }
        return new UnwrappedOWLObjectGeneralisation(bindings, constraintSystem);
    }
}
