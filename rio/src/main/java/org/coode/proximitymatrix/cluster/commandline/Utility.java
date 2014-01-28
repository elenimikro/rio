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
package org.coode.proximitymatrix.cluster.commandline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.coode.distance.wrapping.DistanceTableObject;
import org.coode.pair.Pair;
import org.coode.pair.SimplePair;
import org.coode.proximitymatrix.ClusteringProximityMatrix;
import org.coode.proximitymatrix.History;
import org.coode.proximitymatrix.HistoryItem;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.MultiMap;

/** @author eleni */
public class Utility {
    /** @param m
     *            m
     * @param i
     *            i */
    public static void printAgglomeration(ClusteringProximityMatrix<?> m, int i) {
        if (i % 50 == 0) {
            System.out.println(String.format("Agglomerations: %d for %d clusters", i, m
                    .getObjects().size()));
        }
    }

    /** @param clusteringMatrix
     *            clusteringMatrix */
    public static void print(
            @SuppressWarnings("unused") ClusteringProximityMatrix<?> clusteringMatrix) {
        // System.out.println(String.format("Next Pair %s %s %f", Utils
        // .render((Collection<DistanceTableObject<OWLEntity>>) clusteringMatrix
        // .getMinimumDistancePair().getFirst()), Utils
        // .render((Collection<DistanceTableObject<OWLEntity>>) clusteringMatrix
        // .getMinimumDistancePair().getSecond()), clusteringMatrix
        // .getMinimumDistance()));
    }

    /** @param clusteringMatrix
     *            clusteringMatrix */
    public static void print2(
            @SuppressWarnings("unused") ClusteringProximityMatrix<?> clusteringMatrix) {
        // System.out
        // .println(String.format(
        // "Next Pair %s %s %f",
        // Utils.render((Collection<DistanceTableObject<OWLEntity>>)
        // clusteringMatrix
        // .getMinimumDistancePair().getFirst()),
        // Utils.render((Collection<DistanceTableObject<OWLEntity>>)
        // (Collection<? extends OWLEntity>) clusteringMatrix
        // .getMinimumDistancePair().getSecond()), clusteringMatrix
        // .getMinimumDistance()));
    }

    /** @param clusteringMatrix
     *            clusteringMatrix */
    public static void print1(
            @SuppressWarnings("unused") ClusteringProximityMatrix<?> clusteringMatrix) {
        // System.out
        // .println(String.format(
        // "Next Pair %s %s %f",
        // Utils.renderManchester((Collection<DistanceTableObject<OWLEntity>>)
        // clusteringMatrix
        // .getMinimumDistancePair().getFirst()),
        // Utils.renderManchester((Collection<DistanceTableObject<OWLEntity>>)
        // clusteringMatrix
        // .getMinimumDistancePair().getSecond()), clusteringMatrix
        // .getMinimumDistance()));
    }

    /** @param history
     *            history
     * @return history */
    public static History<Collection<? extends OWLEntity>> unwrapHistory(
            History<Collection<? extends DistanceTableObject<OWLEntity>>> history) {
        return unwrapHistory(history, new MultiMap<OWLEntity, OWLEntity>());
    }

    /** @param history
     *            history
     * @param equivalenceClass
     *            equivalenceClass
     * @return history */
    public static History<Collection<? extends OWLEntity>> unwrapHistory(
            History<Collection<? extends DistanceTableObject<OWLEntity>>> history,
            MultiMap<OWLEntity, OWLEntity> equivalenceClass) {
        History<Collection<? extends OWLEntity>> toReturn = new History<Collection<? extends OWLEntity>>();
        for (int i = 0; i < history.size(); i++) {
            HistoryItem<Collection<? extends DistanceTableObject<OWLEntity>>> historyItem = history
                    .get(i);
            Pair<Collection<? extends DistanceTableObject<OWLEntity>>> pair = historyItem
                    .getPair();
            Collection<? extends DistanceTableObject<OWLEntity>> first = pair.getFirst();
            Set<OWLEntity> unwrappedFirst = unwrapObjects(first, equivalenceClass);
            Collection<? extends DistanceTableObject<OWLEntity>> second = pair
                    .getSecond();
            Set<OWLEntity> unwrappedSecond = unwrapObjects(second, equivalenceClass);
            Pair<Collection<? extends OWLEntity>> newPair = new SimplePair<Collection<? extends OWLEntity>>(
                    unwrappedFirst, unwrappedSecond);
            Collection<Collection<? extends DistanceTableObject<OWLEntity>>> items = historyItem
                    .getItems();
            Collection<Collection<? extends OWLEntity>> newItems = new ArrayList<Collection<? extends OWLEntity>>(
                    items.size());
            for (Collection<? extends DistanceTableObject<OWLEntity>> collection : items) {
                newItems.add(unwrapObjects(collection, equivalenceClass));
            }
            HistoryItem<Collection<? extends OWLEntity>> newHistoryItem = new HistoryItem<Collection<? extends OWLEntity>>(
                    newPair, newItems);
            toReturn.add(newHistoryItem);
        }
        return toReturn;
    }

    /** @param wrappedObjects
     *            wrappedObjects
     * @param <P>
     *            type
     * @return unwrapped objects */
    public static <P> Set<P> unwrapObjects(
            Collection<? extends DistanceTableObject<P>> wrappedObjects) {
        return unwrapObjects(wrappedObjects, new MultiMap<P, P>());
    }

    /** @param wrappedObjects
     *            wrappedObjects
     * @param equivalenceClass
     *            equivalenceClass
     * @param <P>
     *            type
     * @return unwrapped objects */
    public static <P> Set<P> unwrapObjects(
            Collection<? extends DistanceTableObject<P>> wrappedObjects,
            MultiMap<P, P> equivalenceClass) {
        Set<P> toReturn = new LinkedHashSet<P>();
        for (DistanceTableObject<P> distanceTableObject : wrappedObjects) {
            P object = distanceTableObject.getObject();
            toReturn.add(object);
            toReturn.addAll(equivalenceClass.get(object));
        }
        return toReturn;
    }
}
