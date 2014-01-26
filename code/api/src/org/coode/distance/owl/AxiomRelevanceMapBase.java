package org.coode.distance.owl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.oppl.bindingtree.Assignment;
import org.coode.oppl.bindingtree.BindingNode;
import org.coode.owl.generalise.BindingNodeGeneralisationTreeNode;
import org.coode.owl.generalise.GeneralisationTreeNode;
import org.coode.owl.generalise.GeneralisationTreeNodeVisitorAdapter;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.utils.TreeNode;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.OWLObjectVisitorAdapter;

/** @author eleni */
public class AxiomRelevanceMapBase {
    protected final Map<OWLAxiom, OWLAxiom> generalisationMap = new HashMap<OWLAxiom, OWLAxiom>();
    protected final MultiMap<OWLAxiom, OWLAxiomInstantiation> instantionMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
    protected final MultiMap<OWLAxiom, OWLEntity> relevanceMap = new MultiMap<OWLAxiom, OWLEntity>();

    /** @param axiom
     *            axiom
     * @return entities */
    public Collection<OWLEntity> getRelevantEntities(OWLAxiom axiom) {
        OWLAxiom generalisedOWLAxiom = generalisationMap.get(axiom);
        if (generalisedOWLAxiom != null) {
            return relevanceMap.get(generalisedOWLAxiom);
        } else {
            return Collections.emptySet();
        }
    }

    protected Set<OWLEntity> extractValues(
            GeneralisationTreeNode<?> generalisationTreeNode) {
        final Set<OWLEntity> toReturn = new HashSet<OWLEntity>();
        generalisationTreeNode.accept(new GeneralisationTreeNodeVisitorAdapter() {
            @Override
            public void visitBindingNodeGeneralisationTreeNode(
                    BindingNodeGeneralisationTreeNode bindingNodeGeneralisationTreeNode) {
                BindingNode bindingNode = bindingNodeGeneralisationTreeNode
                        .getUserObject();
                Collection<Assignment> assignments = bindingNode.getAssignments();
                for (Assignment assignment : assignments) {
                    OWLObject assignmentValue = assignment.getAssignment();
                    assignmentValue.accept(new OWLObjectVisitorAdapter() {
                        @Override
                        public void visit(OWLClass desc) {
                            toReturn.add(desc);
                        }

                        @Override
                        public void visit(OWLAnnotationProperty property) {
                            toReturn.add(property);
                        }

                        @Override
                        public void visit(OWLDataProperty property) {
                            toReturn.add(property);
                        }

                        @Override
                        public void visit(OWLObjectProperty property) {
                            toReturn.add(property);
                        }

                        @Override
                        public void visit(OWLNamedIndividual individual) {
                            toReturn.add(individual);
                        }
                    });
                }
            }
        });
        List<TreeNode<?>> children = generalisationTreeNode.getChildren();
        for (TreeNode<?> child : children) {
            toReturn.addAll(extractValues((GeneralisationTreeNode<?>) child));
        }
        return toReturn;
    }
}
