package org.coode.distance;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** @author eleni */
public class SparseMatrixImpl implements SparseMatrix {
    Map<Record, Record> matrix = new HashMap<>();
    private final Map<Object, Integer> objectIndex = new HashMap<>();
    private final Record key = new Record();
    private final int size;

    /**
     * @param size size
     */
    public SparseMatrixImpl(int size) {
        this.size = size;
    }

    /**
     * @param m m
     */
    public SparseMatrixImpl(SparseMatrixImpl m) {
        size = m.length();
        for (Record r : m.matrix.keySet()) {
            Record copy = r.copy();
            matrix.put(copy, copy);
        }
    }

    /**
     * @param i i
     * @return row
     */
    public double[] getRow(int i) {
        double[] toReturn = new double[size];
        Arrays.fill(toReturn, 1D);
        for (Record r : matrix.values()) {
            if (r.i == i) {
                toReturn[r.j] = r.value;
            }
        }
        return toReturn;
    }

    @Override
    public int length() {
        return size;
    }

    @Override
    public double get(int _i, int _j) {
        if (_i < size && _j < size) {
            if (_i == _j) {
                return 0D;
            }
            int i = _i < _j ? _i : _j;
            int j = _i < _j ? _j : _i;
            key.i = i;// Math.min(i, j);
            key.j = j;// Math.max(i, j);
            Record value = matrix.get(key);
            if (value != null) {
                return value.value;
            }
            return 1D;
        }
        throw new IllegalArgumentException(
            "Table of size " + size + " does not contain (" + _i + "," + _j + ")");
    }

    @Override
    public double get(Object i, Object j) {
        Integer rowIndex = objectIndex.get(i);
        if (rowIndex == null) {
            throw new IllegalArgumentException(
                String.format("%s is not contained in this table based distance", i));
        }
        Integer columnIndex = objectIndex.get(j);
        if (columnIndex == null) {
            throw new IllegalArgumentException(
                String.format("%s is not contained in this table based distance", j));
        }
        return get(rowIndex.intValue(), columnIndex.intValue());
    }

    @Override
    public void set(int _i, int _j, double d) {
        if (_i < size && _j < size) {
            if (_i == _j) {
                return;
            }
            int i = _i < _j ? _i : _j;
            int j = _i < _j ? _j : _i;
            if (d == 1D) {
                return;
            }
            // System.out.println("SparseMatrix.set() "+i+"\t"+j);
            Record value = new Record();
            value.i = i;
            value.j = j;
            value.value = d;
            matrix.put(value, value);
            // cache.remove(i);
        } else {
            throw new IllegalArgumentException(
                "Table of size " + size + " does not contain (" + _i + "," + _j + ")");
        }
    }

    @Override
    public void printLine(int i, PrintWriter out) {
        MathContext mathContext = new MathContext(2);
        for (int j = 0; j < size; j++) {
            out.print(String.format("\t%s", new BigDecimal(get(i, j), mathContext)));
        }
    }

    @Override
    public void setKeys(Collection<?> objects) {
        for (Object o : objects) {
            objectIndex.put(o, Integer.valueOf(objectIndex.size()));
        }
    }

    static class Record {
        int i;
        int j;
        double value;

        @Override
        public int hashCode() {
            return i << 16 + j;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            Record r = (Record) obj;
            return i == r.i && j == r.j;
        }

        public Record copy() {
            Record r = new Record();
            r.i = i;
            r.j = j;
            r.value = value;
            return r;
        }

        @Override
        public String toString() {
            return "key " + i + " " + j;
        }
    }
}
