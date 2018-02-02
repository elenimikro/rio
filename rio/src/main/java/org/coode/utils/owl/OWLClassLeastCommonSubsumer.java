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
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/** @author Eleni Mikroyannidi */
public class OWLClassLeastCommonSubsumer extends LeastCommonSubsumer<OWLClass, OWLClass> {
    /**
     * @param axiomProvider axiomProvider
     * @param dataFactory dataFactory
     */
    public OWLClassLeastCommonSubsumer(OWLAxiomProvider axiomProvider, OWLDataFactory dataFactory) {
        super(axiomProvider, dataFactory.getOWLThing());
    }

    @Override
    protected void rebuild() {
        getAxiomProvider().stream().filter(ax -> ax instanceof OWLSubClassOfAxiom)
            .forEach(this::handleSubClassOf);
    }

    private void handleSubClassOf(OWLAxiom a) {
        OWLSubClassOfAxiom ax = (OWLSubClassOfAxiom) a;
        if (!ax.getSubClass().isAnonymous() && !ax.getSuperClass().isAnonymous()) {
            addParent(ax.getSubClass().asOWLClass(), ax.getSuperClass().asOWLClass());
        }
    }

    @Override
    public OWLClass get(Collection<? extends OWLClass> c) {
        List<OWLClass> results = new ArrayList<>(c);
        while (results.size() > 1) {
            OWLClass owlClass = results.get(0);
            results.remove(owlClass);
            getParents(owlClass).forEach(parent -> {

                removeDescendants(parent, results);
                results.add(parent);
            });
        }
        return results.get(0);
    }
}
