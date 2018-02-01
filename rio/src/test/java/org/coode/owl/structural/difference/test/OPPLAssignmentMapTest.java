package org.coode.owl.structural.difference.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.oppl.bindingtree.Assignment;
import org.coode.oppl.bindingtree.AssignmentMap;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.oppl.variabletypes.InputVariable;
import org.coode.oppl.variabletypes.VariableType;
import org.coode.oppl.variabletypes.VariableTypeFactory;
import org.coode.utils.OntologyManagerUtils;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/** @author eleni */
@SuppressWarnings("javadoc")
public class OPPLAssignmentMapTest {
    @Test
    public void testAsignmentMapDisjointness() throws OWLOntologyCreationException, OPPLException {
        OWLOntologyManager manager = OntologyManagerUtils.ontologyManager();
        OWLOntology o = manager.createOntology();
        OWLDataFactory df = manager.getOWLDataFactory();
        OPPLFactory factory = new OPPLFactory(manager, o, null);
        ConstraintSystem constraintSystem = factory.createConstraintSystem();
        OWLClass a = df.getOWLClass(IRI.create("urn:test#A"));
        OWLClass b = df.getOWLClass(IRI.create("urn:test#B"));
        VariableType<?> variableType = VariableTypeFactory.getVariableType(a);
        InputVariable<?> var = constraintSystem.createVariable("?x", variableType, null);
        InputVariable<?> anothervar = constraintSystem.createVariable("?y", variableType, null);
        BindingNode bindingNode = BindingNode.createNewEmptyBindingNode();
        bindingNode.addAssignment(new Assignment(var, a));
        // bindingNode.addAssignment(new Assignment(var, b));
        BindingNode anotherBindingNode = BindingNode.createNewEmptyBindingNode();
        anotherBindingNode.addAssignment(new Assignment(var, b));
        BindingNode yBindingNode = BindingNode.createNewEmptyBindingNode();
        yBindingNode.addAssignment(new Assignment(anothervar, a));
        Set<BindingNode> nodes = new HashSet<>();
        nodes.add(bindingNode);
        // nodes.add(anotherBindingNode);
        Set<BindingNode> anothernodeset = new HashSet<>();
        anothernodeset.add(anotherBindingNode);
        // nodes.add(anotherBindingNode);
        Set<BindingNode> ynodeset = new HashSet<>();
        ynodeset.add(yBindingNode);
        AssignmentMap map = new AssignmentMap(nodes);
        AssignmentMap anothermap = new AssignmentMap(anothernodeset);
        AssignmentMap ymap = new AssignmentMap(ynodeset);
        List<AssignmentMap> maps = new ArrayList<>();
        maps.add(anothermap);
        maps.add(map);
        assertTrue(AssignmentMap.areDisjoint(maps));
        maps.clear();
        maps.add(map);
        maps.add(ymap);
        assertTrue(AssignmentMap.areDisjoint(maps));
        BindingNode thirdBindingNode = BindingNode.createNewEmptyBindingNode();
        thirdBindingNode.addAssignment(new Assignment(var, a));
        thirdBindingNode.addAssignment(new Assignment(var, b));
        Set<BindingNode> thirdset = new HashSet<>();
        thirdset.add(thirdBindingNode);
        maps.clear();
        maps.add(map);
        maps.add(new AssignmentMap(thirdset));
        assertFalse(AssignmentMap.areDisjoint(maps));
    }
}
