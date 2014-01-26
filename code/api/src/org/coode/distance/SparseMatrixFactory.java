package org.coode.distance;

/** @author eleni */
public class SparseMatrixFactory {
    /** @param size
     *            size
     * @return sparse matrix */
    public static SparseMatrix create(final int size) {
        // if (size > 2000) {
        // return new SparseMatrixImpl(size);
        // }
        return new SparseMatrixListImpl(size);
    }

    /** @param m
     *            m
     * @return sparse matrix */
    public static SparseMatrix create(final SparseMatrix m) {
        // if (m.length() > 2000) {
        // return new SparseMatrixImpl((SparseMatrixImpl) m);
        // }
        return new SparseMatrixListImpl((SparseMatrixListImpl) m);
    }
}
