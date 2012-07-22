package org.coode.distance;

import java.io.PrintWriter;
import java.util.Collection;

public interface SparseMatrix {
    public int length();

    public double get(final int _i, final int _j);

    public double get(final Object i, final Object j);

    public void set(final int _i, final int _j, final double d);

    public void printLine(final int i, final PrintWriter out);

    public void setKeys(final Collection<?> objects);
}
