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
/**
 * 
 */
package org.coode.distance.owl;

import java.io.IOException;
import java.util.Properties;

import org.coode.distance.ReplacementStrategy;
import org.coode.distance.entityrelevance.RelevancePolicy;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/** @author Luigi Iannone */
public class SingleOWLObjectReplacementByKindStrategy implements ReplacementStrategy {
    private static final String IRI_PLACE_HOLDER = "iriPlaceHolder";
    private final OWLEntity owlEntity;
    private final ReplacementStrategy delegate;
    private final OWLEntity replacement;
    private final OWLDataFactory dataFactory;
    private final RelevancePolicy relevancePolicy;
    private final static Properties properties = new Properties();
    private final ReplacementStrategy defaultStrategy;
    static {
        try {
            properties.load(ReplacementByKindStrategy.class
                    .getResourceAsStream(SingleOWLObjectReplacementByKindStrategy.class
                            .getName() + ".properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SingleOWLObjectReplacementByKindStrategy(OWLEntity owlEntity,
            OWLDataFactory dataFactory, RelevancePolicy relevancePolicy) {
        if (dataFactory == null) {
            throw new NullPointerException("The data factory cannot be null");
        }
        if (owlEntity == null) {
            throw new NullPointerException("The owl Object cannot be null");
        }
        if (relevancePolicy == null) {
            throw new NullPointerException("The relevance policy cannnot be null");
        }
        this.relevancePolicy = relevancePolicy;
        this.owlEntity = owlEntity;
        this.dataFactory = dataFactory;
        defaultStrategy = new ReplacementByKindStrategy(getDataFactory());
        delegate = new ReplacementStrategy() {
            @Override
            public <O extends OWLObject> O replace(O owlObject) {
                return owlObject.accept(Utils.getOWLEntityRecogniser())
                        && SingleOWLObjectReplacementByKindStrategy.this
                                .getRelevancePolicy().isRelevant(owlObject) ? owlObject
                        : defaultStrategy.replace(owlObject);
            }
        };
        replacement = owlEntity.accept(new OWLEntityVisitorEx<OWLEntity>() {
            @Override
            public OWLAnnotationProperty visit(OWLAnnotationProperty property) {
                return SingleOWLObjectReplacementByKindStrategy.this.getDataFactory()
                        .getOWLAnnotationProperty(
                                SingleOWLObjectReplacementByKindStrategy.this
                                        .getPlaceHolderIRI());
            }

            @Override
            public OWLClass visit(OWLClass cls) {
                return SingleOWLObjectReplacementByKindStrategy.this.getDataFactory()
                        .getOWLClass(
                                SingleOWLObjectReplacementByKindStrategy.this
                                        .getPlaceHolderIRI());
            }

            @Override
            public OWLDataProperty visit(OWLDataProperty property) {
                return SingleOWLObjectReplacementByKindStrategy.this.getDataFactory()
                        .getOWLDataProperty(
                                SingleOWLObjectReplacementByKindStrategy.this
                                        .getPlaceHolderIRI());
            }

            @Override
            public OWLObjectProperty visit(OWLObjectProperty property) {
                return SingleOWLObjectReplacementByKindStrategy.this.getDataFactory()
                        .getOWLObjectProperty(
                                SingleOWLObjectReplacementByKindStrategy.this
                                        .getPlaceHolderIRI());
            }

            @Override
            public OWLDatatype visit(OWLDatatype datatype) {
                return SingleOWLObjectReplacementByKindStrategy.this.getDataFactory()
                        .getOWLDatatype(
                                SingleOWLObjectReplacementByKindStrategy.this
                                        .getPlaceHolderIRI());
            }

            @Override
            public OWLNamedIndividual visit(OWLNamedIndividual individual) {
                return SingleOWLObjectReplacementByKindStrategy.this.getDataFactory()
                        .getOWLNamedIndividual(
                                SingleOWLObjectReplacementByKindStrategy.this
                                        .getPlaceHolderIRI());
            }
        });
    }

    protected IRI getPlaceHolderIRI() {
        return IRI.create(properties.getProperty(IRI_PLACE_HOLDER));
    }

    /** @return the owlObject */
    public OWLObject getOWLObject() {
        return owlEntity;
    }

    /** @param <O>
     * @param owlObject
     * @return
     * @see org.coode.distance.owl.ReplacementByKindStrategy#replace(org.semanticweb.owlapi.model.OWLObject) */
    @Override
    @SuppressWarnings("unchecked")
    public <O extends OWLObject> O replace(O owlObject) {
        return (O) (owlObject.equals(owlEntity) ? getReplacement() : delegate
                .replace(owlObject));
    }

    /** @return the replacement */
    public OWLEntity getReplacement() {
        return replacement;
    }

    /** @return the delegate */
    public ReplacementStrategy getDelegate() {
        return delegate;
    }

    /** @return the dataFactory */
    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }

    /** @return the relevancePolicy */
    public RelevancePolicy getRelevancePolicy() {
        return relevancePolicy;
    }
}
