package org.coode.owl.experiments.quality.assurance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.oppl.Variable;
import org.coode.oppl.bindingtree.Assignment;
import org.coode.oppl.bindingtree.AssignmentMap;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.exceptions.QuickFailRuntimeExceptionHandler;
import org.coode.oppl.rendering.ManchesterSyntaxRenderer;
import org.coode.oppl.variabletypes.InputVariable;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.proximitymatrix.cluster.GeneralisationDecompositionModel;
import org.coode.proximitymatrix.cluster.RegularitiesDecompositionModel;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.util.MultiMap;
import org.xml.sax.SAXException;

import experiments.ExperimentHelper;
import experiments.ExperimentUtils;

public class GeneralisationMetaprocessing<C extends Set<OWLEntity>> {
    public static void main(String[] args) {
        try {
            // ontology
            File ontoFile = new File(args[0]);
            // xml with regularities
            File xmlReg = new File(args[1]);
            System.out.println("Loading ontology... " + args[0]);
            OWLOntology ontology = ExperimentUtils.loadOntology(ontoFile);
            System.out.println("Loading regularities..." + args[1]);
            Set<Set<OWLEntity>> clusters = Utils.readFromXML(new FileInputStream(xmlReg),
                    ontology.getOWLOntologyManager());
            OPPLFactory opplfactory = new OPPLFactory(ontology.getOWLOntologyManager(),
                    ontology, null);
            ConstraintSystem constraintSystem = opplfactory.createConstraintSystem();
            OWLObjectGeneralisation unwrappedOWLObjectGeneralisation = ExperimentUtils
                    .getUnwrappedOWLObjectGeneralisation(clusters, ontology
                            .getImportsClosure(), constraintSystem, ExperimentHelper
                            .setManchesterSyntaxWithLabelRendering(ontology
                                    .getOWLOntologyManager()));
            MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
            for (Set<OWLEntity> cluster : clusters) {
                generalisationMap.putAll(Utils.buildGeneralisationMap(cluster,
                        ontology.getImportsClosure(), unwrappedOWLObjectGeneralisation,
                        new QuickFailRuntimeExceptionHandler()));
            }
            GeneralisationMetaprocessing<Set<OWLEntity>> metaprocessor = new GeneralisationMetaprocessing<Set<OWLEntity>>(
                    generalisationMap, constraintSystem);
            // System.out.println("Processing generalisation Map...");
            // MultiMap<OWLAxiom, OWLAxiomInstantiation>
            // processedGeneralisationMap = metaprocessor
            // .getProcessedGeneralisationMap();
            GeneralisationDecompositionModel<OWLEntity> model = new GeneralisationDecompositionModel<OWLEntity>(
                    clusters, ontology);
            model.setGeneralisationMap(generalisationMap);
            ExperimentUtils.saveToTXT(model, ontology,
                    new File(args[1].replaceAll(".xml", "_readable.txt")));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnknownOWLOntologyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OPPLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private final MultiMap<OWLAxiom, OWLAxiomInstantiation> map = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
    private final ConstraintSystem constraintSystem;

    public GeneralisationMetaprocessing(
            RegularitiesDecompositionModel<C, OWLEntity> model,
            ConstraintSystem constraintSystem) {
        this.map.putAll(Utils.extractGeneralisationMap(model));
        this.constraintSystem = constraintSystem;
    }

    public GeneralisationMetaprocessing(
            MultiMap<OWLAxiom, OWLAxiomInstantiation> generalisationMap,
            ConstraintSystem constraintSystem) {
        this.map.putAll(generalisationMap);
        this.constraintSystem = constraintSystem;
    }

    public MultiMap<OWLAxiom, OWLAxiomInstantiation> getProcessedGeneralisationMap()
            throws OPPLException {
        MultiMap<OWLAxiom, OWLAxiomInstantiation> toReturn = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
        Set<OWLAxiom> generalisations = map.keySet();
        ManchesterSyntaxRenderer renderer = constraintSystem.getOPPLFactory()
                .getManchesterSyntaxRenderer(constraintSystem);
        for (OWLAxiom g : generalisations) {
            MultiMap<Variable<?>, OWLObject> variableMap = extractVariableMap(map, g);
            Set<BindingNode> newVariableBindings = getNewVariableBindings(variableMap,
                    renderer);
            if (!newVariableBindings.isEmpty()) {
                OWLObjectGeneralisation owlObjectGeneralisation = new OWLObjectGeneralisation(
                        newVariableBindings, constraintSystem);
                for (OWLAxiomInstantiation inst : map.get(g)) {
                    OWLAxiom axiom = inst.getAxiom();
                    OWLAxiom generalised = (OWLAxiom) axiom
                            .accept(owlObjectGeneralisation);
                    toReturn.put(generalised, new OWLAxiomInstantiation(axiom,
                            owlObjectGeneralisation.getSubstitutions()));
                }
            }
        }
        return toReturn;
    }

    private MultiMap<Variable<?>, OWLObject> extractVariableMap(
            MultiMap<OWLAxiom, OWLAxiomInstantiation> m, OWLAxiom generalisation) {
        MultiMap<Variable<?>, OWLObject> variableMap = new MultiMap<Variable<?>, OWLObject>();
        Collection<OWLAxiomInstantiation> insts = m.get(generalisation);
        for (OWLAxiomInstantiation inst : insts) {
            AssignmentMap substitutions = inst.getSubstitutions();
            for (Variable<?> v : substitutions.keySet()) {
                Set<OWLObject> set = substitutions.get(v);
                variableMap.putAll(v, set);
            }
        }
        return variableMap;
    }

    private Set<BindingNode> getNewVariableBindings(
            MultiMap<Variable<?>, OWLObject> variableMap,
            ManchesterSyntaxRenderer renderer) throws OPPLException {
        Set<Variable<?>> toRemove = new HashSet<Variable<?>>();
        Set<BindingNode> bindings = new HashSet<BindingNode>();
        checkValuesWithSingleVariables(variableMap, renderer, toRemove, bindings);
        if (!bindings.isEmpty()) {
            for (Variable<?> v : variableMap.keySet()) {
                BindingNode bindingNode = BindingNode.createNewEmptyBindingNode();
                if (!toRemove.contains(v)) {
                    for (OWLObject o : variableMap.get(v)) {
                        bindingNode.addAssignment(new Assignment(v, o));
                        bindings.add(bindingNode);
                    }
                }
            }
        }
        return bindings;
    }

    private void checkValuesWithSingleVariables(
            MultiMap<Variable<?>, OWLObject> variableMap,
            ManchesterSyntaxRenderer renderer, Set<Variable<?>> toRemove,
            Set<BindingNode> bindings) throws OPPLException {
        for (Variable<?> v : variableMap.keySet()) {
            if (variableMap.get(v).size() == 1) {
                BindingNode bindingNode = BindingNode.createNewEmptyBindingNode();
                variableMap.get(v).iterator().next().accept(renderer);
                String name = renderer.toString().replaceAll("\\?", "_");
                String newVariableName = String.format("%s_%s", v.getName(), name)
                        .replaceAll(
                                ConstraintSystem.VARIABLE_NAME_INVALID_CHARACTERS_REGEXP,
                                "_");
                InputVariable<?> newVariable = constraintSystem.createVariable(
                        newVariableName, v.getType(), null);
                toRemove.add(v);
                bindingNode.addAssignment(new Assignment(newVariable, variableMap.get(v)
                        .iterator().next()));
                bindings.add(bindingNode);
            }
        }
    }
}
