/*******************************************************************************
 * Copyright (c) 2012 Eleni Mikroyannidi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Eleni Mikroyannidi, Luigi Iannone - initial API and implementation
 ******************************************************************************/
package org.coode.generalise.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.coode.basetest.TestHelper;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.oppl.Variable;
import org.coode.oppl.VariableVisitor;
import org.coode.oppl.bindingtree.Assignment;
import org.coode.oppl.bindingtree.AssignmentMap;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.generated.GeneratedVariable;
import org.coode.oppl.generated.RegexpGeneratedVariable;
import org.coode.oppl.rendering.ManchesterSyntaxRenderer;
import org.coode.oppl.utils.VariableExtractor;
import org.coode.oppl.variabletypes.InputVariable;
import org.coode.oppl.variabletypes.VariableTypeFactory;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.owl.generalise.structural.StructuralOWLObjectGeneralisation;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

/** @author eleni */
@SuppressWarnings("javadoc")
public class TestGeneralisation extends TestCase {
    private class UnionFinder extends OWLObjectVisitorExAdapter<Boolean> {
        public UnionFinder(Boolean defaultReturnValue) {
            super(defaultReturnValue);
        }

        @Override
        public Boolean visit(OWLObjectUnionOf desc) {
            return true;
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ToStringRenderer.getInstance().setRenderer(
                new ManchesterOWLSyntaxOWLObjectRendererImpl());
    }

    public void testMultipleStructuralGeneralisationWholeOntology() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        int generalisationCount = 0;
        OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
        Set<OWLAxiom> axioms = ontology.getAxioms();
        Set<OWLAxiom> generalisedAxioms = new HashSet<OWLAxiom>();
        for (OWLAxiom axiom : axioms) {
            if (!axiom.getAxiomType().equals(AxiomType.DECLARATION)) {
                ConstraintSystem constraintSystem = factory.createConstraintSystem();
                StructuralOWLObjectGeneralisation generalisation = new StructuralOWLObjectGeneralisation(
                        new OntologyManagerBasedOWLEntityProvider(ontologyManager),
                        constraintSystem);
                OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
                generalisedAxioms.add(generalised);
                generalisationCount++;
            }
        }
        assertTrue(generalisationCount > 1);
        System.out.printf("Generalised over %d axioms\n", generalisationCount);
        for (OWLAxiom owlAxiom : generalisedAxioms) {
            System.out.println(owlAxiom);
        }
    }

