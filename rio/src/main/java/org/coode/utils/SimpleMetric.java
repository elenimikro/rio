package org.coode.utils;

/** @author eleni
 * @param <M>
 *            type */
public class SimpleMetric<M> {
    private final M value;
    private final String name;

    /** @param name
     *            name
     * @param value
     *            value */
    public SimpleMetric(String name, M value) {
        this.value = value;
        this.name = name;
    }

    /** @return name */
    public String getName() {
        return name;
    }

    /** @return value */
    public M getValue() {
        return value;
    }

    @Override
    public String toString() {
        String toReturn = name + ": " + value.toString();
        return toReturn;
    }
}
