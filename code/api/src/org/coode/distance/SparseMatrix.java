package org.coode.distance;

import java.io.PrintWriter;
import java.util.Collection;

/** @author eleni */
public interface SparseMatrix {
    /** @return length */
    public int length();

    /** @param _i
     *            _i
     * @param _j
     *            _j
     * @return element */
    public double get(final int _i, final int _j);

    /** @param i
     *            i
     * @param j
     *            j
     * @return element */
    public double get(final Object i, final Object j);

    /** @param _i
     *            _i
     * @param _j
     *            _j
     * @param d
     *            d */
    public void set(final int _i, final int _j, final double d);

    /** @param i
     *            i
     * @param out
     *            out */
    public void printLine(final int i, final PrintWriter out);

    /** @param objects
     *            objects */
    public void setKeys(final Collection<?> objects);
}
