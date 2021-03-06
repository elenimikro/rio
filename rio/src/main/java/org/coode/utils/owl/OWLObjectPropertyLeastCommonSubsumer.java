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
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

/** @author Eleni Mikroyannidi */
public class OWLObjectPropertyLeastCommonSubsumer
    extends LeastCommonSubsumer<OWLObjectProperty, OWLObjectProperty> {
    /**
     * @param axiomProvider axiomProvider
     * @param dataFactory dataFactory
     */
    public OWLObjectPropertyLeastCommonSubsumer(OWLAxiomProvider axiomProvider,
        OWLDataFactory dataFactory) {
        super(axiomProvider, dataFactory.getOWLTopObjectProperty());
    }

    @Override
    protected void rebuild() {
        getAxiomProvider().stream().filter(ax -> ax instanceof OWLSubObjectPropertyOfAxiom)
            .forEach(this::handleSubObjectProperty);
    }

    private void handleSubObjectProperty(OWLAxiom ax) {
        OWLSubObjectPropertyOfAxiom axiom = (OWLSubObjectPropertyOfAxiom) ax;
        if (!axiom.getSubProperty().isAnonymous() && !axiom.getSuperProperty().isAnonymous()) {
            addParent(axiom.getSubProperty().asOWLObjectProperty(),
                axiom.getSuperProperty().asOWLObjectProperty());
        }
    }

    @Override
    public OWLObjectProperty get(Collection<? extends OWLObjectProperty> c) {
        List<OWLObjectProperty> results = new ArrayList<>(c);
        while (results.size() > 1) {
            OWLObject obj = results.get(0);
            if (obj instanceof OWLObjectProperty) {
                OWLObjectProperty p = results.get(0);
                results.remove(p);
                getParents(p).forEach(parent -> {
                    removeDescendants(parent, results);
                    results.add(parent);
                });
            }
        }
        return results.get(0);
    }
}
