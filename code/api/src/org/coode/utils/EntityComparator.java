package org.coode.utils;

import java.util.Comparator;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

/** entity comparator using a short form provider
 * 
 * @author ignazio */
public class EntityComparator implements Comparator<OWLEntity> {
    @Override
    public int compare(OWLEntity o1, OWLEntity o2) {
        SimpleShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        return shortFormProvider.getShortForm(o1).compareTo(
                shortFormProvider.getShortForm(o2));
    }
}
