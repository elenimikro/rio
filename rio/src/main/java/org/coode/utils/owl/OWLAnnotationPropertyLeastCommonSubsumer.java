/*******************************************************************************
 * Copyright (c) 2012 Eleni Mikroyannidi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Eleni Mikroyannidi, Luigi Iannone - initial API and implementation
 ******************************************************************************/
package org.coode.utils.owl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.coode.owl.wrappers.OWLAxiomProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

/** @author Eleni Mikroyannidi */
public class OWLAnnotationPropertyLeastCommonSubsumer
    extends LeastCommonSubsumer<OWLAnnotationProperty, OWLAnnotationProperty> {
    /**
     * 
     */
    public static final IRI TOP_ANNOTATION_PROPERTY_IRI =
        IRI.create("http://www.coode.org#topAnnotationProperty");

    /**
     * @param axiomProvider axiomProvider
     * @param dataFactory dataFactory
     */
    public OWLAnnotationPropertyLeastCommonSubsumer(OWLAxiomProvider axiomProvider,
        OWLDataFactory dataFactory) {
        super(axiomProvider, dataFactory.getOWLAnnotationProperty(TOP_ANNOTATION_PROPERTY_IRI));
    }

    @Override
    protected void rebuild() {
        getAxiomProvider().stream().filter(ax -> ax instanceof OWLSubAnnotationPropertyOfAxiom)
            .forEach(this::handleSubAnnotationProperty);
    }

    protected void handleSubAnnotationProperty(OWLAxiom ax) {
        addParent(((OWLSubAnnotationPropertyOfAxiom) ax).getSubProperty(),
            ((OWLSubAnnotationPropertyOfAxiom) ax).getSuperProperty());
    }

    @Override
    public OWLAnnotationProperty get(Collection<? extends OWLAnnotationProperty> c) {
        List<OWLAnnotationProperty> results = new ArrayList<>(c);
        while (results.size() > 1) {
            OWLAnnotationProperty OWLDataProperty = results.get(0);
            results.remove(OWLDataProperty);
            getParents(OWLDataProperty).forEach(parent -> {
                removeDescendants(parent, results);
                results.add(parent);
            });
        }
        return results.get(0);
    }
}
