package org.coode.distance.owl;

import java.util.Collection;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

public abstract class AbstractAxiomBasedDistanceImpl implements
		AbstractAxiomBasedDistance {

	OWLEntity last;
	Set<OWLAxiom> lastset;

	@Override
	public double getDistance(OWLEntity a, OWLEntity b) {
		double toReturn = a.equals(b) ? 0 : 1;
		if (toReturn == 1) {
			if (a != last) {
				lastset = getAxioms(a);
				last = a;
			}
			Collection<OWLAxiom> axiomsForB = getAxioms(b);
			if (!lastset.isEmpty() || !axiomsForB.isEmpty()) {
				int AorB = lastset.size();
				int AandB = 0;
				for (OWLAxiom e1 : axiomsForB) {
					if (!lastset.contains(e1)) {
						// union increases for every non duplicate
						AorB++;
					} else {
						// intersection increases for each common element
						AandB++;
					}
				}
				toReturn = (double) (AorB - AandB) / AorB;
			}
		}
		return toReturn;
	}

}
