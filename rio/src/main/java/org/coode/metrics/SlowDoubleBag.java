package org.coode.metrics;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** @author eleni */
public class SlowDoubleBag implements Iterable<Double> {
    private static class DoubleIterator implements Iterator<Double> {
        int next = 0;
        SlowDoubleBag d;

        public DoubleIterator(SlowDoubleBag d1) {
            d = d1;
        }

        @Override
        public void remove() {}

        @Override
        public Double next() {
            if (next < d.size) {
                return d.get(next++);
            }
            throw new ArrayIndexOutOfBoundsException(next);
        }

        @Override
        public boolean hasNext() {
            return next < d.size;
        }
    }

    private double[] keys;
    private int[] occurrences;
    protected int size = 0;

    /**
     * @param values values
     * @param uniques uniques
     */
    public SlowDoubleBag(double[] values, Collection<Double> uniques) {
        keys = new double[uniques.size()];
        occurrences = new int[uniques.size()];
        size = values.length;
        int counter = 0;
        for (double d : uniques) {
            keys[counter] = d;
            occurrences[counter] = 0;
            for (double d1 : values) {
                if (d1 == d) {
                    occurrences[counter]++;
                }
            }
            counter++;
        }
    }

    /**
     * @param s s
     * @param uniques uniques
     */
    public SlowDoubleBag(int s, Collection<Double> uniques) {
        keys = new double[uniques.size()];
        occurrences = new int[uniques.size()];
        size = s;
        int counter = 0;
        for (double d : uniques) {
            keys[counter] = d;
            occurrences[counter] = 0;
            counter++;
        }
    }

    /** @return list of unique values */
    public double[] keys() {
        return keys;
    }

    /**
     * @param key key
     * @param toAdd toAdd
     */
    public void addOccurrence(double key, int toAdd) {
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == key) {
                occurrences[i] += toAdd;
                return;
            }
        }
    }

    /**
     * @param key key
     * @param toAdd toAdd
     */
    public void setOccurrence(double key, int toAdd) {
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == key) {
                occurrences[i] = toAdd;
                return;
            }
        }
        throw new RuntimeException("key not found: " + key);
    }

    /**
     * @param key key
     * @return occurrences
     */
    public int occurrences(double key) {
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == key) {
                return occurrences[i];
            }
        }
        return 0;
    }

    /** @return all occurrences */
    public int[] occurrences() {
        return occurrences;
    }

    /** @return total occurrences */
    public int addOccurrences() {
        int toReturn = 0;
        for (int i : occurrences) {
            toReturn += i;
        }
        return toReturn;
    }

    /** @return size */
    public long getSize() {
        return size;
    }

    /**
     * @param position position
     * @return double at position
     */
    public double get(int position) {
        if (position < 0 || position >= size) {
            throw new NoSuchElementException("Position is past last element: " + position);
        }
        int index = 0;
        int current = 0;
        while (current < keys.length) {
            index += occurrences[current];
            if (position < index) {
                return keys[current];
            }
        }
        throw new RuntimeException("could not find element position: " + position);
    }

    @Override
    public Iterator<Double> iterator() {
        return new DoubleIterator(this);
    }
}
