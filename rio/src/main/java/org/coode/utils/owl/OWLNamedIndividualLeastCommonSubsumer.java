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

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.util.Collection;
import java.util.List;

import org.coode.owl.wrappers.OWLAxiomProvider;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

/** @author Eleni Mikroyannidi */
public class OWLNamedIndividualLeastCommonSubsumer
    extends LeastCommonSubsumer<OWLNamedIndividual, OWLClass> {
    private final OWLClassLeastCommonSubsumer delegate;

    /**
     * @param axiomProvider axiomProvider
     * @param dataFactory dataFactory
     */
    public OWLNamedIndividualLeastCommonSubsumer(OWLAxiomProvider axiomProvider,
        OWLDataFactory dataFactory) {
        super(axiomProvider, dataFactory.getOWLThing());
        delegate = new OWLClassLeastCommonSubsumer(axiomProvider, dataFactory);
    }

    @Override
    protected void rebuild() {
        getAxiomProvider().stream().filter(ax -> ax instanceof OWLClassAssertionAxiom)
            .forEach(this::handleClassAssertion);
    }

    private void handleClassAssertion(OWLAxiom a) {
        OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;
        if (!ax.getIndividual().isAnonymous() && !ax.getClassExpression().isAnonymous()) {
            addParent(ax.getIndividual().asOWLNamedIndividual(),
                ax.getClassExpression().asOWLClass());
        }
    }

    @Override
    public OWLClass get(Collection<? extends OWLNamedIndividual> c) {
        List<OWLClass> results = asList(c.stream().flatMap(this::getParents).distinct());
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() == 1) {
            return results.get(0);
        }
        return delegate.get(results);
    }
}
