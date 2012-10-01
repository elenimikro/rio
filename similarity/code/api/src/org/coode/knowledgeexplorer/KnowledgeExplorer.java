package org.coode.knowledgeexplorer;

import java.util.Collection;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.reasoner.knowledgeexploration.OWLKnowledgeExplorerReasoner;

public interface KnowledgeExplorer {

	public Collection<OWLAxiom> getAxioms(OWLEntity entity);
	
	public Set<OWLAxiom> getAxioms();
	
	public OWLKnowledgeExplorerReasoner getKnowledgeExplorerReasoner();
	
	public Set<OWLEntity> getEntities();
	
}
