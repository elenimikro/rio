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
package org.coode.owl.structural.difference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/** @author eleni */
public class StructuralDifference {
    private final TIntList position = new TIntArrayList();

    /**
     * @param position position
     */
    public StructuralDifference(TIntList position) {
        if (position == null) {
            throw new NullPointerException("The position cannot be null");
        }
        this.position.addAll(position);
    }

    /**
     * 
     */
    public StructuralDifference() {
        this(new TIntArrayList());
    }

    /**
     * Retrieves the position of the top-most difference between the two input object, relative to
     * the first one.
     * 
     * @param anOWLObject input OWL Object.
     * @param anotherOWLObject input OWL Object.
     * @return An instance of StructuralDifferenceReport describing the result of the comparison.
     */
    public StructuralDifferenceReport getTopDifference(OWLObject anOWLObject,
        OWLObject anotherOWLObject) {
        StructuralDifferenceReport toReturn =
            areComparable(anOWLObject, anotherOWLObject) ? StructuralDifferenceReport.NO_DIFFERENCE
                : StructuralDifferenceReport.INCOMPARABLE;
        if (toReturn == StructuralDifferenceReport.NO_DIFFERENCE) {
            StructuralComparison comparison = new StructuralComparison(anOWLObject, position);
            toReturn = anotherOWLObject.accept(comparison);
        }
        return toReturn;
    }

    /**
     * @param anOWLObject anOWLObject
     * @param anotherOWLObject anotherOWLObject
     * @return top differences
     */
    public List<StructuralDifferenceReport> getTopDifferences(OWLObject anOWLObject,
        OWLObject anotherOWLObject) {
        boolean areComparable = areComparable(anOWLObject, anotherOWLObject);
        List<StructuralDifferenceReport> toReturn =
            areComparable ? Collections.<StructuralDifferenceReport>emptyList()
                : new ArrayList<>(Collections.singleton(StructuralDifferenceReport.INCOMPARABLE));
        if (areComparable) {
            CompleteStructuralComparison comparison =
                new CompleteStructuralComparison(anOWLObject, position);
            toReturn = anotherOWLObject.accept(comparison);
        }
        toReturn.remove(StructuralDifferenceReport.NO_DIFFERENCE);
        return toReturn;
    }

    /**
     * @param c c
     * @return top differences
     */
    public Set<List<StructuralDifferenceReport>> getTopDifferences(
        Collection<? extends OWLObject> c) {
        if (c == null) {
            throw new NullPointerException("The collection cannot be null");
        }
        Set<List<StructuralDifferenceReport>> toReturn = new HashSet<>();
        for (OWLObject owlObject : c) {
            for (OWLObject anotherOWLObject : c) {
                if (owlObject != anotherOWLObject) {
                    toReturn.add(this.getTopDifferences(owlObject, anotherOWLObject));
                }
            }
        }
        return toReturn;
    }

    /**
     * Determines if the structural difference makes sense for the pair of input OWLObjects. It
     * returns {@code true} if the input objects are fo the same kind.
     * 
     * @param anOWLObject An input object
     * @param anotherOWLObject Another input object
     * @return {@code true} if the input objects are of the same kind.
     */
    @SuppressWarnings("boxing")
    public boolean areComparable(OWLObject anOWLObject, final OWLObject anotherOWLObject) {
        boolean toReturn = false;
        if (anOWLObject == null) {
            toReturn = anotherOWLObject == null;
        } else if (anotherOWLObject != null) {
            toReturn = anOWLObject.typeIndex() == anotherOWLObject.typeIndex();
        }
        return toReturn;
    }

    /** @return the position */
    public TIntList getPosition() {
        return new TIntArrayList(position);
    }
}
