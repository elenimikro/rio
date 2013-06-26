package org.coode.metrics;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SlowDoubleBag implements Iterable<Double> {
    private static class DoubleIterator implements Iterator<Double> {
        int next = 0;
        SlowDoubleBag d;

        public DoubleIterator(SlowDoubleBag d1) {
            d = d1;
        }

        @Override
        public void remove() {
            // TODO Auto-generated method stub
        }

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
    private int size = 0;
    private int recordSize = 0;

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

    public void addOccurrence(double key, int toAdd) {
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == key) {
                occurrences[i] += toAdd;
                return;
            }
        }
    }

    public void setOccurrence(double key, int toAdd) {
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == key) {
                occurrences[i] = toAdd;
                return;
            }
        }
        throw new RuntimeException("key not found: " + key);
    }

    public int occurrences(double key) {
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == key) {
                return occurrences[i];
            }
        }
        return 0;
    }

    public int[] occurrences() {
        return occurrences;
    }

    public int addOccurrences() {
        int toReturn = 0;
        for (int i : occurrences) {
            toReturn += i;
        }
        return toReturn;
    }

    public long getSize() {
        return size;
    }

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
