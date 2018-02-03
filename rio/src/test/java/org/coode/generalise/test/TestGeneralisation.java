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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.coode.basetest.TestHelper;
import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.oppl.Variable;
import org.coode.oppl.VariableVisitor;
import org.coode.oppl.bindingtree.Assignment;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.generated.GeneratedVariable;
import org.coode.oppl.generated.RegexpGeneratedVariable;
import org.coode.oppl.utils.VariableExtractor;
import org.coode.oppl.variabletypes.InputVariable;
import org.coode.oppl.variabletypes.VariableTypeFactory;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.owl.generalise.structural.StructuralOWLObjectGeneralisation;
import org.coode.owl.wrappers.OntologyManagerBasedOWLEntityProvider;
import org.coode.proximitymatrix.cluster.Utils;
import org.coode.utils.OntologyManagerUtils;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/** @author eleni */
@SuppressWarnings("javadoc")
public class TestGeneralisation {
    private class UnionFinder implements OWLObjectVisitorEx<Boolean> {
        private Boolean value;

        public UnionFinder(Boolean defaultReturnValue) {
            value = defaultReturnValue;
        }

        @Override
        public <T> Boolean doDefault(T object) {
            return value;
        }

        @Override
        public Boolean visit(OWLObjectUnionOf desc) {
            return Boolean.TRUE;
        }
    }

    @Test
    public void testMultipleStructuralGeneralisationWholeOntology() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
        Set<OWLAxiom> generalisedAxioms = new HashSet<>();
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        Utils.axiomsSkipDeclarations(Collections.singleton(ontology)).forEach(axiom -> {
            StructuralOWLObjectGeneralisation generalisation =
                new StructuralOWLObjectGeneralisation(
                    new OntologyManagerBasedOWLEntityProvider(ontologyManager), constraintSystem);
            OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
            generalisedAxioms.add(generalised);
        });

