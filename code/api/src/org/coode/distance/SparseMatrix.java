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
    public double get(int _i, int _j);

    /** @param i
     *            i
     * @param j
     *            j
     * @return element */
    public double get(Object i, Object j);

    /** @param _i
     *            _i
     * @param _j
     *            _j
     * @param d
     *            d */
    public void set(int _i, int _j, double d);

    /** @param i
     *            i
     * @param out
     *            out */
    public void printLine(int i, PrintWriter out);

    /** @param objects
     *            objects */
    public void setKeys(Collection<?> objects);
}
