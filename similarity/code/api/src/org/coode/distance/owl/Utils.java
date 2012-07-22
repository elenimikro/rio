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
package org.coode.distance.owl;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

public class Utils {
    @SuppressWarnings("unused")
    private final static OWLObjectVisitorEx<Boolean> ENTITY_RECOGNISER = new OWLObjectVisitorExAdapter<Boolean>(
            false) {
        @Override
        public Boolean visit(final OWLClass desc) {
            return true;
        }

        @Override
        public Boolean visit(final OWLAnnotationProperty property) {
            return true;
        }

        @Override
        public Boolean visit(final OWLDataProperty property) {
            return true;
        }

        @Override
        public Boolean visit(final OWLObjectProperty property) {
            return true;
        }

        @Override
        public Boolean visit(final OWLDatatype node) {
            return true;
        }

        @Override
        public Boolean visit(final OWLNamedIndividual individual) {
            return true;
        }
    };

    public static OWLObjectVisitorEx<Boolean> getOWLEntityRecogniser() {
        return ENTITY_RECOGNISER;
    }
}
