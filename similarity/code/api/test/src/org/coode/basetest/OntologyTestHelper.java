package org.coode.basetest;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.coode.distance.owl.AxiomRelevanceAxiomBasedDistance;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.proximitymatrix.cluster.Cluster;
import org.coode.proximitymatrix.cluster.ClusterDecompositionModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.MultiMap;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

public class OntologyTestHelper {

	public static OWLOntology getSmallTestOntology()
			throws OWLOntologyCreationException {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o = m.createOntology();
		OWLDataFactory factory = m.getOWLDataFactory();
		OWLClass a = factory.getOWLClass(IRI.create("urn:test#A"));
		OWLClass b = factory.getOWLClass(IRI.create("urn:test#B"));
		OWLClass c = factory.getOWLClass(IRI.create("urn:test#C"));

		OWLClass d = factory.getOWLClass(IRI.create("urn:test#D"));
		OWLClass e = factory.getOWLClass(IRI.create("urn:test#E"));
		OWLClass f = factory.getOWLClass(IRI.create("urn:test#F"));
		OWLClass g = factory.getOWLClass(IRI.create("urn:test#G"));
		OWLClass i = factory.getOWLClass(IRI.create("urn:test#I"));
		OWLClass j = factory.getOWLClass(IRI.create("urn:test#J"));
		OWLClass k = factory.getOWLClass(IRI.create("urn:test#K"));

		OWLSubClassOfAxiom ab = factory.getOWLSubClassOfAxiom(a, b);
		OWLSubClassOfAxiom bc = factory.getOWLSubClassOfAxiom(b, c);
		OWLSubClassOfAxiom db = factory.getOWLSubClassOfAxiom(d, b);
		OWLSubClassOfAxiom ec = factory.getOWLSubClassOfAxiom(e, c);

		OWLSubClassOfAxiom fg = factory.getOWLSubClassOfAxiom(f, g);
		OWLSubClassOfAxiom gi = factory.getOWLSubClassOfAxiom(g, i);
		OWLSubClassOfAxiom jg = factory.getOWLSubClassOfAxiom(j, g);
		OWLSubClassOfAxiom ki = factory.getOWLSubClassOfAxiom(k, i);

		m.addAxiom(o, ab);
		m.addAxiom(o, bc);
		m.addAxiom(o, db);
		m.addAxiom(o, ec);
		m.addAxiom(o, fg);
		m.addAxiom(o, gi);
		m.addAxiom(o, jg);
		m.addAxiom(o, ki);

		return o;
	}

	public static OWLOntology getSmallMeaningfullTestOntology()
			throws OWLOntologyCreationException {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o = m.createOntology();
		OWLDataFactory factory = m.getOWLDataFactory();
		OWLClass a = factory.getOWLClass(IRI
				.create("urn:test#Freestyle_swimming"));
		OWLClass b = factory.getOWLClass(IRI.create("urn:test#Swimming"));
		OWLClass c = factory.getOWLClass(IRI.create("urn:test#Sport"));

		OWLClass d = factory.getOWLClass(IRI
				.create("urn:test#Breaststroke_swimming"));
		OWLClass e = factory.getOWLClass(IRI.create("urn:test#Cycling"));
		OWLClass f = factory.getOWLClass(IRI
				.create("urn:test#Watercolor_painting"));
		OWLClass g = factory.getOWLClass(IRI.create("urn:test#Painting"));
		OWLClass i = factory.getOWLClass(IRI.create("urn:test#Hobby"));
		OWLClass j = factory.getOWLClass(IRI.create("urn:test#Oil_painting"));
		OWLClass k = factory.getOWLClass(IRI.create("urn:test#Sightseeing"));

		OWLSubClassOfAxiom ab = factory.getOWLSubClassOfAxiom(a, b);
		OWLSubClassOfAxiom bc = factory.getOWLSubClassOfAxiom(b, c);
		OWLSubClassOfAxiom db = factory.getOWLSubClassOfAxiom(d, b);
		OWLSubClassOfAxiom ec = factory.getOWLSubClassOfAxiom(e, c);

		OWLSubClassOfAxiom fg = factory.getOWLSubClassOfAxiom(f, g);
		OWLSubClassOfAxiom gi = factory.getOWLSubClassOfAxiom(g, i);
		OWLSubClassOfAxiom jg = factory.getOWLSubClassOfAxiom(j, g);
		OWLSubClassOfAxiom ki = factory.getOWLSubClassOfAxiom(k, i);

		m.addAxiom(o, ab);
		m.addAxiom(o, bc);
		m.addAxiom(o, db);
		m.addAxiom(o, ec);
		m.addAxiom(o, fg);
		m.addAxiom(o, gi);
		m.addAxiom(o, jg);
		m.addAxiom(o, ki);

		return o;
	}

	public static ClusterDecompositionModel<OWLEntity> getPopularityClusterModel(
			OWLOntology o) throws Exception {

		OWLOntologyManager m = o.getOWLOntologyManager();
		AxiomRelevanceAxiomBasedDistance distance = (AxiomRelevanceAxiomBasedDistance) DistanceCreator
				.createAxiomRelevanceAxiomBasedDistance(m);
		ClusterCreator clusterer = new ClusterCreator();

		final SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		Set<OWLEntity> entities = new TreeSet<OWLEntity>(
				new Comparator<OWLEntity>() {
					@Override
					public int compare(final OWLEntity o1, final OWLEntity o2) {
						return shortFormProvider.getShortForm(o1).compareTo(
								shortFormProvider.getShortForm(o2));
					}
				});
		for (OWLOntology ontology : m.getOntologies()) {
			entities.addAll(ontology.getSignature());
		}
		Set<Cluster<OWLEntity>> clusters = clusterer.agglomerateAll(o,
				distance, entities);
		ClusterDecompositionModel<OWLEntity> model = clusterer
				.buildClusterDecompositionModel(o, m, clusters);

		List<Cluster<OWLEntity>> clusterList = model.getClusterList();
		MultiMap<OWLAxiom, OWLAxiomInstantiation> multiMap = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
		for (int counter = 0; counter < clusterList.size(); counter++) {
			multiMap.putAll(model.get(clusterList.get(counter)));
		}
		for (OWLAxiom ax : multiMap.keySet()) {
			System.out.println("Generalisation: " + ax);
			System.out.println("Instantiations:");
			for (OWLAxiomInstantiation inst : multiMap.get(ax)) {
				System.out.println("\t" + inst);
			}
		}
		return model;
	}

}
