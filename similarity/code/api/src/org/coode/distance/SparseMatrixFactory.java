package org.coode.distance;

public class SparseMatrixFactory {
    public static SparseMatrix create(final int size) {
        // if (size > 2000) {
        // return new SparseMatrixImpl(size);
        // }
        return new SparseMatrixListImpl(size);
    }

    public static SparseMatrix create(final SparseMatrix m) {
        // if (m.length() > 2000) {
        // return new SparseMatrixImpl((SparseMatrixImpl) m);
        // }
        return new SparseMatrixListImpl((SparseMatrixListImpl) m);
    }
}