    public void testMultipleStructuralGeneralisation() {
        int generalisationCount = 0;
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
        Set<OWLAxiom> axioms = ontology.getAxioms();
        Set<OWLAxiom> generalisedAxioms = new HashSet<OWLAxiom>();
        for (OWLAxiom axiom : axioms) {
            ConstraintSystem constraintSystem = factory.createConstraintSystem();
            StructuralOWLObjectGeneralisation generalisation = new StructuralOWLObjectGeneralisation(
                    new OntologyManagerBasedOWLEntityProvider(ontologyManager),
                    constraintSystem);
            boolean doIt = axiom.accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                @Override
                public Boolean visit(OWLSubClassOfAxiom ax) {
                    boolean isRightSuperClass = ax.getSuperClass().accept(
                            new OWLObjectVisitorExAdapter<Boolean>(false) {
                                @Override
                                public Boolean visit(OWLObjectAllValuesFrom desc) {
                                    return !desc.getProperty().isAnonymous()
                                            && desc.getFiller().accept(
                                                    new UnionFinder(false));
                                }
                            });
                    return !ax.getSubClass().isAnonymous() && isRightSuperClass;
                }
            });
            if (doIt) {
                OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
                generalisedAxioms.add(generalised);
                generalisationCount++;
            }
        }
        assertTrue(generalisationCount > 1);
        assertTrue(generalisedAxioms.size() > 1);
        System.out.printf("%d Generalisations over %d axioms\n",
                generalisedAxioms.size(), generalisationCount);
        for (OWLAxiom owlAxiom : generalisedAxioms) {
            System.out.println(owlAxiom);
        }
    }

    public void testStructuralGeneralisation() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        StructuralOWLObjectGeneralisation generalisation = new StructuralOWLObjectGeneralisation(
                new OntologyManagerBasedOWLEntityProvider(ontologyManager),
                constraintSystem);
        OWLDataFactory dataFactory = ontologyManager.getOWLDataFactory();
        OWLClass margerita = dataFactory.getOWLClass(IRI
                .create("http://www.co-ode.org/ontologies/pizza/pizza.owl#Margherita"));
        OWLObjectProperty hasTopping = dataFactory.getOWLObjectProperty(IRI
                .create("http://www.co-ode.org/ontologies/pizza/pizza.owl#hasTopping"));
        OWLClass tomato = dataFactory
                .getOWLClass(IRI
                        .create("http://www.co-ode.org/ontologies/pizza/pizza.owl#TomatoTopping"));
        OWLClass mozzarella = dataFactory
                .getOWLClass(IRI
                        .create("http://www.co-ode.org/ontologies/pizza/pizza.owl#MozzarellaTopping"));
        OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(
                margerita,
                dataFactory.getOWLObjectAllValuesFrom(hasTopping,
                        dataFactory.getOWLObjectUnionOf(tomato, mozzarella)));
        OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
        System.out.println(generalised);
        VariableExtractor variableExtractor = new VariableExtractor(constraintSystem,
                true);
        Set<Variable<?>> variables = variableExtractor.extractVariables(generalised);
        assertTrue(variables.size() == 4);
        final Set<InputVariable<?>> inputVariables = new HashSet<InputVariable<?>>();
        final Set<GeneratedVariable<?>> generatedVariables = new HashSet<GeneratedVariable<?>>();
        for (Variable<?> variable : variables) {
            variable.accept(new VariableVisitor() {
                @Override
                public <P extends OWLObject> void visit(
                        RegexpGeneratedVariable<P> regExpGenerated) {}

                @Override
                public <P extends OWLObject> void visit(GeneratedVariable<P> v) {
                    generatedVariables.add(v);
                }

                @Override
                public <P extends OWLObject> void visit(InputVariable<P> v) {
                    inputVariables.add(v);
                }
            });
        }
        assertTrue(inputVariables.size() == 4);
        assertTrue(generatedVariables.size() == 0);
        AssignmentMap substitutions = generalisation.getSubstitutions();
        for (Variable<?> v : substitutions.getVariables()) {
            Set<OWLObject> set = substitutions.get(v);
            System.out.printf("%s %s\n", v.getName(), set);
        }
    }

    public void testSimpleGeneralisation() throws OWLOntologyCreationException,
            OPPLException {
        BindingNode aBindingNode = BindingNode.createNewEmptyBindingNode();
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;
        ontology = ontologyManager.createOntology();
        OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        InputVariable<OWLClassExpression> x = constraintSystem.createVariable("?x",
                VariableTypeFactory.getCLASSVariableType(), null);
        OWLClass a = ontologyManager.getOWLDataFactory()
                .getOWLClass(IRI.create("blah#a"));
        aBindingNode.addAssignment(new Assignment(x, a));
        OWLObjectGeneralisation generalisation = new OWLObjectGeneralisation(
                Collections.singleton(aBindingNode), constraintSystem);
        OWLSubClassOfAxiom axiom = ontologyManager.getOWLDataFactory()
                .getOWLSubClassOfAxiom(a, a);
        OWLObject generalised = axiom.accept(generalisation);
        ManchesterSyntaxRenderer renderer = factory
                .getManchesterSyntaxRenderer(constraintSystem);
        VariableExtractor variableExtractor = new VariableExtractor(constraintSystem,
                false);
        Set<Variable<?>> extractVariables = variableExtractor
                .extractVariables(generalised);
        assertFalse(extractVariables.isEmpty());
        assertTrue(extractVariables.contains(x));
        generalised.accept(renderer);
        System.out.println(renderer.toString());
    }

    public void testGeneralisationSingleVariableMultipleValues() throws OPPLException,
            OWLOntologyCreationException {
        BindingNode aBindingNode = BindingNode.createNewEmptyBindingNode();
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        InputVariable<OWLClassExpression> x = constraintSystem.createVariable("?x",
                VariableTypeFactory.getCLASSVariableType(), null);
        OWLClass a = ontologyManager.getOWLDataFactory()
                .getOWLClass(IRI.create("blah#a"));
        OWLClass b = ontologyManager.getOWLDataFactory()
                .getOWLClass(IRI.create("blah#b"));
        aBindingNode.addAssignment(new Assignment(x, a));
        BindingNode anotherBindingNode = BindingNode.createNewEmptyBindingNode();
        anotherBindingNode.addAssignment(new Assignment(x, b));
        OWLObjectGeneralisation generalisation = new OWLObjectGeneralisation(
                Arrays.asList(aBindingNode, anotherBindingNode), constraintSystem);
        OWLSubClassOfAxiom axiom = ontologyManager.getOWLDataFactory()
                .getOWLSubClassOfAxiom(
                        ontologyManager.getOWLDataFactory().getOWLObjectIntersectionOf(a,
                                b), a);
        OWLObject generalised = axiom.accept(generalisation);
        ManchesterSyntaxRenderer renderer = factory
                .getManchesterSyntaxRenderer(constraintSystem);
        VariableExtractor variableExtractor = new VariableExtractor(constraintSystem,
                false);
        Set<Variable<?>> extractVariables = variableExtractor
                .extractVariables(generalised);
        assertFalse(extractVariables.isEmpty());
        assertTrue(extractVariables.contains(x));
        generalised.accept(renderer);
        System.out.println(renderer.toString());
    }

    public void testGeneralisationSingleVariableMultipleValuesComplexClassExpression()
            throws OPPLException, OWLOntologyCreationException {
        BindingNode aBindingNode = BindingNode.createNewEmptyBindingNode();
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;
        ontology = ontologyManager.createOntology();
        OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        InputVariable<OWLClassExpression> x = constraintSystem.createVariable("?x",
                VariableTypeFactory.getCLASSVariableType(), null);
        OWLDataFactory df = ontologyManager.getOWLDataFactory();
        OWLClass a = df.getOWLClass(IRI.create("blah#a"));
        OWLClass b = df.getOWLClass(IRI.create("blah#b"));
        aBindingNode.addAssignment(new Assignment(x, a));
        BindingNode anotherBindingNode = BindingNode.createNewEmptyBindingNode();
        anotherBindingNode.addAssignment(new Assignment(x, b));
        OWLObjectGeneralisation generalisation = new OWLObjectGeneralisation(
                Arrays.asList(aBindingNode, anotherBindingNode), constraintSystem);
        OWLObjectProperty p = df.getOWLObjectProperty(IRI.create("blah#p"));
        OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(
                df.getOWLObjectIntersectionOf(df.getOWLObjectSomeValuesFrom(p, a),
                        df.getOWLObjectSomeValuesFrom(p, b)), a);
        OWLObject generalised = axiom.accept(generalisation);
        ManchesterSyntaxRenderer renderer = factory
                .getManchesterSyntaxRenderer(constraintSystem);
        VariableExtractor variableExtractor = new VariableExtractor(constraintSystem,
                false);
        Set<Variable<?>> extractVariables = variableExtractor
                .extractVariables(generalised);
        assertFalse(extractVariables.isEmpty());
        assertTrue(extractVariables.contains(x));
        generalised.accept(renderer);
        System.out.println(renderer.toString());
        Set<Variable<?>> variables = constraintSystem.getVariables();
        assertTrue(variables.size() == 3);
        for (Variable<?> variable : variables) {
            System.out.println(variable.render(constraintSystem));
        }
    }

    public void testVariableNameEscaping() {
        String variableNameChars = ConstraintSystem.VARIABLE_NAME_INVALID_CHARACTERS_REGEXP;
        String oldName = "?name";
        String newName = oldName.replaceAll(variableNameChars, "_");
        assertTrue(String.format("old name %s new name %s", oldName, newName),
                oldName.equals(newName));
        System.out.println(newName);
        oldName = "?'na:me";
        newName = oldName.replaceAll(variableNameChars, "_");
        assertFalse(String.format("old name %s new name %s", oldName, newName),
                oldName.equals(newName));
        System.out.println(newName);
    }
}
