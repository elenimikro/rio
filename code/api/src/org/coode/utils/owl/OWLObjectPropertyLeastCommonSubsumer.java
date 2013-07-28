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
import java.util.Set;

import org.coode.owl.wrappers.OWLAxiomProvider;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

public class OWLObjectPropertyLeastCommonSubsumer extends
        LeastCommonSubsumer<OWLObjectProperty, OWLObjectProperty> {
    public OWLObjectPropertyLeastCommonSubsumer(final OWLAxiomProvider axiomProvider,
            final OWLDataFactory dataFactory) {
        super(axiomProvider, dataFactory.getOWLTopObjectProperty());
    }

    @Override
    protected void rebuild() {
        for (OWLAxiom ax : getAxiomProvider()) {
            ax.accept(new OWLAxiomVisitorAdapter() {
                @Override
                public void visit(final OWLSubObjectPropertyOfAxiom axiom) {
                    if (!axiom.getSubProperty().isAnonymous()
                            && !axiom.getSuperProperty().isAnonymous()) {
                        OWLObjectPropertyLeastCommonSubsumer.this.addParent(axiom
                                .getSubProperty().asOWLObjectProperty(), axiom
                                .getSuperProperty().asOWLObjectProperty());
                    }
                }
            });
        }
    }

    @Override
    public OWLObjectProperty get(final Collection<? extends OWLObjectProperty> c) {
        List<OWLObjectProperty> results = new ArrayList<OWLObjectProperty>(c);
        while (results.size() > 1) {
            OWLObject obj = results.get(0);
            if (obj instanceof OWLObjectProperty) {
                // if(obj.getClass().getName().equals("OWLObjectProperty")){
                OWLObjectProperty OWLObjectProperty = results.get(0);
                results.remove(OWLObjectProperty);
                Set<OWLObjectProperty> parents = getParents(OWLObjectProperty);
                for (OWLObjectProperty parent : parents) {
                    removeDescendants(parent, results);
                    if (!results.contains(parent)) {
                        results.add(parent);
                    }
                }
            }
        }
        return results.get(0);
    }
}
