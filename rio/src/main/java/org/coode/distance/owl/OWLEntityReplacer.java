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

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

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
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
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
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
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
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
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

    private <T extends OWLObject> T v(T t) {
        return (T) t.accept(this);
    }


    @Override
    public OWLObject visit(OWLSubClassOfAxiom axiom) {
        return getDataFactory().getOWLSubClassOfAxiom(v(axiom.getSubClass()),
            v(axiom.getSuperClass()));
    }

    @Override
    public OWLObject visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return getDataFactory().getOWLNegativeObjectPropertyAssertionAxiom(v(axiom.getProperty()),
            v(axiom.getSubject()), v(axiom.getObject()));
    }

    @Override
    public OWLObject visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        return getDataFactory().getOWLAsymmetricObjectPropertyAxiom(v(axiom.getProperty()));
    }

    @Override
    public OWLObject visit(OWLReflexiveObjectPropertyAxiom axiom) {
        return getDataFactory().getOWLReflexiveObjectPropertyAxiom(v(axiom.getProperty()));
    }

    @Override
    public OWLObject visit(OWLDisjointClassesAxiom axiom) {
        return getDataFactory().getOWLDisjointClassesAxiom(axiom.operands().map(this::v));
    }

    @Override
    public OWLObject visit(OWLDataPropertyDomainAxiom axiom) {
        return getDataFactory().getOWLDataPropertyDomainAxiom(v(axiom.getProperty()),
            v(axiom.getDomain()));
    }

    @Override
    public OWLObject visit(OWLObjectPropertyDomainAxiom axiom) {
        return getDataFactory().getOWLObjectPropertyDomainAxiom(v(axiom.getProperty()),
            v(axiom.getDomain()));
    }

    @Override
    public OWLObject visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        return getDataFactory()
            .getOWLEquivalentObjectPropertiesAxiom(asList(axiom.properties().map(this::v)));
    }

    @Override
    public OWLObject visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        return getDataFactory().getOWLNegativeDataPropertyAssertionAxiom(v(axiom.getProperty()),
            v(axiom.getSubject()), v(axiom.getObject()));
    }

    @Override
    public OWLObject visit(OWLDifferentIndividualsAxiom axiom) {
        Set<OWLIndividual> individuals = new HashSet<>(axiom.getOperandsAsList().size());
        for (OWLIndividual owlIndividual : axiom.getOperandsAsList()) {
            individuals.add(v(owlIndividual));
        }
        return getDataFactory().getOWLDifferentIndividualsAxiom(individuals);
    }

    @Override
    public OWLObject visit(OWLDisjointDataPropertiesAxiom axiom) {
        return getDataFactory()
            .getOWLDisjointDataPropertiesAxiom(asList(axiom.properties().map(this::v)));
    }

    @Override
    public OWLObject visit(OWLDisjointObjectPropertiesAxiom axiom) {
        return getDataFactory()
            .getOWLDisjointObjectPropertiesAxiom(asList(axiom.properties().map(this::v)));
    }

    @Override
    public OWLObject visit(OWLObjectPropertyRangeAxiom axiom) {
        return getDataFactory().getOWLObjectPropertyRangeAxiom(v(axiom.getProperty()),
            v(axiom.getRange()));
    }

    @Override
    public OWLObject visit(OWLObjectPropertyAssertionAxiom axiom) {
        return getDataFactory().getOWLObjectPropertyAssertionAxiom(v(axiom.getProperty()),
            v(axiom.getSubject()), v(axiom.getObject()));
    }

    @Override
    public OWLObject visit(OWLFunctionalObjectPropertyAxiom axiom) {
        return getDataFactory().getOWLFunctionalObjectPropertyAxiom(v(axiom.getProperty()));
    }

    @Override
    public OWLObject visit(OWLSubObjectPropertyOfAxiom axiom) {
        return getDataFactory().getOWLSubObjectPropertyOfAxiom(v(axiom.getSubProperty()),
            v(axiom.getSuperProperty()));
    }

    @Override
    public OWLObject visit(OWLDisjointUnionAxiom axiom) {
        return getDataFactory().getOWLDisjointUnionAxiom(v(axiom.getOWLClass()),
            axiom.classExpressions().map(this::v));
    }

    @Override
    public OWLObject visit(OWLDeclarationAxiom axiom) {
        return getDataFactory().getOWLDeclarationAxiom(v(axiom.getEntity()));
    }

    @Override
    public OWLObject visit(OWLAnnotationAssertionAxiom axiom) {
        return getDataFactory().getOWLAnnotationAssertionAxiom(v(axiom.getSubject()),
            v(axiom.getAnnotation()));
    }

    @Override
    public OWLObject visit(OWLSymmetricObjectPropertyAxiom axiom) {
        return getDataFactory().getOWLSymmetricObjectPropertyAxiom(v(axiom.getProperty()));
    }

    @Override
    public OWLObject visit(OWLDataPropertyRangeAxiom axiom) {
        return getDataFactory().getOWLDataPropertyRangeAxiom(v(axiom.getProperty()),
            v(axiom.getRange()));
    }

    @Override
    public OWLObject visit(OWLFunctionalDataPropertyAxiom axiom) {
        return getDataFactory().getOWLFunctionalDataPropertyAxiom(v(axiom.getProperty()));
    }

    @Override
    public OWLObject visit(OWLEquivalentDataPropertiesAxiom axiom) {
        return getDataFactory()
            .getOWLEquivalentDataPropertiesAxiom(asList(axiom.properties().map(this::v)));
    }

    @Override
    public OWLObject visit(OWLClassAssertionAxiom axiom) {
        return getDataFactory().getOWLClassAssertionAxiom(v(axiom.getClassExpression()),
            v(axiom.getIndividual()));
    }

    @Override
    public OWLObject visit(OWLEquivalentClassesAxiom axiom) {
        return getDataFactory()
            .getOWLEquivalentClassesAxiom(asList(axiom.classExpressions().map(this::v)));
    }

    @Override
    public OWLObject visit(OWLDataPropertyAssertionAxiom axiom) {
        return getDataFactory().getOWLDataPropertyAssertionAxiom(v(axiom.getProperty()),
            v(axiom.getSubject()), v(axiom.getObject()));
    }

    @Override
    public OWLObject visit(OWLTransitiveObjectPropertyAxiom axiom) {
        return getDataFactory().getOWLTransitiveObjectPropertyAxiom(v(axiom.getProperty()));
    }

    @Override
    public OWLObject visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        return getDataFactory().getOWLIrreflexiveObjectPropertyAxiom(v(axiom.getProperty()));
    }

    @Override
    public OWLObject visit(OWLSubDataPropertyOfAxiom axiom) {
        return getDataFactory().getOWLSubDataPropertyOfAxiom(v(axiom.getSubProperty()),
            v(axiom.getSuperProperty()));
    }

    @Override
    public OWLObject visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return getDataFactory().getOWLInverseFunctionalObjectPropertyAxiom(v(axiom.getProperty()));
    }

    @Override
    public OWLObject visit(OWLSameIndividualAxiom axiom) {
        Set<OWLIndividual> individuals = new HashSet<>(axiom.getOperandsAsList().size());
        for (OWLIndividual owlIndividual : axiom.getOperandsAsList()) {
            individuals.add(v(owlIndividual));
        }
        return getDataFactory().getOWLSameIndividualAxiom(individuals);
    }

    @Override
    public OWLObject visit(OWLSubPropertyChainOfAxiom axiom) {
        List<OWLObjectPropertyExpression> properties =
            new ArrayList<>(axiom.getPropertyChain().size());
        for (OWLObjectPropertyExpression owlObjectPropertyExpression : axiom.getPropertyChain()) {
            properties.add(v(owlObjectPropertyExpression));
        }
        return getDataFactory().getOWLSubPropertyChainOfAxiom(properties,
            v(axiom.getSuperProperty()));
    }

    @Override
    public OWLObject visit(OWLInverseObjectPropertiesAxiom axiom) {
        return getDataFactory().getOWLInverseObjectPropertiesAxiom(v(axiom.getFirstProperty()),
            v(axiom.getSecondProperty()));
    }

    @Override
    public OWLObject visit(OWLHasKeyAxiom axiom) {
        return getDataFactory().getOWLHasKeyAxiom(v(axiom.getClassExpression()),
            asSet(axiom.propertyExpressions().map(this::v)));
    }

    @Override
    public OWLObject visit(OWLDatatypeDefinitionAxiom axiom) {
        return getDataFactory().getOWLDatatypeDefinitionAxiom(v(axiom.getDatatype()),
            v(axiom.getDataRange()));
    }

    @Override
    public OWLObject visit(SWRLRule rule) {
        return getDataFactory().getSWRLRule(asList(rule.head().map(this::v)),
            asList(rule.body().map(this::v)));
    }

    @Override
    public OWLObject visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        return getDataFactory().getOWLSubAnnotationPropertyOfAxiom(v(axiom.getSubProperty()),
            v(axiom.getSuperProperty()));
    }

    @Override
    public OWLObject visit(OWLAnnotationPropertyDomainAxiom axiom) {
        return getDataFactory().getOWLAnnotationPropertyDomainAxiom(v(axiom.getProperty()),
            axiom.getDomain());
    }

    @Override
    public OWLObject visit(OWLAnnotationPropertyRangeAxiom axiom) {
        return getDataFactory().getOWLAnnotationPropertyRangeAxiom(v(axiom.getProperty()),
            axiom.getRange());
    }

    @Override
    public OWLObject visit(OWLClass ce) {
        return getReplacementStrategy().replace(ce);
    }

    @Override
    public OWLObject visit(OWLObjectIntersectionOf ce) {
        return getDataFactory().getOWLObjectIntersectionOf(ce.operands().map(this::v));
    }

    @Override
    public OWLObject visit(OWLObjectUnionOf ce) {
        return getDataFactory().getOWLObjectUnionOf(ce.operands().map(this::v));
    }

    @Override
    public OWLObject visit(OWLObjectComplementOf ce) {
        return getDataFactory().getOWLObjectComplementOf(v(ce.getOperand()));
    }

    @Override
    public OWLObject visit(OWLObjectSomeValuesFrom ce) {
        return getDataFactory().getOWLObjectSomeValuesFrom(v(ce.getProperty()), v(ce.getFiller()));
    }

    @Override
    public OWLObject visit(OWLObjectAllValuesFrom ce) {
        return getDataFactory().getOWLObjectAllValuesFrom(v(ce.getProperty()), v(ce.getFiller()));
    }

    @Override
    public OWLObject visit(OWLObjectHasValue ce) {
        return getDataFactory().getOWLObjectHasValue(v(ce.getProperty()), v(ce.getFiller()));
    }

    @Override
    public OWLObject visit(OWLObjectMinCardinality ce) {
        return getDataFactory().getOWLObjectMinCardinality(ce.getCardinality(), v(ce.getProperty()),
            v(ce.getFiller()));
    }

    @Override
    public OWLObject visit(OWLObjectExactCardinality ce) {
        return getDataFactory().getOWLObjectExactCardinality(ce.getCardinality(),
            v(ce.getProperty()), v(ce.getFiller()));
    }

    @Override
    public OWLObject visit(OWLObjectMaxCardinality ce) {
        return ce.getFiller() == null
            ? getDataFactory().getOWLObjectMaxCardinality(ce.getCardinality(), v(ce.getProperty()))
            : getDataFactory().getOWLObjectMaxCardinality(ce.getCardinality(), v(ce.getProperty()),
                v(ce.getFiller()));
    }

    @Override
    public OWLObject visit(OWLObjectHasSelf ce) {
        return getDataFactory().getOWLObjectHasSelf(v(ce.getProperty()));
    }

    @Override
    public OWLObject visit(OWLObjectOneOf ce) {
        Set<OWLIndividual> individuals = new HashSet<>(ce.getOperandsAsList().size());
        for (OWLIndividual owlIndividual : ce.getOperandsAsList()) {
            individuals.add(v(owlIndividual));
        }
        return getDataFactory().getOWLObjectOneOf(individuals);
    }

    @Override
    public OWLObject visit(OWLDataSomeValuesFrom ce) {
        return getDataFactory().getOWLDataSomeValuesFrom(v(ce.getProperty()), v(ce.getFiller()));
    }

    @Override
    public OWLObject visit(OWLDataAllValuesFrom ce) {
        return getDataFactory().getOWLDataAllValuesFrom(v(ce.getProperty()), v(ce.getFiller()));
    }

    @Override
    public OWLObject visit(OWLDataHasValue ce) {
        return getDataFactory().getOWLDataHasValue(v(ce.getProperty()), v(ce.getFiller()));
    }

    @Override
    public OWLObject visit(OWLDataMinCardinality ce) {
        return ce.getFiller() == null
            ? getDataFactory().getOWLDataMinCardinality(ce.getCardinality(), v(ce.getProperty()))
            : getDataFactory().getOWLDataMinCardinality(ce.getCardinality(), v(ce.getProperty()),
                v(ce.getFiller()));
    }

    @Override
    public OWLObject visit(OWLDataExactCardinality ce) {
        return ce.getFiller() == null
            ? getDataFactory().getOWLDataExactCardinality(ce.getCardinality(), v(ce.getProperty()))
            : getDataFactory().getOWLDataExactCardinality(ce.getCardinality(), v(ce.getProperty()),
                v(ce.getFiller()));
    }

    @Override
    public OWLObject visit(OWLDataMaxCardinality ce) {
        return ce.getFiller() == null
            ? getDataFactory().getOWLDataMaxCardinality(ce.getCardinality(), v(ce.getProperty()))
            : getDataFactory().getOWLDataMaxCardinality(ce.getCardinality(), v(ce.getProperty()),
                v(ce.getFiller()));
    }

    @Override
    public OWLObject visit(OWLDatatype node) {
        return node;
    }

    @Override
    public OWLObject visit(OWLDataComplementOf node) {
        return getDataFactory().getOWLDataComplementOf(v(node.getDataRange()));
    }

    @Override
    public OWLObject visit(OWLDataOneOf node) {
        return getDataFactory().getOWLDataOneOf(node.values().map(this::v));
    }

    @Override
    public OWLObject visit(OWLDataIntersectionOf node) {
        return getDataFactory().getOWLDataIntersectionOf(node.operands().map(this::v));
    }

    @Override
    public OWLObject visit(OWLDataUnionOf node) {
        return getDataFactory().getOWLDataUnionOf(node.operands().map(this::v));
    }

    @Override
    public OWLObject visit(OWLDatatypeRestriction node) {
        return getDataFactory().getOWLDatatypeRestriction(v(node.getDatatype()),
            asList(node.facetRestrictions().map(this::v)));
    }

    @Override
    public OWLObject visit(OWLLiteral node) {
        return getReplacementStrategy().replace(node);
    }

    @Override
    public OWLObject visit(OWLFacetRestriction node) {
        return getDataFactory().getOWLFacetRestriction(node.getFacet(), v(node.getFacetValue()));
    }

    @Override
    public OWLObject visit(OWLObjectProperty property) {
        return getReplacementStrategy().replace(property);
    }

    @Override
    public OWLObject visit(OWLObjectInverseOf property) {
        return getDataFactory()
            .getOWLObjectInverseOf(v(property.getInverse()).asOWLObjectProperty());
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
        return getDataFactory().getOWLAnnotation(v(node.getProperty()), v(node.getValue()));
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
        return getDataFactory().getSWRLClassAtom(v(node.getPredicate()), v(node.getArgument()));
    }

    @Override
    public OWLObject visit(SWRLDataRangeAtom node) {
        return getDataFactory().getSWRLDataRangeAtom(v(node.getPredicate()), v(node.getArgument()));
    }

    @Override
    public OWLObject visit(SWRLObjectPropertyAtom node) {
        return getDataFactory().getSWRLObjectPropertyAtom(v(node.getPredicate()),
            v(node.getFirstArgument()), v(node.getSecondArgument()));
    }

    @Override
    public OWLObject visit(SWRLDataPropertyAtom node) {
        return getDataFactory().getSWRLDataPropertyAtom(v(node.getPredicate()),
            v(node.getFirstArgument()), v(node.getSecondArgument()));
    }

    @Override
    public OWLObject visit(SWRLBuiltInAtom node) {
        List<SWRLDArgument> arguments = new ArrayList<>(node.getArguments().size());
        for (SWRLDArgument swrldArgument : node.getArguments()) {
            arguments.add(v(swrldArgument));
        }
        return getDataFactory().getSWRLBuiltInAtom(node.getPredicate(), arguments);
    }

    @Override
    public OWLObject visit(SWRLVariable node) {
        return node;
    }

    @Override
    public OWLObject visit(SWRLIndividualArgument node) {
        return getDataFactory().getSWRLIndividualArgument(v(node.getIndividual()));
    }

    @Override
    public OWLObject visit(SWRLLiteralArgument node) {
        return getDataFactory().getSWRLLiteralArgument(v(node.getLiteral()));
    }

    @Override
    public OWLObject visit(SWRLSameIndividualAtom node) {
        return getDataFactory().getSWRLSameIndividualAtom(v(node.getFirstArgument()),
            v(node.getSecondArgument()));
    }

    @Override
    public OWLObject visit(SWRLDifferentIndividualsAtom node) {
        return getDataFactory().getSWRLDifferentIndividualsAtom(v(node.getFirstArgument()),
            v(node.getSecondArgument()));
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
