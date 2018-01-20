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
package org.coode.owl.distance.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.coode.distance.owl.HashCode;
import org.coode.distance.owl.ReplacementByKindStrategy;
import org.coode.distance.owl.StructuralHashCode;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

/** @author eleni */
@SuppressWarnings("javadoc")
public class TestStructuralHashCode {
    private final static OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
    private final static HashCode hashCode =
        new StructuralHashCode(ontologyManager.getOWLDataFactory(),
            new ReplacementByKindStrategy(ontologyManager.getOWLDataFactory()));

    @Test
    public void testSubClassAxiom() {
        OWLClass a = ontologyManager.getOWLDataFactory().getOWLClass(IRI.create("A"));
        OWLClass b = ontologyManager.getOWLDataFactory().getOWLClass(IRI.create("B"));
        OWLSubClassOfAxiom anAxiom =
            ontologyManager.getOWLDataFactory().getOWLSubClassOfAxiom(a, a);
        OWLSubClassOfAxiom anotherAxiom =
            ontologyManager.getOWLDataFactory().getOWLSubClassOfAxiom(a, b);
        assertEquals(hashCode.hashCode(anAxiom), hashCode.hashCode(anotherAxiom));
    }

    @Test
    public void testSubClassAxiomNotStructurallyEqual() {
        OWLClass a = ontologyManager.getOWLDataFactory().getOWLClass(IRI.create("A"));
        OWLClass b = ontologyManager.getOWLDataFactory().getOWLClass(IRI.create("B"));
        OWLSubClassOfAxiom anAxiom = ontologyManager.getOWLDataFactory().getOWLSubClassOfAxiom(a,
            ontologyManager.getOWLDataFactory().getOWLObjectIntersectionOf(a, a));
        OWLSubClassOfAxiom anotherAxiom =
            ontologyManager.getOWLDataFactory().getOWLSubClassOfAxiom(a, b);
        assertFalse(hashCode.hashCode(anAxiom) == hashCode.hashCode(anotherAxiom));
    }

    @Test
    public void testEquivalentAxiom() {
        OWLClass a = ontologyManager.getOWLDataFactory().getOWLClass(IRI.create("A"));
        OWLClass b = ontologyManager.getOWLDataFactory().getOWLClass(IRI.create("B"));
        OWLAxiom anAxiom = ontologyManager.getOWLDataFactory().getOWLEquivalentClassesAxiom(a, a);
        OWLAxiom anotherAxiom =
            ontologyManager.getOWLDataFactory().getOWLEquivalentClassesAxiom(a, b);
        assertEquals(hashCode.hashCode(anAxiom), hashCode.hashCode(anotherAxiom));
    }
}
