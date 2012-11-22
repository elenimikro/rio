package org.coode.knowledgeexplorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.knowledgeexploration.OWLKnowledgeExplorerReasoner;
import org.semanticweb.owlapi.reasoner.knowledgeexploration.OWLKnowledgeExplorerReasoner.RootNode;
import org.semanticweb.owlapi.util.MultiMap;

public class KnowledgeExplorerMaxFillersImpl implements KnowledgeExplorer {

    // private final OWLKnowledgeExplorerReasoner r;
    // private final OWLReasoner reasoner;
    // private final OWLOntology o;
    // private final OWLOntologyManager manager;
	private final Set<OWLEntity> signature = new HashSet<OWLEntity>();
	private final MultiMap<OWLEntity, OWLAxiom> axiomMap = new MultiMap<OWLEntity, OWLAxiom>();

    // private final OWLDataFactory dataFactory;


    public KnowledgeExplorerMaxFillersImpl(OWLReasoner reasoner,
            OWLKnowledgeExplorerReasoner r) {
		if (reasoner == null) {
			throw new NullPointerException(
					"OWLKnowledgeExplorerReasoner cannot be null");
		}
        buildAxiomMap(reasoner.getRootOntology(), reasoner, r);
        reasoner.dispose();
        r.dispose();
	}

    private void buildAxiomMap(OWLOntology o, OWLReasoner reasoner,
            OWLKnowledgeExplorerReasoner r) {
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
            Set<OWLAxiom> computeAxioms = computeAxioms(root, c, reasoner, r);

            computeAxioms.addAll(getNamedSubClassAxioms(root, c, reasoner, r));
            // System.out.println(computeAxioms);
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

    private Set<OWLAxiom> computeAxioms(RootNode root, OWLClass rootClass,
            OWLReasoner reasoner, OWLKnowledgeExplorerReasoner r) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		Node<? extends OWLObjectPropertyExpression> objectNeighbours = r
				.getObjectNeighbours(root, false);
		Set<? extends OWLObjectPropertyExpression> entities = objectNeighbours
				.getEntities();
		for (OWLObjectPropertyExpression prop : entities) {
			if (prop instanceof OWLObjectProperty) {
				Collection<RootNode> objectRootNodes = r.getObjectNeighbours(
						root, prop.asOWLObjectProperty());
				Set<OWLClassExpression> fillers = new HashSet<OWLClassExpression>();
                fillers.add(OWLManager.getOWLDataFactory().getOWLThing());
				for (RootNode objectRootNode : objectRootNodes) {
					fillers.addAll(getMaxFillers(objectRootNode,
 new HashSet<RootNode>(),
                            r));
				}
                axioms.addAll(check(rootClass, prop, fillers, reasoner));
			}
		}

		return axioms;
	}

    // private Set<OWLClassExpression> getFillers(RootNode node,
    // OWLKnowledgeExplorerReasoner r) {
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
    // for (OWLClassExpression f : getFillers(n, r)) {
    // // and every its filler
    // fillers.add(OWLManager.getOWLDataFactory()
    // .getOWLObjectSomeValuesFrom(prop, f));
    // }
    // }
    // }
    // return fillers;
    // }

	private Set<OWLClassExpression> getMaxFillers(RootNode node,
 Set<RootNode> visited,
            OWLKnowledgeExplorerReasoner r) {
		Set<OWLClassExpression> fillers = new HashSet<OWLClassExpression>();
		Set<OWLClassExpression> label = new HashSet<OWLClassExpression>();
		for (OWLClassExpression c : r.getObjectLabel(node, false).getEntities()) {
			// all in the label is a filler
			label.add(c);
		}
		OWLClassExpression LabC;
		if (label.size() == 0){
			LabC = null;
		}
		else if (label.size() == 1){
			LabC = label.iterator().next();
		}
		else{
            LabC = OWLManager.getOWLDataFactory().getOWLObjectIntersectionOf(label);
		}

		visited.add(node);
		for (OWLObjectPropertyExpression prop : r.getObjectNeighbours(node,
				false).getEntities()) {
			for (RootNode n : r.getObjectNeighbours(node,
					prop.asOWLObjectProperty())) {
				if (!visited.contains(n)) {
                    for (OWLClassExpression f : getMaxFillers(n, visited, r)) {
                        OWLClassExpression exists = OWLManager.getOWLDataFactory()
								.getOWLObjectSomeValuesFrom(prop, f);
						if (LabC != null) {
                            exists = OWLManager.getOWLDataFactory()
                                    .getOWLObjectIntersectionOf(
									LabC, exists);
						}
						fillers.add(exists);
					}
				}
			}
		}
		if (fillers.isEmpty()) {
			if (LabC == null){
                LabC = OWLManager.getOWLDataFactory().getOWLThing();
			}
			fillers.add(LabC);
		}
		return fillers;
	}

	private Set<OWLAxiom> check(OWLClass c, OWLObjectPropertyExpression prop,
            Set<OWLClassExpression> fillers, OWLReasoner reasoner) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for (OWLClassExpression properyFiller : fillers) {
            OWLObjectSomeValuesFrom somePClass = OWLManager.getOWLDataFactory()
					.getOWLObjectSomeValuesFrom(prop, properyFiller);
            OWLSubClassOfAxiom existentialRestriction = OWLManager.getOWLDataFactory()
					.getOWLSubClassOfAxiom(c, somePClass);
			final boolean containsEntity = reasoner
					.isEntailed(existentialRestriction);

			if (containsEntity) {
				axioms.add(existentialRestriction);
			}
			// equivalent
            OWLEquivalentClassesAxiom equivalentRestr = OWLManager.getOWLDataFactory()
					.getOWLEquivalentClassesAxiom(c, somePClass);
			final boolean containsEntityEq1 = reasoner
					.isEntailed(equivalentRestr);
			if (containsEntityEq1) {
				axioms.add(equivalentRestr);
			}
            if (!properyFiller.equals(OWLManager.getOWLDataFactory().getOWLThing())) {
                OWLObjectAllValuesFrom allPClass = OWLManager.getOWLDataFactory()
						.getOWLObjectAllValuesFrom(prop, properyFiller);
                OWLSubClassOfAxiom universalRestriction = OWLManager.getOWLDataFactory()
						.getOWLSubClassOfAxiom(c, allPClass);
				final boolean containsEntity2 = reasoner
						.isEntailed(universalRestriction);
				if (containsEntity2) {
					axioms.add(universalRestriction);
				}
				// equivalent classes
                OWLEquivalentClassesAxiom equivUniversalRestriction = OWLManager
                        .getOWLDataFactory()
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

	private Collection<? extends OWLAxiom> getNamedSubClassAxioms(
RootNode root,
            OWLClass c, OWLReasoner reasoner, OWLKnowledgeExplorerReasoner r) {
		Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();
		Node<? extends OWLClassExpression> classes = r.getObjectLabel(root,
				false);
		for (OWLClassExpression ex : classes) {
			if (ex instanceof OWLClass && !ex.asOWLClass().equals(c)) {
                OWLSubClassOfAxiom ax = OWLManager.getOWLDataFactory()
                        .getOWLSubClassOfAxiom(
						c, ex);
				if (reasoner.isEntailed(ax)) {
					toReturn.add(ax);
				}
                OWLEquivalentClassesAxiom ax2 = OWLManager.getOWLDataFactory()
						.getOWLEquivalentClassesAxiom(c, ex);
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
        return null;
	}

	@Override
	public Set<OWLEntity> getEntities() {
		return signature;
	}

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
