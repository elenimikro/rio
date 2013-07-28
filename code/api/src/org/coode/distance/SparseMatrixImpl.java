package org.coode.distance;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SparseMatrixImpl implements SparseMatrix {
    Map<Record, Record> matrix = new HashMap<Record, Record>();
    private final Map<Object, Integer> objectIndex = new HashMap<Object, Integer>();
    private final Record key = new Record();
    private final int size;

    public SparseMatrixImpl(final int size) {
        this.size = size;
    }

    public SparseMatrixImpl(final SparseMatrixImpl m) {
        size = m.length();
        for (Record r : m.matrix.keySet()) {
            final Record copy = r.copy();
            matrix.put(copy, copy);
        }
    }

    public double[] getRow(final int i) {
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
    public double get(final int _i, final int _j) {
        if (_i < size && _j < size) {
            if (_i == _j) {
                return 0D;
            }
            int i = _i < _j ? _i : _j;
            int j = _i < _j ? _j : _i;
            // double[] d=cache.get(i);
            // if(d==null) {
            // d=getRow(i);
            // cache.put(i, d);
            // }
            // return d[j];
            key.i = i;// Math.min(i, j);
            key.j = j;// Math.max(i, j);
            Record value = matrix.get(key);
            if (value != null) {
                return value.value;
            }
            return 1D;
        }
        throw new IllegalArgumentException("Table of size " + size
                + " does not contain (" + _i + "," + _j + ")");
    }

    @Override
    public double get(final Object i, final Object j) {
        Integer index = objectIndex.get(i);
        int rowIndex = index == null ? -1 : index;
        if (rowIndex == -1) {
            throw new IllegalArgumentException(String.format(
                    "%s is not contained in this table based distance", i));
        }
        index = objectIndex.get(j);
        int columnIndex = index == null ? -1 : index;
        if (columnIndex == -1) {
            throw new IllegalArgumentException(String.format(
                    "%s is not contained in this table based distance", j));
        }
        return get(rowIndex, columnIndex);
    }

    @Override
    public void set(final int _i, final int _j, final double d) {
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
            throw new IllegalArgumentException("Table of size " + size
                    + " does not contain (" + _i + "," + _j + ")");
        }
    }

    @Override
    public void printLine(final int i, final PrintWriter out) {
        MathContext mathContext = new MathContext(2);
        for (int j = 0; j < size; j++) {
            out.print(String.format("\t%s", new BigDecimal(get(i, j), mathContext)));
        }
    }

    @Override
    public void setKeys(final Collection<?> objects) {
        for (Object o : objects) {
            objectIndex.put(o, objectIndex.size());
        }
    }

    final static class Record {
        int i;
        int j;
        double value;

        @Override
        public final int hashCode() {
            return i << 16 + j;
        }

        @Override
        public final boolean equals(final Object obj) {
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
