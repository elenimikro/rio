/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, The University of Manchester
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2011, University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.coode.utils.owl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.semanticweb.owlapi.model.OWLAxiom;
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
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
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
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
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
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

/**
 * This is an alternative implementation of the utility class
 * {@link org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory}. For every entity that is
 * created, a corresponding declaration axiom is created and added in the ontology. The same applies
 * for the other axioms; they are also added in the ontology. The methods may be ally imported so
 * that OWL API objects can be constructed by writing code that looks like the OWL 2 Functional
 * Syntax. <br>
 * Note that this class is primarily intended for developers who need to write test cases. Normal
 * client code should probably use an {@link org.semanticweb.owlapi.model.OWLDataFactory} for
 * creating objects.
 * 
 * @author Eleni Mikroyannidi, The University of Manchester, Information Management Group
 */
@SuppressWarnings("javadoc")
public class OWLFunctionalSyntaxAxiomFactory {
    private final OWLDataFactory df;
    private final OWLOntology o;

    public OWLFunctionalSyntaxAxiomFactory(OWLOntology ontology) {
        o = ontology;
        df = o.getOWLOntologyManager().getOWLDataFactory();
    }

    public OWLImportsDeclaration ImportsDeclaration(IRI i) {
        return df.getOWLImportsDeclaration(i);
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //
    // // Entities
    // //
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public OWLClass Class(IRI iri) {
        OWLClass c = df.getOWLClass(iri);
        OWLDeclarationAxiom axiom = df.getOWLDeclarationAxiom(c);
        o.getOWLOntologyManager().addAxiom(o, axiom);
        return c;
    }

    public OWLClass Class(String abbreviatedIRI, PrefixManager pm) {
        OWLClass c = df.getOWLClass(abbreviatedIRI, pm);
        OWLDeclarationAxiom axiom = df.getOWLDeclarationAxiom(c);
        o.getOWLOntologyManager().addAxiom(o, axiom);
        return c;
    }

    public OWLAnnotationProperty RDFSComment() {
        OWLAnnotationProperty rdfsComment = df.getRDFSComment();
        OWLDeclarationAxiom axiom = df.getOWLDeclarationAxiom(rdfsComment);
        o.getOWLOntologyManager().addAxiom(o, axiom);
        return rdfsComment;
    }

    public OWLAnnotationProperty RDFSLabel() {
        OWLAnnotationProperty rdfsLabel = df.getRDFSLabel();
        OWLDeclarationAxiom axiom = df.getOWLDeclarationAxiom(rdfsLabel);
        o.getOWLOntologyManager().addAxiom(o, axiom);
        return rdfsLabel;
    }

    public OWLDatatype TopDatatype() {
        return df.getTopDatatype();
    }

    public OWLClass OWLThing() {
        return df.getOWLThing();
    }

    public OWLDatatype Integer() {
        return df.getIntegerOWLDatatype();
    }

    public OWLDatatype Double() {
        return df.getDoubleOWLDatatype();
    }

    public OWLDatatype Float() {
        return df.getFloatOWLDatatype();
    }

    public OWLDatatype Boolean() {
        return df.getBooleanOWLDatatype();
    }

    public OWLClass OWLNothing() {
        return df.getOWLNothing();
    }

    public OWLObjectProperty ObjectProperty(IRI iri) {
        OWLObjectProperty owlObjectProperty = df.getOWLObjectProperty(iri);
        OWLDeclarationAxiom axiom = df.getOWLDeclarationAxiom(owlObjectProperty);
        o.getOWLOntologyManager().addAxiom(o, axiom);
        return owlObjectProperty;
    }

    public OWLObjectProperty ObjectProperty(String abbreviatedIRI, PrefixManager pm) {
        return df.getOWLObjectProperty(abbreviatedIRI, pm);
    }

    public OWLObjectInverseOf ObjectInverseOf(OWLObjectProperty pe) {
        return df.getOWLObjectInverseOf(pe);
    }

    public OWLDataProperty DataProperty(IRI iri) {
        return df.getOWLDataProperty(iri);
    }

    public OWLDataProperty DataProperty(String abbreviatedIRI, PrefixManager pm) {
        return df.getOWLDataProperty(abbreviatedIRI, pm);
    }

    public OWLAnnotationProperty AnnotationProperty(IRI iri) {
        return df.getOWLAnnotationProperty(iri);
    }

    public OWLAnnotationProperty AnnotationProperty(String abbreviatedIRI, PrefixManager pm) {
        return df.getOWLAnnotationProperty(abbreviatedIRI, pm);
    }

    public OWLNamedIndividual NamedIndividual(IRI iri) {
        return df.getOWLNamedIndividual(iri);
    }

    public OWLAnonymousIndividual AnonymousIndividual() {
        return df.getOWLAnonymousIndividual();
    }

    public OWLAnonymousIndividual AnonymousIndividual(String id) {
        return df.getOWLAnonymousIndividual(id);
    }

    public OWLNamedIndividual NamedIndividual(String abbreviatedIRI, PrefixManager pm) {
        return df.getOWLNamedIndividual(abbreviatedIRI, pm);
    }

    public OWLDatatype Datatype(IRI iri) {
        return df.getOWLDatatype(iri);
    }

    public OWLDeclarationAxiom Declaration(OWLEntity entity) {
        return df.getOWLDeclarationAxiom(entity);
    }

    public OWLDeclarationAxiom Declaration(OWLEntity entity, Set<OWLAnnotation> a) {
        return df.getOWLDeclarationAxiom(entity, a);
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //
    // // Class Expressions
    // //
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public OWLObjectIntersectionOf ObjectIntersectionOf(OWLClassExpression... classExpressions) {
        return df.getOWLObjectIntersectionOf(classExpressions);
    }

    public OWLObjectUnionOf ObjectUnionOf(OWLClassExpression... classExpressions) {
        return df.getOWLObjectUnionOf(classExpressions);
    }

    public OWLObjectComplementOf ObjectComplementOf(OWLClassExpression classExpression) {
        return df.getOWLObjectComplementOf(classExpression);
    }

    public OWLObjectSomeValuesFrom ObjectSomeValuesFrom(OWLObjectPropertyExpression pe,
        OWLClassExpression ce) {
        return df.getOWLObjectSomeValuesFrom(pe, ce);
    }

    public OWLObjectAllValuesFrom ObjectAllValuesFrom(OWLObjectPropertyExpression pe,
        OWLClassExpression ce) {
        return df.getOWLObjectAllValuesFrom(pe, ce);
    }

    public OWLObjectHasValue ObjectHasValue(OWLObjectPropertyExpression pe,
        OWLIndividual individual) {
        return df.getOWLObjectHasValue(pe, individual);
    }

    public OWLObjectMinCardinality ObjectMinCardinality(int cardinality,
        OWLObjectPropertyExpression pe, OWLClassExpression ce) {
        return df.getOWLObjectMinCardinality(cardinality, pe, ce);
    }

    public OWLObjectMaxCardinality ObjectMaxCardinality(int cardinality,
        OWLObjectPropertyExpression pe, OWLClassExpression ce) {
        return df.getOWLObjectMaxCardinality(cardinality, pe, ce);
    }

    public OWLObjectExactCardinality ObjectExactCardinality(int cardinality,
        OWLObjectPropertyExpression pe, OWLClassExpression ce) {
        return df.getOWLObjectExactCardinality(cardinality, pe, ce);
    }

    public OWLObjectHasSelf ObjectHasSelf(OWLObjectPropertyExpression pe) {
        return df.getOWLObjectHasSelf(pe);
    }

    public OWLObjectOneOf ObjectOneOf(OWLIndividual... individuals) {
        return df.getOWLObjectOneOf(individuals);
    }

    public OWLDataSomeValuesFrom DataSomeValuesFrom(OWLDataPropertyExpression pe, OWLDataRange dr) {
        return df.getOWLDataSomeValuesFrom(pe, dr);
    }

    public OWLDataAllValuesFrom DataAllValuesFrom(OWLDataPropertyExpression pe, OWLDataRange dr) {
        return df.getOWLDataAllValuesFrom(pe, dr);
    }

    public OWLDataHasValue DataHasValue(OWLDataPropertyExpression pe, OWLLiteral literal) {
        return df.getOWLDataHasValue(pe, literal);
    }

    public OWLDataMinCardinality DataMinCardinality(int cardinality, OWLDataPropertyExpression pe,
        OWLDataRange dr) {
        return df.getOWLDataMinCardinality(cardinality, pe, dr);
    }

    public OWLDataMaxCardinality DataMaxCardinality(int cardinality, OWLDataPropertyExpression pe,
        OWLDataRange dr) {
        return df.getOWLDataMaxCardinality(cardinality, pe, dr);
    }

    public OWLDataExactCardinality DataExactCardinality(int cardinality,
        OWLDataPropertyExpression pe, OWLDataRange dr) {
        return df.getOWLDataExactCardinality(cardinality, pe, dr);
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //
    // // Data Ranges other than datatype
    // //
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public OWLDataIntersectionOf DataIntersectionOf(OWLDataRange... dataRanges) {
        return df.getOWLDataIntersectionOf(dataRanges);
    }

    public OWLDataUnionOf DataUnionOf(OWLDataRange... dataRanges) {
        return df.getOWLDataUnionOf(dataRanges);
    }

    public OWLDataComplementOf DataComplementOf(OWLDataRange dataRange) {
        return df.getOWLDataComplementOf(dataRange);
    }

    public OWLDataOneOf DataOneOf(OWLLiteral... literals) {
        return df.getOWLDataOneOf(literals);
    }

    public OWLDatatypeRestriction DatatypeRestriction(OWLDatatype datatype,
        OWLFacetRestriction... facetRestrictions) {
        return df.getOWLDatatypeRestriction(datatype, facetRestrictions);
    }

    public OWLFacetRestriction FacetRestriction(OWLFacet facet, OWLLiteral facetValue) {
        return df.getOWLFacetRestriction(facet, facetValue);
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //
    // // Axioms
    // //
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public OWLSubClassOfAxiom SubClassOf(OWLClassExpression subClass,
        OWLClassExpression superClass) {
        return df.getOWLSubClassOfAxiom(subClass, superClass);
    }

    public OWLSubClassOfAxiom SubClassOf(OWLClassExpression subClass, OWLClassExpression superClass,
        Set<OWLAnnotation> a) {
        return df.getOWLSubClassOfAxiom(subClass, superClass, a);
    }

    public OWLEquivalentClassesAxiom EquivalentClasses(OWLClassExpression... classExpressions) {
        return df.getOWLEquivalentClassesAxiom(classExpressions);
    }

    public OWLEquivalentClassesAxiom EquivalentClasses(Set<OWLAnnotation> a,
        OWLClassExpression... classExpressions) {
        return df.getOWLEquivalentClassesAxiom(
            new HashSet<OWLClassExpression>(Arrays.asList(classExpressions)), a);
    }

    public OWLDisjointClassesAxiom DisjointClasses(OWLClassExpression... classExpressions) {
        return df.getOWLDisjointClassesAxiom(classExpressions);
    }

    public OWLDisjointClassesAxiom DisjointClasses(
        Set<? extends OWLClassExpression> classExpressions) {
        return df.getOWLDisjointClassesAxiom(classExpressions);
    }

    public OWLDisjointClassesAxiom DisjointClasses(Set<OWLClassExpression> classExpressions,
        Set<OWLAnnotation> a) {
        return df.getOWLDisjointClassesAxiom(classExpressions, a);
    }

    public OWLDisjointUnionAxiom DisjointUnion(OWLClass cls,
        OWLClassExpression... classExpressions) {
        return df.getOWLDisjointUnionAxiom(cls, CollectionFactory.createSet(classExpressions));
    }

    public OWLDisjointClassesAxiom DisjointClasses(Set<OWLAnnotation> a,
        OWLClassExpression... classExpressions) {
        return df.getOWLDisjointClassesAxiom(
            new HashSet<OWLClassExpression>(Arrays.asList(classExpressions)), a);
    }

    public OWLSubObjectPropertyOfAxiom SubObjectPropertyOf(OWLObjectPropertyExpression subProperty,
        OWLObjectPropertyExpression superProperty) {
        return df.getOWLSubObjectPropertyOfAxiom(subProperty, superProperty);
    }

    public OWLSubPropertyChainOfAxiom SubPropertyChainOf(
        List<? extends OWLObjectPropertyExpression> chain,
        OWLObjectPropertyExpression superProperty) {
        return df.getOWLSubPropertyChainOfAxiom(chain, superProperty);
    }

    public OWLSubPropertyChainOfAxiom SubPropertyChainOf(
        List<? extends OWLObjectPropertyExpression> chain,
        OWLObjectPropertyExpression superProperty, Set<OWLAnnotation> a) {
        return df.getOWLSubPropertyChainOfAxiom(chain, superProperty, a);
    }

    public OWLSubObjectPropertyOfAxiom SubObjectPropertyOf(OWLObjectPropertyExpression subProperty,
        OWLObjectPropertyExpression superProperty, Set<OWLAnnotation> a) {
        return df.getOWLSubObjectPropertyOfAxiom(subProperty, superProperty, a);
    }

    public OWLEquivalentObjectPropertiesAxiom EquivalentObjectProperties(
        OWLObjectPropertyExpression... properties) {
        return df.getOWLEquivalentObjectPropertiesAxiom(properties);
    }

    public OWLEquivalentObjectPropertiesAxiom EquivalentObjectProperties(Set<OWLAnnotation> a,
        OWLObjectPropertyExpression... properties) {
        return df.getOWLEquivalentObjectPropertiesAxiom(
            new HashSet<OWLObjectPropertyExpression>(Arrays.asList(properties)), a);
    }

    public OWLDisjointObjectPropertiesAxiom DisjointObjectProperties(
        OWLObjectPropertyExpression... properties) {
        return df.getOWLDisjointObjectPropertiesAxiom(properties);
    }

    public OWLDisjointObjectPropertiesAxiom DisjointObjectProperties(Set<OWLAnnotation> a,
        OWLObjectPropertyExpression... properties) {
        return df.getOWLDisjointObjectPropertiesAxiom(
            new HashSet<OWLObjectPropertyExpression>(Arrays.asList(properties)), a);
    }

    public OWLInverseObjectPropertiesAxiom InverseObjectProperties(OWLObjectPropertyExpression peA,
        OWLObjectPropertyExpression peB) {
        return df.getOWLInverseObjectPropertiesAxiom(peA, peB);
    }

    public OWLObjectPropertyDomainAxiom ObjectPropertyDomain(OWLObjectPropertyExpression property,
        OWLClassExpression domain) {
        return df.getOWLObjectPropertyDomainAxiom(property, domain);
    }

    public OWLObjectPropertyDomainAxiom ObjectPropertyDomain(OWLObjectPropertyExpression property,
        OWLClassExpression domain, Set<OWLAnnotation> a) {
        return df.getOWLObjectPropertyDomainAxiom(property, domain, a);
    }

    public OWLObjectPropertyRangeAxiom ObjectPropertyRange(OWLObjectPropertyExpression property,
        OWLClassExpression range) {
        return df.getOWLObjectPropertyRangeAxiom(property, range);
    }

    public OWLObjectPropertyRangeAxiom ObjectPropertyRange(OWLObjectPropertyExpression property,
        OWLClassExpression range, Set<OWLAnnotation> a) {
        return df.getOWLObjectPropertyRangeAxiom(property, range, a);
    }

    public OWLFunctionalObjectPropertyAxiom FunctionalObjectProperty(
        OWLObjectPropertyExpression property) {
        return df.getOWLFunctionalObjectPropertyAxiom(property);
    }

    public OWLFunctionalObjectPropertyAxiom FunctionalObjectProperty(
        OWLObjectPropertyExpression property, Set<OWLAnnotation> a) {
        return df.getOWLFunctionalObjectPropertyAxiom(property, a);
    }

    public OWLInverseFunctionalObjectPropertyAxiom InverseFunctionalObjectProperty(
        OWLObjectPropertyExpression property) {
        return df.getOWLInverseFunctionalObjectPropertyAxiom(property);
    }

    public OWLInverseFunctionalObjectPropertyAxiom InverseFunctionalObjectProperty(
        OWLObjectPropertyExpression property, Set<OWLAnnotation> a) {
        return df.getOWLInverseFunctionalObjectPropertyAxiom(property, a);
    }

    public OWLReflexiveObjectPropertyAxiom ReflexiveObjectProperty(
        OWLObjectPropertyExpression property) {
        return df.getOWLReflexiveObjectPropertyAxiom(property);
    }

    public OWLReflexiveObjectPropertyAxiom ReflexiveObjectProperty(
        OWLObjectPropertyExpression property, Set<OWLAnnotation> a) {
        return df.getOWLReflexiveObjectPropertyAxiom(property, a);
    }

    public OWLIrreflexiveObjectPropertyAxiom IrreflexiveObjectProperty(
        OWLObjectPropertyExpression property) {
        return df.getOWLIrreflexiveObjectPropertyAxiom(property);
    }

    public OWLIrreflexiveObjectPropertyAxiom IrreflexiveObjectProperty(
        OWLObjectPropertyExpression property, Set<OWLAnnotation> a) {
        return df.getOWLIrreflexiveObjectPropertyAxiom(property, a);
    }

    public OWLSymmetricObjectPropertyAxiom SymmetricObjectProperty(
        OWLObjectPropertyExpression property) {
        return df.getOWLSymmetricObjectPropertyAxiom(property);
    }

    public OWLSymmetricObjectPropertyAxiom SymmetricObjectProperty(
        OWLObjectPropertyExpression property, Set<OWLAnnotation> a) {
        return df.getOWLSymmetricObjectPropertyAxiom(property, a);
    }

    public OWLAsymmetricObjectPropertyAxiom AsymmetricObjectProperty(
        OWLObjectPropertyExpression property) {
        return df.getOWLAsymmetricObjectPropertyAxiom(property);
    }

    public OWLAsymmetricObjectPropertyAxiom AsymmetricObjectProperty(
        OWLObjectPropertyExpression property, Set<OWLAnnotation> a) {
        return df.getOWLAsymmetricObjectPropertyAxiom(property, a);
    }

    public OWLTransitiveObjectPropertyAxiom TransitiveObjectProperty(
        OWLObjectPropertyExpression property) {
        return df.getOWLTransitiveObjectPropertyAxiom(property);
    }

    public OWLTransitiveObjectPropertyAxiom TransitiveObjectProperty(
        OWLObjectPropertyExpression property, Set<OWLAnnotation> a) {
        return df.getOWLTransitiveObjectPropertyAxiom(property, a);
    }

    public OWLSubDataPropertyOfAxiom SubDataPropertyOf(OWLDataPropertyExpression subProperty,
        OWLDataPropertyExpression superProperty) {
        return df.getOWLSubDataPropertyOfAxiom(subProperty, superProperty);
    }

    public OWLSubDataPropertyOfAxiom SubDataPropertyOf(OWLDataPropertyExpression subProperty,
        OWLDataPropertyExpression superProperty, Set<OWLAnnotation> a) {
        return df.getOWLSubDataPropertyOfAxiom(subProperty, superProperty, a);
    }

    public OWLEquivalentDataPropertiesAxiom EquivalentDataProperties(
        OWLDataPropertyExpression... properties) {
        return df.getOWLEquivalentDataPropertiesAxiom(properties);
    }

    public OWLEquivalentDataPropertiesAxiom EquivalentDataProperties(Set<OWLAnnotation> a,
        OWLDataPropertyExpression... properties) {
        return df.getOWLEquivalentDataPropertiesAxiom(
            new HashSet<OWLDataPropertyExpression>(Arrays.asList(properties)), a);
    }

    public OWLDisjointDataPropertiesAxiom DisjointDataProperties(
        OWLDataPropertyExpression... properties) {
        return df.getOWLDisjointDataPropertiesAxiom(properties);
    }

    public OWLDisjointDataPropertiesAxiom DisjointDataProperties(Set<OWLAnnotation> a,
        OWLDataPropertyExpression... properties) {
        return df.getOWLDisjointDataPropertiesAxiom(
            new HashSet<OWLDataPropertyExpression>(Arrays.asList(properties)), a);
    }

    public OWLDataPropertyDomainAxiom DataPropertyDomain(OWLDataPropertyExpression property,
        OWLClassExpression domain) {
        return df.getOWLDataPropertyDomainAxiom(property, domain);
    }

    public OWLDataPropertyDomainAxiom DataPropertyDomain(OWLDataPropertyExpression property,
        OWLClassExpression domain, Set<OWLAnnotation> a) {
        return df.getOWLDataPropertyDomainAxiom(property, domain, a);
    }

    public OWLDataPropertyRangeAxiom DataPropertyRange(OWLDataPropertyExpression property,
        OWLDataRange range) {
        return df.getOWLDataPropertyRangeAxiom(property, range);
    }

    public OWLDataPropertyRangeAxiom DataPropertyRange(OWLDataPropertyExpression property,
        OWLDataRange range, Set<OWLAnnotation> a) {
        return df.getOWLDataPropertyRangeAxiom(property, range, a);
    }

    public OWLFunctionalDataPropertyAxiom FunctionalDataProperty(
        OWLDataPropertyExpression property) {
        return df.getOWLFunctionalDataPropertyAxiom(property);
    }

    public OWLFunctionalDataPropertyAxiom FunctionalDataProperty(OWLDataPropertyExpression property,
        Set<OWLAnnotation> a) {
        return df.getOWLFunctionalDataPropertyAxiom(property, a);
    }

    public OWLDatatypeDefinitionAxiom DatatypeDefinition(OWLDatatype datatype,
        OWLDataRange dataRange) {
        return df.getOWLDatatypeDefinitionAxiom(datatype, dataRange);
    }

    public OWLHasKeyAxiom HasKey(OWLClassExpression classExpression,
        OWLPropertyExpression... propertyExpressions) {
        return df.getOWLHasKeyAxiom(classExpression, propertyExpressions);
    }

    public OWLHasKeyAxiom HasKey(Set<OWLAnnotation> a, OWLClassExpression classExpression,
        OWLPropertyExpression... propertyExpressions) {
        return df.getOWLHasKeyAxiom(classExpression,
            new HashSet<OWLPropertyExpression>(Arrays.asList(propertyExpressions)), a);
    }

    public OWLSameIndividualAxiom SameIndividual(OWLIndividual... individuals) {
        return df.getOWLSameIndividualAxiom(individuals);
    }

    public OWLDifferentIndividualsAxiom DifferentIndividuals(OWLIndividual... individuals) {
        return df.getOWLDifferentIndividualsAxiom(individuals);
    }

    public OWLClassAssertionAxiom ClassAssertion(OWLClassExpression ce, OWLIndividual ind,
        Set<OWLAnnotation> a) {
        return df.getOWLClassAssertionAxiom(ce, ind, a);
    }

    public OWLClassAssertionAxiom ClassAssertion(OWLClassExpression ce, OWLIndividual ind) {
        return df.getOWLClassAssertionAxiom(ce, ind);
    }

    public OWLObjectPropertyAssertionAxiom ObjectPropertyAssertion(
        OWLObjectPropertyExpression property, OWLIndividual source, OWLIndividual target) {
        return df.getOWLObjectPropertyAssertionAxiom(property, source, target);
    }

    public OWLObjectPropertyAssertionAxiom ObjectPropertyAssertion(
        OWLObjectPropertyExpression property, OWLIndividual source, OWLIndividual target,
        Set<OWLAnnotation> a) {
        return df.getOWLObjectPropertyAssertionAxiom(property, source, target, a);
    }

    public OWLNegativeObjectPropertyAssertionAxiom NegativeObjectPropertyAssertion(
        OWLObjectPropertyExpression property, OWLIndividual source, OWLIndividual target) {
        return df.getOWLNegativeObjectPropertyAssertionAxiom(property, source, target);
    }

    public OWLNegativeObjectPropertyAssertionAxiom NegativeObjectPropertyAssertion(
        OWLObjectPropertyExpression property, OWLIndividual source, OWLIndividual target,
        Set<OWLAnnotation> a) {
        return df.getOWLNegativeObjectPropertyAssertionAxiom(property, source, target, a);
    }

    public OWLDataPropertyAssertionAxiom DataPropertyAssertion(OWLDataPropertyExpression property,
        OWLIndividual source, OWLLiteral target) {
        return df.getOWLDataPropertyAssertionAxiom(property, source, target);
    }

    public OWLDataPropertyAssertionAxiom DataPropertyAssertion(OWLDataPropertyExpression property,
        OWLIndividual source, OWLLiteral target, Set<OWLAnnotation> a) {
        return df.getOWLDataPropertyAssertionAxiom(property, source, target, a);
    }

    public OWLNegativeDataPropertyAssertionAxiom NegativeDataPropertyAssertion(
        OWLDataPropertyExpression property, OWLIndividual source, OWLLiteral target) {
        return df.getOWLNegativeDataPropertyAssertionAxiom(property, source, target);
    }

    public OWLNegativeDataPropertyAssertionAxiom NegativeDataPropertyAssertion(
        OWLDataPropertyExpression property, OWLIndividual source, OWLLiteral target,
        Set<OWLAnnotation> a) {
        return df.getOWLNegativeDataPropertyAssertionAxiom(property, source, target, a);
    }

    public OWLAnnotationAssertionAxiom AnnotationAssertion(OWLAnnotationProperty property,
        OWLAnnotationSubject subject, OWLAnnotationValue value) {
        return df.getOWLAnnotationAssertionAxiom(property, subject, value);
    }

    public OWLAnnotation Annotation(OWLAnnotationProperty property, OWLAnnotationValue value) {
        return df.getOWLAnnotation(property, value);
    }

    public OWLSubAnnotationPropertyOfAxiom SubAnnotationPropertyOf(
        OWLAnnotationProperty subProperty, OWLAnnotationProperty superProperty) {
        return df.getOWLSubAnnotationPropertyOfAxiom(subProperty, superProperty);
    }

    public OWLAnnotationPropertyDomainAxiom AnnotationPropertyDomain(OWLAnnotationProperty property,
        IRI iri) {
        return df.getOWLAnnotationPropertyDomainAxiom(property, iri);
    }

    public OWLAnnotationPropertyRangeAxiom AnnotationPropertyRange(OWLAnnotationProperty property,
        IRI iri) {
        return df.getOWLAnnotationPropertyRangeAxiom(property, iri);
    }

    public OWLAnnotationPropertyDomainAxiom AnnotationPropertyDomain(OWLAnnotationProperty property,
        String iri) {
        return df.getOWLAnnotationPropertyDomainAxiom(property, IRI(iri));
    }

    public OWLAnnotationPropertyRangeAxiom AnnotationPropertyRange(OWLAnnotationProperty property,
        String iri) {
        return df.getOWLAnnotationPropertyRangeAxiom(property, IRI(iri));
    }

    public IRI IRI(String iri) {
        return IRI.create(iri);
    }

    public OWLLiteral PlainLiteral(String literal) {
        return df.getOWLLiteral(literal, "");
    }

    public OWLDatatype PlainLiteral() {
        return df.getRDFPlainLiteral();
    }

    public OWLLiteral Literal(String literal, String lang) {
        return df.getOWLLiteral(literal, lang);
    }

    public OWLLiteral Literal(String literal, OWLDatatype type) {
        return df.getOWLLiteral(literal, type);
    }

    public OWLLiteral Literal(String literal, OWL2Datatype type) {
        return df.getOWLLiteral(literal, type);
    }

    public OWLLiteral Literal(String literal) {
        return df.getOWLLiteral(literal);
    }

    public OWLLiteral Literal(boolean literal) {
        return df.getOWLLiteral(literal);
    }

    public OWLLiteral Literal(int literal) {
        return df.getOWLLiteral(literal);
    }

    public OWLLiteral Literal(double literal) {
        return df.getOWLLiteral(literal);
    }

    public OWLLiteral Literal(float literal) {
        return df.getOWLLiteral(literal);
    }

    public OWLOntology Ontology(OWLOntologyManager man, OWLAxiom... axioms)
        throws OWLOntologyCreationException {
        return man.createOntology(CollectionFactory.createSet(axioms));
    }
}
