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

import java.util.Iterator;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
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
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
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
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

/** Visitor that returns true when visits OWLAxiom instances that can be further
 * generalised by a simplification of its structure.
 * 
 * @author Luigi Iannone */
@SuppressWarnings("boxing")
public class AxiomGeneralityDetector extends OWLObjectVisitorExAdapter<Boolean> implements
        OWLAxiomVisitorEx<Boolean> {
    private final static AxiomGeneralityDetector instance = new AxiomGeneralityDetector();

    private AxiomGeneralityDetector() {
        super(true);
    }

    @Override
    public Boolean visit(final OWLClass desc) {
        return false;
    }

    @Override
    public Boolean visit(final OWLDataProperty property) {
        return false;
    }

    @Override
    public Boolean visit(final OWLObjectProperty property) {
        return false;
    }

    @Override
    public Boolean visit(final OWLAnnotationProperty property) {
        return false;
    }

    @Override
    public Boolean visit(final OWLNamedIndividual individual) {
        return false;
    }

    @Override
    public Boolean visit(final OWLLiteral literal) {
        return false;
    }

    @Override
    public Boolean visit(final OWLSubClassOfAxiom axiom) {
        return axiom.getSubClass().accept(this) || axiom.getSuperClass().accept(this);
    }

    @Override
    public Boolean visit(final OWLAnnotationAssertionAxiom axiom) {
        return axiom.getAnnotation().getValue().accept(this);
    }

    @Override
    public Boolean visit(final OWLAnnotationPropertyDomainAxiom axiom) {
        return false;
    }

    @Override
    public Boolean visit(final OWLAnnotationPropertyRangeAxiom axiom) {
        return false;
    }

    @Override
    public Boolean visit(final OWLDatatype node) {
        return false;
    }

    @Override
    public Boolean visit(final OWLAsymmetricObjectPropertyAxiom axiom) {
        return axiom.getProperty().accept(this);
    }

    @Override
    public Boolean visit(final OWLClassAssertionAxiom axiom) {
        return axiom.getClassExpression().accept(this)
                || axiom.getIndividual().accept(this);
    }

    @Override
    public Boolean visit(final OWLDataPropertyAssertionAxiom axiom) {
        return axiom.getProperty().accept(this) || axiom.getObject().accept(this);
    }

    @Override
    public Boolean visit(final OWLDataPropertyDomainAxiom axiom) {
        return axiom.getProperty().accept(this) || axiom.getDomain().accept(this);
    }

    @Override
    public Boolean visit(final OWLDataPropertyRangeAxiom axiom) {
        return axiom.getProperty().accept(this) || axiom.getRange().accept(this);
    }

    @Override
    public Boolean visit(final OWLDatatypeDefinitionAxiom axiom) {
        return axiom.getDatatype().accept(this);
    }

    @Override
    public Boolean visit(final OWLDeclarationAxiom axiom) {
        return false;
    }

    @Override
    public Boolean visit(final OWLDifferentIndividualsAxiom axiom) {
        Iterator<OWLIndividual> iterator = axiom.getIndividuals().iterator();
        boolean found = false;
        while (!found && iterator.hasNext()) {
            OWLIndividual owlIndividual = iterator.next();
            found = owlIndividual.accept(this);
        }
        return found;
    }

    @Override
    public Boolean visit(final OWLDisjointClassesAxiom axiom) {
        Iterator<OWLClassExpression> iterator = axiom.getClassExpressions().iterator();
        boolean found = false;
        while (!found && iterator.hasNext()) {
            OWLClassExpression owlClassDescription = iterator.next();
            found = owlClassDescription.accept(this);
        }
        return found;
    }

    @Override
    public Boolean visit(final OWLDisjointDataPropertiesAxiom axiom) {
        Iterator<OWLDataPropertyExpression> iterator = axiom.getProperties().iterator();
        boolean found = false;
        while (!found && iterator.hasNext()) {
            OWLDataPropertyExpression owlDataProperty = iterator.next();
            found = owlDataProperty.accept(this);
        }
        return found;
    }

    @Override
    public Boolean visit(final OWLDisjointObjectPropertiesAxiom axiom) {
        Iterator<OWLObjectPropertyExpression> iterator = axiom.getProperties().iterator();
        boolean found = false;
        while (!found && iterator.hasNext()) {
            OWLObjectPropertyExpression owlObjectProperty = iterator.next();
            found = owlObjectProperty.accept(this);
        }
        return found;
    }

    @Override
    public Boolean visit(final OWLDisjointUnionAxiom axiom) {
        boolean found = false;
        Iterator<OWLClassExpression> iterator = axiom.getClassExpressions().iterator();
        while (!found && iterator.hasNext()) {
            OWLClassExpression owlClassExpression = iterator.next();
            found = owlClassExpression.accept(this);
        }
        return found;
    }

    @Override
    public Boolean visit(final OWLEquivalentClassesAxiom axiom) {
        boolean found = false;
        Iterator<OWLClassExpression> iterator = axiom.getClassExpressions().iterator();
        while (!found && iterator.hasNext()) {
            OWLClassExpression owlClassExpression = iterator.next();
            found = owlClassExpression.accept(this);
        }
        return found;
    }

    @Override
    public Boolean visit(final OWLEquivalentDataPropertiesAxiom axiom) {
        Iterator<OWLDataPropertyExpression> iterator = axiom.getProperties().iterator();
        boolean found = false;
        while (!found && iterator.hasNext()) {
            OWLDataPropertyExpression owlDataProperty = iterator.next();
            found = owlDataProperty.accept(this);
        }
        return found;
    }

    @Override
    public Boolean visit(final OWLEquivalentObjectPropertiesAxiom axiom) {
        Iterator<OWLObjectPropertyExpression> iterator = axiom.getProperties().iterator();
        boolean found = false;
        while (!found && iterator.hasNext()) {
            OWLObjectPropertyExpression owlObjectProperty = iterator.next();
            found = owlObjectProperty.accept(this);
        }
        return found;
    }

    @Override
    public Boolean visit(final OWLFunctionalDataPropertyAxiom axiom) {
        return axiom.getProperty().accept(this);
    }

    @Override
    public Boolean visit(final OWLFunctionalObjectPropertyAxiom axiom) {
        return axiom.getProperty().accept(this);
    }

    @Override
    public Boolean visit(final OWLHasKeyAxiom axiom) {
        Iterator<OWLPropertyExpression<?, ?>> iterator = axiom.getPropertyExpressions()
                .iterator();
        boolean found = false;
        while (!found && iterator.hasNext()) {
            OWLPropertyExpression<?, ?> owlPropertyExpression = iterator.next();
            found = owlPropertyExpression.accept(this);
        }
        if (!found) {
            found = axiom.getClassExpression().accept(this);
        }
        return found;
    }

    @Override
    public Boolean visit(final OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return axiom.getProperty().accept(this);
    }

    @Override
    public Boolean visit(final OWLInverseObjectPropertiesAxiom axiom) {
        return axiom.getFirstProperty().accept(this)
                || axiom.getSecondProperty().accept(this);
    }

    @Override
    public Boolean visit(final OWLIrreflexiveObjectPropertyAxiom axiom) {
        return axiom.getProperty().accept(this);
    }

    @Override
    public Boolean visit(final OWLNegativeDataPropertyAssertionAxiom axiom) {
        return axiom.getProperty().accept(this) || axiom.getSubject().accept(this)
                || axiom.getObject().accept(this);
    }

    @Override
    public Boolean visit(final OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return axiom.getProperty().accept(this) || axiom.getSubject().accept(this)
                || axiom.getObject().accept(this);
    }

    @Override
    public Boolean visit(final OWLObjectPropertyAssertionAxiom axiom) {
        return axiom.getProperty().accept(this) || axiom.getSubject().accept(this)
                || axiom.getObject().accept(this);
    }

    @Override
    public Boolean visit(final OWLObjectPropertyDomainAxiom axiom) {
        return axiom.getProperty().accept(this) || axiom.getDomain().accept(this);
    }

    @Override
    public Boolean visit(final OWLObjectPropertyRangeAxiom axiom) {
        return axiom.getProperty().accept(this) || axiom.getRange().accept(this);
    }

    @Override
    public Boolean visit(final OWLReflexiveObjectPropertyAxiom axiom) {
        return axiom.getProperty().accept(this);
    }

    @Override
    public Boolean visit(final OWLSameIndividualAxiom axiom) {
        Iterator<OWLIndividual> iterator = axiom.getIndividuals().iterator();
        boolean found = false;
        while (!found && iterator.hasNext()) {
            OWLIndividual owlIndividual = iterator.next();
            found = owlIndividual.accept(this);
        }
        return found;
    }

    @Override
    public Boolean visit(final OWLSubAnnotationPropertyOfAxiom axiom) {
        return axiom.getSubProperty().accept(this)
                || axiom.getSuperProperty().accept(this);
    }

    @Override
    public Boolean visit(final OWLSubDataPropertyOfAxiom axiom) {
        return axiom.getSubProperty().accept(this)
                || axiom.getSuperProperty().accept(this);
    }

    @Override
    public Boolean visit(final OWLSubObjectPropertyOfAxiom axiom) {
        return axiom.getSubProperty().accept(this)
                || axiom.getSuperProperty().accept(this);
    }

    @Override
    public Boolean visit(final OWLSubPropertyChainOfAxiom axiom) {
        Iterator<OWLObjectPropertyExpression> iterator = axiom.getPropertyChain()
                .iterator();
        boolean found = false;
        while (!found && iterator.hasNext()) {
            OWLObjectPropertyExpression owlObjectPropertyExpression = iterator.next();
            found = owlObjectPropertyExpression.accept(this);
        }
        return found;
    }

    @Override
    public Boolean visit(final OWLSymmetricObjectPropertyAxiom axiom) {
        return axiom.getProperty().accept(this);
    }

    @Override
    public Boolean visit(final OWLTransitiveObjectPropertyAxiom axiom) {
        return axiom.getProperty().accept(this);
    }

    @Override
    public Boolean visit(final SWRLRule rule) {
        return false;
    }

    /** @return the instance */
    public static AxiomGeneralityDetector getInstance() {
        return instance;
    }
}