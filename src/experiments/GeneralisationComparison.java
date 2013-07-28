package experiments;

import java.util.HashSet;
import java.util.Set;

import org.coode.owl.generalise.OWLAxiomInstantiation;
import org.coode.proximitymatrix.cluster.Utils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.MultiMap;

public class GeneralisationComparison {

	public MultiMap<OWLAxiom, OWLAxiomInstantiation> getGeneralisationIntersection(
			MultiMap<OWLAxiom, OWLAxiomInstantiation> map,
			MultiMap<OWLAxiom, OWLAxiomInstantiation> anotherMap) {
		MultiMap<OWLAxiom, OWLAxiomInstantiation> toReturn = new MultiMap<OWLAxiom, OWLAxiomInstantiation>();
		Set<OWLAxiom> intersection = map.keySet();
		intersection.retainAll(anotherMap.keySet());
		for (OWLAxiom ax : intersection) {
			toReturn.putAll(ax, map.get(ax));
			toReturn.putAll(ax, anotherMap.get(ax));
		}
		return toReturn;
	}

	public Set<OWLAxiom> getOWLInstantiationIntersection(
			MultiMap<OWLAxiom, OWLAxiomInstantiation> map,
			MultiMap<OWLAxiom, OWLAxiomInstantiation> anotherMap) {
		Set<OWLAxiom> intersection = Utils.extractAxioms(map.getAllValues());
		intersection.retainAll(Utils.extractAxioms(anotherMap.getAllValues()));
		return intersection;
	}

	public double getGeneralisationSimilarity(
			MultiMap<OWLAxiom, OWLAxiomInstantiation> map,
			MultiMap<OWLAxiom, OWLAxiomInstantiation> anotherMap) {
		if (map.size() == 0 && anotherMap.size() == 0) {
			return 0;
		} else {
			Set<OWLAxiom> union = new HashSet<OWLAxiom>();
			Set<OWLAxiom> intersection = new HashSet<OWLAxiom>();
			union.addAll(map.keySet());
			union.addAll(anotherMap.keySet());
			intersection.addAll(map.keySet());
			intersection.retainAll(anotherMap.keySet());

			double toReturn = ((double) intersection.size()) / union.size();
			return toReturn;
		}
	}

	public double getOWLInstantiationSimilarity(
			MultiMap<OWLAxiom, OWLAxiomInstantiation> map,
			MultiMap<OWLAxiom, OWLAxiomInstantiation> anotherMap) {
		if (map.size() == 0 && anotherMap.size() == 0) {
			return 0;
		} else {
			Set<OWLAxiom> set = Utils.extractAxioms(map.getAllValues());
			Set<OWLAxiom> anotherSet = Utils.extractAxioms(anotherMap
					.getAllValues());
			Set<OWLAxiom> union = new HashSet<OWLAxiom>();
			Set<OWLAxiom> intersection = new HashSet<OWLAxiom>();
			union.addAll(set);
			union.addAll(anotherSet);
			intersection.addAll(set);
			intersection.retainAll(anotherSet);

			double toReturn = ((double) intersection.size()) / union.size();
			return toReturn;
		}
	}
}
