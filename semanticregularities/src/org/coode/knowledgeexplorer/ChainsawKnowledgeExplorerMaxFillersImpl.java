package org.coode.knowledgeexplorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.knowledgeexploration.OWLKnowledgeExplorerReasoner;
import org.semanticweb.owlapi.reasoner.knowledgeexploration.OWLKnowledgeExplorerReasoner.RootNode;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
public class ChainsawKnowledgeExplorerMaxFillersImpl implements KnowledgeExplorer {
    private final OWLKnowledgeExplorerReasoner r;
    private final OWLReasoner reasoner;
    private final OWLOntology o;
    private final OWLOntologyManager manager;
    private final Set<OWLEntity> signature = new HashSet<OWLEntity>();
    private final MultiMap<OWLEntity, OWLAxiom> axiomMap = new MultiMap<OWLEntity, OWLAxiom>();
    private final OWLDataFactory dataFactory;

    /** @param reasoner
     *            reasoner
     * @param r
     *            r */
    public ChainsawKnowledgeExplorerMaxFillersImpl(OWLReasoner reasoner,
            OWLKnowledgeExplorerReasoner r) {
        if (reasoner == null) {
            throw new NullPointerException("OWLKnowledgeExplorerReasoner cannot be null");
        }
        o = reasoner.getRootOntology();
        this.reasoner = reasoner;
        this.r = r;
        this.r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        manager = o.getOWLOntologyManager();
        dataFactory = manager.getOWLDataFactory();
        buildAxiomMap();
    }

    private void buildAxiomMap() {
        List<OWLClass> satisfiable = new ArrayList<OWLClass>();
        for (OWLClass c : o.getClassesInSignature(true)) {
            if (reasoner.isSatisfiable(c)) {
                satisfiable.add(c);
            }
        }
        for (OWLClass c : satisfiable) {
            RootNode root = r.getRoot(c);
            // System.out.println(r.getClass().getSimpleName() + " ROOT CLASS "
            // + c);
            Set<OWLAxiom> computeAxioms = computeAxioms(root, c);
            computeAxioms.addAll(getNamedSubClassAxioms(root, c));
            System.out.println(computeAxioms);
            for (OWLAxiom ax : computeAxioms) {
                Set<OWLEntity> sig = ax.getSignature();
                for (OWLEntity e : sig) {
                    axiomMap.put(e, ax);
                    if (!e.isOWLObjectProperty()) {
                        signature.add(e);
                    }
                }
            }
            // signature.add(c);
        }
    }

