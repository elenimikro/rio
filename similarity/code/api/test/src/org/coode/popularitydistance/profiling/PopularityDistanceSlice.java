package org.coode.popularitydistance.profiling;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.coode.basetest.TestHelper;
import org.coode.distance.Distance;
import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.distance.owl.OWLEntityReplacer;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.distance.wrapping.DistanceTableObject;
import org.coode.distance.wrapping.DistanceThresholdBasedFilter;
import org.coode.pair.filter.PairFilter;
import org.coode.proximitymatrix.CentroidProximityMeasureFactory;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.SimpleHistoryItemFactory;
import org.coode.proximitymatrix.SimpleProximityMatrix;
import org.coode.proximitymatrix.cluster.PairFilterBasedComparator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class PopularityDistanceSlice {
    /** This program takes an ontology as an input, computes pairwise distances
     * between all entities in the signature of the ontology, and build the
     * proximity matrix. The distance is computed with the use of popularity
     * ranking of an entity.
     * 
     * @param args */
    // XXX: Change path!
    // private final static String nci_iri =
    // "file:/eclipse-workspace/similarity/profiling_ontologies/nci-2012.owl.xml";
    private final static String obi_iri = "file:/eclipse-workspace/similarity/profiling_ontologies/obi.owl";

    public static void main(final String[] args) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        String ontology_iri = obi_iri;
        IRI iri = IRI.create(ontology_iri);
        try {
            TestHelper.loadIRIMappers(Collections.singleton(iri), manager);
            OWLOntology onto = manager.loadOntology(iri);
            System.out.println("PopularityDistanceSlice.main() Ontology "
                    + onto.getOntologyID() + " was loaded");
            final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
            final OWLEntityReplacer owlEntityReplacer = new OWLEntityReplacer(
                    manager.getOWLDataFactory(), new ReplacementByKindStrategy(
                            manager.getOWLDataFactory()));
            final AxiomRelevanceAxiomBasedDistance distance = new AxiomRelevanceAxiomBasedDistance(
                    onto.getImportsClosure(), owlEntityReplacer, manager);
            System.out
                    .println("PopularityDistanceSlice.main() Distance measure was built");
            Set<OWLEntity> entities = new TreeSet<OWLEntity>(new Comparator<OWLEntity>() {
                @Override
                public int compare(final OWLEntity o1, final OWLEntity o2) {
                    return shortFormProvider.getShortForm(o1).compareTo(
                            shortFormProvider.getShortForm(o2));
                }
            });
            for (OWLOntology ontology : onto.getImportsClosure()) {
                Set<OWLEntity> signature = ontology.getSignature();
                for (OWLEntity e : signature) {
                    if (!e.isOWLObjectProperty()) {
                        entities.add(e);
                    }
                }
            }
            System.out
                    .println("PopularityDistanceSlice.main() Creating baseDistanceMatrix...");
            // the baseDistanceMatrix is needed for clustering
            // SimpleProximityMatrix<OWLEntity> baseDistanceMatrix = new
            // SimpleProximityMatrix<OWLEntity>(
            // entities, distance);
            // System.out
            // .println("PopularityDistanceSlice.main() baseDistanceMatrix was created");
            // reduce entities wherever distance = 0 -> equivalent classes
            System.out
                    .println("PopularityDistanceSlice.main() building equivalent classes...");
            MultiMap<OWLEntity, OWLEntity> equivalenceClasses = org.coode.distance.Utils
                    .getEquivalenceClasses(entities, distance);
            entities = new HashSet<OWLEntity>(equivalenceClasses.keySet());
            System.out
                    .println("PopularityDistanceSlice.main() building distanceMatrix...");
            final SimpleProximityMatrix<OWLEntity> distanceMatrix = new SimpleProximityMatrix<OWLEntity>(
                    entities, distance);
            System.out.println(String.format(
                    "Finished computing distance between %d entities", distanceMatrix
                            .getObjects().size()));
            final SimpleProximityMatrix<DistanceTableObject<OWLEntity>> wrappedMatrix = new SimpleProximityMatrix<DistanceTableObject<OWLEntity>>(
                    DistanceTableObject.createDistanceTableObjectSet(distance,
                            distanceMatrix.getObjects()),
                    new Distance<DistanceTableObject<OWLEntity>>() {
                        @Override
                        public double getDistance(final DistanceTableObject<OWLEntity> a,
                                final DistanceTableObject<OWLEntity> b) {
                            return distanceMatrix.getDistance(a.getIndex(), b.getIndex());
                        }
                    });
            Set<Collection<? extends DistanceTableObject<OWLEntity>>> newObjects = new LinkedHashSet<Collection<? extends DistanceTableObject<OWLEntity>>>();
            for (DistanceTableObject<OWLEntity> object : wrappedMatrix.getObjects()) {
                newObjects.add(Collections.singletonList(object));
            }
            Distance<Collection<? extends DistanceTableObject<OWLEntity>>> singletonDistance = new Distance<Collection<? extends DistanceTableObject<OWLEntity>>>() {
                @Override
                public double getDistance(
                        final Collection<? extends DistanceTableObject<OWLEntity>> a,
                        final Collection<? extends DistanceTableObject<OWLEntity>> b) {
                    return wrappedMatrix.getDistance(a.iterator().next().getIndex(), b
                            .iterator().next().getIndex());
                }
            };
            PairFilter<Collection<? extends DistanceTableObject<OWLEntity>>> filter = DistanceThresholdBasedFilter
                    .build(distanceMatrix.getData(), 1);
            System.out.println("Building clustering matrix....");
            ClusteringProximityMatrix<DistanceTableObject<OWLEntity>> clusteringMatrix = ClusteringProximityMatrix
                    .build(wrappedMatrix,
                            new CentroidProximityMeasureFactory(),
                            filter,
                            PairFilterBasedComparator.build(filter, newObjects,
                                    singletonDistance),
                            new SimpleHistoryItemFactory<Collection<? extends DistanceTableObject<OWLEntity>>>());
            System.out.println(String.format(
                    "Finished building clustering matrix of %d entities",
                    clusteringMatrix.getObjects().size()));
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }
}
