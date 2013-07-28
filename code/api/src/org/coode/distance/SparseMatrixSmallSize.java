package org.coode.distance;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SparseMatrixSmallSize implements SparseMatrix {
    double[][] matrix;
    final int size;
    private final Map<Object, Integer> objectIndex = new HashMap<Object, Integer>();

    public SparseMatrixSmallSize(final int size) {
        this.size = size;
        matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = i == j ? 0D : 1D;
            }
        }
    }

    public SparseMatrixSmallSize(final SparseMatrixSmallSize m) {
        this(m.length());
        System.arraycopy(m.matrix, 0, matrix, 0, m.matrix.length);
    }

    @Override
    public int length() {
        return size;
    }

    @Override
    public double get(final int _i, final int _j) {
        if (_i < size && _j < size) {
            // int i = _i < _j ? _i : _j;
            // int j = _i < _j ? _j : _i;
            return matrix[_i][_j];
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
            // int i = _i < _j ? _i : _j;
            // int j = _i < _j ? _j : _i;
            matrix[_i][_j] = d;
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
