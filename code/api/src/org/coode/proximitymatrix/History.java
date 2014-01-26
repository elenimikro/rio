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
package org.coode.proximitymatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/** @author eleni
 * @param <O>
 *            type */
public class History<O> implements Cloneable {
    private final List<HistoryItem<O>> list = new ArrayList<HistoryItem<O>>();

    /** default constructor */
    public History() {
        this(Collections.<HistoryItem<O>> emptyList());
    }

    /** @param c
     *            c */
    public History(Collection<? extends HistoryItem<O>> c) {
        if (c == null) {
            throw new NullPointerException("The input collection cannot be null");
        }
        this.list.addAll(c);
    }

    @Override
    public History<O> clone() throws CloneNotSupportedException {
        Set<HistoryItem<O>> newItems = new LinkedHashSet<HistoryItem<O>>();
        for (HistoryItem<O> historyItem : this.list) {
            newItems.add(historyItem);
        }
        return new History<O>(newItems);
    }

    /** @param e
     *            e
     * @return true if addition
     * @see java.util.List#add(java.lang.Object) */
    public boolean add(HistoryItem<O> e) {
        return this.list.add(e);
    }

    /** @param c
     *            c
     * @return true if added
     * @see java.util.List#addAll(java.util.Collection) */
    public boolean addAll(Collection<? extends HistoryItem<O>> c) {
        return this.list.addAll(c);
    }

    /** @see java.util.List#clear() */
    public void clear() {
        this.list.clear();
    }

    /** @param o
     *            o
     * @return true if contains
     * @see java.util.List#contains(java.lang.Object) */
    public boolean contains(Object o) {
        return this.list.contains(o);
    }

    /** @param c
     *            c
     * @return true if contains all
     * @see java.util.List#containsAll(java.util.Collection) */
    public boolean containsAll(Collection<?> c) {
        return this.list.containsAll(c);
    }

    @Override
    public boolean equals(Object o) {
        return this.list.equals(o);
    }

    /** @param index
     *            index
     * @return element at position i
     * @see java.util.List#get(int) */
    public HistoryItem<O> get(int index) {
        return this.list.get(index);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }

    /** @param o
     *            o
     * @return index
     * @see java.util.List#indexOf(java.lang.Object) */
    public int indexOf(Object o) {
        return this.list.indexOf(o);
    }

    /** @return true if empty
     * @see java.util.List#isEmpty() */
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    /** @return iterator
     * @see java.util.List#iterator() */
    public Iterator<HistoryItem<O>> iterator() {
        return this.list.iterator();
    }

    /** @param o
     *            o
     * @return last index of
     * @see java.util.List#lastIndexOf(java.lang.Object) */
    public int lastIndexOf(Object o) {
        return this.list.lastIndexOf(o);
    }

    /** @return list iterator
     * @see java.util.List#listIterator() */
    public ListIterator<HistoryItem<O>> listIterator() {
        return this.list.listIterator();
    }

    /** @param index
     *            index
     * @return iterator
     * @see java.util.List#listIterator(int) */
    public ListIterator<HistoryItem<O>> listIterator(int index) {
        return this.list.listIterator(index);
    }

    /** @param index
     *            index
     * @return element removed
     * @see java.util.List#remove(int) */
    public HistoryItem<O> remove(int index) {
        return this.list.remove(index);
    }

    /** @param o
     *            o
     * @return true if removed
     * @see java.util.List#remove(java.lang.Object) */
    public boolean remove(Object o) {
        return this.list.remove(o);
    }

    /** @param c
     *            c
     * @return true if all removed
     * @see java.util.List#removeAll(java.util.Collection) */
    public boolean removeAll(Collection<?> c) {
        return this.list.removeAll(c);
    }

    /** @param c
     *            c
     * @return true if retained all
     * @see java.util.List#retainAll(java.util.Collection) */
    public boolean retainAll(Collection<?> c) {
        return this.list.retainAll(c);
    }

    /** @param index
     *            index
     * @param element
     *            element
     * @return removed value
     * @see java.util.List#set(int, java.lang.Object) */
    public HistoryItem<O> set(int index, HistoryItem<O> element) {
        return this.list.set(index, element);
    }

    /** @return size
     * @see java.util.List#size() */
    public int size() {
        return this.list.size();
    }

    /** @param fromIndex
     *            fromIndex
     * @param toIndex
     *            toIndex
     * @return sublist
     * @see java.util.List#subList(int, int) */
    public List<HistoryItem<O>> subList(int fromIndex, int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }

    /** @return as array
     * @see java.util.List#toArray() */
    public Object[] toArray() {
        return this.list.toArray();
    }

    /** @param <T>
     *            type
     * @param a
     *            a
     * @return as array
     * @see java.util.List#toArray(T[]) */
    public <T> T[] toArray(T[] a) {
        return this.list.toArray(a);
    }
}
