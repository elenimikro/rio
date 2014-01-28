package org.coode.distance;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** @author eleni */
public class SparseMatrixSmallSize implements SparseMatrix {
    double[][] matrix;
    final int size;
    private final Map<Object, Integer> objectIndex = new HashMap<Object, Integer>();

    /** @param size
     *            size */
    public SparseMatrixSmallSize(int size) {
        this.size = size;
        matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = i == j ? 0D : 1D;
            }
        }
    }

    /** @param m
     *            m */
    public SparseMatrixSmallSize(SparseMatrixSmallSize m) {
        this(m.length());
        System.arraycopy(m.matrix, 0, matrix, 0, m.matrix.length);
    }

    @Override
    public int length() {
        return size;
    }

    @Override
    public double get(int _i, int _j) {
        if (_i < size && _j < size) {
            // int i = _i < _j ? _i : _j;
            // int j = _i < _j ? _j : _i;
            return matrix[_i][_j];
        }
        throw new IllegalArgumentException("Table of size " + size
                + " does not contain (" + _i + "," + _j + ")");
    }

    @Override
    public double get(Object i, Object j) {
        return get(getPosition(i), getPosition(j));
    }

    /** @param i
     *            i
     * @return position */
    public int getPosition(Object i) {
        Integer index = objectIndex.get(i);
        if (index == -1) {
            throw new IllegalArgumentException(String.format(
                    "%s is not contained in this table based distance", i));
        }
        return index.intValue();
    }

    @Override
    public void set(int _i, int _j, double d) {
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
    public void printLine(int i, PrintWriter out) {
        MathContext mathContext = new MathContext(2);
        for (int j = 0; j < size; j++) {
            out.print(String.format("\t%s", new BigDecimal(get(i, j), mathContext)));
        }
    }

    /** @param i
     *            i
     * @return row */
    public double[] getRow(int i) {
        double[] row = new double[size];
        for (int j = 0; j < size; j++) {
            row[j] = get(i, j);
        }
        return row;
    }

    @Override
    public void setKeys(Collection<?> objects) {
        for (Object o : objects) {
            objectIndex.put(o, objectIndex.size());
        }
    }
}
