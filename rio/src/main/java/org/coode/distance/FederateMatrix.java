package org.coode.distance;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;

/** @author eleni */
public class FederateMatrix implements SparseMatrix {
    private final SparseMatrixSmallSize actual;
    private final SparseMatrixListImpl test;

    /**
     * @param i i
     */
    public FederateMatrix(int i) {
        actual = new SparseMatrixSmallSize(i);
        test = new SparseMatrixListImpl(i);
    }

    /**
     * @param m m
     */
    public FederateMatrix(FederateMatrix m) {
        for (int i = 0; i < m.length(); i++) {
            for (int j = 0; j < m.length(); j++) {
                m.get(i, j);
            }
        }
        actual = new SparseMatrixSmallSize(m.actual);
        test = new SparseMatrixListImpl(m.test);
        for (int i = 0; i < m.length(); i++) {
            for (int j = 0; j < m.length(); j++) {
                get(i, j);
            }
        }
        // System.out.println("FederateMatrix.FederateMatrix() successful");
    }

    @Override
    public double get(int _i, int _j) {
        double toReturn = actual.get(_i, _j);
        if (toReturn != test.get(_i, _j)) {
            printDiff(_i);
        }
        return toReturn;
    }

    @Override
    public int length() {
        int toReturn = actual.length();
        if (toReturn != test.length()) {
            System.out.println("FederateMatrix.length() " + toReturn + " " + " " + test.length());
        }
        return toReturn;
    }

    @Override
    public double get(Object i, Object j) {
        double toReturn = actual.get(i, j);
        if (toReturn != test.get(i, j)) {
            printDiff(test.getPosition(i));
            System.out.println("FederateMatrix.get Object() " + toReturn + "\t " + test.get(i, j)
                + "\t" + test.getPosition(i) + "\t" + test.getPosition(j));
        }
        return toReturn;
    }

    @Override
    public void set(int _i, int _j, double d) {
        actual.set(_i, _j, d);
        test.set(_i, _j, d);
        get(_i, _j);
        // printDiff(_i);
    }

    /**
     * @param _i _i
     */
    public void printDiff(int _i) {
        double[] d1 = actual.getRow(_i);
        double[] d2 = test.getRow(_i);
        if (!Arrays.equals(d1, d2)) {
            System.out.println("FederateMatrix.printDiff()");
            for (int i = 0; i < d1.length; i++) {
                if (d1[i] != d2[i]) {
                    System.out.println(_i + " " + i + "\t" + d1[i] + "\t" + d2[i]);
                }
            }
        }
    }

    @Override
    public void setKeys(Collection<?> objects) {
        actual.setKeys(objects);
        test.setKeys(objects);
    }

    @Override
    public void printLine(int i, PrintWriter out) {
        actual.printLine(i, out);
    }
}
