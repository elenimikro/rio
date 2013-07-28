package org.coode.proximitymatrix.cluster;

import java.util.List;
import java.util.Set;

import org.coode.oppl.Variable;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

public interface RegularitiesDecompositionModel<C extends Set<P>, P> {

	public abstract void put(C cluster,
			MultiMap<OWLAxiom, OWLAxiomInstantiation> map);

	public abstract List<C> getClusterList();

	public abstract MultiMap<OWLAxiom, OWLAxiomInstantiation> get(C c);

	public abstract Set<OWLOntology> getOntologies();

	public abstract Variable<?> getVariableRepresentative(C c);

	public MultiMap<OWLAxiom, OWLAxiomInstantiation> getGeneralisationMap();

}