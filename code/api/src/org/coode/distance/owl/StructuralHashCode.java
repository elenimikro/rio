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

import org.coode.distance.ReplacementStrategy;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;

/** Hashing function that takes into account only the structure of an OWL Object.
 * OWL entities of the same kind will have the same hash code. For instance
 * <code>this.hasCode(owl:Thing) == this.hashCode(owl:Nothing)</code>
 * 
 * @author Luigi Iannone */
public class StructuralHashCode implements HashCode {
    private final OWLEntityReplacer replacer;
    private final OWLDataFactory dataFactory;
    private final ReplacementStrategy replacementStrategy;

    /** @param dataFactory
     *            dataFactory
     * @param replacementStrategy
     *            replacementStrategy */
    public StructuralHashCode(OWLDataFactory dataFactory,
            ReplacementStrategy replacementStrategy) {
        if (dataFactory == null) {
            throw new NullPointerException("The OWL data factory cannot be null");
        }
        if (replacementStrategy == null) {
            throw new NullPointerException("The replacement strategy cannot be null");
        }
        this.dataFactory = dataFactory;
        this.replacementStrategy = replacementStrategy;
        replacer = new OWLEntityReplacer(getDataFactory(), getReplacementStrategy());
    }

    /*
     * Computes the hash code of the object only by looking at its structure.
     * Can only distinguish between different kinds of OWL entities, nut not
     * between entities of the same kind.
     */
    @Override
    public int hashCode(OWLObject owlObject) {
        OWLObject replacedOWLObject = owlObject.accept(replacer);
        return replacedOWLObject.hashCode();
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
