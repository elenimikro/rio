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
package org.coode.distance.entityrelevance.owl;

import org.coode.distance.entityrelevance.RelevancePolicy;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

public class Utils {
    public static RelevancePolicy
            toOWLObjectRelevancePolicy(final RelevancePolicy policy) {
        return new RelevancePolicy() {
            @Override
            public boolean isRelevant(final OWLEntity object) {
                return object.accept(new OWLObjectVisitorExAdapter<Boolean>(false) {
                    @Override
                    public Boolean visit(final OWLClass desc) {
                        return policy.isRelevant(desc);
                    }

                    @Override
                    public Boolean visit(final OWLAnnotationProperty property) {
                        return policy.isRelevant(property);
                    }

                    @Override
                    public Boolean visit(final OWLDataProperty property) {
                        return policy.isRelevant(property);
                    }

                    @Override
                    public Boolean visit(final OWLObjectProperty property) {
                        return policy.isRelevant(property);
                    }

                    @Override
                    public Boolean visit(final OWLNamedIndividual individual) {
                        return policy.isRelevant(individual);
                    }
                });
            }
        };
    }
}