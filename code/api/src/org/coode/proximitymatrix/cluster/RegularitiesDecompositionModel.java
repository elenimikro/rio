package org.coode.proximitymatrix.cluster;

import java.util.List;
import java.util.Set;

import org.coode.oppl.Variable;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni
 * @param <C>
 *            type
 * @param <P>
 *            type */
public interface RegularitiesDecompositionModel<C extends Set<P>, P> {
    /** @param cluster
     *            cluster
     * @param map
     *            map */
    public abstract void put(C cluster, MultiMap<OWLAxiom, OWLAxiomInstantiation> map);

    /** @return cluster list */
    public abstract List<C> getClusterList();

    /** @param c
     *            c
     * @return map */
    public abstract MultiMap<OWLAxiom, OWLAxiomInstantiation> get(C c);

    /** @return ontologies */
    public abstract Set<OWLOntology> getOntologies();

    /** @param c
     *            c
     * @return variable */
    public abstract Variable<?> getVariableRepresentative(C c);

    /** @return generalisation map */
    public MultiMap<OWLAxiom, OWLAxiomInstantiation> getGeneralisationMap();
}
