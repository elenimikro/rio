package org.coode.basetest;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.coode.distance.owl.AbstractAxiomBasedDistance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.coode.proximitymatrix.cluster.RegularitiesDecompositionModel;
import org.coode.utils.owl.ClusterCreator;
import org.coode.utils.owl.DistanceCreator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

/** @author eleni */
public class ClusteringHelper {
    /** @param o
     *            o
     * @return cluster decomposition model */
    public static ClusterDecompositionModel<OWLEntity>
            getSyntacticStructuralClusterModel(OWLOntology o) {
        try {
            OWLOntologyManager m = o.getOWLOntologyManager();
            AbstractAxiomBasedDistance distance = (AbstractAxiomBasedDistance) DistanceCreator
                    .createStructuralAxiomRelevanceAxiomBasedDistance(m);
            final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
            Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
                @Override
                public int compare(final OWLEntity o1, final OWLEntity o2) {
                    return shortFormProvider.getShortForm(o1).compareTo(
                            shortFormProvider.getShortForm(o2));
                }
            });
            for (OWLOntology ontology : m.getOntologies()) {
                entities.addAll(ontology.getSignature());
            }
            return getClusterDecompositionModel(o, distance, entities);
        } catch (OPPLException e) {
            throw new RuntimeException(e);
        }
    }

    /** @param o
     *            o
     * @return cluster decomposition model */
    public static ClusterDecompositionModel<OWLEntity> getSyntacticPropertyClusterModel(
            OWLOntology o) {
        try {
            OWLOntologyManager m = o.getOWLOntologyManager();
            AbstractAxiomBasedDistance distance = (AbstractAxiomBasedDistance) DistanceCreator
                    .createOWLEntityRelevanceAxiomBasedDistance(m);
            final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
            Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
                @Override
                public int compare(final OWLEntity o1, final OWLEntity o2) {
                    return shortFormProvider.getShortForm(o1).compareTo(
                            shortFormProvider.getShortForm(o2));
                }
            });
            for (OWLOntology ontology : o.getImportsClosure()) {
                for (OWLEntity e : ontology.getSignature()) {
                    if (!e.isType(EntityType.OBJECT_PROPERTY)
                            && !e.isType(EntityType.DATA_PROPERTY)
                            && !e.isType(EntityType.ANNOTATION_PROPERTY)
                            && !e.isType(EntityType.DATATYPE)) {
                        entities.add(e);
                    }
                }
            }
            return getClusterDecompositionModel(o, distance, entities);
        } catch (OPPLException e) {
            throw new RuntimeException(e);
        }
    }

    /** @param seedAxioms
     *            seedAxioms
     * @return cluster decomposition model */
    public static ClusterDecompositionModel<OWLEntity>
            getSyntacticPopularityClusterModel(Set<OWLAxiom> seedAxioms) {
        OWLOntology onto = null;
        try {
            onto = OWLManager.createOWLOntologyManager().createOntology(seedAxioms);
        } catch (OWLOntologyCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return getSyntacticPopularityClusterModel(onto);
    }

    /** @param o
     *            o
     * @return cluster decomposition model */
    public static ClusterDecompositionModel<OWLEntity>
            getSyntacticPopularityClusterModel(OWLOntology o) {
        try {
            OWLOntologyManager m = o.getOWLOntologyManager();
            AxiomRelevanceAxiomBasedDistance distance = (AxiomRelevanceAxiomBasedDistance) DistanceCreator
                    .createAxiomRelevanceAxiomBasedDistance(m);
            // XXX: remove next comment
            System.out.println("Distance was created...");
            final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
            Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
                @Override
                public int compare(final OWLEntity o1, final OWLEntity o2) {
                    return shortFormProvider.getShortForm(o1).compareTo(
                            shortFormProvider.getShortForm(o2));
                }
            });
            for (OWLOntology ontology : m.getOntologies()) {
                entities.addAll(ontology.getSignature());
            }
            return getClusterDecompositionModel(o, distance, entities);
        } catch (OPPLException e) {
            throw new RuntimeException(e);
        }
    }

    private static ClusterDecompositionModel<OWLEntity> getClusterDecompositionModel(
            OWLOntology o, AbstractAxiomBasedDistance distance, Set<OWLEntity> entities)
            throws OPPLException {
        ClusterCreator clusterer = new ClusterCreator();
        Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(distance, entities);
        ClusterDecompositionModel<OWLEntity> model = clusterer
                .buildClusterDecompositionModel(o, clusters);
        return model;
    }

    /** @param model
     *            model
     * @return generalisation map */
    public static <C extends Set<OWLEntity>> MultiMap<OWLAxiom, OWLAxiomInstantiation>
            extractGeneralisationMap(RegularitiesDecompositionModel<C, OWLEntity> model) {
        List<C> clusterList = model.getClusterList();
        MultiMap<OWLAxiom, OWLAxiomInstantiation> multiMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
        for (int counter = 0; counter < clusterList.size(); counter++) {
            multiMap.putAll(model.get(clusterList.get(counter)));
        }
        return multiMap;
    }
}
