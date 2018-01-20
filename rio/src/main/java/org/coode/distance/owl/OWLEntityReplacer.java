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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coode.distance.ReplacementStrategy;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
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
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
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
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIArgument;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;

/** @author eleni */
public class OWLEntityReplacer implements OWLObjectVisitorEx<OWLObject> {
    private final OWLDataFactory dataFactory;
    private final ReplacementStrategy replacementStrategy;

    /**
     * @param dataFactory dataFactory
     * @param replacementStrategy replacementStrategy
     */
    public OWLEntityReplacer(OWLDataFactory dataFactory, ReplacementStrategy replacementStrategy) {
        if (dataFactory == null) {
            throw new NullPointerException("The datafactory cannot be null");
        }
        if (replacementStrategy == null) {
            throw new NullPointerException("The replacement strategy cannot be null");
        }
        this.dataFactory = dataFactory;
        this.replacementStrategy = replacementStrategy;
    }

    @Override
    public OWLObject visit(OWLSubClassOfAxiom axiom) {
        return getDataFactory().getOWLSubClassOfAxiom(
            (OWLClassExpression) axiom.getSubClass().accept(this),
            (OWLClassExpression) axiom.getSuperClass().accept(this));
    }

    @Override
    public OWLObject visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return getDataFactory().getOWLNegativeObjectPropertyAssertionAxiom(
            (OWLObjectPropertyExpression) axiom.getProperty().accept(this),
            (OWLIndividual) axiom.getSubject().accept(this),
            (OWLIndividual) axiom.getObject().accept(this));
    }

    @Override
    public OWLObject visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        return getDataFactory().getOWLAsymmetricObjectPropertyAxiom(
            (OWLObjectPropertyExpression) axiom.getProperty().accept(this));
    }

    @Override
    public OWLObject visit(OWLReflexiveObjectPropertyAxiom axiom) {
        return getDataFactory().getOWLReflexiveObjectPropertyAxiom(
            (OWLObjectPropertyExpression) axiom.getProperty().accept(this));
    }

    @Override
    public OWLObject visit(OWLDisjointClassesAxiom axiom) {
        Set<OWLClassExpression> operands = new HashSet<>(axiom.getClassExpressions().size());
        for (OWLClassExpression owlClassExpression : axiom.getClassExpressions()) {
            operands.add((OWLClassExpression) owlClassExpression.accept(this));
        }
        return getDataFactory().getOWLDisjointClassesAxiom(operands);
    }

    @Override
    public OWLObject visit(OWLDataPropertyDomainAxiom axiom) {
        return getDataFactory().getOWLDataPropertyDomainAxiom(
            (OWLDataPropertyExpression) axiom.getProperty().accept(this),
            (OWLClassExpression) axiom.getDomain().accept(this));
    }

    @Override
    public OWLObject visit(OWLObjectPropertyDomainAxiom axiom) {
        return getDataFactory().getOWLObjectPropertyDomainAxiom(
            (OWLObjectPropertyExpression) axiom.getProperty().accept(this),
            (OWLClassExpression) axiom.getDomain().accept(this));
    }

    @Override
    public OWLObject visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        Set<OWLObjectPropertyExpression> properties = new HashSet<>(axiom.getProperties().size());
        for (OWLObjectPropertyExpression owlObjectPropertyExpression : axiom.getProperties()) {
            properties.add((OWLObjectPropertyExpression) owlObjectPropertyExpression.accept(this));
        }
        return getDataFactory().getOWLEquivalentObjectPropertiesAxiom(properties);
    }

    @Override
    public OWLObject visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        return getDataFactory().getOWLNegativeDataPropertyAssertionAxiom(
            (OWLDataPropertyExpression) axiom.getProperty().accept(this),
            (OWLIndividual) axiom.getSubject().accept(this),
            (OWLLiteral) axiom.getObject().accept(this));
    }

    @Override
    public OWLObject visit(OWLDifferentIndividualsAxiom axiom) {
        Set<OWLIndividual> individuals = new HashSet<>(axiom.getIndividuals().size());
        for (OWLIndividual owlIndividual : axiom.getIndividuals()) {
            individuals.add((OWLIndividual) owlIndividual.accept(this));
        }
        return getDataFactory().getOWLDifferentIndividualsAxiom(individuals);
    }

    @Override
    public OWLObject visit(OWLDisjointDataPropertiesAxiom axiom) {
        Set<OWLDataPropertyExpression> individuals = new HashSet<>(axiom.getProperties().size());
        for (OWLDataPropertyExpression owlDataPropertyExpression : axiom.getProperties()) {
            individuals.add((OWLDataPropertyExpression) owlDataPropertyExpression.accept(this));
        }
        return getDataFactory().getOWLDisjointDataPropertiesAxiom(individuals);
    }

    @Override
    public OWLObject visit(OWLDisjointObjectPropertiesAxiom axiom) {
        Set<OWLObjectPropertyExpression> owlObjectPropertyExpressions =
            new HashSet<>(axiom.getProperties().size());
        for (OWLObjectPropertyExpression owlObjectPropertyExpression : axiom.getProperties()) {
            owlObjectPropertyExpressions
                .add((OWLObjectPropertyExpression) owlObjectPropertyExpression.accept(this));
        }
        return getDataFactory().getOWLDisjointObjectPropertiesAxiom(owlObjectPropertyExpressions);
    }

    @Override
    public OWLObject visit(OWLObjectPropertyRangeAxiom axiom) {
        return getDataFactory().getOWLObjectPropertyRangeAxiom(
            (OWLObjectPropertyExpression) axiom.getProperty().accept(this),
            (OWLClassExpression) axiom.getRange().accept(this));
    }

    @Override
    public OWLObject visit(OWLObjectPropertyAssertionAxiom axiom) {
        return getDataFactory().getOWLObjectPropertyAssertionAxiom(
            (OWLObjectPropertyExpression) axiom.getProperty().accept(this),
            (OWLIndividual) axiom.getSubject().accept(this),
            (OWLIndividual) axiom.getObject().accept(this));
    }

    @Override
    public OWLObject visit(OWLFunctionalObjectPropertyAxiom axiom) {
        return getDataFactory().getOWLFunctionalObjectPropertyAxiom(
            (OWLObjectPropertyExpression) axiom.getProperty().accept(this));
    }

    @Override
    public OWLObject visit(OWLSubObjectPropertyOfAxiom axiom) {
        return getDataFactory().getOWLSubObjectPropertyOfAxiom(
            (OWLObjectPropertyExpression) axiom.getSubProperty().accept(this),
            (OWLObjectPropertyExpression) axiom.getSuperProperty().accept(this));
    }

    @Override
    public OWLObject visit(OWLDisjointUnionAxiom axiom) {
        Set<OWLClassExpression> expressions = new HashSet<>(axiom.getClassExpressions().size());
        for (OWLClassExpression owlClassExpression : axiom.getClassExpressions()) {
            expressions.add((OWLClassExpression) owlClassExpression.accept(this));
        }
        return getDataFactory()
            .getOWLDisjointUnionAxiom((OWLClass) axiom.getOWLClass().accept(this), expressions);
    }

    @Override
    public OWLObject visit(OWLDeclarationAxiom axiom) {
        return getDataFactory().getOWLDeclarationAxiom((OWLEntity) axiom.getEntity().accept(this));
    }

    @Override
    public OWLObject visit(OWLAnnotationAssertionAxiom axiom) {
        return getDataFactory().getOWLAnnotationAssertionAxiom(
            (OWLAnnotationSubject) axiom.getSubject().accept(this),
            (OWLAnnotation) axiom.getAnnotation().accept(this));
    }

    @Override
    public OWLObject visit(OWLSymmetricObjectPropertyAxiom axiom) {
        return getDataFactory().getOWLSymmetricObjectPropertyAxiom(
            (OWLObjectPropertyExpression) axiom.getProperty().accept(this));
    }

    @Override
    public OWLObject visit(OWLDataPropertyRangeAxiom axiom) {
        return getDataFactory().getOWLDataPropertyRangeAxiom(
            (OWLDataPropertyExpression) axiom.getProperty().accept(this),
            (OWLDataRange) axiom.getRange().accept(this));
    }

    @Override
    public OWLObject visit(OWLFunctionalDataPropertyAxiom axiom) {
        return getDataFactory().getOWLFunctionalDataPropertyAxiom(
            (OWLDataPropertyExpression) axiom.getProperty().accept(this));
    }

    @Override
    public OWLObject visit(OWLEquivalentDataPropertiesAxiom axiom) {
        Set<OWLDataPropertyExpression> properties = new HashSet<>(axiom.getProperties().size());
        for (OWLDataPropertyExpression owlDataPropertyExpression : axiom.getProperties()) {
            properties.add((OWLDataPropertyExpression) owlDataPropertyExpression.accept(this));
        }
        return getDataFactory().getOWLEquivalentDataPropertiesAxiom(properties);
    }

    @Override
    public OWLObject visit(OWLClassAssertionAxiom axiom) {
        return getDataFactory().getOWLClassAssertionAxiom(
            (OWLClassExpression) axiom.getClassExpression().accept(this),
            (OWLIndividual) axiom.getIndividual().accept(this));
    }

    @Override
    public OWLObject visit(OWLEquivalentClassesAxiom axiom) {
        Set<OWLClassExpression> classExpressions =
            new HashSet<>(axiom.getClassExpressions().size());
        for (OWLClassExpression owlClassExpression : axiom.getClassExpressions()) {
            classExpressions.add((OWLClassExpression) owlClassExpression.accept(this));
        }
        return getDataFactory().getOWLEquivalentClassesAxiom(classExpressions);
    }

    @Override
    public OWLObject visit(OWLDataPropertyAssertionAxiom axiom) {
        return getDataFactory().getOWLDataPropertyAssertionAxiom(
            (OWLDataPropertyExpression) axiom.getProperty().accept(this),
            (OWLIndividual) axiom.getSubject().accept(this),
            (OWLLiteral) axiom.getObject().accept(this));
    }

    @Override
    public OWLObject visit(OWLTransitiveObjectPropertyAxiom axiom) {
        return getDataFactory().getOWLTransitiveObjectPropertyAxiom(
            (OWLObjectPropertyExpression) axiom.getProperty().accept(this));
    }

    @Override
    public OWLObject visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        return getDataFactory().getOWLIrreflexiveObjectPropertyAxiom(
            (OWLObjectPropertyExpression) axiom.getProperty().accept(this));
    }

    @Override
    public OWLObject visit(OWLSubDataPropertyOfAxiom axiom) {
        return getDataFactory().getOWLSubDataPropertyOfAxiom(
            (OWLDataPropertyExpression) axiom.getSubProperty().accept(this),
            (OWLDataPropertyExpression) axiom.getSuperProperty().accept(this));
    }

    @Override
    public OWLObject visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return getDataFactory().getOWLInverseFunctionalObjectPropertyAxiom(
            (OWLObjectPropertyExpression) axiom.getProperty().accept(this));
    }

    @Override
    public OWLObject visit(OWLSameIndividualAxiom axiom) {
        Set<OWLIndividual> individuals = new HashSet<>(axiom.getIndividuals().size());
        for (OWLIndividual owlIndividual : axiom.getIndividuals()) {
            individuals.add((OWLIndividual) owlIndividual.accept(this));
        }
        return getDataFactory().getOWLSameIndividualAxiom(individuals);
    }

    @Override
    public OWLObject visit(OWLSubPropertyChainOfAxiom axiom) {
        List<OWLObjectPropertyExpression> properties =
            new ArrayList<>(axiom.getPropertyChain().size());
        for (OWLObjectPropertyExpression owlObjectPropertyExpression : axiom.getPropertyChain()) {
            properties.add((OWLObjectPropertyExpression) owlObjectPropertyExpression.accept(this));
        }
        return getDataFactory().getOWLSubPropertyChainOfAxiom(properties,
            (OWLObjectPropertyExpression) axiom.getSuperProperty().accept(this));
    }

    @Override
    public OWLObject visit(OWLInverseObjectPropertiesAxiom axiom) {
        return getDataFactory().getOWLInverseObjectPropertiesAxiom(
            (OWLObjectPropertyExpression) axiom.getFirstProperty().accept(this),
            (OWLObjectPropertyExpression) axiom.getSecondProperty().accept(this));
    }

    @Override
    public OWLObject visit(OWLHasKeyAxiom axiom) {
        Set<OWLPropertyExpression> properties =
            new HashSet<>(axiom.getPropertyExpressions().size());
        for (OWLPropertyExpression owlPropertyExpression : axiom.getPropertyExpressions()) {
            properties.add((OWLPropertyExpression) owlPropertyExpression.accept(this));
        }
        return getDataFactory().getOWLHasKeyAxiom(
            (OWLClassExpression) axiom.getClassExpression().accept(this), properties);
    }

    @Override
    public OWLObject visit(OWLDatatypeDefinitionAxiom axiom) {
        return getDataFactory().getOWLDatatypeDefinitionAxiom(
            (OWLDatatype) axiom.getDatatype().accept(this),
            (OWLDataRange) axiom.getDataRange().accept(this));
    }

    @Override
    public OWLObject visit(SWRLRule rule) {
        Set<SWRLAtom> body = new HashSet<>(rule.getBody().size());
        Set<SWRLAtom> head = new HashSet<>(rule.getHead().size());
        for (SWRLAtom swrlAtom : rule.getHead()) {
            head.add((SWRLAtom) swrlAtom.accept(this));
        }
        for (SWRLAtom swrlAtom : rule.getBody()) {
            body.add((SWRLAtom) swrlAtom.accept(this));
        }
        return getDataFactory().getSWRLRule(head, body);
    }

    @Override
    public OWLObject visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        return getDataFactory().getOWLSubAnnotationPropertyOfAxiom(
            (OWLAnnotationProperty) axiom.getSubProperty().accept(this),
            (OWLAnnotationProperty) axiom.getSuperProperty().accept(this));
    }

    @Override
    public OWLObject visit(OWLAnnotationPropertyDomainAxiom axiom) {
        return getDataFactory().getOWLAnnotationPropertyDomainAxiom(
            (OWLAnnotationProperty) axiom.getProperty().accept(this), axiom.getDomain());
    }

    @Override
    public OWLObject visit(OWLAnnotationPropertyRangeAxiom axiom) {
        return getDataFactory().getOWLAnnotationPropertyRangeAxiom(
            (OWLAnnotationProperty) axiom.getProperty().accept(this), axiom.getRange());
    }

    @Override
    public OWLObject visit(OWLClass ce) {
        return getReplacementStrategy().replace(ce);
    }

    @Override
    public OWLObject visit(OWLObjectIntersectionOf ce) {
        Set<OWLClassExpression> operands = new HashSet<>(ce.getOperands().size());
        for (OWLClassExpression owlClassExpression : ce.getOperands()) {
            operands.add((OWLClassExpression) owlClassExpression.accept(this));
        }
        return getDataFactory().getOWLObjectIntersectionOf(operands);
    }

    @Override
    public OWLObject visit(OWLObjectUnionOf ce) {
        Set<OWLClassExpression> operands = new HashSet<>(ce.getOperands().size());
        for (OWLClassExpression owlClassExpression : ce.getOperands()) {
            operands.add((OWLClassExpression) owlClassExpression.accept(this));
        }
        return getDataFactory().getOWLObjectUnionOf(operands);
    }

    @Override
    public OWLObject visit(OWLObjectComplementOf ce) {
        return getDataFactory()
            .getOWLObjectComplementOf((OWLClassExpression) ce.getOperand().accept(this));
    }

    @Override
    public OWLObject visit(OWLObjectSomeValuesFrom ce) {
        return getDataFactory().getOWLObjectSomeValuesFrom(
            (OWLObjectPropertyExpression) ce.getProperty().accept(this),
            (OWLClassExpression) ce.getFiller().accept(this));
    }

    @Override
    public OWLObject visit(OWLObjectAllValuesFrom ce) {
        return getDataFactory().getOWLObjectAllValuesFrom(
            (OWLObjectPropertyExpression) ce.getProperty().accept(this),
            (OWLClassExpression) ce.getFiller().accept(this));
    }

    @Override
    public OWLObject visit(OWLObjectHasValue ce) {
        return getDataFactory().getOWLObjectHasValue(
            (OWLObjectPropertyExpression) ce.getProperty().accept(this),
            (OWLIndividual) ce.getValue().accept(this));
    }

    @Override
    public OWLObject visit(OWLObjectMinCardinality ce) {
        return ce.getFiller() == null
            ? getDataFactory().getOWLObjectMinCardinality(ce.getCardinality(),
                (OWLObjectPropertyExpression) ce.getProperty().accept(this))
            : getDataFactory().getOWLObjectMinCardinality(ce.getCardinality(),
                (OWLObjectPropertyExpression) ce.getProperty().accept(this),
                (OWLClassExpression) ce.getFiller().accept(this));
    }

    @Override
    public OWLObject visit(OWLObjectExactCardinality ce) {
        return ce.getFiller() == null
            ? getDataFactory().getOWLObjectExactCardinality(ce.getCardinality(),
                (OWLObjectPropertyExpression) ce.getProperty().accept(this))
            : getDataFactory().getOWLObjectExactCardinality(ce.getCardinality(),
                (OWLObjectPropertyExpression) ce.getProperty().accept(this),
                (OWLClassExpression) ce.getFiller().accept(this));
    }

    @Override
    public OWLObject visit(OWLObjectMaxCardinality ce) {
        return ce.getFiller() == null
            ? getDataFactory().getOWLObjectMaxCardinality(ce.getCardinality(),
                (OWLObjectPropertyExpression) ce.getProperty().accept(this))
            : getDataFactory().getOWLObjectMaxCardinality(ce.getCardinality(),
                (OWLObjectPropertyExpression) ce.getProperty().accept(this),
                (OWLClassExpression) ce.getFiller().accept(this));
    }

    @Override
    public OWLObject visit(OWLObjectHasSelf ce) {
        return getDataFactory()
            .getOWLObjectHasSelf((OWLObjectPropertyExpression) ce.getProperty().accept(this));
    }

    @Override
    public OWLObject visit(OWLObjectOneOf ce) {
        Set<OWLIndividual> individuals = new HashSet<>(ce.getIndividuals().size());
        for (OWLIndividual owlIndividual : ce.getIndividuals()) {
            individuals.add((OWLIndividual) owlIndividual.accept(this));
        }
        return getDataFactory().getOWLObjectOneOf(individuals);
    }

    @Override
    public OWLObject visit(OWLDataSomeValuesFrom ce) {
        return getDataFactory().getOWLDataSomeValuesFrom(
            (OWLDataPropertyExpression) ce.getProperty().accept(this),
            (OWLDataRange) ce.getFiller().accept(this));
    }

    @Override
    public OWLObject visit(OWLDataAllValuesFrom ce) {
        return getDataFactory().getOWLDataAllValuesFrom(
            (OWLDataPropertyExpression) ce.getProperty().accept(this),
            (OWLDataRange) ce.getFiller().accept(this));
    }

    @Override
    public OWLObject visit(OWLDataHasValue ce) {
        return getDataFactory().getOWLDataHasValue(
            (OWLDataPropertyExpression) ce.getProperty().accept(this),
            (OWLLiteral) ce.getValue().accept(this));
    }

    @Override
    public OWLObject visit(OWLDataMinCardinality ce) {
        return ce.getFiller() == null
            ? getDataFactory().getOWLDataMinCardinality(ce.getCardinality(),
                (OWLDataPropertyExpression) ce.getProperty().accept(this))
            : getDataFactory().getOWLDataMinCardinality(ce.getCardinality(),
                (OWLDataPropertyExpression) ce.getProperty().accept(this),
                (OWLDataRange) ce.getFiller().accept(this));
    }

    @Override
    public OWLObject visit(OWLDataExactCardinality ce) {
        return ce.getFiller() == null
            ? getDataFactory().getOWLDataExactCardinality(ce.getCardinality(),
                (OWLDataPropertyExpression) ce.getProperty().accept(this))
            : getDataFactory().getOWLDataExactCardinality(ce.getCardinality(),
                (OWLDataPropertyExpression) ce.getProperty().accept(this),
                (OWLDataRange) ce.getFiller().accept(this));
    }

    @Override
    public OWLObject visit(OWLDataMaxCardinality ce) {
        return ce.getFiller() == null
            ? getDataFactory().getOWLDataMaxCardinality(ce.getCardinality(),
                (OWLDataPropertyExpression) ce.getProperty().accept(this))
            : getDataFactory().getOWLDataMaxCardinality(ce.getCardinality(),
                (OWLDataPropertyExpression) ce.getProperty().accept(this),
                (OWLDataRange) ce.getFiller().accept(this));
    }

    @Override
    public OWLObject visit(OWLDatatype node) {
        return node;
    }

    @Override
    public OWLObject visit(OWLDataComplementOf node) {
        return getDataFactory()
            .getOWLDataComplementOf((OWLDataRange) node.getDataRange().accept(this));
    }

    @Override
    public OWLObject visit(OWLDataOneOf node) {
        Set<OWLLiteral> literals = new HashSet<>(node.getValues().size());
        for (OWLLiteral owlLiteral : node.getValues()) {
            literals.add((OWLLiteral) owlLiteral.accept(this));
        }
        return getDataFactory().getOWLDataOneOf(literals);
    }

    @Override
    public OWLObject visit(OWLDataIntersectionOf node) {
        Set<OWLDataRange> operands = new HashSet<>(node.getOperands().size());
        for (OWLDataRange owlDataRange : node.getOperands()) {
            operands.add((OWLDataRange) owlDataRange.accept(this));
        }
        return getDataFactory().getOWLDataIntersectionOf(operands);
    }

    @Override
    public OWLObject visit(OWLDataUnionOf node) {
        Set<OWLDataRange> operands = new HashSet<>(node.getOperands().size());
        for (OWLDataRange owlDataRange : node.getOperands()) {
            operands.add((OWLDataRange) owlDataRange.accept(this));
        }
        return getDataFactory().getOWLDataUnionOf(operands);
    }

    @Override
    public OWLObject visit(OWLDatatypeRestriction node) {
        Set<OWLFacetRestriction> facetRestrictions =
            new HashSet<>(node.getFacetRestrictions().size());
        for (OWLFacetRestriction owlFacetRestriction : node.getFacetRestrictions()) {
            facetRestrictions.add((OWLFacetRestriction) owlFacetRestriction.accept(this));
        }
        return getDataFactory().getOWLDatatypeRestriction(
            (OWLDatatype) node.getDatatype().accept(this), facetRestrictions);
    }

    @Override
    public OWLObject visit(OWLLiteral node) {
        return getReplacementStrategy().replace(node);
    }

    @Override
    public OWLObject visit(OWLFacetRestriction node) {
        return getDataFactory().getOWLFacetRestriction(node.getFacet(),
            (OWLLiteral) node.getFacetValue().accept(this));
    }

    @Override
    public OWLObject visit(OWLObjectProperty property) {
        return getReplacementStrategy().replace(property);
    }

    @Override
    public OWLObject visit(OWLObjectInverseOf property) {
        return getDataFactory()
            .getOWLObjectInverseOf((OWLObjectProperty) property.getInverse().accept(this));
    }

    @Override
    public OWLObject visit(OWLDataProperty property) {
        return getReplacementStrategy().replace(property);
    }

    @Override
    public OWLObject visit(OWLNamedIndividual individual) {
        return getReplacementStrategy().replace(individual);
    }

    @Override
    public OWLObject visit(OWLAnnotationProperty property) {
        return getReplacementStrategy().replace(property);
    }

    @Override
    public OWLObject visit(OWLAnnotation node) {
        return getDataFactory().getOWLAnnotation(
            (OWLAnnotationProperty) node.getProperty().accept(this),
            (OWLAnnotationValue) node.getValue().accept(this));
    }

    @Override
    public OWLObject visit(IRI iri) {
        return getReplacementStrategy().replace(iri);
    }

    @Override
    public OWLObject visit(OWLAnonymousIndividual individual) {
        return individual;
    }

    @Override
    public OWLObject visit(SWRLClassAtom node) {
        return getDataFactory().getSWRLClassAtom(
            (OWLClassExpression) node.getPredicate().accept(this),
            (SWRLIArgument) node.getArgument().accept(this));
    }

    @Override
    public OWLObject visit(SWRLDataRangeAtom node) {
        return getDataFactory().getSWRLDataRangeAtom(
            (OWLDataRange) node.getPredicate().accept(this),
            (SWRLDArgument) node.getArgument().accept(this));
    }

    @Override
    public OWLObject visit(SWRLObjectPropertyAtom node) {
        return getDataFactory().getSWRLObjectPropertyAtom(
            (OWLObjectPropertyExpression) node.getPredicate().accept(this),
            (SWRLIArgument) node.getFirstArgument().accept(this),
            (SWRLIArgument) node.getSecondArgument().accept(this));
    }

    @Override
    public OWLObject visit(SWRLDataPropertyAtom node) {
        return getDataFactory().getSWRLDataPropertyAtom(
            (OWLDataPropertyExpression) node.getPredicate().accept(this),
            (SWRLIArgument) node.getFirstArgument().accept(this),
            (SWRLDArgument) node.getSecondArgument().accept(this));
    }

    @Override
    public OWLObject visit(SWRLBuiltInAtom node) {
        List<SWRLDArgument> arguments = new ArrayList<>(node.getArguments().size());
        for (SWRLDArgument swrldArgument : node.getArguments()) {
            arguments.add((SWRLDArgument) swrldArgument.accept(this));
        }
        return getDataFactory().getSWRLBuiltInAtom(node.getPredicate(), arguments);
    }

    @Override
    public OWLObject visit(SWRLVariable node) {
        return node;
    }

    @Override
    public OWLObject visit(SWRLIndividualArgument node) {
        return getDataFactory()
            .getSWRLIndividualArgument((OWLIndividual) node.getIndividual().accept(this));
    }

    @Override
    public OWLObject visit(SWRLLiteralArgument node) {
        return getDataFactory().getSWRLLiteralArgument((OWLLiteral) node.getLiteral().accept(this));
    }

    @Override
    public OWLObject visit(SWRLSameIndividualAtom node) {
        return getDataFactory().getSWRLSameIndividualAtom(
            (SWRLIArgument) node.getFirstArgument().accept(this),
            (SWRLIArgument) node.getSecondArgument().accept(this));
    }

    @Override
    public OWLObject visit(SWRLDifferentIndividualsAtom node) {
        return getDataFactory().getSWRLDifferentIndividualsAtom(
            (SWRLIArgument) node.getFirstArgument().accept(this),
            (SWRLIArgument) node.getSecondArgument().accept(this));
    }

    @Override
    public OWLObject visit(OWLOntology ontology) {
        return ontology;
    }

    /** @return the dataFactory */
    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }

    /** @return the replacementStrategy */
    public ReplacementStrategy getReplacementStrategy() {
        return replacementStrategy;
    }
}
