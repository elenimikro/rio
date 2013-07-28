package org.coode.owl.experiments.quality.assurance.test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.coode.oppl.ConstraintSystem;
import org.coode.oppl.OPPLFactory;
import org.coode.oppl.exceptions.OPPLException;
import org.coode.owl.experiments.quality.assurance.GeneralisationMetaprocessing;
import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.owl.generalise.OWLObjectGeneralisation;
import org.coode.proximitymatrix.cluster.Utils;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.util.MultiMap;

public class GeneralisationMetaprocessingTest {

	@Test
	public void testSimpleGeneralisationMetaprocessing() {
		try {
			OWLOntology o = OWLManager.createOWLOntologyManager()
					.createOntology();
			OWLDataFactory df = OWLManager.getOWLDataFactory();
			OWLClass a = df.getOWLClass(IRI.create("urn:test#A"));
			OWLClass b = df.getOWLClass(IRI.create("urn:test#B"));
			OWLClass c = df.getOWLClass(IRI.create("urn:test#C"));
			OWLClass d = df.getOWLClass(IRI.create("urn:test#D"));

			OWLSubClassOfAxiom aSubc = df.getOWLSubClassOfAxiom(a, c);
			OWLSubClassOfAxiom bSubc = df.getOWLSubClassOfAxiom(b, c);

			o.getOWLOntologyManager().addAxiom(o, aSubc);
			o.getOWLOntologyManager().addAxiom(o, bSubc);

			Set<OWLEntity> cluster = new HashSet<OWLEntity>(Arrays.asList(a, b));
			Set<OWLEntity> anotherCluster = new HashSet<OWLEntity>(
					Arrays.asList(c, d));

			OPPLFactory opplfactory = new OPPLFactory(
					o.getOWLOntologyManager(), o, null);
			ConstraintSystem constraintSystem = opplfactory
					.createConstraintSystem();

			Set<Collection<OWLEntity>> clusters = new HashSet<Collection<OWLEntity>>();
			clusters.add(cluster);
			clusters.add(anotherCluster);
			OWLObjectGeneralisation generalisation = Utils
					.getOWLObjectGeneralisation(clusters,
							o.getImportsClosure(), constraintSystem);

			OWLAxiom generalised = (OWLAxiom) aSubc.accept(generalisation);
			OWLAxiom anotherGeneralised = (OWLAxiom) bSubc
					.accept(generalisation);

			MultiMap<OWLAxiom, OWLAxiomInstantiation> map = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
			map.put(generalised, new OWLAxiomInstantiation(aSubc,
					generalisation.getSubstitutions()));
			map.put(anotherGeneralised, new OWLAxiomInstantiation(bSubc,
					generalisation.getSubstitutions()));

			System.out.println(map);

			GeneralisationMetaprocessing<Set<OWLEntity>> metaGen = new GeneralisationMetaprocessing<Set<OWLEntity>>(
					map, constraintSystem);
			MultiMap<OWLAxiom, OWLAxiomInstantiation> processedGeneralisationMap = metaGen
					.getProcessedGeneralisationMap();

			assertTrue(!processedGeneralisationMap.equals(map));

			System.out.println(processedGeneralisationMap);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownOWLOntologyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OPPLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