        assertTrue(generalisedAxioms.size() > 1);

    }

    @Test
    public void testMultipleStructuralGeneralisation() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
        Set<OWLAxiom> generalisedAxioms = new HashSet<>();
        for (OWLAxiom axiom : asList(ontology.axioms())) {
            ConstraintSystem constraintSystem = factory.createConstraintSystem();
            StructuralOWLObjectGeneralisation generalisation =
                new StructuralOWLObjectGeneralisation(
                    new OntologyManagerBasedOWLEntityProvider(ontologyManager), constraintSystem);
            boolean doIt = axiom.accept(new OWLObjectVisitorEx<Boolean>() {
                @Override
                public <T> Boolean doDefault(T object) {
                    return Boolean.FALSE;
                }

                @Override
                public Boolean visit(OWLSubClassOfAxiom ax) {
                    return Boolean.valueOf(!ax.getSubClass().isAnonymous()
                        && ax.getSuperClass().accept(new OWLObjectVisitorEx<Boolean>() {
                            @Override
                            public <T> Boolean doDefault(T object) {
                                return Boolean.FALSE;
                            }

                            @Override
                            public Boolean visit(OWLObjectAllValuesFrom desc) {
                                return Boolean
                                    .valueOf(!desc.getProperty().isAnonymous() && desc.getFiller()
                                        .accept(new UnionFinder(Boolean.FALSE)).booleanValue());
                            }
                        }).booleanValue());
                }
            }).booleanValue();
            if (doIt) {
                OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
                generalisedAxioms.add(generalised);
            }
        }
        assertTrue(generalisedAxioms.size() > 1);
    }

    @Test
    public void testStructuralGeneralisation() {
        OWLOntology ontology = TestHelper.getPizza();
        OWLOntologyManager ontologyManager = ontology.getOWLOntologyManager();
        OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        StructuralOWLObjectGeneralisation generalisation = new StructuralOWLObjectGeneralisation(
            new OntologyManagerBasedOWLEntityProvider(ontologyManager), constraintSystem);
        OWLDataFactory dataFactory = ontologyManager.getOWLDataFactory();
        OWLClass margerita = dataFactory
            .getOWLClass(IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#Margherita"));
        OWLObjectProperty hasTopping = dataFactory.getOWLObjectProperty(
            IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#hasTopping"));
        OWLClass tomato = dataFactory.getOWLClass(
            IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#TomatoTopping"));
        OWLClass mozzarella = dataFactory.getOWLClass(
            IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#MozzarellaTopping"));
        OWLSubClassOfAxiom axiom =
            dataFactory.getOWLSubClassOfAxiom(margerita, dataFactory.getOWLObjectAllValuesFrom(
                hasTopping, dataFactory.getOWLObjectUnionOf(tomato, mozzarella)));
        OWLAxiom generalised = (OWLAxiom) axiom.accept(generalisation);
        VariableExtractor variableExtractor = new VariableExtractor(constraintSystem, true);
        Set<Variable<?>> variables = variableExtractor.extractVariables(generalised);
        assertTrue(variables.size() == 4);
        final Set<InputVariable<?>> inputVariables = new HashSet<>();
        final Set<GeneratedVariable<?>> generatedVariables = new HashSet<>();
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
    }

    @Test
    public void testSimpleGeneralisation() throws OWLOntologyCreationException, OPPLException {
        BindingNode aBindingNode = BindingNode.createNewEmptyBindingNode();
        OWLOntologyManager ontologyManager = OntologyManagerUtils.ontologyManager();
        OWLOntology ontology;
        ontology = ontologyManager.createOntology();
        OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        InputVariable<OWLClassExpression> x =
            constraintSystem.createVariable("?x", VariableTypeFactory.getCLASSVariableType(), null);
        OWLClass a = ontologyManager.getOWLDataFactory().getOWLClass(IRI.create("blah#a"));
        aBindingNode.addAssignment(new Assignment(x, a));
        OWLObjectGeneralisation generalisation =
            new OWLObjectGeneralisation(Collections.singleton(aBindingNode), constraintSystem);
        OWLSubClassOfAxiom axiom = ontologyManager.getOWLDataFactory().getOWLSubClassOfAxiom(a, a);
        OWLObject generalised = axiom.accept(generalisation);
        VariableExtractor variableExtractor = new VariableExtractor(constraintSystem, false);
        Set<Variable<?>> extractVariables = variableExtractor.extractVariables(generalised);
        assertFalse(extractVariables.isEmpty());
        assertTrue(extractVariables.contains(x));
    }

    @Test
    public void testGeneralisationSingleVariableMultipleValues()
        throws OPPLException, OWLOntologyCreationException {
        BindingNode aBindingNode = BindingNode.createNewEmptyBindingNode();
        OWLOntologyManager ontologyManager = OntologyManagerUtils.ontologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        InputVariable<OWLClassExpression> x =
            constraintSystem.createVariable("?x", VariableTypeFactory.getCLASSVariableType(), null);
        OWLClass a = ontologyManager.getOWLDataFactory().getOWLClass(IRI.create("blah#a"));
        OWLClass b = ontologyManager.getOWLDataFactory().getOWLClass(IRI.create("blah#b"));
        aBindingNode.addAssignment(new Assignment(x, a));
        BindingNode anotherBindingNode = BindingNode.createNewEmptyBindingNode();
        anotherBindingNode.addAssignment(new Assignment(x, b));
        OWLObjectGeneralisation generalisation = new OWLObjectGeneralisation(
            Arrays.asList(aBindingNode, anotherBindingNode), constraintSystem);
        OWLSubClassOfAxiom axiom = ontologyManager.getOWLDataFactory().getOWLSubClassOfAxiom(
            ontologyManager.getOWLDataFactory().getOWLObjectIntersectionOf(a, b), a);
        OWLObject generalised = axiom.accept(generalisation);
        VariableExtractor variableExtractor = new VariableExtractor(constraintSystem, false);
        Set<Variable<?>> extractVariables = variableExtractor.extractVariables(generalised);
        assertFalse(extractVariables.isEmpty());
        assertTrue(extractVariables.contains(x));
    }

    @Test
    public void testGeneralisationSingleVariableMultipleValuesComplexClassExpression()
        throws OPPLException, OWLOntologyCreationException {
        BindingNode aBindingNode = BindingNode.createNewEmptyBindingNode();
        OWLOntologyManager ontologyManager = OntologyManagerUtils.ontologyManager();
        OWLOntology ontology;
        ontology = ontologyManager.createOntology();
        OPPLFactory factory = new OPPLFactory(ontologyManager, ontology, null);
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        InputVariable<OWLClassExpression> x =
            constraintSystem.createVariable("?x", VariableTypeFactory.getCLASSVariableType(), null);
        OWLDataFactory df = ontologyManager.getOWLDataFactory();
        OWLClass a = df.getOWLClass(IRI.create("blah#a"));
        OWLClass b = df.getOWLClass(IRI.create("blah#b"));
        aBindingNode.addAssignment(new Assignment(x, a));
        BindingNode anotherBindingNode = BindingNode.createNewEmptyBindingNode();
        anotherBindingNode.addAssignment(new Assignment(x, b));
        OWLObjectGeneralisation generalisation = new OWLObjectGeneralisation(
            Arrays.asList(aBindingNode, anotherBindingNode), constraintSystem);
        OWLObjectProperty p = df.getOWLObjectProperty(IRI.create("blah#p"));
        OWLSubClassOfAxiom axiom = df.getOWLSubClassOfAxiom(df.getOWLObjectIntersectionOf(
            df.getOWLObjectSomeValuesFrom(p, a), df.getOWLObjectSomeValuesFrom(p, b)), a);
        OWLObject generalised = axiom.accept(generalisation);
        VariableExtractor variableExtractor = new VariableExtractor(constraintSystem, false);
        Set<Variable<?>> extractVariables = variableExtractor.extractVariables(generalised);
        assertFalse(extractVariables.isEmpty());
        assertTrue(extractVariables.contains(x));
        Set<Variable<?>> variables = constraintSystem.getVariables();
        assertTrue(variables.size() == 3);
    }

    @Test
    public void testVariableNameEscaping() {
        String variableNameChars = ConstraintSystem.VARIABLE_NAME_INVALID_CHARACTERS_REGEXP;
        String oldName = "?name";
        String newName = oldName.replaceAll(variableNameChars, "_");
        assertTrue(String.format("old name %s new name %s", oldName, newName),
            oldName.equals(newName));
        oldName = "?'na:me";
        newName = oldName.replaceAll(variableNameChars, "_");
        assertFalse(String.format("old name %s new name %s", oldName, newName),
            oldName.equals(newName));
    }
}
