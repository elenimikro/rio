package org.coode.knowledgeexplorer;

import java.util.Collection;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.reasoner.knowledgeexploration.OWLKnowledgeExplorerReasoner;

/** @author eleni */
public interface KnowledgeExplorer {
    /** @param entity
     *            entity
     * @return axioms */
    public Collection<OWLAxiom> getAxioms(OWLEntity entity);

    /** @return axioms */
    public Set<OWLAxiom> getAxioms();

    /** @return knoweldge explorer */
    public OWLKnowledgeExplorerReasoner getKnowledgeExplorerReasoner();

    /** @return axioms */
    public Set<OWLEntity> getEntities();
}
