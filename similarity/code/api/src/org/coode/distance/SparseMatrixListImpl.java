package org.coode.distance;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math.util.OpenIntToDoubleHashMap;

public class SparseMatrixListImpl implements SparseMatrix {
    private final OpenIntToDoubleHashMap m;
    // private static class Record {
    // int y;
    // double d;
    // }
    // private final List<List<Record>> matrix;
    // private final List<Map<Integer, Double>> matrix;
    private final int size;
    private final Map<Object, Integer> objectIndex = new HashMap<Object, Integer>();

    public SparseMatrixListImpl(final int size) {
        m = new OpenIntToDoubleHashMap(1D);
        this.size = size;
    }

    public SparseMatrixListImpl(final SparseMatrixListImpl m) {
        this(m.length());
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                final double d = m.get(i, j);
                if (d < 1D) {
                    set(i, j, d);
                }
            }
        }
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
            return m.get(i * size + j);
        }
        throw new IllegalArgumentException("Table of size " + size
                + " does not contain (" + _i + "," + _j + ")");
    }

    @Override
    public double get(final Object i, final Object j) {
        return get(getPosition(i), getPosition(j));
    }

    public int getPosition(final Object i) {
        Integer index = objectIndex.get(i);
        if (index == -1) {
            throw new IllegalArgumentException(String.format(
                    "%s is not contained in this table based distance", i));
        }
        return index.intValue();
    }

    @Override
    public void set(final int _i, final int _j, final double d) {
        // System.out.println("SparseMatrixSmallSize.set() " + _i + "\t" + _j +
        // "\t" + d);
        if (_i < size && _j < size) {
            if (_i == _j) {
                return;
            }
            int i = _i < _j ? _i : _j;
            int j = _i < _j ? _j : _i;
            if (d == 1D) {
                m.remove(i * size + j);
            } else {
                m.put(i * size + j, d);
            }
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

    public double[] getRow(final int i) {
        double[] row = new double[size];
        for (int j = 0; j < size; j++) {
            row[j] = get(i, j);
        }
        return row;
    }

    @Override
    public void setKeys(final Collection<?> objects) {
        for (Object o : objects) {
            objectIndex.put(o, objectIndex.size());
        }
    }
}
