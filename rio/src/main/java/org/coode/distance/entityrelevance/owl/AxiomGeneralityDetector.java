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

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

/**
 * Visitor that returns true when visits OWLAxiom instances that can be further generalised by a
 * simplification of its structure.
 * 
 * @author Luigi Iannone
 */
@SuppressWarnings("boxing")
public class AxiomGeneralityDetector implements OWLObjectVisitorEx<Boolean> {
    private static final AxiomGeneralityDetector instance = new AxiomGeneralityDetector();

    private AxiomGeneralityDetector() {}

    @Override
    public <T> Boolean doDefault(T object) {
        return Boolean.TRUE;
    }

    private Boolean v(OWLObject t) {
        return t.accept(this);
    }

    private boolean vt(OWLObject t) {
        return t.accept(this).booleanValue();
    }

    @Override
    public Boolean visit(OWLClass desc) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(OWLDataProperty property) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(OWLObjectProperty property) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(OWLAnnotationProperty property) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(OWLNamedIndividual individual) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(OWLLiteral literal) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(OWLSubClassOfAxiom axiom) {
        return v(axiom.getSubClass()) || v(axiom.getSuperClass());
    }

    @Override
    public Boolean visit(OWLAnnotationAssertionAxiom axiom) {
        return v(axiom.getAnnotation().getValue());
    }

    @Override
    public Boolean visit(OWLAnnotationPropertyDomainAxiom axiom) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(OWLAnnotationPropertyRangeAxiom axiom) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(OWLDatatype node) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        return v(axiom.getProperty());
    }

    @Override
    public Boolean visit(OWLClassAssertionAxiom axiom) {
        return v(axiom.getClassExpression()) || v(axiom.getIndividual());
    }

    @Override
    public Boolean visit(OWLDataPropertyAssertionAxiom axiom) {
        return v(axiom.getProperty()) || v(axiom.getObject());
    }

    @Override
    public Boolean visit(OWLDataPropertyDomainAxiom axiom) {
        return v(axiom.getProperty()) || v(axiom.getDomain());
    }

    @Override
    public Boolean visit(OWLDataPropertyRangeAxiom axiom) {
        return v(axiom.getProperty()) || v(axiom.getRange());
    }

    @Override
    public Boolean visit(OWLDatatypeDefinitionAxiom axiom) {
        return v(axiom.getDatatype());
    }

    @Override
    public Boolean visit(OWLDeclarationAxiom axiom) {
        return Boolean.FALSE;
    }

    @Override
    public Boolean visit(OWLDifferentIndividualsAxiom axiom) {
        return Boolean.valueOf(axiom.individuals().anyMatch(this::vt));
    }

    @Override
    public Boolean visit(OWLDisjointClassesAxiom axiom) {
        return Boolean.valueOf(axiom.classExpressions().anyMatch(this::vt));
    }

    @Override
    public Boolean visit(OWLDisjointDataPropertiesAxiom axiom) {
        return Boolean.valueOf(axiom.properties().anyMatch(this::vt));
    }

    @Override
    public Boolean visit(OWLDisjointObjectPropertiesAxiom axiom) {
        return Boolean.valueOf(axiom.properties().anyMatch(this::vt));
    }

    @Override
    public Boolean visit(OWLDisjointUnionAxiom axiom) {
        return Boolean.valueOf(axiom.classExpressions().anyMatch(this::vt));
    }

    @Override
    public Boolean visit(OWLEquivalentClassesAxiom axiom) {
        return Boolean.valueOf(axiom.classExpressions().anyMatch(this::vt));
    }

    @Override
    public Boolean visit(OWLEquivalentDataPropertiesAxiom axiom) {
        return Boolean.valueOf(axiom.properties().anyMatch(this::vt));
    }

    @Override
    public Boolean visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        return Boolean.valueOf(axiom.properties().anyMatch(this::vt));
    }

    @Override
    public Boolean visit(OWLFunctionalDataPropertyAxiom axiom) {
        return v(axiom.getProperty());
    }

    @Override
    public Boolean visit(OWLFunctionalObjectPropertyAxiom axiom) {
        return v(axiom.getProperty());
    }

    @Override
    public Boolean visit(OWLHasKeyAxiom axiom) {
        return Boolean.valueOf(
            axiom.propertyExpressions().anyMatch(this::vt) || vt(axiom.getClassExpression()));
    }

    @Override
    public Boolean visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return v(axiom.getProperty());
    }

    @Override
    public Boolean visit(OWLInverseObjectPropertiesAxiom axiom) {
        return v(axiom.getFirstProperty()) || v(axiom.getSecondProperty());
    }

    @Override
    public Boolean visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        return v(axiom.getProperty());
    }

    @Override
    public Boolean visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        return v(axiom.getProperty()) || v(axiom.getSubject()) || v(axiom.getObject());
    }

    @Override
    public Boolean visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return v(axiom.getProperty()) || v(axiom.getSubject()) || v(axiom.getObject());
    }

    @Override
    public Boolean visit(OWLObjectPropertyAssertionAxiom axiom) {
        return v(axiom.getProperty()) || v(axiom.getSubject()) || v(axiom.getObject());
    }

    @Override
    public Boolean visit(OWLObjectPropertyDomainAxiom axiom) {
        return v(axiom.getProperty()) || v(axiom.getDomain());
    }

    @Override
    public Boolean visit(OWLObjectPropertyRangeAxiom axiom) {
        return v(axiom.getProperty()) || v(axiom.getRange());
    }

    @Override
    public Boolean visit(OWLReflexiveObjectPropertyAxiom axiom) {
        return v(axiom.getProperty());
    }

    @Override
    public Boolean visit(OWLSameIndividualAxiom axiom) {
        return Boolean.valueOf(axiom.individuals().anyMatch(this::vt));
    }

    @Override
    public Boolean visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        return v(axiom.getSubProperty()) || v(axiom.getSuperProperty());
    }

    @Override
    public Boolean visit(OWLSubDataPropertyOfAxiom axiom) {
        return v(axiom.getSubProperty()) || v(axiom.getSuperProperty());
    }

    @Override
    public Boolean visit(OWLSubObjectPropertyOfAxiom axiom) {
        return v(axiom.getSubProperty()) || v(axiom.getSuperProperty());
    }

    @Override
    public Boolean visit(OWLSubPropertyChainOfAxiom axiom) {
        return Boolean.valueOf(axiom.getPropertyChain().stream().anyMatch(this::vt));
    }

    @Override
    public Boolean visit(OWLSymmetricObjectPropertyAxiom axiom) {
        return v(axiom.getProperty());
    }

    @Override
    public Boolean visit(OWLTransitiveObjectPropertyAxiom axiom) {
        return v(axiom.getProperty());
    }

    @Override
    public Boolean visit(SWRLRule rule) {
        return Boolean.FALSE;
    }

    /** @return the instance */
    public static AxiomGeneralityDetector getInstance() {
        return instance;
    }
}
