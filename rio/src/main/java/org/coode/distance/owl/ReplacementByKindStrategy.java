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

import java.io.IOException;
import java.util.Properties;

import org.coode.distance.ReplacementStrategy;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

/** @author eleni */
public class ReplacementByKindStrategy
    implements ReplacementStrategy, OWLObjectVisitorEx<OWLObject> {
    private final Properties properties = new Properties();
    private final OWLDataFactory df;

    /**
     * @param dataFactory dataFactory
     */
    public ReplacementByKindStrategy(OWLDataFactory dataFactory) {
        if (dataFactory == null) {
            throw new NullPointerException("The data factory cannot be null");
        }
        df = dataFactory;
        try {
            properties.load(getClass().getResourceAsStream(
                "/org.coode.distance.owl.ReplacementByKindStrategy.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> OWLObject doDefault(T object) {
        return (OWLObject) object;
    }

    @Override
    public OWLObject visit(OWLClass desc) {
        return df.getOWLClass(properties.getProperty("owlclass"));
    }

    @Override
    public OWLObject visit(OWLObjectProperty property) {
        return df.getOWLObjectProperty(properties.getProperty("owlobjectproperty"));
    }

    @Override
    public OWLObject visit(IRI iri) {
        return IRI.create(properties.getProperty("iri"));
    }

    @Override
    public OWLObject visit(OWLDataProperty property) {
        return df.getOWLDataProperty(properties.getProperty("owldataproperty"));
    }

    @Override
    public OWLObject visit(OWLNamedIndividual individual) {
        return df.getOWLNamedIndividual(properties.getProperty("owlnamedindividual"));
    }

    @Override
    public OWLObject visit(OWLLiteral literal) {
        return df.getOWLLiteral(properties.getProperty("owlliteral"));
    }

    @Override
    public OWLObject visit(OWLAnnotationProperty property) {
        return df.getOWLAnnotationProperty(properties.getProperty("owlannotationproperty"));
    }

    @Override
    public <O extends OWLObject> O replace(O owlObject) {
        return (O) owlObject.accept(this);
    }
}