    private Set<OWLAxiom> computeAxioms(RootNode root, OWLClass rootClass) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        Node<? extends OWLObjectPropertyExpression> objectNeighbours = r
                .getObjectNeighbours(root, false);
        Set<? extends OWLObjectPropertyExpression> entities = objectNeighbours
                .getEntities();
        for (OWLObjectPropertyExpression prop : entities) {
            if (prop instanceof OWLObjectProperty) {
                Collection<RootNode> objectRootNodes = r.getObjectNeighbours(root,
                        prop.asOWLObjectProperty());
                Set<OWLClassExpression> fillers = new HashSet<OWLClassExpression>();
                fillers.add(dataFactory.getOWLThing());
                for (RootNode objectRootNode : objectRootNodes) {
                    fillers.addAll(getMaxFillers(objectRootNode, new HashSet<RootNode>(),
                            rootClass));
                }
                axioms.addAll(check(rootClass, prop, fillers));
            }
        }
        return axioms;
    }

    // private Set<OWLClassExpression> getFillers(RootNode node) {
    // Set<OWLClassExpression> fillers = new HashSet<OWLClassExpression>();
    // // Fillers.add(Thing); // we never get Thing from KE
    // for (OWLClassExpression c : r.getObjectLabel(node, false).getEntities())
    // {
    // // all in the label is a filler
    // fillers.add(c);
    // }
    // for (OWLObjectPropertyExpression prop : r.getObjectNeighbours(node,
    // false).getEntities()) {
    // for (RootNode n : r.getObjectNeighbours(node,
    // prop.asOWLObjectProperty())) {
    // // for every neighbour of a Node...
    // for (OWLClassExpression f : getFillers(n)) {
    // // and every its filler
    // fillers.add(dataFactory.getOWLObjectSomeValuesFrom(prop, f));
    // }
    // }
    // }
    // return fillers;
    // }
    private Set<OWLClassExpression> getMaxFillers(RootNode node, Set<RootNode> visited,
            OWLClass rootClass) {
        Set<OWLClassExpression> fillers = new HashSet<OWLClassExpression>();
        Set<OWLClassExpression> label = new HashSet<OWLClassExpression>();
        // XXX: I do not if this is correct; need to check with Igni.
        r.getRoot(rootClass);
        for (OWLClassExpression c : r.getObjectLabel(node, false).getEntities()) {
            // all in the label is a filler
            label.add(c);
            r.getRoot(rootClass);
        }
        OWLClassExpression LabC;
        if (label.size() == 0) {
            LabC = null;
        } else if (label.size() == 1) {
            LabC = label.iterator().next();
        } else {
            LabC = dataFactory.getOWLObjectIntersectionOf(label);
        }
        visited.add(node);
        for (OWLObjectPropertyExpression prop : r.getObjectNeighbours(node, false)
                .getEntities()) {
            for (RootNode n : r.getObjectNeighbours(node, prop.asOWLObjectProperty())) {
                if (!visited.contains(n)) {
                    for (OWLClassExpression f : getMaxFillers(n, visited, rootClass)) {
                        OWLClassExpression exists = dataFactory
                                .getOWLObjectSomeValuesFrom(prop, f);
                        if (LabC != null) {
                            exists = dataFactory.getOWLObjectIntersectionOf(LabC, exists);
                        }
                        fillers.add(exists);
                    }
                }
            }
        }
        if (fillers.isEmpty()) {
            if (LabC == null) {
                LabC = dataFactory.getOWLThing();
            }
            fillers.add(LabC);
        }
        return fillers;
    }

    private Set<OWLAxiom> check(OWLClass c, OWLObjectPropertyExpression prop,
            Set<OWLClassExpression> fillers) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLClassExpression properyFiller : fillers) {
            OWLObjectSomeValuesFrom somePClass = dataFactory.getOWLObjectSomeValuesFrom(
                    prop, properyFiller);
            OWLSubClassOfAxiom existentialRestriction = dataFactory
                    .getOWLSubClassOfAxiom(c, somePClass);
            final boolean containsEntity = reasoner.isEntailed(existentialRestriction);
            if (containsEntity) {
                axioms.add(existentialRestriction);
            }
            // equivalent
            OWLEquivalentClassesAxiom equivalentRestr = dataFactory
                    .getOWLEquivalentClassesAxiom(c, somePClass);
            final boolean containsEntityEq1 = reasoner.isEntailed(equivalentRestr);
            if (containsEntityEq1) {
                axioms.add(equivalentRestr);
            }
            if (!properyFiller.equals(dataFactory.getOWLThing())) {
                OWLObjectAllValuesFrom allPClass = dataFactory.getOWLObjectAllValuesFrom(
                        prop, properyFiller);
                OWLSubClassOfAxiom universalRestriction = dataFactory
                        .getOWLSubClassOfAxiom(c, allPClass);
                final boolean containsEntity2 = reasoner.isEntailed(universalRestriction);
                if (containsEntity2) {
                    axioms.add(universalRestriction);
                }
                // equivalent classes
                OWLEquivalentClassesAxiom equivUniversalRestriction = dataFactory
                        .getOWLEquivalentClassesAxiom(c, allPClass);
                final boolean containsEntityEq2 = reasoner
                        .isEntailed(equivUniversalRestriction);
                if (containsEntityEq2) {
                    axioms.add(equivUniversalRestriction);
                }
            }
        }
        return axioms;
    }

    private Collection<? extends OWLAxiom> getNamedSubClassAxioms(RootNode root,
            OWLClass c) {
        Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();
        Node<? extends OWLClassExpression> classes = r.getObjectLabel(root, false);
        for (OWLClassExpression ex : classes) {
            if (ex instanceof OWLClass && !ex.asOWLClass().equals(c)) {
                OWLSubClassOfAxiom ax = dataFactory.getOWLSubClassOfAxiom(c, ex);
                if (reasoner.isEntailed(ax)) {
                    toReturn.add(ax);
                }
                OWLEquivalentClassesAxiom ax2 = dataFactory.getOWLEquivalentClassesAxiom(
                        c, ex);
                if (reasoner.isEntailed(ax2)) {
                    toReturn.add(ax2);
                }
            }
        }
        return toReturn;
    }

    @Override
    public Collection<OWLAxiom> getAxioms(OWLEntity entity) {
        return axiomMap.get(entity);
    }

    @Override
    public Set<OWLAxiom> getAxioms() {
        return axiomMap.getAllValues();
    }

    @Override
    public OWLKnowledgeExplorerReasoner getKnowledgeExplorerReasoner() {
        return r;
    }

    @Override
    public Set<OWLEntity> getEntities() {
        return signature;
    }

    /** @return classes */
    public Set<OWLClass> getOWLClasses() {
        Set<OWLClass> toReturn = new HashSet<OWLClass>();
        for (OWLEntity e : signature) {
            if (e.isOWLClass()) {
                toReturn.add(e.asOWLClass());
            }
        }
        return toReturn;
    }
}
